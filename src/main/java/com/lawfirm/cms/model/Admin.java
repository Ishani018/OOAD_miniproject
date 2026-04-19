package com.lawfirm.cms.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("ADMIN")
public class Admin extends User {

    public Admin() {
        setRole("ADMIN");
    }

    public Admin(String name, String email, String password) {
        super(name, email, password, "ADMIN");
    }
}
