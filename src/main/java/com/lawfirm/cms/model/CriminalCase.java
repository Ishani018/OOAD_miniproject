package com.lawfirm.cms.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("CRIMINAL")
public class CriminalCase extends LegalCase {

    private String crimeSeverity;

    public CriminalCase() {
        setCaseType("CRIMINAL");
    }

    public String getCrimeSeverity() {
        return crimeSeverity;
    }

    public void setCrimeSeverity(String crimeSeverity) {
        this.crimeSeverity = crimeSeverity;
    }
}
