package com.jiezi.emotion.common;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.PropertyPreFilter;
import com.alibaba.fastjson.serializer.SerializeFilter;
import com.alibaba.fastjson.serializer.SerializeWriter;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.serializer.ValueFilter;
import com.jiezi.emotion.common.annotation.ClearCascade;
import com.jiezi.emotion.common.annotation.Extract;
import com.jiezi.emotion.common.annotation.ExtractRenameTerm;
import com.jiezi.emotion.common.annotation.FilterRetainTerm;




/**
 * 该类返回JSONString,目标格式化掉JPA中级联的约定属性,同时扩展萃取二级属性到一级对象中<br/>
 * 同时本类还支持改变结果属性的列头{@link #addRenameTerm(String, String)}增加改名关系，<b>效率不高，且会将所有同名的属性修改掉慎用！</b>
 * 注意{@link #addFilterTerm(Class, String...)}可以增加排除关系，{@link #addRetainTerm(Class, String...)}可以增加保留关系，
 * <b>但是执行顺序是先排除后保留，如果同时设置相同属性既排除又保留会执行排除.</b>
 * {@link #addExtractTerm(String, String)}仅对{@linkplain #toJSON()}方法有效<br/>
 * 本方法还支持将DATE格式转化成Timestamp。默认转化格式为yyyy-mm-dd HH:mm:ss，如需打开{@link #dateToTimestamp()}}
 * @author ZhouYong
 *
 */
public class ClearCascadeJSON {
//	private static Log LOG = Log.getLog(ClearCascadeJSON.class);
	/**
	 * 排除的关系列表格式为A.class->{"prop1","prop2"}
	 */
	private Map<Class<?>, String[]> excludes = new HashMap<>();
	
	/**
	 * 保留的关系列表格式为A.class->{"prop1","prop2"}
	 */
	private Map<Class<?>, String[]> includes = new HashMap<>();
	
	/**
	 * 约定的重名列表格式为A.class->{"prop1","prop2"}
	 */
	private  HashMap<String, String>  renameItems = new HashMap<>();
	
	/**
	 * 萃取列表格式为username->{'user','name'}
	 */
	private Map<String, String[]> extracts = new HashMap<>();
	
	/**
	 * 萃取列表的计数器，用来统计同一个一级属性是否有多次萃取
	 */
	private Map<String, Integer> extractsCount = new HashMap<>();

	private Object javaObject;

	private MyJsonFilter myJsonFilter = new MyJsonFilter();
	
	/**
	 * 默认保留原属性
	 */
	private boolean extractRetain = true;//新增属性后是否保留原属性
	
	/**
	 * 默认打开日期格式化 date自动格式化成 yyyy-mm-dd hh:mm:ss
	 */
	private boolean useDateFormat = true;
	
	
	private boolean isAnnotation = false;//如果走过注解判断将不会继续走

	private ClearCascadeJSON() {
	}

	/**
	 * 获取工具类实例
	 * 
	 * @return ClearCascadeJSON
	 */
	public static ClearCascadeJSON get() {
		return new ClearCascadeJSON();
	}

	/**
	 * 增加一组过滤条件
	 * 
	 * @param classObj
	 *            需要过滤的属性所属对象Class名利如A.class
	 * @param items
	 *            需要过滤的属性，可以使多个
	 * @return ClearCascadeJSON
	 */
	public ClearCascadeJSON addFilterTerm(Class<?> classObj, String... items) {
		if (items != null) {
			String[] old = excludes.get(classObj);
			if(old != null){
				String[] newArr = new String[old.length + items.length];
				System.arraycopy(old, 0,newArr, 0,old.length);
				System.arraycopy(items, 0,newArr, old.length,items.length);
				excludes.put(classObj, newArr);
			}else{
				excludes.put(classObj, items);
			}
		}
		return this;
	}
	
	/**
	 * 萃取后删除原属性
	 * <b>该方法仅对设置了萃取属性有效</b>
	 * @return ClearCascadeJSON
	 */
	public ClearCascadeJSON extractRemove(){
		extractRetain = false;
		return this;
	}
	
	/**
	 * 修改默认的日期格式化方式，将日志格式化成时间戳.
	 * @return ClearCascadeJSON
	 */
	public ClearCascadeJSON dateToTimestamp(){
		useDateFormat = false;
		return this;
	}
	
