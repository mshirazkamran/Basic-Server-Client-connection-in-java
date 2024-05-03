import java.io.*;
import java.net.Socket;

public class Client {
    public static void main(String[] args) throws IOException {

        try {

            System.out.println("Client started!");
            // 127.0.0.1 is localhost
            Socket client = new Socket("127.0.0.1", 8888);

        } catch (IOException e) {

            e.printStackTrace();
            System.out.println("Client failed!");

        }
    }
}
