package com.ruoyi.wms.basics.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
 * 仓库基本信息对象
 *
 * @author ruoyi
 * @date 2023-01-30
 */
@Data
@Accessors(chain = true)
@TableName("wms_warehouse_info")
public class Warehouse extends BaseEntity {


    /**
     * 编号
     */
    private String id;

    /**
     * 仓库编码
     */
    @Excel(name = "仓库编码")
    private String warehouseCode;

    /**
     * 仓库名称
     */
    @Excel(name = "仓库名称")
    private String warehouseName;

    /**
     * 仓库容量(m³)
     */
    @Excel(name = "仓库容量(m³)")
    private BigDecimal totalCapacity;

    /**
     * 可用容量(m³)
     */
//    @Excel(name = "可用容量(m³)")
    private BigDecimal availableCapacity;


}
