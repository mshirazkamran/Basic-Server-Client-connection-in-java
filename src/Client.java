import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {

	Socket socket = null;
	InputStreamReader isr = null;
	OutputStreamWriter osw = null;
	BufferedReader messageReader = null;
	BufferedWriter messageWriter = null;
	private String clientMessage;


	public static void main(String[] args) {

		Client user = new Client();

		user.run();


	}

	private void run()  {

		try {
			socket = new Socket("localhost", 8888);

			osw = new OutputStreamWriter(socket.getOutputStream());
			isr = new InputStreamReader(socket.getInputStream());

			messageWriter = new BufferedWriter(osw);
			messageReader = new BufferedReader(isr);

			while(true) {

				sendMessage();

				if (exitCondition() == true) {
					break;
				}

				receiveMessageFromServer();
			}


		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			try {
				if (socket != null)
					socket.close();
				if (osw != null)
					osw.close();
				if (isr != null)
					isr.close();
				if (messageReader != null)
					messageReader.close();
				if (messageWriter != null)
					messageWriter.close();
			} catch (IOException e) {
				e.printStackTrace();
			} // catch block

		} // finally block

	} // method

	private void sendMessage() {

		try {
			Scanner scan = new Scanner(System.in);

			System.out.println("Enter a message to send: ");
			clientMessage = scan.nextLine();

			messageWriter.write(clientMessage);
			messageWriter.newLine();
			messageWriter.flush();


		} catch (IOException e) {
			e.printStackTrace();
		}


	}

	private boolean exitCondition() {
		return this.clientMessage.equalsIgnoreCase(("bye"));
	}

	private void receiveMessageFromServer() {

		try {
			String serverMessage = messageReader.readLine();
			System.out.println("Server says: " + serverMessage);
		} catch (IOException e) {
			throw new RuntimeException(e);
		} // catch block

	} // method

} // class