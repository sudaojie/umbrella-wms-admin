package com.ruoyi.wcs.task;

import com.ruoyi.common.core.domain.WmsWcsInfo;
import com.ruoyi.wcs.constans.WcsConstants;
import com.ruoyi.wcs.domain.WcsOperateTask;
import com.ruoyi.wcs.service.WcsOperateTaskService;
import com.ruoyi.wms.basics.service.LocationService;
import com.ruoyi.wms.wcstask.domain.WmsWcsCallbackInfo;
import com.ruoyi.wms.wcstask.service.WmsWcsCallbackInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Slf4j
@Component("wcsTimeTask")
public class WcsTimeTask {


    @Autowired
    private LocationService locationService;

    @Autowired
    private WcsOperateTaskService wcsOperateTaskService;

    @Autowired
    private WmsWcsCallbackInfoService wmsWcsCallbackInfoService;

    public void wcsDeviceCallback() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        WmsWcsInfo agvWcsInfo = WcsConstants.agvCallBackQueue.poll();
        WmsWcsInfo stackerWcsInfo = WcsConstants.stackerCallBackQueue.poll();

        log.info("定时执行WCS设备回调任务：");
        log.info("agvWcsInfo,{}", agvWcsInfo);
        log.info("stackerWcsInfo,{}", stackerWcsInfo);
        if (agvWcsInfo != null) {
            log.info("开始消费agv回调任务");
            locationService.agvInfo(agvWcsInfo);
        }
        if (stackerWcsInfo != null) {
            log.info("开始消费堆垛机回调任务");
            locationService.stackerInfo(stackerWcsInfo);
        }

    }


}
