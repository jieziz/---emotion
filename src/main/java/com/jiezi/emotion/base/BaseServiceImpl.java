package com.jiezi.emotion.base;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.jiezi.emotion.common.util.BeanCopyUtils;

/**
 * <p>抽象service层基类 提供一些简便方法
 * <p/>
 * <p>泛型 ： M 表示实体类型；ID表示主键类型
 */
public abstract class BaseServiceImpl<T extends AbstractEntity<ID>, ID extends Serializable> {

    protected BaseRepository<T, ID> baseRepository;

    @Autowired
    public void setBaseRepository(BaseRepository<T, ID> baseRepository) {
        this.baseRepository = baseRepository;
    }

    /**
     * 保存单个实体
     *
     * @param t 实体
     * @return 返回保存的实体
     */
    public T save(T t) {
        return baseRepository.save(t);
    }
    
    /**
     * 保存实体
     *
     * @param t 实体
     * @return 返回保存的实体
     */
    public List<? extends T> save(Iterable<? extends T> t) {
		return baseRepository.saveAll(t);
    }

    /**
     * 更新单个实体
     *
     * @param t 实体
     * @return 返回更新的实体
     */
    public T update(T t,T ot) {
    	BeanCopyUtils.copyNotEmpty(t,ot,"");
        return baseRepository.save(ot);
    }

    /**
     * 根据主键删除相应实体
     *
     * @param id 主键
     */
    public void delete(ID id) {
        baseRepository.deleteById(id);
    }

    /**
     * 删除实体
     *
     * @param t 实体
     */
    public void delete(T t) {
        baseRepository.delete(t);
    }

    /**
     * 根据主键删除相应实体
     *
     * @param ids 实体
     */
    public void delete(Iterable<? extends T> t) {
		baseRepository.deleteAll(t);
    }


    /**
     * 按照主键查询
     *
     * @param id 主键
     * @return 返回id对应的实体
     */
    public Optional<T> findOne(ID id) {
        return baseRepository.findById(id);
    }
    
    /**
     * 按照主键查询
     *
     * @param id 主键
     * @return 返回id对应的实体
     */
    public T getOne(ID id) {
        return baseRepository.getOne(id);
    }

    /**
     * 实体是否存在
     *
     * @param id 主键
     * @return 存在 返回true，否则false
     */
    public boolean exists(ID id) {
        return baseRepository.existsById(id);
    }

    /**
     * 统计实体总数
     *
     * @return 实体总数
     */
    public long count() {
        return baseRepository.count();
    }

    /**
     * 查询所有实体
     *
     * @return 返回所有实体
     */
    public List<T> findAll() {
        return baseRepository.findAll();
    }

    /**
     * 按照顺序查询所有实体
     *
     * @param sort 排序
     * @return 返回所有实体并排序
     */
    public List<T> findAll(Sort sort) {
        return baseRepository.findAll(sort);
    }

    /**
     * 分页及排序查询实体
     *
     * @param pageable 分页及排序数据
     * @return 返回分页实体
     */
    public Page<T> findAll(Pageable pageable) {
        return baseRepository.findAll(pageable);
    }



}
