package nia.chapter2.myechoserver;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import nia.chapter2.echoserver.EchoServer;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

/**
 * @author wuyiccc
 * @date 2023/8/12 16:50
 */
public class MyEchoServer {

    private final int port;

    public MyEchoServer(int port) {
        this.port = port;
    }


    public void start() throws InterruptedException {
        EventLoopGroup group = new NioEventLoopGroup();
        // bootstraps the server
        ServerBootstrap b = new ServerBootstrap();
        // specifies nio transport, local socket address
        b.group(group)
                .channel(NioServerSocketChannel.class)
                .localAddress(new InetSocketAddress(port))
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        // add handler to channel pipeline
                        ch.pipeline().addLast(new MyEchoServerHandler());
                    }
                });

        // binds server, wait for server to close, and releases resources
        try {
            final ChannelFuture f = b.bind().sync();
            System.out.println(MyEchoServer.class.getName() + "started and listen on " + f.channel().localAddress());
            // test close netty server
//            new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    System.out.println("sleep....");
//                    try {
//                        TimeUnit.SECONDS.sleep(20);
//                    } catch (InterruptedException e) {
//                        throw new RuntimeException(e);
//                    }
//                    try {
//                        f.channel().close().sync();
//                    } catch (InterruptedException e) {
//                        throw new RuntimeException(e);
//                    }
//                    System.out.println("sleep end....");
//                }
//            }).start();
            System.out.println("closeFuture sync");
            f.channel().closeFuture().sync();
            System.out.println("closeFuture sync end");
        } finally {
            group.shutdownGracefully().sync();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        new MyEchoServer(8081).start();
    }
}
