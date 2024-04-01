package com.ruoyi.iot.netty.handler;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ruoyi.common.utils.spring.SpringUtils;
import com.ruoyi.iot.packet.electric.rsp.*;
import com.ruoyi.wcs.domain.WcsDeviceBaseInfo;
import com.ruoyi.wcs.domain.WcsElectricalEnergyCollectInfo;
import com.ruoyi.wcs.enums.wcs.WcsTaskDeviceTypeEnum;
import com.ruoyi.wcs.service.WcsDeviceBaseInfoService;
import com.ruoyi.wcs.service.WcsElectricalEnergyCollectInfoService;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.net.InetSocketAddress;
import java.util.Date;

/**
 * 电表设备-客户端处理类
 */
@Slf4j
@Component
public class ElectricClientHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        InetSocketAddress ipSocket = (InetSocketAddress) ctx.channel().remoteAddress();
        log.info("电表设备-客户端连接建立成功,ip:{},port:{}",ipSocket.getHostName(),ipSocket.getPort());
        super.channelActive(ctx);
    }


    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        WcsDeviceBaseInfoService wcsDeviceBaseInfoService = SpringUtils.getBean(WcsDeviceBaseInfoService.class);

        WcsElectricalEnergyCollectInfoService wcsElectricalEnergyCollectInfoService = SpringUtils.getBean(WcsElectricalEnergyCollectInfoService.class);

        String message = (String) msg;
        log.info("收到电表设备，服务端的消息内容:" + message);
        String a = "";

        ElectricReadPhaseActiveEnergyRsp electricReadPhaseActiveEnergyRsp = ElectricReadPhaseActiveEnergyRsp.hexStrToObj(message, a);
        log.info("总有功电能：{}", electricReadPhaseActiveEnergyRsp.getTotalActiveEnergy());
        WcsElectricalEnergyCollectInfo wcsElectricalEnergyCollectInfo = new WcsElectricalEnergyCollectInfo();
        WcsDeviceBaseInfo wcsDeviceBaseInfo = wcsDeviceBaseInfoService.getBaseMapper()
                .selectOne(new QueryWrapper<WcsDeviceBaseInfo>()
                .eq("device_address", electricReadPhaseActiveEnergyRsp.getAddressCode()).eq("device_type", WcsTaskDeviceTypeEnum.AMMETER.getCode()));
        if (ObjectUtil.isNotNull(wcsDeviceBaseInfo)) {
            wcsElectricalEnergyCollectInfo.setId(IdUtil.fastSimpleUUID())
                    .setActiveTotalElectricalEnergy(electricReadPhaseActiveEnergyRsp.getTotalActiveEnergy())
                    .setCollectTime(new Date()).setPostalAddress(electricReadPhaseActiveEnergyRsp.getAddressCode())
                    .setDeviceInfoId(ObjectUtil.isNotNull(wcsDeviceBaseInfo) ? wcsDeviceBaseInfo.getId() : "");
            wcsElectricalEnergyCollectInfoService.saveOrUpdate(wcsElectricalEnergyCollectInfo);
        }

        super.channelRead(ctx, msg);
    }


    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
//        ctx.channel().close();
    }

}
