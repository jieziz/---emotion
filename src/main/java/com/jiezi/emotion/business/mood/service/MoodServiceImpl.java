package com.jiezi.emotion.business.mood.service;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.Predicate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.jiezi.emotion.base.BaseServiceImpl;
import com.jiezi.emotion.business.mood.dao.MoodDao;
import com.jiezi.emotion.business.mood.entity.Mood;
import com.jiezi.emotion.common.entity.PageParameter;
import com.jiezi.emotion.common.entity.PageResult;

@Service
public class MoodServiceImpl  extends BaseServiceImpl<Mood, Long> implements MoodService{
	
	@Autowired
	private MoodDao dao;

	
	@Override
	public PageResult<Mood> getPageMood(PageParameter page, Mood param) {
		Page<Mood> pages = dao.findAll((root, query, cb) -> {
			List<Predicate> predicate = new ArrayList<>();
			//按id查找
			if (param.getId() != null) {
				predicate.add(cb.equal(root.get("id").as(Long.class),param.getId()));
			}
			
			//按产品名称
        	if(!StringUtils.isEmpty(param.getUsername())){
        		predicate.add(cb.like(root.get("user").get("username").as(String.class), "%"+param.getUsername()+"%"));
        	}
			Predicate[] pre = new Predicate[predicate.size()];
			query.where(predicate.toArray(pre));
			return null;
		}, page);
		PageResult<Mood> result = new PageResult<>(pages,page);
		
		return result;
	}

	


	
}
