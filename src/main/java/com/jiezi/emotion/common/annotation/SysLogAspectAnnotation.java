package com.jiezi.emotion.common.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;    

/**  
 *自定义系统日志注解
 */    
@Target({ElementType.PARAMETER, ElementType.METHOD})    
@Retention(RetentionPolicy.RUNTIME)    
@Documented    
public @interface SysLogAspectAnnotation {
	
	/*方法描述*/
    String description()  default "";    
    /*操作模块*/
    String module() default "";
    /*操作名*/
    String operateName() default "";
    /*操作类型*/
    String operateType() default "请求";
    /*日志类型：前端用户日志，后端用户日志*/
    String logType() default "";

}
