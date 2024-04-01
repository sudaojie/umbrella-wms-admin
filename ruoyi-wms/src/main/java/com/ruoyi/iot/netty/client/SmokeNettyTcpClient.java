package com.ruoyi.iot.netty.client;

import com.ruoyi.iot.netty.constant.NettyConfigConstant;
import com.ruoyi.iot.netty.decoder.SmokeDecoder;
import com.ruoyi.iot.netty.encoder.SmokeEncoder;
import com.ruoyi.iot.netty.handler.SmokeClientHandler;
import com.ruoyi.iot.packet.smoke.req.SmokeReadMeasuredValReq;
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
 * 烟感
 * @author Administrator
 */
public class SmokeNettyTcpClient {

    static int msgLength = 7;

    public static void main(String[] args) {
        EventLoopGroup worker = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(worker).channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline()
                                .addLast(new FixedLengthFrameDecoder(msgLength))
                                .addLast(new SmokeDecoder())
                                .addLast(new SmokeEncoder())
                                .addLast(new SmokeClientHandler());
                    }
                });
        try {
            ChannelFuture future = bootstrap.connect(new InetSocketAddress(NettyConfigConstant.IP, NettyConfigConstant.PORT)).sync();
            Channel channel = future.channel();
            channel.writeAndFlush(new SmokeReadMeasuredValReq().toString());
            future.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            worker.shutdownGracefully();
        }
    }
}
