package com.root.employeeservice.specifications;

import com.root.crossdbservice.entities.UserEntity;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class EmployeeSpecification {
    public Specification<UserEntity> nameLike(String param) {
        return (root, query, builder) -> builder.like(root.get("name"), "%" + param + "%");
    }

    public Specification<UserEntity> emailLike(String param) {
        return (root, query, builder) -> builder.like(root.get("email"), "%" + param + "%");
    }

    public Specification<UserEntity> positionLike(String param) {
        return (root, query, builder) -> builder.like(root.get("position"), "%" + param + "%");
    }

    public Specification<UserEntity> isDisabled(boolean isDisabled) {
        return (root, query, builder) -> builder.equal(root.get("disabled"), isDisabled);
    }
}
