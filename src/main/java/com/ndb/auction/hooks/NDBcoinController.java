package com.ndb.auction.hooks;

import com.ndb.auction.models.CirculatingSupply;
import com.ndb.auction.models.TotalSupply;
import org.springframework.web.bind.annotation.*;

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
