package com.ruoyi.wms.basics.dto;

import com.ruoyi.wms.basics.vo.LocationMapVo;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class AreaDto {
    /**
     * 需求数量
     */
    private long sureCount;
    /**
     * 空库位数量
     */
    private long emptyNum;
    /**
     * 库区编码
     */
    private String areaCode;
    /**
     * 某类型货物库位数量
     */
    private long goodsCount;

    /**
     * 取实际的空库位编码集合
     */
    private List<String> sureLocations;

    /**
     * 托盘编码集合
     */
    private List<LocationMapVo> trayInfoList;

}
