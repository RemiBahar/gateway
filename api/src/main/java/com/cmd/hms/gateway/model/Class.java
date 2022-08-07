package com.cmd.hms.gateway.model;

import javax.persistence.Entity;
import javax.persistence.FetchType;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Entity
@Table(name = "class")
public class Class {
    // Fields
    @Id
    @Column(name = "class_id", columnDefinition = "integer default nextval('class_seq'::regclass)")
    // Create sequence for id values
    @SequenceGenerator(name = "class_seq", sequenceName = "class_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "class_seq")
    private Long ClassId;

    @Column(name="name", nullable = false, length = 100)
    @NotBlank(message="Class name required")
    @Size(max=100)
    private String Name;

    @OneToMany(mappedBy = "Class") 
    private List<Field> Fields;


    // Getters and setters
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
