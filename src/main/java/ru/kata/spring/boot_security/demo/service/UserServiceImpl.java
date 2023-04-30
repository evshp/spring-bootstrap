package ru.kata.spring.boot_security.demo.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.boot_security.demo.models.Role;
import ru.kata.spring.boot_security.demo.models.User;
import ru.kata.spring.boot_security.demo.repository.RoleRepository;
import ru.kata.spring.boot_security.demo.repository.UserRepository;
import ru.kata.spring.boot_security.demo.security.UserPrincipal;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;


import java.util.*;


@Service("MyUserDetailsService")
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService, UserDetailsService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;


    @Value("${app.defaultRoleName}")
    private String defaultRoleName;


    private final BCryptPasswordEncoder passwordEncoder; //= new BCryptPasswordEncoder(12);


    @Autowired
    public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }


    @Transactional
    @Override
    public Boolean saveUser(User user) {
        if (userRepository.findByName(user.getName()).isPresent()) {
            throw new UsernameNotFoundException("Пользователь с таким именем уже существует");
        }

        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new UsernameNotFoundException("Пользователь с таким email уже существует");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        try {
            userRepository.save(user);
            return true;
        } catch (Exception e) {
            return false;
        }

    }

    @Transactional
    @Override
    public void removeUserById(long id) {
        userRepository.deleteById(id);
    }


    @Override
    public User getUserById(long id) {
        Optional<User> foundUser = userRepository.findById(id);
        foundUser.ifPresent(User::getRoles);
        return foundUser.orElse(null);
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }


    @Transactional
    @Override
    public boolean update(User user) {

        User userFromDB = userRepository.findById(user.getId()).orElseThrow(() ->
                new UsernameNotFoundException("User doesn't exist"));


        if (user.getRoles().isEmpty()) {
            user.setRoles(userFromDB.getRoles());
        }

        if (!user.getPassword().equals(userFromDB.getPassword())) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }

        try {
            userRepository.save(user);
            return true;
        } catch (Exception e) {
            System.out.println("Ошибка при сохранении пользователя");
            return false;
        }

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


    public String getCurrentUserName() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }

    public String getCurrentUserRoles() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getAuthorities().toString();
    }

}
