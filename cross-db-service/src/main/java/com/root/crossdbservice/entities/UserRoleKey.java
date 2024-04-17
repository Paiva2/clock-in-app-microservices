package com.root.crossdbservice.entities;

import javax.persistence.*;
import java.io.Serializable;
import java.util.UUID;

@Embeddable
public class UserRoleKey implements Serializable {
    private static final long serialVersionUID = 8242738535542671872L;

    @Column(name = "user_id")
    private UUID userId;

    @Column(name = "role_id")
    private UUID roleId;

    public UserRoleKey() {
    }

    public UserRoleKey(UUID userId, UUID roleId) {
        this.userId = userId;
        this.roleId = roleId;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public UUID getRoleId() {
        return roleId;
    }

    public void setRoleId(UUID roleId) {
        this.roleId = roleId;
    }
}
