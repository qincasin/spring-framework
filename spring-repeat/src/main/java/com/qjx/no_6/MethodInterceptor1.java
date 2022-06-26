package com.qjx.no_6;

import org.aopalliance.aop.Advice;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

public class MethodInterceptor1 implements MethodInterceptor {

	@Override
	public Object invoke(MethodInvocation invocation) throws Throwable {
		System.out.println("MethodInterceptor1 begin");
		Object proceed = invocation.proceed();
		System.out.println("MethodInterceptor1 end");
		return proceed;
	}
}
