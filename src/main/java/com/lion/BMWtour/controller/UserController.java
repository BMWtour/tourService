package com.lion.BMWtour.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class UserController {

    @GetMapping("/login")
    public String userLoginForm() {
        return "";
    }

    @PostMapping("/login")
    public String userLogin() {
        return "";
    }
}
