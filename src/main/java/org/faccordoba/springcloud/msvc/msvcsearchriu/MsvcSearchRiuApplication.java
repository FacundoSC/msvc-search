package org.faccordoba.springcloud.msvc.msvcsearchriu;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class MsvcSearchRiuApplication {

    public static void main(String[] args) {
        SpringApplication.run(MsvcSearchRiuApplication.class, args);
    }

}
