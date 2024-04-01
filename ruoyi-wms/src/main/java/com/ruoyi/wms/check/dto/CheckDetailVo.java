package com.ruoyi.wms.check.dto;

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
public class CheckDetailVo extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 盘点单号 */
    private String checkBillCode;

    /** 盘点货物编码 */
    private String goodsCode;

    /** 盘点货物名称 */
    private String goodsName;

    /** 盘点库区编号 */
    private String areaCode;

    /** 盘点库区名称 */
    private String areaName;

    /** 盘点库位编号 */
    private String locationCode;

    /** 盘点库位名称 */
    private String locationName;

    /** 盘点托盘编号 */
    private String trayCode;

    /** 账面数量 */
    private String curtainNum;

    /** 盘点数量 */
    private String checkNum;

    /** 盘亏数量 */
    private String lossNum;

    /** 盘盈数量 */
    private String profitNum;

    /** 盘点状态(0.未开始  2.已完成 ) */
    private String checkStatus;

    /**
     * 盘点单详情信息信息
     */

    @TableField(exist = false)
    private List<CheckDetailVo> checkDetailsList;

}
