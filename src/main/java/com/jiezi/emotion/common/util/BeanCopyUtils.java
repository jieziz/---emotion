package com.jiezi.emotion.common.util;

import java.beans.PropertyDescriptor;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

/**
 * 为简化开发，特提供bean拷贝类，将a bean中不为null的元素拷贝到b中。
 * <br/>可以增加过滤字段，为目标bean数据安全，优先不过滤原则。
 * @author ZhouYong
 *
 */
public class BeanCopyUtils {

	/**
	 * 格式化非空属性复制给target
	 * @param source
	 * @param target
	 * @param ignoreProperties
	 */
	public static void copyNotEmpty(Object source, Object target, String...ignoreProperties){
		BeanWrapper src = new BeanWrapperImpl(source);
		List<String> ignoreList = null;
		if(ignoreProperties != null){
			ignoreList = Arrays.asList(ignoreProperties);
	    }
		List<String> ignoreListRe = ignoreList;
	    String[] arr = Arrays.stream(
	    		 src
	    	    .getPropertyDescriptors())
	    		.filter(pd -> {
	    			if(ignoreListRe != null && ignoreListRe.contains(pd.getName())) return true;
	    			Object value = src.getPropertyValue(pd.getName());
	    			return value == null || (value instanceof Collection && ((Collection<?>)value).size() == 0);
	    		 })
	    		.flatMap(pd -> Arrays.asList(pd.getName()).stream())
	    		.collect(Collectors.toList())
	    		.toArray(new String[]{});
	    
	    BeanUtils.copyProperties(source, target, arr);
	}
	
	
	
	
	
	 /**
     * 将目标源中不为空的字段过滤，将数据库中查出的数据源复制到提交的目标源中
     *
     * @param source 用id从数据库中查出来的数据源
     * @param target 提交的实体，目标源
     */
    public static void copyNullProperties(Object source, Object target) {
        BeanUtils.copyProperties(source, target, getNoNullProperties(target));
    }

    /**
     * @param target 目标源数据
     * @return 将目标源中不为空的字段取出
     */
    private static String[] getNoNullProperties(Object target) {
        BeanWrapper srcBean = new BeanWrapperImpl(target);
        PropertyDescriptor[] pds = srcBean.getPropertyDescriptors();
        Set<String> noEmptyName = new HashSet<>();
        for (PropertyDescriptor p : pds) {
            Object value = srcBean.getPropertyValue(p.getName());
            if (value != null) noEmptyName.add(p.getName());
        }
        String[] result = new String[noEmptyName.size()];
        return noEmptyName.toArray(result);
    }
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
