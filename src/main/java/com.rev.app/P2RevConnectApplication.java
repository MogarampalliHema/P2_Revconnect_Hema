package com.rev.app;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class P2RevConnectApplication {

    private static final Logger logger = LogManager.getLogger(P2RevConnectApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(P2RevConnectApplication.class, args);
        logger.info("RevConnect P2 Application Started Successfully!");
    }
}
