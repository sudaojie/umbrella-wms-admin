package com.ruoyi.wcs.domain.vo;

import lombok.Data;

/**
 * @author hewei
 * @date 2023/4/4 0004 14:18
 * @description 摄像头推流Vo
 */
@Data
public class WcsCameraPushStreamVo {

    /**
     * 编号
     */
    private String id;

    /**
     * http地址
     */
    private String wsFlv;

    /**
     * https地址
     */
    private String wssFlv;

    /**
     * 描述
     */
    private String message;

    /**
     *app
     */
    private String app;

    /**
     * 流媒体服务id
     */
    private String mediaServerId;

    /**
     * 流
     */
    private String stream;
}
