package com.ruoyi.wcs.req.stacker;

import lombok.Data;

/**
 * 堆垛机任务响应对象
 */
@Data
public class StackerTaskRsp {

    /**
     * 成功返回任务编号 ID
     */
    private Integer resultData;

    /**
     * 成功或者失败码
     */
    private Integer state;

    /**
     * 成功或者失败信息
     */
    private String message;

    public boolean isSuccess() {
        return this.state == 10000;
    }

}
