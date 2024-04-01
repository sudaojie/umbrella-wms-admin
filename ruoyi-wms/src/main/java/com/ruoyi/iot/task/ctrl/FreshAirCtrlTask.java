package com.ruoyi.iot.task.ctrl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.iot.global.NettyGlobalConstant;
import com.ruoyi.iot.packet.electric.req.ElectricReadCurrentReq;
import com.ruoyi.iot.packet.freshair.req.FreshReadDisCreteInputValReq;
import com.ruoyi.iot.packet.freshair.req.FreshWriteMultiCoilValReq;
import com.ruoyi.iot.task.common.TaskTime;
import com.ruoyi.wcs.domain.WcsDeviceBaseInfo;
import com.ruoyi.wms.utils.socket.DeviceDataStream;
import com.ruoyi.wms.utils.socket.DeviceSocketUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 新风控制任务
 */
@Slf4j
@Service
public class FreshAirCtrlTask {

    /**
     * 开启新风
     *
     * @param openFreshAirDevices 新风设备列表
     */
    public AjaxResult openFreshAir(List<WcsDeviceBaseInfo> openFreshAirDevices) {
        Map<WcsDeviceBaseInfo, List<WcsDeviceBaseInfo>> deviceGroupData = NettyGlobalConstant.filterFreshAirData;
        AtomicReference<String> msg= new AtomicReference<>("");
        List<String> erroList = new ArrayList<>();
        StringBuffer stringBuffer = new StringBuffer();
        deviceGroupData.forEach((gateWayDevice, powerDeviceList) -> {
            for (WcsDeviceBaseInfo powerDevice : powerDeviceList) {
                for (WcsDeviceBaseInfo openFreshAirDevice : openFreshAirDevices) {
                    if (powerDevice.getDeviceNo().equals(openFreshAirDevice.getDeviceNo())) {
                        // NettyGlobalConstant.lock();
                        Socket socket = null;
                        try {
                            socket = new Socket(gateWayDevice.getDeviceIp(), gateWayDevice.getDevicePort().intValue());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        if (StrUtil.isNotEmpty(powerDevice.getDeviceAddress())) {
                            if (powerDevice.getDeviceNo().equals(openFreshAirDevice.getDeviceNo())) {
                                if (openFreshAirDevices.size() > 1) {
                                    // 线程睡眠500毫秒
                                    try {
                                        Thread.sleep(TaskTime.FRESH_SLEEP_TIME);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }
                                //todo 读取新风是否开启远程 DL状态 a40200010001
                                FreshReadDisCreteInputValReq freshReadDisCreteInputValReq =
                                        new FreshReadDisCreteInputValReq(powerDevice.getDeviceAddress(), "02",
                                                powerDevice.getCoilAddress(),
                                                "0001", "", "");
                                if (ObjectUtil.isNull(socket)) {
                                    return;
                                }
                                //1.构建对象
                                DeviceDataStream deviceDataDl = DeviceSocketUtil.buildDataStream(socket);
                                // DeviceDataStream deviceDataOpen = new DeviceDataStream();
                                try {
                                    //2.写入
                                    DeviceSocketUtil.writeData(deviceDataDl.getOut(), freshReadDisCreteInputValReq.toString());
                                    //3.读取
                                    String responseReadDate = DeviceSocketUtil.readData(deviceDataDl.getInput());
                                    if (StrUtil.isNotEmpty(responseReadDate)) {
                                        log.info("读取DL状态 response = " + responseReadDate.trim());
                                        String dlSatus = responseReadDate.substring(6, 8);
                                        if ("01".equals(dlSatus)) {
                                            erroList.add(openFreshAirDevice.getId());
                                            if (StrUtil.isNotEmpty(msg.get())) {
                                                msg.set(msg.get() + ", " + powerDevice.getDeviceName() + " 当前非远程状态");
                                            } else {
                                                msg.set(powerDevice.getDeviceName() + " 当前非远程状态");
                                            }
                                            break;
                                        }
                                    }
                                    //todo 新风开启请求报文对象 A4050000FF00
                                    FreshWriteMultiCoilValReq freshWriteMultiCoilValReq =
                                            new FreshWriteMultiCoilValReq(powerDevice.getDeviceAddress(), "05",
                                                    powerDevice.getCoilAddress(),
                                                    "FF00", "", "");
                                    // //1.构建对象
                                    // deviceDataOpen = DeviceSocketUtil.buildDataStream(socket);
                                    //2.写入
                                    DeviceSocketUtil.writeData(deviceDataDl.getOut(), freshWriteMultiCoilValReq.toString());
                                    //3.读取
                                    String responseData = DeviceSocketUtil.readData(deviceDataDl.getInput());
                                    if (StrUtil.isNotEmpty(responseData)) {
                                        log.info("开启新风 response = " + responseData.trim());
                                    }
                                    // 线程睡眠1500毫秒
                                    try {
                                        Thread.sleep(TaskTime.ELECTRIC_SLEEP_TIME);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                    //判断新风电流
                                    Map<String, String> resultMap = electricity(powerDevice, openFreshAirDevice);
                                    if (StrUtil.isNotEmpty(resultMap.get("data"))) {
                                        erroList.add(resultMap.get("data"));
                                    }
                                    if (StrUtil.isNotEmpty(resultMap.get("msg"))) {
                                        stringBuffer.append(resultMap.get("msg")).append(",");
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                } finally {
                                    // NettyGlobalConstant.lock.unlock();
                                    //4.关闭资源
                                    DeviceSocketUtil.close(deviceDataDl.getInput(), deviceDataDl.getOut());
                                }
                            }
                        } else {
                            log.error("开启新风设备地址码异常");
                        }
                    }
                }
            }
        });
        //去除故障信息最后一个,
        String result = stringBuffer.toString();
        if (result.endsWith(",")) {
            if(StrUtil.isNotEmpty(msg.get())){
                msg.set(msg.get()+", "+ result.substring(0, result.length() - 1));
            }else{
                msg.set(result.substring(0, result.length() - 1));
            }
        }
        if (!"".equals(msg.get())) {
            AjaxResult ajax = new AjaxResult();
            // 终止循环
            ajax.put(AjaxResult.DATA_TAG, erroList);
            ajax.put(AjaxResult.MSG_TAG,msg.get());
            ajax.put(AjaxResult.CODE_TAG, HttpStatus.WARN);
            return ajax;
        }else{
            return AjaxResult.success(openFreshAirDevices);
        }
    }

    /**
     * 关闭新风
     *
     * @param closeFreshAirDevices 新风设备列表
     */
    public AjaxResult closeFreshAir(List<WcsDeviceBaseInfo> closeFreshAirDevices) {
        Map<WcsDeviceBaseInfo, List<WcsDeviceBaseInfo>> deviceGroupData = NettyGlobalConstant.filterFreshAirData;
        deviceGroupData.forEach((gateWayDevice, powerDeviceList) -> {
            for (WcsDeviceBaseInfo powerDevice : powerDeviceList) {
                for (WcsDeviceBaseInfo closeFreshAirDevice : closeFreshAirDevices) {
                    if (powerDevice.getDeviceNo().equals(closeFreshAirDevice.getDeviceNo())) {
                        if (StrUtil.isNotEmpty(powerDevice.getDeviceAddress())) {
                            if (powerDevice.getDeviceNo().equals(closeFreshAirDevice.getDeviceNo())) {
                                // NettyGlobalConstant.lock.lock();
                                Socket socket = null;
                                try {
                                    socket = new Socket(gateWayDevice.getDeviceIp(), gateWayDevice.getDevicePort().intValue());
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                //todo 新风关闭请求报文对象 A40500000000
                                FreshWriteMultiCoilValReq freshWriteMultiCoilValReq =
                                        new FreshWriteMultiCoilValReq(powerDevice.getDeviceAddress(), "05",
                                                powerDevice.getCoilAddress(),
                                                "0000", "", "");
                                if (ObjectUtil.isNull(socket)) {
                                    return;
                                }
                                //1.构建对象
                                DeviceDataStream deviceDataStream = DeviceSocketUtil.buildDataStream(socket);
                                try {
                                    //2.写入
                                    DeviceSocketUtil.writeData(deviceDataStream.getOut(), freshWriteMultiCoilValReq.toString());
                                    //3.读取
                                    String responseData = DeviceSocketUtil.readData(deviceDataStream.getInput());
                                    if (StrUtil.isNotEmpty(responseData)) {
                                        log.info("关闭新风 response = " + responseData.trim());
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                } finally {
                                    //4.关闭资源
                                    // NettyGlobalConstant.lock.unlock();
                                    DeviceSocketUtil.close(deviceDataStream.getInput(), deviceDataStream.getOut());
                                }
                            }
                        } else {
                            log.error("关闭新风设备地址码异常");
                        }
                    }
                }
            }
        });
        return AjaxResult.success(closeFreshAirDevices);
    }

    /**
     *判断新风开启 电流电压是否满足条件
     */
    private Map<String,String> electricity(WcsDeviceBaseInfo powerDevice,WcsDeviceBaseInfo openFreshAirDevice){
        Map<WcsDeviceBaseInfo, List<WcsDeviceBaseInfo>> deviceGroupData = NettyGlobalConstant.filterGroupPowerData;
        Map<String,String> resultMap = new HashMap<>();
        AtomicReference<String> msg= new AtomicReference<>("");
        AtomicReference<String> erroId= new AtomicReference<>("");
        deviceGroupData.forEach((gateWayDevice, powerDeviceList) -> {
            if(powerDeviceList.size() > 0){
                Socket socketElectric = null;
                try {
                    socketElectric = new Socket(gateWayDevice.getDeviceIp(), gateWayDevice.getDevicePort().intValue());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                String address = "";
                if ("0000".equals(powerDevice.getCoilAddress())) {
                    address =  "a8";
                } else if ("0001".equals(powerDevice.getCoilAddress())) {
                    address ="a9";
                } else if ("0002".equals(powerDevice.getCoilAddress())) {
                    address = "aa";
                } else if ("0003".equals(powerDevice.getCoilAddress())) {
                    address = "ab";
                }
                if (ObjectUtil.isNull(socketElectric)) {
                    return;
                }
                //todo 读取电表是否有电流 0103002B0003
                ElectricReadCurrentReq electricReadCurrentReq =
                        new ElectricReadCurrentReq(address, "03", "00",
                                "2b", "0003", "", "");
                    //1.构建对象
                    DeviceDataStream deviceElectric = DeviceSocketUtil.buildDataStream(socketElectric);
                try {
                    //2.写入
                    DeviceSocketUtil.writeData(deviceElectric.getOut(), electricReadCurrentReq.toString());
                    //3.读取
                   String responseReadDate = DeviceSocketUtil.readData(deviceElectric.getInput());
                    if (StrUtil.isNotEmpty(responseReadDate)) {
                        log.info("读取电流 response = " + responseReadDate.trim());
                        String aElectric = responseReadDate.substring(6, 10);
                        String bElectric = responseReadDate.substring(10, 14);
                        String cElectric = responseReadDate.substring(14, 18);
                        if ("0000".equals(aElectric) && "0000".equals(bElectric) && "0000".equals(cElectric)) {
                            closeFir(powerDevice);
                            erroId.set(openFreshAirDevice.getId());
                            msg.set(powerDevice.getDeviceName()+" 故障");
                            // throw new ServiceException(powerDevice.getDeviceName() + " 故障");
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    //4.关闭资源
                    DeviceSocketUtil.close(deviceElectric.getInput(), deviceElectric.getOut());
                }
            }
        });
        resultMap.put("msg",msg.get());
        resultMap.put("data",erroId.get());
        return resultMap;
    }

    /**
     * 关闭新风
     * @param powerDevice
     */
    public void closeFir(WcsDeviceBaseInfo powerDevice){
        //关闭新风
        Map<WcsDeviceBaseInfo, List<WcsDeviceBaseInfo>> deviceGroup = NettyGlobalConstant.filterFreshAirData;
        deviceGroup.forEach((gateFreshAirWayDevice, freshAirDeviceList) -> {
            for (WcsDeviceBaseInfo freshAirDevice : freshAirDeviceList) {
                Socket freshAirSocket = null;
                try {
                    freshAirSocket = new Socket(gateFreshAirWayDevice.getDeviceIp(), gateFreshAirWayDevice.getDevicePort().intValue());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (freshAirDevice.getDeviceNo().equals(powerDevice.getDeviceNo())) {
                    //todo 新风关闭请求报文对象 A40500000000
                    FreshWriteMultiCoilValReq freshWriteValReq =
                            new FreshWriteMultiCoilValReq(freshAirDevice.getDeviceAddress(), "05",
                                    freshAirDevice.getCoilAddress(),
                                    "0000", "", "");
                    if (ObjectUtil.isNull(freshAirSocket)) {
                        return;
                    }
                    //1.构建对象
                    DeviceDataStream freshAirSocketDeviceDataStream = DeviceSocketUtil.buildDataStream(freshAirSocket);
                    try {
                        //2.写入
                        DeviceSocketUtil.writeData(freshAirSocketDeviceDataStream.getOut(), freshWriteValReq.toString());
                        //3.读取
                        String responseData = DeviceSocketUtil.readData(freshAirSocketDeviceDataStream.getInput());
                        if (StrUtil.isNotEmpty(responseData)) {
                            log.info("关闭新风 response = " + responseData.trim());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        //4.关闭资源
                        DeviceSocketUtil.close(freshAirSocketDeviceDataStream.getInput(), freshAirSocketDeviceDataStream.getOut());
                    }
                }
            }
        });
    }
}
