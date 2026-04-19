package com.lawfirm.cms.strategy;

public interface BillingStrategy {
    double calculateFee(double hours, double rate);
}
