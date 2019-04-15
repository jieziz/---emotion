package com.jiezi.emotion.sys.user.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.persistence.criteria.Predicate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.jiezi.emotion.base.BaseServiceImpl;
import com.jiezi.emotion.common.entity.PageParameter;
import com.jiezi.emotion.common.entity.PageResult;
import com.jiezi.emotion.sys.user.dao.PermissionDao;
import com.jiezi.emotion.sys.user.dao.RoleResourcePermissionDao;
import com.jiezi.emotion.sys.user.entity.Permission;
import com.jiezi.emotion.sys.user.entity.Resource;
import com.jiezi.emotion.sys.user.entity.RoleResourcePermission;

@Service
public class PermissionServiceImpl extends BaseServiceImpl<Permission, Long> implements PermissionService {

	@Autowired
	private ResourceService resourceService;

	@Autowired
	private PermissionDao dao;

	@Autowired
	private RoleResourcePermissionDao roleResourcePermissionDao;

	public Set<String> getActualPermissionStr(Long resourceId, Set<Long> permissionIds) {
		Resource resource = resourceService.findOne(resourceId).get();
		Set<String> result = new HashSet<String>();
		if (!CollectionUtils.isEmpty(permissionIds)) {
			for (Long permissionId : permissionIds) {
				Permission permission = findOne(permissionId).get();
				// 将资源的身份和拥有的权限作为组合传入
				String actualPermissionStr = resource.getIdentity() + ":" + permission.getPermissionName();
				result.add(actualPermissionStr);
			}
		}
		return result;
	}

	@Override
	public PageResult<Permission> getFindPage(PageParameter page, Permission param) {
		Page<Permission> pageData = dao.findAll((root, query, cb) -> {
			List<Predicate> predicate = new ArrayList<>();
			if (param.getId() != null) {
				predicate.add(cb.equal(root.get("id").as(Long.class), param.getId()));
			}
			if (!StringUtils.isEmpty(param.getName())) {
				predicate.add(cb.like(root.get("name").as(String.class), "%" + param.getName() + "%"));
			}
			Predicate[] pre = new Predicate[predicate.size()];
			query.where(predicate.toArray(pre));
			return null;
		}, page);
		PageResult<Permission> result = new PageResult<>(pageData, page);
		return result;
	}

	// 查询所有权限,同时传入资源id，对资源拥有的权限，做标记
	@Override
	public List<Permission> getResourceToPermission(Long id) {
		List<Permission> permissionList = dao.findAll();
		Optional<Resource> resource = resourceService.findOne(id);
		if (resource.isPresent()) {
			List<RoleResourcePermission> resourcePermissions = roleResourcePermissionDao.findByResourceId(id);
			if (resourcePermissions.size() > 0) {
				for (RoleResourcePermission resourcePermission : resourcePermissions) {
					for (Permission rs : permissionList) {
						if (rs.getId() == resourcePermission.getResourceId()) {
							rs.setIswitch(true);
						}
					}
				}
			}
		}
		return permissionList;
	}

	@Override
	public int addResourcePermission(String ids, Long id, String name) {
		Optional<Resource> resource = resourceService.findOne(id);
		int count = 0;
		if (resource.isPresent()) {
			Set<Long> permissionIds = new HashSet<Long>();
			String permissionNames = "";
			String[] arrIds = ids.split(",");
			RoleResourcePermission roleResourcePermission = new RoleResourcePermission();
			roleResourcePermission.setResourceId(id);
			if (arrIds.length > 0) {
				for (int i = 0; i < arrIds.length; i++) {
					Long rid = Long.parseLong(arrIds[i]);
					Optional<Permission> permission = dao.findById(rid);
					permissionIds.add(rid);
					permissionNames += permission.get().getName() + ",";
					count++;
				}
			}
			roleResourcePermission.setName(name);
			roleResourcePermission.setPermissionIds(permissionIds);
			roleResourcePermission.setPermissionNames(permissionNames);
			roleResourcePermissionDao.save(roleResourcePermission);
		}
		return count;
	}

	@Override
	public PageResult<RoleResourcePermission> getFindPermissionPage(PageParameter page, RoleResourcePermission param) {
		Page<RoleResourcePermission> pageData = roleResourcePermissionDao.findAll((root, query, cb) -> {
			List<Predicate> predicate = new ArrayList<>();
			if (param.getId() != null) {
				predicate.add(cb.equal(root.get("id").as(Long.class), param.getId()));
			}
			if (!StringUtils.isEmpty(param.getName())) {
				predicate.add(cb.like(root.get("name").as(String.class), "%" + param.getName() + "%"));
			}
			Predicate[] pre = new Predicate[predicate.size()];
			query.where(predicate.toArray(pre));
			return null;
		}, page);
		PageResult<RoleResourcePermission> result = new PageResult<>(pageData, page);
		result.getRows().stream().forEach(rep -> {
			Optional<Resource> resource = resourceService.findOne(rep.getResourceId());
			if (resource.isPresent()) {
				rep.setResourceName(resource.get().getName());
			}
		});
		return result;
	}

	@Override
	public Optional<RoleResourcePermission> getResourcePermission(Long id) {
		return roleResourcePermissionDao.findById(id);
	}

	@Override
	public void updateResourcePermission(String ids, Long id) {
		Optional<RoleResourcePermission> roleResourcePermission = roleResourcePermissionDao.findById(id);
		if (roleResourcePermission.isPresent()) {
			Set<Long> permissionIds = new HashSet<Long>();
			String permissionNames = "";
			String[] arrIds = ids.split(",");
			if (arrIds.length > 0) {
				for (int i = 0; i < arrIds.length; i++) {
					Long rid = Long.parseLong(arrIds[i]);
					Optional<Permission> permission = dao.findById(rid);
					permissionIds.add(rid);
					permissionNames += permission.get().getName() + ",";
				}
			}
			roleResourcePermission.get().setPermissionIds(permissionIds);
			roleResourcePermission.get().setPermissionNames(permissionNames);
			roleResourcePermissionDao.save(roleResourcePermission.get());
		}
	}

	@Override
	public void deleteResourcePermission(Long id) {
		Optional<RoleResourcePermission> roleResourcePermission = roleResourcePermissionDao.findById(id);
		if (roleResourcePermission.isPresent()) {
			roleResourcePermission.get().setRole(null);
			roleResourcePermission.get().setPermissionIds(null);
			roleResourcePermission.get().setResourceId(null);
			roleResourcePermissionDao.delete(roleResourcePermission.get());
		}
	}

}
