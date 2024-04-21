package com.root.employeeservice.specifications;

import com.root.crossdbservice.entities.PendingTimeRecordAction;
import com.root.crossdbservice.entities.TimeRecord;
import com.root.crossdbservice.entities.UserEntity;
import com.root.crossdbservice.entities.UserManager;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import javax.persistence.criteria.Join;
import java.util.Date;
import java.util.UUID;

@Component
public class PendingTimeRecordActionSpecification {
    public Specification<PendingTimeRecordAction> managerEq(UUID managerId) {

        return (entity, query, criteriaBuilder) -> {
            Join<TimeRecord, PendingTimeRecordAction> actionRecord = entity.join("timeRecord");
            Join<TimeRecord, UserEntity> recordUser = actionRecord.join("user");
            Join<UserEntity, UserManager> userManager = recordUser.join("userManager");
            Join<UserManager, UserEntity> manager = userManager.join("manager");

            return criteriaBuilder.equal(manager.get("id"), managerId);
        };
    }

    public Specification<PendingTimeRecordAction> employeeEq(UUID employeeId) {
        return (entity, query, criteriaBuilder) -> {
            Join<PendingTimeRecordAction, TimeRecord> joinTimeRecord = entity.join("timeRecord");
            Join<TimeRecord, UserEntity> joinUser = joinTimeRecord.join("user");

            return criteriaBuilder.equal(joinUser.get("id"), employeeId);
        };
    }

    public Specification<PendingTimeRecordAction> yearEq(int year) {
        return (entity, query, criteriaBuilder) ->
                criteriaBuilder.equal(
                        criteriaBuilder.function("YEAR", Integer.class, entity.get("createdAt")), year);
    }

    public Specification<PendingTimeRecordAction> monthEq(int month) {
        return (entity, query, criteriaBuilder) ->
                criteriaBuilder.equal(
                        criteriaBuilder.function("MONTH", Integer.class, entity.get("createdAt")), month);
    }

    public Specification<PendingTimeRecordAction> dayEq(int day) {
        return (entity, query, criteriaBuilder) ->
                criteriaBuilder.equal(
                        criteriaBuilder.function("DAY", Integer.class, entity.get("createdAt")), day);
    }

    public Specification<PendingTimeRecordAction> employeeNameLike(String name) {
        return (entity, query, criteriaBuilder) -> {
            Join<TimeRecord, PendingTimeRecordAction> actionRecord = entity.join("timeRecord");
            Join<TimeRecord, UserEntity> recordUser = actionRecord.join("user");

            return criteriaBuilder.equal(recordUser.get("name"), name);
        };
    }

    public Specification<PendingTimeRecordAction> actionEqual(PendingTimeRecordAction.ActionType action) {
        return (entity, query, criteriaBuilder) -> criteriaBuilder.equal(entity.get("actionType"), action);
    }

    public Specification<PendingTimeRecordAction> minDate(Date dateMin) {
        return (entity, query, criteriaBuilder) ->
                criteriaBuilder.greaterThanOrEqualTo(entity.get("createdAt").as(Date.class), dateMin);
    }

    public Specification<PendingTimeRecordAction> maxDate(Date dateMax) {
        return (entity, query, criteriaBuilder) ->
                criteriaBuilder.lessThanOrEqualTo(entity.get("createdAt").as(Date.class), dateMax);
    }

    public Specification<PendingTimeRecordAction> actionDone(boolean done) {
        return (entity, query, criteriaBuilder) -> criteriaBuilder.equal(entity.get("actionDone"), done);
    }
}
