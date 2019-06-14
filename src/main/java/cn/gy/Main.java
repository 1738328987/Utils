package cn.gy;

import com.github.dadiyang.httpinvoker.annotation.HttpApiScan;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@HttpApiScan(value = "cn.gy.utils")
@MapperScan("cn.gy.dao")
public class Main {
    public static void main(String args[]){
        SpringApplication.run(Main.class);
    }

}
