package com.cmd.hms.gateway.model;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonProperty.Access;

@Entity
@Table(name="privilege")
public class Privilege {
    // Fields
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Use Id sequencing unique for this table
    @Column(name = "privilege_id", updatable = false, nullable = false)
    private Long PrivilegeId;

    @ManyToOne(cascade = {CascadeType.MERGE}, fetch=FetchType.LAZY)
    @JoinColumn(name="role_id",insertable = false, updatable = false)
    private Role Role;
    
    @Column(name="role_id")
    private Long RoleId;

    @ManyToOne(cascade = {CascadeType.MERGE}, fetch=FetchType.LAZY)
    @JoinColumn(name="class_id",insertable = false, updatable = false)
    private Class Class;
    
    @Column(name="class_id")
    private Long ClassId;

    @Column(name="all_fields")
    private Boolean AllFields;

    @ManyToOne(cascade = {CascadeType.MERGE}, fetch=FetchType.LAZY)
    @JoinColumn(name="field_id",insertable = false, updatable = false)
    private Field Field;
    
    @Column(name="field_id")
    private Long FieldId;

    @Column(name="access_level")
    private Long AccessLevel;

    @Column(name="condition")
    private String Condition;

    // Getters and setters
    public Long getPrivilegeId() {
        return PrivilegeId;
    }

    public void setPrivilegeId(Long PrivilegeId) {  
        this.PrivilegeId = PrivilegeId;   
    }

    public Role getRole() {
        return Role;
    }

    public Long getRoleId() {
        return RoleId;
    }

    public void setRoleId(Long RoleId) {  
        this.RoleId = RoleId;   
    }

    public Long getClassId() {
        return ClassId;
    }

    public void setClassId(Long ClassId) {  
        this.ClassId = ClassId;   
    }

    public Boolean getAllFields() {
        return AllFields;
    }

    public void setAllFields(Boolean AllFields) {  
        this.AllFields = AllFields;   
    }

    public Field getField() {
        return Field;
    }

    public Long getFieldId() {
        return FieldId;
    }

    public void setFieldId(Long FieldId) {  
        this.FieldId = FieldId;   
    }

    public Long getAccessLevel() {
        return AccessLevel;
    }

    public void setAccessLevel(Long AccessLevel) {  
        this.AccessLevel = AccessLevel;   
    }

    public String getCondition() {
        return Condition;
    }

    public void setCondition(String Condition) {  
        this.Condition = Condition;   
    }


}
