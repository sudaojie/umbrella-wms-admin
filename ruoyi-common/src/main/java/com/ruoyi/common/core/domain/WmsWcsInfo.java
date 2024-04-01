package com.ruoyi.common.core.domain;

import java.util.HashMap;

/**
 * wms/wcs交互信息实体
 *
 * @author ruoyi
 */
public class WmsWcsInfo extends HashMap<String, Object> {
    private static final long serialVersionUID = 1L;


    /**
     * 状态码
     */
    public static final String CODE = "code";

    /**
     * 消息内容
     */
    public static final String MSG = "msg";

    /**
     * 交互类型
     */
    public static final String TYPE = "type";

    /**
     * 任务类型
     */
    public static final String TASKTYPE = "taskType";
    /**
     * 操作库区类型(1晾晒区，2理货区)
     */
    public static final String AREATYPE = "areaType";

    /**
     * 托盘编码
     */
    public static final String TRAY_CODE = "trayCode";

    /**
     * 起始库位
     */
    public static final String START_LOCATION_CODE = "startLocationCode";

    /**
     * 结束库位
     */
    public static final String END_LOCATION_CODE = "endLocationCode";

    /**
     * 起始库区
     */
    public static final String START_AREA_CODE = "startAreaCode";

    /**
     * 结束库区
     */
    public static final String END_AREA_CODE = "endAreaCode";

    /**
     * 子信息列表
     */
    public static final String CHILD_INFO_LIST = "childInfoList";

    /**
     * 业务单据
     */
    public static final String DOC = "doc";


    /**
     * AGV设备编号
     */
    public static final String AGV_DEVICE_NO = "deviceNo";


    /**
     * 业务编号
     */
    public static final String SERVICE_ID = "serviceId";

    public static final String MOVE_LAST = "moveLast";


    /**
     * 初始化一个新创建的 WmsWcsInfo 对象
     */
    public WmsWcsInfo(String type) {
        super.put(TYPE, type);
    }

    /**
     * 初始化一个新创建的 WmsWcsInfo 对象
     */
    public WmsWcsInfo(String type, String taskType) {
        super.put(TYPE, type);
        super.put(TASKTYPE, taskType);
    }

    public WmsWcsInfo() {

    }

    /**
     * 获取一个交互对象
     *
     * @param type
     * @return
     */
    public static WmsWcsInfo getInfo(String type) {
        return new WmsWcsInfo(type);
    }

    /**
     * 获取一个交互对象
     *
     * @param type
     * @return
     */
    public static WmsWcsInfo getInfo(String type, String taskType) {
        return new WmsWcsInfo(type, taskType);
    }


}
