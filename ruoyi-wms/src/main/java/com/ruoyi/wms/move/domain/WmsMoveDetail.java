package com.ruoyi.wms.move.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 移库单详情对象 wms_move_detail
 *
 * @author nf
 * @date 2023-03-01
 */
@Data
@Accessors(chain = true)
@TableName("wms_move_detail")
public class WmsMoveDetail extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键 */
    private String id;

    /** 移库单号 */
    @Excel(name = "移库单号")
    private String moveCode;

    /** 移库状态;0-未移库,1-移库中,2-已完成 */
//    @Excel(name = "移库状态")
    private String moveStatus;
    /** 人工状态 0-未执行；1-已执行 */
    private String manMadeStatus;

    /** 移出库位托盘编码 */
    private String outTrayCode;

    /** 移出仓库编号 */
    @Excel(name = "移出仓库编号")
    private String outWarehouseCode;

    /** 移出仓库名称 */
    private String outWarehouseName;

    /** 移出库区编号 */
    @Excel(name = "移出库区编号")
    private String outAreaCode;

    /** 移出库区名称 */
    private String outAreaName;

    /** 移出库位编号 */
    @Excel(name = "移出库位编号")
    private String outLocationCode;

    /** 移出库位名称 */
    private String outLocationName;

    /** 移入仓库编号 */
    @Excel(name = "移入仓库编号")
    private String inWarehouseCode;

    /** 移入仓库名称 */
    private String inWarehouseName;

    /** 移入库区编号 */
    @Excel(name = "移入库区编号")
    private String inAreaCode;

    /** 移入库区名称 */
    private String inAreaName;

    /** 移入库位编号 */
    @Excel(name = "移入库位编号")
    private String inLocationCode;

    /** 移入库位名称 */
    private String inLocationName;

    /** 移入库位托盘编码 */
    private String inTrayCode;



}
