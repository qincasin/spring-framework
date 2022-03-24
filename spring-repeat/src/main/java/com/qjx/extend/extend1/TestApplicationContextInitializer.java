package com.qjx.extend.extend1;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.web.context.ConfigurableWebApplicationContext;

public class TestApplicationContextInitializer implements ApplicationContextInitializer<ConfigurableWebApplicationContext> {
	@Override
	public void initialize(ConfigurableWebApplicationContext applicationContext) {
		System.out.println("1. TestApplicationContextInitializer ~~~~~~~");
	}
}
