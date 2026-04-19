package com.lawfirm.cms.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@DiscriminatorValue("LAWYER")
public class Lawyer extends User {

    private String specialization;

    @OneToMany(mappedBy = "assignedLawyer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<LegalCase> assignedCases = new ArrayList<>();

    public Lawyer() {
        setRole("LAWYER");
    }

    public Lawyer(String name, String email, String password, String specialization) {
        super(name, email, password, "LAWYER");
        this.specialization = specialization;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public List<LegalCase> getAssignedCases() {
        return assignedCases;
    }

    public void setAssignedCases(List<LegalCase> assignedCases) {
        this.assignedCases = assignedCases;
    }
}