	/**
	 * 萃取后保留原属性
	 * <b>该方法仅对设置了萃取属性有效</b>
	 * @return ClearCascadeJSON
	 */
	public ClearCascadeJSON extractRetain(){
		extractRetain = true;
		return this;
	}
	
	
	/**
	 * 萃取一个属性，将级联的某个属性作为上级的属性,暂时仅支持二级属性a.b不支持3级a.b.c
	 * <b>该方法仅对输出格式为JSON有效</b>
	 * @param finalAtt 目标参数名
	 * @param sourceAttRef 源参数名
	 * @return ClearCascadeJSON
	 */
	public ClearCascadeJSON addExtractTerm(String finalAtt,String sourceAttRef){
		String[] arr = sourceAttRef.split("\\.");
		if(arr.length != 2){
			return this;
		}
		extracts.put(finalAtt, arr);
		Integer count = extractsCount.get(arr[0]);
		if(count == null){
			count = 0;
		}
		count++;
		extractsCount.put(arr[0], count);
		return this;
	}
	
	/**
	 * 增加一组保留条件
	 * @param classObj
	 * 				需要保留的属性所属对象Class名利如A.class
	 * @param items
	 * 				需要保留的属性，可以使多个
	 * @return ClearCascadeJSON
	 */
	public ClearCascadeJSON addRetainTerm(Class<?> classObj, String... items) {
		if (items != null) {
			String[] old = includes.get(classObj);
			if(old != null){
				String[] newArr = new String[old.length + items.length];
				System.arraycopy(old, 0,newArr, 0,old.length);
				System.arraycopy(items, 0,newArr, old.length,items.length);
				includes.put(classObj, newArr);
			}else{
				includes.put(classObj, items);
			}
		}
		return this;
	}
	
	/**
	 * 增加一组改名条件
	 * 
	 * @return ClearCascadeJSON
	 */
	public ClearCascadeJSON addRenameTerm(String oldName, String newName) {
		renameItems.put(oldName, newName);
		return this;
		
	}

	/**
	 * 设置格式化目标对象
	 * @param tar 目标对象
	 * @return ClearCascadeJSON
	 */
	public ClearCascadeJSON format(Object tar) {
		javaObject = tar;
		validAnnotation();
		return this;
	}
	
	public ClearCascadeJSON formatAnnotation(ClearCascade clearCascade){
		if(isAnnotation){
			return this;
		}
		if(clearCascade.dateToTimestamp()){
			dateToTimestamp();
		}
		FilterRetainTerm[] fileters = clearCascade.filter();
		for(FilterRetainTerm term : fileters){
			addFilterTerm(term.classe(), term.attrs());
		}
		FilterRetainTerm[] retains = clearCascade.retain();
		for(FilterRetainTerm term : retains){
			addRetainTerm(term.classe(), term.attrs());
		}
		ExtractRenameTerm[] renames = clearCascade.rename();
		for(ExtractRenameTerm term : renames){
			addRenameTerm(term.old(), term.newValue());
		}
		Extract extract = clearCascade.extract();
		if (extract.items().length > 0) {
			if(extract.extractRemove()){
				extractRemove();
			}else{
				extractRetain();
			}
			for(ExtractRenameTerm term : extract.items()){
				addExtractTerm(term.newValue(),term.old());
			}
		}
		isAnnotation = true;
		return this;
	}
	
	/**
	 * 检查是否包含注解
	 */
	private void validAnnotation() {
		StackTraceElement element = null;
		StackTraceElement[] elements = Thread.currentThread().getStackTrace();
		for (int i = 1; i < elements.length; i++) {
			if (!elements[i].getClassName().equals(ClearCascadeJSON.class.getName())) {
				element = elements[i];
				break;
			}
		}
		Class<?> classT;
		try {
			classT = Class.forName(element.getClassName());

			Method metho = null;
			for (Method method : classT.getDeclaredMethods()) {
				if (method.getName().equals(element.getMethodName())) {
					metho = method;
					break;
				}
			}
			ClearCascade clearCascade = metho.getAnnotation(ClearCascade.class);
			if(clearCascade != null){
				formatAnnotation(clearCascade);
			}
		} catch (Exception e) {
			e.printStackTrace();
//			LOG.warn("反射获取方法名注解失败", e);
		}
	}

