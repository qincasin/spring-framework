package com.qjx.no_6;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

public class MethodInterceptor2 implements MethodInterceptor {

	@Override
	public Object invoke(MethodInvocation invocation) throws Throwable {
		System.out.println("MethodInterceptor2 begin");
		Object proceed = invocation.proceed();
		System.out.println("MethodInterceptor2 end");
		return proceed;
	}
}
