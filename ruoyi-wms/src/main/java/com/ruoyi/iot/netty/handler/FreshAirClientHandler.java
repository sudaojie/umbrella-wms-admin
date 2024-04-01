package com.ruoyi.iot.netty.handler;

import com.ruoyi.iot.packet.freshair.rsp.FreshWriteMultiCoilValRsp;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

/**
 * 新风设备-客户端处理类
 */
@Slf4j
public class FreshAirClientHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("新风设备-客户端连接建立成功");
        super.channelActive(ctx);
    }


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        String message = (String) msg;
        log.info("收到新风设备，服务端的消息内容:" + message);

        FreshWriteMultiCoilValRsp freshWriteMultiCoilValRsp = FreshWriteMultiCoilValRsp.hexStrToObj(message);
        log.info(freshWriteMultiCoilValRsp.getCoilAddress().toString());

        super.channelRead(ctx, msg);
    }


    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
//        ctx.channel().close();
    }

}
