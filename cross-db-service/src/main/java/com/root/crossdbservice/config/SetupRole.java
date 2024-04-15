package com.root.crossdbservice.config;

import com.root.crossdbservice.entities.RoleEntity;
import com.root.crossdbservice.entities.UserEntity;
import com.root.crossdbservice.entities.UserRole;
import com.root.crossdbservice.repositories.RoleRepository;
import com.root.crossdbservice.repositories.UserRepository;
import com.root.crossdbservice.repositories.UserRoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

import java.util.*;

@Configuration
public class SetupRole implements CommandLineRunner {
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserRoleRepository userRoleRepository;

    private final String passwordDefault = "$2a$10$7KwfQCKd.4eFcQgEq/.v2.uCCRcuDbBpELoyv/CGohd8Kim0dd4ne";

    @Override
    public void run(String... args) throws Exception {
        Optional<RoleEntity> rolesExists = this.roleRepository.findByRole(RoleEntity.Role.USER);
        if (rolesExists.isPresent()) return;

        Set<RoleEntity> roleSet = new LinkedHashSet<RoleEntity>() {{
            add(new RoleEntity(RoleEntity.Role.USER));
            add(new RoleEntity(RoleEntity.Role.ADMIN));
            add(new RoleEntity(RoleEntity.Role.HUMAN_RESOURCES));
            add(new RoleEntity(RoleEntity.Role.MANAGER));
        }};

        List<RoleEntity> roles = this.roleRepository.saveAll(roleSet);

        UserEntity userAdmin = new UserEntity();
        userAdmin.setPosition("Admin");
        userAdmin.setEmail("admin@email.com");
        userAdmin.setName("Admin");
        userAdmin.setPassword(this.passwordDefault); //123456

        UserEntity createdAdmin = this.userRepository.save(userAdmin);

        UserRole userRole = new UserRole();
        userRole.setUser(createdAdmin);
        userRole.setRole(roles.get(1));

        this.userRoleRepository.save(userRole);
    }
}
