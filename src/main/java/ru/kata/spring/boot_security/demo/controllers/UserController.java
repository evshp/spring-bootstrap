package ru.kata.spring.boot_security.demo.controllers;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import ru.kata.spring.boot_security.demo.models.User;
import ru.kata.spring.boot_security.demo.security.UserPrincipal;


@Controller
public class UserController {

    @GetMapping("/user")
    public String userPage(ModelMap model, @AuthenticationPrincipal UserPrincipal userPrincipal) {
        User user = userPrincipal.getUserDetails();
        model.addAttribute("user", user);
        return "user";
    }


}
