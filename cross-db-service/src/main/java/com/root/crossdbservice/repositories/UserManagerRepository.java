package com.root.crossdbservice.repositories;

import com.root.crossdbservice.entities.UserManager;
import com.root.crossdbservice.entities.UserManagerKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserManagerRepository extends JpaRepository<UserManager, UserManagerKey> {
}
