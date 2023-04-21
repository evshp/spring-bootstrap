package ru.kata.spring.boot_security.demo.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
public class UserServiceImpl implements UserService, UserDetailsService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;




    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder(12);


    @Autowired
    public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;

    }


    @Transactional
    @Override
    public void saveUser(User user) throws RoleNotFoundException {
        Role userRole = roleRepository.findByName("ROLE_USER");

        Set<Role> roles = new HashSet<>();

        if (user.getRoles().isEmpty()) {
            roles.add(userRole);
            user.setRoles(roles);
        }

        if (userRole == null) {
            throw new RoleNotFoundException("ROLE_USER  не найдена");
        }


        if (userRepository.findByName(user.getName()).isPresent()) {
            throw new UsernameNotFoundException("Пользователь с таким именем уже существует");
        }

        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new UsernameNotFoundException("Пользователь с таким email уже существует");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);

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
        System.out.println(user.getRoles());
        return new UserPrincipal(user);
    }

    public Optional<User> getUserByName(String name) {
        return userRepository.findByName(name);
    }


    public boolean isUserExist(String name) {
        return userRepository.findByName(name).isPresent();
    }

    public String getCurrentUserName() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }

    public String getCurrentUserRoles() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getAuthorities().toString();
    }



}
