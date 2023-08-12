package nia.chapter1.my;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.concurrent.CountDownLatch;

/**
 * @author wuyiccc
 * @date 2023/8/12 09:58
 */
public class PlainNio2EchoServer {

    public static void main(String[] args) throws IOException {
        new PlainNio2EchoServer().serve(8081);

    }

    public void serve(int port) throws IOException {
        System.out.println("Listening for connections on port " + port);

        final AsynchronousServerSocketChannel serverChannel = AsynchronousServerSocketChannel.open();
        InetSocketAddress address = new InetSocketAddress(port);
        serverChannel.bind(address);
        final CountDownLatch latch = new CountDownLatch(1);
        serverChannel.accept(null, new CompletionHandler<AsynchronousSocketChannel, Object>() {
            @Override
            public void completed(AsynchronousSocketChannel channel, Object attachment) {
                System.out.println("yes do it serverChannel");
                // again accept new client connections
                serverChannel.accept(null, this);
                ByteBuffer buffer = ByteBuffer.allocate(100);
                // trigger a read operation on the channel, the given completionHandler will be notified once something was read
                channel.read(buffer, buffer, new EchoCompletionHandler(channel));
            }

            @Override
            public void failed(Throwable exc, Object attachment) {
                try {
                    // close the socket on error
                    serverChannel.close();
                } catch (IOException e) {
                    latch.countDown();
                }

            }
        });

        try {
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

    }

    private final class EchoCompletionHandler implements CompletionHandler<Integer, ByteBuffer> {

        private final AsynchronousSocketChannel channel;

        private EchoCompletionHandler(AsynchronousSocketChannel channel) {
            this.channel = channel;
        }

        @Override
        public void completed(Integer result, ByteBuffer buffer) {
            System.out.println("yes do it EchoCompletionHandler");
            buffer.flip(); // switch buffer to read mode
            // trigger a write operation on the channel, the given CompletionHandler will be notified one something was written
            channel.write(buffer, buffer, new CompletionHandler<Integer, ByteBuffer>() {
                @Override
                public void completed(Integer result, ByteBuffer buffer) {
                    System.out.println("yes do it EchoCompletionHandler inner class");
                    if (buffer.hasRemaining()) {
                        System.out.println("has remaining...");
                        // trigger again a write operation if something is left in the bytebuffer
                        channel.write(buffer, buffer, this);
                    } else {
                        buffer.compact(); // switch buffer to write mode
                        // trigger a read operation on the channel, the given CompletionHandler will be notified one something was read
                        channel.read(buffer, buffer, EchoCompletionHandler.this);
                    }
                }

                @Override
                public void failed(Throwable exc, ByteBuffer attachment) {
                    try {
                        channel.close();
                    } catch (IOException e) {

                    }
                }
            });
        }

        @Override
        public void failed(Throwable exc, ByteBuffer attachment) {
            try {
                channel.close();
            } catch (IOException e) {

            }
        }
    }
}
