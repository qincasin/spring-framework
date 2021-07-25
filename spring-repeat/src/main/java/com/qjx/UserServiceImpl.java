package com.qjx;

import org.springframework.stereotype.Service;

/**
 * Created by qincasin on 2021/5/7.
 */
@Service
public class UserServiceImpl implements UserService{
	@Override
	public User getUserById(Integer id) {
		User user = new User();
		user.setAge(17);;
		user.setName("qjx");
		return user;
	}
}
