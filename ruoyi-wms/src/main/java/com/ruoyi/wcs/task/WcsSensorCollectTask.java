package com.ruoyi.wcs.task;

import com.ruoyi.iot.task.collect.SmokeCollectTask;
import com.ruoyi.iot.task.collect.TemperatureAndHumidityCollectTask;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author hewei
 * @description Wcs传感器数据采集定时任务
 */
@Slf4j
@Component("WcsSensorCollectTask")
public class WcsSensorCollectTask {

    @Autowired
    private SmokeCollectTask smokeCollectTask;

    @Autowired
    private TemperatureAndHumidityCollectTask temperatureAndHumidityCollectTask;

    public void startTask() {

        log.info(" ========定时执行Wcs传感器数据采集定时任务开始执行======= ");
        System.out.println("dajiahao");
        // 将涉及到烟感设备依次发送报文
        smokeCollectTask.smokeCollectTask();

        // 将涉及到温湿度设备依次发送报文
        temperatureAndHumidityCollectTask.collectTemperatureAndHumidityData();

        log.info(" ========定时执行Wcs传感器数据采集定时任务结束执行======= ");
    }
}
