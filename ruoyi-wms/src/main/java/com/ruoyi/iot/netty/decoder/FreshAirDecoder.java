package com.ruoyi.iot.netty.decoder;

import cn.hutool.core.util.HexUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * 新风设备-解码器
 */
@Slf4j
public class FreshAirDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        log.info("==========开始进行-新风设备消息解码==================");
        byte[] bytes = new byte[in.readableBytes()];
        in.readBytes(bytes);
        out.add(HexUtil.encodeHexStr(bytes));
    }

}
