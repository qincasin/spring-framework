package com.qjx.no_2.beanfactory;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.ClassPathResource;

/**
 * Created by qincasin on 2021/5/16.
 */
public class BeanFactoryTest {
	public static void main(String[] args) {
		BeanFactory beanFactory = new XmlBeanFactory(new ClassPathResource("spring-bf.xml"));
		Object a = beanFactory.getBean("componentA");
		Object b = beanFactory.getBean("componetB");
		System.out.println(a);
		System.out.println(b);
	}
}
