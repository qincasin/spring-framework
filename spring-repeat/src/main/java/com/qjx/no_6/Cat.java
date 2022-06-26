package com.qjx.no_6;

/**
 * 动态代理
 */
public class Cat implements Animal{
	@Override
	public void eat() {
		System.out.println("吃猫粮");
	}

	@Override
	public void go() {
		System.out.println("跑起来~~");
	}
}
