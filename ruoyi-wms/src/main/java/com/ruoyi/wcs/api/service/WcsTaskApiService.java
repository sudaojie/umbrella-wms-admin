package com.ruoyi.wcs.api.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.WmsWcsInfo;
import com.ruoyi.common.enums.WmsWcsTaskTypeEnum;
import com.ruoyi.common.enums.WmsWcsTypeEnum;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.http.HttpUtils;
import com.ruoyi.wcs.api.domain.WmsToWcsTaskReq;
import com.ruoyi.wcs.constans.WcsConstants;
import com.ruoyi.wcs.domain.WcsOperateTask;
import com.ruoyi.wcs.enums.agv.AgvRespCodeEnum;
import com.ruoyi.wcs.enums.wcs.WcsTaskDeviceTypeEnum;
import com.ruoyi.wcs.enums.wcs.WcsTaskRunStatusEnum;
import com.ruoyi.wcs.req.agv.AgvAddMoveTaskReq;
import com.ruoyi.wcs.req.agv.AgvTaskInfo;
import com.ruoyi.wcs.req.agv.AgvTaskRsp;
import com.ruoyi.wcs.req.stacker.StackerAndTransferStatusInfo;
import com.ruoyi.wcs.req.stacker.StackerSrmStatusRsp;
import com.ruoyi.wcs.req.stacker.StackerTaskReq;
import com.ruoyi.wcs.req.stacker.StackerTaskRsp;
import com.ruoyi.wcs.service.WcsOperateTaskService;
import com.ruoyi.wms.basics.service.WmsTransferLocationService;
import com.ruoyi.wms.check.service.WmsWarehouseCheckDetailService;
import com.ruoyi.wms.enums.TransferLocationArrowEnums;
import com.ruoyi.wms.utils.constant.LhqLocationConstants;
import com.ruoyi.wms.wcstask.domain.WmsWcsCallbackInfo;
import com.ruoyi.wms.wcstask.service.WmsWcsCallbackInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * WCS任务对外api service
 */
@Slf4j
@Service
public class WcsTaskApiService {

    @Autowired
    private WcsOperateTaskService wcsOperateTaskService;

    @Autowired
    private WmsWarehouseCheckDetailService wmsWarehouseCheckDetailService;

    @Autowired
    private WmsTransferLocationService wmsTransferLocationService;

    @Autowired
    private WmsWcsCallbackInfoService wmsWcsCallbackInfoService;

    @Value("${spring.profiles.active}")
    private String profileActive;

    @Value("${stacker.remote.api}")
    private String stackerRemoteApiUrl;

    @Value("${agv.remote.api}")
    private String agvRemoteApiUrl;


    /**
     * avg和堆垛机联动任务
     *
     * @param wmsToWcsTaskReqs
     * @return
     */
    @Transactional(propagation= Propagation.NESTED)
    public AjaxResult agvLinkAgeStacker(List<WmsToWcsTaskReq> wmsToWcsTaskReqs) {
        int size = wmsToWcsTaskReqs.stream().filter(item ->
                item.getTaskType().equals(WmsWcsTaskTypeEnum.CHECK_WAREHOUSEING.getCode()) ||
                        item.getTaskType().equals(WmsWcsTaskTypeEnum.CHECK_OUTBOUND.getCode())
        ).collect(Collectors.toList()).size();

        if(size == 0){
            for (WmsToWcsTaskReq wmsToWcsTaskReq : wmsToWcsTaskReqs) {
                if(CollUtil.isNotEmpty(wmsToWcsTaskReq.getChildInfoList())){
                    for (WmsToWcsTaskReq childReq : wmsToWcsTaskReq.getChildInfoList()) {
                        if(  childReq.getTaskType().equals(WmsWcsTaskTypeEnum.CHECK_WAREHOUSEING.getCode()) ||
                                childReq.getTaskType().equals(WmsWcsTaskTypeEnum.CHECK_OUTBOUND.getCode())){
                            size++;
                        }
                    }
                }
            }
        }

        if(wmsWarehouseCheckDetailService.haveChecking() && CollUtil.isNotEmpty(wmsToWcsTaskReqs) && size == 0){
            throw new ServiceException("盘点任务正在执行，不能操作相关设备");
        }

        //todo agv情况下，执行哪台agv
        if (CollUtil.isNotEmpty(wmsToWcsTaskReqs)) {
                for (WmsToWcsTaskReq wmsToWcsTaskReq : wmsToWcsTaskReqs) {
                    String taskNo = IdUtil.getSnowflakeNextId() + "";
                    String endAreaCode = wmsToWcsTaskReq.getEndAreaCode();
                    String agvId = "";
                    if (endAreaCode.equals("LHQ01")) {
                        agvId = "理货区AGV";
                    } else if (endAreaCode.equals("LSQ01")) {
                        agvId = "晾晒区AGV";
                    }

                    //如果是盘点，只需要调用堆垛机
                    if(wmsToWcsTaskReq.getTaskType().equals(WmsWcsTaskTypeEnum.CHECK_OUTBOUND.getCode()) ||
                        wmsToWcsTaskReq.getTaskType().equals(WmsWcsTaskTypeEnum.CHECK_WAREHOUSEING.getCode())){
                        stackerTask(wmsToWcsTaskReq,taskNo);
                    }else{
                        if (wmsToWcsTaskReq.getType().equals(WmsWcsTypeEnum.TAKETRAY.getCode())) {
                            //取盘(先调用堆垛机,在调用AGV)
                            stackerTask(wmsToWcsTaskReq,taskNo);
                            if (!wmsToWcsTaskReq.getEndLocationCode().contains("csd")) {
                                log.info("触发{}",agvId);
                                //启用AGV
                                agvTask(wmsToWcsTaskReq,taskNo);
                            }
                        } else if (wmsToWcsTaskReq.getType().equals(WmsWcsTypeEnum.PUTTRAY.getCode())) {
                            //回盘(先调用AGV,在调用堆垛机)
                            if (StrUtil.isNotEmpty(wmsToWcsTaskReq.getStartLocationCode())) {
                                //启用AGV
                                agvTask(wmsToWcsTaskReq,taskNo);
                            }
                            stackerTask(wmsToWcsTaskReq,taskNo);
                        } else if (wmsToWcsTaskReq.getType().equals(WmsWcsTypeEnum.RELOCATION.getCode())) {
                            List<WmsToWcsTaskReq> childInfoList = wmsToWcsTaskReq.getChildInfoList();
                            for (WmsToWcsTaskReq toWcsTaskReq : childInfoList) {
                                if(!toWcsTaskReq.getType().equals(WmsWcsTypeEnum.PUTTRAY.getCode())){
                                    stackerTask(toWcsTaskReq,taskNo);
                                    if (!toWcsTaskReq.getEndLocationCode().contains("csd") && (toWcsTaskReq.getType().equals(WmsWcsTypeEnum.TAKETRAY.getCode()))) {
                                        log.info("触发{}",agvId);
                                        //启用AGV
                                        agvTask(toWcsTaskReq,taskNo);
                                    }
                                }else{
                                    //回盘任务，如果agv开启，先调用agv,在调用堆垛机
                                    if (StrUtil.isNotEmpty(toWcsTaskReq.getStartLocationCode())) {
                                        //启用AGV
                                        agvTask(toWcsTaskReq,taskNo);
                                    }
                                    stackerTask(toWcsTaskReq,taskNo);
                                }
                            }
                        }
                    }
                }
            }
        return AjaxResult.success();
    }

