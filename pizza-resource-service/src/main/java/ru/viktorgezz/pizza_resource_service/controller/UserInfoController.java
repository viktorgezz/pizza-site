package ru.viktorgezz.pizza_resource_service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.viktorgezz.pizza_resource_service.model.UserJwtInfo;
import ru.viktorgezz.pizza_resource_service.service.JwtService;

@RestController
public class UserInfoController {

    private final JwtService jwtService;

    @Autowired
    public UserInfoController(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @GetMapping("/me")
    public UserJwtInfo getCurrentUser() {
        return jwtService.getCurrentUserInfo();
    }
}
