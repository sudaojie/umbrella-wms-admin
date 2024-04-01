package com.ruoyi.iot.netty.client;

import com.ruoyi.iot.netty.constant.NettyConfigConstant;
import com.ruoyi.iot.netty.decoder.LightDecoder;
import com.ruoyi.iot.netty.encoder.LightEncoder;
import com.ruoyi.iot.netty.handler.LightClientHandler;
import com.ruoyi.iot.packet.light.req.*;
import com.ruoyi.iot.packet.light.rsp.LightReadDisCreteInputValRsp;
import com.ruoyi.iot.packet.light.rsp.LightReadingInputRegisterValRsp;
import com.ruoyi.iot.packet.light.rsp.LightWriteCoilValRsp;
import com.ruoyi.iot.packet.light.rsp.LightWriteMultiHoldingRegisterValRsp;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.FixedLengthFrameDecoder;

import java.net.InetSocketAddress;

/**
 * @author Administrator
 */
public class LightNettyTcpClient {

    static int msgLength = 6;

    public static void main(String[] args) {
        EventLoopGroup worker = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(worker).channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline()
                                .addLast(new FixedLengthFrameDecoder(msgLength))
                                .addLast(new LightDecoder())
                                .addLast(new LightEncoder())
                                .addLast(new LightClientHandler());
                    }
                });
        try {
            ChannelFuture future = bootstrap.connect(new InetSocketAddress(NettyConfigConstant.IP, NettyConfigConstant.PORT)).sync();
            Channel channel = future.channel();

//            channel.writeAndFlush(new LightReadCoilValReq().toString());
//            channel.writeAndFlush(new LightWriteCoilValReq().toString());
            channel.writeAndFlush(new LightWriteMultiCoilValReq().toString());


//            channel.writeAndFlush(new LightReadDisCreteInputValReq().toString());
//            channel.writeAndFlush(new LightReadHoldingRegisterValReq().toString());
//           channel.writeAndFlush(new LightWriteSingleHoldingRegisterValReq().toString());
            future.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            worker.shutdownGracefully();
        }
    }
}
