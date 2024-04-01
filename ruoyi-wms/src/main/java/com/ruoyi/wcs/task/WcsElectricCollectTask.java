package com.ruoyi.wcs.task;

import com.ruoyi.iot.task.collect.ElectricPowerCollectTask;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author hewei
 * @description 电表采集定时任务
 */
@Slf4j
@Component("wcsElectricCollectTask")
public class WcsElectricCollectTask {

    @Autowired
    private ElectricPowerCollectTask electricPowerCollectTask;

    public void startTask() {

        log.info(" ========定时执行WCS电表采集定时任务开始执行======= ");

        // 将涉及到电表设备依次发送报文
        electricPowerCollectTask.collectElectricPowerData();

        log.info(" ========定时执行WCS电表采集定时任务结束执行======= ");
    }
}
