package com.root.crossdbservice.entities;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "tb_pending_time_record_actions")
public class PendingTimeRecordAction {
    @Id
    @Column(name = "time_record_id")
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(name = "action_type", nullable = false)
    private ActionType actionType;

    @Column(name = "action_done", nullable = true)
    private boolean actionDone = false;

    @Column(name = "time_updated")
    private Date timeUpdated;

    @OneToOne(fetch = FetchType.EAGER)
    @MapsId
    @JoinColumn(name = "time_record_id")
    private TimeRecord timeRecord;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Date createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Date updatedAt;

    public enum ActionType {
        UPDATE("update"), DELETE("delete");

        private final String actionType;

        ActionType(String actionType) {
            this.actionType = actionType;
        }

        public String actionName() {
            return this.actionType;
        }
    }

    public PendingTimeRecordAction() {
    }

    public PendingTimeRecordAction(UUID id, ActionType actionType, boolean actionDone, Date timeUpdated, TimeRecord timeRecord, Date createdAt, Date updatedAt) {
        this.id = id;
        this.actionType = actionType;
        this.actionDone = actionDone;
        this.timeUpdated = timeUpdated;
        this.timeRecord = timeRecord;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public ActionType getActionType() {
        return actionType;
    }

    public void setActionType(ActionType actionType) {
        this.actionType = actionType;
    }

    public boolean isActionDone() {
        return actionDone;
    }

    public void setActionDone(boolean actionDone) {
        this.actionDone = actionDone;
    }

    public Date getTimeUpdated() {
        return timeUpdated;
    }

    public void setTimeUpdated(Date timeUpdated) {
        this.timeUpdated = timeUpdated;
    }

    public TimeRecord getTimeRecord() {
        return timeRecord;
    }

    public void setTimeRecord(TimeRecord timeRecord) {
        this.timeRecord = timeRecord;
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
}
