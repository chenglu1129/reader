package com.htmake.reader;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 阅读器应用程序主类
 * 
 * @author reader
 * @version 3.0.0
 */
@SpringBootApplication
@EnableAsync
@EnableScheduling
public class ReaderApplication {

    public static void main(String[] args) {
        SpringApplication.run(ReaderApplication.class, args);
    }

}
