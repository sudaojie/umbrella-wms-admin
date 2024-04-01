package com.ruoyi.wcs.req.stacker;

import lombok.Data;

/**
 * 堆垛机IO控制请求对象
 * @author select
 */
@Data
public class StackerIOReq {

    /**
     * IO地址码
     */
    private String ioAddress;

    /**
     * IO点中文描述
     */
    private String ioName;

    /**
     * IO点值 0就是不报警，1就是报警
     */
    private int ioValue;

    /**
     * 堆垛机设备编号
     */
    private String ioDeviceId;
}
