package org.isf;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;

@ImportResource({"classpath*:/applicationContext.xml"})
@SpringBootApplication
public class OpenHospitalApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(OpenHospitalApiApplication.class, args);
    }

}
