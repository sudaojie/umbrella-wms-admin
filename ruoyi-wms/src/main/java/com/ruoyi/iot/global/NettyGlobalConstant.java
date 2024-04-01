package com.ruoyi.iot.global;

import com.ruoyi.iot.packet.humiture.rsp.HumitureReadRsp;
import com.ruoyi.wcs.domain.WcsDeviceBaseInfo;
import io.netty.channel.Channel;

import java.net.Socket;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Netty全局常量通讯对象
 */
public class NettyGlobalConstant {

    /**
     * 网关设备-->温湿度传感器设备的关联关系
     */
    public static Map<WcsDeviceBaseInfo,List<WcsDeviceBaseInfo>> filterGroupTemperatureAndHumidityData = new HashMap<>();

    /**
     * 网关设备-->烟雾传感器设备的关联关系
     */
    public static Map<WcsDeviceBaseInfo,List<WcsDeviceBaseInfo>> filterGroupSmokeData = new HashMap<>();

    /**
     * 网关设备-->电表传感器设备的关联关系
     */
    public static Map<WcsDeviceBaseInfo,List<WcsDeviceBaseInfo>> filterGroupPowerData = new HashMap<>();

    /**
     * 网关设备-->照明设备的关联关系
     */
    public static Map<WcsDeviceBaseInfo,List<WcsDeviceBaseInfo>> filterLightData = new HashMap<>();

    /**
     * 网关设备-->新风设备的关联关系
     */
    public static Map<WcsDeviceBaseInfo,List<WcsDeviceBaseInfo>> filterFreshAirData = new HashMap<>();

    /**
     * 网关设备-->除湿机设备的关联关系
     */
    public static Map<WcsDeviceBaseInfo,List<WcsDeviceBaseInfo>> dehumidifierData = new HashMap<>();

    /**
     * 照明网关-管道
     */
    public static Map<WcsDeviceBaseInfo, Socket> lightChannelMap = new HashMap<>();

    /**
     * 烟感网关-管道
     */
    public static Map<WcsDeviceBaseInfo, Socket> smokeChannelMap = new HashMap<>();

    /**
     * 电表电能网关-管道
     */
    public static Map<WcsDeviceBaseInfo, Socket> electricChannelMap = new HashMap<>();

    /**
     * 电表有功功率网关-管道
     */
    public static Map<WcsDeviceBaseInfo, Socket> electricActivePowerChannelMap = new HashMap<>();

    /**
     * 电表无功功率网关-管道
     */
    public static Map<WcsDeviceBaseInfo, Socket> electricReactivePowerChannelMap = new HashMap<>();

    /**
     * 电表电流网关-管道
     */
    public static Map<WcsDeviceBaseInfo, Socket> electricCurrentChannelMap = new HashMap<>();

    /**
     * 电表电压网关-管道
     */
    public static Map<WcsDeviceBaseInfo, Socket> electricVoltagePowerChannelMap = new HashMap<>();

    /**
     * 新风网关-管道
     */
    public static Map<WcsDeviceBaseInfo, Socket> freshAirChannelMap = new HashMap<>();

    /**
     * 除湿机网关-管道
     */
    public static Map<WcsDeviceBaseInfo, Socket> dehumidifierMap = new HashMap<>();

    /**
     * 温湿度网关-管道
     */
    public static Map<WcsDeviceBaseInfo, Socket> templatureChannelMap = new HashMap<>();

    /**
     * 温湿度结果集
     */
    public static Map<String, HumitureReadRsp> humidityMap = new HashMap();

    /**
     * 温湿度结果集
     */
    public static  Map<String, List<HumitureReadRsp>> humiditylistMap = new HashMap();

    /**
     * 新风温湿度网关-管道
     */
    public static Map<WcsDeviceBaseInfo,List<WcsDeviceBaseInfo>> freshAirMappingMap = new HashMap();

    public static Lock lock = new ReentrantLock();

}
