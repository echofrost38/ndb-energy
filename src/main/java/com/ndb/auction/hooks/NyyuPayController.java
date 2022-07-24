package com.ndb.auction.hooks;

import com.google.gson.Gson;
import com.ndb.auction.dao.oracle.balance.CryptoBalanceDao;
import com.ndb.auction.models.nyyupay.NyyuPayResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@RequestMapping("/")
public class NyyuPayController extends BaseController{

    @Value("${nyyupay.pubKey}")
    private String PUBLIC_KEY;

    @Value("${nyyupay.privKey}")
    private String PRIVATE_KEY;

    CryptoBalanceDao balanceDao;
    @Autowired
    public NyyuPayController(CryptoBalanceDao balanceDao){
        this.balanceDao=balanceDao;
    }
    @PostMapping("/nyyupay")
    @ResponseBody
    public Object NyyuPayCallbackHandler(HttpServletRequest request) {
        String reqQuery;
        Map<String,String> reqHeader;
        try {
            reqQuery = getBody(request);
            reqHeader= getHeadersInfo(request);
            NyyuPayResponse response = new Gson().fromJson(reqQuery, NyyuPayResponse.class);
            String payload = reqHeader.get("x-auth-ts") +"POST"+reqQuery.toString();
            String hmac = buildHmacSignature(payload, PRIVATE_KEY);
            if (reqHeader.get("x-auth-key").equals(PUBLIC_KEY) && reqHeader.get("x-auth-token").equals(hmac)){
                System.out.println("NYYU PAY CALLBACK BODY: " + reqQuery);
                
                //New deposit
                int tokenId = tokenAssetService.getTokenIdBySymbol("NDB");
                int userId = nyyuWalletService.selectByAddress(response.getAddress()).getUserId();
                double amount = response.getAmount().doubleValue()/Math.pow(10,12);
                balanceDao.addFreeBalance(userId, tokenId, amount);
                System.out.println("Deposit detection : " + reqQuery.toString());
                return response;
            } else
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}
