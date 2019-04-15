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

import com.jiezi.emotion.base.BaseServiceImpl;
import com.jiezi.emotion.common.entity.PageParameter;
import com.jiezi.emotion.common.entity.PageResult;
import com.jiezi.emotion.sys.user.dao.UserDao;
import com.jiezi.emotion.sys.user.entity.Role;
import com.jiezi.emotion.sys.user.entity.RoleResourcePermission;
import com.jiezi.emotion.sys.user.entity.User;

@Service
public class UserServiceImpl extends BaseServiceImpl<User, Long> implements UserService {
	@Autowired
	private UserDao dao;
	@Autowired
	private PermissionService permissionService;

	@Override
	public Optional<User> login(String username, String password) {
		return  dao.findByUsernameAndPassword(username,password);
	}

	@Override
	public Optional<User> getUser(String username) {
		return dao.findByUsername(username);
	}

	@Override
	public Set<String> findStringPermissions(Optional<User> user) {
		 if (!user.isPresent()) {
	            return new HashSet<String>();
	        }
		 Set<String> result = new HashSet<String>();
	        Set<Role> roles = user.get().getRoles();
	        if (!CollectionUtils.isEmpty(roles)) {
	            for (Role role : roles) {
	            	// 查找所有角色关联的菜单权限
	                List<RoleResourcePermission> rolResourcePermissions = role.getResourcePermission();
	                if (!CollectionUtils.isEmpty(rolResourcePermissions)) {
	                    for (RoleResourcePermission rmp : rolResourcePermissions) {
	                    	 Set<String> actualPermissions = permissionService.getActualPermissionStr(rmp.getResourceId(),rmp.getPermissionIds());
	                        result.addAll(actualPermissions);
	                    }
	                }
	            }
	        }
	        return result;
	}

	@Override
	public PageResult<User> getPageUser(PageParameter page, User param) {
		
		Page<User> pageUser = dao.findAll((root, query, cb) -> {
			List<Predicate> predicate = new ArrayList<>();
			//按id查找
			if (param.getId() != null) {
				predicate.add(cb.equal(root.get("id").as(Long.class),param.getId()));
			}
			Predicate[] pre = new Predicate[predicate.size()];
			query.where(predicate.toArray(pre));
			return null;
		}, page);
		PageResult<User> result = new PageResult<>(pageUser,page);
		return result;
	}
	
	



}
