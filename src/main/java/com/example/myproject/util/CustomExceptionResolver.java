package com.example.myproject.util;

import graphql.GraphQLError;
import graphql.GraphqlErrorBuilder;
import graphql.schema.DataFetchingEnvironment;
import jakarta.annotation.Nonnull;
import org.springframework.graphql.execution.DataFetcherExceptionResolverAdapter;
import org.springframework.graphql.execution.ErrorType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;

import java.util.Map;

@Component
public class CustomExceptionResolver extends DataFetcherExceptionResolverAdapter {

    @Override
    protected GraphQLError resolveToSingleError(@Nonnull Throwable ex, @Nonnull DataFetchingEnvironment env) {
        if (ex instanceof HttpClientErrorException ex2) {
            return GraphqlErrorBuilder.newError()
                    .message("HTTP error occurred: " + ex.getMessage())
                    .extensions(Map.of(
                            "statusCode", ex2.getStatusCode().value(),
                            "error", ex2.getStatusText()
                    ))
                    .build();
        } else {
            return GraphqlErrorBuilder.newError()
                    .message("An unexpected error occurred: " + ex.getMessage())
                    .errorType(ErrorType.INTERNAL_ERROR)
                    .extensions(Map.of(
                            "error", ex.getClass().getSimpleName()
                    ))
                    .build();
        }
    }
}
