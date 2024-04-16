package com.root.crossdbservice.entities;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.util.Date;
import java.util.Objects;
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

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Set<UserRole> userRoles;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Set<UserManager> userManager;

    public UserEntity() {
    }

    public UserEntity(
            UUID id,
            String name,
            String password,
            String email,
            String position,
            Date createdAt,
            Date updatedAt,
            Date disabledAt,
            Set<UserRole> userRoles,
            Set<UserManager> userManager
    ) {
        this.id = id;
        this.name = name;
        this.password = password;
        this.email = email;
        this.position = position;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.userRoles = userRoles;
        this.userManager = userManager;
        this.disabledAt = disabledAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserEntity that = (UserEntity) o;
        return Objects.equals(id, that.id) && Objects.equals(name, that.name) && Objects.equals(password, that.password) && Objects.equals(email, that.email) && Objects.equals(position, that.position) && Objects.equals(disabled, that.disabled) && Objects.equals(disabledAt, that.disabledAt) && Objects.equals(createdAt, that.createdAt) && Objects.equals(updatedAt, that.updatedAt) && Objects.equals(userRoles, that.userRoles) && Objects.equals(userManager, that.userManager);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, password, email, position, disabled, disabledAt, createdAt, updatedAt, userRoles, userManager);
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
