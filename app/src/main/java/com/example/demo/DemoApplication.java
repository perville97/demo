package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication  // ← On enlève @RestController
public class DemoApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
        System.out.println("Hello World!"); // S'affichera dans la console
    }
    
    // On supprime les méthodes home() et test() pour éviter le conflit
}