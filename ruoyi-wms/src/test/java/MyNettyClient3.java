
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.ruoyi.iot.packet.electric.req.ElectricReadPhaseActivePowerReq;
import com.ruoyi.iot.packet.electric.req.ElectricReadVoltageReq;
import com.ruoyi.wcs.domain.WcsDeviceBaseInfo;
import com.ruoyi.wms.utils.socket.DeviceDataStream;
import com.ruoyi.wms.utils.socket.DeviceSocketUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class MyNettyClient3 {

    public static Map<WcsDeviceBaseInfo, Socket> channels = new HashMap<>();

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


    public static void main(String[] args) {
        try {

            //1.数据准备工作
            WcsDeviceBaseInfo gateWayDeviceBaseInfo = JSON.parseObject("{\"channelId\":\"\",\"createBy\":\"admin\",\"createTime\":\"2023-05-05 15:08:49\",\"deviceArea\":\"2\",\"deviceIp\":\"10.1.5.8\",\"deviceName\":\"网关4号\",\"deviceNo\":\"gateway04\",\"devicePort\":27,\"deviceType\":\"8\",\"enableStatus\":\"0\",\"id\":\"fa0f0fb380f249258c57e517a912edc4\",\"params\":{},\"updateBy\":\"admin\",\"updateTime\":\"2023-05-05 15:11:33\"}", WcsDeviceBaseInfo.class);
            List<WcsDeviceBaseInfo> powerDevices = JSON.parseArray("[{\"channelId\":\"\",\"createBy\":\"admin\",\"createTime\":\"2023-04-10 15:19:28\",\"deviceAddress\":\"01\",\"deviceArea\":\"0\",\"deviceName\":\"电表1号\",\"deviceNo\":\"db1\",\"deviceProducer\":\"jundu\",\"deviceType\":\"6\",\"enableStatus\":\"0\",\"id\":\"4af9de5ef1874b078cf2266f5e0d8ab2\",\"params\":{},\"updateBy\":\"admin\",\"updateTime\":\"2023-05-05 17:46:35\"}]", WcsDeviceBaseInfo.class);
            Map<WcsDeviceBaseInfo, List<WcsDeviceBaseInfo>> deviceGroupData = new HashMap<>();
            deviceGroupData.put(gateWayDeviceBaseInfo, powerDevices);
            List<WcsDeviceBaseInfo> gateWayDevices = new ArrayList<>(deviceGroupData.keySet());

            //2.初始化设备和channel的关联map
            MyNettyClient3.getChannel(gateWayDevices);

            // Thread.sleep(3000);
            deviceGroupData.forEach((gateWayDevice, powerDeviceList) -> {
                Socket socket = MyNettyClient3.channels.get(gateWayDevice);
                for (WcsDeviceBaseInfo powerDevice : powerDeviceList) {
                    if (StrUtil.isNotEmpty(powerDevice.getDeviceAddress())) {
                        //有功总电能
                        ElectricReadVoltageReq electricReadVoltageReq = new ElectricReadVoltageReq(powerDevice.getDeviceAddress(), "03",
                                "00", "25",
                                "0003", "", "");

                        //有功功率
                        ElectricReadPhaseActivePowerReq electricReadPhaseActivePowerReq = new ElectricReadPhaseActivePowerReq(powerDevice.getDeviceAddress(), "03",
                                "00", "2E",
                                "0004", "", "");
                        if (ObjectUtil.isNull(socket)) {
                            return;
                        }
                        //1.构建对象
                        DeviceDataStream deviceDataStream = DeviceSocketUtil.buildDataStream(socket);
                        try {
                            //2.写入（有功总电能）
                            DeviceSocketUtil.writeData(deviceDataStream.getOut(),electricReadVoltageReq.toString());

                            //3.读取（有功总电能）
                            String rspData1 = DeviceSocketUtil.readData(deviceDataStream.getInput());
                            System.out.println("有功总电能返回,"+rspData1);

                            // 写入（有功功率）
                            DeviceSocketUtil.writeData(deviceDataStream.getOut(),electricReadPhaseActivePowerReq.toString());

                            //读取（有功功率）
                            String rspData2 = DeviceSocketUtil.readData(deviceDataStream.getInput());
                            System.out.println("有功功率返回,"+rspData2);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }finally {
                            //4.关闭资源
                            DeviceSocketUtil.close(deviceDataStream.getInput(),deviceDataStream.getOut());
                        }
                    } else {
                        log.error("电表设备地址码异常");
                    }
                }

            });

        } catch (Exception e) {
            e.printStackTrace();
        }
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
