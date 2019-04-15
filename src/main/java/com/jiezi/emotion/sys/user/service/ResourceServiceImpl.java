package com.jiezi.emotion.sys.user.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.criteria.Predicate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.jiezi.emotion.base.BaseServiceImpl;
import com.jiezi.emotion.common.entity.CurrentUser;
import com.jiezi.emotion.common.entity.PageParameter;
import com.jiezi.emotion.common.entity.PageResult;
import com.jiezi.emotion.sys.user.dao.ResourceDao;
import com.jiezi.emotion.sys.user.dao.RoleDao;
import com.jiezi.emotion.sys.user.dao.RoleResourcePermissionDao;
import com.jiezi.emotion.sys.user.entity.Resource;
import com.jiezi.emotion.sys.user.entity.Role;
import com.jiezi.emotion.sys.user.entity.RoleResourcePermission;
import com.jiezi.emotion.sys.user.entity.User;

@Service
public class ResourceServiceImpl extends BaseServiceImpl<Resource, Long> implements ResourceService {

	@Autowired
	private ResourceDao resourceDao;
	@Autowired
	private RoleDao roleDao;
	@Autowired
	private UserService userService;
	@Autowired
	private RoleResourcePermissionDao roleResourcePermissionDao;

	/**
	 * 查找用户有权限访问的菜单
	 */
	@Override
	public List<Resource> findHasPermissionMenusByUsername(String username) {
		List<Resource> result = new ArrayList<Resource>();
		Optional<User> user = userService.getUser(username);
		if(user.isPresent()){
			Set<Role> roles = user.get().getRoles();
			if (!CollectionUtils.isEmpty(roles)) {
				Set<Long> menuIds = new HashSet<>();
				for (Role role : roles) {
					List<RoleResourcePermission> roleMenuPermissions = role.getResourcePermission();
					if (!CollectionUtils.isEmpty(roleMenuPermissions)) {
						for (RoleResourcePermission roleMenuPermission : roleMenuPermissions) {
							menuIds.add(roleMenuPermission.getResourceId());
						}
					}
				}
				// 取到所有的菜单ID后，获取菜单数据
				if (!CollectionUtils.isEmpty(menuIds)) {
					result.addAll(resourceDao.findByIdInAndIsShowOrderByOrderNo(menuIds, true).stream().filter(Resource -> Resource.getType() == 0 ).collect(Collectors.toList()) );
				}
			}
		}
		// 为分类建立键值对
		Map mapNodes = new HashMap(result.size());
		for (Resource treeNode : result) {
			mapNodes.put(treeNode.getId(), treeNode);
		}
		// 初始化多叉树信息，里面只保存顶级分类信息
		List<Resource> topTree = new ArrayList<Resource>();// 多叉树
		for (Resource treeNode : result) {
			if (treeNode.getParentId() != null && treeNode.getParentId() == 0) {// 添加根节点（顶级分类）
				Resource rootNode = (Resource) mapNodes.get(treeNode.getId());
				topTree.add(rootNode);
			} // end if
			else {
				Resource parentNode = (Resource) mapNodes.get(treeNode.getParentId());
				if (parentNode != null) {
					if (parentNode.getChildren() == null) {
						parentNode.setChildren(new ArrayList<Resource>());
					}
					List<Resource> children = parentNode.getChildren();
					children.add(treeNode);
				}
			} // end else
		} // end for
		return topTree;
	}

	@Override
	public List<Resource> findHasPermissionMenusByUsernameNew(String username) {
		List<Resource> validMenus = findHasPermissionMenusByUsername(username);
		return validMenus;
//		// 不需要特殊过滤
//		CurrentUser currentUser = new CurrentUser();
//		Set<String> filterMenus = getFilterMenus(currentUser);
//		return validMenus.stream().filter(m -> {
//			boolean isContain = !filterMenus.contains(m.getIdentity());
//			if (isContain && !CollectionUtils.isEmpty(m.getChildren())) {
//				// 如果二级菜单需要过滤也来判断一把
//				List<Resource> removeMenus = new ArrayList<>();
//				m.getChildren().forEach(me -> {
//					if (filterMenus.contains(me.getIdentity())) {
//						removeMenus.add(me);
//					}
//				});
//				m.getChildren().removeAll(removeMenus);
//			}
//			return isContain;
//		}).collect(Collectors.toList());
	}

	/**
	 * 获取需要特殊滤掉的菜单
	 * 
	 * @param currentUser
	 * @return
	 */
	private Set<String> getFilterMenus(CurrentUser currentUser) {
		Set<String> result = new HashSet<>();
		if (!currentUser.getIsAdmin()) {
			result.add("/sys/admin");
		}
		return result;
	}

	@Override
	public PageResult<Resource> getPageResource(PageParameter page, Resource param) {

		Page<Resource> pages = resourceDao.findAll((root, query, cb) -> {
			List<Predicate> predicate = new ArrayList<>();
			// 按id查找
			if (param.getId() != null) {
				predicate.add(cb.equal(root.get("id").as(Long.class), param.getId()));
			}

			// 按资源名称
			if (!StringUtils.isEmpty(param.getName())) {
				predicate.add(cb.like(root.get("name").as(String.class), "%" + param.getName() + "%"));
			}
			Predicate[] pre = new Predicate[predicate.size()];
			query.where(predicate.toArray(pre));
			return null;
		}, page);
		PageResult<Resource> result = new PageResult<>(pages, page);

		return result;
	}

	@Override
	public List<RoleResourcePermission> getRoleToResource(Long id) {
		List<RoleResourcePermission> roleResourcePermissionList = roleResourcePermissionDao.findAll();
		roleResourcePermissionList = roleResourcePermissionList.stream().filter(RoleResourcePermission->RoleResourcePermission.getRole()==null).collect(Collectors.toList());
		return roleResourcePermissionList;
	}

}
