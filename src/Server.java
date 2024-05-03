import java.io.*;
import java.net.Socket;
import java.net.ServerSocket;

public class Server {

    public static void main(String[] args) throws IOException {

        try {

            System.out.println("Waiting for connection...");

            ServerSocket serverSocket = new ServerSocket(8888);
            // the control flow of program will stop on this
            // serverSocket.accept() method and not proceed until
            // a clients connects to this IP address on the specified port
            Socket server = serverSocket.accept();

            System.out.println("Connection established");

        } catch (IOException e) {

            e.printStackTrace();
            e.getMessage();

        }

    }

}
