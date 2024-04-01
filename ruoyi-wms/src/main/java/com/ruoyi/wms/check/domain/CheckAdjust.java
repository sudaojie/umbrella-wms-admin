package com.ruoyi.wms.check.domain;

import java.util.Date;
import java.util.List;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 库存盘点调整单对象
 *
 * @author nf
 * @date 2023-03-23
 */
@Data
@Accessors(chain = true)
@TableName("wms_warehouse_check_adjust")
public class CheckAdjust extends BaseEntity{


    /** 主键ID */
    private String id;

    /** 盘点单号 */
    @Excel(name = "盘点单号")
    private String checkBillCode;

    /** 调整状态 */
    @Excel(name = "调整状态")
    private String adjustStatus;

    /** 账面数量 */
    @Excel(name = "账面数量")
    private String curtainNum;

    /** 盘点数量 */
    @Excel(name = "盘点数量")
    private String checkNum;

    /** 盘盈数量 */
    @Excel(name = "盘盈数量")
    private String profitNum;

    /** 盘亏数量 */
    @Excel(name = "盘亏数量")
    private String lossNum;

    /** 开始盘点时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "开始盘点时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date startTime;

    /** 结束盘点时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "结束盘点时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date endTime;

    /** 盘点人 */
    @Excel(name = "盘点人")
    private String checkBy;

    /** 盘点调整详情 */
    @TableField(exist = false)
    private List<CheckAdjustDetail> checkDetailList;



}
