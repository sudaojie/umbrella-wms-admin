package com.ruoyi.wms.wcstask.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 给wcs的任务日志对象
 *
 * @author ruoyi
 * @date 2023-02-28
 */
@Data
@Accessors(chain = true)
@TableName("wms_wcs_tasklog")
public class Tasklog extends BaseEntity {


    /**
     * 主键
     */
    private String id;

    /**
     * 数据
     */
    @Excel(name = "数据")
    private String taskData;
    /**
     * 操作库区类型(1晾晒区，2理货区)
     */
    private String areaType;
    /**
     * 操作类型（takeTray-取盘；putTray-回盘）
     */
    private String opType;



}
