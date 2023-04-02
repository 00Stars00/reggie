package org.example.reggie;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

@Slf4j
@SpringBootApplication
@ServletComponentScan
public class ReggieApplication {
    public static void main(String[] args) {
        org.springframework.boot.SpringApplication.run(ReggieApplication.class, args);
        log.info("项目启动成功！");
    }
}
