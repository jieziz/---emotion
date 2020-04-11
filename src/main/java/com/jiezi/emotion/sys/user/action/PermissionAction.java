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
import com.jiezi.emotion.common.entity.PageParameter;
import com.jiezi.emotion.common.entity.PageResult;
import com.jiezi.emotion.sys.user.entity.Permission;
import com.jiezi.emotion.sys.user.entity.Role;
import com.jiezi.emotion.sys.user.entity.RoleResourcePermission;
import com.jiezi.emotion.sys.user.service.PermissionService;

@RestController
@RequestMapping("/sys/permission")
public class PermissionAction {

	@Autowired
	private PermissionService permissionService;
	


	private ClearCascadeJSON clearCascadeJSON;
	{
		clearCascadeJSON = ClearCascadeJSON.get().addRetainTerm(Permission.class, "id", "name", "permissionName","description", "show", "createdAt");
	}

	/**
	 * 查看权限列表
	 * 
	 * @param request
	 * @param user
	 * @return
	 */
	@GetMapping(value = "/pages")
	public CommonResponse pages(HttpServletRequest request, Permission permission, PageParameter page) {
		CommonResponse cr = new CommonResponse();
		cr.setData(clearCascadeJSON.format(permissionService.getFindPage(page, permission)).toJSON());
		return cr;
	}
	
	/**
	 * 查看所有权限
	 * 
	 * @param request
	 * @param user
	 * @return
	 */
	@GetMapping(value = "/allPermission")
	public CommonResponse allPermission() {
		CommonResponse cr = new CommonResponse();
		cr.setData(clearCascadeJSON.format(permissionService.findAll()).toJSON());
		return cr;
	}

	/**
	 * 查看资源权限列表
	 * 
	 * @param request
	 * @param user
	 * @return
	 */
	@GetMapping(value = "/permissionPages")
	public CommonResponse permissionPages(HttpServletRequest request, RoleResourcePermission roleResourcePermission,
			PageParameter page) {
		CommonResponse cr = new CommonResponse();
		PageResult<RoleResourcePermission>  list = permissionService.getFindPermissionPage(page, roleResourcePermission);
		cr.setData(ClearCascadeJSON.get()
				.addRetainTerm(RoleResourcePermission.class, "id", "name", "role","resourceId","permissionIds","createdAt","permissionNames","resourceName")
				.addRetainTerm(Role.class, "id", "name")
				.format(list).toJSON());
		return cr;
	}

	/**
	 * 查询所有权限,同时传入资源id，对资源拥有的权限，做标记
	 * 
	 * @param request
	 *            请求
	 * @return cr
	 */
	@GetMapping(value = "/all")
	public CommonResponse all(Long id) {
		CommonResponse cr = new CommonResponse();
		List<Permission> resourceList = permissionService.findAll();
		cr.setData(clearCascadeJSON.format(resourceList).toJSON());
		cr.setMessage("查询成功");
		return cr;
	}

	/**
	 * 新增权限
	 * 
	 * @param request
	 * @param user
	 * @return cr
	 */
	@PostMapping("/add")
	public CommonResponse addUser(HttpServletRequest request, Permission permission) {
		CommonResponse cr = new CommonResponse();
		if (permission.getId() != null) {
			Optional<Permission> oldpermission = permissionService.findOne(permission.getId());
			permissionService.update(permission, oldpermission.get());
			cr.setMessage("修改成功");
		} else {
			permissionService.save(permission);
			cr.setMessage("添加成功");
		}
		return cr;
	}
	

	/**
	 * 给资源分配权限
	 * 
	 * @param request
	 * @param user
	 * @return cr
	 */
	@GetMapping("/addResourcePermission")
	public CommonResponse addResourcePermission(String ids, Long id,String name) {
		CommonResponse cr = new CommonResponse();
		if (!StringUtils.isEmpty(ids) && !StringUtils.isEmpty(id)) {
			int count = permissionService.addResourcePermission(ids,id,name);
			cr.setMessage("成功为资源分配" + count + "个权限");
		} else {
			cr.setMessage("资源和权限不能为空");

		}
		return cr;
	}
	
	
	
	/**
	 * 修改资源权限
	 * 
	 * @param request
	 * @param user
	 * @return cr
	 */
	@GetMapping("/updateResourcePermission")
	public CommonResponse updateResourcePermission(String ids, Long id) {
		CommonResponse cr = new CommonResponse();
		if (!StringUtils.isEmpty(ids) && !StringUtils.isEmpty(id)) {
			permissionService.updateResourcePermission(ids, id);
			cr.setMessage("修改成功");
		} else {
			cr.setMessage("资源和权限不能为空");

		}
		return cr;
	}

	/**
	 * 删除权限
	 * 
	 * @param request
	 *            请求
	 * @param user
	 *            用户实体类
	 * @return cr
	 */
	@DeleteMapping("/delete/{ids}")
	public CommonResponse delete(@PathVariable("ids") String[] ids) {
		CommonResponse cr = new CommonResponse();
		if (!StringUtils.isEmpty(ids)) {
			if (ids.length > 0) {
				for (int i = 0; i < ids.length; i++) {
					Long id = Long.parseLong(ids[i]);
					permissionService.delete(id);
				}
			}
		}
		cr.setMessage("删除成功");
		return cr;
	}

	/**
	 * 查看权限
	 */
	@GetMapping(value = "/get/{id}")
	public CommonResponse get(@PathVariable("id") Long id) {
		CommonResponse cr = new CommonResponse();
		Optional<Permission> permission = permissionService.findOne(id);
		if (permission.isPresent()) {
			cr.setData(clearCascadeJSON.format(permission.get()).toJSON());
			cr.setMessage("查找成功");
		} else {
			cr.setMessage("查找失败");
		}
		return cr;
	}
	
	/**
	 * 查看资源权限
	 */
	@GetMapping(value = "/getResourcePermission/{id}")
	public CommonResponse getResourcePermission(@PathVariable("id") Long id) {
		CommonResponse cr = new CommonResponse();
		Optional<RoleResourcePermission> roleResourcePermission = permissionService.getResourcePermission(id);
		if (roleResourcePermission.isPresent()) {
			cr.setData(ClearCascadeJSON.get()
					.addRetainTerm(RoleResourcePermission.class, "id", "name", "role","resourceId","permissionIds","createdAt")
					.addRetainTerm(Role.class, "id", "name")
					.format(roleResourcePermission.get()).toJSON());
			cr.setMessage("查找成功");
		} else {
			cr.setMessage("查找失败");
		}
		return cr;
	}
	
	/**
	 * 删除资源权限
	 * 
	 * @param request
	 *            请求
	 * @param user
	 *            用户实体类
	 * @return cr
	 */
	@DeleteMapping("/deleteResourcePermission/{ids}")
	public CommonResponse deleteResourcePermission(@PathVariable("ids") String[] ids) {
		CommonResponse cr = new CommonResponse();
		if (!StringUtils.isEmpty(ids)) {
			if (ids.length > 0) {
				for (int i = 0; i < ids.length; i++) {
					Long id = Long.parseLong(ids[i]);
					permissionService.deleteResourcePermission(id);
				}
			}
		}
		cr.setMessage("删除成功");
		return cr;
	}

}
