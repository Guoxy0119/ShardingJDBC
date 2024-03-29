package com.atguigu.shardingjdbcdemo;

import com.atguigu.shardingjdbcdemo.config.CustNameGenaritor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.transaction.jta.JtaAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import tk.mybatis.spring.annotation.MapperScan;

@SpringBootApplication(exclude = JtaAutoConfiguration.class)
@ComponentScan(nameGenerator = CustNameGenaritor.class)
@MapperScan("com.atguigu.shardingjdbcdemo.mapper")
public class Application {

    public static void main(String[] args) {
        try {
            SpringApplication.run(Application.class, args);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
