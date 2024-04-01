package com.ruoyi.wcs.api.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.WmsWcsTaskTypeEnum;
import com.ruoyi.common.enums.WmsWcsTypeEnum;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.wcs.api.domain.WmsToWcsTaskReq;
import com.ruoyi.wcs.constans.WcsConstants;
import com.ruoyi.wcs.domain.WcsOperateTask;
import com.ruoyi.wcs.enums.agv.AgvRespCodeEnum;
import com.ruoyi.wcs.enums.wcs.WcsTaskDeviceTypeEnum;
import com.ruoyi.wcs.enums.wcs.WcsTaskRunStatusEnum;
import com.ruoyi.wcs.req.agv.AgvAddMoveTaskReq;
import com.ruoyi.wcs.req.agv.AgvTaskRsp;
import com.ruoyi.wcs.req.stacker.StackerTaskReq;
import com.ruoyi.wcs.req.stacker.StackerTaskRsp;
import com.ruoyi.wcs.service.WcsOperateTaskService;
import com.ruoyi.wms.basics.service.WmsTransferLocationService;
import com.ruoyi.wms.check.service.WmsWarehouseCheckDetailService;
import com.ruoyi.wms.enums.TransferLocationArrowEnums;
import com.ruoyi.wms.wcstask.domain.WmsWcsCallbackInfo;
import com.ruoyi.wms.wcstask.service.WmsWcsCallbackInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * WCS任务对外api service(现场联调)
 */
@Slf4j
@Service
public class WcsTaskApiServiceSite {

    @Autowired
    private WcsOperateTaskService wcsOperateTaskService;

    @Autowired
    private WmsWarehouseCheckDetailService wmsWarehouseCheckDetailService;

    @Autowired
    private WmsTransferLocationService wmsTransferLocationService;

