package com.tenx.fraudamlmanager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * This is the class that start the all application.
 *
 * @author Massimo Della Rovere
 */
@SpringBootApplication
@EnableFeignClients
public class FraudAmlManagerApplication {
    /**
     * This is the entry point of the application.
     * The main method allow this project to be started as a java application.
     *
     * @param args the argument passed during the startup of the application.
     */
    public static void main(String[] args) {
        SpringApplication.run(FraudAmlManagerApplication.class, args);
    }

}
