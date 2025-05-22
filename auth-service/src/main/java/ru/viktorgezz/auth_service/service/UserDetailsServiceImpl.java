package ru.viktorgezz.auth_service.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.viktorgezz.auth_service.repo.UserRepo;
import ru.viktorgezz.auth_service.util.CustomUserDetails;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private static final String USER_NOT_FOUND = "User not found";

    private final UserRepo userRepo;

    @Autowired
    public UserDetailsServiceImpl(
            UserRepo userRepo
    ) {
        this.userRepo = userRepo;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return new CustomUserDetails(userRepo.
                findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(USER_NOT_FOUND)));
    }
}
