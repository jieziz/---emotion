package com.jiezi.emotion.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
/**
 * 清除级联关系之萃取和改名item
 * @author zhangliang
 *
 */
@Target(value=ElementType.ANNOTATION_TYPE)
@Retention(value = RetentionPolicy.RUNTIME)
public @interface ExtractRenameTerm{
	/**
	 * 原来的值
	 * @return old
	 */
	String old();
	/**
	 * 新值
	 * @return new
	 */
	String newValue();
}
