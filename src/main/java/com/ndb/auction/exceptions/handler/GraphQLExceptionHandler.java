package com.ndb.auction.exceptions.handler;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import graphql.ExceptionWhileDataFetching;
import graphql.GraphQLError;
import graphql.kickstart.execution.error.GraphQLErrorHandler;

@Component
public class GraphQLExceptionHandler implements GraphQLErrorHandler {

    @Override
    public List<GraphQLError> processErrors(List<GraphQLError> list) {
        return list.stream().map(this::getNested).collect(Collectors.toList());
    }

    private GraphQLError getNested(GraphQLError error) {
        if (error instanceof ExceptionWhileDataFetching) {
            ExceptionWhileDataFetching exceptionError = (ExceptionWhileDataFetching) error;
            Throwable t = exceptionError.getException();
            if (t instanceof GraphQLError) {
                error = (GraphQLError) t;
                t = exceptionError.getException();
            }
            // if (false && error.getErrorType() == ErrorType.DataFetchingException) {
            //     return new DatabaseAccessException("database error", t);
            // }
        }
        return error;
    }

}
