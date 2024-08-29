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
@RequestMapping("/api/superadmin")
public class SuperAdminController {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Autowired
    public SuperAdminController(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    /**
     * Получить всех пользователей с их ролями
     */
    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsersWithRoles() {
        List<User> users = userRepository.findAll();
        return ResponseEntity.ok(users);
    }

    /**
     * Присвоить пользователю роль супер администратора
     */
    @PostMapping("/promote/{userId}")
    public ResponseEntity<String> promoteUserToSuperAdmin(@PathVariable Long userId) {
        Optional<User> userOpt = userRepository.findById(userId);

        if (userOpt.isPresent()) {
            User user = userOpt.get();
            Role superAdminRole = roleRepository.findByName("ROLE_SUPER_ADMIN");
            user.getRoles().add(superAdminRole);
            userRepository.save(user);
            return ResponseEntity.ok("User promoted to super admin successfully.");
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Удалить пользователя
     */
    @DeleteMapping("/delete/{userId}")
    public ResponseEntity<String> deleteUser(@PathVariable Long userId) {
        if (userRepository.existsById(userId)) {
            userRepository.deleteById(userId);
            return ResponseEntity.ok("User deleted successfully.");
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Дополнительные маршруты для супер администраторов можно добавить здесь
}
