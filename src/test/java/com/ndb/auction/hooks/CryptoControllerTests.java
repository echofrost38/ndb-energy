package com.ndb.auction.hooks;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;

import com.ndb.auction.AuctionApplicationTests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class CryptoControllerTests extends AuctionApplicationTests {
    
    // private MockMvc mockMvc;

	// @Autowired
	// private WebApplicationContext webApplicationContext;

	// @BeforeEach
	// public void setup() {
	// 	mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
	// }

    // @Test
    // void depositNotification() throws Exception {
    //     mockMvc.perform(
    //         post("/ipn/deposit/{id}", 24))
    //     .andExpect(status().isOk());
    // }

}
