package com.sandbox;

import de.codecentric.boot.admin.server.config.EnableAdminServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
@EnableAdminServer
@SpringBootApplication
public class SimpleAdminApp {

    public static void main(String[] args) {
        SpringApplication.run(SimpleAdminApp.class, args);
    }

}