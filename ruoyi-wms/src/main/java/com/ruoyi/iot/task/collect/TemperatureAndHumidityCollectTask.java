package com.ruoyi.iot.task.collect;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.ruoyi.common.utils.spring.SpringUtils;
import com.ruoyi.iot.global.NettyGlobalConstant;
import com.ruoyi.iot.packet.humiture.req.HumitureReadReq;
import com.ruoyi.iot.packet.humiture.rsp.HumitureReadRsp;
import com.ruoyi.iot.task.common.LimitationSensor;
import com.ruoyi.iot.task.common.TaskTime;
import com.ruoyi.wcs.domain.WcsDeviceBaseInfo;
import com.ruoyi.wcs.domain.WcsTemplatureHumidityCollectInfo;
import com.ruoyi.wcs.domain.dto.WcsFreshAirParamDto;
import com.ruoyi.wcs.enums.wcs.WcsTaskDeviceTypeEnum;
import com.ruoyi.wcs.service.WcsDeviceBaseInfoService;
import com.ruoyi.wcs.service.WcsFreshAirDetailInfoService;
import com.ruoyi.wcs.service.WcsTemplatureHumidityCollectInfoService;
import com.ruoyi.wms.utils.socket.DeviceDataStream;
import com.ruoyi.wms.utils.socket.DeviceSocketUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.Socket;
import java.util.*;

/**
 * 温湿度数据采集任务
 */
@Slf4j
@Component("temperatureAndHumidityCollectTask")
public class TemperatureAndHumidityCollectTask {

    @Autowired
    private WcsFreshAirDetailInfoService wcsFreshAirDetailInfoService;

    /**
     * 采集温湿度数据
     */
    public void collectTemperatureAndHumidityData() {
        Map<WcsDeviceBaseInfo, List<WcsDeviceBaseInfo>> deviceGroupData = NettyGlobalConstant.filterGroupTemperatureAndHumidityData;
        List<HumitureReadRsp> humitureReadRsps = new LinkedList<>();
        deviceGroupData.forEach((gateWayDevice, powerDeviceList) -> {
            for (WcsDeviceBaseInfo powerDevice : powerDeviceList) {
                if (StrUtil.isNotEmpty(powerDevice.getDeviceAddress())) {

                    Socket socket = null;
                    try {
                        socket = new Socket(gateWayDevice.getDeviceIp(), gateWayDevice.getDevicePort().intValue());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    HumitureReadReq humitureReadReq = new HumitureReadReq(powerDevice.getDeviceAddress(),
                            "03", "00", "00",
                            "0002", "", "");
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
                        WcsDeviceBaseInfoService wcsDeviceBaseInfoService = SpringUtils.getBean(WcsDeviceBaseInfoService.class);
                        WcsDeviceBaseInfo baseInfo = wcsDeviceBaseInfoService.getBaseMapper().selectGateWayCollect(gateWayDevice.getDeviceNo(),
                                WcsTaskDeviceTypeEnum.TEMPERATURE_AND_HUMIDITY.getCode(), humitureReadReq.toString().substring(0, 2));

                        //2.写入
                        DeviceSocketUtil.writeData(deviceDataStream.getOut(), humitureReadReq.toString());
                        //3.读取
                        String responseData = "";
                        if (ObjectUtil.isNotNull(baseInfo)) {
                            responseData = DeviceSocketUtil.readData(deviceDataStream.getInput());
                        } else {
                            return;
                        }
                        if (StrUtil.isNotEmpty(responseData)) {
                            log.info("采集温湿度数据 response = " + responseData.trim());

                            String addressCode = responseData.substring(0, 2);

                            WcsTemplatureHumidityCollectInfoService wcsTemplatureHumidityCollectInfoService = SpringUtils.getBean(WcsTemplatureHumidityCollectInfoService.class);
                            WcsDeviceBaseInfo wcsDeviceBaseInfo = wcsDeviceBaseInfoService.getBaseMapper().selectGateWayCollect(gateWayDevice.getDeviceNo(),
                                    WcsTaskDeviceTypeEnum.TEMPERATURE_AND_HUMIDITY.getCode(), addressCode);

                            if (ObjectUtil.isNotNull(wcsDeviceBaseInfo)) {
                                HumitureReadRsp humitureReadRsp = HumitureReadRsp.hexStrToObj(responseData);
                                log.info("湿度:" + humitureReadRsp.getHumidityVal());
                                log.info("温度:" + humitureReadRsp.getTemperatureVal());
                                // 记录当前实时温湿度
                                wcsDeviceBaseInfo.setTemplature(String.valueOf(humitureReadRsp.getTemperatureVal()));
                                wcsDeviceBaseInfo.setHumidity(String.valueOf(humitureReadRsp.getHumidityVal()));
                                wcsDeviceBaseInfoService.saveOrUpdate(wcsDeviceBaseInfo);

                                // 记录温湿度历史表
                                WcsTemplatureHumidityCollectInfo collectInfo = new WcsTemplatureHumidityCollectInfo();
                                collectInfo.setDeviceInfoId(wcsDeviceBaseInfo.getId());
                                collectInfo.setTemplature(String.valueOf(humitureReadRsp.getTemperatureVal()));
                                collectInfo.setHumidity(String.valueOf(humitureReadRsp.getHumidityVal()));
                                collectInfo.setCollectTime(new Date());
                                wcsTemplatureHumidityCollectInfoService.saveOrUpdate(collectInfo);
                                //温度
                                BigDecimal templature = new BigDecimal(String.valueOf(humitureReadRsp.getTemperatureVal()));
                                //湿度
                                BigDecimal humidity = new BigDecimal(String.valueOf(humitureReadRsp.getHumidityVal()));
                                //判断是否符合条件
                                if(new BigDecimal(LimitationSensor.MAX_VALUE).compareTo(templature) > 0  &&  new BigDecimal(LimitationSensor.MIN_VALUE).compareTo(templature) <0
                                  &&  new BigDecimal(LimitationSensor.MAX_VALUE).compareTo(humidity) > 0  &&  new BigDecimal(LimitationSensor.MIN_VALUE).compareTo(humidity) <0){
                                    NettyGlobalConstant.humidityMap.put(humitureReadRsp.getAddressCode(), humitureReadRsp);
                                    humitureReadRsps.add(humitureReadRsp);
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
                    log.error("温湿度传感器地址码异常");
                }
            }
        });
        NettyGlobalConstant.humiditylistMap.put("1",humitureReadRsps);
        Map<String, List<HumitureReadRsp>> resultMap = NettyGlobalConstant.humiditylistMap;
        log.info("温湿度结果集："+resultMap);
        // 定时校准温湿度 开关新风
        // wcsFreshAirDetailInfoService.isEnabledWcsFreshAir(new WcsFreshAirParamDto());
        // 定时校准温湿度 开关爆闪灯
        wcsFreshAirDetailInfoService.isEnableExplosiveFlash();
    }


}
