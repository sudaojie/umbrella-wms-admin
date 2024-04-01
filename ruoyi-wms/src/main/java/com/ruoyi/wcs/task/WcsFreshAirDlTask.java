package com.ruoyi.wcs.task;

import com.ruoyi.iot.task.ctrl.FreshAirDlCtrTask;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author sdj
 * @create 2023-08-05 16:53
 */
@Slf4j
@Component("WcsFreshAirDlTask")
public class WcsFreshAirDlTask {

    @Autowired
    private FreshAirDlCtrTask freshAirDlCtrTask;

    public void startTask(){
        log.info(" ========定时执行Wcs新风DL状态定时任务开始执行======= ");
        System.out.println("dajiahao");

        // 将涉及到新风设备依次发送报文
        freshAirDlCtrTask.getFreshAirDl();

        log.info(" ========定时执行Wcs新风DL状态定时任务结束执行======= ");
    }

}
