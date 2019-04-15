package com.jiezi.emotion.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
/**
 * 清除级联关系之保留过滤item
 * @author zhangliang
 *
 */
@Target(value=ElementType.ANNOTATION_TYPE)
@Retention(value = RetentionPolicy.RUNTIME)
public @interface FilterRetainTerm{
	/**
	 * 目标类
	 * @return class<?>
	 */
	Class<?> classe() ;
	/**
	 * 字段名
	 * @return attrs[]
	 */
	String[] attrs();
}
