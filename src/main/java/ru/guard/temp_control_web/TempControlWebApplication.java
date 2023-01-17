package ru.guard.temp_control_web;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@RequiredArgsConstructor
public class TempControlWebApplication {

    public static void main(String[] args) {
        SpringApplication.run(TempControlWebApplication.class, args);
    }

}
