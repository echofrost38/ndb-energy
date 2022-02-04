package com.ndb.auction;

import com.ndb.auction.hooks.ShuftiController;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AuctionApplicationTests {
    
    @Autowired
    ShuftiController shuftiController;

    @Test
    public void contextLoads() {
    }

}
