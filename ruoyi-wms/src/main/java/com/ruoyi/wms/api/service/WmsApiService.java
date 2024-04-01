package com.ruoyi.wms.api.service;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.WmsWcsInfo;
import com.ruoyi.common.enums.DelFlagEnum;
import com.ruoyi.common.enums.WmsWcsTypeEnum;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.wcs.api.service.WcsTaskApiService;
import com.ruoyi.wcs.domain.WcsDeviceEarlyWarningInfo;
import com.ruoyi.wcs.domain.WcsOperateTask;
import com.ruoyi.wcs.enums.wcs.WcsTaskRunStatusEnum;
import com.ruoyi.wcs.mapper.WcsOperateTaskMapper;
import com.ruoyi.wcs.req.stacker.StackerAndTransferStatusInfo;
import com.ruoyi.wcs.service.WcsDeviceBaseInfoService;
import com.ruoyi.wcs.service.WcsDeviceEarlyWarningInfoService;
import com.ruoyi.wms.api.dto.AgvPositionSignalDto;
import com.ruoyi.wms.api.dto.StackerPositionSignalDto;
import com.ruoyi.wms.api.dto.StackerWarnReportDto;
import com.ruoyi.wms.basics.domain.AgvPutWayFinshLog;
import com.ruoyi.wms.basics.mapper.AgvPutWayFinshLogMapper;
import com.ruoyi.wms.basics.service.LocationService;
import com.ruoyi.wms.wcstask.domain.WmsWcsCallbackInfo;
import com.ruoyi.wms.wcstask.service.WmsWcsCallbackInfoService;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * WMS对外提供服务Service
 */
@Slf4j
@Service
public class WmsApiService {

    @Autowired
    private LocationService locationService;

    @Autowired
    private WmsWcsCallbackInfoService wmsWcsCallbackInfoService;

    @Autowired
    private WcsDeviceEarlyWarningInfoService wcsDeviceEarlyWarningInfoService;

    @Autowired
    private WcsDeviceBaseInfoService wcsDeviceBaseInfoService;

    @Autowired
    private WcsTaskApiService wcsTaskApiService;

    @Autowired
    private WcsOperateTaskMapper wcsOperateTaskMapper;

    @Autowired
    private AgvPutWayFinshLogMapper agvPutWayFinshLogMapper;

