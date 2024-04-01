package com.ruoyi.wms.basics.dto;

import lombok.Data;

import java.util.List;

@Data
public class TrayGoodsDto {

    private String goodsCode;

    private List<String> trayCodeList;

}
