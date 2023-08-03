package me.kimihiqq.springtil;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class SpringTilApplication {
    public static void main(String[] args) {

        SpringApplication.run(SpringTilApplication.class, args);
    }

}