    /**
     * 堆垛机到位信息号Dto
     *
     * @param stackerPositionSignalDto
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult stackerPositionSignal(StackerPositionSignalDto stackerPositionSignalDto) {
        String taskNo = stackerPositionSignalDto.getTaskNo();
        if (StrUtil.isEmpty(taskNo)) {
            throw new ServiceException("堆垛机到位信号,任务号不能为空");
        }

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        WcsOperateTask wcsOperateTask = wcsOperateTaskMapper.selectOne(new QueryWrapper<WcsOperateTask>()
                .eq("del_flag", DelFlagEnum.DEL_NO.getCode())
                .eq("task_no", taskNo)
        );

        if(wcsOperateTask == null) {
            throw new ServiceException("堆垛机任务,任务号[" + taskNo + "]不存在");
        }

        wcsOperateTask.setTaskStatus(WcsTaskRunStatusEnum.COMPLETED.getCode());
        wcsOperateTask.setOperateEndTime(new Date());
        int i = wcsOperateTaskMapper.updateById(wcsOperateTask);
        if (i > 0) {
            WmsWcsCallbackInfo wmsWcsCallbackInfo = wmsWcsCallbackInfoService.getBaseMapper().selectOne(
                    new QueryWrapper<WmsWcsCallbackInfo>()
                            .eq("del_flag", DelFlagEnum.DEL_NO.getCode())
                            .eq("task_no", taskNo));
            if (wmsWcsCallbackInfo == null) {
                throw new ServiceException("回调队列表，"+taskNo+"任务号不存在");
            }
            WmsWcsInfo wmsWcsInfo = new WmsWcsInfo();
            wmsWcsInfo.put(WmsWcsInfo.TYPE, wmsWcsCallbackInfo.getType());
            wmsWcsInfo.put(WmsWcsInfo.TASKTYPE, wmsWcsCallbackInfo.getTaskType());
            wmsWcsInfo.put(WmsWcsInfo.AREATYPE, wmsWcsCallbackInfo.getAreaType());
            wmsWcsInfo.put(WmsWcsInfo.TRAY_CODE, wmsWcsCallbackInfo.getTrayCode());
            wmsWcsInfo.put(WmsWcsInfo.START_AREA_CODE, wmsWcsCallbackInfo.getStartAreaCode());
            wmsWcsInfo.put(WmsWcsInfo.END_AREA_CODE, wmsWcsCallbackInfo.getEndAreaCode());
            wmsWcsInfo.put(WmsWcsInfo.START_LOCATION_CODE, wmsWcsCallbackInfo.getStartLocationCode());
            wmsWcsInfo.put(WmsWcsInfo.END_LOCATION_CODE, wmsWcsCallbackInfo.getEndLocationCode());
            wmsWcsInfo.put(WmsWcsInfo.DOC, wmsWcsCallbackInfo.getDoc());
            wmsWcsInfo.put(WmsWcsInfo.SERVICE_ID, wmsWcsCallbackInfo.getServiceId());
            wmsWcsInfo.put(WmsWcsInfo.MOVE_LAST, wmsWcsCallbackInfo.getMoveLast());
            //堆垛机到位消费信息
            locationService.stackerInfo(wmsWcsInfo);
            //将回调记录变成已删除,标识为已消费
            wmsWcsCallbackInfoService.deleteWmsWcsCallbackInfoById(wmsWcsCallbackInfo.getId());
            return AjaxResult.success();
        }else{
            throw new ServiceException("修改堆垛机任务:"+taskNo+" 为成功状态失败");
        }
    }


    /**
     * 堆垛机报警信息上报
     *
     * @param stackerWarnReportDto
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean stackerWarnReport(StackerWarnReportDto stackerWarnReportDto) {

        if (StrUtil.isEmpty(stackerWarnReportDto.getTaskNo())) {
            throw new ServiceException("任务号不能为空");
        }

        //堆垛机有错误信息任务回调，将相应的WCS任务状态改成执行失败
        WcsOperateTask wcsOperateTask = wcsOperateTaskMapper.selectOne(
                new QueryWrapper<WcsOperateTask>()
                        .eq("del_flag", DelFlagEnum.DEL_NO.getCode())
                        .eq("task_no", stackerWarnReportDto.getTaskNo())
        );
        if (wcsOperateTask == null) {
            throw new ServiceException("任务号错误，未查询到任务信息");
        }

        if(StrUtil.isEmpty(stackerWarnReportDto.getAlarmCode())){
            wcsOperateTask.setTaskStatus(WcsTaskRunStatusEnum.IN_EXCEPTION.getCode());
            wcsOperateTaskMapper.updateById(wcsOperateTask);
        }

        WcsDeviceEarlyWarningInfo wcsDeviceEarlyWarningInfo = new WcsDeviceEarlyWarningInfo();
        wcsDeviceEarlyWarningInfo.setId(IdUtil.fastSimpleUUID());
        wcsDeviceEarlyWarningInfo.setDeviceInfoId(stackerWarnReportDto.getStackerId());
        wcsDeviceEarlyWarningInfo.setWarningTime(stackerWarnReportDto.getAlarmTime());
        wcsDeviceEarlyWarningInfo.setWarningContent(stackerWarnReportDto.getAlarmReason());
        wcsDeviceEarlyWarningInfoService.save(wcsDeviceEarlyWarningInfo);
        return true;
    }


    /**
     * AGV到位信号上报
     *
     * @param agvPositionSignalDto
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean agvPositionSignal(AgvPositionSignalDto agvPositionSignalDto) {
        String taskNo = agvPositionSignalDto.getTaskNo();
        if (StrUtil.isEmpty(taskNo)) {
            throw new ServiceException("AGV到位信号,任务号不能为空");
        }

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        WcsOperateTask wcsOperateTask = wcsOperateTaskMapper.selectOne(new QueryWrapper<WcsOperateTask>()
                .eq("del_flag", DelFlagEnum.DEL_NO.getCode())
                .eq("task_no", taskNo)
        );

        if(wcsOperateTask == null) {
            throw new ServiceException("AGV到位信号任务,任务号[" + taskNo + "]不存在");
        }



        wcsOperateTask.setOperateEndTime(new Date());
        wcsOperateTask.setTaskStatus(WcsTaskRunStatusEnum.COMPLETED.getCode());
        return wcsOperateTaskMapper.updateById(wcsOperateTask) > 0;

        // WmsWcsInfo wmsWcsInfo = new WmsWcsInfo();
        // wmsWcsInfo.put(WmsWcsInfo.TYPE, wmsWcsCallbackInfo.getType());
        // wmsWcsInfo.put(WmsWcsInfo.TASKTYPE, wmsWcsCallbackInfo.getTaskType());
        // wmsWcsInfo.put(WmsWcsInfo.AREATYPE, wmsWcsCallbackInfo.getAreaType());
        // wmsWcsInfo.put(WmsWcsInfo.TRAY_CODE, wmsWcsCallbackInfo.getTrayCode());
        // wmsWcsInfo.put(WmsWcsInfo.START_AREA_CODE, wmsWcsCallbackInfo.getStartAreaCode());
        // wmsWcsInfo.put(WmsWcsInfo.END_AREA_CODE, wmsWcsCallbackInfo.getEndAreaCode());
        // wmsWcsInfo.put(WmsWcsInfo.START_LOCATION_CODE, wmsWcsCallbackInfo.getStartLocationCode());
        // wmsWcsInfo.put(WmsWcsInfo.END_LOCATION_CODE, wmsWcsCallbackInfo.getEndLocationCode());
        // wmsWcsInfo.put(WmsWcsInfo.DOC, wmsWcsCallbackInfo.getDoc());
        // wmsWcsInfo.put(WmsWcsInfo.SERVICE_ID, wmsWcsCallbackInfo.getServiceId());
        //
        // //AGV到位信号消费信息
        // locationService.agvInfo(wmsWcsInfo);
        //
        // //如果AGV上架类型为入库，则到位后调用触发响应的堆垛机过来进行搬运
        // boolean isInSignal = agvPositionSignalDto.getAgvSignalType().equals(AgvSignalEnum.IN_SIGNAL.getCode());
        // if(isInSignal){
        //     //调用堆垛机设备任务
        //     WmsToWcsTaskReq wmsToWcsTaskReq = JSON.parseObject(JSON.toJSONString(wmsWcsInfo), WmsToWcsTaskReq.class);
        //     wcsTaskApiService.stackerTask(wmsToWcsTaskReq,taskNo);
        // }

        //将回调记录变成已删除,标识为已消费
        //wmsWcsCallbackInfoService.deleteWmsWcsCallbackInfoById(wmsWcsCallbackInfo.getId());
    }


    /**
     * 根据任务号获取WCS任务信息
     *
     * @return
     */
    public WcsOperateTask getWcsTaskInfoByTaskNo(String taskNo) {
        WcsOperateTask wcsOperateTask = wcsOperateTaskMapper.selectOne(new QueryWrapper<WcsOperateTask>()
                .eq("del_flag", DelFlagEnum.DEL_NO.getCode())
                .eq("task_no", taskNo)
        );
        if (wcsOperateTask == null) {
            throw new ServiceException("任务号不存在");
        }
        return wcsOperateTask;
    }

