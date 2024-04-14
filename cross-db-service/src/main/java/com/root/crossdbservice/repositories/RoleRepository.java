package com.root.crossdbservice.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.root.crossdbservice.entities.RoleEntity;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface RoleRepository extends JpaRepository<RoleEntity, UUID> {
    @Query("SELECT r from RoleEntity r WHERE roleName = :role")
    Optional<RoleEntity> findByRole(@Param("role") RoleEntity.Role role);
}
