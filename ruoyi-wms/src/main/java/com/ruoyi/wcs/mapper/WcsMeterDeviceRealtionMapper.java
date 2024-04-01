package com.ruoyi.wcs.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.wcs.domain.WcsMeterDeviceRealtion;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * WCS电表设备关联关系Mapper接口
 *
 * @author ruoyi
 * @date 2023-05-10
 */
public interface WcsMeterDeviceRealtionMapper extends BaseMapper<WcsMeterDeviceRealtion> {

    /**
     * 查询已被电表关联的设备
     * @return
     */
    List<String> selectMeterRelatedDevices();

    List<WcsMeterDeviceRealtion> select(@Param("ew") QueryWrapper<WcsMeterDeviceRealtion> queryWrapper);
}
