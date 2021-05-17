package com.qjx;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.config.BeanDefinition;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by qincasin on 2021/5/16.
 */
public class FuckArTest {

	// #
//	public static final String GENERATED_BEAN_NAME_SEPARATOR = BeanFactoryUtils.GENERATED_BEAN_NAME_SEPARATOR;

	/**
	 * 生成一个自增的默认className 名字 mode#0
	 */
	public void generateBeanName() {
		String id = "demo";
		Map<String, String> beanDefinitionMap = new ConcurrentHashMap<>(256);
		beanDefinitionMap.put("demo#1", "11");
		int count = -1;
		String prefix = id + "#";
		while (count == -1 || beanDefinitionMap.containsKey(prefix)) {
			count++;
			id = prefix + count;
		}
		System.out.println(id);


	}

	public static void main(String[] args) {
		FuckArTest test = new FuckArTest();
		test.generateBeanName();
		test.generateBeanName();
	}
}
