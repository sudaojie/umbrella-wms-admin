package com.ruoyi.wcs.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.wcs.domain.WcsDeviceEarlyWarningInfo;
import com.ruoyi.wcs.domain.dto.WcsDeviceEarlyWarningFormDto;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 设备预警信息Mapper接口
 *
 * @author hewei
 * @date 2023-04-17
 */
public interface WcsDeviceEarlyWarningInfoMapper extends BaseMapper<WcsDeviceEarlyWarningInfo> {


    /**
     * 查询设备预警信息列表
     *
     * @param wcsDeviceEarlyWarningInfo 设备预警信息
     * @return 设备预警信息集合
     */
    List<WcsDeviceEarlyWarningFormDto> select(@Param("ew") QueryWrapper<WcsDeviceEarlyWarningInfo> wcsDeviceEarlyWarningInfo);

    /**
     * 设备预警信息详情
     *
     * @param id 编号
     * @return WcsDeviceEarlyWarningFormDto WcsDeviceEarlyWarningFormDto
     */
    WcsDeviceEarlyWarningFormDto getInfoById(@Param("id") String id);

}
