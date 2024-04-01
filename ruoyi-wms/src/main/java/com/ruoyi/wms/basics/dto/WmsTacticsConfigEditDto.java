package com.ruoyi.wms.basics.dto;

import com.ruoyi.wcs.domain.WcsDeviceBaseInfo;
import com.ruoyi.wms.basics.domain.WmsTacticsConfig;
import lombok.Data;

import java.util.List;

/**
 * 货物类型托盘取盘回盘策略配置 编辑Dto
 */
@Data
public class WmsTacticsConfigEditDto {

    /**
     *  agv设备列表
     */
    private List<WcsDeviceBaseInfo> agvDevices;

    /**
     * 策略列表集合
     */
    private List<WmsTacticsConfig> wmsTacticsConfigList;
}
