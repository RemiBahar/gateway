package com.cmd.hms.gateway.model;

import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Entity
@Table(name = "users")
public class User {
    // Fields
    @Id
    // Use sequence for id values
    @Column(name = "user_id", columnDefinition = "integer default nextval('user_seq'::regclass)")
    // Create sequence for id values
    @SequenceGenerator(name = "user_seq", sequenceName = "user_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_seq")
    private int id;

    @Column(name="user_name", unique = true, length = 100)
    @NotBlank(message = "UserName is mandatory")
    @Size(max = 100)
    private String userName;

    @Column(name="password")
    @NotBlank(message = "Password is mandatory")
    private String password;

    @Column(name="active", columnDefinition = "boolean default true")
    private boolean active = true;

    @Column(name="patient_id")
    private Long PatientId = null;

    // Join to role

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(
    name = "user_role", 
    joinColumns = @JoinColumn(name = "user_id"), 
    inverseJoinColumns = @JoinColumn(name = "role_id"))
    private List<Role> Roles;

    // Getters and Setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Long getPatientId() {
        return PatientId;
    }

    public void setPatientId(Long PatientId) {
        this.PatientId = PatientId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public List<Role> getRoles() {
        return Roles;
    }

}
