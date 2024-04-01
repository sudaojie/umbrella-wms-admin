package com.ruoyi.iot.task.ctrl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.ruoyi.iot.global.NettyGlobalConstant;
import com.ruoyi.iot.packet.dehumidifier.req.DehumidifierWriteMultiCoilValReq;
import com.ruoyi.iot.packet.freshair.req.FreshWriteMultiCoilValReq;
import com.ruoyi.wcs.domain.WcsDeviceBaseInfo;
import com.ruoyi.wms.utils.socket.DeviceDataStream;
import com.ruoyi.wms.utils.socket.DeviceSocketUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.net.Socket;
import java.util.List;
import java.util.Map;

/**
 * 除湿机控制任务
 */
@Slf4j
@Service
public class DehumidifierCtrlTask {

    /**
     * 开启除湿机
     *
     * @param openDehumidifierDevices 除湿机设备列表
     */
    public void setOpenDehumidifierDevices(List<WcsDeviceBaseInfo> openDehumidifierDevices) {
        Map<WcsDeviceBaseInfo, List<WcsDeviceBaseInfo>> deviceGroupData = NettyGlobalConstant.dehumidifierData;
        deviceGroupData.forEach((gateWayDevice, powerDeviceList) -> {
            Socket socket = NettyGlobalConstant.dehumidifierMap.get(gateWayDevice);
            for (WcsDeviceBaseInfo powerDevice : powerDeviceList) {
                for (WcsDeviceBaseInfo openDehumidifierDevice : openDehumidifierDevices) {
                    if (powerDevice.getDeviceNo().equals(openDehumidifierDevice.getDeviceNo())) {
                        if (StrUtil.isNotEmpty(powerDevice.getDeviceAddress())) {
                            if (powerDevice.getDeviceNo().equals(openDehumidifierDevice.getDeviceNo())) {
                                //todo 除湿机开启请求报文对象
                                DehumidifierWriteMultiCoilValReq dehumidifierWriteMultiCoilValReq =
                                        new DehumidifierWriteMultiCoilValReq(powerDevice.getDeviceAddress(), "0F", "00", "00",
                                                Integer.toHexString(Integer.parseInt(powerDevice.getCoilAddress())), "01", "FF", "", "");
                                if (ObjectUtil.isNull(socket)) {
                                    return;
                                }
                                //1.构建对象
                                DeviceDataStream deviceDataStream = DeviceSocketUtil.buildDataStream(socket);
                                try {
                                    //2.写入
                                    DeviceSocketUtil.writeData(deviceDataStream.getOut(), dehumidifierWriteMultiCoilValReq.toString());
                                    //3.读取
                                    String responseData = DeviceSocketUtil.readData(deviceDataStream.getInput());
                                    if (StrUtil.isNotEmpty(responseData)) {
                                        log.info("开启除湿机 response = " + responseData.trim());
                                    }
                                } catch (Exception e) {
                                    log.error(e.getMessage());
                                } finally {
                                    //4.关闭资源
//                                    DeviceSocketUtil.close(deviceDataStream.getInput(), deviceDataStream.getOut());
                                }
                            }
                        } else {
                            log.error("开启除湿机设备地址码异常");
                        }
                    }
                }
            }
        });
    }


    /**
     * 关闭除湿机
     *
     * @param closeDehumidifierDevices 除湿机设备列表
     */
    public void setCloseDehumidifierDevices(List<WcsDeviceBaseInfo> closeDehumidifierDevices) {
        Map<WcsDeviceBaseInfo, List<WcsDeviceBaseInfo>> deviceGroupData = NettyGlobalConstant.dehumidifierData;
        deviceGroupData.forEach((gateWayDevice, powerDeviceList) -> {
            Socket socket = NettyGlobalConstant.dehumidifierMap.get(gateWayDevice);
            for (WcsDeviceBaseInfo powerDevice : powerDeviceList) {
                for (WcsDeviceBaseInfo closeDehumidifierDevice : closeDehumidifierDevices) {
                    if (powerDevice.getDeviceNo().equals(closeDehumidifierDevice.getDeviceNo())) {
                        if (StrUtil.isNotEmpty(powerDevice.getDeviceAddress())) {
                            if (powerDevice.getDeviceNo().equals(closeDehumidifierDevice.getDeviceNo())) {
                                //todo 除湿关闭请求报文对象
                                DehumidifierWriteMultiCoilValReq dehumidifierWriteMultiCoilValReq =
                                        new DehumidifierWriteMultiCoilValReq(powerDevice.getDeviceAddress(), "0F", "00", "00",
                                                Integer.toHexString(Integer.parseInt(powerDevice.getCoilAddress())), "01", "00", "", "");
                                if (ObjectUtil.isNull(socket)) {
                                    return;
                                }
                                //1.构建对象
                                DeviceDataStream deviceDataStream = DeviceSocketUtil.buildDataStream(socket);
                                try {
                                    //2.写入
                                    DeviceSocketUtil.writeData(deviceDataStream.getOut(), dehumidifierWriteMultiCoilValReq.toString());
                                    //3.读取
                                    String responseData = DeviceSocketUtil.readData(deviceDataStream.getInput());
                                    if (StrUtil.isNotEmpty(responseData)) {
                                        log.info("关闭除湿机 response = " + responseData.trim());
                                    }
                                } catch (Exception e) {
                                    log.error(e.getMessage());
                                } finally {
                                    //4.关闭资源
//                                    DeviceSocketUtil.close(deviceDataStream.getInput(), deviceDataStream.getOut());
                                }
                            }
                        } else {
                            log.error("关闭除湿机设备地址码异常");
                        }
                    }
                }
            }
        });
    }


}
