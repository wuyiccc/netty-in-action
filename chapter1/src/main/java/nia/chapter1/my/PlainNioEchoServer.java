package nia.chapter1.my;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * @author wuyiccc
 * @date 2023/8/12 08:17
 */
public class PlainNioEchoServer {


    public static void main(String[] args) throws IOException {

        server(8081);
    }

    public static void server(int port) throws IOException {

        System.out.println("Listening for connections on port : " + port);

        ServerSocketChannel  serverChannel = ServerSocketChannel.open();
        ServerSocket ss = serverChannel.socket();
        InetSocketAddress address = new InetSocketAddress(port);
        // bind server to port
        ss.bind(address);
        serverChannel.configureBlocking(false);
        Selector selector = Selector.open();
        // register the channel with the selector to be interested in new client connections that get accepted
        serverChannel.register(selector, SelectionKey.OP_ACCEPT);

        while (true) {
            try {
                // block until something is selected
                selector.select();
            } catch (Exception ex) {
                ex.printStackTrace();
                break;
            }
            System.out.println("select...");

            // get all selectedKey instance
            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = selectionKeys.iterator();

            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();
                // remove the selectedKey from the iterator
                iterator.remove();
                try {
                    if (key.isAcceptable()) {
                        ServerSocketChannel server = (ServerSocketChannel) key.channel();
                        SocketChannel client = server.accept();
                        System.out.println("accepted connection from " + client);

                        client.configureBlocking(false);
                        // register connection to selector and set byteBuffer
                        client.register(selector, SelectionKey.OP_WRITE | SelectionKey.OP_READ, ByteBuffer.allocate(100));
                    } else if (key.isReadable()) {
                        SocketChannel client = (SocketChannel) key.channel();
                        ByteBuffer output = (ByteBuffer) key.attachment();
                        client.read(output);
                    } else if (key.isWritable()) {
                        SocketChannel client = (SocketChannel) key.channel();
                        ByteBuffer output = (ByteBuffer) key.attachment();
                        output.flip();
                        client.write(output);
                        output.compact();
                        System.out.println("test");
                    }
                } catch (IOException e) {
                    key.cancel();
                    try {
                        key.channel().close();
                    } catch (IOException cex) {

                    }
                }

            }

        }


    }
}
