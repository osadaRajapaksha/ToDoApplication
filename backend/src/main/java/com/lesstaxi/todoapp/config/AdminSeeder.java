package com.lesstaxi.todoapp.config;

import com.lesstaxi.todoapp.models.ERole;
import com.lesstaxi.todoapp.models.User;
import com.lesstaxi.todoapp.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AdminSeeder implements CommandLineRunner {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder encoder;

    @Override
    public void run(String... args) throws Exception {
        if (!userRepository.existsByUsername("admin")) {
            User admin = new User("admin", encoder.encode("admin123"));
            admin.setRole(ERole.ROLE_ADMIN);
            userRepository.save(admin);
            System.out.println("Admin user created: admin / admin123");
        }
    }
}
