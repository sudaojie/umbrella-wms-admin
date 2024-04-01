package com.ruoyi.wms.basics.vo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * 首页类基本信息对象
 *
 * @author ruoyi
 * @date 2023-03-16
 */
@Data
@Accessors(chain = true)
public class LocationRespVo {

    /**
     * 库位编码
     */
    private List<LocationAreaVo> locationData;

    /**
     * 所属库区类型
     */
    private String areaType;

    /**
     * 所有库位编码
     */
    private List<LocationVo> locationList;
}
