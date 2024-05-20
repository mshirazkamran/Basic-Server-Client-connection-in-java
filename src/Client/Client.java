package Client;

import java.io.*;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

// classes in differnet folders
import Games.CountryGuess;
import Games.TicTacToe;
import Games.Typeracer;

public class Client {

	private Socket socket;
	private BufferedReader bufferedReader;
	private BufferedWriter bufferedWriter;
	private String username;
	private String messageToSend;
	// ANSI Colour code
	public static final String ANSI_GREEN = "\u001B[32m";
	public static final String ANSI_RESET = "\u001B[0m";


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
				messageToSend = scan.nextLine();
				// checks if user wants to play for games
				checkMessageForGames();
				bufferedWriter.write(username + ": " + messageToSend + ANSI_RESET + " " + getSystemDateAndTime() + ANSI_RESET);
				bufferedWriter.newLine();
				bufferedWriter.flush();
			}
		} catch (IOException e) {
			closeAll(socket, bufferedReader, bufferedWriter);
		}
	}

	private String getSystemDateAndTime() {
		// returns dateand time at that instant in format dd/MM HH:mm:ss
		LocalDateTime now = LocalDateTime.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM HH:mm:ss");
		return ANSI_GREEN + now.format(formatter);
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

	private void checkMessageForGames() {
		if (messageToSend.equalsIgnoreCase("play capital")) {
			runCaptalGuessingGame();
		}
		if (messageToSend.equalsIgnoreCase("play tictactoe")) {
			runTicTacToe();
		}if (messageToSend.equalsIgnoreCase("play typeracer")) {
			Typeracer.typeracer();
		}
	}

	private void runCaptalGuessingGame() {
		CountryGuess user = new CountryGuess(username);
		user.startGame();
	}

	private void runTicTacToe() {
		TicTacToe.startTicTacToe();
	}


	public static void main(String[] args) throws IOException {
		Scanner scan = new Scanner(System.in);
		System.out.println("Enter your username for the group chat: ");
		String username = scan.nextLine();

		Socket clientSocket = new Socket(fetchIP.recieveIP(), 12345);
		Client client = new Client(clientSocket, username);
		client.sendMessage(username);

		// Both these methods are blocking methods,so each method is run
		// on a separate thread so the process is concurrent (i.e. sending and
		// receiving messages)
		client.listenForMessage();
		client.sendMessage();
	}
}