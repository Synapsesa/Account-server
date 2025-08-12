package com.synapse.account_service.convert;

public interface ProviderUserConverter<T, R> {
    R convert(T t);
}
