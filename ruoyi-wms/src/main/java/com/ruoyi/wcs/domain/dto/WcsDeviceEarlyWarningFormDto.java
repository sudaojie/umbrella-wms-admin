package com.ruoyi.wcs.domain.dto;

import com.ruoyi.wcs.domain.WcsDeviceEarlyWarningInfo;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author hewei
 * @date 2023/4/11 0011 09:32
 */
@Data
@Accessors(chain = true)
public class WcsDeviceEarlyWarningFormDto extends WcsDeviceEarlyWarningInfo {

    /**
     * 设备名称
     */
    private String deviceName;

    /**
     * 设备类型
     */
    private String deviceType;

}
