package com.ruoyi.wcs.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.wcs.domain.WcsOperateTask;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * WCS任务信息Mapper接口
 *
 * @author yangjie
 * @date 2023-02-28
 */
public interface WcsOperateTaskMapper extends BaseMapper<WcsOperateTask> {


    /**
     * 查询WCS任务信息列表
     *
     * @param wcsOperateTask WCS任务信息
     * @return WCS任务信息集合
     */
    List<WcsOperateTask> select(@Param("ew") QueryWrapper<WcsOperateTask> wcsOperateTask);

}
