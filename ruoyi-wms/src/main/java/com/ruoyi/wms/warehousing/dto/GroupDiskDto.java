package com.ruoyi.wms.warehousing.dto;

import lombok.Data;

import java.util.List;

@Data
public class GroupDiskDto {
    /**
     * 托盘编码
     */
    private String trayCode;

    /**
     * 库位编号
     */
    private String locationCode;

    /**
     * 机件号集合
     */
    private List<String> partsCodeList;

    /**
     * 唯一码集合
     */
    private List<String> onlyCodeList;
}
