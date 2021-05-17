package com.qjx.no_2.lookup_method;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by qincasin on 2021/5/16.
 */
public class Main {
	public static void main(String[] args) {
		ApplicationContext bf = new ClassPathXmlApplicationContext("lookupTest.xml");
		GeBeanTest bean = (GeBeanTest) bf.getBean("getBeanTest");
		bean.showMe();
	}
}
