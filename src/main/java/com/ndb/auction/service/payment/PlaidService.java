package com.ndb.auction.service.payment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import com.ndb.auction.service.BaseService;
import com.plaid.client.ApiClient;
import com.plaid.client.model.CountryCode;
import com.plaid.client.model.ItemPublicTokenExchangeRequest;
import com.plaid.client.model.ItemPublicTokenExchangeResponse;
import com.plaid.client.model.LinkTokenCreateRequest;
import com.plaid.client.model.LinkTokenCreateRequestPaymentInitiation;
import com.plaid.client.model.LinkTokenCreateRequestUser;
import com.plaid.client.model.LinkTokenCreateResponse;
import com.plaid.client.model.PaymentAmount;
import com.plaid.client.model.PaymentInitiationAddress;
import com.plaid.client.model.PaymentInitiationPaymentCreateRequest;
import com.plaid.client.model.PaymentInitiationPaymentCreateResponse;
import com.plaid.client.model.PaymentInitiationRecipientCreateRequest;
import com.plaid.client.model.PaymentInitiationRecipientCreateResponse;
import com.plaid.client.model.Products;
import com.plaid.client.model.RecipientBACSNullable;
import com.plaid.client.request.PlaidApi;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import retrofit2.Response;

@Service
public class PlaidService extends BaseService {

    @Value("${plaid.client.id}")
    private String CLIENT_ID;

    @Value("${plaid.secret.key}")
    private String SECRET;

    private static PlaidApi plaidClient;

    @PostConstruct
    public void init() {
        // Create your Plaid client
        Map<String, String> apiKeys = new HashMap<>();
        apiKeys.put("clientId", CLIENT_ID);
        apiKeys.put("secret", SECRET);
        ApiClient apiClient = new ApiClient(apiKeys);

        apiClient.setPlaidAdapter(ApiClient.Sandbox);
        plaidClient = apiClient.createService(PlaidApi.class);
    }

    // Create a recipient
    private String createRecipient() throws IOException {
        PaymentInitiationAddress address = new PaymentInitiationAddress()
            .street(Arrays.asList("Street Name 999"))
            .city("City")
            .postalCode("99999")
            .country("GB");
        RecipientBACSNullable basc = new RecipientBACSNullable ()
            .account("260934")
            .sortCode("560029");

        PaymentInitiationRecipientCreateRequest request = new PaymentInitiationRecipientCreateRequest()
            .name("John Doe")
            .bacs(basc)
            .address(address);
        Response<PaymentInitiationRecipientCreateResponse> response = plaidClient
            .paymentInitiationRecipientCreate(request)
            .execute();
        return response.body().getRecipientId();
    }

    // Create a payment
    private String createPayment(String recipientId) throws IOException {
        PaymentAmount  amount = new PaymentAmount ()
            .currency(PaymentAmount.CurrencyEnum.GBP)
            .value(999.99);

        PaymentInitiationPaymentCreateRequest request = new PaymentInitiationPaymentCreateRequest()
            .recipientId(recipientId)
            .reference("reference")
            .amount(amount);
        
        Response<PaymentInitiationPaymentCreateResponse> response = plaidClient
            .paymentInitiationPaymentCreate(request)
            .execute();
        return response.body().getPaymentId();
    }

    // Create link token
    public LinkTokenCreateResponse createLinkToken(int userId) throws IOException {
        String recipientId = createRecipient();
        String paymentId = createPayment(recipientId);
        
        // User user = userDao.selectById(userId);
        LinkTokenCreateRequestUser user = new LinkTokenCreateRequestUser()
            .clientUserId(String.valueOf(userId));

        // Create a link_token for the given user
        LinkTokenCreateRequestPaymentInitiation paymentInitiation = new LinkTokenCreateRequestPaymentInitiation()
            .paymentId(paymentId);
        
        List<Products> productList = new ArrayList<>();
        productList.add(Products.PAYMENT_INITIATION);

        List<CountryCode> codeList = new ArrayList<>();
        codeList.add(CountryCode.GB);
        

        LinkTokenCreateRequest request = new LinkTokenCreateRequest()
            .user(user)
            .clientName("Plaid Test App")
            .products(productList)
            .countryCodes(codeList)
            .language("en")
            .redirectUri("redirectUri")
            .webhook("https://sample.webhook.com")
            .paymentInitiation(paymentInitiation);
        Response<LinkTokenCreateResponse> response = plaidClient
            .linkTokenCreate(request)
            .execute();
        
        return response.body();
    }

    public ItemPublicTokenExchangeResponse getExchangeToken(String publicToken) throws IOException {
        ItemPublicTokenExchangeRequest request = new ItemPublicTokenExchangeRequest()
            .publicToken(publicToken);
        Response<ItemPublicTokenExchangeResponse> response = plaidClient
            .itemPublicTokenExchange(request)
            .execute();
        return response.body();
    }
}
