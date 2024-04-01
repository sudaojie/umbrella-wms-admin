package com.ruoyi.wms.basics.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
 * 库区基本信息对象
 *
 * @author ruoyi
 * @date 2023-01-31
 */
@Data
@Accessors(chain = true)
@TableName("wms_warehouse_area")
public class Area extends BaseEntity {


    /**
     * 编号
     */
    private String id;

    /**
     * 库区编码
     */
    @Excel(name = "库区编码")
    private String areaCode;

    /**
     * 库区名称
     */
    @Excel(name = "库区名称")
    private String areaName;

    /** 库区类型
     * (0存储区，1晾晒区，2理货区) */
    @Excel(name = "库区类型",dictType = "wms_area_type")
    private String areaType;

    /**
     * 移库库位编号
     */
    private String moveLocationCode;

    /**
     * 所属仓库编号
     */
    @Excel(name = "所属仓库编号")
    private String warehouseId;

    /**
     * 库区容量(m³)
     */
    @Excel(name = "库区容量(m³)")
    private BigDecimal totalCapacity;

    /**
     * 可用容量(m³)
     */
//    @Excel(name = "可用容量")
    private BigDecimal availableCapacity;

    /**
     * 空库位数量
     */
    @TableField(exist = false)
    private String emptyNum;


    /**
     * 空托盘数量
     */
    @TableField(exist = false)
    private String emptyTrayNum;

}
