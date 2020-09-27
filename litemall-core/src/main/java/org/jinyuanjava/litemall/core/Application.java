package org.jinyuanjava.litemall.core;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"org.jinyuanjava.litemall.db", "org.jinyuanjava.litemall.core"})
@MapperScan("org.jinyuanjava.litemall.db.dao")
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
