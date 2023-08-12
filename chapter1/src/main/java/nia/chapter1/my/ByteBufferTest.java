package nia.chapter1.my;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

/**
 * @author wuyiccc
 * @date 2023/8/11 23:46
 */
public class ByteBufferTest {

    public static void main(String[] args) {

        Path filePath = Paths.get("/Users/wuxingyu/work/code_learn/001-java/57_nett_in_action/netty-in-action/chapter1/src/main/java/nia/chapter1/package-info.java");
        FileChannel inChannel = null;
        try {
            inChannel = FileChannel.open(filePath, StandardOpenOption.READ);
            ByteBuffer buf = ByteBuffer.allocate(48);
            int bytesRead;

            while ((bytesRead = inChannel.read(buf)) != -1) {
                buf.flip(); // Switch to reading mode

                while (buf.hasRemaining()) {
                    System.out.print((char) buf.get()); // Read and print each character
                }

                buf.clear(); // Clear the buffer for next read
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inChannel != null) {
                try {
                    inChannel.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
