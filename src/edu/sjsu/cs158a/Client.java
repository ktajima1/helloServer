package edu.sjsu.cs158a;
/* This program is from Ben Reed's GitHub for TCP protocol lesson:
 * https://github.com/breed/CS158A-SP23-class/blob/main/TCP/edu/sjsu/cs158a/Client.java */

import java.io.*;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class Client {
    public static void main(String[] args) throws IOException {
//        var s = new Socket("::1", 3333);
        var s = new Socket("127.0.0.1", 3333);

        var bis = new BufferedInputStream(s.getInputStream());
        var bos = new BufferedOutputStream(s.getOutputStream());
//        System.out.println("from server: " + bris.readLine());

        //The first byte of the message is the length of message, length needed to read entire message
        var len = bis.read();
        var bytes = bis.readNBytes(len);
        //Print out welcome message
        System.out.printf("%s\n", new String(bytes, 0, len));

        // Student ID
        var myid = 15537405;
        //Convert Student ID to bytes in big-endian format
        //The decimal ID is converted to bit-format and then shifted accordingly to its position.
        //The first byte (the MSB) is shifted to the right by 24 bits and then
        var myidBytes = new byte[4];
        myidBytes[0] = (byte)((myid >> 24) & 0xff);
        myidBytes[1] = (byte)((myid >> 16) & 0xff);
        myidBytes[2] = (byte)((myid >> 8) & 0xff);
        myidBytes[3] = (byte)((myid) & 0xff);
        //Full Name and convert to Bytes
        var name = "Potato";
        var nameBytes = name.getBytes();

        bos.write(myidBytes);
        bos.write(nameBytes.length);
        bos.write(nameBytes);
        bos.flush();

        /* These two lines were for when the server sent a string message where the first byte of the message indicated the length of the msg */
//        var len = bis.read();
//        var bytes = bis.readNBytes(len);
        var pin = bis.readNBytes(4);

        bos.write(pin);
        bos.flush();

        var leng = bis.read();
        var successMsg = bis.readNBytes(len);

        System.out.println(new String(successMsg, 0,leng));
//
//        var message = "goodbye";
//        var msgBytes = message.getBytes();
//        bos.write(msgBytes.length);
//        bos.write(msgBytes);
//        bos.flush();
//        //The reason why BufferReaders and BufferedWriters are fast is that they stage things in memory before actually doing them
//        //So here the BufferedWriter stores the hello message instead of sending it out, so we need to flush the BufferedWriter
//        bros.append("goodbye\n").flush();
//        System.out.println("sent goodbye");
    }
}