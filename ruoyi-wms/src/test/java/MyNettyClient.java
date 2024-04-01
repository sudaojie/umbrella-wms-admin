import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.ruoyi.iot.global.NettyGlobalConstant;
import com.ruoyi.iot.netty.decoder.ElectricDecoder;
import com.ruoyi.iot.netty.encoder.ElectricEncoder;
import com.ruoyi.iot.netty.handler.*;
import com.ruoyi.iot.packet.electric.req.ElectricReadPhaseActivePowerReq;
import com.ruoyi.iot.packet.electric.req.ElectricReadReactivePowerReq;
import com.ruoyi.iot.packet.electric.req.ElectricReadVoltageReq;
import com.ruoyi.wcs.domain.WcsDeviceBaseInfo;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.FixedLengthFrameDecoder;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

@Slf4j
public class MyNettyClient {

    public static Map<WcsDeviceBaseInfo, ChannelFuture> channels = new HashMap<>();

    public static Map<WcsDeviceBaseInfo, ChannelFuture> channels1 = new HashMap<>();

    public static Map<WcsDeviceBaseInfo, ChannelFuture> channels2 = new HashMap<>();

    public static Map<WcsDeviceBaseInfo, ChannelFuture> channels3 = new HashMap<>();

    public static Map<WcsDeviceBaseInfo, ChannelFuture> channels4 = new HashMap<>();

    static EventLoopGroup eventLoopGroup = new NioEventLoopGroup();

