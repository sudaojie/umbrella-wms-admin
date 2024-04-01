package com.ruoyi.wcs.domain.bo;

import com.ruoyi.wcs.domain.WcsDeviceBaseInfo;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class WcsHumidityBo extends WcsDeviceBaseInfo {

    /**
     * 湿度值
     */
    private Double humidityVal;

    /**
     * 温度值
     */
    private Double temperatureVal;
}
