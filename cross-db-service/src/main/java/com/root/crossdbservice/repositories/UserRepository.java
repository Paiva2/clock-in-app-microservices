package com.root.crossdbservice.repositories;

import com.root.crossdbservice.entities.RoleEntity;
import com.root.crossdbservice.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, UUID> {
    @Query(value = "SELECT usr FROM UserEntity usr JOIN FETCH usr.userRoles ur WHERE email = :email")
    Optional<UserEntity> findByEmail(@Param("email") String email);

    @Query(value = "WITH to_list AS " +
            "(SELECT tb_users.id from tb_users " +
            "INNER JOIN tb_users_roles url ON url.user_id = tb_users.id " +
            "INNER JOIN tb_roles rl ON rl.id = url.role_id " +
            "WHERE rl.role_name = :role) " +

            "SELECT * from tb_users usr " +
            "INNER JOIN tb_users_roles url ON url.user_id = usr.id " +
            "LEFT JOIN tb_roles rl ON rl.id = url.role_id " +
            "WHERE url.user_id in (SELECT * FROM to_list)" +
            "ORDER BY usr.created_at DESC",
            nativeQuery = true
    )
    List<UserEntity> findAllByRole(@Param("role") String role);
}
