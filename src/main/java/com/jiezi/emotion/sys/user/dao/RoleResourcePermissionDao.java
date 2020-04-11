package com.jiezi.emotion.sys.user.dao;

import java.util.List;

import com.jiezi.emotion.base.BaseRepository;
import com.jiezi.emotion.sys.user.entity.RoleResourcePermission;

public interface RoleResourcePermissionDao extends BaseRepository<RoleResourcePermission, Long>{

	public List<RoleResourcePermission> findByResourceId(Long id);

}
