package ru.kata.spring.boot_security.demo.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.Util.UserValidator;
import ru.kata.spring.boot_security.demo.models.User;
import ru.kata.spring.boot_security.demo.service.UserService;

import javax.validation.Valid;

@Controller
public class AdminController {


    private final UserService userServiceJPA;
    private final UserValidator userValidator;


    @Autowired
    public AdminController(UserService userServiceJPA, UserValidator userValidator) {
        this.userServiceJPA = userServiceJPA;
        this.userValidator = userValidator;
    }


    @GetMapping("/people")
    public String index(Model model) {
        String name = userServiceJPA.getCurrentUserName();
        String roles = userServiceJPA.getCurrentUserRoles();
        model.addAttribute("name", name);
        model.addAttribute("roles", roles);
        model.addAttribute("activeTab", "tab1");
        try {
            model.addAttribute("users", userServiceJPA.getAllUsers());
        } catch (Exception e) {
            System.out.println("Ошибка: " + e);
        } finally {
            model.addAttribute("formUser", new User());
        }
        System.out.println("Вывод страницы МАЙН");
        return "admin";
    }


    //Создать нового пользователя
    @PostMapping("/postAction")
    public String create(@ModelAttribute("formUser") @Valid User user,
                         BindingResult bindingResult, Model model) {

        userValidator.validate(user, bindingResult);

        if (bindingResult.hasErrors()) {
            System.out.println("Ошибка валидации");
            String name = userServiceJPA.getCurrentUserName();
            String roles = userServiceJPA.getCurrentUserRoles();
            model.addAttribute("name", name);
            model.addAttribute("roles", roles);
            model.addAttribute ("activeTab", "tab2");
            System.out.println("Возвращаем страницу TestPage");
            return "admin";
        }
        try {
            userServiceJPA.saveUser(user);
        } catch (Exception e) {
            System.out.println("Исключение: " + e.getMessage());
            e.printStackTrace();
        }
        return "redirect:/people";
    }








}
