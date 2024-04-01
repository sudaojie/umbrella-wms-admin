package com.ruoyi.wms.nolist.vo;

import com.ruoyi.wms.outbound.vo.PartsCodeVo;
import lombok.Data;

import java.util.List;

/**
 * 无单离线数据
 */
@Data
public class NoListVo {
    //托盘编码
    private String trayCode;
    //货物类别
    private String goodsCode;
    //机件号列表
    private List<String> partsCodeList;

}
