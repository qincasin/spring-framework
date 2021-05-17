package com.qjx.no_2.lookup_method;

/**
 * Created by qincasin on 2021/5/16.
 */
public abstract class GeBeanTest {
	public void showMe(){
		this.getBean().showMe();
	}
	public abstract User getBean();
}
