package com.ruoyi.wms.basics.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import com.ruoyi.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * AGV取放货完成日志
 */
@Data
@Accessors(chain = true)
@TableName("agv_put_way_finsh_log")
public class AgvPutWayFinshLog extends BaseEntity {


    /**
     * 编号
     */
    private String id;


    /**
     * 托盘号
     */
    private String trayCode;


    /**
     * 堆垛机执行中(Y/N)
     */
    private String stackerTaskTypeStatus;


    /**
     * AGV取放货完成(Y/N)
     */
    private String agvTaskTypeStatus;


    /**
     * 运输带编号
     */
    private String lineNo;


}
