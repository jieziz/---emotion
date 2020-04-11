package com.jiezi.emotion.business.mood.service;

import org.springframework.stereotype.Service;

import com.jiezi.emotion.base.BaseCRUDService;
import com.jiezi.emotion.business.mood.entity.Mood;
import com.jiezi.emotion.common.entity.PageParameter;
import com.jiezi.emotion.common.entity.PageResult;
@Service
public interface MoodService  extends BaseCRUDService<Mood, Long>{
	
	/**
	 * 分页查看用户心情
	 * @param page
	 * @param mood
	 * @return
	 */
	public PageResult<Mood> getPageMood(PageParameter page, Mood param);

}
