package com.jiezi.emotion.sys.user.entity;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import com.jiezi.emotion.base.BaseEntity;

import lombok.Getter;
import lombok.Setter;

/**
 * 角色  资源-权限管理表（主体是资源，标识该资源所拥有的权限，例如增删改查）
 * @author zhangliang
 *
 */
@TypeDef(name = "SetToStringUserType", typeClass = CollectionToStringUserType.class, parameters = {
		@Parameter(name = "separator", value = ","),
		@Parameter(name = "collectionType", value = "java.util.HashSet"),
		@Parameter(name = "elementType", value = "java.lang.Long") })
@Entity
@Table(name = "sys_role_resource_permission")
@Setter
@Getter
public class RoleResourcePermission extends BaseEntity<Long> {
	
	
	/**
	 * 角色实体
	 */
	@ManyToOne(optional = true, fetch = FetchType.EAGER)
	@Fetch(FetchMode.SELECT)
	private Role role;

	/**
	 * 资源id
	 */
	@Column(name = "resource_id")
	private Long resourceId;
	
	/**
	 * 资源权限名称
	 */
	@Column(name = "name")
	private String name;

	/**
	 * 权限id列表 数据库通过字符串存储 逗号分隔
	 */
	@Column(name = "permission_ids")
	@Type(type = "SetToStringUserType")
	private Set<Long> permissionIds;
	
	/**
	 * 权限名称
	 */
	@Column(name = "permission_names")
	private  String  permissionNames;

	/**
	 * 资源名称
	 */
	@Transient
	private String resourceName;

}
