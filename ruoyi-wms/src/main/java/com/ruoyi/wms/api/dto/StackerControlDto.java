package com.ruoyi.wms.api.dto;

import lombok.Data;

import java.util.Date;

/**
 * 堆垛机IO控制 传输对象
 * @author select
 */
@Data
public class StackerControlDto {

    /**
     * 堆垛机编号 1、2、3
     */
    private String ioDeviceId;


    /**
     * io地址码
     */
    private String ioAddress;

    /**
     * io名称
     */
    private String ioName;

    /**
     * 故障值 0就是不报警，1就是报警
     */
    private String ioValue;

}