	/**
	 * 最终操作-转换成JSONString
	 * @return jsonstring
	 */
	public String toJSONString(){
		if(javaObject == null){
			return null;
		}
		myJsonFilter.setExcludes(excludes);
		myJsonFilter.setIncludes(includes);
		if(javaObject instanceof Collection){
			return toJSONString(javaObject,myJsonFilter,
					SerializerFeature.WriteNullStringAsEmpty,
					SerializerFeature.WriteMapNullValue);
		}
		return toJSONString(javaObject,myJsonFilter,
				SerializerFeature.WriteNullStringAsEmpty,
				SerializerFeature.WriteMapNullValue);
	}
	
	/**
	 * 最终操作转换成JSON
	 * @return JSON
	 */
	public JSON toJSON(){
		String result = toJSONString();
		if(result == null) return null;
		JSON jsonResult = (JSON) JSON.parse(result);
		if(extracts.size() != 0){
			try{
				for(String key : extracts.keySet()){
					String ref[] = extracts.get(key);
					if(javaObject instanceof Collection){
						JSONArray resultArr = (JSONArray) jsonResult;
						for(Object obj : resultArr){
							extractAttribute(key, ref, (JSONObject)obj);
						}
					}else{
						extractAttribute(key, ref, (JSONObject)jsonResult);
					}
				}
			}catch(Exception e){
				e.printStackTrace();
//				LOG.error("萃取异常", e);
			}
		}
		if(renameItems.size() != 0){
			if(javaObject instanceof Collection){
				JSONArray resultArr = (JSONArray) jsonResult;
				JSONArray jsonArr = new JSONArray();
				renameAttribute(resultArr , jsonArr);
				return jsonArr;
			}else{
				JSONObject jsonObject = new JSONObject();
				renameAttribute(  (JSONObject)jsonResult,jsonObject);
				return jsonObject;
			}
		}
		return jsonResult;
	}
	
	private void extractAttribute(String finalAtt,String ref[],JSONObject item){
		//这里增加小逻辑，有的时候需要从数组里的第一个元素萃取属性
		try{
			JSONObject jsonObject = null;
			Object obj = item.get(ref[0]);
			if(obj instanceof JSONObject){
				jsonObject = (JSONObject)obj;
			}else{
				//否则获取数组的第一个
				jsonObject = ((JSONArray)obj).getJSONObject(0);
			}
			
			item.put(finalAtt, jsonObject.get(ref[1]));
			if(!extractRetain){
				Integer count = extractsCount.get(ref[0]);
				if(count == 1){
					item.remove(ref[0]);
				}else{
					count--;
					extractsCount.put(ref[0], count);
				}
					
			}
		}catch(Exception e){
			e.printStackTrace();
//			LOG.error("萃取异常", e);
		}
	}
	private void renameAttribute(JSONObject item,JSONObject newObj){
		 for(String key : item.keySet()){
			 Object obj = item.get(key);
			 
			 if(obj instanceof JSONObject){
				 //jsonobj
				 JSONObject newJson = new JSONObject();
				 newObj.put(getNewKey(key), newJson);
				 renameAttribute((JSONObject)obj,newJson);
			 }
			 else if(obj instanceof JSONArray){
				 JSONArray jsonArr = new JSONArray();
				 newObj.put(getNewKey(key), jsonArr);
				 renameAttribute((JSONArray)obj,jsonArr);
			 }else{
				 newObj.put(getNewKey(key), obj);
			 }
			 
		 }
	}
	
	private String getNewKey(String old){
		if(renameItems.containsKey(old)){
			return renameItems.get(old);
		 }
		return old;
	}
	
	private void renameAttribute(JSONArray item,JSONArray newArr){
		 for(Object obj : item){
			 if(obj instanceof JSONObject){
				 //jsonobj
				 JSONObject newJsonObject = new JSONObject();
				 newArr.add(newJsonObject);
				 renameAttribute((JSONObject)obj , newJsonObject);
			 }
			 if(obj instanceof JSONArray){
				 JSONArray newJsonArr = new JSONArray();
				 newArr.add(newJsonArr);
				 renameAttribute((JSONArray)obj , newJsonArr);
			 }
		 }
	}
	

	public final String toJSONString(Object object, SerializeFilter filter, SerializerFeature... features) {
		if (useDateFormat) {
			return JSON.toJSONString(object, filter, features);
		}
		SerializeWriter out = new SerializeWriter();

		try {
			JSONSerializer serializer = new JSONSerializer(out);
			for (com.alibaba.fastjson.serializer.SerializerFeature feature : features) {
				serializer.config(feature, true);
			}
			setFilter(serializer, filter);
			serializer.write(object);
			return out.toString();
		} finally {
			out.close();
		}
	}

