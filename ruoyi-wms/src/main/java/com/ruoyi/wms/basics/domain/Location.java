package com.ruoyi.wms.basics.domain;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.List;

/**
 * 库位基本信息对象
 *
 * @author ruoyi
 * @date 2023-01-31
 */
@Data
@Accessors(chain = true)
@TableName("wms_warehouse_location")
public class Location extends BaseEntity {


    /**
     * 编号
     */
    private String id;

    /**
     * 库位编码
     */
    @Excel(name = "库位编码")
    private String locationCode;

    /**
     * 库位名称
     */
    @Excel(name = "库位名称")
    private String locationName;

    /**
     * 锁定状态（0-未锁定 1-已锁定）
     */
    private String lockStatus;

    /**
     * 当前库位上的托盘编码
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String trayCode;

    /**
     * 所属仓库编号
     */
    @Excel(name = "所属仓库编号", type = Excel.Type.EXPORT)
    private String warehouseId;

    /**
     * 所属库区编号
     */
    @Excel(name = "所属库区编号", type = Excel.Type.EXPORT)
    private String areaId;


    /**
     * 库位容量(m³)
     */
    @Excel(name = "库位容量(m³)")
    private BigDecimal totalCapacity;

    /**
     * 可容重量（KG）
     */
    @Excel(name = "可容重量(KG)")
    private BigDecimal tolerableWeight;

    /**
     * 第几排
     */
    @Excel(name = "排数")
    private Integer platoon;

    /**
     * 第几层
     */
    @Excel(name = "层数")
    private Integer layer;
    /**
     * 第几行
     */
    @Excel(name = "列数")
    private Integer columnNum;

    /**
     * 库位类型(1.母库位 2.子库位)
     */
    private String locationType;

    /**
     * 库位朝向(1.左侧  2.右侧)
     */
    private String locationArrow;

    /**
     * 排序值(每个区的排序值不能重复)
     */
    @Excel(name = "排序值")
    private Integer orderNum;

    /**
     * 启用状态(0:启用  1:禁用)
     */
    private String enableStatus;

    /**
     * 所属库区类型
     */
    @TableField(exist = false)
    private String areaType;

    /**
     * 库位编码集合
     */
    @TableField(exist = false)
    private List<String> locationCodes;

    /**
     * 理货区托盘是否存在
     */
    @TableField(exist = false)
    private Boolean flag;

}
