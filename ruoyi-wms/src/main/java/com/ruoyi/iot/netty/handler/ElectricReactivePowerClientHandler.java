package com.ruoyi.iot.netty.handler;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ruoyi.common.utils.spring.SpringUtils;
import com.ruoyi.iot.packet.electric.rsp.ElectricReadReactivePowerRsp;
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

/**
 * 电表设备-客户端处理类
 */
@Slf4j
@Component
public class ElectricReactivePowerClientHandler extends ChannelInboundHandlerAdapter {

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
        ElectricReadReactivePowerRsp electricReadReactivePowerRsp = ElectricReadReactivePowerRsp.hexStrToObj(message,a);
        log.info("无功功率A：{}", electricReadReactivePowerRsp.getPhaseReactivePowerA());
        log.info("无功功率B：{}", electricReadReactivePowerRsp.getPhaseReactivePowerB());
        log.info("无功功率C：{}", electricReadReactivePowerRsp.getPhaseReactivePowerC());
        log.info("总无功功率：{}", electricReadReactivePowerRsp.getTotalPhaseReactivePower());

        WcsDeviceBaseInfoService wcsDeviceBaseInfoService = SpringUtils.getBean(WcsDeviceBaseInfoService.class);

        WcsPowerCollectInfoService wcsPowerCollectInfoService = SpringUtils.getBean(WcsPowerCollectInfoService.class);
        WcsEnergyConsumeMonitorMapper wcsEnergyConsumeMonitorMapper = SpringUtils.getBean(WcsEnergyConsumeMonitorMapper.class);
        WcsDeviceBaseInfo wcsDeviceBaseInfo = wcsDeviceBaseInfoService.getBaseMapper()
                .selectOne(new QueryWrapper<WcsDeviceBaseInfo>()
                        .eq("device_address", electricReadReactivePowerRsp.getAddressCode()).eq("device_type", WcsTaskDeviceTypeEnum.AMMETER.getCode()));
        Date date = new Date();

        if (ObjectUtil.isNotNull(wcsDeviceBaseInfo)) {
            List<WcsPowerCollectInfo> infoList = wcsEnergyConsumeMonitorMapper.selectDiffList(wcsDeviceBaseInfo.getId(), String.valueOf(date), wcsDeviceBaseInfo.getDeviceAddress());
            if (CollUtil.isNotEmpty(infoList)) {
                infoList.forEach(e -> {
                    e.setPhaseReactivePowerA(String.valueOf(electricReadReactivePowerRsp.getPhaseReactivePowerA()))
                            .setPhaseReactivePowerB(String.valueOf(electricReadReactivePowerRsp.getPhaseReactivePowerB()))
                            .setPhaseReactivePowerC(String.valueOf(electricReadReactivePowerRsp.getPhaseReactivePowerC()))
                            .setTotalReactivePower(String.valueOf(electricReadReactivePowerRsp.getTotalPhaseReactivePower()))
                            .setCollectTime(new Date()).setDeviceInfoId(wcsDeviceBaseInfo.getId()).setPostalAddress(electricReadReactivePowerRsp.getAddressCode());
                });
                wcsPowerCollectInfoService.saveOrUpdateBatch(infoList);
            } else {
                WcsPowerCollectInfo collectInfo = new WcsPowerCollectInfo();
                collectInfo.setPhaseReactivePowerA(String.valueOf(electricReadReactivePowerRsp.getPhaseReactivePowerA()))
                        .setPhaseReactivePowerB(String.valueOf(electricReadReactivePowerRsp.getPhaseReactivePowerB()))
                        .setPhaseReactivePowerC(String.valueOf(electricReadReactivePowerRsp.getPhaseReactivePowerC()))
                        .setTotalReactivePower(String.valueOf(electricReadReactivePowerRsp.getTotalPhaseReactivePower()))
                        .setCollectTime(new Date()).setDeviceInfoId(wcsDeviceBaseInfo.getId()).setPostalAddress(electricReadReactivePowerRsp.getAddressCode())
                .setPhaseActivePowerA("0").setPhaseActivePowerB("0").setPhaseActivePowerC("0").setTotalActivePower("0");
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
