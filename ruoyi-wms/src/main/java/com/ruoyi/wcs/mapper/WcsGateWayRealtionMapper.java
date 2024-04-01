package com.ruoyi.wcs.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.wcs.domain.WcsDeviceBaseInfo;
import com.ruoyi.wcs.domain.WcsGateWayRealtion;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * WCS网关采集器关联关系Mapper接口
 *
 * @author yangjie
 * @date 2023-03-31
 */
public interface WcsGateWayRealtionMapper extends BaseMapper<WcsGateWayRealtion> {


    /**
     * 查询WCS网关采集器关联关系列表
     *
     * @param wcsGateWayRealtion WCS网关采集器关联关系
     * @return WCS网关采集器关联关系集合
     */
    List<WcsGateWayRealtion> select(@Param("ew") QueryWrapper<WcsGateWayRealtion> wcsGateWayRealtion);

}
