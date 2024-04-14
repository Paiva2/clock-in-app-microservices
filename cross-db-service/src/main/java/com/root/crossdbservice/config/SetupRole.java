package com.root.crossdbservice.config;

import com.root.crossdbservice.entities.RoleEntity;
import com.root.crossdbservice.repositories.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Configuration
public class SetupRole implements CommandLineRunner {
    @Autowired
    private RoleRepository roleRepository;

    @Override
    public void run(String... args) throws Exception {
        Optional<RoleEntity> rolesExists = this.roleRepository.findByRole(RoleEntity.Role.USER);
        if (rolesExists.isPresent()) return;

        Set<RoleEntity> roleSet = new HashSet<RoleEntity>() {{
            add(new RoleEntity(RoleEntity.Role.USER));
            add(new RoleEntity(RoleEntity.Role.ADMIN));
            add(new RoleEntity(RoleEntity.Role.HUMAN_RESOURCES));
        }};

        this.roleRepository.saveAll(roleSet);
    }
}
