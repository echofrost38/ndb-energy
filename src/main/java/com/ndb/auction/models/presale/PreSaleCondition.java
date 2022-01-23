package com.ndb.auction.models.presale;

import org.springframework.stereotype.Component;
import com.ndb.auction.models.BaseModel;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Component
@Getter
@Setter
@NoArgsConstructor
public class PreSaleCondition extends BaseModel{
    private int presaleId;
    private String task;
    private String url;
}
