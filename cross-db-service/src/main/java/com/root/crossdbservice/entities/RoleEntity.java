package com.root.crossdbservice.entities;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import java.util.Date;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "tb_roles")
public class RoleEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", unique = true)
    private Role role;

    @CreatedDate
    @Column(name = "created_at")
    private Date createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private Date updatedAt;

    @OneToMany(mappedBy = "role", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<UserRole> userRoles;

    public RoleEntity() {}

    public RoleEntity(UUID id, Role role, Date createdAt, Date updatedAt, Set<UserRole> userRoless) {
        this.id = id;
        this.role = role;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.userRoles = userRoless;
    }

    public RoleEntity(Role role) {
        this.role = role;;
        this.createdAt = new Date();
        this.updatedAt = new Date();
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
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

    public enum Role {
        USER("USER"), ADMIN("ADMIN"), HUMAN_RESOURCES("HUMAN_RESOURCES");

        String role;

        Role(String role){
            this.role = role;
        }

        public String getRoleName(){
            return this.role;
        }
    }
}
