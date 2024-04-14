package com.root.crossdbservice.repositories;

import com.root.crossdbservice.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, UUID> {
    @Query(value = "SELECT usr FROM UserEntity usr JOIN FETCH usr.userRoles ur WHERE email = :email")
    Optional<UserEntity> findByEmail(@Param("email") String email);
}
