package com.ruoyi.wcs.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.wcs.domain.WcsDeviceBaseInfo;
import com.ruoyi.wcs.domain.WcsFreshAirThtbRealtion;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * WCS新风温湿度传感器关联关系Mapper接口
 *
 * @author yangjie
 * @date 2023-03-31
 */
public interface WcsFreshAirThtbRealtionMapper extends BaseMapper<WcsFreshAirThtbRealtion> {


    /**
     * 查询WCS新风温湿度传感器关联关系列表
     *
     * @param wcsFreshAirThtbRealtion WCS新风温湿度传感器关联关系
     * @return WCS新风温湿度传感器关联关系集合
     */
    List<WcsFreshAirThtbRealtion> select(@Param("ew") QueryWrapper<WcsFreshAirThtbRealtion> wcsFreshAirThtbRealtion);

    /**
     * 获取设备信息下拉列表
     * @return WCS设备基本信息集合
     */
    List<WcsDeviceBaseInfo> listTypeDeviceInfos(String deviceType);
}
