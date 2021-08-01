package com.qjx.no_2.beanfactory;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.xml.*;
import org.springframework.core.io.ClassPathResource;

/**
 * Created by qincasin on 2021/5/16.
 */
@SuppressWarnings("deprecation")
public class BeanFactoryTest {
	public static void main(String[] args) {

		BeanFactory beanFactory = new XmlBeanFactory(new ClassPathResource("spring-bf.xml"));
		Object a = beanFactory.getBean("componentA");
		Object b = beanFactory.getBean("componentB");
		System.out.println(a);
		System.out.println(b);
	}
}
