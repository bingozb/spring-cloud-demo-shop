package com.bhnote.component;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.Collection;

/**
 * @author bingo
 * @date 2022/1/6
 */
public interface CustomMapper<T> extends BaseMapper<T> {

    /**
     * 批量插入数据
     * 需要批量插入时，mapper接口继承CustomMapper，即可拥有这个批量插入的方法；
     * 仅适用于 mysql！
     *
     * @param entityList 实体列表
     * @return 影响行数
     */
    Integer insertBatchSomeColumn(Collection<T> entityList);
}
