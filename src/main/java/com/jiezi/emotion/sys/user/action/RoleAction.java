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
import com.jiezi.emotion.common.entity.CommonResponse;
import com.jiezi.emotion.common.entity.ErrorCode;
import com.jiezi.emotion.common.entity.PageParameter;
import com.jiezi.emotion.common.entity.PageResult;
import com.jiezi.emotion.sys.user.entity.Role;
import com.jiezi.emotion.sys.user.entity.RoleResourcePermission;
import com.jiezi.emotion.sys.user.service.RoleService;



@RestController
@RequestMapping("/sys/role")
public class RoleAction {
	
	
	@Autowired
	private RoleService roleService;

	private ClearCascadeJSON clearCascadeJSON;

	{
		clearCascadeJSON = ClearCascadeJSON.get()
				.addFilterTerm(Role.class, "users","resourcePermissions")
				.addFilterTerm(RoleResourcePermission.class, "role");
	}



	/**
	 * 查询全部的角色
	 * @param request 请求
	 * @param pp 分页参数
	 * @param role 角色
	 * @return cr
	 */
	@GetMapping(value = "/all")
	public CommonResponse all(HttpServletRequest request) {
		List<Role> roles = roleService.findAll();
		CommonResponse cr = new CommonResponse(clearCascadeJSON.format(roles)
				.toJSON());
		return cr;
	}

	/**
	 * 分页查询全部的角色
	 * @param request 请求
	 * @param pp 分页参数
	 * @param role 角色
	 * @return cr
	 */
	@GetMapping(value = "/pages")
	public CommonResponse pages(HttpServletRequest request,
			PageParameter pp, Role role) {
		PageResult<Role> roles = roleService.getPage(pp, role);
		CommonResponse cr = new CommonResponse(clearCascadeJSON.format(roles)
				.toJSON());
		return cr;
	}

	/**
	 * 根据id查询一个角色
	 * 
	 * @param id 角色id
	 * @return cr
	 */
	@GetMapping(value = "/get/{id}")
	public CommonResponse getRole(@PathVariable("id") long id) {
		Role role = roleService.findOne(id).get();
		CommonResponse cr = new CommonResponse(clearCascadeJSON.format(role)
				.toJSON());
		return cr;
	}

	/**
	 * 添加一个角色
	 * @param request 请求
	 * @param role 角色
	 * @return cr
	 */
	@PostMapping(value = "/add")
	public CommonResponse add(Role role) {
		CommonResponse cr = new CommonResponse();
		if (role.getId() != null) {
			Optional<Role> oldRole = roleService.findOne(role.getId());
			roleService.update(role, oldRole.get());
			cr.setMessage("修改成功");
		} else {
			roleService.save(role);
			cr.setMessage("添加成功");
		}
		return cr;
		
	}
	
	
	/**
	 * 給角色分配資源权限
	 * @param request 请求
	 * @param role 角色
	 * @return cr
	 */
	@GetMapping(value = "/addRoleResource")
	public CommonResponse addRoleResource(Long id, String ids) {
		CommonResponse cr = new CommonResponse();
		int count = roleService.addRoleResource(id,ids);
		cr.setMessage("成功给当前角色分配"+count+"条资源权限");
		return cr;
		
	}


	/**
	 * 判断角色的name是否存在相同的
	 * 
	 * @param role 角色实体类
	 * @return cr
	 */
	@GetMapping(value = "/checkRole")
	public CommonResponse exists(Role role) {
		CommonResponse cr = new CommonResponse();
		if (role.getName() != null) {
			if (roleService.findByName(role.getName()) == null) {
				cr.setData("角色名可以使用");
			} else {
				cr.setCode(ErrorCode.SYSTEM_USER_NAME_REPEAT.getCode());
				cr.setData("角色名已存在");
			}
		} else {
			cr.setData("没有传递必要的参数name");
		}
		return cr;
	}



	/**
	 * 删除一个角色信息
	 * @param request 请求
	 * @param id roleid
	 * @return cr
	 */
	@DeleteMapping("/delete/{ids}")
	public CommonResponse delete(@PathVariable("ids") String[] ids) {
		CommonResponse cr = new CommonResponse();
		if (!StringUtils.isEmpty(ids)) {
			if (ids.length > 0) {
				for (int i = 0; i < ids.length; i++) {
					Long id = Long.parseLong(ids[i]);
					roleService.delete(id);
				}
			}
		}
		cr.setMessage("删除成功");
		return cr;
	}
	

}
