package nia.chapter5;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.nio.charset.StandardCharsets;

/**
 * @author wuyiccc
 * @date 2023/9/29 11:00
 */
public class Test {

    public static void main(String[] args) {


    }

    public static void test2() {
        ByteBuf buf = Unpooled.copiedBuffer("Netty in Action rocks!", StandardCharsets.UTF_8);
        printByte(buf);

        int readerIndex = buf.readerIndex();
        int writerIndex = buf.writerIndex();

        buf.writeByte((byte) '?');


        printByte(buf);

        System.out.println(readerIndex == buf.readerIndex());
        System.out.println(writerIndex != buf.writerIndex());
    }

    public static void test1() {
        ByteBuf buf = Unpooled.copiedBuffer("Netty in Action rocks!", StandardCharsets.UTF_8);
        ByteBuf sliced = buf.slice(0, 14);
        printByte(sliced);
        buf.setByte(0, (byte) 'J');
        printByte(sliced);
    }

    static void printByte(ByteBuf buf) {

        for (int i = 0; i < buf.capacity(); i++) {
            System.out.print((char) buf.getByte(i));
        }
        System.out.println();
    }
}
