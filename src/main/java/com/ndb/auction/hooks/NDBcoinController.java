package com.ndb.auction.hooks;

import com.ndb.auction.controllers.P2pController;
import com.ndb.auction.models.CirculatingSupply;
import com.ndb.auction.models.Marketcap;
import com.ndb.auction.models.TotalSupply;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.text.DecimalFormat;

@RestController
public class NDBcoinController extends BaseController {
    @Autowired
    P2pController p2pController;
    private static final DecimalFormat df = new DecimalFormat("0.00");

    @RequestMapping(value = "/totalsupply", method = RequestMethod.GET)
    public TotalSupply totalSupply() throws Exception {
        double totalSupply = ndbCoinService.getTotalSupply();
        return new TotalSupply(totalSupply);
    }

    @RequestMapping(value = "/circulatingsupply", method = RequestMethod.GET)
    public CirculatingSupply circulatingSupply() throws Exception {
        double circulatingSupply = ndbCoinService.getCirculatingSupply();
        return new CirculatingSupply(circulatingSupply);
    }

    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/marketcap", method = RequestMethod.GET)
    public Marketcap marketcap() throws Exception {
        double marketcap = ndbCoinService.getMarketCap();
        return new Marketcap(marketcap);
    }

}
