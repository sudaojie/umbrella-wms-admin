package com.ruoyi.wms.wcstask.mapper;

import java.util.List;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ruoyi.wms.wcstask.domain.Waittask;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * 启用AGV时，wcs等待执行任务列Mapper接口
 *
 * @author ruoyi
 * @date 2023-02-24
 */
public interface WaittaskMapper extends BaseMapper<Waittask> {


    /**
     * 查询启用AGV时，wcs等待执行任务列列表
     *
     * @param waittask 启用AGV时，wcs等待执行任务列
     * @return 启用AGV时，wcs等待执行任务列集合
     */
    List<Waittask> select(@Param("ew") QueryWrapper<Waittask> waittask);

}
