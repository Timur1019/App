package org.example.app.controller;

import org.example.app.model.Role;
import org.example.app.model.User;
import org.example.app.repository.RoleRepository;
import org.example.app.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Autowired
    public AdminController(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    /**
     * Получить всех пользователей
     */
    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userRepository.findAll();
        return ResponseEntity.ok(users);
    }

    /**
     * Присвоить пользователю роль администратора
     */
    @PostMapping("/promote/{userId}")
    public ResponseEntity<String> promoteUserToAdmin(@PathVariable Long userId) {
        Optional<User> userOpt = userRepository.findById(userId);

        if (userOpt.isPresent()) {
            User user = userOpt.get();
            Role adminRole = roleRepository.findByName("ROLE_ADMIN");
            user.getRoles().add(adminRole);
            userRepository.save(user);
            return ResponseEntity.ok("User promoted to admin successfully.");
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Дополнительные административные маршруты можно добавить здесь
}
