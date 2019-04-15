package com.jiezi.emotion.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
/**
 * 清除剂联关系之萃取
 * @author zhangliang
 *
 */
@Target(value=ElementType.ANNOTATION_TYPE)
@Retention(value = RetentionPolicy.RUNTIME)
public @interface Extract{
	/**
	 * 萃取列表
	 * @return items[]
	 */
	ExtractRenameTerm[] items();
	/**
	 * 萃取后是否删除原属性 true删除，false保留，默认false
	 * @return boolean
	 */
	boolean extractRemove() default false;
}
