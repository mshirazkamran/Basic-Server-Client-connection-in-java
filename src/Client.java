import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {

	private Socket socket;
	private BufferedReader bufferedReader;
	private BufferedWriter bufferedWriter;
	private String username;

	// Constructor
	public Client(Socket socket, String username) {
		try {
			this.socket = socket;
			this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			this.username = username;
		} catch (IOException e) {
			closeAll(socket, bufferedReader, bufferedWriter);
		}
	}


	private void sendMessage(String name) {
		try {
			// initial message sent(only the username is sent so that it can be shown that user has joined
			bufferedWriter.write(name);
			bufferedWriter.newLine();
			bufferedWriter.flush();
			System.out.println("Your name has been registered!");
		} catch (IOException e) {
			closeAll(socket, bufferedReader, bufferedWriter);
			System.out.println("Your name has not been registered!");
		}
	}

	// Client handler is waiting for this message in its constructor (line 20)
	private void sendMessage() {
		try {
			Scanner scan = new Scanner(System.in);

			while (socket.isConnected()) {
				String messageToSend = scan.nextLine();
				bufferedWriter.write(username + ": " + messageToSend);
				bufferedWriter.newLine();
				bufferedWriter.flush();
			}
		} catch (IOException e) {
			closeAll(socket, bufferedReader, bufferedWriter);
		}
	}


	public void listenForMessage() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				String messageReadFromGroup;
				while (socket.isConnected()) {
					try {
						messageReadFromGroup = bufferedReader.readLine();
						System.out.println(messageReadFromGroup);
					} catch (IOException e) {
						System.out.println(username + " has disconnected!");
						e.printStackTrace();
						e.getMessage();
						closeAll(socket, bufferedReader, bufferedWriter);
						break;
					}
				}
			}
		}).start(); // Reads the message from broadcastMessage method in ClientHandler class
	}

	public void closeAll(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {
		try {
			if (bufferedReader != null) {
				bufferedReader.close();
			}
			if (bufferedWriter != null) {
				bufferedWriter.close();
			}
			if (socket != null) {
				socket.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws IOException {
		Scanner scan = new Scanner(System.in);
		System.out.println("Enter your username for the group chat: ");
		String username = scan.nextLine();


		Socket clientSocket = new Socket("localhost", 12345);
		Client client = new Client(clientSocket, username);
		client.sendMessage(username);

		// Both these methods are blocking methods,so each ,method is run
		// on a separate thread so the process is concurrent (i.e. sending and
		// receiving messages)
		client.listenForMessage();
		client.sendMessage();

	}
}