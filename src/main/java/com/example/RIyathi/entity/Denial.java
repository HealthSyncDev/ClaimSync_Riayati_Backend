package com.example.RIyathi.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "tbl_denials")
public class Denial {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "denial_code")
    private String denialCode;

    @Column(name = "denial_type")
    private String denialType;

    @Column(name = "denial_reason")
    private String denialReason;

    @Column(name = "denial_category")
    private String denialCategory;

    @Column(name = "category")
    private String category;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDenialCode() {
        return denialCode;
    }

    public void setDenialCode(String denialCode) {
        this.denialCode = denialCode;
    }

    public String getDenialType() {
        return denialType;
    }

    public void setDenialType(String denialType) {
        this.denialType = denialType;
    }

    public String getDenialReason() {
        return denialReason;
    }

    public void setDenialReason(String denialReason) {
        this.denialReason = denialReason;
    }

    public String getDenialCategory() {
        return denialCategory;
    }

    public void setDenialCategory(String denialCategory) {
        this.denialCategory = denialCategory;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
