package com.jiezi.emotion.sys.user.service;

import java.util.Optional;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.jiezi.emotion.base.BaseCRUDService;
import com.jiezi.emotion.common.entity.PageParameter;
import com.jiezi.emotion.common.entity.PageResult;
import com.jiezi.emotion.sys.user.entity.User;

@Service
public interface UserService  extends BaseCRUDService<User, Long>{
	
	/**
	 * 登录
	 * @param username
	 * @param password
	 * @return
	 */
	Optional<User> login(String username, String password);
	/**
	 * 通过用户名查询用户
	 * @param username
	 * @return
	 */
	Optional<User> getUser(String username);
	/**
	 * 查询用户拥有的权限
	 */
	Set<String> findStringPermissions(Optional<User> user);
	
	/**
	 * 分页查询 用户信息
	 * @param page
	 * @param user
	 * @return
	 */
	public PageResult<User> getPageUser(PageParameter page, User user);

}
