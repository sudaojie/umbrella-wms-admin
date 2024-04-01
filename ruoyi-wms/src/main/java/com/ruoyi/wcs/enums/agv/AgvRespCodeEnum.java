package com.ruoyi.wcs.enums.agv;

/**
 * 劢微Agv 请求响应码枚举
 */
public enum AgvRespCodeEnum {

    SUCCESS(10000, "请求成功"),
    SERVICE_ERROR(-10000, "请求执行失败，请查看log"),
    DATA_ERROR(103, "请求数据错误"),
    DATA_VERIFY_ERROR(104, "数据校验失败"),
    STORAGE_EXEIST(206, "当前位置已经有库位存在"),
    DISTAPCH_NOT_CONNECT(301, "调度没有连接"),
    OUT_STORAGE_NOT_HAS_STORAGE(401, "出库库位没货"),
    IN_STORAGE_NOT_HAS_TORAGE(402, "入库库位有货"),
    STORAGE_LIST_IS_EMPTY(403, "库位列表是空的"),
    CURRENT_TASK_EXIST(501, "当前已经有任务存在")
    ;


    private int code;

    private String name;


    AgvRespCodeEnum(int code, String name) {
        this.code = code;
        this.name = name;
    }


    public static AgvRespCodeEnum getInstance(int code) {
        for (AgvRespCodeEnum curEnum : AgvRespCodeEnum.values()) {
            if (curEnum.code == code) {
                return curEnum;
            }
        }
        return null;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
