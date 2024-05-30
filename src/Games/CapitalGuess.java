package Games;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class CapitalGuess {

	// make constructor in accordancce to the client name, if client name is Ahmad, di it like this
	// CountryGuess client1 = new CountryGuess("Ahmad");

	private static String clientName;
	private String input = "";
	private ArrayList<String> countries = null;
	private ArrayList<String> capitals = null;
	private int repeatGame = 0;
	private int secondsToWait = 0;
	private int correct = 0;
	private int difficulty = 0;
	private final Scanner scan = new Scanner(System.in);


	public CapitalGuess(String clientName) {
		CapitalGuess.clientName = clientName;
	}


	public String startGame() {

		readCountriesFromFile();
		setGameDifficulty();
		guessCountry();

		// Returns result so that server can send it to the client 
		// rather than sending it locally 
		return result();

	}


	public String result() {
		return clientName + ":  got " + correct + "/" + repeatGame + " points on " + getSystemDateAndTime();
	}


	private void readCountriesFromFile() {

		String countriesFile = "src/Games/capitals/countries.txt";
		String capitalsFile = "src/Games/capitals/capitals.txt";


		// 135 countries and capitals
		String countrriesarray[] = new String[135];
		String capitalssarray[] = new String[135];

		try(BufferedReader reader = new BufferedReader(new FileReader(countriesFile))) {
			String line = reader.readLine();
			countrriesarray = line.split(",");

		} catch (IOException e) {
			e.printStackTrace();
		}

		try(BufferedReader reader1 = new BufferedReader(new FileReader(capitalsFile))) {
			String line = reader1.readLine();
			capitalssarray = line.split(",");

		} catch (IOException e) {
			e.printStackTrace();
		}

		// converting to arraylist for better use and functionality
		countries = new ArrayList<>(Arrays.asList(countrriesarray));
		capitals = new ArrayList<>(Arrays.asList(capitalssarray));
	}


	private void setGameDifficulty() {

		System.out.println("Enter the difficulty level: ");
		System.out.println("\n1 for easy\n2 for medium\n3 for hard\n4 for ultra");
		input = scan.nextLine();
		while (true) {
			if (input.equals("1")) {
				secondsToWait = 9;   // 9 seconds to think of an answer
				difficulty = 50; // top 50 populous countries will be asked
				repeatGame = 3;  // game repeats 3 times
				break;
			} else if (input.equals("2")) {
				secondsToWait = 8;    // 8 seconds to think of an answer
				difficulty = 90;  // top 90 populous countries will be asked
				repeatGame = 4;   // game repeat 4 times
				break;
			} else if (input.equals("3")) {
				secondsToWait = 7;    // 7 seconds to think of an answer
				difficulty = 135; // top 135 populous countries will be asked
				repeatGame = 5;   // game repeat 5 times
				break;
			} else if (input.equals("4")) {
				secondsToWait = 5;    // 5 seconds to think of an answer
				difficulty = 135; // top 135 populous countries will be asked
				repeatGame = 6;   // game repeat 5 times
				break;
			} else {
				System.out.println("Invalid input, Enter again: ");
				input = scan.nextLine();
			}
		}
	}


	// displays a constantly decreasing live timer
	private void displayTimer(String str, int seconds) {
		String line = "";
		for (int i = seconds; i > 0; i--) {
			line = i + str;
			System.out.print(line);
			stopExecFor(1000);
			// Clear the line by moving the cursor to the beginning and clearing from there
			System.out.print("\r\033[K");
			System.out.flush();
		}
	}


	private String getSystemDateAndTime() {
		// Returns date and time at that instant in format dd/MM/yyyy HH:mm:ss
		LocalDateTime now = LocalDateTime.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
		return now.format(formatter);
	}


	private void stopExecFor(int ms) {
		try {
			Thread.sleep(ms);
		} catch (InterruptedException e) {
			System.out.println("Thread could not be made to sleep");
		}
	}

	// returns an index in relaltion to the difficulty of the game
	private int randomCountryGenerator() {
		return (int) Math.round(Math.random() * difficulty);
	}

	private void guessCountry() {

		int timesToRepeat =  this.repeatGame;
		for (int i = 0; i < timesToRepeat; i++) {

			int randomCountry = randomCountryGenerator();

			System.out.println(capitals.get(randomCountry));
			System.out.println("Guess the capital of: " + countries.get(randomCountry));

			// Displays the timer according to the difficulty
			displayTimer(" seconds to guess...", secondsToWait);

			// user has 9 seconds to type the capital else he gets a false
			String capitalGuessed = timedUserInput(9);

			// checks the country
			if (capitalGuessed.equalsIgnoreCase(capitals.get(randomCountry))) {
				correct++;
			}
		}
	}

	private String timedUserInput(int timeToWait) {

		System.out.println("You have " + timeToWait + " seconds to answer...");
		long startTime = System.currentTimeMillis(); // Get the start time

		// Wait for user input
		String userInput = scan.nextLine();

		long endTime = System.currentTimeMillis(); // Get the end time
		long elapsedTime = endTime - startTime; // Calculate elapsed time

		// seconds * 1000 = milliseconds (because System.currentTimeMillis()
		// gives result in milliseconds
		if (elapsedTime > (timeToWait * 1000)) {
			System.out.println("You failed to answer in " + timeToWait + " seconds");
			return "false";
		}
		return userInput;
	}
}