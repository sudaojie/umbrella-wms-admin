package com.ruoyi.wms.basics.vo;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class DeviceStatusVo {

    /**
     * 设备总数
     */
    private Integer deviceNum;

    /**
     * 正常设备数
     */
    private Integer normalDeviceNum;

    /**
     * 异常设备数
     */
    private Integer abNormalDeviceNum;

}
