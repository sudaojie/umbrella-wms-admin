package com.ruoyi.iot.netty.client;

import com.ruoyi.iot.netty.constant.NettyConfigConstant;
import com.ruoyi.iot.netty.decoder.ElectricDecoder;
import com.ruoyi.iot.netty.encoder.ElectricEncoder;
import com.ruoyi.iot.netty.handler.ElectricClientHandler;
import com.ruoyi.iot.packet.electric.req.*;
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
 * 电表
 * @author Administrator
 */
public class ElectricNettyTcpClient {

    static int msgLength = 11;

    public static void main(String[] args) {
        EventLoopGroup worker = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(worker).channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline()
                                .addLast(new FixedLengthFrameDecoder(msgLength))
                                .addLast(new ElectricDecoder())
                                .addLast(new ElectricEncoder())
                                .addLast(new ElectricClientHandler());
                    }
                });
        try {
            ChannelFuture future = bootstrap.connect(new InetSocketAddress(NettyConfigConstant.IP, NettyConfigConstant.PORT)).sync();
            Channel channel = future.channel();
//            channel.writeAndFlush(new ElectricReadAddressReq().toString());
            channel.writeAndFlush(new ElectricReadVoltageReq().toString());
//                        channel.writeAndFlush(new ElectricReadApparentPowerReq().toString());
//                        channel.writeAndFlush(new ElectricReadCurrentReq().toString());
//                        channel.writeAndFlush(new ElectricReadEnergyReq().toString());
            //            channel.writeAndFlush(new ElectricReadLineVoltageReq().toString());
//                        channel.writeAndFlush(new ElectricReadMaximumReq().toString());
//                        channel.writeAndFlush(new ElectricReadMinimumReq().toString());
//                        channel.writeAndFlush(new ElectricReadPhaseActiveEnergyReq().toString());
//                        channel.writeAndFlush(new ElectricReadPhaseActivePowerReq().toString());
            //            channel.writeAndFlush(new ElectricReadPhasePowerFactorReq().toString());
//                        channel.writeAndFlush(new ElectricReadReactivePowerReq().toString());
//                        channel.writeAndFlush(new ElectricReadSideZeroVoltageCurrentReq().toString());
//                        channel.writeAndFlush(new ElectricReadVoltagePhaseAngleReq().toString());
            //            channel.writeAndFlush(new ElectricReadReactivePowerReq().toString());
//                        channel.writeAndFlush(new ElectricReadVoltageReq().toString());
            future.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            worker.shutdownGracefully();
        }
    }
}
