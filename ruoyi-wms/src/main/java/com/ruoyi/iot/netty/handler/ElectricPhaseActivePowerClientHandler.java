package com.ruoyi.iot.netty.handler;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ruoyi.common.utils.spring.SpringUtils;
import com.ruoyi.iot.packet.electric.rsp.ElectricReadPhaseActivePowerRsp;
import com.ruoyi.wcs.domain.WcsDeviceBaseInfo;
import com.ruoyi.wcs.domain.WcsPowerCollectInfo;
import com.ruoyi.wcs.enums.wcs.WcsTaskDeviceTypeEnum;
import com.ruoyi.wcs.mapper.WcsEnergyConsumeMonitorMapper;
import com.ruoyi.wcs.service.WcsDeviceBaseInfoService;
import com.ruoyi.wcs.service.WcsPowerCollectInfoService;
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
public class ElectricPhaseActivePowerClientHandler extends ChannelInboundHandlerAdapter {

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
        ElectricReadPhaseActivePowerRsp electricReadPhaseActivePowerRsp = ElectricReadPhaseActivePowerRsp.hexStrToObj(message,a);
        log.info("A相有功功率：{}", electricReadPhaseActivePowerRsp.getPhaseActivePowerA());
        log.info("B相有功功率：{}", electricReadPhaseActivePowerRsp.getPhaseActivePowerB());
        log.info("C相有功功率：{}", electricReadPhaseActivePowerRsp.getPhaseActivePowerC());
        log.info("总相有功功率：{}", electricReadPhaseActivePowerRsp.getTotalPhaseActivePower());

        WcsDeviceBaseInfoService wcsDeviceBaseInfoService = SpringUtils.getBean(WcsDeviceBaseInfoService.class);

        WcsPowerCollectInfoService wcsPowerCollectInfoService = SpringUtils.getBean(WcsPowerCollectInfoService.class);
        WcsEnergyConsumeMonitorMapper wcsEnergyConsumeMonitorMapper = SpringUtils.getBean(WcsEnergyConsumeMonitorMapper.class);
        WcsDeviceBaseInfo wcsDeviceBaseInfo = wcsDeviceBaseInfoService.getBaseMapper()
                .selectOne(new QueryWrapper<WcsDeviceBaseInfo>()
                        .eq("device_address", electricReadPhaseActivePowerRsp.getAddressCode()).eq("device_type", WcsTaskDeviceTypeEnum.AMMETER.getCode()));
        Date date = DateUtil.date();

        if (ObjectUtil.isNotNull(wcsDeviceBaseInfo)) {
            ThreadUtil.sleep(1, TimeUnit.SECONDS);
            List<WcsPowerCollectInfo> infoList = wcsEnergyConsumeMonitorMapper.selectDiffList(wcsDeviceBaseInfo.getId(), String.valueOf(date), wcsDeviceBaseInfo.getDeviceAddress());
            if (CollUtil.isNotEmpty(infoList)) {
                infoList.forEach(e -> {
                    e.setPhaseActivePowerA(String.valueOf(electricReadPhaseActivePowerRsp.getPhaseActivePowerA()))
                            .setPhaseActivePowerB(String.valueOf(electricReadPhaseActivePowerRsp.getPhaseActivePowerB()))
                            .setPhaseActivePowerC(String.valueOf(electricReadPhaseActivePowerRsp.getPhaseActivePowerC()))
                            .setTotalActivePower(String.valueOf(electricReadPhaseActivePowerRsp.getTotalPhaseActivePower()))
                            .setCollectTime(new Date()).setDeviceInfoId(wcsDeviceBaseInfo.getId()).setPostalAddress(electricReadPhaseActivePowerRsp.getAddressCode());
                });
                wcsPowerCollectInfoService.saveOrUpdateBatch(infoList);
            } else {
                WcsPowerCollectInfo collectInfo = new WcsPowerCollectInfo();
                collectInfo.setPhaseActivePowerA(String.valueOf(electricReadPhaseActivePowerRsp.getPhaseActivePowerA()))
                        .setPhaseActivePowerB(String.valueOf(electricReadPhaseActivePowerRsp.getPhaseActivePowerB()))
                        .setPhaseActivePowerC(String.valueOf(electricReadPhaseActivePowerRsp.getPhaseActivePowerC()))
                        .setTotalActivePower(String.valueOf(electricReadPhaseActivePowerRsp.getTotalPhaseActivePower()))
                        .setCollectTime(new Date()).setDeviceInfoId(wcsDeviceBaseInfo.getId()).setPostalAddress(electricReadPhaseActivePowerRsp.getAddressCode())
                        .setPhaseReactivePowerA("0").setPhaseReactivePowerB("0").setPhaseReactivePowerC("0").setTotalReactivePower("0");
                wcsPowerCollectInfoService.saveOrUpdate(collectInfo);
            }
        }

        super.channelRead(ctx, msg);
    }


    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
//        ctx.channel().close();
    }

}
