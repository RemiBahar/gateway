package com.cmd.hms.gateway.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Entity
@Table(name="gender")
public class Gender {
    /** Represents a gender. Decoupled from Patient by being unidirectional, allowing it to be reused for different classes.
    */

    // Fields
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Use Id sequencing unique for this table
    @Column(name="gender_id")
    private Long GenderId;

    @Column(name="title", nullable = false, length = 100)
    @NotBlank(message="Gender title required")
    @Size(max=100)
    private String Title;

    // Getters and setters
    public Long getGenderId() {
        return GenderId;
    }

    public void setGenderId(Long GenderId) {
        this.GenderId = GenderId;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String Title) {
        this.Title = Title;
    }
    
}