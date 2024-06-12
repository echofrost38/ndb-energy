package com.ndb.auction.hooks;


import java.math.BigInteger;

import com.ndb.auction.contracts.NDBcoin;
import com.ndb.auction.models.CirculatingSupply;
import com.ndb.auction.models.TotalSupply;
import org.springframework.web.bind.annotation.*;
import org.web3j.tx.FastRawTransactionManager;
import org.web3j.tx.gas.DefaultGasProvider;

@RestController
public class NDBcoinController extends BaseController {
    @RequestMapping(value = "/totalsupply", method = RequestMethod.GET)
    public TotalSupply totalSupply() throws Exception {
        String totalSupply = ndbCoinService.getTotalSupply();
        return new TotalSupply(totalSupply);
    }

    @RequestMapping(value = "/circulatingsupply", method = RequestMethod.GET)
    public CirculatingSupply circulatingSupply() throws Exception {
        String circulatingSupply = ndbCoinService.getCirculatingSupply();
        return new CirculatingSupply(circulatingSupply);
    }
}
