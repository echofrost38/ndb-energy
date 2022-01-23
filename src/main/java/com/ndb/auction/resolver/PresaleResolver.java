package com.ndb.auction.resolver;

import java.util.List;

import com.ndb.auction.models.presale.PreSale;
import com.ndb.auction.models.presale.PreSaleCondition;

import org.springframework.stereotype.Component;

import graphql.kickstart.tools.GraphQLMutationResolver;
import graphql.kickstart.tools.GraphQLQueryResolver;

@Component
public class PresaleResolver extends BaseResolver implements GraphQLQueryResolver, GraphQLMutationResolver{
    
    // create new presale round
    public int createNewPresale(
        int round,
        Long startedAt,
        Long endedAt,
        Long tokenAmount,
        Long tokenPrice,
        List<PreSaleCondition> conditions
    ) {
        PreSale presale = new PreSale(round, startedAt, endedAt, tokenAmount, tokenPrice, conditions);
        return presaleService.createNewPresale(presale);
    }

}
