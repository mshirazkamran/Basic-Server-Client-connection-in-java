import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Server {

    ServerSocket ss = null;
    Socket socket = null;
    OutputStreamWriter os = null;
    InputStreamReader is = null;
    BufferedReader messageReader = null;
    BufferedWriter messageWriter = null;
    String serverMessage = null;

    public static void main(String[] args) {

        Server please = new Server();

        please.run();

    }

    public void run() {


        for (;;) {
            try {

                ss = new ServerSocket(8888);
                socket = ss.accept();

                os = new OutputStreamWriter(socket.getOutputStream());
                is = new InputStreamReader(socket.getInputStream());

                messageWriter = new BufferedWriter(os);
                messageReader = new BufferedReader(is);


                while (true) {

                    receiveMessage();
                    sendMessage();

                    if (exitMessage() == true) {
                        break;
                    }
                }

                socket.close();
                is.close();
                os.close();
                messageReader.close();
                messageWriter.close();


            } catch (IOException e) {
                throw new RuntimeException(e);
            }// catch block

        }// for (;;)

    }// methpd


    private void receiveMessage() {

		try {
			System.out.println("Client says: " + messageReader.readLine());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

    private void sendMessage() {

        Scanner scan = new Scanner(System.in);
        System.out.println("Enter a message to send to client: ");
        this.serverMessage = scan.nextLine();


        try {
            messageWriter.write(serverMessage);
            messageWriter.newLine();
            messageWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

    }

    private boolean exitMessage() {
        return this.serverMessage.equalsIgnoreCase("exit");
    }
}