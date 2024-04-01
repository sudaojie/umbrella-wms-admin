package com.ruoyi.wcs.domain.vo;

import lombok.Data;

@Data
public class WcsDryOutHourVo {

    /**
     * 晾晒时长
     */
    private String hour;

    /**
     * 时间
     */
    private String time;

    public WcsDryOutHourVo() {
    }

    public WcsDryOutHourVo(String hour, String time) {
        this.hour = hour;
        this.time = time;
    }
}
