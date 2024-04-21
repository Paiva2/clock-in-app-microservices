package com.root.crossdbservice.entities;

import org.hibernate.annotations.*;

import javax.persistence.*;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "tb_time_records")
public class TimeRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "record_hour", updatable = true, nullable = false)
    private Date recordHour;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Date createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Date updatedAt;

    @Column(name = "disabled", nullable = true)
    private Boolean disabled = false;

    @Column(name = "disabled_at", nullable = true)
    private Date disabledAt;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    @Fetch(FetchMode.JOIN)
    private UserEntity user;

    @OneToOne(mappedBy = "timeRecord", cascade = CascadeType.ALL)
    @PrimaryKeyJoinColumn
    @OnDelete(action = OnDeleteAction.NO_ACTION)
    private PendingTimeRecordAction pendingTimeRecordAction;

    public TimeRecord() {
    }

    public TimeRecord(UUID id, Date recordHour, Date createdAt, Date updatedAt, Boolean disabled, Date disabledAt, UserEntity user, PendingTimeRecordAction pendingTimeRecordAction) {
        this.id = id;
        this.recordHour = recordHour;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.disabled = disabled;
        this.disabledAt = disabledAt;
        this.user = user;
        this.pendingTimeRecordAction = pendingTimeRecordAction;
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

    public PendingTimeRecordAction getPendingTimeRecordAction() {
        return pendingTimeRecordAction;
    }

    public void setPendingTimeRecordAction(PendingTimeRecordAction pendingTimeRecordAction) {
        this.pendingTimeRecordAction = pendingTimeRecordAction;
    }

    public Date getRecordHour() {
        return recordHour;
    }

    public void setRecordHour(Date recordHour) {
        this.recordHour = recordHour;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public UUID getId() {
        return id;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public UserEntity getUser() {
        return user;
    }
}
