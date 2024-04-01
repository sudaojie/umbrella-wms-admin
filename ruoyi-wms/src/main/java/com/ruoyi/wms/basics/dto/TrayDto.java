package com.ruoyi.wms.basics.dto;

import com.ruoyi.wms.basics.domain.Location;
import lombok.Data;

import java.util.List;

@Data
public class TrayDto {


    /**
     * 结束库区编码
     */
    private String startAreaCode;
    /**
     * 结束库区编码
     */
    private String endAreaCode;
    /**
     * 结束库位编码
     */
    private String endLocationCode;
    /**
     * 开始库位编码
     */
    private String startLocationCode;
    /**
     * 任务类型
     */
    private String taskType;
    /**
     * 货物编码
     */
    private String goodsCode;
    /**
     * 入库单详情主键
     */
    private String inbillDetailId;
    /**
     * 取托盘数
     */
    private String num;
    /**
     * 托盘编码
     */
    private String trayCode;

    /**
     * 机件码集合
     */
    private List<String> partsCodeList;

    /**
     * 增加的机件号列表
     */
    private List<String> addPartsCodeList;
    /**
     * 需要回盘的托盘编码
     */
    private List<String> trayCodeList;

    /**
     * 业务单据号
     */
    private String doc;


    /**
     * 人工取盘策略（avgGet：平均取盘  pointGet：指定巷道取盘）
     */
    private String getTrayType;


    /**
     * 指定巷道取盘的库区编码
     */
    private String pointAreaCode;

    /**
     * 理货区回盘库区
     */
    private List<Location> locationList;


    private String deviceNo;
}
