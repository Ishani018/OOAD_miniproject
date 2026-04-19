package com.lawfirm.cms.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@DiscriminatorValue("CLIENT")
public class Client extends User {

    private String contactInfo;

    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<LegalCase> ownedCases = new ArrayList<>();

    public Client() {
        setRole("CLIENT");
    }

    public Client(String name, String email, String password, String contactInfo) {
        super(name, email, password, "CLIENT");
        this.contactInfo = contactInfo;
    }

    public String getContactInfo() {
        return contactInfo;
    }

    public void setContactInfo(String contactInfo) {
        this.contactInfo = contactInfo;
    }

    public List<LegalCase> getOwnedCases() {
        return ownedCases;
    }

    public void setOwnedCases(List<LegalCase> ownedCases) {
        this.ownedCases = ownedCases;
    }
}
