package com.jiezi.emotion.sys.user.dao;

import com.jiezi.emotion.base.BaseRepository;
import com.jiezi.emotion.sys.user.entity.Role;

public interface RoleDao extends BaseRepository<Role, Long> {

	/**
	 * 通过角色中文名查询
	 * @param name 中文名称
	 * @return role
	 */
	public Role findByName(String name);

	/**
	 * 通过角色英文名查询
	 * @param name 英文名
	 * @return role
	 */
	public Role findByRoleName(String name);
}
