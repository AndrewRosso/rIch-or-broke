package ru.andrewrosso.richorbroke;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class RichOrBrokeApplication {

    public static void main(String[] args) {
        SpringApplication.run(RichOrBrokeApplication.class, args);
    }
}
