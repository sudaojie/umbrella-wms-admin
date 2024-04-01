package com.ruoyi.iot.netty.client;

import com.ruoyi.iot.netty.decoder.FreshAirDecoder;
import com.ruoyi.iot.netty.encoder.FreshAirEncoder;
import com.ruoyi.iot.netty.handler.FreshAirClientHandler;
import com.ruoyi.iot.packet.freshair.req.FreshWriteMultiCoilValReq;
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
public class FreshAirNettyTcpClient {

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
                                .addLast(new FreshAirDecoder())
                                .addLast(new FreshAirEncoder())
                                .addLast(new FreshAirClientHandler());
                    }
                });
        try {
            ChannelFuture future = bootstrap.connect(new InetSocketAddress("10.1.5.7", 26)).sync();
            Channel channel = future.channel();
            channel.writeAndFlush(new FreshWriteMultiCoilValReq().toString());
            future.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            worker.shutdownGracefully();
        }
    }
}
