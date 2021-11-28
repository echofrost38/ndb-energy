package com.ndb.auction.resolver;

import java.util.List;

import com.ndb.auction.payload.statistics.RoundChance;
import com.ndb.auction.payload.statistics.RoundPerform1;
import com.ndb.auction.payload.statistics.RoundPerform2;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import graphql.kickstart.tools.GraphQLQueryResolver;

@Component
public class StatResolver extends BaseResolver implements GraphQLQueryResolver {
    
    @PreAuthorize("isAuthenticated()")
    public List<RoundChance> getRoundChance() {
        return statService.getRoundChance();
    }

    @PreAuthorize("isAuthenticated()")
    public List<RoundPerform2> getRoundPerform2() {
        return statService.getRoundPerform2();
    }

    @PreAuthorize("isAuthenticated()")
    public List<RoundPerform1> getRoundPerform1() {
        return statService.getRoundPerform1();
    }

}
