package ru.kata.spring.boot_security.demo.Util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import ru.kata.spring.boot_security.demo.models.User;
import ru.kata.spring.boot_security.demo.repository.RoleRepository;
import ru.kata.spring.boot_security.demo.service.UserService;

import java.time.LocalDate;
import java.util.Set;


@Component
public class TestUserLoader implements ApplicationRunner {

    private final UserService userService;

    private final RoleRepository roleRepository;

    @Autowired
    public TestUserLoader(UserService userService, RoleRepository roleRepository) {
        this.userService = userService;
        this.roleRepository = roleRepository;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {

        if (userService.getUserByName("admin").isEmpty()) {
            User admin = new User();
            admin.setName("admin");
            admin.setLastname("admin");
            admin.setEmail("admin@mail.ru");
            admin.setPassword("admin");//admin
            admin.setDateOfBirth(LocalDate.of(1990, 1, 1));
            admin.setRoles(Set.of(roleRepository.findByName("ROLE_ADMIN")));
            userService.saveUser(admin);
        }

        if (userService.getUserByName("user").isEmpty()) {
            User user = new User();
            user.setName("user");
            user.setLastname("user");
            user.setEmail("user@mail.ru");
            user.setPassword("user");//user
            user.setDateOfBirth(LocalDate.of(1990, 1, 1));
            user.setRoles(Set.of(roleRepository.findByName("ROLE_USER")));
            userService.saveUser(user);
        }
    }
}