    @Autowired
    private WmsWcsCallbackInfoService wmsWcsCallbackInfoService;


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
                    if(wmsToWcsTaskReq.getTaskType().equals(WmsWcsTaskTypeEnum.CHECK_OUTBOUND) ||
                        wmsToWcsTaskReq.getTaskType().equals(WmsWcsTaskTypeEnum.CHECK_WAREHOUSEING)){
                        stackerTaskOnly(wmsToWcsTaskReq,taskNo);
                    }else{
                        if (wmsToWcsTaskReq.getType().equals(WmsWcsTypeEnum.TAKETRAY.getCode())) {
                            //取盘(先调用堆垛机,在调用AGV)
                            stackerTask(wmsToWcsTaskReq,taskNo);
                            //if (!wmsToWcsTaskReq.getEndLocationCode().contains("csd")) {
                            //    log.info("触发{}",agvId);
                            //    //启用AGV
                            //    agvTask(wmsToWcsTaskReq,taskNo);
                            //}
                        } else if (wmsToWcsTaskReq.getType().equals(WmsWcsTypeEnum.PUTTRAY.getCode())) {
                            //回盘(先调用AGV,在调用堆垛机)
                            if (StrUtil.isNotEmpty(wmsToWcsTaskReq.getStartLocationCode())) {
                                //启用AGV
                                agvTask(wmsToWcsTaskReq,taskNo);
                            }else{
                                stackerTask(wmsToWcsTaskReq,taskNo);
                            }

                        } else if (wmsToWcsTaskReq.getType().equals(WmsWcsTypeEnum.RELOCATION.getCode())) {
                            List<WmsToWcsTaskReq> childInfoList = wmsToWcsTaskReq.getChildInfoList();
                            for (WmsToWcsTaskReq toWcsTaskReq : childInfoList) {
                                if(!toWcsTaskReq.getType().equals(WmsWcsTypeEnum.PUTTRAY.getCode())){
                                    stackerTask(toWcsTaskReq,taskNo);
                                    //if (!toWcsTaskReq.getEndLocationCode().contains("csd") && (toWcsTaskReq.getType().equals(WmsWcsTypeEnum.TAKETRAY.getCode()))) {
                                    //    log.info("触发{}",agvId);
                                    //    //启用AGV
                                    //    agvTask(toWcsTaskReq,taskNo);
                                    //}
                                }else{
                                    //回盘任务，如果agv开启，先调用agv,在调用堆垛机
                                    if (StrUtil.isNotEmpty(toWcsTaskReq.getStartLocationCode())) {
                                        //启用AGV
                                        agvTask(toWcsTaskReq,taskNo);
                                    }else{
                                        stackerTask(toWcsTaskReq,taskNo);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        return AjaxResult.success();
    }

    /**
     * agv任务
     *
     * @param wmsToWcsTaskReq
     * @return
     */
    public AjaxResult agvTask(WmsToWcsTaskReq wmsToWcsTaskReq,String serviceTaskNo) {
        String taskNo = IdUtil.getSnowflakeNextId() + "";
        WcsOperateTask wcsOperateTask = new WcsOperateTask();
        wcsOperateTask.setId(IdUtil.fastSimpleUUID());
        wcsOperateTask.setTaskNo(taskNo);
        wcsOperateTask.setServiceTaskNo(serviceTaskNo);
        wcsOperateTask.setTaskDeviceType(WcsTaskDeviceTypeEnum.AVG.getCode());
        wcsOperateTask.setTaskType(wmsToWcsTaskReq.getTaskType());
        wcsOperateTask.setStartPosition(wmsToWcsTaskReq.getStartLocationCode());
        wcsOperateTask.setEndPosition(wmsToWcsTaskReq.getEndLocationCode());
        wcsOperateTask.setTrayNo(wmsToWcsTaskReq.getTrayCode());
        wcsOperateTask.setOperateBeginTime(new Date());
        wcsOperateTask.setTaskStatus(WcsTaskRunStatusEnum.IN_PROGRESS.getCode());
        wcsOperateTask.setInBillNo(wmsToWcsTaskReq.getDoc());
        wcsOperateTask.setOperateType(wmsToWcsTaskReq.getType());
        try {
            wcsOperateTaskService.insertWcsOperateTask(wcsOperateTask);

            AgvAddMoveTaskReq agvAddMoveTaskReq = new AgvAddMoveTaskReq();
            agvAddMoveTaskReq.setInStoragesNum("1");

            AgvTaskRsp agvTaskRsp = new AgvTaskRsp();
            agvTaskRsp.setState(10000);

            //todo http 调用  avg厂商搬运任务接口
            Thread.sleep(500);

            wcsOperateTask.setTaskReqJson(JSON.toJSONString(agvAddMoveTaskReq));
            wcsOperateTask.setTaskRspJson(JSON.toJSONString(agvTaskRsp));
            wcsOperateTask.setWaitTaskReqJson(JSON.toJSONString(wmsToWcsTaskReq));
            if (agvTaskRsp.isSuccess()) {
                wcsOperateTask.setTaskStatus(WcsTaskRunStatusEnum.COMPLETED.getCode());
            } else {
                wcsOperateTask.setTaskStatus(WcsTaskRunStatusEnum.IN_EXCEPTION.getCode());
                wcsOperateTask.setErrMsg(AgvRespCodeEnum.getInstance(agvTaskRsp.getState()).getName());
            }
            wcsOperateTask.setOperateEndTime(new Date());
            wcsOperateTaskService.updateById(wcsOperateTask);

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
             wmsWcsCallbackInfoService.insertWmsWcsCallbackInfo(wmsWcsCallbackInfo);


            //todo agv到位信号
            //Thread.sleep(500);
            //log.info("agv到位信号");
            //WmsWcsInfo wmsWcsInfo = new WmsWcsInfo();
            //wmsWcsInfo.put("startAreaCode", wmsToWcsTaskReq.getStartAreaCode());
            //wmsWcsInfo.put("endAreaCode", wmsToWcsTaskReq.getEndAreaCode());
            //wmsWcsInfo.put("startLocationCode", wmsToWcsTaskReq.getStartLocationCode());
            //wmsWcsInfo.put("endLocationCode", wmsToWcsTaskReq.getEndLocationCode());
            //wmsWcsInfo.put("type", wmsToWcsTaskReq.getType());
            //wmsWcsInfo.put("taskType", wmsToWcsTaskReq.getTaskType());
            //wmsWcsInfo.put("trayCode", wmsToWcsTaskReq.getTrayCode());
            //wmsWcsInfo.put("doc", wmsToWcsTaskReq.getDoc());
            //WcsConstants.agvCallBackQueue.put(wmsWcsInfo);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return AjaxResult.success();
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
        wcsOperateTask.setServiceTaskNo(serviceTaskNo);
        wcsOperateTask.setTaskDeviceType(WcsTaskDeviceTypeEnum.STACKER.getCode());
        wcsOperateTask.setTaskType(wmsToWcsTaskReq.getTaskType());
        wcsOperateTask.setStartPosition(wmsToWcsTaskReq.getStartLocationCode());
        wcsOperateTask.setEndPosition(wmsToWcsTaskReq.getEndLocationCode());

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

        wcsOperateTask.setTrayNo(wmsToWcsTaskReq.getTrayCode());
        wcsOperateTask.setOperateBeginTime(new Date());
        wcsOperateTask.setTaskStatus(WcsTaskRunStatusEnum.IN_PROGRESS.getCode());
        wcsOperateTask.setInBillNo(wmsToWcsTaskReq.getDoc());
        wcsOperateTask.setOperateType(wmsToWcsTaskReq.getType());
        try {
            wcsOperateTaskService.insertWcsOperateTask(wcsOperateTask);

            StackerTaskReq stackerTaskReq = new StackerTaskReq();
            stackerTaskReq.setDoBeginTime(DateUtil.formatDateTime(new Date()));

            StackerTaskRsp stackerTaskRsp = new StackerTaskRsp();
            stackerTaskRsp.setState(10000);

            //todo http 调用  苏立堆垛机厂商搬运任务接口
            Thread.sleep(500);

            wcsOperateTask.setTaskReqJson(JSON.toJSONString(stackerTaskReq));
            wcsOperateTask.setTaskRspJson(JSON.toJSONString(stackerTaskRsp));
            wcsOperateTask.setWaitTaskReqJson(JSON.toJSONString(wmsToWcsTaskReq));
            if (stackerTaskRsp.isSuccess()) {
                wcsOperateTask.setTaskStatus(WcsTaskRunStatusEnum.COMPLETED.getCode());
            } else {
                wcsOperateTask.setTaskStatus(WcsTaskRunStatusEnum.IN_EXCEPTION.getCode());
                wcsOperateTask.setErrMsg("堆垛机执行任务异常");
            }
            wcsOperateTask.setOperateEndTime(new Date());
            wcsOperateTaskService.updateById(wcsOperateTask);


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
             wmsWcsCallbackInfoService.insertWmsWcsCallbackInfo(wmsWcsCallbackInfo);

            //todo 堆垛机到位信号
            //Thread.sleep(500);
            //log.info("堆垛机到位信号");
            //WmsWcsInfo wmsWcsInfo = new WmsWcsInfo();
            //wmsWcsInfo.put("startAreaCode", wmsToWcsTaskReq.getStartAreaCode());
            //wmsWcsInfo.put("endAreaCode", wmsToWcsTaskReq.getEndAreaCode());
            //wmsWcsInfo.put("startLocationCode", wmsToWcsTaskReq.getStartLocationCode());
            //wmsWcsInfo.put("endLocationCode", wmsToWcsTaskReq.getEndLocationCode());
            //wmsWcsInfo.put("type", wmsToWcsTaskReq.getType());
            //wmsWcsInfo.put("taskType", wmsToWcsTaskReq.getTaskType());
            //wmsWcsInfo.put("trayCode", wmsToWcsTaskReq.getTrayCode());
            //wmsWcsInfo.put("doc", wmsToWcsTaskReq.getDoc());
            //wmsWcsInfo.put("serviceId", wmsToWcsTaskReq.getServiceId());
            //WcsConstants.stackerCallBackQueue.put(wmsWcsInfo);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return AjaxResult.success();
    }

    /**
     * 只调用堆垛机任务
     *
     * @param wmsToWcsTaskReq
     * @return
     */
    public AjaxResult stackerTaskOnly(WmsToWcsTaskReq wmsToWcsTaskReq,String serviceTaskNo) {
        String taskNo = IdUtil.getSnowflakeNextId() + "";
        WcsOperateTask wcsOperateTask = new WcsOperateTask();
        wcsOperateTask.setId(IdUtil.fastSimpleUUID());
        wcsOperateTask.setTaskNo(taskNo);
        wcsOperateTask.setServiceTaskNo(serviceTaskNo);
        wcsOperateTask.setTaskDeviceType(WcsTaskDeviceTypeEnum.STACKER.getCode());
        wcsOperateTask.setTaskType(wmsToWcsTaskReq.getTaskType());
        wcsOperateTask.setStartPosition(wmsToWcsTaskReq.getStartLocationCode());
        wcsOperateTask.setEndPosition(wmsToWcsTaskReq.getEndLocationCode());
        wcsOperateTask.setTrayNo(wmsToWcsTaskReq.getTrayCode());
        wcsOperateTask.setOperateBeginTime(new Date());
        wcsOperateTask.setTaskStatus(WcsTaskRunStatusEnum.IN_PROGRESS.getCode());
        wcsOperateTask.setInBillNo(wmsToWcsTaskReq.getDoc());
        wcsOperateTask.setOperateType(wmsToWcsTaskReq.getType());
        try {
            wcsOperateTaskService.insertWcsOperateTask(wcsOperateTask);

            StackerTaskReq stackerTaskReq = new StackerTaskReq();
            stackerTaskReq.setDoBeginTime(DateUtil.formatDateTime(new Date()));

            StackerTaskRsp stackerTaskRsp = new StackerTaskRsp();
            stackerTaskRsp.setState(10000);

            //todo http 调用  苏立堆垛机厂商搬运任务接口
            Thread.sleep(500);

            wcsOperateTask.setTaskReqJson(JSON.toJSONString(stackerTaskReq));
            wcsOperateTask.setTaskRspJson(JSON.toJSONString(stackerTaskRsp));
            wcsOperateTask.setWaitTaskReqJson(JSON.toJSONString(wmsToWcsTaskReq));
            if (stackerTaskRsp.isSuccess()) {
                wcsOperateTask.setTaskStatus(WcsTaskRunStatusEnum.COMPLETED.getCode());
            } else {
                wcsOperateTask.setTaskStatus(WcsTaskRunStatusEnum.IN_EXCEPTION.getCode());
                wcsOperateTask.setErrMsg("堆垛机执行任务异常");
            }
            wcsOperateTask.setOperateEndTime(new Date());
            wcsOperateTaskService.updateById(wcsOperateTask);

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
             wmsWcsCallbackInfoService.insertWmsWcsCallbackInfo(wmsWcsCallbackInfo);

            //todo 堆垛机到位信号
            //Thread.sleep(500);
            //log.info("堆垛机到位信号");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return AjaxResult.success();
    }


    //todo 堆垛机和AGV是否有正在执行中的任务
    public boolean haveInWcsTask(){
        return WcsConstants.agvCallBackQueue.size() >0 ||  WcsConstants.stackerCallBackQueue.size() > 0;
    }


}
