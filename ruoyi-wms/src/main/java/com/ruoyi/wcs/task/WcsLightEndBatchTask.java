package com.ruoyi.wcs.task;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.ruoyi.common.enums.DelFlagEnum;
import com.ruoyi.iot.task.ctrl.LightCtrlTask;
import com.ruoyi.wcs.domain.WcsDeviceBaseInfo;
import com.ruoyi.wcs.domain.WcsSmartLightingDetailInfo;
import com.ruoyi.wcs.enums.wcs.WcsSwitchStatusEnum;
import com.ruoyi.wcs.enums.wcs.WcsTaskDeviceTypeEnum;
import com.ruoyi.wcs.service.WcsDeviceBaseInfoService;
import com.ruoyi.wcs.service.WcsSmartLightingDetailInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author hewei
 * @description 照明系统全局定时关闭任务
 */
@Slf4j
@Component("WcsLightEndBatchTask")
public class WcsLightEndBatchTask {

    @Autowired
    private WcsDeviceBaseInfoService wcsDeviceBaseInfoService;

    @Autowired
    private LightCtrlTask lightCtrlTask;

    @Autowired
    private WcsSmartLightingDetailInfoService wcsSmartLightingDetailInfoService;

    public void endBatchTask(String jobId) {

        log.info(" ========任务号{}; 定时执行WCS照明系统全局结束任务开始执行======= ", jobId);

        QueryWrapper<WcsDeviceBaseInfo> qw = new QueryWrapper<>();
        qw.eq("enable_status", DelFlagEnum.DEL_NO.getCode());
        qw.eq("del_flag", DelFlagEnum.DEL_NO.getCode());
        qw.eq("device_type", WcsTaskDeviceTypeEnum.LIGHT.getCode());

        List<WcsDeviceBaseInfo> list = wcsDeviceBaseInfoService.getBaseMapper().selectList(qw);

        List<String> filterList = list.stream().map(WcsDeviceBaseInfo::getId).collect(Collectors.toList());

        if (CollUtil.isNotEmpty(list)) {
            lightCtrlTask.closeLight(list);
            if (CollUtil.isNotEmpty(filterList)) {
                UpdateWrapper<WcsSmartLightingDetailInfo> updateWrapper = new UpdateWrapper();
                updateWrapper.in("device_info_id", filterList);
                updateWrapper.set("switch_status", WcsSwitchStatusEnum.OPEN.getCode());
                wcsSmartLightingDetailInfoService.update(null, updateWrapper);
            }
        }

        log.info(" ========任务号{}; 定时执行WCS照明系统全局结束任务结束执行======= ", jobId);
    }


}
