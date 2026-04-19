package com.lawfirm.cms.factory;

import com.lawfirm.cms.model.CivilCase;
import com.lawfirm.cms.model.CorporateCase;
import com.lawfirm.cms.model.CriminalCase;
import com.lawfirm.cms.model.LegalCase;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class CaseFactory implements ICaseFactory {

    @Override
    public LegalCase createCase(String type, String title, String description) {
        LegalCase legalCase;
        switch (type.toUpperCase()) {
            case "CIVIL":
                legalCase = new CivilCase();
                break;
            case "CRIMINAL":
                legalCase = new CriminalCase();
                break;
            case "CORPORATE":
                legalCase = new CorporateCase();
                break;
            default:
                throw new IllegalArgumentException("Unknown case type: " + type);
        }
        legalCase.setTitle(title);
        legalCase.setDescription(description);
        legalCase.setCaseType(type.toUpperCase());
        legalCase.setCaseNumber("CASE-" + System.currentTimeMillis());
        legalCase.setFilingDate(LocalDate.now());
        return legalCase;
    }
}
