package com.jiezi.emotion.sys.user.dao;




import java.util.List;
import java.util.Set;

import com.jiezi.emotion.base.BaseRepository;
import com.jiezi.emotion.sys.user.entity.Resource;

/**
 *  ResourceDao
 * @author zhangliang
 **/
public interface ResourceDao extends BaseRepository<Resource, Long> {
	/**
	 * 取出parentid《1的一级菜单，并排序
	 * 
	 * @param ids menu ids
	 * @param parentId
	 * @return list
	 */
	public List<Resource> findByIdInAndParentIdLessThanOrderByOrderNo(
            Set<Long> ids, Long parentId);

	/**
	 * 取出parentid《1的一级菜单，并排序
	 *
	 * @param parentId 父级id
	 * @return list
	 */
	public List<Resource> findByParentIdLessThanOrderByOrderNo(Long parentId);

	/**
	 * 取出ids菜单
	 * @param ids menuids
	 * @param isshow true 未删除，false已删除
	 * @return list
	 */
	public List<Resource> findByIdInAndIsShowOrderByOrderNo(Set<Long> ids, Boolean isshow);
	
	/**
	 * 通过name查找
	 * @param name 菜单名字
	 * @return menu
	 */
	public Resource findByName(String name);


}
