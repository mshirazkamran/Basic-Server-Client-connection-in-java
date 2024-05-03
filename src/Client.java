import java.io.*;
import java.net.Socket;

public class Client {
    public static void main(String[] args) throws IOException {

        try {

            System.out.println("Client started!");
            // 127.0.0.1 is localhost
            Socket socket = new Socket("127.0.0.1", 14442);

            // System.in gives data in bytes, InputStreamReader wraps that data in
            // characters and BufferedReader helps in reading that data as a whole line
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

            // Prompting user for input
            System.out.println("Enter some data: ");
            String data = reader.readLine();

            // Making an output stream that writes data in Characters as opposed to
            // PrintStream which writes data in bytes
            PrintWriter dataFromClient = new PrintWriter(socket.getOutputStream(), true);
            dataFromClient.println(data);

            //Echo effect
            BufferedReader dataFromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            System.out.println(dataFromServer.readLine());

        } catch (IOException e) {

            e.printStackTrace();
            System.out.println("Client failed!");

        }
    }
}
