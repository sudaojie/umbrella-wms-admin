package com.ruoyi.iot.task.ctrl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.ruoyi.iot.global.NettyGlobalConstant;
import com.ruoyi.iot.packet.freshair.req.FreshReadDisCreteInputValReq;
import com.ruoyi.iot.packet.freshair.req.FreshWriteMultiCoilValReq;
import com.ruoyi.wcs.domain.WcsDeviceBaseInfo;
import com.ruoyi.wcs.domain.WcsFreshAirDetailInfo;
import com.ruoyi.wcs.enums.wcs.WcsSwitchStatusEnum;
import com.ruoyi.wcs.mapper.WcsFreshAirDetailInfoMapper;
import com.ruoyi.wms.utils.socket.DeviceDataStream;
import com.ruoyi.wms.utils.socket.DeviceSocketUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.net.Socket;
import java.util.List;
import java.util.Map;

/**
 * @author sdj
 * @create 2023-08-05 16:55
 */
@Slf4j
@Service
public class FreshAirDlCtrTask {

    @Resource
    private WcsFreshAirDetailInfoMapper wcsFreshAirDetailInfoMapper;

    /**
     * 获取新风Dl状态 修改线圈开关
     */
    public void getFreshAirDl(){
        Map<WcsDeviceBaseInfo, List<WcsDeviceBaseInfo>> deviceGroupData = NettyGlobalConstant.filterFreshAirData;
        deviceGroupData.forEach((gateWayDevice, powerDeviceList) -> {
            for (WcsDeviceBaseInfo powerDevice : powerDeviceList) {
                if (StrUtil.isNotEmpty(powerDevice.getDeviceAddress())) {
                    NettyGlobalConstant.lock.lock();
                    Socket socket = null;
                    try {
                        socket = new Socket(gateWayDevice.getDeviceIp(), gateWayDevice.getDevicePort().intValue());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (ObjectUtil.isNull(socket)) {
                        return;
                    }
                    //1.构建对象
                    DeviceDataStream freshAirDataDlStream = DeviceSocketUtil.buildDataStream(socket);
                    // DeviceDataStream freshAirDataDlStream = new DeviceDataStream();
                    try {
                        //todo 读取新风是否开启远程 DL状态 a40200010001
                        FreshReadDisCreteInputValReq freshReadDisCreteInputValReq =
                                new FreshReadDisCreteInputValReq(powerDevice.getDeviceAddress(), "02",
                                        powerDevice.getCoilAddress(),
                                        "0001", "", "");
                        //2.写入
                        DeviceSocketUtil.writeData(freshAirDataDlStream.getOut(), freshReadDisCreteInputValReq.toString());
                        //3.读取
                        String responseReadDate = DeviceSocketUtil.readData(freshAirDataDlStream.getInput());
                        if (StrUtil.isNotEmpty(responseReadDate)) {
                            log.info("读取DL状态 response = " + responseReadDate.trim());
                            String dlSatus = responseReadDate.substring(6, 8);
                            if ("01".equals(dlSatus)) {
                                //todo 新风关闭请求报文对象 A40500000000
                                FreshWriteMultiCoilValReq freshWriteValReq =
                                        new FreshWriteMultiCoilValReq(powerDevice.getDeviceAddress(), "05",
                                                powerDevice.getCoilAddress(),
                                                "0000", "", "");
                                if (ObjectUtil.isNull(socket)) {
                                    return;
                                }
                                //1.构建对象
                                   freshAirDataDlStream = DeviceSocketUtil.buildDataStream(socket);
                                    //2.写入
                                    DeviceSocketUtil.writeData(freshAirDataDlStream.getOut(), freshWriteValReq.toString());
                                    //3.读取
                                    String responseData = DeviceSocketUtil.readData(freshAirDataDlStream.getInput());
                                    if (StrUtil.isNotEmpty(responseData)) {
                                        log.info("关闭新风 response = " + responseData.trim());
                                    }
                                    //根据设备id查询新风详细内容
                                    LambdaQueryWrapper<WcsFreshAirDetailInfo> freshAirQw = Wrappers.lambdaQuery();
                                    freshAirQw.eq(WcsFreshAirDetailInfo::getDeviceInfoId, powerDevice.getId());
                                    WcsFreshAirDetailInfo wcsFreshAirDetailInfo = wcsFreshAirDetailInfoMapper.selectOne(freshAirQw);
                                    wcsFreshAirDetailInfo.setSwitchStatus(Integer.parseInt(WcsSwitchStatusEnum.CLOSE.getCode()));
                                    wcsFreshAirDetailInfoMapper.updateById(wcsFreshAirDetailInfo);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        NettyGlobalConstant.lock.unlock();
                        DeviceSocketUtil.close(freshAirDataDlStream.getInput(), freshAirDataDlStream.getOut());
                    }
                }else{
                    log.error("查询新风设备DL状态异常");
                }
            }
        });
    }
}
