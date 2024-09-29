package ru.practicum.explore;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"ru.practicum.explore", "client"})
public class ExploreServer {
    public static void main(String[] args) {
        SpringApplication.run(ExploreServer.class, args);
    }
}
