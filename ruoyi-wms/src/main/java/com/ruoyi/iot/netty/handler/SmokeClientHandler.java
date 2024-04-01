package com.ruoyi.iot.netty.handler;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ruoyi.iot.packet.smoke.rsp.SmokeReadMeasuredValRsp;
import com.ruoyi.wcs.domain.WcsDeviceBaseInfo;
import com.ruoyi.wcs.domain.WcsDeviceEarlyWarningInfo;
import com.ruoyi.wcs.service.WcsDeviceBaseInfoService;
import com.ruoyi.wcs.service.WcsDeviceEarlyWarningInfoService;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * 烟雾感知设备-客户端处理类
 */
@Slf4j
@Component
public class SmokeClientHandler extends ChannelInboundHandlerAdapter {

//    @Autowired
//    private WcsDeviceBaseInfoService wcsDeviceBaseInfoService;
//
//    @Autowired
//    private WcsDeviceEarlyWarningInfoService wcsDeviceEarlyWarningInfoService;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("烟雾感知设备-客户端连接建立成功");
        super.channelActive(ctx);
    }

    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        String message = (String) msg;
        log.info("收到烟雾感知设备，服务端的消息内容:" + message);
        SmokeReadMeasuredValRsp smokeReadMeasuredValRsp = SmokeReadMeasuredValRsp.hexStrToObj(message);
        log.info("烟感报警值:" + smokeReadMeasuredValRsp.getSmokeAlarmVal());
        if ("1".equals(smokeReadMeasuredValRsp.getSmokeAlarmVal().toString())) {
            if (StrUtil.isNotEmpty(smokeReadMeasuredValRsp.getAddressCode())) {

//                WcsDeviceBaseInfo wcsDeviceBaseInfo = wcsDeviceBaseInfoService.getBaseMapper()
//                        .selectOne(new QueryWrapper<WcsDeviceBaseInfo>().eq("device_address", smokeReadMeasuredValRsp.getAddressCode()));
//                if (ObjectUtil.isNotNull(wcsDeviceBaseInfo)) {
//                    WcsDeviceEarlyWarningInfo wcsDeviceEarlyWarningInfo = new WcsDeviceEarlyWarningInfo();
//                    wcsDeviceEarlyWarningInfo.setId(IdUtil.fastSimpleUUID());
//                    wcsDeviceEarlyWarningInfo.setDeviceInfoId(wcsDeviceBaseInfo.getDeviceNo());
//                    wcsDeviceEarlyWarningInfo.setWarningContent("当前处于报警状态，烟雾浓度过高");
//                    wcsDeviceEarlyWarningInfo.setWarningTime(new Date());
//                    wcsDeviceEarlyWarningInfoService.saveOrUpdate(wcsDeviceEarlyWarningInfo);
//                }
            }
        }
        super.channelRead(ctx, msg);
    }


    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
//        ctx.channel().close();
    }

}
