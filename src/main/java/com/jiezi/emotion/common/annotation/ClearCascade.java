package com.jiezi.emotion.common.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;
import java.lang.annotation.RetentionPolicy;

/**
 * 提供清除级联关系注解，<b>该注解仅对Action中返回值为CommonResponse的方法自动格式化，其余还需手动调用format()</b>
 * @author xlsiek
 *
 */
@Target({ ElementType.METHOD })
@Retention(value = RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface ClearCascade {
	/**
	 * 标注输出格式为timestamp，日期类自动转换，true timestamp，false string。默认string
	 * @return boolean
	 */
	boolean dateToTimestamp() default false;

	/**
	 * 过滤条款
	 * @return filter[]
	 */
	FilterRetainTerm[] filter() default {};
	/**
	 * 保留条款
	 * @return retain[]
	 */
	FilterRetainTerm[] retain() default {};
	/**
	 * 
	 * @return extract()
	 */
	Extract extract() default @Extract(items = {});
	
	ExtractRenameTerm[] rename() default {};
}
