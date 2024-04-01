package com.ruoyi.iot.task.channel;

import cn.hutool.core.util.ObjectUtil;
import com.ruoyi.iot.global.NettyGlobalConstant;
import com.ruoyi.wcs.domain.WcsDeviceBaseInfo;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class ChannelMapProcess {

    /**
     * 获取所有连接
     *
     * @param gateWayDevices 网关设备列表
     * @return
     */
    public static final Map<WcsDeviceBaseInfo, Socket> getChannel(List<WcsDeviceBaseInfo> gateWayDevices) throws IOException {
        Map<WcsDeviceBaseInfo, Socket> result = new HashMap<>();
        for (WcsDeviceBaseInfo wcsDeviceBaseInfo : gateWayDevices) {
            String host = wcsDeviceBaseInfo.getDeviceIp();
            int port = wcsDeviceBaseInfo.getDevicePort().intValue();
            boolean reachable = isReachable(host, port);
            if(!reachable){
                log.info(host+": "+port+" 网络不可达");
                continue;
            }
            Socket socket = new Socket(host, port);
            if (ObjectUtil.isNotNull(socket)) {
                result.put(wcsDeviceBaseInfo, socket);
            }
        }
        return result;
    }

    /**
     * IP地址和端口是否可达
     * @param ipAddress 地址
     * @param port 端口
     * @return ture 可达 false 不可达
     */
    public static boolean isReachable(String ipAddress, int port) {
        try (Socket socket = new Socket()) {
            // 设置socket的超时时间，单位为毫秒
            socket.setSoTimeout(2000);

            // 尝试连接到目标IP地址和端口
            socket.connect(new InetSocketAddress(ipAddress, port), 2000);

            // 如果连接成功，返回true
            return true;

        } catch (IOException e) {
            // 连接失败，返回false
            return false;
        }
    }

    /**
     * 照明Socket连接
     *
     * @param deviceGroupData deviceGroupData
     */
    @Async
    @SneakyThrows
    public void getLightChannelMap(Map<WcsDeviceBaseInfo, List<WcsDeviceBaseInfo>> deviceGroupData) {
        List<WcsDeviceBaseInfo> gateWayDevices = new ArrayList<>(deviceGroupData.keySet());

        //2.初始化设备和channel的关联map
        NettyGlobalConstant.lightChannelMap = getChannel(gateWayDevices);;
    }

    /**
     * 烟感Socket连接
     *
     * @param deviceGroupData deviceGroupData
     */
    @Async
    @SneakyThrows
    public void getSmokeChannelMap(Map<WcsDeviceBaseInfo, List<WcsDeviceBaseInfo>> deviceGroupData) {
        List<WcsDeviceBaseInfo> gateWayDevices = new ArrayList<>(deviceGroupData.keySet());

        //2.初始化设备和channel的关联map
        NettyGlobalConstant.smokeChannelMap = getChannel(gateWayDevices);

    }

    /**
     * 电表Socket连接 总有功电能
     *
     * @param deviceGroupData deviceGroupData
     */
    @Async
    @SneakyThrows
    public void getElectricChannelMap(Map<WcsDeviceBaseInfo, List<WcsDeviceBaseInfo>> deviceGroupData) {
        List<WcsDeviceBaseInfo> gateWayDevices = new ArrayList<>(deviceGroupData.keySet());

        //2.初始化设备和channel的关联map
        NettyGlobalConstant.electricChannelMap = getChannel(gateWayDevices);
    }

    /**
     * 电表Socket连接 有功功率
     *
     * @param deviceGroupData deviceGroupData
     */
    @Async
    @SneakyThrows
    public void getElectricActiveChannelMap(Map<WcsDeviceBaseInfo, List<WcsDeviceBaseInfo>> deviceGroupData) {
        List<WcsDeviceBaseInfo> gateWayDevices = new ArrayList<>(deviceGroupData.keySet());

        //2.初始化设备和channel的关联map
        NettyGlobalConstant.electricActivePowerChannelMap = getChannel(gateWayDevices);
    }

    /**
     * 电表Socket连接 无功功率
     *
     * @param deviceGroupData deviceGroupData
     */
    @Async
    @SneakyThrows
    public void getElectricReactiveChannelMap(Map<WcsDeviceBaseInfo, List<WcsDeviceBaseInfo>> deviceGroupData) {
        List<WcsDeviceBaseInfo> gateWayDevices = new ArrayList<>(deviceGroupData.keySet());

        //2.初始化设备和channel的关联map
        NettyGlobalConstant.electricReactivePowerChannelMap = getChannel(gateWayDevices);
    }

    /**
     * 电表Socket连接 电流
     *
     * @param deviceGroupData deviceGroupData
     */
    @Async
    @SneakyThrows
    public void getElectricCurrentChannelMap(Map<WcsDeviceBaseInfo, List<WcsDeviceBaseInfo>> deviceGroupData) {
        List<WcsDeviceBaseInfo> gateWayDevices = new ArrayList<>(deviceGroupData.keySet());

        //2.初始化设备和channel的关联map
        NettyGlobalConstant.electricCurrentChannelMap = getChannel(gateWayDevices);
    }

    /**
     * 电表Socket连接 电压
     *
     * @param deviceGroupData deviceGroupData
     */
    @Async
    @SneakyThrows
    public void getElectricVoltageChannelMap(Map<WcsDeviceBaseInfo, List<WcsDeviceBaseInfo>> deviceGroupData) {
        List<WcsDeviceBaseInfo> gateWayDevices = new ArrayList<>(deviceGroupData.keySet());

        //2.初始化设备和channel的关联map
        NettyGlobalConstant.electricVoltagePowerChannelMap = getChannel(gateWayDevices);
    }

    /**
     * 新风开关系统Socket连接
     *
     * @param deviceGroupData deviceGroupData
     */
    @Async
    @SneakyThrows
    public void getFreshAirChannelMap(Map<WcsDeviceBaseInfo, List<WcsDeviceBaseInfo>> deviceGroupData) {
        List<WcsDeviceBaseInfo> gateWayDevices = new ArrayList<>(deviceGroupData.keySet());

        //2.初始化设备和channel的关联map
        NettyGlobalConstant.freshAirChannelMap = getChannel(gateWayDevices);
    }

    /**
     * 除湿机开关系统Socket连接
     *
     * @param deviceGroupData deviceGroupData
     */
    @Async
    @SneakyThrows
    public void getDehumidifierChannelMap(Map<WcsDeviceBaseInfo, List<WcsDeviceBaseInfo>> deviceGroupData) {
        List<WcsDeviceBaseInfo> gateWayDevices = new ArrayList<>(deviceGroupData.keySet());

        //2.初始化设备和channel的关联map
        NettyGlobalConstant.dehumidifierMap = getChannel(gateWayDevices);
    }

    /**
     * 温湿度Socket连接
     *
     * @param deviceGroupData deviceGroupData
     */
    @Async
    @SneakyThrows
    public void getTemplatureChannelMap(Map<WcsDeviceBaseInfo, List<WcsDeviceBaseInfo>> deviceGroupData) {
        List<WcsDeviceBaseInfo> gateWayDevices = new ArrayList<>(deviceGroupData.keySet());

        //2.初始化设备和channel的关联map
        NettyGlobalConstant.templatureChannelMap = getChannel(gateWayDevices);
    }

}

