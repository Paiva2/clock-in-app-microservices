package com.root.employeeservice.specifications;

import com.root.crossdbservice.entities.TimeRecord;
import com.root.crossdbservice.entities.UserEntity;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.Order;
import java.util.UUID;

@Component
public class TimeRecordSpecification {
    public Specification<TimeRecord> employeeEq(UUID employeeId) {
        return (entity, query, criteriaBuilder) -> {
            Join<TimeRecord, UserEntity> userJoin = entity.join("user");

            return criteriaBuilder.equal(userJoin.get("id"), employeeId);
        };
    }
    
    public Specification<TimeRecord> monthEq(int month) {
        return (entity, query, criteriaBuilder) -> criteriaBuilder.equal(
                criteriaBuilder.function("MONTH", Integer.class, entity.get("createdAt")),
                month
        );
    }

    public Specification<TimeRecord> yearEq(int year) {
        return (entity, query, criteriaBuilder) -> criteriaBuilder.equal(
                criteriaBuilder.function("YEAR", Integer.class, entity.get("createdAt")),
                year
        );
    }

    public Specification<TimeRecord> dayEq(int day) {
        return (entity, query, criteriaBuilder) -> criteriaBuilder.equal(
                criteriaBuilder.function("DAY", Integer.class, entity.get("createdAt")),
                day
        );
    }
}
