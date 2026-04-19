package com.lawfirm.cms.factory;

import com.lawfirm.cms.model.LegalCase;

public interface ICaseFactory {
    LegalCase createCase(String type, String title, String description);
}
