package org.yenln8.ChatApp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.TimeZone;


@SpringBootApplication
@EnableScheduling
public class StrongStrongApplication {
    private static final Logger logger = LoggerFactory.getLogger(StrongStrongApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(StrongStrongApplication.class, args);
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));
    }
}
