package com.ruoyi.wms.check.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 库存盘点调整详情货物对象
 *
 * @author nf
 * @date 2023-03-23
 */
@Data
@Accessors(chain = true)
@TableName("wms_warehouse_check_adjust_goods")
public class CheckAdjustGoods extends BaseEntity{


    /** 主键ID */
    private String id;

    /** 盘点调整详情主键 */
    private String checkAdjustDetail;

    /** 货物编码 */
    @Excel(name = "货物编码")
    private String goodsCode;

    /** 货物名称 */
    @Excel(name = "货物名称")
    private String goodsName;

    /** 机件号 */
    @Excel(name = "机件号")
    private String partsCode;

    /** 货物唯一码 */
    @Excel(name = "货物唯一码")
    private String onlyCode;

    /** 盘点托盘编号 */
    @Excel(name = "盘点托盘编号")
    private String trayCode;

    /** 盘点托盘名称 */
    private String trayName;

    /** 盘点库位编号 */
    @Excel(name = "盘点库位编号")
    private String locationCode;

    /** 规格型号 */
    @Excel(name = "规格型号")
    private String model;

    /** 计量单位 */
    @Excel(name = "计量单位")
    private String measureUnit;

    /** 盘亏数量 */
    @Excel(name = "盘亏数量")
    private String lossNum;

    /** 盘盈数量 */
    @Excel(name = "盘盈数量")
    private String profitNum;


}
