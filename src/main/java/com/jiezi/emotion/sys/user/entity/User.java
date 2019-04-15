package com.jiezi.emotion.sys.user.entity;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import com.jiezi.emotion.base.BaseEntity;

import lombok.Getter;
import lombok.Setter;
/**
 * 用户实体
 * @author zhangliang
 *
 */
@Entity
@Table(name = "sys_user")
@Setter
@Getter
public class User extends BaseEntity<Long> {
	
	/**
	 * 用户名
	 */
	@Column(name = "username")
	private String username;
	
	/**
	 * 密码
	 */
	@Column(name = "password")
	private String password;
	
	/**
	 * 真实姓名
	 */
	@Column(name = "real_name")
	private String realname;
	
	/**
	 * 
	 * 性别
	 */
	@Column(name = "gender")
	private Integer gender;
	
	/**
	 * 角色集合
	 */
	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "sys_user_role", joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"))
	private Set<Role> roles = new HashSet<Role>();
	
	
	/**
	 * 用户是否被禁用（0=未，1=禁用）
	 */
	@Column(name = "ban")
    private Integer ban;
	
	
	
	
	
}
