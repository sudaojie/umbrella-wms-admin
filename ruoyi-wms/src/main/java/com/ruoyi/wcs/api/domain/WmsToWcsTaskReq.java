package com.ruoyi.wcs.api.domain;

import lombok.Data;

import java.util.List;

/**
 * WMS向WCS发送任务请求对象
 */
@Data
public class WmsToWcsTaskReq {


    /**
     * 起始库区
     */
    private String startAreaCode;


    /**
     * 结束库区
     */
    private String endAreaCode;

    /**
     * 起始库位
     */
    private String startLocationCode;


    /**
     * 结束库位
     */
    private String endLocationCode;

    /**
     * 交互类型（takeTray取盘；putTray回盘,relocation移库）
     */
    private String type;


    /**
     * 任务类型
     */
    private String taskType;

    /**
     * 托盘号
     */
    private String trayCode;

    /**
     * 单据(移库多任务使用）
     */
    private String doc;

    /**
     * 单据号
     */
    private String inBillNo;

    /**
     * 业务编号
     */
    private String serviceId;

    private String moveLast;

    /**
     * 移库子任务列表
     */
    private List<WmsToWcsTaskReq> childInfoList;

}
