package com.jiezi.emotion.business.mood.action;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jiezi.emotion.business.mood.entity.Mood;
import com.jiezi.emotion.business.mood.service.MoodService;
import com.jiezi.emotion.common.ClearCascadeJSON;
import com.jiezi.emotion.common.entity.CommonResponse;
import com.jiezi.emotion.common.entity.PageParameter;
import com.jiezi.emotion.sys.user.entity.User;

@RestController
@RequestMapping("/business/mood")
public class MoodAction {
	
	@Autowired
	private MoodService moodService;
	
	private ClearCascadeJSON clearCascadeJSON;
	{
		clearCascadeJSON = ClearCascadeJSON
				.get()
				.addRetainTerm(Mood.class,"id","title","content","type","user","createdAt","updatedAt")
				.addRetainTerm(User.class,"id","username","realname","gender");
	}
	
	/**
	 *  查看用户心情列表
	 * @param request
	 * @param user
	 * @return
	 */
	@GetMapping(value = "/pages")
	public CommonResponse pages(Mood mood,PageParameter page) {
		CommonResponse cr = new CommonResponse();
		cr.setData(clearCascadeJSON.format(moodService.getPageMood(page,mood)).toJSON());
		return cr;
	}
	
	/**
	 * 新增用户心情
	 * 
	 * @param request 请求
	 * @param user 用户实体类
	 * @return cr
	 */
	@PostMapping("/add")
	public CommonResponse add(Mood mood) {
		CommonResponse cr = new CommonResponse();
		moodService.save(mood);
		cr.setMessage("添加成功");
		return cr;
	}
	
	
	/**
	 * 删除用户心情
	 * 
	 * @param request 请求
	 * @param user 用户实体类
	 * @return cr
	 */
	@DeleteMapping("/delete/{id}")
	public CommonResponse delete(@PathVariable("id") Long id) {
		CommonResponse cr = new CommonResponse();
		moodService.delete(id);
		cr.setMessage("删除成功");
		return cr;
	}
	
	

	
	

}
