package com.root.crossdbservice.entities;

import javax.persistence.*;

@Entity
@Table(name = "users_roles")
public class UserRole {
    @EmbeddedId
    private UserRoleKey id = new UserRoleKey();

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @ManyToOne
    @MapsId("roleId")
    @JoinColumn(name = "role_id")
    private RoleEntity role;

    public UserRole() {
    }

    public UserRoleKey getId() {
        return id;
    }

    public UserRole(UserRoleKey id, UserEntity user, RoleEntity role) {
        this.id = id;
        this.user = user;
        this.role = role;
    }

    public void setId(UserRoleKey id) {
        this.id = id;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public RoleEntity getRole() {
        return role;
    }

    public void setRole(RoleEntity role) {
        this.role = role;
    }
}
