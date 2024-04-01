import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.ruoyi.iot.packet.electric.req.ElectricReadPhaseActivePowerReq;
import com.ruoyi.iot.packet.electric.req.ElectricReadVoltageReq;
import com.ruoyi.iot.packet.humiture.req.HumitureReadReq;
import com.ruoyi.iot.packet.humiture.rsp.HumitureReadRsp;
import com.ruoyi.iot.packet.smoke.req.SmokeReadMeasuredValReq;
import com.ruoyi.iot.packet.smoke.rsp.SmokeReadMeasuredValRsp;
import com.ruoyi.wcs.domain.WcsDeviceBaseInfo;
import com.ruoyi.wcs.enums.wcs.WcsTaskDeviceTypeEnum;
import com.ruoyi.wms.utils.socket.DeviceDataStream;
import com.ruoyi.wms.utils.socket.DeviceSocketUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
public class MyNettyClient4 {

    public static Map<WcsDeviceBaseInfo, Socket> channels = new HashMap<>();

    public static Lock lock = new ReentrantLock();

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
            Socket socket = new Socket(host, port);
            channels.put(wcsDeviceBaseInfo, socket);
        }
        return result;
    }


    public static void main(String[] args) throws IOException {

        List<WcsDeviceBaseInfo> powerDevices = new ArrayList<>();

        //1.数据准备工作
        WcsDeviceBaseInfo gateWayDeviceBaseInfo = JSON.parseObject("{\"channelId\":\"\",\"createBy\":\"admin\",\"createTime\":\"2023-05-05 15:08:49\",\"deviceArea\":\"2\",\"deviceIp\":\"10.1.5.7\",\"deviceName\":\"网关1号\",\"deviceNo\":\"gateway01\",\"devicePort\":23,\"deviceType\":\"8\",\"enableStatus\":\"0\",\"id\":\"fa0f0fb380f249258c57e517a912edc4\",\"params\":{},\"updateBy\":\"admin\",\"updateTime\":\"2023-05-05 15:11:33\"}", WcsDeviceBaseInfo.class);
        //烟感
        List<WcsDeviceBaseInfo> smokeList = JSON.parseArray("[{\"channelId\":\"\",\"createBy\":\"admin\",\"createTime\":\"2023-04-10 15:19:28\",\"deviceAddress\":\"03\",\"deviceArea\":\"0\",\"deviceName\":\"烟感传感器1号\",\"deviceNo\":\"YW2005\",\"deviceProducer\":\"jundu\",\"deviceType\":\"4\",\"enableStatus\":\"0\",\"id\":\"4af9de5ef1874b078cf2266f5e0d8ab2\",\"params\":{},\"updateBy\":\"admin\",\"updateTime\":\"2023-05-05 17:46:35\"}]", WcsDeviceBaseInfo.class);
        //温湿
        List<WcsDeviceBaseInfo> templatureList = JSON.parseArray("[{\"channelId\":\"\",\"createBy\":\"admin\",\"createTime\":\"2023-04-10 15:19:28\",\"deviceAddress\":\"05\",\"deviceArea\":\"2\",\"deviceName\":\"温湿度传感器1号\",\"deviceNo\":\"wsdcgq\",\"deviceProducer\":\"jundu\",\"deviceType\":\"3\",\"enableStatus\":\"0\",\"id\":\"4af9de5ef1874b078cf2266f5e0d8ab2\",\"params\":{},\"updateBy\":\"admin\",\"updateTime\":\"2023-05-05 17:46:35\"}]", WcsDeviceBaseInfo.class);
        powerDevices.addAll(smokeList);
        powerDevices.addAll(templatureList);
        Map<WcsDeviceBaseInfo, List<WcsDeviceBaseInfo>> deviceGroupData = new HashMap<>();
        deviceGroupData.put(gateWayDeviceBaseInfo, powerDevices);
        List<WcsDeviceBaseInfo> gateWayDevices = new ArrayList<>(deviceGroupData.keySet());

        //2.初始化设备和channel的关联map
        MyNettyClient4.getChannel(gateWayDevices);
        //模拟温湿度定时
        new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                deviceGroupData.forEach((gateWayDevice, powerDeviceList) -> {
                    for (WcsDeviceBaseInfo powerDevice : powerDeviceList) {
                        lock.lock();
                        Socket socket = null;
                        try {
                            socket = new Socket(gateWayDevice.getDeviceIp(), gateWayDevice.getDevicePort().intValue());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        if (StrUtil.isNotEmpty(powerDevice.getDeviceAddress())) {
                            //温湿度
                            HumitureReadReq humitureReadReq = new HumitureReadReq(powerDevice.getDeviceAddress(),
                                    "03", "00", "00",
                                    "0002", "", "");

                            if (ObjectUtil.isNull(socket)) {
                                return;
                            }
                            //1.构建对象
                            DeviceDataStream deviceDataStream = DeviceSocketUtil.buildDataStream(socket);
                            try {
                                if (WcsTaskDeviceTypeEnum.TEMPERATURE_AND_HUMIDITY.getCode().equals(powerDevice.getDeviceType())) {
                                    //2.写入（温湿度）
                                    DeviceSocketUtil.writeData(deviceDataStream.getOut(), humitureReadReq.toString());

                                    //3.读取（温湿度）
                                    String rspData1 = DeviceSocketUtil.readData(deviceDataStream.getInput());
                                    if (StrUtil.isNotEmpty(rspData1)) {
                                        log.info("采集温湿度数据 response = " + rspData1.trim());

                                        HumitureReadRsp humitureReadRsp = HumitureReadRsp.hexStrToObj(rspData1);
                                        log.info("湿度:" + humitureReadRsp.getHumidityVal());
                                        log.info("温度:" + humitureReadRsp.getTemperatureVal());
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            } finally {
                                //4.关闭资源
                                DeviceSocketUtil.close(deviceDataStream.getInput(), deviceDataStream.getOut());
                                lock.unlock();
                            }
                        } else {
                            log.error("温湿度设备地址码异常");
                        }
                    }

                });
            }
        }).start();

        //模拟烟感定时
        new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                deviceGroupData.forEach((gateWayDevice, powerDeviceList) -> {
                    for (WcsDeviceBaseInfo powerDevice : powerDeviceList) {
                        lock.lock();
                        Socket socket = null;
                        try {
                            socket = new Socket(gateWayDevice.getDeviceIp(), gateWayDevice.getDevicePort().intValue());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        if (StrUtil.isNotEmpty(powerDevice.getDeviceAddress())) {

                            //烟感
                            SmokeReadMeasuredValReq smokeReadMeasuredValReq = new SmokeReadMeasuredValReq(powerDevice.getDeviceAddress(), "03"
                                    , "02", "00"
                                    , "0001", "", "");
                            if (ObjectUtil.isNull(socket)) {
                                return;
                            }
                            //1.构建对象
                            DeviceDataStream deviceDataStream = DeviceSocketUtil.buildDataStream(socket);
                            try {
                                if (WcsTaskDeviceTypeEnum.SMOKE.getCode().equals(powerDevice.getDeviceType())) {
                                    //2.写入（烟感）
                                    DeviceSocketUtil.writeData(deviceDataStream.getOut(), smokeReadMeasuredValReq.toString());

                                    //3.读取（烟感）
                                    String rspData2 = DeviceSocketUtil.readData(deviceDataStream.getInput());
                                    if (StrUtil.isNotEmpty(rspData2)) {
                                        log.info("采集烟雾传感器数据 response = " + rspData2.trim());
                                        SmokeReadMeasuredValRsp smokeReadMeasuredValRsp = SmokeReadMeasuredValRsp.hexStrToObj(rspData2);
                                        log.info("感烟报警值:" + smokeReadMeasuredValRsp.getSmokeAlarmVal());
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            } finally {
                                //4.关闭资源
                                DeviceSocketUtil.close(deviceDataStream.getInput(), deviceDataStream.getOut());
                                lock.unlock();
                            }
                        } else {
                            log.error("温湿度设备地址码异常");
                        }
                    }

                });
            }
        }).start();

    }


    public static byte[] receive(DataInputStream is) throws Exception {
        try {
            byte[] inputData = new byte[is.available()];
            is.read(inputData);
            return inputData;
        } catch (Exception exception) {
            throw exception;
        }
    }

}
