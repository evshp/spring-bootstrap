package ru.kata.spring.boot_security.demo.Util;

public class UserAlreadyExistException extends RuntimeException {

    String name;
    String time;

    public UserAlreadyExistException(String message, String name, String time) {
        super(message);
        this.name = name;
        this.time = time;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
