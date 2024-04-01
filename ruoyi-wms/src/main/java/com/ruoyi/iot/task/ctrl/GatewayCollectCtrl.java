package com.ruoyi.iot.task.ctrl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ruoyi.common.enums.DelFlagEnum;
import com.ruoyi.iot.global.NettyGlobalConstant;
import com.ruoyi.iot.task.channel.ChannelMapProcess;
import com.ruoyi.wcs.domain.WcsDeviceBaseInfo;
import com.ruoyi.wcs.domain.WcsFreshAirThtbRealtion;
import com.ruoyi.wcs.domain.WcsGateWayRealtion;
import com.ruoyi.wcs.enums.wcs.WcsTaskDeviceTypeEnum;
import com.ruoyi.wcs.mapper.WcsDeviceBaseInfoMapper;
import com.ruoyi.wcs.mapper.WcsFreshAirThtbRealtionMapper;
import com.ruoyi.wcs.mapper.WcsGateWayRealtionMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 网关采集控制
 */
@Slf4j
@Service
public class GatewayCollectCtrl {

    @Autowired
    private WcsGateWayRealtionMapper wcsGateWayRealtionMapper;

    @Autowired
    private WcsDeviceBaseInfoMapper wcsDeviceBaseInfoMapper;

    @Autowired
    private WcsFreshAirThtbRealtionMapper wcsFreshAirThtbRealtionMapper;

    @Autowired
    private ChannelMapProcess channelMapProcess;

