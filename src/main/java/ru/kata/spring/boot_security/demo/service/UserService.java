package ru.kata.spring.boot_security.demo.service;




import ru.kata.spring.boot_security.demo.models.User;

import javax.management.relation.RoleNotFoundException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface UserService {

    void saveUser(User user) throws RoleNotFoundException;

    void removeUserById(long id);

    User getUserById(long id);

    List<User> getAllUsers();

    void cleanUsersTable();
    void update(long id, User user);

    Optional<User> getUserByEmail(String email);
}
