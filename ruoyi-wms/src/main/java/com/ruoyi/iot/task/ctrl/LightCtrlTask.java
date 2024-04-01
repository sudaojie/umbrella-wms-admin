package com.ruoyi.iot.task.ctrl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.HexUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.ruoyi.iot.global.NettyGlobalConstant;
import com.ruoyi.iot.packet.light.req.LightWriteMultiCoilValReq;
import com.ruoyi.iot.task.common.TaskTime;
import com.ruoyi.wcs.domain.WcsDeviceBaseInfo;
import com.ruoyi.wcs.util.WcsSocketUtil;
import com.ruoyi.wms.utils.socket.DeviceDataStream;
import com.ruoyi.wms.utils.socket.DeviceSocketUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.Socket;
import java.util.List;
import java.util.Map;

/**
 * 照明控制任务
 */
@Slf4j
@Service
public class LightCtrlTask {

    /**
     * 开灯
     *
     * @param openLightDevices 需要开启的照明设备列表
     */
    public void openLight(List<WcsDeviceBaseInfo> openLightDevices) {
        Map<WcsDeviceBaseInfo, List<WcsDeviceBaseInfo>> deviceGroupData = NettyGlobalConstant.filterLightData;
        if (CollUtil.isNotEmpty(deviceGroupData) && CollUtil.isNotEmpty(deviceGroupData.values())) {
            deviceGroupData.forEach((gateWayDevice, powerDeviceList) -> {
                // Socket socket = NettyGlobalConstant.lightChannelMap.get(gateWayDevice);
                for (WcsDeviceBaseInfo powerDevice : powerDeviceList) {
                    for (WcsDeviceBaseInfo openLightDevice : openLightDevices) {
                        if (powerDevice.getDeviceNo().equals(openLightDevice.getDeviceNo())) {
                            Socket socket = null;
                            try {
                                socket = new Socket(gateWayDevice.getDeviceIp(), gateWayDevice.getDevicePort().intValue());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            if (StrUtil.isNotEmpty(powerDevice.getDeviceAddress())) {
                                if(openLightDevices.size()>1){
                                    // 线程睡眠1000毫秒
                                    try {
                                        Thread.sleep(TaskTime.LINGT_SLEEP_TIME);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }
                                //todo 照明开启请求报文对象
                                //FF00 开 0000 关  //A4050000FF00
                                LightWriteMultiCoilValReq lightWriteMultiCoilValReq =
                                        new LightWriteMultiCoilValReq(powerDevice.getDeviceAddress(), "05",
                                                powerDevice.getCoilAddress(),
                                                "FF00","", "");
                                if (ObjectUtil.isNull(socket)) {
                                    return;
                                }
                                buildSocketStream(socket, lightWriteMultiCoilValReq);
                            } else {
                                log.error("照明设备地址码异常");
                            }
                        }
                    }
                }
            });
        }
    }

    /**
     * 关灯
     *
     * @param closeLightDevices 需要关闭的照明设备列表
     */
    public void closeLight(List<WcsDeviceBaseInfo> closeLightDevices) {
        Map<WcsDeviceBaseInfo, List<WcsDeviceBaseInfo>> deviceGroupData = NettyGlobalConstant.filterLightData;
        deviceGroupData.forEach((gateWayDevice, powerDeviceList) -> {
            // Socket socket = NettyGlobalConstant.lightChannelMap.get(gateWayDevice);
            for (WcsDeviceBaseInfo powerDevice : powerDeviceList) {
                for (WcsDeviceBaseInfo closeLightDevice : closeLightDevices) {
                    if (powerDevice.getDeviceNo().equals(closeLightDevice.getDeviceNo())) {
                        Socket socket = null;
                        try {
                            socket = new Socket(gateWayDevice.getDeviceIp(), gateWayDevice.getDevicePort().intValue());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        if (StrUtil.isNotEmpty(powerDevice.getDeviceAddress())) {
                            //todo 照明开启请求报文对象
                            //FF00 开 0000 关
                            LightWriteMultiCoilValReq lightWriteMultiCoilValReq =
                                    new LightWriteMultiCoilValReq(powerDevice.getDeviceAddress(), "05",
                                            powerDevice.getCoilAddress(),
                                            "0000","", "");
                            if (ObjectUtil.isNull(socket)) {
                                return;
                            }
                            buildSocketStream(socket, lightWriteMultiCoilValReq);
                        } else {
                            log.error("照明设备地址码异常");
                        }
                    }
                }
            }
        });
    }

    /**
     *
     * @param socket socket
     * @param lightWriteMultiCoilValReq lightWriteMultiCoilValReq
     */
    private void buildSocketStream(Socket socket, LightWriteMultiCoilValReq lightWriteMultiCoilValReq) {
        //1.构建对象
        DeviceDataStream deviceDataStream = DeviceSocketUtil.buildDataStream(socket);
        try {
            //2.写入
            DeviceSocketUtil.writeData(deviceDataStream.getOut(), lightWriteMultiCoilValReq.toString());
            //3.读取
            String responseData = DeviceSocketUtil.readData(deviceDataStream.getInput());
            if (StrUtil.isNotEmpty(responseData)) {
                log.info("采集照明设备数据 response = " + responseData.trim());
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        } finally {
            //4.关闭资源
            DeviceSocketUtil.close(deviceDataStream.getInput(), deviceDataStream.getOut());
        }
    }

}
