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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserManagerKey that = (UserManagerKey) o;
        return Objects.equals(user, that.user) && Objects.equals(manager, that.manager);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user, manager);
    }
}
