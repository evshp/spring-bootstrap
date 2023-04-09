package ru.kata.spring.boot_security.demo.controllers;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.Util.UserValidator;
import ru.kata.spring.boot_security.demo.models.User;
import ru.kata.spring.boot_security.demo.service.UserService;

import javax.validation.Valid;

@Controller
@PreAuthorize(value = "hasRole('ADMIN')")
public class EntityController {


    private final UserService userServiceJPA;
    private final UserValidator userValidator;



    @Autowired
    public EntityController(UserService userServiceJPA, UserValidator userValidator) {
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
        return "admin";
    }

    //Получить пользователя по id
    @GetMapping("userPage/{id}")
    public String show(@PathVariable("id") int id, Model model) {
        model.addAttribute("users", userServiceJPA.getAllUsers());
        model.addAttribute("user", userServiceJPA.getUserById(id));
        model.addAttribute("formUser", new User());
        return "admin";
    }

    //Создать нового пользователя
    @PostMapping("/postAction")
    public String create(@ModelAttribute("formUser") @Valid User user, BindingResult bindingResult) {

        userValidator.validate(user, bindingResult);

        if (bindingResult.hasErrors()) {
            return "admin";
        }

        try {
            System.out.println("Создаем пользоваетля: " + user);
            userServiceJPA.saveUser(user);
        } catch (Exception e) {
            System.out.println("Исключение: " + e.getMessage());
            e.printStackTrace();
        }
        return "redirect:/people";
    }

    //Контроллер для открытия формы для редактирования пользователя
    @GetMapping("/edit")
   public String openEditUserForm(Model model, @RequestParam("id") long id) {
        model.addAttribute("user", userServiceJPA.getUserById(id));
        return "/edit";
    }

    //Метод для обновления данных пользователя
    @PatchMapping("/users/{id}")
    public String update(@ModelAttribute("user") @Valid User user,
                         BindingResult bindingResult) {

        userValidator.validate(user, bindingResult);
        if (bindingResult.hasFieldErrors("name")
                || bindingResult.hasFieldErrors("lastname")
                || bindingResult.hasFieldErrors("email")) {

            return "/edit";
        }
        String name = user.getName();
        String lastName = user.getLastname();
        String email = user.getEmail();
        userServiceJPA.update(user.getId(), name, lastName, email);
        return "redirect:/people";

    }

    //Метод для удаления пользователя
    @DeleteMapping("/delete")
    public String delete(@ModelAttribute("user") User user) {
        userServiceJPA.removeUserById(user.getId());
        System.out.println("Удален пользователь с id: " + user.getId());
        return "redirect:/people";
    }

}
