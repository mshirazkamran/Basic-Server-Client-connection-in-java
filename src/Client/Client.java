package Client;

import Games.*;
import Utils.*;
import java.io.*;
import java.net.Socket;
import java.util.*;

// final makes unoverridable
public class Client {

	private Socket socket;
	private BufferedReader ReadServer;
	private BufferedWriter WriteServer;
	private String username;
	private String messageToSend;


	// Constructor
	public Client(Socket socket, String username) {
		try {
			this.socket = socket;
			this.ReadServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			this.WriteServer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			this.username = username;
		} catch (IOException e) {
			closeAll(socket, ReadServer, WriteServer);
		}
	}

	// initial message sent(only the username is sent so that it can be shown that user has joined
	private void sendMessage(String name) {
		try {
			WriteServer.write(name);
			WriteServer.newLine();
			WriteServer.flush();
			System.out.println("Your name has been registered!");
		} catch (IOException e) {
			closeAll(socket, ReadServer, WriteServer);
			System.out.println("Your name has not been registered!");
		}
	}


	private void sendMessage() {
		try {
			Scanner scan = new Scanner(System.in);
			while (socket.isConnected()) {
				messageToSend = scan.nextLine();
				// checks if user wants to play for games or exit
				if (messageToSend.equalsIgnoreCase("exit")) {
                    break;
				}

				checkMesasgeForLocalGames(messageToSend);


				HashMap<String, String> messageMap = new HashMap<>();
				messageMap.put("id", username);
				messageMap.put("type", "general");
				messageMap.put("payload", messageToSend);

				String serializedMessage = ParseMap.unparse(messageMap);
				
				/// write the message to the server
				WriteServer.write(serializedMessage);
				WriteServer.newLine();
				WriteServer.flush();
			}
		} catch (IOException e) {
			closeAll(socket, ReadServer, WriteServer);
		}
	}


	public void closeAll(Socket socket, BufferedReader ReadServer, BufferedWriter WriteServer) {
		try {
			if (ReadServer != null) {
				ReadServer.close();
			}
			if (WriteServer != null) {
				WriteServer.close();
			}
			if (socket != null) {
				socket.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void checkMesasgeForLocalGames(String message) throws IOException {

		if (message.equalsIgnoreCase("play capital")) {
			CapitalGuess user = new CapitalGuess(username);
			String result = user.startGame();
			WriteServer.write(result); // result goes to client handler line 73
			WriteServer.newLine();
			WriteServer.flush();

		} else if (message.equalsIgnoreCase("play tictactoe")) {
			TicTacToe.startTicTacToe();
		}
	}
	

    private  void handleIncomingMessages() {
        try {
            while (socket.isConnected()) {
                String message = ReadServer.readLine();
				// if (messageToSend.equalsIgnoreCase("play capital")) {
				// 	return;
				// }
                HashMap<String, String> parsedMessage = ParseMap.parse(message);
               
                String messageType = parsedMessage.get("type");
				if  (messageType == null) { 
					System.out.println(message);
				}else {
					switch (messageType) {
						case "typeracer":
							Typeracer.handleGame(parsedMessage,username ,  WriteServer);
							break;
						default:
							handleDefaultMessage(parsedMessage);
							break;
					}
				}
            }
        } catch (IOException e) {
			e.printStackTrace();
			e.getMessage();
			closeAll(socket, ReadServer, WriteServer);
        }
    }
    
    private  void handleDefaultMessage(HashMap<String, String> parsedMessage) {
        if (parsedMessage.containsKey("payload")) {
			if (parsedMessage.containsKey("id") && !parsedMessage.get("id").equals(username)) {
            System.out.println(parsedMessage.get("id") + " says " + parsedMessage.get("payload"));
			}
        } else {
            System.out.println("Something might have gone wrong. Data: " + parsedMessage);
        }
    }


	public static void startClient() throws Exception {

		Scanner scan = new Scanner(System.in);
		System.out.println("Enter your username for the group chat: ");
		String username = scan.nextLine();
		// adding the timename to user so that every username is unique
		long currentTimeMillis = System.currentTimeMillis();
		username = username + "_" + currentTimeMillis;

		String serverIP = fetchIP.recieveIP(); // fetching ipfrom server who is broadcasting ip
		Socket clientSocket = null;


		try {
			clientSocket = new Socket(serverIP, 12345);
		} catch (IOException e) {
			// If connection fails, prompt user to enter the IP address manually
			System.out.println("Failed to connect to the server at IP: " + serverIP);
			System.out.println("Please enter the IP address manually: ");
			serverIP = scan.nextLine();
			try {
				clientSocket = new Socket(serverIP, 12345);
			} catch (IOException ex) {
				System.out.println("Failed to connect to the server");
			}
		}

		Client client = new Client(clientSocket, username);
		client.sendMessage(username);

		// Both these methods are blocking methods,so each method is run
		// on a separate thread so the process is concurrent (i.e. sending and
		// receiving messages)
		new Thread( new Runnable () { // Anonymous Inner class
			@Override 
			public void run() {
				client.handleIncomingMessages();
			}
		}).start();

		client.sendMessage();
	}
}



// {
// 	type: "game/general",
// 	"id": "username",
// 	payload: "data/run",
// }