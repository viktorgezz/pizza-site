package ru.viktorgezz.auth_service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.viktorgezz.auth_service.dto.rq.RegisterRequest;
import ru.viktorgezz.auth_service.model.User;
import ru.viktorgezz.auth_service.repo.UserRepo;
import ru.viktorgezz.auth_service.util.Role;

@RestController
public class RegistrationController {

    private final UserRepo userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public RegistrationController(UserRepo repo,
                                  PasswordEncoder encoder) {
        this.userRepository = repo;
        this.passwordEncoder = encoder;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        if (userRepository.findByUsername(request.username()).isPresent()) {
            return ResponseEntity.badRequest().body("User exists");
        }

        userRepository.save(new User(
                request.username(),
                passwordEncoder.encode(request.password()),
                Role.USER)
        );
        return ResponseEntity.ok("Registered");
    }
}
