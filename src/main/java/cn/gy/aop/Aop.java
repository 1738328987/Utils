package cn.gy.aop;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.context.annotation.Configuration;

@Aspect
@Configuration
public class Aop {

    @Pointcut(value = "execution(public void get())")
    public void login(){
        System.out.println("进入切点了");
    }

    @Before("login()")
    public void syo(){
        System.out.println("执行之前的方法");
    }
}
