package com.root.employeeservice.specifications;

import com.root.crossdbservice.entities.PendingTimeRecordAction;
import com.root.crossdbservice.entities.TimeRecord;
import com.root.crossdbservice.entities.UserEntity;
import com.root.crossdbservice.entities.UserManager;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Join;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

@Component
public class PendingTimeRecordActionSpecification {
    public Specification<PendingTimeRecordAction> managerEq(UUID managerId) {

        return (root, query, criteriaBuilder) -> {
            Join<TimeRecord, PendingTimeRecordAction> actionRecord = root.join("timeRecord");
            Join<TimeRecord, UserEntity> recordUser = actionRecord.join("user");
            Join<UserEntity, UserManager> userManager = recordUser.join("userManager");
            Join<UserManager, UserEntity> manager = userManager.join("manager");

            return criteriaBuilder.equal(manager.get("id"), managerId);
        };
    }

    public Specification<PendingTimeRecordAction> employeeNameLike(String name) {
        return (root, query, criteriaBuilder) -> {
            Join<TimeRecord, PendingTimeRecordAction> actionRecord = root.join("timeRecord");
            Join<TimeRecord, UserEntity> recordUser = actionRecord.join("user");

            return criteriaBuilder.equal(recordUser.get("name"), name);
        };
    }

    public Specification<PendingTimeRecordAction> actionEqual(PendingTimeRecordAction.ActionType action) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("actionType"), action);
    }

    public Specification<PendingTimeRecordAction> minDate(Date dateMin) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.greaterThanOrEqualTo(root.get("createdAt").as(Date.class), dateMin);
    }

    public Specification<PendingTimeRecordAction> maxDate(Date dateMax) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.lessThanOrEqualTo(root.get("createdAt").as(Date.class), dateMax);
    }

    public Specification<PendingTimeRecordAction> actionDone(boolean done) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("actionDone"), done);
    }
}
