package com.synapse.account_service_api.aggregate;

public enum AggregateType {
    MEMBER("com.synapse.account_service.domain.entity.Member"),
    SUBSCRIPTION("com.synapse.account_service.domain.entity.Subscription");
    
    private final String aggregateType;
    
    AggregateType(String aggregateType) {
        this.aggregateType = aggregateType;
    }
    
    public String getAggregateType() {
        return aggregateType;
    }
    
    @Override
    public String toString() {
        return aggregateType;
    }
}
