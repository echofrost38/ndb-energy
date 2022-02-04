package com.ndb.auction.exceptions;

import java.util.List;

import graphql.ErrorClassification;
import graphql.ErrorType;
import graphql.GraphQLError;
import graphql.language.SourceLocation;

public class DatabaseAccessException extends RuntimeException implements GraphQLError {

    /**
	 * 
	 */
	private static final long serialVersionUID = -4915025020331453965L;

	public DatabaseAccessException(String message, Throwable e) {
        super(message, e);
    }

    @Override
    public List<Object> getPath() {
        return null;
    }

    @Override
    public List<SourceLocation> getLocations() {
        return null;
    }

    @Override
    public ErrorClassification getErrorType() {
        return ErrorType.DataFetchingException;
    }

}
