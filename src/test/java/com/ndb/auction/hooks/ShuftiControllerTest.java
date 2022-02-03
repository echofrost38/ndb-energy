package com.ndb.auction.hooks;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

@ExtendWith(SpringExtension.class)
@WebMvcTest(ShuftiController.class)
public class ShuftiControllerTest {
    
    @MockBean
    ShuftiController shuftiController;

    @Autowired
    MockMvc mockMvc;

    @Test
    public void testCallback() {
        // Mockito.when(shuftiController.ShuftiCallbackHandler(request)).thenReturn(object);

        // mockMvc.perform(get("/shufti"))
        //     .andExpect(status().isOk());
        
    }
}
