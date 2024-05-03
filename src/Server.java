import java.io.*;
import java.net.Socket;
import java.net.ServerSocket;

public class Server {

    public static void main(String[] args) throws IOException {

        try {
            System.out.println("Waiting for connection...");
            ServerSocket serverSocket = new ServerSocket(8888);
            Socket server = serverSocket.accept();
            System.out.println("Connection established");

        } catch (IOException e) {

            e.printStackTrace();
            e.getMessage();

        }

    }

}
