package com.chsbk.mesh;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication(scanBasePackages = "com.chsbk.mesh")
public class MeshApplication {

    public static void main(String[] arguments) {
        SpringApplication.run(MeshApplication.class, arguments);
    }
}
