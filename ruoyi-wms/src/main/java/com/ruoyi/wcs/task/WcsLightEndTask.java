package com.ruoyi.wcs.task;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.ruoyi.iot.task.ctrl.LightCtrlTask;
import com.ruoyi.wcs.domain.WcsDeviceBaseInfo;
import com.ruoyi.wcs.domain.WcsSmartLightingDetailInfo;
import com.ruoyi.wcs.domain.WcsSmartLightingJobRelateInfo;
import com.ruoyi.wcs.enums.wcs.WcsSwitchStatusEnum;
import com.ruoyi.wcs.service.WcsDeviceBaseInfoService;
import com.ruoyi.wcs.service.WcsSmartLightingDetailInfoService;
import com.ruoyi.wcs.service.WcsSmartLightingJobRelateInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author hewei
 * @description 照明系统定时关闭任务
 */
@Slf4j
@Component("WcsLightEndTask")
public class WcsLightEndTask {

    @Autowired
    private WcsDeviceBaseInfoService wcsDeviceBaseInfoService;

    @Autowired
    private WcsSmartLightingJobRelateInfoService wcsSmartLightingJobRelateInfoService;

    @Autowired
    private WcsSmartLightingDetailInfoService wcsSmartLightingDetailInfoService;

    @Autowired
    private LightCtrlTask lightCtrlTask;

    public void endTask(String jobId) {

        log.info(" ========任务号{}; 定时执行WCS照明系统结束任务开始执行======= ", jobId);

        List<String> deviceInfoIds = wcsSmartLightingJobRelateInfoService.getBaseMapper()
                .selectList(new QueryWrapper<WcsSmartLightingJobRelateInfo>().eq("job_id", jobId))
                .stream().map(WcsSmartLightingJobRelateInfo::getDeviceInfoId).collect(Collectors.toList());

        // 将涉及到照明设备依次发送报文
        QueryWrapper<WcsDeviceBaseInfo> qw = new QueryWrapper<>();
        qw.in("id", deviceInfoIds);

        List<WcsDeviceBaseInfo> list = wcsDeviceBaseInfoService.getBaseMapper().selectList(qw);
        List<String> filterList = deviceInfoIds.stream().filter(StrUtil::isNotEmpty).collect(Collectors.toList());

        if (CollUtil.isNotEmpty(list)) {
            lightCtrlTask.closeLight(list);
            if (CollUtil.isNotEmpty(filterList)) {
                UpdateWrapper<WcsSmartLightingDetailInfo> updateWrapper = new UpdateWrapper();
                updateWrapper.in("device_info_id", filterList);
                updateWrapper.set("switch_status", WcsSwitchStatusEnum.OPEN.getCode());
                wcsSmartLightingDetailInfoService.update(null, updateWrapper);
            }
        }

        log.info(" ========任务号{}; 定时执行WCS照明系统结束任务结束执行======= ", jobId);
    }


}
