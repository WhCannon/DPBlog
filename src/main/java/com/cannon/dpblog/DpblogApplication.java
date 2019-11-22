package com.cannon.dpblog;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;

@SpringBootApplication
public class DpblogApplication {

    public static void main(String[] args) {
        SpringApplication.run(DpblogApplication.class, args);
    }

    @PostConstruct
    public void init(){
         //解决es的netty启动冲突
        System.setProperty("es.set.netty.runtime.available.processors", "false");
    }

}
