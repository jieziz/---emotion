package com.jiezi.emotion.common;

import com.jiezi.emotion.common.entity.ErrorCode;

/**
 * Service层公用的Exception.
 * 
 * 继承自RuntimeException.
 * 
 */
public class ServiceException extends RuntimeException {

	private static final long serialVersionUID = 4393164921532381692L;
	
	private ErrorCode errorCode = null;

	public ServiceException() {
		super();
	}
	
	/**
	 * 指定内容创建业务异常。
	 * @param message
	 */
	public ServiceException(String message) {
		super(message);
	}
	
	
	/**
	 * 指定错误码和内容创建业务异常。
	 * @param errorCode 错误码。
	 * @param message
	 */
	public ServiceException(ErrorCode errorCode,String message) {
		super(message);
		this.errorCode = errorCode;
	}

	
	/**
	 * 指定异常对象创建业务异常。
	 * @param cause
	 */
	public ServiceException(Throwable cause) {
		super(cause);
	}
	
	/**
	 * 指定错误码和异常对象创建业务异常。
	 * @param errorCode 错误码。
	 * @param cause
	 */
	public ServiceException(ErrorCode errorCode,Throwable cause) {
		super(cause);
		this.errorCode = errorCode;
	}

	/**
	 * 指定内容和异常对象创建业务异常。
	 * @param message
	 * @param cause
	 */
	public ServiceException(String message, Throwable cause) {
		super(message, cause);
	}
	
	/**
	 * 指定错误码、内容和异常对象创建业务异常。
	 * @param errorCode 错误码。
	 * @param message
	 * @param cause
	 */
	public ServiceException(ErrorCode errorCode,String message, Throwable cause) {
		super(message, cause);
		this.errorCode = errorCode;
	}
	
	/**
	 * 得到错误码。
	 * @return 错误码。
	 */
	public ErrorCode getErrorCode() {
		return this.errorCode;
	}
	
}
