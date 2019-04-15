package com.jiezi.emotion.sys.user.action;

import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jiezi.emotion.common.ClearCascadeJSON;
import com.jiezi.emotion.common.Constants;
import com.jiezi.emotion.common.entity.CommonResponse;
import com.jiezi.emotion.common.entity.CurrentUser;
import com.jiezi.emotion.common.entity.PageParameter;
import com.jiezi.emotion.common.util.JWTUtil;
import com.jiezi.emotion.sys.user.entity.Resource;
import com.jiezi.emotion.sys.user.entity.Role;
import com.jiezi.emotion.sys.user.entity.User;
import com.jiezi.emotion.sys.user.service.ResourceService;
import com.jiezi.emotion.sys.user.service.RoleService;
import com.jiezi.emotion.sys.user.service.UserService;

@RestController
@RequestMapping("/system/user")
public class UserAction {

	@Autowired
	private UserService userService;
	@Autowired
	private RoleService roleService;

	@Autowired
	private ResourceService resourceService;

	private ClearCascadeJSON clearCascadeJSON;
	{
		clearCascadeJSON = ClearCascadeJSON
				.get()
				.addRetainTerm(User.class, "id", "username", "password", "realname","gender", "createdAt","roles")
				.addRetainTerm(Role.class, "id", "name");
	}

	/**
	 * 查看用户列表
	 * @param request
	 * @param user
	 * @return
	 */
	@GetMapping(value = "/pages")
	public CommonResponse userPages(HttpServletRequest request, User user, PageParameter page) {
		CommonResponse cr = new CommonResponse();
		cr.setData(clearCascadeJSON.format(userService.getPageUser(page, user)).toJSON());
		return cr;
	}

	/**
	 * 新增用户
	 * @param request
	 * @param user
	 * @return cr
	 */
	@PostMapping("/add")
//	@RequiresPermissions(value={"user:update"},logical=Logical.AND)
	public CommonResponse addUser(HttpServletRequest request, User user) {
		CommonResponse cr = new CommonResponse();
		if (user.getId() != null) {
			Optional<User> oldUser = userService.findOne(user.getId());
			userService.update(user, oldUser.get());
			cr.setMessage("修改成功");
		} else {
			user.setPassword("123456");
			user.setBan(0);
			userService.save(user);
			cr.setMessage("添加成功");
		}
		return cr;
	}
	
	/**
	 * 给用户分配角色
	 * @param request
	 * @param user
	 * @return cr
	 */
	@GetMapping("/addUserRole")
	public CommonResponse addUserRole(String ids,Long id) {
		CommonResponse cr = new CommonResponse();
		if (!StringUtils.isEmpty(ids) && !StringUtils.isEmpty(id)) {
			int count = roleService.addUserRole(ids,id);
			cr.setMessage("成功为用户分配"+count+"个角色");
		}else{
			cr.setMessage("用戶和角色不能为空");
			
		}
		return cr;
	}

	/**
	 * 删除用户
	 * 
	 * @param request
	 *            请求
	 * @param user
	 *            用户实体类
	 * @return cr
	 */
	@DeleteMapping("/delete/{ids}")
	public CommonResponse deleteUser(@PathVariable("ids") String[] ids) {
		CommonResponse cr = new CommonResponse();
		if (!StringUtils.isEmpty(ids)) {
			if (ids.length > 0) {
				for (int i = 0; i < ids.length; i++) {
					Long id = Long.parseLong(ids[i]);
					userService.delete(id);
				}
			}
		}
		cr.setMessage("删除成功");
		return cr;
	}

	/**
	 * 查看用户
	 */
	@GetMapping(value = "/get/{id}")
	public CommonResponse getUser(@PathVariable("id") Long id) {
		CommonResponse cr = new CommonResponse();
		Optional<User> user = userService.findOne(id);
		if (user.isPresent()) {
			cr.setData(clearCascadeJSON.format(user.get()).toJSON());
			cr.setMessage("查找成功");
		} else {
			cr.setMessage("查找失败");
		}
		return cr;
	}
	

	/**
	 * 查看当前登录用户
	 */
	@GetMapping(value = "/getCurrentUser")
	public CommonResponse getCurrentUser(HttpServletRequest request) {
		CommonResponse cr = new CommonResponse();
		String token = request.getHeader(Constants.PARAM_TOKEN);
		CurrentUser currentUser = new CurrentUser();
		// 解密获得username
        String username = JWTUtil.getClaim(token, Constants.ACCOUNT);
		List<Resource> resourceList = resourceService.findHasPermissionMenusByUsernameNew(username);
		currentUser.setPermissions(resourceList);
		currentUser.setUserName(username);
		if (currentUser != null) {
			cr.setData(currentUser);
			cr.setMessage("查找成功");
		} 
		return cr;
	}

}
