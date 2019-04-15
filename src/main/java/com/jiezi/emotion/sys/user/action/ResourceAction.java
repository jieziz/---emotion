package com.jiezi.emotion.sys.user.action;

import java.util.List;
import java.util.Optional;

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
import com.jiezi.emotion.sys.user.entity.Resource;
import com.jiezi.emotion.sys.user.entity.RoleResourcePermission;
import com.jiezi.emotion.sys.user.service.ResourceService;

/**
 * Resource
 * 
 * @author zhangliang
 * @date 2017年4月21日 上午10:16:54
 */
@RestController
@RequestMapping("/sys/resource")
public class ResourceAction {

	@Autowired
	private ResourceService resourceService;

	private ClearCascadeJSON clearCascadeJSON;

	{
		clearCascadeJSON = ClearCascadeJSON.get().addRetainTerm(Resource.class, "identity", "url", "icon", "name",
				"children", "isShow", "parentId", "component", "type", "orderNo", "meta", "id", "createdAt", "iswitch");
	}

	/**
	 * 分页查询所有资源
	 * 
	 * @param request
	 *            请求
	 * @return cr
	 */
	@GetMapping(value = "/pages")
	public CommonResponse pages(Resource resource, PageParameter page) {
		CommonResponse cr = new CommonResponse(
				clearCascadeJSON.format(resourceService.getPageResource(page, resource)).toJSON());
		return cr;
	}

	/**
	 * 查询所有资源,同时传入角色id，对角色已拥有的资源，做标记
	 * 
	 * @param request
	 *            请求
	 * @return cr
	 */
	@GetMapping(value = "/all")
	public CommonResponse all(Long id) {
		CommonResponse cr = new CommonResponse();
		List<RoleResourcePermission> roleResourcePermissionList = resourceService.getRoleToResource(id);
		cr.setData(ClearCascadeJSON.get().addRetainTerm(RoleResourcePermission.class, "id", "name", "iswitch").format(roleResourcePermissionList).toJSON());
		cr.setMessage("查询成功");
		return cr;
	}

	/**
	 * 查看资源
	 */
	@GetMapping(value = "/get/{id}")
	public CommonResponse getUser(@PathVariable("id") Long id) {
		CommonResponse cr = new CommonResponse();
		Optional<Resource> resource = resourceService.findOne(id);
		if (resource.isPresent()) {
			cr.setData(clearCascadeJSON.format(resource.get()).toJSON());
			cr.setMessage("查找成功");
		} else {
			cr.setMessage("查找失败");
		}
		return cr;
	}

	/**
	 * 新增资源
	 * 
	 * @param request
	 *            请求
	 * @param user
	 *            用户实体类
	 * @return cr
	 */
	@PostMapping("/add")
	public CommonResponse add(Resource resource) {
		CommonResponse cr = new CommonResponse();
		if (resource.getId() != null) {
			Optional<Resource> oldResource = resourceService.findOne(resource.getId());
			resourceService.update(resource, oldResource.get());
			cr.setMessage("修改成功");
		} else {
			resourceService.save(resource);
			cr.setMessage("添加成功");
		}
		return cr;
	}

	/**
	 * 删除资源
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
					resourceService.delete(id);
				}
			}
		}
		cr.setMessage("删除成功");
		return cr;
	}

}
