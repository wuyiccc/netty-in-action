package nia.chapter2.myechoserver;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import nia.chapter2.echoserver.EchoServerHandler;

import java.net.InetSocketAddress;

/**
 * @author wuyiccc
 * @date 2023/8/12 19:42
 */
public class MyEchoClient {


    private final String host;

    private final int port;

    public MyEchoClient(String host, int port) {
        this.host = host;
        this.port = port;
    }


    public void start() throws Exception {

        EventLoopGroup group = new NioEventLoopGroup();

        try {
            // create bootstrap for client
            Bootstrap b = new Bootstrap();
            // specify EventLoopGroup to handle client events, NioEventLoopGroup is used, as the NIO-Transport should be used
            b.group(group)
                    // specify channel type, use correct one for NIO-Transport
                    .channel(NioSocketChannel.class)
                    // Set InetSocketAddress to which client connects
                    .remoteAddress(new InetSocketAddress(host, port))
                    // specify ChannelHandler, using ChannelInitializer, called once connection established
                    // and channel created
                    .handler(new ChannelInitializer<SocketChannel>() {

                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            // add EchoClientHandler to ChannelPipeline that belongs to channel
                            // ChannelPipeline holds all ChannelHandlers of channel
                            ch.pipeline().addLast(new MyEchoClientHandler());
                        }
                    });
            // Connect client to remote peer
            // wait until sync() competes connect completes
            ChannelFuture f = b.connect().sync();
            System.out.println("start client");
            // wait until ClientChannel closes. This will block
            f.channel().closeFuture().sync();
        } finally {
            // shut down bootstrap and thread pools, release all resources
            group.shutdownGracefully().sync();
        }
    }

    public static void main(String[] args) throws Exception {

        new MyEchoClient("localhost", 8081).start();
    }


}
