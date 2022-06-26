package com.qjx.no_6;

import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.support.DefaultPointcutAdvisor;

public class Main {
	public static void main(String[] args) {
		Cat cat = new Cat();
		//2.创建spring 代理工厂对象 proxyFactory
		//ProxyFactory  是 config + Factory 的存在，持有AOP操作所有的生产资料
		ProxyFactory proxyFactory = new ProxyFactory(cat);
		//3. 添加方法拦截器
		MyPointCut pointCut = new MyPointCut();
		proxyFactory.addAdvisor(new DefaultPointcutAdvisor(pointCut,new MethodInterceptor1()));
		proxyFactory.addAdvisor(new DefaultPointcutAdvisor(pointCut,new MethodInterceptor2()));
//		proxyFactory.addAdvice(new MethodInterceptor1());
//		proxyFactory.addAdvice(new MethodInterceptor2());

		//4. 获取代理对象
		Animal proxy = (Animal)proxyFactory.getProxy();
		proxy.eat();
	}
}
