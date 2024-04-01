package com.ruoyi.iot.task.collect;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.ruoyi.common.utils.spring.SpringUtils;
import com.ruoyi.iot.global.NettyGlobalConstant;
import com.ruoyi.iot.packet.smoke.req.SmokeReadMeasuredValReq;
import com.ruoyi.iot.packet.smoke.rsp.SmokeReadMeasuredValRsp;
import com.ruoyi.iot.task.common.TaskTime;
import com.ruoyi.wcs.domain.WcsDeviceBaseInfo;
import com.ruoyi.wcs.domain.WcsDeviceEarlyWarningInfo;
import com.ruoyi.wcs.enums.wcs.WcsTaskDeviceTypeEnum;
import com.ruoyi.wcs.service.WcsDeviceBaseInfoService;
import com.ruoyi.wcs.service.WcsDeviceEarlyWarningInfoService;
import com.ruoyi.wms.utils.socket.DeviceDataStream;
import com.ruoyi.wms.utils.socket.DeviceSocketUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.Socket;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 烟雾传感器数据采集任务
 */
@Slf4j
@Component("smokeCollectTask")
public class SmokeCollectTask {

    /**
     * 采集烟雾传感器数据
     */
    public void smokeCollectTask() {
        Map<WcsDeviceBaseInfo, List<WcsDeviceBaseInfo>> deviceGroupData = NettyGlobalConstant.filterGroupSmokeData;
        if (CollUtil.isNotEmpty(deviceGroupData) && CollUtil.isNotEmpty(deviceGroupData.values())) {
            deviceGroupData.forEach((gateWayDevice, powerDeviceList) -> {
                if (CollUtil.isNotEmpty(powerDeviceList)) {
                    for (WcsDeviceBaseInfo powerDevice : powerDeviceList) {
                        if (StrUtil.isNotEmpty(powerDevice.getDeviceAddress())) {
                            Socket socket = null;
                            try {
                                socket = new Socket(gateWayDevice.getDeviceIp(), gateWayDevice.getDevicePort().intValue());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            SmokeReadMeasuredValReq smokeReadMeasuredValReq = new SmokeReadMeasuredValReq(powerDevice.getDeviceAddress(), "03"
                                    , "02", "00"
                                    , "0001", "", "");
                            if (ObjectUtil.isNull(socket)) {
                                return;
                            }
                            // 线程睡眠1000毫秒
                            try {
                                Thread.sleep(TaskTime.SENSOR_SLEEP_TIME);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            NettyGlobalConstant.lock.lock();
                            //1.构建对象
                            DeviceDataStream deviceDataStream = DeviceSocketUtil.buildDataStream(socket);
                            try {
                                //2.写入
                                DeviceSocketUtil.writeData(deviceDataStream.getOut(), smokeReadMeasuredValReq.toString());
                                //3.读取
                                String responseData = DeviceSocketUtil.readData(deviceDataStream.getInput());
                                if (StrUtil.isNotEmpty(responseData)) {
                                    String addressCode = responseData.substring(0, 2);
                                    if (StrUtil.isNotEmpty(addressCode)) {
                                        WcsDeviceBaseInfoService wcsDeviceBaseInfoService = SpringUtils.getBean(WcsDeviceBaseInfoService.class);
                                        WcsDeviceEarlyWarningInfoService wcsDeviceEarlyWarningInfoService = SpringUtils.getBean(WcsDeviceEarlyWarningInfoService.class);
                                        WcsDeviceBaseInfo wcsDeviceBaseInfo = wcsDeviceBaseInfoService.getBaseMapper().selectGateWayCollect(gateWayDevice.getDeviceNo(),
                                                WcsTaskDeviceTypeEnum.SMOKE.getCode(), addressCode);
                                        SmokeReadMeasuredValRsp smokeReadMeasuredValRsp = new SmokeReadMeasuredValRsp();
                                        if (ObjectUtil.isNotNull(wcsDeviceBaseInfo)) {
                                            log.info("采集烟雾传感器数据 response = " + responseData.trim());
                                            smokeReadMeasuredValRsp = SmokeReadMeasuredValRsp.hexStrToObj(responseData);
                                            log.info("感烟报警值:" + smokeReadMeasuredValRsp.getSmokeAlarmVal());

                                            wcsDeviceBaseInfo.setSmokeFlag(smokeReadMeasuredValRsp.getSmokeAlarmVal().toString());
                                            //更新烟感报警值
                                            wcsDeviceBaseInfoService.updateById(wcsDeviceBaseInfo);
                                            if (ObjectUtil.isNotNull(smokeReadMeasuredValRsp) && "1".equals(smokeReadMeasuredValRsp.getSmokeAlarmVal().toString())) {
                                                if (ObjectUtil.isNotNull(wcsDeviceBaseInfo)) {
                                                    WcsDeviceEarlyWarningInfo wcsDeviceEarlyWarningInfo = new WcsDeviceEarlyWarningInfo();
                                                    wcsDeviceEarlyWarningInfo.setId(IdUtil.fastSimpleUUID());
                                                    wcsDeviceEarlyWarningInfo.setDeviceInfoId(wcsDeviceBaseInfo.getDeviceNo());
                                                    wcsDeviceEarlyWarningInfo.setWarningContent("当前处于报警状态，烟雾浓度过高");
                                                    wcsDeviceEarlyWarningInfo.setWarningTime(new Date());
                                                    wcsDeviceEarlyWarningInfoService.saveOrUpdate(wcsDeviceEarlyWarningInfo);
                                                }
                                            }
                                        }
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            } finally {
                                NettyGlobalConstant.lock.unlock();
                                //4.关闭资源
                                DeviceSocketUtil.close(deviceDataStream.getInput(), deviceDataStream.getOut());
                            }
                        } else {
                            log.error("烟雾传感器地址码异常");
                        }
                    }
                }
            });
        }
    }


}
