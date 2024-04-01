package com.ruoyi.wcs.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.wcs.domain.WcsDeviceBaseInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * WCS设备基本信息Mapper接口
 *
 * @author yangjie
 * @date 2023-02-24
 */
public interface WcsDeviceBaseInfoMapper extends BaseMapper<WcsDeviceBaseInfo> {


    /**
     * 查询WCS设备基本信息列表
     *
     * @param wcsDeviceBaseInfo WCS设备基本信息
     * @return WCS设备基本信息集合
     */
    List<WcsDeviceBaseInfo> select(@Param("ew") QueryWrapper<WcsDeviceBaseInfo> wcsDeviceBaseInfo);

    /**
     * 获取传感器信息编号集合
     * @return 传感器设备编号集合
     */
    List<String> getSensorInfoIds();

    /**
     * 获取网关可绑定设备信息编号集合
     * @return 设备编号集合
     */
    List<String> getGateWayDeviceInfoIds();

    /**
     * 获取指定网关下指定设备地址的设备信息
     * @param gateWayDeviceNo
     * @param deviceAddress
     * @param deviceType
     * @return
     */
    WcsDeviceBaseInfo selectGateWayCollect(@Param("gateWayDeviceNo") String gateWayDeviceNo, @Param("deviceType") String deviceType, @Param("deviceAddress") String deviceAddress);

}
