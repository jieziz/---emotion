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
import org.springframework.util.StringUtils;

import com.jiezi.emotion.base.BaseServiceImpl;
import com.jiezi.emotion.common.entity.PageParameter;
import com.jiezi.emotion.common.entity.PageResult;
import com.jiezi.emotion.sys.user.dao.ResourceDao;
import com.jiezi.emotion.sys.user.dao.RoleDao;
import com.jiezi.emotion.sys.user.dao.RoleResourcePermissionDao;
import com.jiezi.emotion.sys.user.dao.UserDao;
import com.jiezi.emotion.sys.user.entity.Resource;
import com.jiezi.emotion.sys.user.entity.Role;
import com.jiezi.emotion.sys.user.entity.RoleResourcePermission;
import com.jiezi.emotion.sys.user.entity.User;

@Service
public class RoleServiceImpl extends BaseServiceImpl<Role, Long> implements RoleService {

	@Autowired
	private RoleDao dao;
	@Autowired
	private UserDao userDao;
	@Autowired
	private ResourceDao resourceDao;
	@Autowired
	private RoleResourcePermissionDao roleResourcePermissionDao;

	@Override
	public PageResult<Role> getPage(PageParameter page, Role param) {
		Page<Role> pageData = dao.findAll((root, query, cb) -> {
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
		PageResult<Role> result = new PageResult<>(pageData, page);
		return result;
	}

	@Override
	public Role findByName(String name) {
		return dao.findByName(name);
	}

	@Override
	public int addUserRole(String ids,Long id) {
		Optional<User>  user = userDao.findById(id);
		int count = 0;
		if(user.isPresent()){
			Set<Role> roleSet = new HashSet<Role>();
			String[] arrIds = ids.split(",");
			if (arrIds.length > 0) {
				for (int i = 0; i < arrIds.length; i++) {
					Long rid = Long.parseLong(arrIds[i]);
					 Optional<Role> role = dao.findById(rid);
					 if(role.isPresent()){
						 roleSet.add(role.get());
						 count++;
					 }
				}
			}
			user.get().setRoles(roleSet);
			userDao.save(user.get());
		}
		return count;
	}

	@Override
	public int addRoleResource(Long id, String ids) {
		Optional<Role>  role = dao.findById(id);
		int count = 0;
		if(role.isPresent()){			
			String[] arrIds = ids.split(",");
			if (arrIds.length > 0) {
				for (int i = 0; i < arrIds.length; i++) {
					Long rid = Long.parseLong(arrIds[i]);
					Optional<RoleResourcePermission> resourcePermission = roleResourcePermissionDao.findById(rid);;
					resourcePermission.get().setRole(role.get());
					roleResourcePermissionDao.save(resourcePermission.get());
					count++;
				}
			}
		}
		return count;	
	}

}
