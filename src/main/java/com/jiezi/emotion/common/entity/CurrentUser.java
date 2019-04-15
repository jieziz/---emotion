package com.jiezi.emotion.common.entity;

import java.util.List;
import java.util.Set;

import com.alibaba.fastjson.annotation.JSONField;
import com.jiezi.emotion.sys.user.entity.Resource;

import lombok.Data;

/**
 * 当前登录用户信息
 * 
 * @author 
 *
 */
@Data
public class CurrentUser {

	/**
	 * 登录用户名
	 */
	private String userName;
	/**
	 * 地址
	 */
	private String url;
	
	/**
	 * token
	 */
	private String token;
	
	/**
	 * 是否是管理员
	 */
	private Boolean isAdmin = true;
	
	/**
	 * 登录用户角色集合
	 */
	@JSONField(serialize = false)
	private Set<String> role;

	/**
	 * 用户权限集合(可访问的资源集合)
	 */
	@JSONField(serialize = false)
	private List<Resource> permissions;


	
}
