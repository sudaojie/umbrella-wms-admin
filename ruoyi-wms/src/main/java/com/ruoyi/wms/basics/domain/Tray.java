package com.ruoyi.wms.basics.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
 * 托盘基本信息对象
 *
 * @author ruoyi
 * @date 2023-02-02
 */
@Data
@Accessors(chain = true)
@TableName("wms_warehouse_tray")
public class Tray extends BaseEntity {


    /**
     * 编号
     */
    private String id;

    /**
     * 托盘编码
     */
    @Excel(name = "托盘编码")
    private String trayCode;

    /**
     * 托盘名称
     */
//    @Excel(name = "托盘名称")
    private String trayName;

    /**
     * 托盘简称
     */
//    @Excel(name = "托盘简称")
    private String traySimpleName;

    /**
     * 所属仓库
     */
//    @Excel(name = "所属仓库")
    private String warehouseId;

    /**
     * 所属仓库名称
     */
//    @Excel(name = "所属仓库")
    private String warehouseName;

    /**
     * 所属库区
     */
//    @Excel(name = "所属库区")
    private String areaId;
    /**
     * 所属库区名称
     */
//    @Excel(name = "所属库区名称")
    private String areaName;

    /**
     * 所属库位编号
     */
//    @Excel(name = "所属库位编号")
    private String locationId;
    /**
     * 所属库位名称
     */
    private String locationName;

    /**
     * 长(cm)
     */
    @Excel(name = "长(cm)",type = Excel.Type.EXPORT)
    private BigDecimal trayLength;

    /**
     * 宽(cm)
     */
    @Excel(name = "宽(cm)",type = Excel.Type.EXPORT)
    private BigDecimal trayWidth;

    /**
     * 高(cm)
     */
    @Excel(name = "高(cm)",type = Excel.Type.EXPORT)
    private BigDecimal trayHeight;

    /**
     * 体积(m³)
     */
    @Excel(name = "体积(m³)",type = Excel.Type.EXPORT)
    private BigDecimal trayVolume;

    /**
     * 可用容量(m³)
     */
    private BigDecimal trayUsableVolume;

    /**
     * 限重(kg)
     */
    @Excel(name = "限重(kg)",type = Excel.Type.EXPORT)
    private BigDecimal trayLimitWeight;

    /**
     * 可用重量(kg)
     */
    private BigDecimal trayUsableWeight;

    /**
     * 启用状态(0:启用  1:禁用)
     */
    private String enableStatus;
    /**
     * 是否为空盘（0-空的 1-不空）
     */
    private String emptyStatus;
    /**
     * 托盘上的货物编码
     */
    private String goodsCode;

    /**
     * 托盘规格编号
     */
    private String trayModelCode;


}
