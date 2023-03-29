package edu.sjsu.cs158a;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Here {

    private static final ExecutorService threadPool = Executors.newCachedThreadPool();

    public static void main(String[] args) throws IOException {
        usageTest(args);
        String command = args[0];
        if(command.equals("server")) {
            int serverPort = Integer.parseInt(args[1]);
            try (var ss = new ServerSocket(serverPort)) {
//            ExecutorService threadPool = Executors.newCachedThreadPool();
                while (true) {
                    ServerThread newClient = new ServerThread(ss.accept());
                    threadPool.submit(newClient);
                }
            } catch(IOException e) {
                System.err.println("Trouble opening port: port may be invalid");
                e.printStackTrace();
            }
        } else if(command.equals("client")) {
            try {
                var sock = new Socket(args[1], Integer.parseInt(args[2]));
                var out = sock.getOutputStream();
                var in = sock.getInputStream();

                var len = in.read();
                var bytes = in.readNBytes(len);

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
                var name = "Keigo Tajima";
                var nameBytes = name.getBytes();
                out.write(myidBytes);
                out.write(nameBytes.length);
                out.write(nameBytes);
                var pin = in.readNBytes(4);
//            System.out.println(new String(pin));
                out.write(pin);
                var successMessage = in.readNBytes(in.read());
                System.out.println(new String(successMessage));
            } catch(Error e) {
                e.printStackTrace();
            }
        } else {
            System.err.println("Congratulations, you somehow bypassed the usageTest check and made it here.");
            System.exit(1);
        }
    }

    /* This method is for handling arguments passed into the program, dealing with issues like no arguments, illegal
     * arguments, etc. */
    public static void usageTest(String[] arguments) {
        String usageMsg = "\n\nServer Command Usage: Here server <port> <name>" +
                "\nClient Command Usage: Here client <hostname> <port>" +
                "\nNote: <hostname> specifies desired target host to connect to" +
                "\n\t  <name> must be wrapped in quotations when containing spaces" +
                "\nEx: Here server 2323 \"Timothy White\"";
        /*If no arguments are specified, print error and exit program*/
        if (arguments.length<3) {
            System.err.println("Missing required parameters" + usageMsg);
            System.exit(1);
        }
        /*If more than three arguments are passed, print error and exit program*/
        if (arguments.length > 3) {
            StringBuilder str = new StringBuilder();
            str.append("Unmatched arguments from index 3:");
            for(int i=3; i<arguments.length;i++) {
                str.append(" '").append(arguments[i]).append("',");
            }
            str.deleteCharAt(str.length()-1); //Delete the comma at the end before appending usageMsg
            str.append(usageMsg);
            System.err.println(str);
            System.exit(1);
        }
        /* First argument (the command) must be either "server" or "client" */
        if(!arguments[0].equals("server") && !arguments[0].equals("client")) {
            System.err.println("Unrecognized command: \"" + arguments[0] + "\"; Command must either be \"client\" or \"server\"" + usageMsg);
            System.exit(1);
        }
        /* Server command error handling:
         * <port> must be parsable and between 0 and 65535 */
        if (arguments[0].equals("server")) {
            try {
                int port = Integer.parseInt(arguments[1]);
                if(port<0 || port>65535) {throw new NumberFormatException();}
            } catch(NumberFormatException e) {
                System.err.println("\"" + arguments[1] + "\" is not a valid port value: port must be a number between 0 and 65535" + usageMsg);
                System.exit(1);
            }
        }
        /* Client command error handling:
         * <port> must be parsable and between 0 and 65535 */
        if (arguments[0].equals("client")) {
            try {
                int port = Integer.parseInt(arguments[2]);
                if(port<0 || port>65535) {throw new NumberFormatException();}
            } catch(NumberFormatException e) {
                System.err.println(arguments[2] + " is not a valid port value: port must be a number between 0 and 65535" + usageMsg);
                System.exit(1);
            }
        }
        /*No usage problems, proceed with the program*/
    }
}