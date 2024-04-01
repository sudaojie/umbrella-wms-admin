package com.ruoyi.iot.netty.encoder;

import cn.hutool.core.util.HexUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.extern.slf4j.Slf4j;

/**
 * 温湿度设备-编码器
 */
@Slf4j
public class HumitureEncoder extends MessageToByteEncoder<String> {

    @Override
    protected void encode(ChannelHandlerContext ctx, String msg, ByteBuf out) throws Exception {
        log.info("==========开始进行-温湿度设备消息编码==================");
        byte[] hexArray = HexUtil.decodeHex(msg);
        out.writeBytes(hexArray);
    }
}
