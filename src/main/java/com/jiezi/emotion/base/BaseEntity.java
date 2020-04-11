package com.jiezi.emotion.base;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * <p> 抽象实体基类，提供统一的ID，和相关的基本功能方法
 */
@MappedSuperclass
@Data
@EqualsAndHashCode(callSuper=true)//使用后会调用父类重写的方法
public abstract class BaseEntity<ID extends Serializable> extends AbstractEntity<ID> {

    /**
	 * 序列化
	 */
	private static final long serialVersionUID = 1L;
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private ID id;
	
	@Column(name = "created_at", nullable = false, updatable=false)
	private Date createdAt = new Date();
	
	@Column(name = "updated_at", nullable = false, updatable=false)
	private Date updatedAt = new Date();
	
	


}
