package com.jiezi.emotion.common;

import javax.servlet.http.HttpServletRequest;

import org.apache.shiro.ShiroException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.jiezi.emotion.common.entity.CommonResponse;

/**
 * @Author zhangliang
 * @Description 异常处理
 */
@RestControllerAdvice
public class ExceptionController {
	
    private final CommonResponse cr;

    @Autowired
    public ExceptionController(CommonResponse cr) {
        this.cr = cr;
    }

    // 捕捉shiro的异常
    @ExceptionHandler(ShiroException.class)
    public CommonResponse handleShiroException(HttpServletRequest request, ShiroException ex) {
        cr.setCode(getStatus(request).value());
    	cr.setMessage(ex.getMessage());
        return cr;
    }

    // 捕捉其他所有异常
    @ExceptionHandler(Exception.class)
    public CommonResponse globalException(HttpServletRequest request, Exception ex) {
    	Integer code = getStatus(request).value();
    	cr.setCode(code);
    	if(code==404){
    		cr.setMessage("无法找到资源");
    	}else{
    		cr.setMessage("访问出错，无法访问: " + ex.getMessage());
    	}
        return cr;
    }
    
    private HttpStatus getStatus(HttpServletRequest request) {
        Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");
        if (statusCode == null) {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
        return HttpStatus.valueOf(statusCode);
    }
}
