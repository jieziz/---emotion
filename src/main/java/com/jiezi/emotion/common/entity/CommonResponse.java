package com.jiezi.emotion.common.entity;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

/**
 * 通用的返回信息，
 * 调用HTTP接口等统一的响应（返回）信息。
 * 
 */
@Component
public class CommonResponse {
	
	/**正常返回码*/
	public static final int SUCCESS_CODE = 0;
	
	/**返回码*/
	private Integer code = SUCCESS_CODE;
	
	/**提示信息*/
    private String message;
    
    /**时间戳*/
    private Long timestamp = System.currentTimeMillis();
    
	/**返回的数据*/
	private Object data;
	
	/**
	 * 默认构造函数。
	 */
	public CommonResponse() {
		
	}
	
	/**
	 * 指定返回数据的构造函数。
	 * @param data 返回数据。
	 */
	public CommonResponse(Object data) {
		this.setData(data);
	}
	
	/**
	 * 获取返回码，默认为0.
	 * @return 返回码。
	 */
	public Integer getCode() {
		return code;
	}

	/**
	 * 设置返回码，默认为0.
	 * @param code 返回码。
	 */
	public void setCode(Integer code) {
		this.code = code;
	}

	/**
	 * 获取消息内容。
	 * @return 消息内容。
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * 设置消息内容。
	 * @param message 消息内容。
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	/**
	 * 获取时间戳，默认为系统时间。
	 * @return 时间戳。
	 */
	public Long getTimestamp() {
		return timestamp;
	}

	/**
	 * 设置时间戳，默认为系统时间。
	 * @param timestamp 时间戳。
	 */
	public void setTimestamp(Long timestamp) {
		this.timestamp = timestamp;
	}

	
	/**
	 * 获取返回数据。
	 * @return 返回数据。
	 */
	public Object getData() {
		return data;
	}

	/**
	 * 设置返回数据。
	 * @param data 返回数据。
	 */
	public void setData(Object data) {
		this.data = data;
	}
	
	
	/**
	 * 转化成Map对象。
	 * @return Bean对应的Map对象。
	 */
	public Map<String,Object> toMap() {
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("code", getCode());
		map.put("message", getMessage());
		map.put("timestamp", getTimestamp());
		map.put("data", getData());
		return map;
	}
	
	

}
