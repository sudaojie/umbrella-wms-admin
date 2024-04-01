package com.ruoyi.wms.basics.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.wms.basics.domain.Vehicle;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 车辆基本信息Mapper接口
 *
 * @author ruoyi
 * @date 2023-01-31
 */
public interface VehicleMapper extends BaseMapper<Vehicle> {


    /**
     * 查询车辆基本信息列表
     *
     * @param vehicle 车辆基本信息
     * @return 车辆基本信息集合
     */
    List<Vehicle> select(@Param("ew") QueryWrapper<Vehicle> vehicle);

    /**
     * 验证车牌号唯一性
     *
     * @param vehicle（包含车牌号，id主键）
     * @return
     */
    int checkCode(@Param("object") Vehicle vehicle);

    /**
     * 根据车牌号获取数据
     *
     * @param vehicleNo 车牌号
     * @return
     */
    Vehicle selectDataByCode(String vehicleNo);

    /**
     * 获取车辆信息
     *
     * @return 【{label：xx，value：xx}】
     */
    List<Map> getVehicleList();
}
