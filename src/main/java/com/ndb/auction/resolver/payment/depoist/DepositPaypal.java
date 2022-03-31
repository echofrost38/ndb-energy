package com.ndb.auction.resolver.payment.depoist;

import java.util.List;

import com.ndb.auction.exceptions.UserNotFoundException;
import com.ndb.auction.models.Notification;
import com.ndb.auction.models.TaskSetting;
import com.ndb.auction.models.tier.Tier;
import com.ndb.auction.models.tier.TierTask;
import com.ndb.auction.models.tier.WalletTask;
import com.ndb.auction.models.transactions.paypal.PaypalDepositTransaction;
import com.ndb.auction.models.user.User;
import com.ndb.auction.payload.BalancePayload;
import com.ndb.auction.payload.request.paypal.OrderDTO;
import com.ndb.auction.payload.request.paypal.PayPalAppContextDTO;
import com.ndb.auction.payload.request.paypal.PurchaseUnit;
import com.ndb.auction.payload.response.paypal.CaptureOrderResponseDTO;
import com.ndb.auction.payload.response.paypal.OrderResponseDTO;
import com.ndb.auction.payload.response.paypal.PaymentLandingPage;
import com.ndb.auction.resolver.BaseResolver;
import com.ndb.auction.service.TaskSettingService;
import com.ndb.auction.service.TierService;
import com.ndb.auction.service.payment.paypal.PaypalDepositService;
import com.ndb.auction.service.user.UserDetailsImpl;
import com.ndb.auction.utils.PaypalHttpClient;
import com.ndb.auction.utils.ThirdAPIUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import graphql.kickstart.tools.GraphQLMutationResolver;
import graphql.kickstart.tools.GraphQLQueryResolver;

@Component
public class DepositPaypal extends BaseResolver implements GraphQLMutationResolver, GraphQLQueryResolver {
    
    @Autowired
    private PaypalDepositService paypalDepositService;

    private final PaypalHttpClient payPalHttpClient;

    @Autowired
    private ThirdAPIUtils apiUtil;

    @Autowired
    private TierService tierService;

    @Autowired
    private TaskSettingService taskSettingService;

	@Autowired
	public DepositPaypal(PaypalHttpClient payPalHttpClient) {
		this.payPalHttpClient = payPalHttpClient;
	}

    @PreAuthorize("isAuthenticated()")
    public OrderResponseDTO paypalForDeposit(Double amount, String currencyCode, String cryptoType) throws Exception {
        UserDetailsImpl userDetails = (UserDetailsImpl)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int userId = userDetails.getId();
        double fee = getPaypalFee(userId, amount);
        double cryptoPrice = 1.0;
        if(!cryptoType.equals("USDT")) {
            cryptoPrice = thirdAPIUtils.getCryptoPriceBySymbol(cryptoType);
        } 
        double deposited = (amount - fee) / cryptoPrice;

        var order = new OrderDTO();
        var unit = new PurchaseUnit(amount.toString(), currencyCode);
        order.getPurchaseUnits().add(unit);

        var appContext = new PayPalAppContextDTO();
        appContext.setReturnUrl(WEBSITE_URL + "/");
		appContext.setBrandName("Deposit");
        appContext.setLandingPage(PaymentLandingPage.BILLING);
        order.setApplicationContext(appContext);
        OrderResponseDTO orderResponse = payPalHttpClient.createOrder(order);

        var m = new PaypalDepositTransaction(userId, amount, cryptoType, cryptoPrice, orderResponse.getId(), orderResponse.getStatus().toString(), fee, deposited);
                
        paypalDepositService.insert(m);
        return orderResponse;
    }

    @PreAuthorize("isAuthenticated()")
    public boolean captureOrderForDeposit(String orderId) throws Exception {
        UserDetailsImpl userDetails = (UserDetailsImpl)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int userId = userDetails.getId();

        var m = paypalDepositService.selectByPaypalOrderId(orderId);
        if(m == null || m.getUserId() != userId) throw new UserNotFoundException("There is no transaction.", "orderId");

        CaptureOrderResponseDTO responseDTO = payPalHttpClient.captureOrder(orderId);
        if(responseDTO.getStatus() != null && responseDTO.getStatus().equals("COMPLETED")) {
            // check deposited amount & update status
            paypalDepositService.updateOrderStatus(m.getId(), "COMPLETED");

            // add balance to user
            internalBalanceService.addFreeBalance(userId, m.getCryptoType(), m.getDeposited());

            List<BalancePayload> balances = internalBalanceService.getInternalBalances(userId);
            double totalBalance = 0.0;
            for (BalancePayload balance : balances) {
                // get price and total balance
                double _price = apiUtil.getCryptoPriceBySymbol(balance.getTokenSymbol());
                double _balance = _price * (balance.getFree() + balance.getHold());
                totalBalance += _balance;
            }

            User user = userService.getUserById(userId);
            List<Tier> tierList = tierService.getUserTiers();
            TaskSetting taskSetting = taskSettingService.getTaskSetting();
            TierTask tierTask = tierTaskService.getTierTask(userId);

            if(tierTask == null) {
                tierTask = new TierTask(userId);
                tierTaskService.updateTierTask(tierTask);
            }
    
            if(tierTask.getWallet() < totalBalance) {
    
                tierTask.setWallet(totalBalance);
                // get point
                double gainedPoint = 0.0;
                for (WalletTask task : taskSetting.getWallet()) {
                    if(tierTask.getWallet() > task.getAmount()) {
                        continue;
                    }                    
                    if(totalBalance < task.getAmount()) {
                        break;
                    }
                    gainedPoint += task.getPoint();
                }
    
                double newPoint = user.getTierPoint() + gainedPoint;
                int tierLevel = 0;
                // check change in level
                for (Tier tier : tierList) {
                    if(tier.getPoint() <= newPoint) {
                        tierLevel = tier.getLevel();
                    }
                }
                userService.updateTier(user.getId(), tierLevel, newPoint);
                tierTaskService.updateTierTask(tierTask);
            }

            // send notification to user for payment result!!
            notificationService.sendNotification(
                userId,
                Notification.PAYMENT_RESULT,
                "PAYMENT CONFIRMED",
                "You have successfully deposited " + m.getDeposited() + m.getCryptoType() + ".");
            return true;
        }
        return false;
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @SuppressWarnings("unchecked")
    public List<PaypalDepositTransaction> getAllPaypalDepositTxns(String orderBy) {
        return (List<PaypalDepositTransaction>) paypalDepositService.selectAll(orderBy);
    }

    @PreAuthorize("isAuthenticated()")
    @SuppressWarnings("unchecked")
    public List<PaypalDepositTransaction> getPaypalDepositTxnsByUser(String orderBy) {
        UserDetailsImpl userDetails = (UserDetailsImpl)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int userId = userDetails.getId();
        return (List<PaypalDepositTransaction>) paypalDepositService.selectByUser(userId, orderBy);
    }

    @PreAuthorize("isAuthenticated()")
    public PaypalDepositTransaction getPaypalDepositTxnById(int id) {
        return (PaypalDepositTransaction) paypalDepositService.selectById(id);
    }

}
