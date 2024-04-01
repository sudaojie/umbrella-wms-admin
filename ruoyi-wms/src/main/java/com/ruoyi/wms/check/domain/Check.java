package com.ruoyi.wms.check.domain;

import java.util.List;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 库存盘点对象
 *
 * @author ruoyi
 * @date 2023-03-15
 */
@Data
@Accessors(chain = true)
@TableName("wms_warehouse_check")
public class Check extends BaseEntity{


    /** 主键ID */
    private String id;

    /** 盘点单号 */
    @Excel(name = "盘点单号")
    private String checkBillCode;

    /** 盘点状态(0.未开始 1.盘点中 2.已完成 ) */
    @Excel(name = "盘点状态(0.未开始 1.盘点中 2.已完成 )")
    private String checkStatus;

    /** 盘点类型（0.库位 1.货物类型） */
    @Excel(name = "盘点类型（0.库位 1.货物类型）")
    private String checkType;

    /** 盘点方式(0.全盘 1.部分盘) */
    @Excel(name = "盘点方式(0.全盘 1.部分盘)")
    private String checkMethod;

    /** 库区编号 */
    @TableField(exist = false)
    private String areaCode;

    /** 库区名称 */
    @TableField(exist = false)
    private String areaName;

    /** 开始盘点时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startTime;

    /** 结束盘点时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date endTime;

    /** 盘点人 */
    @Excel(name = "盘点人")
    private String checkBy;

    /** 库存盘点详情信息 */
    @TableField(exist = false)
    private List<CheckDetail> wmsWarehouseCheckDetailList;

    /** 库位下拉信息 */
    @TableField(exist = false)
    private List<CheckDetail> locationList;

    /** 盘点创建时间 */
    @TableField(exist = false)
    private String[] daterangeCheckPlanTime;

}
