package nia.chapter1.my;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author wuyiccc
 * @date 2023/8/11 23:05
 */
public class PlainEchoServer {

    public static void main(String[] args) throws Exception {

        serve(8080);
    }

    public static void serve(int port) throws Exception {

        final ServerSocket socket = new ServerSocket(port);

        try {
            while (true) {
                final Socket clientSocket = socket.accept();
                System.out.println("Accepted connection from " + clientSocket);

                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        try {
                            BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

                            PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true);
                            while (true) {
                                writer.println(reader.readLine());
                                writer.flush();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                            try {
                                clientSocket.close();
                            } catch (IOException ex) {
                                // ignore on close
                            }
                        }
                    }
                }).start();

            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
