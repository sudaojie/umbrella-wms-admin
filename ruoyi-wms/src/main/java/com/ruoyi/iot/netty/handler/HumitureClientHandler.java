package com.ruoyi.iot.netty.handler;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.ruoyi.common.core.domain.BaseEntity;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.enums.DelFlagEnum;
import com.ruoyi.common.enums.EnableStatus;
import com.ruoyi.common.utils.spring.SpringUtils;
import com.ruoyi.iot.global.NettyGlobalConstant;
import com.ruoyi.iot.packet.humiture.rsp.HumitureReadRsp;
import com.ruoyi.wcs.domain.WcsDeviceBaseInfo;
import com.ruoyi.wcs.domain.WcsFreshAirDetailInfo;
import com.ruoyi.wcs.domain.WcsTemplatureHumidityCollectInfo;
import com.ruoyi.wcs.enums.wcs.WcsTaskDeviceTypeEnum;
import com.ruoyi.wcs.service.WcsDeviceBaseInfoService;
import com.ruoyi.wcs.service.WcsFreshAirDetailInfoService;
import com.ruoyi.wcs.service.WcsTemplatureHumidityCollectInfoService;
import com.ruoyi.wms.basics.domain.Location;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * 温湿度设备-客户端处理类
 */
@Slf4j
@Component
public class HumitureClientHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("温湿度设备-客户端连接建立成功");
        super.channelActive(ctx);
    }


    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        String message = (String) msg;
        log.info("收到温湿度设备，服务端的消息内容:" + message);
        HumitureReadRsp humitureReadRsp = HumitureReadRsp.hexStrToObj(message);
        log.info("湿度:"+humitureReadRsp.getHumidityVal());
        log.info("温度:"+humitureReadRsp.getTemperatureVal());
        WcsDeviceBaseInfoService wcsDeviceBaseInfoService = SpringUtils.getBean(WcsDeviceBaseInfoService.class);
        WcsTemplatureHumidityCollectInfoService wcsTemplatureHumidityCollectInfoService = SpringUtils.getBean(WcsTemplatureHumidityCollectInfoService.class);
        List<WcsDeviceBaseInfo> list = wcsDeviceBaseInfoService.getBaseMapper().selectList(new QueryWrapper<WcsDeviceBaseInfo>()
                .eq("device_address", humitureReadRsp.getAddressCode())
                .eq("device_type", WcsTaskDeviceTypeEnum.TEMPERATURE_AND_HUMIDITY.getCode()));
        WcsDeviceBaseInfo wcsDeviceBaseInfo = null;
        if (CollUtil.isNotEmpty(list)) {
            wcsDeviceBaseInfo = list.get(0);

            // 记录当前实时温湿度
            wcsDeviceBaseInfo.setTemplature(String.valueOf(humitureReadRsp.getTemperatureVal()));
            wcsDeviceBaseInfo.setHumidity(String.valueOf(humitureReadRsp.getHumidityVal()));
            wcsDeviceBaseInfoService.saveOrUpdate(wcsDeviceBaseInfo);

            // 记录温湿度历史表
            if (humitureReadRsp.getTemperatureVal() < 50.00) {
                WcsTemplatureHumidityCollectInfo collectInfo = new WcsTemplatureHumidityCollectInfo();
                collectInfo.setDeviceInfoId(wcsDeviceBaseInfo.getId());
                collectInfo.setTemplature(String.valueOf(humitureReadRsp.getTemperatureVal()));
                collectInfo.setHumidity(String.valueOf(humitureReadRsp.getHumidityVal()));
                collectInfo.setCollectTime(new Date());
                wcsTemplatureHumidityCollectInfoService.saveOrUpdate(collectInfo);
                NettyGlobalConstant.humidityMap.put(humitureReadRsp.getAddressCode(), humitureReadRsp);
            }
        }
        super.channelRead(ctx, msg);
    }


    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
//        ctx.channel().close();
    }

}
