package com.ruoyi.iot.task.collect;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ruoyi.iot.global.NettyGlobalConstant;
import com.ruoyi.iot.packet.electric.req.*;
import com.ruoyi.iot.packet.electric.rsp.*;
import com.ruoyi.wcs.domain.WcsDeviceBaseInfo;
import com.ruoyi.wcs.domain.WcsElectricalEnergyCollectInfo;
import com.ruoyi.wcs.domain.WcsPowerCollectInfo;
import com.ruoyi.wcs.domain.WcsVoltageCurrentCollectInfo;
import com.ruoyi.wcs.enums.wcs.WcsTaskDeviceTypeEnum;
import com.ruoyi.wcs.mapper.WcsEnergyConsumeMonitorMapper;
import com.ruoyi.wcs.service.WcsDeviceBaseInfoService;
import com.ruoyi.wcs.service.WcsElectricalEnergyCollectInfoService;
import com.ruoyi.wcs.service.WcsPowerCollectInfoService;
import com.ruoyi.wcs.service.WcsVoltageCurrentCollectInfoService;
import com.ruoyi.wms.utils.socket.DeviceDataStream;
import com.ruoyi.wms.utils.socket.DeviceSocketUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.Socket;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 电表数据采集任务
 */
@Slf4j
@Component("electricPowerCollectTask")
public class ElectricPowerCollectTask {

    @Autowired
    private WcsDeviceBaseInfoService wcsDeviceBaseInfoService;

    @Autowired
    private WcsElectricalEnergyCollectInfoService wcsElectricalEnergyCollectInfoService;

    @Autowired
    private WcsVoltageCurrentCollectInfoService wcsVoltageCurrentCollectInfoService;

    @Autowired
    private WcsPowerCollectInfoService wcsPowerCollectInfoService;

    @Autowired
    private WcsEnergyConsumeMonitorMapper wcsEnergyConsumeMonitorMapper;

