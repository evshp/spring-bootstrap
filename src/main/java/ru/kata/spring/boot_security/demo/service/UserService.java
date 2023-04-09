package ru.kata.spring.boot_security.demo.service;




import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.boot_security.demo.models.User;

import javax.management.relation.RoleNotFoundException;
import java.util.List;
import java.util.Optional;

public interface UserService {

    void saveUser(User user) throws RoleNotFoundException;

    void removeUserById(long id);

    User getUserById(long id);

    List<User> getAllUsers();

    void update(long id, String name, String lastName, String email);

    @Transactional
    Optional<User> getUserByEmail(String email);
    @Transactional
    Optional<User> getUserByName(String name);
}
