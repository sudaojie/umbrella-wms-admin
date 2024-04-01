package com.ruoyi.wms.move.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 移库单详情货物对象
 *
 * @author nf
 * @date 2023-03-01
 */
@Data
@Accessors(chain = true)
@TableName("wms_move_detail_goods")
public class WmsMoveDetailGoods extends BaseEntity{


    /** 主键 */
    private String id;

    /** 移库单号 */
    @Excel(name = "移库单号")
    private String moveCode;

    /** 货物编码 */
    @Excel(name = "货物编码")
    private String goodsCode;

    /** 货物名称 */
    @Excel(name = "货物名称")
    private String goodsName;

    /** 规格型号 */
    @Excel(name = "规格型号")
    private String model;

    /** 计量单位 */
    @Excel(name = "计量单位")
    private String measureUnit;

    /** 批次 */
    @Excel(name = "批次")
    private String charg;

    /** 货物唯一码 */
    @Excel(name = "货物唯一码")
    private String onlyCode;

    /** 机件号 */
    @Excel(name = "机件号")
    private String mpCode;

    /** 移出仓库编号 */
    @Excel(name = "移出仓库编号")
    private String outWarehouseCode;

    /** 移出仓库名称 */
    @Excel(name = "移出仓库名称")
    private String outWarehouseName;

    /** 移出库区编号 */
    @Excel(name = "移出库区编号")
    private String outAreaCode;

    /** 移出库区名称 */
    @Excel(name = "移出库区名称")
    private String outAreaName;

    /** 移出库位编号 */
    @Excel(name = "移出库位编号")
    private String outLocationCode;

    /** 移出库位名称 */
    @Excel(name = "移出库位名称")
    private String outLocationName;

    /** 移入仓库编号 */
    @Excel(name = "移入仓库编号")
    private String inWarehouseCode;

    /** 移入仓库名称 */
    @Excel(name = "移入仓库名称")
    private String inWarehouseName;

    /** 移入库区编号 */
    @Excel(name = "移入库区编号")
    private String inAreaCode;

    /** 移入库区名称 */
    @Excel(name = "移入库区名称")
    private String inAreaName;

    /** 移入库位编号 */
    @Excel(name = "移入库位编号")
    private String inLocationCode;

    /** 移入库位名称 */
    @Excel(name = "移入库位名称")
    private String inLocationName;

    /** 托盘编号 */
    @Excel(name = "托盘编号")
    private String trayCode;


}
