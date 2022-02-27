package com.ndb.auction.resolver.payment;

import java.util.List;

import com.ndb.auction.models.transactions.TxnFee;
import com.ndb.auction.resolver.BaseResolver;

import org.springframework.stereotype.Component;

import graphql.kickstart.tools.GraphQLMutationResolver;
import graphql.kickstart.tools.GraphQLQueryResolver;

@Component
public class TxnFeeResolver extends BaseResolver implements GraphQLQueryResolver, GraphQLMutationResolver {
    public List<TxnFee> createNewFee(int tierLevel, double fee) {
        TxnFee txnFee = new TxnFee(tierLevel, fee);
        return txnFeeService.insert(txnFee);
    }

    public List<TxnFee> getAllFees() {
        return txnFeeService.selectAll();
    }

    public List<TxnFee> update(int id, int tierLevel, double fee) {
        TxnFee txnFee = new TxnFee(id, tierLevel, fee);
        return txnFeeService.update(txnFee);
    }

    public List<TxnFee> delete(int id) {
        return txnFeeService.delete(id);
    }
}
