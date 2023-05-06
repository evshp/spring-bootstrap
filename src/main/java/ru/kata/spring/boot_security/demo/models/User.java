package ru.kata.spring.boot_security.demo.models;


import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.time.LocalDate;
import java.time.Period;
import java.util.Arrays;
import java.util.Set;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {

//Fields

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank(message = "Поле не должно быть пустым")
    @Size(min = 2, max = 30, message = "Имя должно быть от 2 до 30 символов")
    @Column(name = "name")
    private String name;

    @NotBlank(message = "Поле не должно быть пустым")
    @Size(min = 2, max = 30, message = "Фамилия должна быть от 2 до 30 символов")
    @Column(name = "last_name")
    private String lastname;


    @NotNull(message = "Поле не должно быть пустым")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name = "date_Of_Birth")
    private LocalDate dateOfBirth;

    @Transient
    private byte age;

    @NotBlank(message = "Поле не должно быть пустым")
    @Column(name = "email")
    private String email;


    @Column(name = "password")
    @Size(min = 8, message = "Пароль должен быть не менее 8 символов")
    private String password;


    @ManyToMany(fetch = FetchType.LAZY)
    private Set<Role> roles;


    //Getters and Setters
    public byte getAge() {
        return (byte) Period.between(dateOfBirth, LocalDate.now()).getYears();
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", lastname='" + lastname + '\'' +
                ", dateOfBirth=" + dateOfBirth +
                ", age=" + age +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", roles=" + roles +
                '}';
    }


    //Methods
    public boolean isAdmin() {
        return roles.stream().anyMatch(role -> role.getName().equals("ROLE_ADMIN"));
    }

    public boolean isUser() {
        return roles.stream().anyMatch(role -> role.getName().equals("ROLE_USER"));
    }


}

