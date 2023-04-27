package ru.kata.spring.boot_security.demo.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.Util.UserValidator;
import ru.kata.spring.boot_security.demo.models.User;
import ru.kata.spring.boot_security.demo.service.UserServiceImpl;

@RestController
public class UserResources {

    private final UserServiceImpl userService;


    @Autowired
    public UserResources(UserServiceImpl userService) {
        this.userService = userService;

    }

    @GetMapping("/user/{userId}")
    public User findById(@PathVariable long userId) {
        return userService.getUserById(userId);
    }

    @PostMapping("/user/edit")
    public boolean updateEdit(@RequestBody User user, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {

        }
        return userService.update(user);

    }

    @DeleteMapping("/user/delete/{userId}")
    public void delete(@PathVariable long userId) {
        userService.removeUserById(userId);
    }

}