    /**
     * 采集电表数据
     */
    public void collectElectricPowerData() {
        Map<WcsDeviceBaseInfo, List<WcsDeviceBaseInfo>> deviceGroupData = NettyGlobalConstant.filterGroupPowerData;
        if (CollUtil.isNotEmpty(deviceGroupData) && CollUtil.isNotEmpty(deviceGroupData.values())) {
            deviceGroupData.forEach((gateWayDevice, powerDeviceList) -> {
                for (WcsDeviceBaseInfo powerDevice : powerDeviceList) {
                    Socket socket = null;
                    try {
                        socket = new Socket(gateWayDevice.getDeviceIp(), gateWayDevice.getDevicePort().intValue());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (StrUtil.isNotEmpty(powerDevice.getDeviceAddress())) {

                        //有功总电能
                        ElectricReadPhaseActiveEnergyReq electricReadPhaseActiveEnergyReq = new ElectricReadPhaseActiveEnergyReq(powerDevice.getDeviceAddress(),
                                "03", "00",
                                "3f", "0002", "", "");

                        //PT、CT
                        ElectricReadPhaseActiveEnergyPTReq electricReadPhaseActiveEnergyPTReq = new ElectricReadPhaseActiveEnergyPTReq(powerDevice.getDeviceAddress(),
                                "03", "00",
                                "03", "0002", "", "");

                        //电压、电流、功率 DPT/DCT/DPQ
                        ElectricReadPhaseActiveEnergyDPTReq electricReadPhaseActiveEnergyDPTReq = new ElectricReadPhaseActiveEnergyDPTReq(powerDevice.getDeviceAddress(),
                                "03", "00",
                                "23", "0002", "", "");

                        //电压 0000000000000
                        ElectricReadVoltageReq electricReadVoltageReq = new ElectricReadVoltageReq(powerDevice.getDeviceAddress(), "03",
                                "00", "25",
                                "0003", "", "");

                        //电流
                        ElectricReadCurrentReq electricReadCurrentReq = new ElectricReadCurrentReq(powerDevice.getDeviceAddress(), "03", "00",
                                "2B", "0003", "", "");

                        //有功功率
                        ElectricReadPhaseActivePowerReq electricReadPhaseActivePowerReq = new ElectricReadPhaseActivePowerReq(powerDevice.getDeviceAddress(), "03",
                                "00", "2E",
                                "0004", "", "");

                        // 无功功率
                        ElectricReadReactivePowerReq electricReadReactivePowerReq = new ElectricReadReactivePowerReq(powerDevice.getDeviceAddress(), "03",
                                "00", "32",
                                "0004", "", "");

                        if (ObjectUtil.isNull(socket)) {
                            return;
                        }
                        NettyGlobalConstant.lock.lock();
                        //1.构建对象
                        DeviceDataStream deviceDataStream = DeviceSocketUtil.buildDataStream(socket);
                        try {
                            //2.写入（有功总电能）
                            DeviceSocketUtil.writeData(deviceDataStream.getOut(), electricReadPhaseActiveEnergyReq.toString());
                            //3.读取（有功总电能）
                            String responseData = DeviceSocketUtil.readData(deviceDataStream.getInput());
                            if (StrUtil.isNotEmpty(responseData)) {
                                String addressCode = responseData.substring(0, 2);
                                WcsElectricalEnergyCollectInfo wcsElectricalEnergyCollectInfo = new WcsElectricalEnergyCollectInfo();
                                WcsDeviceBaseInfo wcsDeviceBaseInfo = wcsDeviceBaseInfoService.getBaseMapper().selectGateWayCollect(gateWayDevice.getDeviceNo(),
                                        WcsTaskDeviceTypeEnum.AMMETER.getCode(), addressCode);
                                QueryWrapper<WcsElectricalEnergyCollectInfo> electricalEq = new QueryWrapper<>();
                                electricalEq.eq("t.device_info_id",wcsDeviceBaseInfo.getId());
                                WcsElectricalEnergyCollectInfo yesterdayData = wcsEnergyConsumeMonitorMapper.getYesterdayData(electricalEq);
                                if(ObjectUtil.isNull(yesterdayData)){
                                    yesterdayData = new WcsElectricalEnergyCollectInfo();
                                    yesterdayData.setTotalElectricalEnergy(0.0);
                                }

                                //2.写入（PT/CT）
                                DeviceSocketUtil.writeData(deviceDataStream.getOut(), electricReadPhaseActiveEnergyPTReq.toString());
                                //3.读取（有功总电能）
                                String responsePTData = DeviceSocketUtil.readData(deviceDataStream.getInput());

                                if (ObjectUtil.isNotNull(wcsDeviceBaseInfo) && StrUtil.isNotEmpty(responsePTData)) {
                                    log.info("有功总电能 response = " + responseData.trim());
                                    ElectricReadPhaseActiveEnergyRsp electricReadPhaseActiveEnergyRsp = ElectricReadPhaseActiveEnergyRsp.hexStrToObj(responseData,responsePTData);
                                    log.info("总有功电能：{}", electricReadPhaseActiveEnergyRsp.getTotalActiveEnergy());
                                    double yesterdayActiveEnergy = yesterdayData.getTotalElectricalEnergy();  // 昨天的总有功电能值
                                    double todayActiveEnergy = electricReadPhaseActiveEnergyRsp.getTotalActiveEnergy();  // 今天的总有功电能值
                                    double difference = todayActiveEnergy - yesterdayActiveEnergy;  // 计算差值
                                    double truncatedDifference = Math.ceil(difference * 100.0) / 100.0;
                                    wcsElectricalEnergyCollectInfo.setId(IdUtil.fastSimpleUUID())
                                            .setTotalElectricalEnergy(todayActiveEnergy)
                                            .setActiveTotalElectricalEnergy(truncatedDifference)
                                            .setCollectTime(new Date()).setPostalAddress(electricReadPhaseActiveEnergyRsp.getAddressCode())
                                            .setDeviceInfoId(ObjectUtil.isNotNull(wcsDeviceBaseInfo) ? wcsDeviceBaseInfo.getId() : "");
                                    wcsElectricalEnergyCollectInfoService.saveOrUpdate(wcsElectricalEnergyCollectInfo);
                                }
                            }
                            //写入电压、电流、功率 DPT/DCT/DPQ
                            DeviceSocketUtil.writeData(deviceDataStream.getOut(), electricReadPhaseActiveEnergyDPTReq.toString());
                            //3.读取（电压、电流、功率 DPT/DCT/DPQ
                            String responseDataDptDctDpq = DeviceSocketUtil.readData(deviceDataStream.getInput());

                            //2.写入（电压）
                            DeviceSocketUtil.writeData(deviceDataStream.getOut(), electricReadVoltageReq.toString());
                            //3.读取（电压）
                            String responseData1 = DeviceSocketUtil.readData(deviceDataStream.getInput());
                            WcsVoltageCurrentCollectInfo wcsVoltageCurrentCollectInfo = new WcsVoltageCurrentCollectInfo();
                            if (StrUtil.isNotEmpty(responseData1)) {
                                log.info("电压 response = " + responseData1.trim());
                                String addressCode = responseData1.substring(0, 2);

                                WcsDeviceBaseInfo wcsDeviceBaseInfo = wcsDeviceBaseInfoService.getBaseMapper().selectGateWayCollect(gateWayDevice.getDeviceNo(),
                                        WcsTaskDeviceTypeEnum.AMMETER.getCode(), addressCode);
                                if (ObjectUtil.isNotNull(wcsDeviceBaseInfo) && StrUtil.isNotEmpty(responseDataDptDctDpq)) {
                                    ElectricReadVoltageRsp electricReadVoltageRsp = ElectricReadVoltageRsp.hexStrToObj(responseData1,responseDataDptDctDpq);
                                    log.info("相电压UA：{}", electricReadVoltageRsp.getPhaseVoltageA());
                                    log.info("相电压UB：{}", electricReadVoltageRsp.getPhaseVoltageB());
                                    log.info("相电压UC：{}", electricReadVoltageRsp.getPhaseVoltageC());
                                    wcsVoltageCurrentCollectInfo.setPhaseVoltageA(String.format("%.1f",electricReadVoltageRsp.getPhaseVoltageA()))
                                            .setPhaseVoltageB(String.format("%.1f",electricReadVoltageRsp.getPhaseVoltageB()))
                                            .setPhaseVoltageC(String.format("%.1f",electricReadVoltageRsp.getPhaseVoltageC()))
                                            .setCollectTime(new Date()).setDeviceInfoId(wcsDeviceBaseInfo.getId()).setPostalAddress(electricReadVoltageRsp.getAddressCode());
                                }
                            }

                            //2.写入（电流）
                            DeviceSocketUtil.writeData(deviceDataStream.getOut(), electricReadCurrentReq.toString());
                            //3.读取（电流）
                            String responseData2 = DeviceSocketUtil.readData(deviceDataStream.getInput());
                            if (StrUtil.isNotEmpty(responseData2) && StrUtil.isNotEmpty(responseDataDptDctDpq)) {
                                log.info("电流 response = " + responseData2.trim());
                                String addressCode = responseData2.substring(0, 2);
                                WcsDeviceBaseInfo wcsDeviceBaseInfo = wcsDeviceBaseInfoService.getBaseMapper().selectGateWayCollect(gateWayDevice.getDeviceNo(),
                                        WcsTaskDeviceTypeEnum.AMMETER.getCode(), addressCode);
                                if (ObjectUtil.isNotNull(wcsDeviceBaseInfo)) {
                                    ElectricReadCurrentRsp electricReadCurrentRsp = ElectricReadCurrentRsp.hexStrToObj(responseData2,responseDataDptDctDpq);
                                    log.info("电流A：{}", electricReadCurrentRsp.getCurrentA());
                                    log.info("电流B：{}", electricReadCurrentRsp.getCurrentB());
                                    log.info("电流C：{}", electricReadCurrentRsp.getCurrentC());
                                    wcsVoltageCurrentCollectInfo.setPhaseCurrentA(String.format("%.1f",electricReadCurrentRsp.getCurrentA()))
                                            .setPhaseCurrentB(String.format("%.1f",electricReadCurrentRsp.getCurrentB()))
                                            .setPhaseCurrentC(String.format("%.1f",electricReadCurrentRsp.getCurrentC()))
                                            .setCollectTime(new Date()).setDeviceInfoId(wcsDeviceBaseInfo.getId()).setPostalAddress(electricReadCurrentRsp.getAddressCode());
                                    wcsVoltageCurrentCollectInfoService.saveOrUpdate(wcsVoltageCurrentCollectInfo);
                                }
                            }

                            //2.写入（有功功率）
                            DeviceSocketUtil.writeData(deviceDataStream.getOut(), electricReadPhaseActivePowerReq.toString());
                            //3.读取（有功功率）
                            String responseData3 = DeviceSocketUtil.readData(deviceDataStream.getInput());
                            WcsPowerCollectInfo wcsPowerCollectInfo = new WcsPowerCollectInfo();
                            if (StrUtil.isNotEmpty(responseData3) && StrUtil.isNotEmpty(responseDataDptDctDpq)) {
                                log.info("有功功率 response = " + responseData3.trim());
                                ElectricReadPhaseActivePowerRsp electricReadPhaseActivePowerRsp = ElectricReadPhaseActivePowerRsp.hexStrToObj(responseData3,responseDataDptDctDpq);
                                log.info("A相有功功率：{}", electricReadPhaseActivePowerRsp.getPhaseActivePowerA());
                                log.info("B相有功功率：{}", electricReadPhaseActivePowerRsp.getPhaseActivePowerB());
                                log.info("C相有功功率：{}", electricReadPhaseActivePowerRsp.getPhaseActivePowerC());
                                log.info("总相有功功率：{}", electricReadPhaseActivePowerRsp.getTotalPhaseActivePower());
                                WcsDeviceBaseInfo wcsDeviceBaseInfo = wcsDeviceBaseInfoService.getBaseMapper().selectGateWayCollect(gateWayDevice.getDeviceNo(),
                                        WcsTaskDeviceTypeEnum.AMMETER.getCode(), electricReadPhaseActivePowerRsp.getAddressCode());
                                wcsPowerCollectInfo.setPhaseActivePowerA(String.format("%.1f",electricReadPhaseActivePowerRsp.getPhaseActivePowerA()))
                                        .setPhaseActivePowerB(String.format("%.1f",electricReadPhaseActivePowerRsp.getPhaseActivePowerB()))
                                        .setPhaseActivePowerC(String.format("%.1f",electricReadPhaseActivePowerRsp.getPhaseActivePowerC()))
                                        .setTotalActivePower(String.format("%.1f",electricReadPhaseActivePowerRsp.getTotalPhaseActivePower()))
                                        .setCollectTime(new Date()).setDeviceInfoId(wcsDeviceBaseInfo.getId()).setPostalAddress(electricReadPhaseActivePowerRsp.getAddressCode());
                            }

                            //2.写入（无功功率）
                            DeviceSocketUtil.writeData(deviceDataStream.getOut(), electricReadReactivePowerReq.toString());
                            //3.读取（无功功率）
                            String responseData4 = DeviceSocketUtil.readData(deviceDataStream.getInput());
                            if (StrUtil.isNotEmpty(responseData4) && StrUtil.isNotEmpty(responseDataDptDctDpq)) {
                                log.info("无功功率 response = " + responseData4.trim());
                                ElectricReadReactivePowerRsp electricReadReactivePowerRsp = ElectricReadReactivePowerRsp.hexStrToObj(responseData4,responseDataDptDctDpq);
                                log.info("无功功率A：{}", electricReadReactivePowerRsp.getPhaseReactivePowerA());
                                log.info("无功功率B：{}", electricReadReactivePowerRsp.getPhaseReactivePowerB());
                                log.info("无功功率C：{}", electricReadReactivePowerRsp.getPhaseReactivePowerC());
                                log.info("总无功功率：{}", electricReadReactivePowerRsp.getTotalPhaseReactivePower());
                                WcsDeviceBaseInfo wcsDeviceBaseInfo = wcsDeviceBaseInfoService.getBaseMapper().selectGateWayCollect(gateWayDevice.getDeviceNo(),
                                        WcsTaskDeviceTypeEnum.AMMETER.getCode(), electricReadReactivePowerRsp.getAddressCode());
                                wcsPowerCollectInfo.setPhaseReactivePowerA(String.format("%.1f",electricReadReactivePowerRsp.getPhaseReactivePowerA()))
                                        .setPhaseReactivePowerB(String.format("%.1f",electricReadReactivePowerRsp.getPhaseReactivePowerB()))
                                        .setPhaseReactivePowerC(String.format("%.1f",electricReadReactivePowerRsp.getPhaseReactivePowerC()))
                                        .setTotalReactivePower(String.format("%.1f",electricReadReactivePowerRsp.getTotalPhaseReactivePower()))
                                        .setCollectTime(new Date()).setDeviceInfoId(wcsDeviceBaseInfo.getId()).setPostalAddress(electricReadReactivePowerRsp.getAddressCode());
                                wcsPowerCollectInfoService.saveOrUpdate(wcsPowerCollectInfo);
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            NettyGlobalConstant.lock.unlock();
                            //4.关闭资源
                            DeviceSocketUtil.close(deviceDataStream.getInput(), deviceDataStream.getOut());
                        }
                    } else {
                        log.error("电表设备地址码异常");
                    }
                }

            });
        }
    }


}
