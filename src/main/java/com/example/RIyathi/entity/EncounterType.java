package com.example.RIyathi.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "tbl_encountertype")
public class EncounterType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "encounter_type_num")
    private Integer encounterTypeNum;

    @Column(name = "encounter_desc")
    private String encounterDesc;

    @Column(name = "patient_type")
    private String patientType;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getEncounterTypeNum() {
        return encounterTypeNum;
    }

    public void setEncounterTypeNum(Integer encounterTypeNum) {
        this.encounterTypeNum = encounterTypeNum;
    }

    public String getEncounterDesc() {
        return encounterDesc;
    }

    public void setEncounterDesc(String encounterDesc) {
        this.encounterDesc = encounterDesc;
    }

    public String getPatientType() {
        return patientType;
    }

    public void setPatientType(String patientType) {
        this.patientType = patientType;
    }
}
