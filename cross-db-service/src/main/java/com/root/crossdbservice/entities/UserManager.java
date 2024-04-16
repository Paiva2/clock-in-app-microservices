package com.root.crossdbservice.entities;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "tb_users_managers")
public class UserManager {
    @EmbeddedId
    @JoinColumn(name = "id")
    private UserManagerKey userManagerKey = new UserManagerKey();

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    @MapsId("user")
    private UserEntity user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "manager_id")
    @MapsId("manager")
    private UserEntity manager;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Date createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Date updatedAt;

    public UserManager() {
    }

    public UserManager(UserManagerKey userManagerKey, UserEntity user, UserEntity manager, Date createdAt, Date updatedAt) {
        this.userManagerKey = userManagerKey;
        this.user = user;
        this.manager = manager;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public UserEntity getManager() {
        return manager;
    }

    public void setManager(UserEntity manager) {
        this.manager = manager;
    }

    public UserManagerKey getUserManagerKey() {
        return userManagerKey;
    }

    public void setUserManagerKey(UserManagerKey userManagerKey) {
        this.userManagerKey = userManagerKey;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserManager that = (UserManager) o;
        return Objects.equals(userManagerKey, that.userManagerKey) && Objects.equals(user, that.user) && Objects.equals(manager, that.manager) && Objects.equals(createdAt, that.createdAt) && Objects.equals(updatedAt, that.updatedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userManagerKey, user, manager, createdAt, updatedAt);
    }
}
