package com.cmd.hms.gateway.model;

import java.lang.reflect.Array;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@Entity
@Table(name="gender")
public class Gender {
    /** Represents a gender. Decoupled from Patient by being unidirectional, allowing it to be reused for different classes.
    */
    /* 
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    
    boolean isAdmin = authentication.getAuthorities().stream()
      .anyMatch(r -> r.getAuthority().equals("ADMIN"));*/
      
    // Fields
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Use Id sequencing unique for this table
    @Column(name="gender_id")
    private Long GenderId;

    @Column(name="title", nullable = false, length = 100)
    @NotBlank(message="Gender title required")
    @Size(max=100)
    private String Title;

    public Boolean hasRole(String role){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String[] roles = authentication.getAuthorities().toString().split(",", -1);
        for (int i = 0; i < roles.length; i++) {
            if(roles[i].contains(role)){
                return true;
            }
        }
        return false;
    }


    
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
    
    @PreAuthorize("hasRole('ADMIN')")
    public void setTitle(String Title) {
        if(this.hasRole("USER")){
            System.out.println("set title");
            this.Title = Title;
        }
        
    }
    
}