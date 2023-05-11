package org.restrata;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"org.restrata"})
public class FileReaderApplication {
    public static void main(String[] args) {
        SpringApplication.run(FileReaderApplication.class, args);
    }
}
