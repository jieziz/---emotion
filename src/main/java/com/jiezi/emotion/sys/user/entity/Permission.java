package com.jiezi.emotion.sys.user.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.jiezi.emotion.base.BaseEntity;

import lombok.Getter;
import lombok.Setter;

/**
 * 权限管理
 * @author zhangliang
 *
 */
@Entity
@Table(name = "sys_permission")
@Setter
@Getter
public class Permission extends BaseEntity<Long> {

	/**
	 * 前端显示名称
	 */
	@Column(name = "name")
	private String name;
	/**
	 * 系统中验证时使用的权限标识
	 */
	@Column(name = "permission_name")
	private String permissionName;

	/**
	 * 详细描述
	 */
	@Column(name = "description")
	private String description;

	/**
	 * 是否显示 也表示是否可用 为了统一 都使用这个
	 */
	@Column(name = "is_show")
	private Boolean show = Boolean.FALSE;
	
	/**
	 * 是否使用
	 */
	@Transient
	private Boolean iswitch = false;

}
