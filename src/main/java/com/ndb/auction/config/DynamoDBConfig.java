package com.ndb.auction.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

@Configuration
public class DynamoDBConfig implements EnvironmentAware {
	
	private static final String REAL = "real";

    @Override
    public void setEnvironment(Environment environment) {
        this.awsAccessKey = environment.getProperty("aws.access.key");
        this.awsSecretKey = environment.getProperty("aws.secret.key");
    }

	@Value("${mode}")
	private String mode;

    private String awsAccessKey;
    private String awsSecretKey;
    
    @Bean
    public AmazonS3 s3() {
        return AmazonS3ClientBuilder
                .standard()
                .withRegion(Regions.EU_WEST_2)
                .withCredentials(new AWSStaticCredentialsProvider(amazonAWSCredentials()))
                .build();
    }

    @Bean
    public DynamoDBMapper dynamoDBMapper() {
    	AmazonDynamoDB client;
        if(mode.equals(REAL)) {
        	client = AmazonDynamoDBClientBuilder.standard()
        			.withCredentials(new AWSStaticCredentialsProvider(amazonAWSCredentials()))
        			.withRegion(Regions.US_EAST_1).build();        	
        } else {
        	client = AmazonDynamoDBClientBuilder.standard().withEndpointConfiguration(
        			new AwsClientBuilder.EndpointConfiguration("http://localhost:8000", "us-east-1"))
        			.build();
        }
        DynamoDBMapper dynamoDBMapper = new DynamoDBMapper(client, DynamoDBMapperConfig.DEFAULT);
        return dynamoDBMapper;
    }

    @Bean
    public AWSCredentials amazonAWSCredentials() {
        return new BasicAWSCredentials(awsAccessKey, awsSecretKey);
    }

}
