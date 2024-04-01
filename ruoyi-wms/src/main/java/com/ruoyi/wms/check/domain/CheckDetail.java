package com.ruoyi.wms.check.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * 库存盘点详情对象 wms_warehouse_check_detail
 *
 * @author ruoyi
 * @date 2023-03-15
 */
@Data
@Accessors(chain = true)
@TableName("wms_warehouse_check_detail")
public class CheckDetail extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    private String id;

    /**
     * 盘点单号
     */
    @Excel(name = "盘点单号")
    private String checkBillCode;

    /**
     * 盘点货物编码
     */
    @Excel(name = "盘点货物编码")
    private String goodsCode;

    /**
     * 盘点货物名称
     */
    @Excel(name = "盘点货物名称")
    private String goodsName;

    /**
     * 盘点库区编号
     */
    @Excel(name = "盘点库区编号")
    private String areaCode;

    /**
     * 盘点库区名称
     */
    @Excel(name = "盘点库区名称")
    private String areaName;

    /**
     * 盘点库位编号
     */
    @Excel(name = "盘点库位编号")
    private String locationCode;

    /**
     * 盘点库位名称
     */
    @Excel(name = "盘点库位名称")
    private String locationName;

    /**
     * 盘点托盘编号
     */
    @Excel(name = "盘点托盘编号")
    private String trayCode;

    /**
     * 账面数量
     */
    @Excel(name = "账面数量")
    private String curtainNum;

    /**
     * 盘点数量
     */
    @Excel(name = "盘点数量")
    private String checkNum;

    /**
     * 盘亏数量
     */
    @Excel(name = "盘亏数量")
    private String lossNum;

    /**
     * 盘盈数量
     */
    @Excel(name = "盘盈数量")
    private String profitNum;

    /**
     * 盘点状态(0.未开始  2.已完成 )
     */
    @Excel(name = "盘点状态(0.未开始  2.已完成 )")
    private String checkStatus;
    /**
     * 盘点人
     */
    @TableField(exist = false)
    private String checkBy;

    /**
     * 盘点单详情信息信息
     */

    @TableField(exist = false)
    private List<CheckDetail> checkDetailsList;

}
