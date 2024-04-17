package com.root.crossdbservice.entities;

import org.hibernate.annotations.*;

import javax.persistence.*;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Table(name = "tb_users")
@Entity
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "name")
    private String name;

    @Column(name = "password")
    private String password;

    @Column(name = "email", unique = true)
    private String email;

    @Column(name = "position", nullable = true)
    private String position;

    @Column(name = "disabled")
    private Boolean disabled = false;

    @Column(name = "disabled_at", nullable = true)
    private Date disabledAt;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Date createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Date updatedAt;

    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @Fetch(FetchMode.JOIN)
    private Set<UserRole> userRoles;

    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @Fetch(FetchMode.JOIN)
    private Set<UserManager> userManager;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @Fetch(FetchMode.JOIN)
    private List<TimeRecord> timeRecords;

    public UserEntity() {
    }

    public UserEntity(UUID id, String name, String password, String email, String position, Boolean disabled, Date disabledAt, Date createdAt, Date updatedAt, Set<UserRole> userRoles, Set<UserManager> userManager, List<TimeRecord> timeRecords) {
        this.id = id;
        this.name = name;
        this.password = password;
        this.email = email;
        this.position = position;
        this.disabled = disabled;
        this.disabledAt = disabledAt;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.userRoles = userRoles;
        this.userManager = userManager;
        this.timeRecords = timeRecords;
    }

    public List<TimeRecord> getTimeRecords() {
        return timeRecords;
    }

    public void setTimeRecords(List<TimeRecord> timeRecords) {
        this.timeRecords = timeRecords;
    }

    public Boolean getDisabled() {
        return disabled;
    }

    public void setDisabled(Boolean disabled) {
        this.disabled = disabled;
    }

    public Date getDisabledAt() {
        return disabledAt;
    }

    public void setDisabledAt(Date disabledAt) {
        this.disabledAt = disabledAt;
    }

    public Set<UserManager> getUserManager() {
        return userManager;
    }

    public void setUserManager(Set<UserManager> userManager) {
        this.userManager = userManager;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Set<UserRole> getUserRoles() {
        return userRoles;
    }

    public void setUserRoles(Set<UserRole> userRoles) {
        this.userRoles = userRoles;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
