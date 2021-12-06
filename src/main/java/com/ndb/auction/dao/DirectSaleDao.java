package com.ndb.auction.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.ndb.auction.models.DirectSale;

import org.springframework.stereotype.Repository;

@Repository
public class DirectSaleDao extends BaseDao {

    public DirectSaleDao(DynamoDBMapper dynamoDBMapper) {
        super(dynamoDBMapper);
    }
    
    // create new empty transaction
    public DirectSale createEmptyDirectSale(DirectSale directSale) {
        dynamoDBMapper.save(directSale);
        return directSale;
    }

    // get transaction by intent id
    public DirectSale getDirectSaleByIntent(String intentId) {
        Map<String, AttributeValue> eav = new HashMap<String, AttributeValue>();
        eav.put(":val1", new AttributeValue().withS(String.valueOf(intentId)));
        
        DynamoDBScanExpression scanExpression = new DynamoDBScanExpression()
            .withFilterExpression("payment_intent_id = :val1")
            .withExpressionAttributeValues(eav);
        List<DirectSale> list = dynamoDBMapper.scan(DirectSale.class, scanExpression);
        if(list.size() == 0) {
            return null;
        } else {
            return list.get(0);
        }
    }

    public DirectSale getDirectSaleByCode(String code) {
        Map<String, AttributeValue> eav = new HashMap<String, AttributeValue>();
        eav.put(":val1", new AttributeValue().withS(String.valueOf(code)));
        
        DynamoDBScanExpression scanExpression = new DynamoDBScanExpression()
            .withFilterExpression("code = :val1")
            .withExpressionAttributeValues(eav);
        List<DirectSale> list = dynamoDBMapper.scan(DirectSale.class, scanExpression);
        if(list.size() == 0) {
            return null;
        } else {
            return list.get(0);
        }
    }

    public DirectSale updateDirectSale(DirectSale directSale) {
        dynamoDBMapper.save(directSale, updateConfig);
        return directSale;
    }

}
