package com.jiezi.emotion.sys.user.service;



import java.util.List;

import org.springframework.stereotype.Service;

import com.jiezi.emotion.base.BaseCRUDService;
import com.jiezi.emotion.common.entity.PageParameter;
import com.jiezi.emotion.common.entity.PageResult;
import com.jiezi.emotion.sys.user.entity.Resource;
import com.jiezi.emotion.sys.user.entity.RoleResourcePermission;

@Service
public interface ResourceService extends BaseCRUDService<Resource,Long>{

	/**
	 * 通过用户名查找
	 * @param username
	 * @return list
	 */
	public List<Resource> findHasPermissionMenusByUsername(String username);
	
	
	/**
	 * 通过用户名查找 -new 加入权限及时间判断
	 * @param username
	 * @return list
	 */
	public List<Resource> findHasPermissionMenusByUsernameNew(String username);
	
	
	/**
	 * 分页查看资源
	 * @param page
	 * @param mood
	 * @return
	 */
	public PageResult<Resource> getPageResource(PageParameter page, Resource param);

	/**
	 * 查询所有资源权限,同时传入角色id，对角色已拥有的资源权限，做标记
	 * @param id
	 * @return
	 */
	public List<RoleResourcePermission> getRoleToResource(Long id);
}