    /**
     * 初始化Bootstrap
     */
    public static final Bootstrap getBootstrap(EventLoopGroup group) {
        if (null == group) {
            group = eventLoopGroup;
        }
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel socketChannel) throws Exception {
                        socketChannel
                                .pipeline()
                                .addLast(new FixedLengthFrameDecoder(11))
                                .addLast(new ElectricEncoder())
                                .addLast(new ElectricDecoder())
                                .addLast(new ElectricClientHandler());
                    }
                });
        return bootstrap;
    }

    /**
     * 初始化Bootstrap
     */
    public static final Bootstrap getBootstrap1(EventLoopGroup group) {
        if (null == group) {
            group = eventLoopGroup;
        }
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel socketChannel) throws Exception {
                        socketChannel
                                .pipeline()
                                .addLast(new FixedLengthFrameDecoder(13))
                                .addLast(new ElectricEncoder())
                                .addLast(new ElectricDecoder())
                                .addLast(new ElectricPhaseActivePowerClientHandler());
                    }
                });
        return bootstrap;
    }


    /**
     * 初始化Bootstrap
     */
    public static final Bootstrap getBootstrap2(EventLoopGroup group) {
        if (null == group) {
            group = eventLoopGroup;
        }
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel socketChannel) throws Exception {
                        socketChannel
                                .pipeline()
                                .addLast(new FixedLengthFrameDecoder(11))
                                .addLast(new ElectricEncoder())
                                .addLast(new ElectricDecoder())
                                .addLast(new ElectricPhaseCurrentClientHandler());
                    }
                });
        return bootstrap;
    }


    /**
     * 初始化Bootstrap
     */
    public static final Bootstrap getBootstrap3(EventLoopGroup group) {
        if (null == group) {
            group = eventLoopGroup;
        }
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel socketChannel) throws Exception {
                        socketChannel
                                .pipeline()
                                .addLast(new FixedLengthFrameDecoder(11))
                                .addLast(new ElectricEncoder())
                                .addLast(new ElectricDecoder())
                                .addLast(new ElectricPhaseVoltageClientHandler());
                    }
                });
        return bootstrap;
    }

    /**
     * 初始化Bootstrap
     */
    public static final Bootstrap getBootstrap4(EventLoopGroup group) {
        if (null == group) {
            group = eventLoopGroup;
        }
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel socketChannel) throws Exception {
                        socketChannel
                                .pipeline()
                                .addLast(new FixedLengthFrameDecoder(13))
                                .addLast(new ElectricEncoder())
                                .addLast(new ElectricDecoder())
                                .addLast(new ElectricReactivePowerClientHandler());
                    }
                });
        return bootstrap;
    }


    /**
     * 二次重连
     *
     * @param gateWayDevices
     * @return
     */
    public static final Map<WcsDeviceBaseInfo, ChannelFuture> twoGetChannel(List<WcsDeviceBaseInfo> gateWayDevices) {
        eventLoopGroup.shutdownGracefully();
        eventLoopGroup = new NioEventLoopGroup();
        return getChannel(gateWayDevices);
    }


    /**
     * 获取所有连接
     *
     * @param gateWayDevices 网关设备列表
     * @return
     */
    public static final Map<WcsDeviceBaseInfo, ChannelFuture> getChannel(List<WcsDeviceBaseInfo> gateWayDevices) {
        Map<WcsDeviceBaseInfo, ChannelFuture> result = new HashMap<>();
        Bootstrap bootstrap = getBootstrap(new NioEventLoopGroup());
        Bootstrap bootstrap1 = getBootstrap1(new NioEventLoopGroup());
        // Bootstrap bootstrap2 = getBootstrap2(null);
        // Bootstrap bootstrap3 = getBootstrap3(null);
        // Bootstrap bootstrap4 = getBootstrap4(null);
        for (WcsDeviceBaseInfo wcsDeviceBaseInfo : gateWayDevices) {
            String host = wcsDeviceBaseInfo.getDeviceIp();
            int port = wcsDeviceBaseInfo.getDevicePort().intValue();
            bootstrap.remoteAddress(host, port);
            bootstrap1.remoteAddress(host, port);
            // bootstrap2.remoteAddress(host, port);
            // bootstrap3.remoteAddress(host, port);
            // bootstrap4.remoteAddress(host, port);
            //异步连接tcp服务端
            ChannelFuture future = bootstrap.connect().addListener((ChannelFuture futureListener) -> {
                final EventLoop eventLoop = futureListener.channel().eventLoop();
                if (!futureListener.isSuccess()) {
                    log.info("与" + host + ":" + port + "连接失败!");
                } else {
                    channels.put(wcsDeviceBaseInfo, futureListener);
                }
            });
            ChannelFuture future1 = bootstrap1.connect().addListener((ChannelFuture futureListener) -> {
                final EventLoop eventLoop = futureListener.channel().eventLoop();
                if (!futureListener.isSuccess()) {
                    log.info("与" + host + ":" + port + "连接失败!");
                } else {
                    channels1.put(wcsDeviceBaseInfo, futureListener);
                }
            });
            // ChannelFuture future2 = bootstrap2.connect().addListener((ChannelFuture futureListener) -> {
            //     final EventLoop eventLoop = futureListener.channel().eventLoop();
            //     if (!futureListener.isSuccess()) {
            //         log.info("与" + host + ":" + port + "连接失败!");
            //     }
            // });
            // ChannelFuture future3 = bootstrap3.connect().addListener((ChannelFuture futureListener) -> {
            //     final EventLoop eventLoop = futureListener.channel().eventLoop();
            //     if (!futureListener.isSuccess()) {
            //         log.info("与" + host + ":" + port + "连接失败!");
            //     }
            // });
            // ChannelFuture future4 = bootstrap4.connect().addListener((ChannelFuture futureListener) -> {
            //     final EventLoop eventLoop = futureListener.channel().eventLoop();
            //     if (!futureListener.isSuccess()) {
            //         log.info("与" + host + ":" + port + "连接失败!");
            //     }
            // });


            // channels2.put(wcsDeviceBaseInfo, future2);
            // channels3.put(wcsDeviceBaseInfo, future3);
            // channels4.put(wcsDeviceBaseInfo, future4);
        }
        return result;
    }

    public static void main(String[] args) {
        try {

            //1.数据准备工作
            WcsDeviceBaseInfo gateWayDeviceBaseInfo = JSON.parseObject("{\"channelId\":\"\",\"createBy\":\"admin\",\"createTime\":\"2023-05-05 15:08:49\",\"deviceArea\":\"2\",\"deviceIp\":\"10.1.5.8\",\"deviceName\":\"网关4号\",\"deviceNo\":\"gateway04\",\"devicePort\":27,\"deviceType\":\"8\",\"enableStatus\":\"0\",\"id\":\"fa0f0fb380f249258c57e517a912edc4\",\"params\":{},\"updateBy\":\"admin\",\"updateTime\":\"2023-05-05 15:11:33\"}", WcsDeviceBaseInfo.class);
            List<WcsDeviceBaseInfo> powerDevices = JSON.parseArray("[{\"channelId\":\"\",\"createBy\":\"admin\",\"createTime\":\"2023-04-10 15:19:28\",\"deviceAddress\":\"01\",\"deviceArea\":\"0\",\"deviceName\":\"电表1号\",\"deviceNo\":\"db1\",\"deviceProducer\":\"jundu\",\"deviceType\":\"6\",\"enableStatus\":\"0\",\"id\":\"4af9de5ef1874b078cf2266f5e0d8ab2\",\"params\":{},\"updateBy\":\"admin\",\"updateTime\":\"2023-05-05 17:46:35\"}]", WcsDeviceBaseInfo.class);
            Map<WcsDeviceBaseInfo, List<WcsDeviceBaseInfo>> deviceGroupData = new HashMap<>();
            deviceGroupData.put(gateWayDeviceBaseInfo, powerDevices);
            List<WcsDeviceBaseInfo> gateWayDevices = new ArrayList<>(deviceGroupData.keySet());

            //2.初始化设备和channel的关联map
            MyNettyClient.getChannel(gateWayDevices);

            Thread.sleep(3000);
            deviceGroupData.forEach((gateWayDevice, powerDeviceList) -> {
                // Channel channel = MyNettyClient.channels.get(gateWayDevice).channel();
                Channel activeChannel = MyNettyClient.channels1.get(gateWayDevice).channel();
                // Channel channel2 = MyNettyClient.channels2.get(gateWayDevice).channel();
                // Channel channel3 = MyNettyClient.channels3.get(gateWayDevice).channel();
                // Channel reactiveChannel = MyNettyClient.channels4.get(gateWayDevice).channel();
                for (WcsDeviceBaseInfo powerDevice : powerDeviceList) {
                    if (StrUtil.isNotEmpty(powerDevice.getDeviceAddress())) {
                        //有功总电能
                        // ElectricReadVoltageReq electricReadVoltageReq = new ElectricReadVoltageReq(powerDevice.getDeviceAddress(), "03",
                        //         "00", "25",
                        //         "0003", "", "");
                        //
                        // channel.writeAndFlush(electricReadVoltageReq.toString());
                        //
                        // try {
                        //     Thread.sleep(1000);
                        // } catch (InterruptedException e) {
                        //     e.printStackTrace();
                        // }
                        // //有功功率
                        ElectricReadPhaseActivePowerReq electricReadPhaseActivePowerReq = new ElectricReadPhaseActivePowerReq(powerDevice.getDeviceAddress(), "03",
                                "00", "2E",
                                "0004", "", "");
                        activeChannel.writeAndFlush(electricReadPhaseActivePowerReq.toString());
                        //
                        // try {
                        //     Thread.sleep(500);
                        // } catch (InterruptedException e) {
                        //     e.printStackTrace();
                        // }
                        // //无功功率
                        // ElectricReadReactivePowerReq electricReadReactivePowerReq = new ElectricReadReactivePowerReq(powerDevice.getDeviceAddress(), "03",
                        //         "00", "32",
                        //         "0004", "", "");
                        // reactiveChannel.writeAndFlush(electricReadReactivePowerReq.toString());
                    } else {
                        log.error("电表设备地址码异常");
                    }
                }

            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
