package com.jiezi.emotion.sys.user.service;

import org.springframework.stereotype.Service;

import com.jiezi.emotion.base.BaseCRUDService;
import com.jiezi.emotion.common.entity.PageParameter;
import com.jiezi.emotion.common.entity.PageResult;
import com.jiezi.emotion.sys.user.entity.Role;


@Service
public interface RoleService extends BaseCRUDService<Role, Long> {

	/**
	 * 分页查询
	 * @param page 分页参数
	 * @param role 角色
	 * @return pageresult
	 */
	public PageResult<Role> getPage(PageParameter page, Role role);

	/**
	 * 通过角色名查询
	 * @param name 角色名
	 * @return role
	 */
	public Role findByName(String name);
	
	/**
	 * 给用户分配角色
	 */
	public int addUserRole(String ids,Long id);
	
	/**
	 * 给角色分配资源
	 * @param id
	 * @param ids
	 */
	public int addRoleResource(Long id, String ids);
}
