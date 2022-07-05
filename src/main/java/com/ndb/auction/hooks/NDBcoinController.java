package com.ndb.auction.hooks;

import com.google.gson.Gson;
import com.ndb.auction.controllers.P2pController;
import com.ndb.auction.models.CirculatingSupply;
import com.ndb.auction.models.Marketcap;
import com.ndb.auction.models.TotalSupply;
import com.ndb.auction.models.p2pb2b.P2PB2BResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;

@RestController
public class NDBcoinController extends BaseController {
    private final String ownerAddress = "0xc9904d6cfff6ea40b6767e6516a83b45c140486b";
    private final BigInteger ownerInitialBalance = new BigInteger("500000000000");

    @Autowired
    P2pController p2pController;

    @RequestMapping(value = "/totalsupply", method = RequestMethod.GET)
    public TotalSupply totalSupply() throws Exception {
        double totalSupply = ndbCoinService.getTotalSupply();
        return new TotalSupply(totalSupply);
    }

    @RequestMapping(value = "/circulatingsupply", method = RequestMethod.GET)
    public CirculatingSupply circulatingSupply() throws Exception {
        BigInteger ownerBalance = ndbCoinService.getBalanceOf(ownerAddress);
        double ciculatingSupply = ownerInitialBalance.subtract(ownerBalance).doubleValue();
        return new CirculatingSupply(ciculatingSupply);
    }

    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/marketcap", method = RequestMethod.GET)
    public Marketcap marketcap() throws Exception {
        ResponseEntity<Object> result = (ResponseEntity<Object>) p2pController.getNdbPrice();
        P2PB2BResponse response = new Gson().fromJson(result.getBody().toString(), P2PB2BResponse.class);
        double ciculatingSupply = this.calculateCiculatingSupply();
        double price = Double.parseDouble(response.result.last);
        double marketcap = ciculatingSupply * price;
        return new Marketcap(marketcap);
    }

    private double calculateCiculatingSupply() {
        BigInteger ownerBalance = ndbCoinService.getBalanceOf(ownerAddress);
        double ciculatingSupply = ownerInitialBalance.subtract(ownerBalance).doubleValue();
        return ciculatingSupply;
    }

}
