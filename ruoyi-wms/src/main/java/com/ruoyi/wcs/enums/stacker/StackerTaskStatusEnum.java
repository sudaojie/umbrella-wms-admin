package com.ruoyi.wcs.enums.stacker;

/**
 * 堆垛机任务状态枚举
 */
public enum StackerTaskStatusEnum {


    NOT_STARTED(0, "未开始"),
    IN_PROGRESS(1, "执行中"),
    COMPLETED(2, "执行完成"),
    IN_EXCEPTION(3, "执行异常")
    ;


    private int code;

    private String name;


    StackerTaskStatusEnum(int code, String name) {
        this.code = code;
        this.name = name;
    }


    public static StackerTaskStatusEnum getInstance(int code) {
        for (StackerTaskStatusEnum curEnum : StackerTaskStatusEnum.values()) {
            if (curEnum.code == code) {
                return curEnum;
            }
        }
        return null;
    }

}
