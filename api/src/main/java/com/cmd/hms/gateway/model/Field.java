package com.cmd.hms.gateway.model;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Entity
@Table(name = "field")
public class Field {
    // Fields
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Use Id sequencing unique for this table
    @Column(name = "field_id", updatable = false, nullable = false)
    private Long FieldId;

    @Column(name="class_id")
    private Long ClassId;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="class_id",insertable = false, updatable = false)
    private Class Class;

    @Column(name="name", nullable = false, length = 100)
    @NotBlank(message="Class name required")
    @Size(max=100)
    private String Name;

    // Getters and setters
    public Long getFieldId() {
        return FieldId;
    }

    public void setFieldId(Long FieldId) {  
        this.FieldId = FieldId;   
    }

    public Long getClassId() {
        return ClassId;
    }

    public void setClassId(Long ClassId) {  
        this.ClassId = ClassId;   
    }
  

    public String getName() {
        return Name;
    }
    
    public void setName(String Name) {
        this.Name = Name;
    }
 
}
