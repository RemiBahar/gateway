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
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name="user_role")
public class UserRole {
    /** Represents a role
    */
      
    // Fields
    @Id
    // @GeneratedValue(strategy = GenerationType.AUTO) // Use Id sequencing unique for this table
    //@Column(name = "user_role_id", columnDefinition = "integer default nextval('user_role_seq'::regclass)")
    // Create sequence for id values
    //@SequenceGenerator(name = "user_role_seq", sequenceName = "user_role_seq", allocationSize = 1)
    //@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_role_seq")
    @Column(name = "user_role_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;

    @Column(name="role_id")
    private Long RoleId;

    @Column(name="user_id")
    private Long UserId;

    @ManyToOne(cascade = {CascadeType.MERGE}, fetch=FetchType.LAZY)
    @JoinColumn(name="role_id",insertable = false, updatable = false)
    private Role Role;

    @ManyToOne(cascade = {CascadeType.MERGE}, fetch=FetchType.LAZY)
    @JoinColumn(name="user_id",insertable = false, updatable = false)
    private User User;
    
    // Getters and setters
    public Long getId() {
        return Id;
    }

    public void setId(Long Id) {  
        this.Id = Id;   
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

    public User getUser() {
        return User;
    }

    public Long getUserId() {
        return UserId;
    }
    
    public void setUserId(Long UserId) {
        this.UserId = UserId;
    }
 
}