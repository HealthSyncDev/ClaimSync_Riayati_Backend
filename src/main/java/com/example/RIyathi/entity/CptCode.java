package com.example.RIyathi.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "tbl_cptcode")
public class CptCode {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "cpt_code")
    private String cptCode;

    @Column(name = "cpt_category")
    private String cptCategory;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCptCode() {
        return cptCode;
    }

    public void setCptCode(String cptCode) {
        this.cptCode = cptCode;
    }

    public String getCptCategory() {
        return cptCategory;
    }

    public void setCptCategory(String cptCategory) {
        this.cptCategory = cptCategory;
    }
}
