package service;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import service.controllers.ApplicationController;

@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(service.Application.class, args);
    }

    @Bean
    CommandLineRunner init(ApplicationController controller) {
        return args -> {
            controller.registerService("http://localhost:8080/quotations");
            controller.registerService("http://localhost:8082/quotations");
            controller.registerService("http://localhost:8081/quotations");
        };
    }
}
