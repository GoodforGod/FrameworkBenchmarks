package io.techempower.benchmark.spring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class Application {
    public static void main(String[] args) {
        System.out.println("AVAILABLE CORES: " + Runtime.getRuntime().availableProcessors());
        SpringApplication.run(Application.class, args);
    }
}
