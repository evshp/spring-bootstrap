package ru.kata.spring.boot_security.demo.Util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import ru.kata.spring.boot_security.demo.models.User;
import ru.kata.spring.boot_security.demo.service.UserService;

import java.util.Objects;
import java.util.Optional;

@Component
public class UserValidator implements Validator {

    private final UserService userServiceJPA;
    private static final Logger logger = LoggerFactory.getLogger(UserValidator.class);

    @Autowired
    public UserValidator(UserService userServiceJPA) {
        this.userServiceJPA = userServiceJPA;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return User.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        User user = (User) target;
        String tempName = user.getName();
        String tempEmail = user.getEmail();

        if (user.getId() == null) {
            // Проверяем, если имя пользователя уже есть в базе данных, добавляем ошибку
            try {
                Optional<User> userByName = userServiceJPA.getUserByName(tempName);
                 if (userByName.isPresent()) {
                    errors.rejectValue("name", "", "Пользователь с таким именем уже существует");
                }

            } catch (Exception e) {
                errors.rejectValue("name", "Произошла ошибка при проверке имени пользователя");
                logger.error("Ошибка при проверке имени пользователя: " + e.getMessage());
            }

            try {
                if (userServiceJPA.getUserByEmail(tempEmail).isPresent()) {
                    errors.rejectValue("email", "", "Пользователь с таким email уже существует");
                }
            } catch (Exception e) {
                errors.rejectValue("email", "Произошла ошибка при проверке email пользователя");
                logger.error("Ошибка при проверке email пользователя: " + e.getMessage());
            }

        } else {

            Optional<User> existingUserName = userServiceJPA.getUserByName(tempName);
            if (existingUserName.isPresent()) {
                if (!Objects.equals(user.getId(), existingUserName.get().getId())) {
                    errors.rejectValue("name", "", "Пользователь с таким именем существует");
                }
            }

            Optional<User> existingUserEmail = userServiceJPA.getUserByEmail(tempEmail);
            if (existingUserEmail.isPresent()) {
                if (!Objects.equals(user.getId(), existingUserEmail.get().getId())) {
                    errors.rejectValue("email", "", "Пользователь с таким email существует");
                }
            }



        }
    }
}
