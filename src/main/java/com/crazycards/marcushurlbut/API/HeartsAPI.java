package com.crazycards.marcushurlbut.API;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.crazycards.marcushurlbut.Hearts;

@CrossOrigin(origins = "http://localhost:8081")

@RestController
public class HeartsAPI {
    
    @GetMapping("/printHello")
    public String printHello() {
        System.out.println("[!] it executed");
        return Hearts.printHello();
    }
}
