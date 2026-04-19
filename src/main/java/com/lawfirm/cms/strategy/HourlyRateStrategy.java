package com.lawfirm.cms.strategy;

import org.springframework.stereotype.Component;

@Component("hourlyRateStrategy")
public class HourlyRateStrategy implements BillingStrategy {

    @Override
    public double calculateFee(double hours, double rate) {
        return hours * rate;
    }
}
