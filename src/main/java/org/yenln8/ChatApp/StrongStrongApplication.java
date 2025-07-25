package org.yenln8.ChatApp;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.TimeZone;


@SpringBootApplication
@EnableScheduling
@Slf4j
public class StrongStrongApplication {
    private static final Logger logger = LoggerFactory.getLogger(StrongStrongApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(StrongStrongApplication.class, args);
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));
        log.info("PROJECT BACKEND IS RUNNING IN PORT 8080 <3");
        log.info("ACCESS http://localhost:8080/swagger-ui/index.html TO SEE SWAGGER");

    }
}
