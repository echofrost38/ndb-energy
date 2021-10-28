package com.ndb.auction.resolver;

import org.springframework.stereotype.Component;

import com.ndb.auction.models.Auction;
import com.ndb.auction.payload.Credentials;

import graphql.kickstart.tools.GraphQLMutationResolver;
import graphql.kickstart.tools.GraphQLQueryResolver;

@Component
public class AuthResolver extends BaseResolver implements GraphQLMutationResolver, GraphQLQueryResolver{
	
	public Auction signup(String email, String password, String country) {
		
		Auction res = new Auction();
		return res;
	}
	
	public Credentials signin(String email, String password) {
		return new Credentials();
	}
	
}
