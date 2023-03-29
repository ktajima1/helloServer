package edu.sjsu.cs158a;
/* This program is from Ben Reed's GitHub for TCP protocol lesson:
* https://github.com/breed/CS158A-SP23-class/blob/main/TCP/edu/sjsu/cs158a/Server.java */

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    static void handleClient(Socket s) {
        try {
            System.out.println(s);
            var bris = new BufferedReader(new InputStreamReader(s.getInputStream()));
            var bros = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
            //The reason why BufferReaders and BufferedWriters are fast is that they stage things in memory before actually doing them
            //So here the BufferedWriter stores the hello message instead of sending it out, so we need to flush the BufferedWriter
            bros.append("Hello! welcome to server!\n").flush();
            System.out.println("Client says: " + bris.readLine());
        } catch (IOException e){
            e.printStackTrace();
        } finally {
            try {
                s.close();
            } catch (IOException e) {}
        }
    }
    public static void main(String[] args) throws IOException {
        try (var ss = new ServerSocket(3333)) {
            while (true) {
//                handleClient(ss.accept());
                final Socket s = ss.accept();
                new Thread(() -> {
                    handleClient(s);
                }).start();
            }
        }
    }
}