package com.ndb.auction.exceptions;

import java.util.Collections;
import java.util.List;

import graphql.ErrorClassification;
import graphql.ErrorType;
import graphql.GraphQLError;
import graphql.language.SourceLocation;

public class LocationException extends RuntimeException implements GraphQLError {

    /**
	 * 
	 */
	private static final long serialVersionUID = 3215658805455521038L;

	public LocationException(String message) {
        super(message);
    }

    @Override
    public List<SourceLocation> getLocations() {
        return Collections.emptyList();
    }

    @Override
    public ErrorClassification getErrorType() {
        return ErrorType.ValidationError;
    }

}
