package com.lawfirm.cms.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("STAFF")
public class Staff extends User {

    public Staff() {
        setRole("STAFF");
    }

    public Staff(String name, String email, String password) {
        super(name, email, password, "STAFF");
    }
}
