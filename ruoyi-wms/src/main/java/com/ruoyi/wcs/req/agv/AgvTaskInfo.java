package com.ruoyi.wcs.req.agv;

import lombok.Data;

/**
 * Agv Task任务对象
 */
@Data
public class AgvTaskInfo {

    /**
     * 任务编号(int 类型，为 0 则系统自动生成)
     */
    private Integer taskId;

    /**
     * 任务名称
     */
    private String taskName;


    /**
     * 添加用户编号（缺省 0）
     */
    private Integer uid;

    /**
     * 小车Id
     */
    private Integer carrierId;

    /**
     * 小车类型
     */
    private Integer carrierType;

    /**
     * 任务类型（缺省 0）
     */
    private Integer taskType;


    /**
     * 任务执行优先级 0-9，默认为 0，9 为最高
     */
    private Integer priority;


    /**
     * 备注说明
     */
    private String remark;

}
