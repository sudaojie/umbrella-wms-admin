package com.ruoyi.wms.basics.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 车辆基本信息对象
 *
 * @author ruoyi
 * @date 2023-01-31
 */
@Data
@Accessors(chain = true)
@TableName("wms_warehouse_vehicle")
public class Vehicle extends BaseEntity {


    /**
     * 编号
     */
    private String id;

    /**
     * 车牌号
     */
    @Excel(name = "车牌号")
    private String vehicleNo;

    /**
     * 车辆类型
     */
    @Excel(name = "车辆类型", readConverterExp = "1=轿车,2=货车,3=客车,4=挂车")
    private String vehicleType;

    /**
     * 车辆载重(kg)
     */
    @Excel(name = "车辆载重(kg)")
    private Long vehicleLoad;

    /**
     * 司机姓名
     */
    @Excel(name = "司机姓名")
    private String driverName;

    /**
     * 司机电话
     */
    @Excel(name = "司机电话")
    private String driverPhone;
    /**
     * 所属单位
     */
    @Excel(name = "所属单位")
    private String company;


}
