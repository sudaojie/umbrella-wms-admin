package com.ruoyi.wms.wcstask.mapper;

import java.util.List;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ruoyi.wms.wcstask.domain.Tasklog;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * 给wcs的任务日志Mapper接口
 *
 * @author ruoyi
 * @date 2023-02-28
 */
public interface TasklogMapper extends BaseMapper<Tasklog> {


    /**
     * 查询给wcs的任务日志列表
     *
     * @param tasklog 给wcs的任务日志
     * @return 给wcs的任务日志集合
     */
    List<Tasklog> select(@Param("ew") QueryWrapper<Tasklog> tasklog);

}
