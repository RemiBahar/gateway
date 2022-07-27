package com.cmd.hms.gateway.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import org.springframework.security.access.prepost.PreAuthorize;

@PreAuthorize("hasRole('ADMIN')")
@Entity
@Table(name="title")
public class Title {
    /** Represents a title. Decoupled from Patient by being unidirectional, allowing it to be reused for different classes.
    */

    // Fields
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Use Id sequencing unique for this table
    @Column(name="title_id")
    private Long TitleId;

    @Column(name="title", length = 100, nullable = false)
    @NotBlank(message="Title required")
    @Size(max=100)
    private String Title;

    // Getters and setters
    @PreAuthorize("hasRole('ADMIN')")
    public Long getTitleId() {
        return TitleId;
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void setTitleId(Long TitleId) {
        this.TitleId = TitleId;
    }

    @PreAuthorize("hasRole('ADMIN')")
    public String getTitle() {
        return Title;
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void setTitle(String Title) {
        this.Title = Title;
    }
    
}