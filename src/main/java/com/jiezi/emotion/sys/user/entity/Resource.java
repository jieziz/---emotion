package com.jiezi.emotion.sys.user.entity;

import java.util.List;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import com.jiezi.emotion.base.BaseEntity;

import lombok.Getter;
import lombok.Setter;

/**
 * 
 * 权限资源表
 * @author zhangliang
 *
 */
@Entity
@Table(name = "sys_resource")
@Setter
@Getter
public class Resource extends BaseEntity<Long> {

	/**
	 * 资源名  
	 */
	@Column(name = "name")
	private String name;

	/**
	 * 资源身份（表示该资源在系统中的权限认证中的标识，对应前台的路由name）
	 */
	@Column(name = "identity")
	private String identity;
	/**
	 * url
	 */
	@Column(name = "url")
	private String url;
	/**
	 * 父id
	 */
	@Column(name = "parent_id")
	private Long parentId;
	
	
	/**
	 * 当资源是菜单是，在这里配置菜单的详细展示信息
	 * meta: {
	 *  title: { String|Number|Function }
	 *         显示在侧边栏、面包屑和标签栏的文字
	 *         使用'{{ 多语言字段 }}'形式结合多语言使用，例子看多语言的路由配置;
	 *         可以传入一个回调函数，参数是当前路由对象，例子看动态路由和带参路由
	 *  hideInMenu: (false) 设为true后在左侧菜单不会显示该页面选项
	 *  notCache: (false) 设为true后页面不会缓存
	 *  access: (null) 可访问该页面的权限数组，当前路由设置的权限会影响子路由
	 *  icon: (-) 该页面在左侧菜单、面包屑和标签导航处显示的图标，如果是自定义图标，需要在图标名称前加下划线'_'
	 *  beforeCloseName: (-) 设置该字段，则在关闭当前tab页时会去'@/router/before-close.js'里寻找该字段名对应的方法，作为关闭前的钩子函数
	 * }
	 **/
	@Column(name = "meta")
	private String meta;
	/**
	 * icon
	 */
	@Column(name = "icon")
	private String icon;
	/**
	 * 顺序
	 */
	@Column(name = "order_no")
	private Integer orderNo;
	
	/**
	 * 是否显示
	 */
	@Column(name = "is_show")
	private Boolean isShow;
	
	/**
	 * 资源类型（1=普通资源，0=菜单）
	 */
	@Column(name = "type")
	private Integer type;
	
	/**
	 * 菜单组件
	 */
	@Column(name = "component")
	private String component;
	

	
	/**
	 * 子类
	 */
	@Transient
	private List<Resource> children;
	


	

	public int getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(int orderNo) {
		this.orderNo = orderNo;
	}

	public int compareTo(Resource arg0) {
		try {
			return orderNo.compareTo(arg0.getOrderNo());
		} catch (Exception e) {
			return 0;
		}
	}

	public void setOrderNo(Integer orderNo) {
		this.orderNo = orderNo;
	}
	
	

}
