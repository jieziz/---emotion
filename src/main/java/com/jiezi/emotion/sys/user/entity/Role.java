package com.jiezi.emotion.sys.user.entity;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import com.jiezi.emotion.base.BaseEntity;

import lombok.Getter;
import lombok.Setter;


/**
 *  角色管理
 * @author zhangliang
 *
 */
@Entity
@Table(name = "sys_role")
@Setter
@Getter
public class Role extends BaseEntity<Long> {

	/**
	 * 中文名称
	 */
	@Column(name = "name")
	private String name;

	/**
	 * 英文名称
	 */
	@Column(name = "role_name")
	private String roleName;
	
	/**
	 * 角色类型
	 */
	@Column(name = "role_type")
	private String roleType;

	/**
	 *描述
	 */
	@Column(name = "description")
	private String description;

	/**
	 * 是否可用 
	 */
	@Column(name = "is_show")
	private Boolean isShow;

	/**
	 * 该角色所有用户集合
	 */
	@ManyToMany(mappedBy = "roles", fetch = FetchType.LAZY)
	private Set<User> users = new HashSet<User>();
	
	/**
	 * 角色资源权限类
	 */
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, targetEntity = RoleResourcePermission.class, mappedBy = "role", orphanRemoval = true)
	@Fetch(FetchMode.SELECT)
	@Basic(optional = true, fetch = FetchType.EAGER)
	private List<RoleResourcePermission> resourcePermission;	
	

}

