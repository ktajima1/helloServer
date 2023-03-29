package edu.sjsu.cs158a;

import java.io.*;
import java.net.Socket;
import java.nio.ByteBuffer;

public class ServerThread implements Runnable {

    private final Socket sock;

    ServerThread(Socket sock) {
        this.sock = sock;
    }

    @Override
    public void run() {
        try {
            var bis = new BufferedInputStream(sock.getInputStream());
            var bos = new BufferedOutputStream(sock.getOutputStream());

            //Send welcome message to client
            sendMessage("welcome to CS 158A. send id and name, and i'll send a PIN which you must also send ", bos);

            //Receive client response containing client information
            byte[] num = bis.readNBytes(4);
            int ssid = ByteBuffer.wrap(num).getInt();
            int len = bis.read();
            var nameBytes = bis.readNBytes(len);
            String name = new String(nameBytes, 0,len);
            String socket = sock.getRemoteSocketAddress().toString().substring(1);

            //Print out response to server side console:
            System.out.println("contact from " + name + " ("+ssid+") at " + socket);

            /* Now server must send the pin to client and receive the pin back from client */
            int pinSent = sendPin(bos);
            int pinResponse = receivePin(bis);


            if(pinSent==pinResponse) {
                System.out.println("sent message to " + socket +" \"you are registered " + name + "\"");
                sendMessage("you are registered " + name, bos);
            }
            else {
                sendMessage("bad pin", bos);
            }

            bis.close();
            bos.close();
        } catch (IOException ignored){
//            e.printStackTrace();
        } finally {
            try {
                sock.close();
            } catch (IOException ignored) {}
        }
    }
    public static void sendMessage(String message, BufferedOutputStream bos) throws IOException {
        var messageBytes = message.getBytes();
        bos.write(messageBytes.length);
        bos.write(messageBytes);
        bos.flush();
    }
    public static int sendPin(BufferedOutputStream bos) throws IOException {
        int pinNum = (int) (Math.random() * 9000) + 1000;
        byte[] pinToSend = new byte[4];
        ByteBuffer bb = ByteBuffer.wrap(pinToSend);
        bb.putInt(pinNum);
        bos.write(pinToSend);
        bos.flush();
        return pinNum;
    }
    public static int receivePin(BufferedInputStream bis) throws IOException {
        var responsePin = bis.readNBytes(4);
        return ByteBuffer.wrap(responsePin).getInt();
    }
}
