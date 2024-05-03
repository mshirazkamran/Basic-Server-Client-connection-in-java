import java.io.*;
import java.net.Socket;
import java.net.ServerSocket;

public class Server {

    public static void main(String[] args) throws IOException {

        try {

            System.out.println("Waiting for connection...");

            ServerSocket serverSocket = new ServerSocket(14442);
            System.out.println(serverSocket.getLocalPort());

            // the control flow of program will stop on this
            // serverSocket.accept() method and not proceed until
            // a client connects to this IP address on the specified port
            Socket soc = serverSocket.accept();

            System.out.println("Connection established");

            // The prompt of client from Socket which sends data
            // from the output stream of that Socket is read by BufferedReader
            // and is stored in a variable
            BufferedReader clientData = new BufferedReader(new InputStreamReader(soc.getInputStream()));
            String inputLine = clientData.readLine();


            PrintWriter dataFromServer = new PrintWriter(soc.getOutputStream(), true);
            dataFromServer.println("Server says: " +  inputLine);




        } catch (IOException e) {

            e.printStackTrace();
            e.getMessage();

        }

    }

}
