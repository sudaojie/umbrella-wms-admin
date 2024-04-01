package com.ruoyi.wcs.enums.camera;

/**
 * wvp接口
 *
 * @author hewei
 */
public enum WcsWvpApiEnum {

    GET_LOGIN_TOKEN("api/user/login", "获取登录Token"),

    PLAY_START("api/play/start/", "单个点播"),

    PLAY_START_ALL("api/play/startAll", "多个点播"),

    ADJUST_DIRECTION("api/ptz/control/", "调整摄像头方向"),

    GB_RECORD("api/gb_record/query", "录像查询"),

    GB_RECORD_DOWNLOAD_START("api/gb_record/download/start", "开始录像处理"),

    GB_RECORD_DOWNLOAD_STOP("api/gb_record/download/stop", "停止录像处理"),

    GB_RECORD_DOWNLOAD_PROGRESS("api/gb_record/download/progress", "获取录像处理进度"),

    GB_RECORD_DOWNLOAD_FILE("record_proxy/mediaServerId/api/record/file/download/task/add", "录像文件下载处理"),

    GB_RECORD_DOWNLOAD_FILE_DOWNLOAD("record_proxy/mediaServerId/api/record/file/download/task/list", "录像文件下载"),

    PLAY_BACK_START("api/playback/start", "录像回放"),

    PLAY_BACK_STOP("api/playback/stop", "录像回放停止"),

    PLAY_BACK_PAUSE("api/playback/pause", "录像暂停"),

    PLAY_BACK_RESUME("api/playback/resume", "录像暂停恢复"),

    PLAY_BACK_SPEED("api/playback/speed", "录像回放倍速播放"),

    DEVICE_REBOOT("api/device/control/teleboot", "设备重启"),

    //已过期，请使用getMediaList接口替代
    GET_MEDIAINFO("zlm/mediaServerId/index/api/getMediaInfo?vhost=__defaultVhost__&schema=rtsp", "获取视频编码信息"),

    GET_MEDIALIST("zlm/mediaServerId/index/api/getMediaList?vhost=__defaultVhost__&schema=rtsp", "获取视频编码信息"),

    ACCESS_TOKEN("access-token", "请求header"),

    expirationTime("1680", "token过期时间30分钟 28分钟时重新获取"),

    SUCCESS("0", "成功"),
    ERROR100("100", "失败"),
    ERROR400("400", "参数不全或者错误"),
    ERROR401("401", "请登录后重新请求"),
    ERROR500("500", "超时");

    String code;
    String name;

    WcsWvpApiEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }
}
