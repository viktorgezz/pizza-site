package ru.viktorgezz.pizza_resource_service.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import ru.viktorgezz.pizza_resource_service.model.UserJwtInfo;

import java.util.List;

@Service
public class JwtService {

    private static final Logger log = LoggerFactory.getLogger(JwtService.class);
    
    private static final String CLAIM_ID = "id";
    private static final String CLAIM_USERNAME = "username";
    private static final String CLAIM_ROLE = "role";
    private static final String JWT_NOT_FOUND = "JWT token not found in security context";

    public UserJwtInfo getCurrentUserInfo() {
        Object principal = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        if (principal instanceof Jwt jwt) {
            Long id = jwt.getClaim(CLAIM_ID);
            String username = jwt.getClaim(CLAIM_USERNAME);
            List<String> roles = jwt.getClaimAsStringList(CLAIM_ROLE);

            return new UserJwtInfo(id, username, roles);
        }

        log.error(JWT_NOT_FOUND);
        throw new IllegalStateException(JWT_NOT_FOUND);
    }
}
