package com.lawfirm.cms.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("CIVIL")
public class CivilCase extends LegalCase {

    private Double claimAmount;

    public CivilCase() {
        setCaseType("CIVIL");
    }

    public Double getClaimAmount() {
        return claimAmount;
    }

    public void setClaimAmount(Double claimAmount) {
        this.claimAmount = claimAmount;
    }
}
