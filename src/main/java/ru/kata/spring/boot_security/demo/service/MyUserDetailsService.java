package ru.kata.spring.boot_security.demo.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.boot_security.demo.models.Role;
import ru.kata.spring.boot_security.demo.models.User;
import ru.kata.spring.boot_security.demo.repository.RoleRepository;
import ru.kata.spring.boot_security.demo.repository.UserRepository;
import ru.kata.spring.boot_security.demo.security.UserPrincipal;

import javax.management.relation.RoleNotFoundException;
import java.util.*;


@Service("MyUserDetailsService")
@Transactional(readOnly = true)
public class MyUserDetailsService implements UserService, UserDetailsService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder(12);


    @Autowired
    public MyUserDetailsService(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;

    }


    @Transactional
    @Override
    public void saveUser(User user) throws RoleNotFoundException {
        Role userRole = roleRepository.findByName("ROLE_USER");
        if (userRole == null) {
            throw new RoleNotFoundException("ROLE_USER  не найдена");
        }

        if (userRepository.findByName(user.getName()).isPresent()) {
            throw new UsernameNotFoundException("Пользователь с таким именем уже существует");
        }

        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new UsernameNotFoundException("Пользователь с таким email уже существует");
        }

        Set<Role> roles = new HashSet<>();
        roles.add(userRole);
        user.setRoles(roles);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        System.out.println("Pre saved" + user);
        userRepository.save(user);
        System.out.println("User saved" + user);
    }

    @Transactional
    @Override
    public void removeUserById(long id) {
        userRepository.deleteById(id);
    }


    @Override
    public User getUserById(long id) {
        Optional<User> foundUser = userRepository.findById(id);
        return foundUser.orElse(null);
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }


    @Transactional
    @Override
    public void update(long id, String name, String lastName, String email) {
        User userFromDB = userRepository.findById(id).orElseThrow(() ->
                new UsernameNotFoundException("User doesn't exist"));

        userFromDB.setName(name);
        userFromDB.setLastname(lastName);
        userFromDB.setEmail(email);
        userRepository.save(userFromDB);
    }


    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }



    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByName(username).orElseThrow(() ->
                new UsernameNotFoundException("User doesn't exist"));
        System.out.println(user);
        return new UserPrincipal(user);
    }

    public Optional<User> getUserByName(String name) {
        return userRepository.findByName(name);
    }


}
