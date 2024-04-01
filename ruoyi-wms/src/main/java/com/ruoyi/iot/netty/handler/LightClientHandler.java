package com.ruoyi.iot.netty.handler;

import com.ruoyi.iot.packet.light.rsp.*;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

/**
 * 照明设备-客户端处理类
 */
@Slf4j
public class LightClientHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("照明设备-客户端连接建立成功");
        super.channelActive(ctx);
    }


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        String message = (String) msg;
        log.info("收到照明设备，服务端的消息内容:" + message);

        // 每个线圈的状态对应一个 bit 的数据，8 个线圈刚好对应一个字节的数据，如果单次读取 9-16
        // 个线圈的数据，字节数就是 2，以此类推。数据 0x05 的二进制表示为 00000101，表示 DO0
        // 和 DO2 状态为 1，其余 DO 状态为 0。
        // 读取状态将16—>2 从后往前依次读取 0-关 1-开
//        LightReadCoilValRsp lightReadCoilValRsp = LightReadCoilValRsp.hexStrToObj(message);
//        log.info("线圈状态:" + lightReadCoilValRsp.getCoilStatus());

//        LightReadDisCreteInputValRsp lightReadDisCreteInputValRsp = LightReadDisCreteInputValRsp.hexStrToObj(message);
//        log.info("离散量（DI）状态:" + lightReadDisCreteInputValRsp.getDiscreteStatus());

//        LightReadHoldingRegisterValRsp lightReadHoldingRegisterValRsp = LightReadHoldingRegisterValRsp.hexStrToObj(message);
//        log.info("保持寄存器数据:" + lightReadHoldingRegisterValRsp.getRegisterVal());

//        LightReadingInputRegisterValRsp lightReadingInputRegisterValRsp = LightReadingInputRegisterValRsp.hexStrToObj(message);
//        log.info("输入寄存器数量:" + lightReadingInputRegisterValRsp.getRegisterVal());
//
//        LightWriteCoilValRsp lightWriteCoilValRsp = LightWriteCoilValRsp.hexStrToObj(message);
//        log.info("线圈地址:" + lightWriteCoilValRsp.getCoilAddress());
//        log.info("控制方式:" + lightWriteCoilValRsp.getControlMethod());
//
//        LightWriteMultiCoilValRsp lightWriteMultiCoilValRsp = LightWriteMultiCoilValRsp.hexStrToObj(message);
//        log.info("线圈数量:" + lightWriteMultiCoilValRsp.getCoilAddress());


//        LightWriteMultiCoilValRsp lightWriteMultiCoilValRsp = LightWriteMultiCoilValRsp.hexStrToObj(message);
//        log.info("写入多个线圈数量:" + lightWriteMultiCoilValRsp.getCoilAddress());

//        LightWriteMultiHoldingRegisterValRsp lightWriteMultiHoldingRegisterValRsp = LightWriteMultiHoldingRegisterValRsp.hexStrToObj(message);
//        log.info("写入多个保持寄存器数量:" + lightWriteMultiHoldingRegisterValRsp.getRegisterCount());
//
//        LightWriteSingleHoldingRegisterValRsp lightWriteSingleHoldingRegisterValRsp = LightWriteSingleHoldingRegisterValRsp.hexStrToObj(message);
//        log.info("写入值:" + lightWriteSingleHoldingRegisterValRsp.getWriteVal());

        super.channelRead(ctx, msg);
    }


    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
//        ctx.channel().close();
    }

}
