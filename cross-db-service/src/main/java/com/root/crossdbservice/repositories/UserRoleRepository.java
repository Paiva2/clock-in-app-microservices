package com.root.crossdbservice.repositories;

import com.root.crossdbservice.entities.UserRole;
import com.root.crossdbservice.entities.UserRoleKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, UserRoleKey> {
}
