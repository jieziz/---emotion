package com.jiezi.emotion.sys.user.service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.jiezi.emotion.base.BaseCRUDService;
import com.jiezi.emotion.common.entity.PageParameter;
import com.jiezi.emotion.common.entity.PageResult;
import com.jiezi.emotion.sys.user.entity.Permission;
import com.jiezi.emotion.sys.user.entity.Resource;
import com.jiezi.emotion.sys.user.entity.Role;
import com.jiezi.emotion.sys.user.entity.RoleResourcePermission;


@Service
public interface PermissionService extends BaseCRUDService<Permission, Long> {
	/**
	 * 真正的权限字符串,user:*或者user:view
	 * 
	 * @param menuId menuid
	 * @param permissionIds 权限ids
	 * @return set
	 */
	public Set<String> getActualPermissionStr(Long resourceId,
			Set<Long> permissionIds);
	/**
	 * 分页查看权限
	 * @param page
	 * @param permission
	 * @return
	 */
	public PageResult<Permission> getFindPage(PageParameter page, Permission permission);	/**
	 * 
	 * 查询所有权限,同时传入资源id，对资源拥有的权限，做标记
	 * @param id
	 * @return
	 */
	public List<Permission> getResourceToPermission(Long id);
	
	/** 
	 *  给资源分配权限
	 * @param ids
	 * @param id
	 * @return
	 */
	public int addResourcePermission(String ids, Long id,String name);
	
	/**
	 * 分页查看权限资源
	 * @param page
	 * @param permission
	 * @return
	 */
	public  PageResult<RoleResourcePermission>  getFindPermissionPage(PageParameter page, RoleResourcePermission roleResourcePermission);
	
	/**
	 * 获取单个资源权限
	 * @param id
	 * @return
	 */
	public Optional<RoleResourcePermission>  getResourcePermission(Long id);
	
	/**
	 * 修改资源权限
	 * @param ids
	 * @param id
	 * @return
	 */
	public void updateResourcePermission(String ids, Long id);
	
	/**
	 * 删除资源权限
	 * @param id
	 */
	public void deleteResourcePermission(Long id);




}
