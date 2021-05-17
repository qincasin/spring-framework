package com.qjx;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by qincasin on 2021/5/7.
 */
public class Main {
	public static void main(String[] args) {
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:spring-test.xml");
		UserService userService = (UserService) applicationContext.getBean("userService");
		User user = userService.getUserById(1);
		System.out.println(user);

		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(Main.class);
		Object a = context.getBean("A");
	}
}
