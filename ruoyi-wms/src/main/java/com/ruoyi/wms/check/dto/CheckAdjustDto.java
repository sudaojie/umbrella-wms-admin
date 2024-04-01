package com.ruoyi.wms.check.dto;

import com.ruoyi.wms.check.domain.CheckAdjustDetail;
import lombok.Data;

import java.util.List;

/**
 * 盘点调整单打印
 */
@Data
public class CheckAdjustDto {
    /**
     * 盘点单号
     */
    private String checkBillCode;
    /**
     * 制单人
     */
    private String createBy;
    /**
     * 制单时间
     */
    private String createTime;

    private List<CheckAdjustDetail> checkDetailList;
}
