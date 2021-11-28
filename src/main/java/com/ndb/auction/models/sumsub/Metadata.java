package com.ndb.auction.models.sumsub;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

@DynamoDBTable(tableName="Metadata")
public class Metadata {
    // https://developers.sumsub.com/api-reference/#request-metadata-body-part-fields
    private String idDocType;
    private String country;

    public Metadata() {
    }

    public Metadata(String idDocType, String country) {
        this.idDocType = idDocType;
        this.country = country;
    }

    @DynamoDBAttribute(attributeName="doc_type")
    public String getIdDocType() {
        return idDocType;
    }

    public void setIdDocType(String idDocType) {
        this.idDocType = idDocType;
    }

    @DynamoDBAttribute(attributeName="country")
    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }
}
