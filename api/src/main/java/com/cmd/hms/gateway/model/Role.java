package com.cmd.hms.gateway.model;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Entity
@Table(name="roles")
public class Role {
    /** Represents a role
    */
      
    // Fields
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Use Id sequencing unique for this table
    @Column(name="role_id")
    private Long RoleId;

    @Column(name="title", nullable = false, length = 100)
    @NotBlank(message="Role title required")
    @Size(max=100)
    private String Title;
    
    @OneToMany(mappedBy = "Role", fetch=FetchType.EAGER) 
    private List<Privilege> Privileges;

    // Getters and setters
    public Long getRoleId() {
        return RoleId;
    }

    public void setRoleId(Long RoleId) {  
        this.RoleId = RoleId;   
    }

    public String getTitle() {
        return Title;
    }
    
    public void setTitle(String Title) {
        this.Title = Title;
    }
    
    public List<Privilege> getPrivileges() {
        return Privileges;
    } 
}