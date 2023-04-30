package ru.kata.spring.boot_security.demo.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.Util.UserValidator;
import ru.kata.spring.boot_security.demo.models.User;
import ru.kata.spring.boot_security.demo.service.UserServiceImpl;



import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;

import java.util.Objects;

@RestController
public class UserResources {

    private final UserServiceImpl userService;
    private final UserValidator userValidator;



    @Autowired
    public UserResources(UserServiceImpl userService, UserValidator userValidator) {
        this.userService = userService;
        this.userValidator = userValidator;
    }

    @InitBinder("user")
    protected void initBinder(WebDataBinder binder) {
        binder.addValidators(userValidator);
    }

    @GetMapping("/user/{userId}")
    public User findById(@PathVariable long userId) {
        return userService.getUserById(userId);
    }


    @PostMapping("/user/edit")
    public ResponseEntity<?> updateEdit(@Validated @RequestBody User user, BindingResult result) {

        if (result.hasFieldErrors("name")) {
            FieldError nameError = result.getFieldError("name");
            if (nameError != null && Objects.equals(nameError.getCode(), "User.duplicate.edit")) {
                return ResponseEntity.badRequest().body(nameError.getDefaultMessage());
            }
        }


        boolean success = userService.update(user);
        if (success) {
            return ResponseEntity.ok(true);
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(true);
        }
    }



    @DeleteMapping("/user/delete/{userId}")
    public void delete(@PathVariable long userId) {
       userService.removeUserById(userId);
    }

}