    /**
     * 返回agv输送线坐标
     * @param position
     * @return
     */
    public String getAgvCsdPosition(String position){
        String csdPosition="";
        if(position.contains("csd")){
            if("csd_01_01".equals(position)){
                csdPosition="csd_01_04_01_01";
            }else if("csd_01_02".equals(position)){
                csdPosition="csd_01_04_01_30";
            }else if("csd_02_01".equals(position)){
                csdPosition="csd_02_04_01_01";
            }else if("csd_02_02".equals(position)){
                csdPosition="csd_02_04_01_30";
            }else if("csd_03_01".equals(position)){
                csdPosition="csd_03_03_01_01";
            }else if("csd_03_02".equals(position)){
                csdPosition="csd_03_03_01_30";
            }else{
                csdPosition=position;
            }
        }else{
            csdPosition=position;
        }
        return csdPosition;
    }

    /**
     * agv任务
     *
     * @param wmsToWcsTaskReq
     * @return
     */
    public AjaxResult agvTask(WmsToWcsTaskReq wmsToWcsTaskReq,String serviceTaskNo) {
        //String taskNo = IdUtil.getSnowflakeNextId() + "";
        Long time = System.currentTimeMillis();
        String taskNo = String.valueOf((time % Integer.MAX_VALUE) & Integer.MAX_VALUE);
        WcsOperateTask wcsOperateTask = new WcsOperateTask();
        wcsOperateTask.setId(IdUtil.fastSimpleUUID());
        wcsOperateTask.setTaskNo(taskNo);
        wcsOperateTask.setStartAreaCode(wmsToWcsTaskReq.getStartAreaCode());
        wcsOperateTask.setEndAreaCode(wmsToWcsTaskReq.getEndAreaCode());
        wcsOperateTask.setServiceTaskNo(serviceTaskNo);
        wcsOperateTask.setTaskDeviceType(WcsTaskDeviceTypeEnum.AVG.getCode());
        wcsOperateTask.setTaskType(wmsToWcsTaskReq.getTaskType());
        wcsOperateTask.setStartPosition(wmsToWcsTaskReq.getStartLocationCode());
        wcsOperateTask.setEndPosition(wmsToWcsTaskReq.getEndLocationCode());
        wcsOperateTask.setTrayNo(wmsToWcsTaskReq.getTrayCode());
        wcsOperateTask.setOperateBeginTime(new Date());
        wcsOperateTask.setTaskStatus(WcsTaskRunStatusEnum.NOT_STARTED.getCode());
        wcsOperateTask.setInBillNo(wmsToWcsTaskReq.getDoc());
        wcsOperateTask.setOperateType(wmsToWcsTaskReq.getType());

        if(wmsToWcsTaskReq.getType().equals(WmsWcsTypeEnum.TAKETRAY.getCode())){
            if(wmsToWcsTaskReq.getTaskType().equals(WmsWcsTaskTypeEnum.DRYING_OUTBOUND.getCode())){
                String transferLocationCode = wmsTransferLocationService.getTransferLocationCodeByArea(wmsToWcsTaskReq.getStartAreaCode(), TransferLocationArrowEnums.LEFT.getCode());
                wcsOperateTask.setStartPosition(transferLocationCode);
            }else{
                String transferLocationCode = wmsTransferLocationService.getTransferLocationCodeByArea(wmsToWcsTaskReq.getStartAreaCode(), TransferLocationArrowEnums.RIGHT.getCode());
                wcsOperateTask.setStartPosition(transferLocationCode);
            }
        }else if(wmsToWcsTaskReq.getType().equals(WmsWcsTypeEnum.PUTTRAY.getCode())) {
            if(wmsToWcsTaskReq.getTaskType().equals(WmsWcsTaskTypeEnum.DRY_STORAGE.getCode())) {
                String transferLocationCode = wmsTransferLocationService.getTransferLocationCodeByArea(wmsToWcsTaskReq.getEndAreaCode(), TransferLocationArrowEnums.LEFT.getCode());
                wcsOperateTask.setEndPosition(transferLocationCode);
            }else{
                String transferLocationCode = wmsTransferLocationService.getTransferLocationCodeByArea(wmsToWcsTaskReq.getEndAreaCode(), TransferLocationArrowEnums.RIGHT.getCode());
                wcsOperateTask.setEndPosition(transferLocationCode);
            }
        }
        try {
            //wcsOperateTaskService.insertWcsOperateTask(wcsOperateTask);
            AgvAddMoveTaskReq agvAddMoveTaskReq = new AgvAddMoveTaskReq();
            agvAddMoveTaskReq.setInStoragesNum(getAgvCsdPosition(wcsOperateTask.getEndPosition()));
            agvAddMoveTaskReq.setOutStoragesNum(getAgvCsdPosition(wcsOperateTask.getStartPosition()));
            AgvTaskInfo agvTaskInfo=new AgvTaskInfo();
            agvTaskInfo.setTaskId(Integer.valueOf(taskNo));
            agvAddMoveTaskReq.setTaskInfo(agvTaskInfo);
//            agvAddMoveTaskReq.setDeviceNo(agvDevice(wcsOperateTask));

            AgvTaskRsp agvTaskRsp = new AgvTaskRsp();
            agvTaskRsp.setState(10000);

            if(profileActive.equals("prod")){
                //todo http 调用  avg厂商搬运任务接口
                ObjectMapper objectMapper = new ObjectMapper();
                try {
                    log.info("调用AGV搬运任务:请求参数:{}",JSON.toJSONString(agvAddMoveTaskReq));
                    String msg=HttpUtils.sendPost(agvRemoteApiUrl+"/addStorageTask",objectMapper.writeValueAsString(agvAddMoveTaskReq));
                    wcsOperateTask.setTaskRspJson(msg);
                    log.info("调用AGV搬运任务:响应参数:{}",msg);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }


            wcsOperateTask.setTaskReqJson(JSON.toJSONString(agvAddMoveTaskReq));
            wcsOperateTask.setWaitTaskReqJson(JSON.toJSONString(wmsToWcsTaskReq));
            if (agvTaskRsp.isSuccess()) {
                //wcsOperateTask.setTaskStatus(WcsTaskRunStatusEnum.COMPLETED.getCode());
            } else {
                wcsOperateTask.setTaskStatus(WcsTaskRunStatusEnum.IN_EXCEPTION.getCode());
                wcsOperateTask.setErrMsg(AgvRespCodeEnum.getInstance(agvTaskRsp.getState()).getName());
            }
            wcsOperateTask.setOperateEndTime(new Date());

            WcsConstants.wcsOperateTaskQueue.put(wcsOperateTask);

            //wcsOperateTaskService.updateById(wcsOperateTask);

             //添加WMS/WCS回调信息（给到位信号提供准备）
             //WmsWcsCallbackInfo wmsWcsCallbackInfo = new WmsWcsCallbackInfo();
             //wmsWcsCallbackInfo.setTaskNo(wcsOperateTask.getTaskNo());
             //wmsWcsCallbackInfo.setType(wmsToWcsTaskReq.getType());
             //wmsWcsCallbackInfo.setTaskType(wmsToWcsTaskReq.getTaskType());
             //wmsWcsCallbackInfo.setStartAreaCode(wmsToWcsTaskReq.getStartAreaCode());
             //wmsWcsCallbackInfo.setEndAreaCode(wmsToWcsTaskReq.getEndAreaCode());
             //wmsWcsCallbackInfo.setStartLocationCode(wmsToWcsTaskReq.getStartLocationCode());
             //wmsWcsCallbackInfo.setEndLocationCode(wmsToWcsTaskReq.getEndLocationCode());
             //wmsWcsCallbackInfo.setTrayCode(wmsToWcsTaskReq.getTrayCode());
             //wmsWcsCallbackInfo.setDoc(wmsToWcsTaskReq.getDoc());
             //wmsWcsCallbackInfoService.insertWmsWcsCallbackInfo(wmsWcsCallbackInfo);


            //todo agv到位信号
            Thread.sleep(4000);
            log.info("agv到位信号");
            WmsWcsInfo wmsWcsInfo = new WmsWcsInfo();
            wmsWcsInfo.put("startAreaCode", wmsToWcsTaskReq.getStartAreaCode());
            wmsWcsInfo.put("endAreaCode", wmsToWcsTaskReq.getEndAreaCode());
            wmsWcsInfo.put("startLocationCode", wmsToWcsTaskReq.getStartLocationCode());
            wmsWcsInfo.put("endLocationCode", wmsToWcsTaskReq.getEndLocationCode());
            wmsWcsInfo.put("type", wmsToWcsTaskReq.getType());
            wmsWcsInfo.put("taskType", wmsToWcsTaskReq.getTaskType());
            wmsWcsInfo.put("trayCode", wmsToWcsTaskReq.getTrayCode());
            wmsWcsInfo.put("doc", wmsToWcsTaskReq.getDoc());
            WcsConstants.agvCallBackQueue.put(wmsWcsInfo);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return AjaxResult.success();
    }

    //获取AGV设备号
    private String agvDevice(WcsOperateTask wcsOperateTask){
        String deviceNo = "";
        //如果上架/回盘/托盘回收
        if (WmsWcsTaskTypeEnum.NORMAL_WAREHOUSING.getCode().equals(wcsOperateTask.getTaskType()) ||
                WmsWcsTaskTypeEnum.NORMAL_EMPTYTRAY.getCode().equals(wcsOperateTask.getTaskType()) ||
                WmsWcsTaskTypeEnum.NORMAL_OUTBOUND.getCode().equals(wcsOperateTask.getTaskType())
        ) {
            //取盘
            if ("takeTray".equals(wcsOperateTask.getOperateType())) {
                boolean exists = Arrays.stream(LhqLocationConstants.stagingOnes)
                        .anyMatch(value -> value.equals(wcsOperateTask.getEndPosition()));
                if (exists) {
                    deviceNo = "141";
                } else {
                    deviceNo = "140";
                }
            } else if ("putTray".equals(wcsOperateTask.getOperateType())) {
                boolean exists = Arrays.stream(LhqLocationConstants.stagingOnes)
                        .anyMatch(value -> value.equals(wcsOperateTask.getStartPosition()));
                if (exists) {
                    deviceNo = "141";
                } else {
                    deviceNo = "140";
                }
            }
        }else{
            deviceNo = "141";
        }
        return deviceNo;
    }

    /**
     * 堆垛机任务
     *
     * @param wmsToWcsTaskReq
     * @return
     */
    public AjaxResult stackerTask(WmsToWcsTaskReq wmsToWcsTaskReq,String serviceTaskNo) {
        String taskNo = IdUtil.getSnowflakeNextId() + "";
        WcsOperateTask wcsOperateTask = new WcsOperateTask();
        wcsOperateTask.setId(IdUtil.fastSimpleUUID());
        wcsOperateTask.setTaskNo(taskNo);
        wcsOperateTask.setStartAreaCode(wmsToWcsTaskReq.getStartAreaCode());
        wcsOperateTask.setEndAreaCode(wmsToWcsTaskReq.getEndAreaCode());
        wcsOperateTask.setServiceTaskNo(serviceTaskNo);
        wcsOperateTask.setTaskDeviceType(WcsTaskDeviceTypeEnum.STACKER.getCode());
        wcsOperateTask.setTaskType(wmsToWcsTaskReq.getTaskType());
        wcsOperateTask.setStartPosition(wmsToWcsTaskReq.getStartLocationCode());
        wcsOperateTask.setEndPosition(wmsToWcsTaskReq.getEndLocationCode());
        String taskType="1";
        if(wmsToWcsTaskReq.getType().equals(WmsWcsTypeEnum.TAKETRAY.getCode())){
            taskType="1";
        }else if(wmsToWcsTaskReq.getType().equals(WmsWcsTypeEnum.PUTTRAY.getCode())){
            taskType="2";
        }else if(wmsToWcsTaskReq.getType().equals(WmsWcsTypeEnum.RELOCATION.getCode())){
            taskType="3";
        }else  if(wmsToWcsTaskReq.getTaskType().equals(WmsWcsTaskTypeEnum.CHECK_WAREHOUSEING.getCode())){
            taskType = "11";
        }
        // else  if(wmsToWcsTaskReq.getTaskType().equals(WmsWcsTaskTypeEnum.CHECK_OUTBOUND.getCode())){
        //     taskType = "5";
        // }
        if(wmsToWcsTaskReq.getType().equals(WmsWcsTypeEnum.TAKETRAY.getCode())){
            if(wmsToWcsTaskReq.getTaskType().equals(WmsWcsTaskTypeEnum.DRYING_OUTBOUND.getCode())){
                String transferLocationCode = wmsTransferLocationService.getTransferLocationCodeByArea(wmsToWcsTaskReq.getStartAreaCode(), TransferLocationArrowEnums.LEFT.getCode());
                wcsOperateTask.setEndPosition(transferLocationCode);
            }else{
                String transferLocationCode = wmsTransferLocationService.getTransferLocationCodeByArea(wmsToWcsTaskReq.getStartAreaCode(), TransferLocationArrowEnums.RIGHT.getCode());
                wcsOperateTask.setEndPosition(transferLocationCode);
            }
        }else if(wmsToWcsTaskReq.getType().equals(WmsWcsTypeEnum.PUTTRAY.getCode())) {
            if(wmsToWcsTaskReq.getTaskType().equals(WmsWcsTaskTypeEnum.DRY_STORAGE.getCode())) {
                String transferLocationCode = wmsTransferLocationService.getTransferLocationCodeByArea(wmsToWcsTaskReq.getEndAreaCode(), TransferLocationArrowEnums.LEFT.getCode());
                wcsOperateTask.setStartPosition(transferLocationCode);
            }else{
                String transferLocationCode = wmsTransferLocationService.getTransferLocationCodeByArea(wmsToWcsTaskReq.getEndAreaCode(), TransferLocationArrowEnums.RIGHT.getCode());
                wcsOperateTask.setStartPosition(transferLocationCode);
            }
        }

        //获取堆垛机号和存储库区
        String AreaId=getAreaId(wmsToWcsTaskReq.getStartAreaCode(),wmsToWcsTaskReq.getEndAreaCode(),wcsOperateTask.getStartPosition(),wcsOperateTask.getEndPosition());
        wcsOperateTask.setTrayNo(wmsToWcsTaskReq.getTrayCode());
        wcsOperateTask.setOperateBeginTime(new Date());
        wcsOperateTask.setTaskStatus(WcsTaskRunStatusEnum.NOT_STARTED.getCode());
        wcsOperateTask.setInBillNo(wmsToWcsTaskReq.getDoc());
        wcsOperateTask.setOperateType(wmsToWcsTaskReq.getType());
        try {
            //wcsOperateTaskService.insertWcsOperateTask(wcsOperateTask);
            StackerTaskReq stackerTaskReq = new StackerTaskReq();
            stackerTaskReq.setDoBeginTime(DateUtil.formatDateTime(new Date()));
            stackerTaskReq.setId(IdUtil.getSnowflakeNextId());
            stackerTaskReq.setBuildTime(DateUtil.formatDateTime(new Date()));
            //忽略字段  穿空值
            stackerTaskReq.setBillNumber(taskNo);
            stackerTaskReq.setDetailId("");
            stackerTaskReq.setDoBeginTime("");
            stackerTaskReq.setDoEndTime("");
            //忽略字段 end
            stackerTaskReq.setIsHand("1");
            stackerTaskReq.setState("0");
            stackerTaskReq.setTaskType(taskType);
            stackerTaskReq.setTrayCode(wcsOperateTask.getTrayNo());
            List<String> list = getWarehouseLocation(wcsOperateTask.getStartPosition());
            stackerTaskReq.setDownZ(list.get(0));
            stackerTaskReq.setDownX(list.get(1));
            stackerTaskReq.setDownY(list.get(2));

            stackerTaskReq.setLocCode(AreaId);
            stackerTaskReq.setMachineNum(Integer.parseInt(AreaId));
            List<String> list2 = getWarehouseLocation(wcsOperateTask.getEndPosition());
            stackerTaskReq.setUpZ(list2.get(0));
            stackerTaskReq.setUpX(list2.get(1));
            stackerTaskReq.setUpY(list2.get(2));
            StackerTaskRsp stackerTaskRsp = new StackerTaskRsp();
            stackerTaskRsp.setState(10000);
            if(profileActive.equals("prod")){
                //todo http 调用  苏立堆垛机厂商搬运任务接口
                ObjectMapper objectMapper = new ObjectMapper();
                try {
                    log.info("stackerTask接口，请求参数:{}",JSON.toJSONString(stackerTaskReq));
                    String resp = HttpUtils.sendPost(stackerRemoteApiUrl + "/stackerTasks", objectMapper.writeValueAsString(stackerTaskReq));
                    stackerTaskRsp = JSON.parseObject(resp,StackerTaskRsp.class);
                    log.info("调用堆垛机执行接口成功，返回:{}",resp);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            wcsOperateTask.setTaskReqJson(JSON.toJSONString(stackerTaskReq));
            wcsOperateTask.setTaskRspJson(JSON.toJSONString(stackerTaskRsp));
            wcsOperateTask.setWaitTaskReqJson(JSON.toJSONString(wmsToWcsTaskReq));
            if (stackerTaskRsp.isSuccess()) {
                //todo 堆垛机到位信号
                Thread.sleep(1500);
                if(profileActive.equals("prod")){
                    //添加WMS/WCS回调信息（给到位信号提供准备）
                    WmsWcsCallbackInfo wmsWcsCallbackInfo = new WmsWcsCallbackInfo();
                    wmsWcsCallbackInfo.setTaskNo(wcsOperateTask.getTaskNo());
                    wmsWcsCallbackInfo.setType(wmsToWcsTaskReq.getType());
                    wmsWcsCallbackInfo.setTaskType(wmsToWcsTaskReq.getTaskType());
                    wmsWcsCallbackInfo.setStartAreaCode(wmsToWcsTaskReq.getStartAreaCode());
                    wmsWcsCallbackInfo.setEndAreaCode(wmsToWcsTaskReq.getEndAreaCode());
                    wmsWcsCallbackInfo.setStartLocationCode(wmsToWcsTaskReq.getStartLocationCode());
                    wmsWcsCallbackInfo.setEndLocationCode(wmsToWcsTaskReq.getEndLocationCode());
                    wmsWcsCallbackInfo.setTrayCode(wmsToWcsTaskReq.getTrayCode());
                    wmsWcsCallbackInfo.setDoc(wmsToWcsTaskReq.getDoc());
                    wmsWcsCallbackInfo.setServiceId(wmsToWcsTaskReq.getServiceId());
                    wmsWcsCallbackInfo.setMoveLast(wmsToWcsTaskReq.getMoveLast());
                    //wmsWcsCallbackInfoService.insertWmsWcsCallbackInfo(wmsWcsCallbackInfo);
                    WcsConstants.wcsCallbackInfoQueue.put(wmsWcsCallbackInfo);
                }else{
                    WmsWcsInfo wmsWcsInfo = new WmsWcsInfo();
                    wmsWcsInfo.put("startAreaCode", wmsToWcsTaskReq.getStartAreaCode());
                    wmsWcsInfo.put("endAreaCode", wmsToWcsTaskReq.getEndAreaCode());
                    wmsWcsInfo.put("startLocationCode", wmsToWcsTaskReq.getStartLocationCode());
                    wmsWcsInfo.put("endLocationCode", wmsToWcsTaskReq.getEndLocationCode());
                    wmsWcsInfo.put("type", wmsToWcsTaskReq.getType());
                    wmsWcsInfo.put("taskType", wmsToWcsTaskReq.getTaskType());
                    wmsWcsInfo.put("trayCode", wmsToWcsTaskReq.getTrayCode());
                    wmsWcsInfo.put("doc", wmsToWcsTaskReq.getDoc());
                    wmsWcsInfo.put("serviceId", wmsToWcsTaskReq.getServiceId());
                    wmsWcsInfo.put("moveLast", wmsToWcsTaskReq.getMoveLast());
                    WcsConstants.stackerCallBackQueue.put(wmsWcsInfo);
                    wcsOperateTask.setTaskStatus(WcsTaskRunStatusEnum.COMPLETED.getCode());
                }
            } else {
                wcsOperateTask.setTaskStatus(WcsTaskRunStatusEnum.IN_EXCEPTION.getCode());
                wcsOperateTask.setErrMsg("堆垛机执行任务异常");
            }
            wcsOperateTask.setOperateEndTime(new Date());
            WcsConstants.wcsOperateTaskQueue.put(wcsOperateTask);


            //wcsOperateTaskService.updateById(wcsOperateTask);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return AjaxResult.success();
    }

    /**
     * 获取巷道 和堆垛机  因为苏立是相反的，所以巷道和堆垛机编号要对调
     * @param startAreaCode 起点库区
     * @param endAreaCode 终点库区
     * @return
     */
    public String getAreaId(String startAreaCode,String endAreaCode,String startPosition,String endPosition){
        String areaId="1";
        if(!startPosition.contains("csd")){
            areaId=startPosition.substring(3, 5);
        }else if(!endPosition.contains("csd")){
            areaId=endPosition.substring(3, 5);
        }else{
            areaId="1";
        }
        if(areaId.equals("01")||areaId.equals("02")||areaId.equals("03")||areaId.equals("04")){
            areaId="1";
        }else if(areaId.equals("05")||areaId.equals("06")||areaId.equals("07")||areaId.equals("08")){
            areaId="2";
        }else{
            areaId="3";
        }
        return areaId;
    }

    //根据库位信息返回坐标集合
    public List<String> getWarehouseLocation(String LocationCode){
        List<String> warehouseLocation=new ArrayList<>();
        //判断是否传送带  顺序分别是排 列 层
        if("csd_01_01".equals(LocationCode)){
            warehouseLocation.add("4");
            warehouseLocation.add("30");
            warehouseLocation.add("1");
        }else if("csd_01_02".equals(LocationCode)){
            warehouseLocation.add("4");
            warehouseLocation.add("1");
            warehouseLocation.add("1");
        }else if("csd_02_01".equals(LocationCode)){
            warehouseLocation.add("3");
            warehouseLocation.add("30");
            warehouseLocation.add("1");
        }else if("csd_02_02".equals(LocationCode)){
            warehouseLocation.add("4");
            warehouseLocation.add("1");
            warehouseLocation.add("1");
        }else if("csd_03_01".equals(LocationCode)){
            warehouseLocation.add("3");
            warehouseLocation.add("30");
            warehouseLocation.add("1");
        }else if("csd_03_02".equals(LocationCode)){
            warehouseLocation.add("3");
            warehouseLocation.add("1");
            warehouseLocation.add("1");
        }else{
            String []tempWarehouseLocation=LocationCode.split("_");
            int platoon=Integer.parseInt(tempWarehouseLocation[1]);
            if(platoon==3||platoon==6||platoon==11){
                platoon=1;
            }else if(platoon==2||platoon==7||platoon==10){
                platoon=2;
            }else if(platoon==4||platoon==5||platoon==12){
                platoon=3;
            }else if(platoon==1||platoon==8||platoon==9){
                platoon=4;
            }
            warehouseLocation.add(platoon+"");
            warehouseLocation.add(Integer.parseInt(tempWarehouseLocation[2])+"");
            warehouseLocation.add(Integer.parseInt(tempWarehouseLocation[3])+"");
        }
        return warehouseLocation;
    }


    /**
     * 只调用堆垛机任务
     *
     * @param wmsToWcsTaskReq
     * @return
     */
    //public AjaxResult stackerTaskOnly(WmsToWcsTaskReq wmsToWcsTaskReq,String serviceTaskNo) {
    //    String taskNo = IdUtil.getSnowflakeNextId() + "";
    //    WcsOperateTask wcsOperateTask = new WcsOperateTask();
    //    wcsOperateTask.setId(IdUtil.fastSimpleUUID());
    //    wcsOperateTask.setTaskNo(taskNo);
    //    wcsOperateTask.setStartAreaCode(wmsToWcsTaskReq.getStartAreaCode());
    //    wcsOperateTask.setEndAreaCode(wmsToWcsTaskReq.getEndAreaCode());
    //    wcsOperateTask.setServiceTaskNo(serviceTaskNo);
    //    wcsOperateTask.setTaskDeviceType(WcsTaskDeviceTypeEnum.STACKER.getCode());
    //    wcsOperateTask.setTaskType(wmsToWcsTaskReq.getTaskType());
    //    wcsOperateTask.setStartPosition(wmsToWcsTaskReq.getStartLocationCode());
    //    wcsOperateTask.setEndPosition(wmsToWcsTaskReq.getEndLocationCode());
    //    String taskType="1";
    //    if(wmsToWcsTaskReq.getType().equals(WmsWcsTypeEnum.TAKETRAY.getCode())){
    //        taskType="1";
    //    }else if(wmsToWcsTaskReq.getType().equals(WmsWcsTypeEnum.PUTTRAY.getCode())){
    //        taskType="2";
    //    }else if(wmsToWcsTaskReq.getType().equals(WmsWcsTypeEnum.RELOCATION.getCode())){
    //        taskType="3";
    //    }else  if(wmsToWcsTaskReq.getTaskType().equals(WmsWcsTaskTypeEnum.CHECK_WAREHOUSEING.getCode())){
    //        taskType = "11";
    //    }
    //    // else  if(wmsToWcsTaskReq.getTaskType().equals(WmsWcsTaskTypeEnum.CHECK_OUTBOUND.getCode())){
    //    //     taskType = "5";
    //    // }
    //    if(wmsToWcsTaskReq.getType().equals(WmsWcsTypeEnum.TAKETRAY.getCode())){
    //        if(wmsToWcsTaskReq.getTaskType().equals(WmsWcsTaskTypeEnum.DRYING_OUTBOUND.getCode())){
    //            String transferLocationCode = wmsTransferLocationService.getTransferLocationCodeByArea(wmsToWcsTaskReq.getStartAreaCode(), TransferLocationArrowEnums.LEFT.getCode());
    //            wcsOperateTask.setEndPosition(transferLocationCode);
    //        }else{
    //            String transferLocationCode = wmsTransferLocationService.getTransferLocationCodeByArea(wmsToWcsTaskReq.getStartAreaCode(), TransferLocationArrowEnums.RIGHT.getCode());
    //            wcsOperateTask.setEndPosition(transferLocationCode);
    //        }
    //    }else if(wmsToWcsTaskReq.getType().equals(WmsWcsTypeEnum.PUTTRAY.getCode())) {
    //        if(wmsToWcsTaskReq.getTaskType().equals(WmsWcsTaskTypeEnum.DRY_STORAGE.getCode())) {
    //            String transferLocationCode = wmsTransferLocationService.getTransferLocationCodeByArea(wmsToWcsTaskReq.getEndAreaCode(), TransferLocationArrowEnums.LEFT.getCode());
    //            wcsOperateTask.setStartPosition(transferLocationCode);
    //        }else{
    //            String transferLocationCode = wmsTransferLocationService.getTransferLocationCodeByArea(wmsToWcsTaskReq.getEndAreaCode(), TransferLocationArrowEnums.RIGHT.getCode());
    //            wcsOperateTask.setStartPosition(transferLocationCode);
    //        }
    //    }
    //
    //    //获取堆垛机号和存储库区
    //    String AreaId=getAreaId(wmsToWcsTaskReq.getStartAreaCode(),wmsToWcsTaskReq.getEndAreaCode(),wcsOperateTask.getStartPosition(),wcsOperateTask.getEndPosition());
    //    wcsOperateTask.setTrayNo(wmsToWcsTaskReq.getTrayCode());
    //    wcsOperateTask.setOperateBeginTime(new Date());
    //    wcsOperateTask.setTaskStatus(WcsTaskRunStatusEnum.NOT_STARTED.getCode());
    //    wcsOperateTask.setInBillNo(wmsToWcsTaskReq.getDoc());
    //    wcsOperateTask.setOperateType(wmsToWcsTaskReq.getType());
    //    try {
    //        wcsOperateTaskService.insertWcsOperateTask(wcsOperateTask);
    //        StackerTaskReq stackerTaskReq = new StackerTaskReq();
    //        stackerTaskReq.setDoBeginTime(DateUtil.formatDateTime(new Date()));
    //        stackerTaskReq.setId(IdUtil.getSnowflakeNextId());
    //        stackerTaskReq.setBuildTime(DateUtil.formatDateTime(new Date()));
    //        //忽略字段  穿空值
    //        stackerTaskReq.setBillNumber(taskNo);
    //        stackerTaskReq.setDetailId("");
    //        stackerTaskReq.setDoBeginTime("");
    //        stackerTaskReq.setDoEndTime("");
    //        //忽略字段 end
    //        stackerTaskReq.setIsHand("1");
    //        stackerTaskReq.setState("0");
    //        stackerTaskReq.setTaskType(taskType);
    //        stackerTaskReq.setTrayCode(wcsOperateTask.getTrayNo());
    //        List<String> list = getWarehouseLocation(wcsOperateTask.getStartPosition());
    //        stackerTaskReq.setDownZ(list.get(0));
    //        stackerTaskReq.setDownX(list.get(1));
    //        stackerTaskReq.setDownY(list.get(2));
    //
    //        stackerTaskReq.setLocCode(AreaId);
    //        stackerTaskReq.setMachineNum(Integer.parseInt(AreaId));
    //        List<String> list2 = getWarehouseLocation(wcsOperateTask.getEndPosition());
    //        stackerTaskReq.setUpZ(list2.get(0));
    //        stackerTaskReq.setUpX(list2.get(1));
    //        stackerTaskReq.setUpY(list2.get(2));
    //        StackerTaskRsp stackerTaskRsp = new StackerTaskRsp();
    //        stackerTaskRsp.setState(10000);
    //        if(profileActive.equals("prod")){
    //            //todo http 调用  苏立堆垛机厂商搬运任务接口
    //            ObjectMapper objectMapper = new ObjectMapper();
    //            try {
    //                log.info("stackerTaskOnly接口，请求参数:{}",JSON.toJSONString(stackerTaskReq));
    //                String resp = HttpUtils.sendPost(stackerRemoteApiUrl + "/stackerTasks", objectMapper.writeValueAsString(stackerTaskReq));
    //                stackerTaskRsp = JSON.parseObject(resp,StackerTaskRsp.class);
    //                log.info("调用堆垛机执行接口成功，返回:{}",resp);
    //            } catch (Exception e) {
    //                e.printStackTrace();
    //            }
    //        }
    //
    //        wcsOperateTask.setTaskReqJson(JSON.toJSONString(stackerTaskReq));
    //        wcsOperateTask.setTaskRspJson(JSON.toJSONString(stackerTaskRsp));
    //        wcsOperateTask.setWaitTaskReqJson(JSON.toJSONString(wmsToWcsTaskReq));
    //        if (stackerTaskRsp.isSuccess()) {
    //            //todo 堆垛机到位信号
    //            Thread.sleep(1500);
    //            log.info("堆垛机到位信号");
    //        } else {
    //            wcsOperateTask.setTaskStatus(WcsTaskRunStatusEnum.IN_EXCEPTION.getCode());
    //            wcsOperateTask.setErrMsg("堆垛机执行任务异常");
    //        }
    //        wcsOperateTask.setOperateEndTime(new Date());
    //        wcsOperateTaskService.updateById(wcsOperateTask);
    //
    //    } catch (Exception e) {
    //        e.printStackTrace();
    //    }
    //    return AjaxResult.success();
    //}


    /**
     * 获取堆垛机在线状态信息
     * @return
     */
    public List<StackerSrmStatusRsp> getStackerSrmStatusList(){
        String respJson = HttpUtils.sendGet(stackerRemoteApiUrl + "/stackerTasks/getSRMStatus");
        List<StackerSrmStatusRsp> stackerSrmStatusRsps = JSON.parseArray(respJson, StackerSrmStatusRsp.class);
        return stackerSrmStatusRsps;
    }

    /**
     * 过滤获取可用的堆垛机编号集合信息
     * @return
     */
    public List<Integer> getEnableStacker(){
        if(profileActive.equals("prod")){
            List<StackerSrmStatusRsp> stackerSrmStatusList = getStackerSrmStatusList();
            List<Integer> enableStackerId = stackerSrmStatusList.parallelStream()
                    .filter(item -> item.getOnlineStatus()==1 && item.getTransferStatus() == 1)
                    .map(StackerSrmStatusRsp::getStackerId).collect(Collectors.toList());
            return enableStackerId;
        }else{
            return new ArrayList<Integer>(){{
                add(1);
                add(2);
                add(3);
            }};
        }
    }

    /**
     * 获取库房内所有堆垛机/传输带状态信息
     * @return
     */
    public List<StackerAndTransferStatusInfo> getAllStackerAndTransferInfo(){
        List<StackerAndTransferStatusInfo> stackerAndTransferStatusInfos = new ArrayList<>();
        if(profileActive.equals("prod")){
            List<StackerSrmStatusRsp> stackerSrmStatusList = getStackerSrmStatusList();
            if(CollUtil.isEmpty(stackerSrmStatusList)){
                throw new ServiceException("堆垛机都处于手动状态，请检查");
            }
            stackerAndTransferStatusInfos.add(new StackerAndTransferStatusInfo(){{
                setName("堆垛机1");
                setState(stackerSrmStatusList.get(2).getOnlineStatus() == 1? "联机" : "手动");
            }});

            stackerAndTransferStatusInfos.add(new StackerAndTransferStatusInfo(){{
                setName("输送带1");
                setState(getTransferStatus(stackerSrmStatusList.get(2).getTransferStatus()));
            }});

            stackerAndTransferStatusInfos.add(new StackerAndTransferStatusInfo(){{
                setName("堆垛机2");
                setState(stackerSrmStatusList.get(1).getOnlineStatus() == 1? "联机" : "手动");
            }});

            stackerAndTransferStatusInfos.add(new StackerAndTransferStatusInfo(){{
                setName("输送带2");
                setState(getTransferStatus(stackerSrmStatusList.get(1).getTransferStatus()));
            }});
            stackerAndTransferStatusInfos.add(new StackerAndTransferStatusInfo(){{
                setName("堆垛机3");
                setState(stackerSrmStatusList.get(0).getOnlineStatus() == 1? "联机" : "手动");
            }});

            stackerAndTransferStatusInfos.add(new StackerAndTransferStatusInfo(){{
                setName("输送带3");
                setState(getTransferStatus(stackerSrmStatusList.get(0).getTransferStatus()));
            }});

        }else{
            stackerAndTransferStatusInfos.add(new StackerAndTransferStatusInfo(){{
                setName("堆垛机1");
                setState("联机");
            }});

            stackerAndTransferStatusInfos.add(new StackerAndTransferStatusInfo(){{
                setName("输送带1");
                setState("联机");
            }});


            stackerAndTransferStatusInfos.add(new StackerAndTransferStatusInfo(){{
                setName("堆垛机2");
                setState("联机");
            }});

            stackerAndTransferStatusInfos.add(new StackerAndTransferStatusInfo(){{
                setName("输送带2");
                setState("联机");
            }});

            stackerAndTransferStatusInfos.add(new StackerAndTransferStatusInfo(){{
                setName("堆垛机3");
                setState("联机");
            }});

            stackerAndTransferStatusInfos.add(new StackerAndTransferStatusInfo(){{
                setName("输送带3");
                setState("联机");
            }});
        }
        return stackerAndTransferStatusInfos;
    }


    public String getTransferStatus(Integer transferStatus){
        if(transferStatus == 0){
            return "手动";
        }else if(transferStatus == 1){
            return "联机";
        }else if(transferStatus == 2){
            return "故障";
        }else{
            return "";
        }
    }

    /**
     * 根据可用的堆垛机编号集合获取对应的库区编号集合
     * @param enableStackerId
     * @return
     */
    public List<String> getStoreAreaIdByEnableStackerId(List<Integer> enableStackerId){
        List<String> areaIds = new ArrayList<>();
        for (Integer integer : enableStackerId) {
            if(integer == 1){
                areaIds.add("CCQ01");
            }
            if(integer == 2){
                areaIds.add("CCQ02");
            }
            if(integer == 3){
                areaIds.add("CCQ03");
            }
        }
        return areaIds;
    }

    //todo 堆垛机和AGV是否有正在执行中的任务
    public boolean haveInWcsTask(){
        return WcsConstants.agvCallBackQueue.size() >0 ||  WcsConstants.stackerCallBackQueue.size() > 0;
    }
}
