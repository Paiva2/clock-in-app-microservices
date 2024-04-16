package com.root.crossdbservice.repositories;

import com.root.crossdbservice.entities.UserManager;
import com.root.crossdbservice.entities.UserManagerKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserManagerRepository extends JpaRepository<UserManager, UserManagerKey> {

    @Query("SELECT usm FROM UserManager usm JOIN FETCH usm.user usr JOIN FETCH usm.manager mng WHERE usr.id = :userId AND mng.id = :superiorId")
    Optional<UserManager> findByUserAndManager(
            @Param("userId") UUID userId,
            @Param("superiorId") UUID superiorId
    );
}
