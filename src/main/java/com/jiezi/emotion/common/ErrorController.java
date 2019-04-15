package com.jiezi.emotion.common;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.apache.shiro.ShiroException;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * 
 * 无法被@ControllerAdvice捕获到的异常，先从这里进行处理
 * @author zhangliang
 *
 */
@RestController
public class ErrorController {
	
	    @RequestMapping(value = "/500", produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
	    public ResponseEntity<?> to500() throws ShiroException {
	        HttpServletRequest request = ((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getRequest();
	        ServletException attribute = (ServletException) request.getAttribute("javax.servlet.error.exception");
	        throw new ShiroException(attribute.getCause().getMessage());
	    }
	    
	    @RequestMapping(value = "/404", produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
	    public ResponseEntity<?> to404() throws Exception {
	        HttpServletRequest request = ((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getRequest();
	        ServletException attribute = (ServletException) request.getAttribute("javax.servlet.error.exception");
	        throw new ShiroException(attribute.getCause().getMessage());
	    }

}
