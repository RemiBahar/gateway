package com.cmd.hms.gateway.model;

import java.util.List;

import javax.annotation.Generated;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;

@Entity
//@Table(name="roles")
@Table(name="roles")
public class Role {
    /** Represents a role
    */
      
    // Fields
    @Id
    // Use sequence for id values
    @Column(name = "role_id", columnDefinition = "integer default nextval('role_id_seq'::regclass)")
    // Create sequence for id values
    @SequenceGenerator(name = "role_id_seq", sequenceName = "role_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "role_id_seq")
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