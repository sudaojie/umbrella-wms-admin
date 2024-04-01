package com.ruoyi.wms.wcstask.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * WMS/WCS回调信息对象
 *
 * @author ruoyi
 * @date 2023-06-27
 */
@Data
@Accessors(chain = true)
@TableName("wms_wcs_callback_info")
public class WmsWcsCallbackInfo extends BaseEntity {


    /**
     * id主键
     */
    private String id;

    /**
     * 交互类型（takeTray取盘；putTray回盘,relocation移库）
     */
    private String type;

    /**
     * 任务类型(0.正常入库 1.正常出库 2.晾晒入库 3.晾晒出库 4.移库 5.无单 6.盘点入库 7.盘点出库 8.回空盘)
     */
    private String taskType;

    /**
     * 操作库区类型(1晾晒区，2理货区)
     */
    private String areaType;

    /**
     * 任务号
     */
    private String taskNo;

    /**
     * 托盘编码
     */
    private String trayCode;

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
     * 业务单据号
     */
    private String doc;

    /**
     * 业务编号
     */
    private String serviceId;

    private String moveLast;

}
