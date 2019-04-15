package com.jiezi.emotion.sys.user.dao;

import java.util.Optional;

import com.jiezi.emotion.base.BaseRepository;
import com.jiezi.emotion.sys.user.entity.User;

public interface UserDao  extends BaseRepository<User, Long> {
	
	/**
	 * 根据用户名密码查找用户
	 * @param username
	 * @param password
	 * @return
	 */
	public Optional<User> findByUsernameAndPassword(String username, String password);

	public Optional<User> findByUsername(String username);

}
