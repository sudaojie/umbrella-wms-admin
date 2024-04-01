package com.ruoyi.wcs.req.agv;

import lombok.Data;

/**
 * AGV 添加搬运任务对象
 */
@Data
public class AgvAddMoveTaskReq {

    /**
     * 终点
     */
    private String inStoragesNum;


    /**
     * 起点
     */
    private String outStoragesNum;

    /**
     * avg 任务对象
     */
    private AgvTaskInfo taskInfo;

    private String deviceNo;
}
