package com.lawfirm.cms.strategy;

import org.springframework.stereotype.Component;

@Component("flatFeeStrategy")
public class FlatFeeStrategy implements BillingStrategy {

    @Override
    public double calculateFee(double hours, double rate) {
        return rate;
    }
}
