package ru.kata.spring.boot_security.demo.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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

import javax.validation.Valid;
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


    @PostMapping(value = "/postAction", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> userCreate(@Valid @RequestBody User user, BindingResult bindingResult) {
        if (bindingResult.hasFieldErrors()) {
            return userAlreadyExist(bindingResult);
        }
        userService.saveUser(user);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @PostMapping("/user/edit")
    public ResponseEntity<?> updateEdit(@Validated @RequestBody User user, BindingResult bindingResult) {

        if (bindingResult.hasFieldErrors()) {
           return userAlreadyExist(bindingResult);
        }

        boolean success = userService.update(user);
        System.out.println(success);
        return success ? ResponseEntity.ok(success) : ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Ошибка изменения данных");
    }

    @PostMapping("/user/create")
    public ResponseEntity<?> create(@Validated @RequestBody User user, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return userAlreadyExist(bindingResult);
        }

        boolean success = userService.saveUser(user);
        return success ? ResponseEntity.ok(success) : ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Ошибка создания пользователя");
    }


    private ResponseEntity<?> userAlreadyExist (BindingResult bindingResult) {

        if (bindingResult.hasFieldErrors("name") || bindingResult.hasFieldErrors("email")) {
            FieldError nameError = bindingResult.getFieldError("name");
            FieldError emailError = bindingResult.getFieldError("email");
            FieldError roleError = bindingResult.getFieldError("roles");

            if (roleError != null && Objects.equals(roleError.getCode(), "User.roles.empty")) {
                System.out.println(roleError.getDefaultMessage());
                return ResponseEntity.badRequest().body(roleError.getDefaultMessage());
            }

            if ((nameError != null && Objects.equals(nameError.getCode(), "User.duplicate.name")) ||
                    (emailError != null && Objects.equals(emailError.getCode(), "User.duplicate.email")))
            {
                return ResponseEntity.badRequest().body(nameError != null ? nameError.getDefaultMessage() : emailError.getDefaultMessage());
            }


        }

        return ResponseEntity.ok(HttpStatus.OK);

    }

    @DeleteMapping("/user/delete/{userId}")
    public void delete(@PathVariable long userId) {
       userService.removeUserById(userId);
    }





}
