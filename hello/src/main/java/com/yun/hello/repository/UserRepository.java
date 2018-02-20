package com.yun.hello.repository;

import java.util.List;

import com.yun.hello.domain.User;

public interface UserRepository {
	/**
	 * 保存或者更新用户
	 * @param user
	 * @return
	 */
	User saveOrUpdateUser(User user);
	
	/**
	 * 
	 * @param 删除用户
	 */
	void deleteUser(long id);
	
	/**
	 * 根据id查询用户
	 * @param id
	 * @return
	 */
	User getUserById(long id);
	
	/**
	 * 获取用户列表
	 * @return
	 */
	List<User> listUser();
}
