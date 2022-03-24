package com.qjx.extend;

import com.qjx.extend.config.AppConfig;
import com.qjx.extend.service.HelloService;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * 测试spring 的许多个扩展点
 */
public class Main {
	public static void main(String[] args) {
		AnnotationConfigApplicationContext ac = new AnnotationConfigApplicationContext(AppConfig.class);
		HelloService bean = ac.getBean(HelloService.class);
		bean.hello();
	}
}
