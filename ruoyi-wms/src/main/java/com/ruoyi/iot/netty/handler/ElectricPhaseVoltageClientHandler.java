package com.ruoyi.iot.netty.handler;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ruoyi.common.utils.spring.SpringUtils;
import com.ruoyi.iot.packet.electric.rsp.ElectricReadVoltageRsp;
import com.ruoyi.wcs.domain.WcsDeviceBaseInfo;
import com.ruoyi.wcs.domain.WcsVoltageCurrentCollectInfo;
import com.ruoyi.wcs.enums.wcs.WcsTaskDeviceTypeEnum;
import com.ruoyi.wcs.mapper.WcsEnergyConsumeMonitorMapper;
import com.ruoyi.wcs.service.WcsDeviceBaseInfoService;
import com.ruoyi.wcs.service.WcsVoltageCurrentCollectInfoService;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 电表设备-客户端处理类
 */
@Slf4j
@Component
public class ElectricPhaseVoltageClientHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("电表设备-客户端连接建立成功");
        super.channelActive(ctx);
    }


    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        String message = (String) msg;
        log.info("收到电表设备，服务端的消息内容:" + message);
        String a = "";
        ElectricReadVoltageRsp electricReadVoltageRsp = ElectricReadVoltageRsp.hexStrToObj(message,a);
        log.info("相电压UA：{}", electricReadVoltageRsp.getPhaseVoltageA());
        log.info("相电压UB：{}", electricReadVoltageRsp.getPhaseVoltageB());
        log.info("相电压UC：{}", electricReadVoltageRsp.getPhaseVoltageC());

        WcsDeviceBaseInfoService wcsDeviceBaseInfoService = SpringUtils.getBean(WcsDeviceBaseInfoService.class);

        WcsVoltageCurrentCollectInfoService wcsVoltageCurrentCollectInfoService = SpringUtils.getBean(WcsVoltageCurrentCollectInfoService.class);
        WcsEnergyConsumeMonitorMapper wcsEnergyConsumeMonitorMapper = SpringUtils.getBean(WcsEnergyConsumeMonitorMapper.class);
        WcsDeviceBaseInfo wcsDeviceBaseInfo = wcsDeviceBaseInfoService.getBaseMapper()
                .selectOne(new QueryWrapper<WcsDeviceBaseInfo>()
                        .eq("device_address", electricReadVoltageRsp.getAddressCode()).eq("device_type", WcsTaskDeviceTypeEnum.AMMETER.getCode()));
        Date date = DateUtil.date();

        if (ObjectUtil.isNotNull(wcsDeviceBaseInfo)) {
            ThreadUtil.sleep(1, TimeUnit.SECONDS);
            List<WcsVoltageCurrentCollectInfo> infoList = wcsEnergyConsumeMonitorMapper.selectDiffVcList(wcsDeviceBaseInfo.getId(), String.valueOf(date), wcsDeviceBaseInfo.getDeviceAddress());
            if (CollUtil.isNotEmpty(infoList)) {
                infoList.forEach(e -> {
                    e.setPhaseVoltageA(String.valueOf(electricReadVoltageRsp.getPhaseVoltageA()))
                            .setPhaseVoltageB(String.valueOf(electricReadVoltageRsp.getPhaseVoltageB()))
                            .setPhaseVoltageC(String.valueOf(electricReadVoltageRsp.getPhaseVoltageC()))
                            .setCollectTime(new Date()).setDeviceInfoId(wcsDeviceBaseInfo.getId()).setPostalAddress(electricReadVoltageRsp.getAddressCode());
                });
                wcsVoltageCurrentCollectInfoService.saveOrUpdateBatch(infoList);
            } else {
                WcsVoltageCurrentCollectInfo collectInfo = new WcsVoltageCurrentCollectInfo();
                collectInfo.setPhaseVoltageA(String.valueOf(electricReadVoltageRsp.getPhaseVoltageA()))
                        .setPhaseVoltageB(String.valueOf(electricReadVoltageRsp.getPhaseVoltageB()))
                        .setPhaseVoltageC(String.valueOf(electricReadVoltageRsp.getPhaseVoltageC()))
                        .setCollectTime(new Date()).setDeviceInfoId(wcsDeviceBaseInfo.getId()).setPostalAddress(electricReadVoltageRsp.getAddressCode())
                        .setPhaseCurrentA("0").setPhaseCurrentB("0").setPhaseCurrentC("0");
                wcsVoltageCurrentCollectInfoService.saveOrUpdate(collectInfo);
            }
        }
        super.channelRead(ctx, msg);
    }


    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
//        ctx.channel().close();
    }

}