	private void setFilter(JSONSerializer serializer, SerializeFilter filter) {
		if (filter == null) {
			return;
		}

		if (filter instanceof PropertyPreFilter) {
			serializer.getPropertyPreFilters().add((PropertyPreFilter) filter);
		}

		if (filter instanceof com.alibaba.fastjson.serializer.NameFilter) {
			serializer.getNameFilters().add((com.alibaba.fastjson.serializer.NameFilter) filter);
		}

		if (filter instanceof ValueFilter) {
			serializer.getValueFilters().add((ValueFilter) filter);
		}

		if (filter instanceof com.alibaba.fastjson.serializer.PropertyFilter) {
			serializer.getPropertyFilters().add((com.alibaba.fastjson.serializer.PropertyFilter) filter);
		}

		if (filter instanceof com.alibaba.fastjson.serializer.BeforeFilter) {
			serializer.getBeforeFilters().add((com.alibaba.fastjson.serializer.BeforeFilter) filter);
		}

		if (filter instanceof com.alibaba.fastjson.serializer.AfterFilter) {
			serializer.getAfterFilters().add((com.alibaba.fastjson.serializer.AfterFilter) filter);
		}
	}

	public static void main(String[] args) {
  
	}	
}

class MyJsonFilter implements PropertyPreFilter {

	 private Map<Class<?>, String[]> includes = null;
	private Map<Class<?>, String[]> excludes = null;

	static {
		JSON.DEFAULT_GENERATE_FEATURE |= SerializerFeature.DisableCircularReferenceDetect.getMask();
	}

	public MyJsonFilter() {

	}

	/*
	 * public MyJsonFilter(Map<Class<?>, String[]> includes) { super();
	 * this.includes = includes; }
	 */

	public boolean apply(JSONSerializer serializer, Object source, String name) {

		// 对象为空。直接放行
		if (source == null) {
			return true;
		}

		// 获取当前需要序列化的对象的类对象
		Class<?> clazz = source.getClass();

		// 无需序列的对象、寻找需要过滤的对象，可以提高查找层级
		// 找到不需要的序列化的类型
		for (Map.Entry<Class<?>, String[]> item : this.excludes.entrySet()) {
			// isAssignableFrom()，用来判断类型间是否有继承关系
			if (item.getKey().isAssignableFrom(clazz)) {
				String[] strs = item.getValue();

				// 该类型下 此 name 值无需序列化
				if (isHave(strs, name)) {
					return false;
				}
			}
		}

		 
		// 需要序列的对象集合为空 表示 全部需要序列化
		if (this.includes.isEmpty()) {
			return true;
		}
		
		long sourceClassFilterCount = includes
									  .entrySet()
									  .parallelStream()
									  .filter(i -> i.getKey().isAssignableFrom(clazz))
									  .count();
		if(sourceClassFilterCount == 0){
			return true;
		}
		// 需要序列的对象 // 找到不需要的序列化的类型
		for (Map.Entry<Class<?>, String[]> item : this.includes.entrySet()) { // isAssignableFrom()，用来判断类型间是否有继承关系
			if (item.getKey().isAssignableFrom(clazz)) {
				String[] strs = item.getValue(); // 该类型下 此 name 值无需序列化
				if (isHave(strs, name)) {
					return true;
				}
			}
		}
		return false;
	}

	/*
	 * 此方法有两个参数，第一个是要查找的字符串数组，第二个是要查找的字符或字符串
	 */
	public static boolean isHave(String[] strs, String s) {

		for (int i = 0; i < strs.length; i++) {
			// 循环查找字符串数组中的每个字符串中是否包含所有查找的内容
			if (strs[i].equals(s)) {
				// 查找到了就返回真，不在继续查询
				return true;
			}
		}

		// 没找到返回false
		return false;
	}

	 
	public Map<Class<?>, String[]> getIncludes() {
		return includes;
	}

	public void setIncludes(Map<Class<?>, String[]> includes) {
		this.includes = includes;
	}
	 

	public Map<Class<?>, String[]> getExcludes() {
		return excludes;
	}

	public void setExcludes(Map<Class<?>, String[]> excludes) {
		this.excludes = excludes;
	}
	

}