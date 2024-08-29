package org.example.app.config;

import org.example.app.model.Role;
import org.example.app.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private RoleRepository roleRepository;

    @Override
    public void run(String... args) throws Exception {
        if (roleRepository.findAll().isEmpty()) {
            Role userRole = new Role();
            userRole.setName("ROLE_USER");
            Role adminRole = new Role();
            adminRole.setName("ROLE_ADMIN");
            Role superAdminRole = new Role();
            superAdminRole.setName("ROLE_SUPER_ADMIN");

            roleRepository.saveAll(Arrays.asList(userRole, adminRole, superAdminRole));
        }
    }
}
