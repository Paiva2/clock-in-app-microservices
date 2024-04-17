package com.root.crossdbservice.entities;

import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Embeddable
public class UserManagerKey implements Serializable {
    @JoinColumn(name = "user_id")
    private UUID user;

    @JoinColumn(name = "manager_id")
    private UUID manager;

    public UserManagerKey(UUID user, UUID manager) {
        this.user = user;
        this.manager = manager;
    }

    public UserManagerKey() {
    }

    public UUID getUser() {
        return user;
    }

    public void setUser(UUID user) {
        this.user = user;
    }

    public UUID getManager() {
        return manager;
    }

    public void setManager(UUID manager) {
        this.manager = manager;
    }
}
