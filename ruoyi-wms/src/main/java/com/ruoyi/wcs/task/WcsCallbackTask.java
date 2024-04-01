package com.ruoyi.wcs.task;

import com.ruoyi.wcs.constans.WcsConstants;
import com.ruoyi.wcs.domain.WcsOperateTask;
import com.ruoyi.wcs.service.WcsOperateTaskService;
import com.ruoyi.wms.wcstask.domain.WmsWcsCallbackInfo;
import com.ruoyi.wms.wcstask.service.WmsWcsCallbackInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Administrator
 * @create 2023-11-01 15:53
 */
@Slf4j
@Component("wcsCallbackTask")
public class WcsCallbackTask {

    @Autowired
    private WcsOperateTaskService wcsOperateTaskService;

    @Autowired
    private WmsWcsCallbackInfoService wmsWcsCallbackInfoService;

    public void wcsTimeCallbackTask(){
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        WcsOperateTask operateTask = WcsConstants.wcsOperateTaskQueue.poll();
        WmsWcsCallbackInfo wcsCallbackInfo = WcsConstants.wcsCallbackInfoQueue.poll();
        insertOrUpdateTask(operateTask,wcsCallbackInfo);
    }

    @Transactional(rollbackFor = Exception.class)
    public void insertOrUpdateTask(WcsOperateTask operateTask,WmsWcsCallbackInfo wcsCallbackInfo) {
        log.info("operateTask,{}", operateTask);
        log.info("wcsCallbackInfo,{}", wcsCallbackInfo);
        if(operateTask != null){
            log.info("开始消费WCS操作任务的回调任务");
            wcsOperateTaskService.saveOrUpdate(operateTask);
        }
        if(wcsCallbackInfo != null){
            log.info("开始消费WCS回调表任务");
            wmsWcsCallbackInfoService.insertWmsWcsCallbackInfo(wcsCallbackInfo);
        }
    }
}
