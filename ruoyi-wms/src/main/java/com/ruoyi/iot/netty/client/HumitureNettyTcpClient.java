package com.ruoyi.iot.netty.client;

import com.ruoyi.iot.netty.constant.NettyConfigConstant;
import com.ruoyi.iot.netty.decoder.HumitureDecoder;
import com.ruoyi.iot.netty.encoder.HumitureEncoder;
import com.ruoyi.iot.netty.handler.HumitureClientHandler;
import com.ruoyi.iot.packet.humiture.req.HumitureReadReq;
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
public class HumitureNettyTcpClient {

    static int msgLength = 9;

    public static void main(String[] args) {
        EventLoopGroup worker = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(worker).channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline()
                                .addLast(new FixedLengthFrameDecoder(msgLength))
                                .addLast(new HumitureDecoder())
                                .addLast(new HumitureEncoder())
                                .addLast(new HumitureClientHandler());
                    }
                });
        try {
            ChannelFuture future = bootstrap.connect(new InetSocketAddress("10.1.5.8", 24)).sync();
            Channel channel = future.channel();
            channel.writeAndFlush(new HumitureReadReq().toString());
            future.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            worker.shutdownGracefully();
        }
    }
}
