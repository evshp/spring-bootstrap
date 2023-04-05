package ru.kata.spring.boot_security.demo.service;




import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
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

//    private BCryptPasswordEncoder bCryptPasswordEncoder;


    @Autowired
    public MyUserDetailsService(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
//        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }


    @Transactional
    @Override
    public void saveUser(User user) throws RoleNotFoundException {
        Optional<User> userFromDB = userRepository.findByName(user.getName());

        if (userFromDB.isPresent()) {
            throw new UsernameNotFoundException("Пользователь не найден");
        }

        Role userRole = roleRepository.findByName("ROLE_USER");

        if (userRole == null) {
            throw new RoleNotFoundException("ROLE_USER  не найдена");
        }
//
//        Set<Role> roles = new HashSet<>();
//        roles.add(userRole);
//        user.setRoles(roles);
//        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
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
    public void cleanUsersTable() {
        userRepository.deleteAll();
    }


    @Transactional
    @Override
    public void update(long id, User user) {
        User userFromDB = getUserById(id);
        userFromDB.setName(user.getName());
        userFromDB.setLastname(user.getLastname());
        userFromDB.setEmail(user.getEmail());
        userRepository.save(userFromDB);
    }


    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user =userRepository.findByName(username).orElseThrow(()->
                new UsernameNotFoundException("User doesn't exist"));
        System.out.println(user);
        return  new UserPrincipal(user);
    }



}
