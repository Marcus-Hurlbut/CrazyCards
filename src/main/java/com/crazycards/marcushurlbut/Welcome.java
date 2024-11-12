package com.crazycards.marcushurlbut;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Welcome {

    @GetMapping("/hello")
    public String sayHello() {
        return "Hello, Spring Boot!";
    }

    @GetMapping("/")
    public String sayWelcome() {
        return "Welcome to the shitshow that is beginning";
    }
}
