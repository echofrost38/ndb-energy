package com.ndb.auction.hooks;

import com.google.gson.Gson;
import com.ndb.auction.controllers.P2pController;
import com.ndb.auction.models.CirculatingSupply;
import com.ndb.auction.models.Marketcap;
import com.ndb.auction.models.TotalSupply;

import com.ndb.auction.models.digifinex.DigiFinex;
import com.ndb.auction.models.p2pb2b.P2PB2BResponse;
import com.ndb.auction.web3.NDBCoinService;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class NDBcoinController extends BaseController {
    @Autowired
    P2pController p2pController;
    private static final DecimalFormat df = new DecimalFormat("0.00");

    @RequestMapping(value = "/totalsupply", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public TotalSupply totalSupply() throws Exception {
        double totalSupply = ndbCoinService.getTotalSupply();
        return new TotalSupply(df.format(totalSupply));
    }

    @RequestMapping(value = "/circulatingsupply", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public CirculatingSupply circulatingSupply() throws Exception {
        double circulatingSupply = ndbCoinService.getCirculatingSupply();
        return new CirculatingSupply(df.format(circulatingSupply));
    }

    @RequestMapping(value = "/marketcap", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public Object marketcap() throws Exception {
        double marketcap = ndbCoinService.getMarketCap();
        return new Marketcap(df.format(marketcap));
    }

}