    /**
     * 初始化网关和设备的关联关系
     */
    @PostConstruct
    public void initGateWayRelationData() {
        log.info("=========== 初始化网关和设备的关联关系开始 =============");
        //数据组装成Map
        List<WcsGateWayRealtion> wcsGateWayRealtions = wcsGateWayRealtionMapper.selectList(
                new QueryWrapper<WcsGateWayRealtion>()
                        .eq("del_flag", DelFlagEnum.DEL_NO.getCode())
        );
        Map<String, List<WcsGateWayRealtion>> groupData = wcsGateWayRealtions.stream()
                .collect(Collectors.groupingBy(WcsGateWayRealtion::getGateWayDeviceNo, Collectors.toList()));
        Map<WcsDeviceBaseInfo, List<WcsDeviceBaseInfo>> deviceGroupData = new HashMap<>();
        if (CollUtil.isNotEmpty(groupData)) {
            groupData.forEach((gateWayDeviceNo, relations) -> {
                WcsDeviceBaseInfo gateWayDeviceBaseInfo = wcsDeviceBaseInfoMapper.selectOne(new QueryWrapper<WcsDeviceBaseInfo>()
                        .eq("del_flag", DelFlagEnum.DEL_NO.getCode())
                        .eq("enable_status", DelFlagEnum.DEL_NO.getCode())
                        .eq("device_no", gateWayDeviceNo));
                List<String> addressDeviceNos = relations.stream().map(WcsGateWayRealtion::getNoIpDeviceNo).collect(Collectors.toList());
                List<WcsDeviceBaseInfo> addressDeviceBaseInfos = wcsDeviceBaseInfoMapper.selectList(new QueryWrapper<WcsDeviceBaseInfo>()
                        .eq("del_flag", DelFlagEnum.DEL_NO.getCode())
                        .eq("enable_status", DelFlagEnum.DEL_NO.getCode())
                        .in("device_no", addressDeviceNos));
                deviceGroupData.put(gateWayDeviceBaseInfo, addressDeviceBaseInfos);
            });
        }

        Map<WcsDeviceBaseInfo, List<WcsDeviceBaseInfo>> filterGroupTemperatureAndHumidityData = new HashMap<>();
        Map<WcsDeviceBaseInfo, List<WcsDeviceBaseInfo>> filterGroupSmokeData = new HashMap<>();
        Map<WcsDeviceBaseInfo, List<WcsDeviceBaseInfo>> filterGroupPowerData = new HashMap<>();
        Map<WcsDeviceBaseInfo, List<WcsDeviceBaseInfo>> filterLightData = new HashMap<>();
        Map<WcsDeviceBaseInfo, List<WcsDeviceBaseInfo>> filterFreshAirData = new HashMap<>();
        Map<WcsDeviceBaseInfo, List<WcsDeviceBaseInfo>> dehumidifierData = new HashMap<>();

        deviceGroupData.forEach((k, v) -> {
            List<WcsDeviceBaseInfo> temperAndHumidityDevices = v.stream()
                    .filter(item -> item.getDeviceType().equals(WcsTaskDeviceTypeEnum.TEMPERATURE_AND_HUMIDITY.getCode()))
                    .collect(Collectors.toList());
            List<WcsDeviceBaseInfo> smokeDevices = v.stream()
                    .filter(item -> item.getDeviceType().equals(WcsTaskDeviceTypeEnum.SMOKE.getCode()))
                    .collect(Collectors.toList());
            List<WcsDeviceBaseInfo> powerDevices = v.stream()
                    .filter(item -> item.getDeviceType().equals(WcsTaskDeviceTypeEnum.AMMETER.getCode()))
                    .collect(Collectors.toList());
            List<WcsDeviceBaseInfo> lightDevices = v.stream()
                    .filter(item -> item.getDeviceType().equals(WcsTaskDeviceTypeEnum.LIGHT.getCode()))
                    .collect(Collectors.toList());
            List<WcsDeviceBaseInfo> FreshAirDevices = v.stream()
                    .filter(item -> item.getDeviceType().equals(WcsTaskDeviceTypeEnum.FRESHAIR.getCode()))
                    .collect(Collectors.toList());
            List<WcsDeviceBaseInfo> dehumidifierDevices = v.stream()
                    .filter(item -> item.getDeviceType().equals(WcsTaskDeviceTypeEnum.DEHUMIDIFIER.getCode()))
                    .collect(Collectors.toList());
            filterGroupTemperatureAndHumidityData.put(k, temperAndHumidityDevices);
            filterGroupSmokeData.put(k, smokeDevices);
            filterGroupPowerData.put(k, powerDevices);
            filterLightData.put(k, lightDevices);
            filterFreshAirData.put(k, FreshAirDevices);
            dehumidifierData.put(k, dehumidifierDevices);
        });

        NettyGlobalConstant.filterFreshAirData = filterFreshAirData;
        NettyGlobalConstant.filterGroupTemperatureAndHumidityData = filterGroupTemperatureAndHumidityData;
        NettyGlobalConstant.filterGroupSmokeData = filterGroupSmokeData;
        NettyGlobalConstant.filterGroupPowerData = filterGroupPowerData;
        NettyGlobalConstant.filterLightData = filterLightData;
        NettyGlobalConstant.dehumidifierData = dehumidifierData;

        List<WcsFreshAirThtbRealtion> wcsFreshAirThtbRealtions = wcsFreshAirThtbRealtionMapper.selectList(
                new QueryWrapper<WcsFreshAirThtbRealtion>()
                        .eq("del_flag", DelFlagEnum.DEL_NO.getCode())
        );
        // 新风-温湿度
        Map<WcsDeviceBaseInfo, List<WcsDeviceBaseInfo>> freshAirMapping = new HashMap<>();
        Map<String, List<WcsFreshAirThtbRealtion>> groupFreshAirData = new HashMap<>();
        if (CollUtil.isNotEmpty(wcsFreshAirThtbRealtions)) {
            groupFreshAirData = wcsFreshAirThtbRealtions.stream()
                    .collect(Collectors.groupingBy(WcsFreshAirThtbRealtion::getFreshAirDeviceNo, Collectors.toList()));
        }
        if (CollUtil.isNotEmpty(groupFreshAirData)) {
            groupFreshAirData.forEach((freshAirDeviceNo, relations) -> {
                WcsDeviceBaseInfo freshAirDeviceBaseInfo = wcsDeviceBaseInfoMapper.selectOne(new QueryWrapper<WcsDeviceBaseInfo>()
                        .eq("del_flag", DelFlagEnum.DEL_NO.getCode())
                        .eq("device_no", freshAirDeviceNo));
                List<String> addressDeviceNos = relations.stream().map(WcsFreshAirThtbRealtion::getThtbDeviceNo).collect(Collectors.toList());
                List<WcsDeviceBaseInfo> addressDeviceBaseInfos = wcsDeviceBaseInfoMapper.selectList(new QueryWrapper<WcsDeviceBaseInfo>()
                        .eq("del_flag", DelFlagEnum.DEL_NO.getCode())
                        .in("device_no", addressDeviceNos));
                freshAirMapping.put(freshAirDeviceBaseInfo, addressDeviceBaseInfos);
            });
            NettyGlobalConstant.freshAirMappingMap = freshAirMapping;
        }

        // 照明设备netty连接初始化
        channelMapProcess.getLightChannelMap(NettyGlobalConstant.filterLightData);

        // 烟感设备netty连接初始化
        channelMapProcess.getSmokeChannelMap(NettyGlobalConstant.filterGroupSmokeData);

        // 电表设备netty连接初始化
        channelMapProcess.getElectricChannelMap(NettyGlobalConstant.filterGroupPowerData);
        channelMapProcess.getElectricActiveChannelMap(NettyGlobalConstant.filterGroupPowerData);
        channelMapProcess.getElectricReactiveChannelMap(NettyGlobalConstant.filterGroupPowerData);
        channelMapProcess.getElectricCurrentChannelMap(NettyGlobalConstant.filterGroupPowerData);
        channelMapProcess.getElectricVoltageChannelMap(NettyGlobalConstant.filterGroupPowerData);

        channelMapProcess.getFreshAirChannelMap(NettyGlobalConstant.filterFreshAirData);
        channelMapProcess.getDehumidifierChannelMap(NettyGlobalConstant.dehumidifierData);
        channelMapProcess.getTemplatureChannelMap(NettyGlobalConstant.filterGroupTemperatureAndHumidityData);

        log.info("=========== 初始化网关和设备的关联关系结束 =============");
    }

}
