package com.qjx.tx;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

public class TransactionTest {
	@Transactional(propagation = Propagation.SUPPORTS)
	public void test(){

	}
}
