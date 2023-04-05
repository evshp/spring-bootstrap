package ru.kata.spring.boot_security.demo.models;


import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
import javax.persistence.*;
import javax.validation.constraints.*;
import java.time.LocalDate;
import java.time.Period;
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
    private long id;
    @NotBlank(message = "Поле не должно быть пустым")
    @Size(min = 2, max = 30, message = "Имя должно быть от 2 до 30 символов")
    @Column(name = "name")
    private String name;

    @NotBlank(message = "Поле не должно быть пустым")
    @Size(min = 2, max = 30, message = "Фамилия должна быть от 2 до 30 символов")
    @Column(name = "lastName")
    private String lastname;


    @NotNull(message = "Поле не должно быть пустым")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name = "dateOfBirth")
    private LocalDate dateOfBirth;

    @Transient
    private byte age;

    @NotBlank(message = "Поле не должно быть пустым")
    @Column(name = "email")
    private String email;


    @Column(name = "password")
    @Min(8)
    private String password;


    @ManyToMany(fetch = FetchType.EAGER)
    private Set<Role> roles;

    public User(String name, String lastName, LocalDate dateOfBirth, String email, String password) {
        this.name = name;
        this.lastname = lastName;
        this.dateOfBirth = dateOfBirth;
        this.email = email;
        this.password = password;
    }


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
                ", age=" + getAge() +
                ", email='" + email + '\'' +
                '}';
    }
}

