package com.lawfirm.cms.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("CORPORATE")
public class CorporateCase extends LegalCase {

    private Double contractValue;

    public CorporateCase() {
        setCaseType("CORPORATE");
    }

    public Double getContractValue() {
        return contractValue;
    }

    public void setContractValue(Double contractValue) {
        this.contractValue = contractValue;
    }
}