    public List<StackerAndTransferStatusInfo> getStackerStatusList() {
        List<StackerAndTransferStatusInfo> allStackerAndTransferInfo = wcsTaskApiService.getAllStackerAndTransferInfo();
        return allStackerAndTransferInfo;
    }

    @Value("${stacker.remote.api}")
    private String stackerRemoteApiUrl;

    /**
     * 堆垛机任务开始执行的信号推送
     * @param taskNo
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean stackerStartActionSignal(String taskNo) {
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        WcsOperateTask wcsOperateTask = wcsOperateTaskMapper.selectOne(new QueryWrapper<WcsOperateTask>()
                    .eq("del_flag", DelFlagEnum.DEL_NO.getCode())
                    .eq("task_no", taskNo)
            );

        if(wcsOperateTask == null) {
            throw new ServiceException("堆垛机任务,任务号[" + taskNo + "]不存在");
        }
        wcsOperateTask.setOperateBeginTime(new Date());
        wcsOperateTask.setTaskStatus(WcsTaskRunStatusEnum.IN_PROGRESS.getCode());
        wcsOperateTaskMapper.updateById(wcsOperateTask);

        return true;
    }

    //发送getAGVDone请求
    private void sendGetAGVDone(AgvPutWayFinshLog search) {
        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpPost request = new HttpPost(stackerRemoteApiUrl + "/stackerTasks/getAGVDone");
        try {
            // 设置请求体
            StringEntity requestBody = new StringEntity("\"" + search.getLineNo() + "\"");
            request.setHeader("Content-Type", "application/json");
            request.setEntity(requestBody);
            HttpResponse response = httpClient.execute(request);
            HttpEntity responseEntity = response.getEntity();
            if (responseEntity != null) {
                String responseBody = EntityUtils.toString(responseEntity);
                log.info("调用WCS的getAGVDone完成,响应参数:{}", responseBody);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
