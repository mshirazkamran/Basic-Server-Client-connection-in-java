import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class ClientHandler implements Runnable {

	private static final ArrayList<ClientHandler> clientHandlers = new ArrayList<>();
	private Socket socket;
	private BufferedReader bufferedReader;
	private BufferedWriter bufferedWriter;
	private String clientUsername;


	public ClientHandler(Socket socket) {

		try {
			this.socket = socket;
			bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			this.clientUsername = bufferedReader.readLine();
			clientHandlers.add(this);
			broadcastMessageToClients("SERVER: " + clientUsername + " has entered the chat!");

		} catch (IOException e) {
			closeAll(socket, bufferedReader, bufferedWriter);
		}
	}


	@Override
	public void run() {

		String messageFromClients;

		while (socket.isConnected()) {
			try {
				messageFromClients = bufferedReader.readLine();
				broadcastMessageToClients(messageFromClients);
			} catch (IOException e){
				closeAll(socket, bufferedReader, bufferedWriter);
				break;
			}
		}
	}

	public void broadcastMessageToClients(String messageToSend) {
		for (ClientHandler client : clientHandlers) {
			try {
				if (client != this) {
					client.bufferedWriter.write(messageToSend + "\n");
					// client.bufferedWriter.newLine();
					client.bufferedWriter.flush();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}


	public void removeClientHandler() {
		clientHandlers.remove(this);
		broadcastMessageToClients("SERVER: " + clientUsername + " has left the chat!");
	}

	public void closeAll(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedwriter) {
		removeClientHandler();
		try {
			if (bufferedReader != null) {
				bufferedReader.close();
			}
			if (bufferedwriter != null) {
				bufferedwriter.close();
			}
			if (socket != null) {
				socket.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
