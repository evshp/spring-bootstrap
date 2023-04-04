package ru.kata.spring.boot_security.demo.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.Util.UserValidator;
import ru.kata.spring.boot_security.demo.models.User;
import ru.kata.spring.boot_security.demo.security.UserDet;
import ru.kata.spring.boot_security.demo.service.UserService;

import javax.naming.AuthenticationException;
import javax.validation.Valid;

@Controller
public class UsersController {


    private UserService userServiceJPA;
    private UserValidator userValidator;

    @Autowired
    public UsersController(UserService userServiceJPA, UserValidator userValidator) {
        this.userServiceJPA = userServiceJPA;
        this.userValidator = userValidator;
    }

    //Показать всех пользователей
    @GetMapping("/people")
    public String index(Model model) {
        try {
            model.addAttribute("users", userServiceJPA.getAllUsers());
        } catch (Exception e) {
            System.out.println("Ошибка: " + e);
        } finally {
            model.addAttribute("formUser", new User());
        }
        return "users/userPage";
    }

    //Получить пользователя по id
    @GetMapping("userPage/{id}")
    public String show(@PathVariable("id") int id, Model model) {
        model.addAttribute("users", userServiceJPA.getAllUsers());
        model.addAttribute("user", userServiceJPA.getUserById(id));
        model.addAttribute("formUser", new User());
        return "users/userPage";
    }

    //Создать нового пользователя
    @PostMapping("/postAction")
    public String create(@ModelAttribute("formUser") @Valid User user, BindingResult bindingResult) {
        userValidator.validate(user, bindingResult);
        if (bindingResult.hasErrors()) {
            return "users/userPage";
        }
        try {
            userServiceJPA.saveUser(user.getName(),
                    user.getLastname(),
                    user.getDateOfBirth(),
                    user.getEmail(),
            user.getPassword());
        } catch (Exception e) {
            System.out.println("Исключение: " + e.getMessage());
            return "error";
        }
        return "redirect:/people";
    }

    //Контроллер для открытия формы для редактирования пользователя
    @GetMapping("/edit")
    public String openEditUserForm (Model model, @RequestParam("id") long id) {
        model.addAttribute("user", userServiceJPA.getUserById(id));
        return "users/edit";
    }

    //Метод для обновления данных пользователя
    @PatchMapping("/users/{id}")
    public String update(@ModelAttribute("user") @Valid User user,
                         BindingResult bindingResult) {
        userValidator.validate(user, bindingResult);
        if (bindingResult.hasErrors()) {
            return "users/edit";
        }
        userServiceJPA.update(user.getId(), user);
        return "redirect:/people";
    }


    //Метод для удаления пользователя
    @DeleteMapping("/delete")
    public String delete(@ModelAttribute("user") User user) {
        userServiceJPA.removeUserById(user.getId());
        System.out.println("Удален пользователь с id: " + user.getId());
        return "redirect:/people";
    }


    @GetMapping("/showUserInfo")
    public String showUserInfo() {
        Authentication authentication = SecurityContextHolder
                .getContext()
                .getAuthentication();
        UserDet userDetails = (UserDet) authentication.getPrincipal();
        System.out.println(userDetails.getUserDetails());
        return "firstPage";
    }


}
