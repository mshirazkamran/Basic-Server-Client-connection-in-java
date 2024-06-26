package Games;

import Utils.*;
import java.io.*;
import java.util.*;

public class Typeracer {
	private static BufferedWriter out;
    private static final String[][] data = new String[4][4];
	private static Typeracer tr;
	private static final GUILeaderboard lb = new GUILeaderboard();

	public Typeracer(BufferedWriter WriteServer) {
		Typeracer.out = WriteServer;
	}

	public void typeracer_handleData(HashMap<String, String> parsedMessage) {
		String id = parsedMessage.get("id");
		for (int i = 0; i < data.length; i++) {
			boolean rowIsEmpty = data[i][0] == null;
			if (data[i][0] != null && id.equals(data[i][0])) {
				data[i][1] = parsedMessage.get("progress");
				data[i][2] = parsedMessage.get("wpm");
				data[i][3] = parsedMessage.get("accuracy");
				break;
			} else if (rowIsEmpty) {
				data[i][0] = id;
				data[i][1] = parsedMessage.get("progress");
				data[i][2] = parsedMessage.get("wpm");
				data[i][3] = parsedMessage.get("accuracy");
				break;
			}
		}
		lb.updateLeaderboardWithData(data);
	}

	private void printLeaderboard(HashMap<String, String> data) {
		String leaderboardS = data.get("leaderboard");
		List<String[]> leaderboard = new ArrayList<>();
	
		String[] rows = leaderboardS.split("-n-");
		for (String row : rows) {
			leaderboard.add(row.split(";"));
		}
		int userindex = Integer.parseInt(data.get("index"));
	
		// Print the table header
	System.out.printf("\033[1m%-35s %-20s %-20s %-20s\033[0m\n", "Username", "WPM", "Accuracy", "Score");
	System.out.println("\033[1m" + new String(new char[95]).replace("\0", "-") + "\033[0m");

	// Print the table rows
	for (int i = 0; i < leaderboard.size(); i++) {
		String[] d = leaderboard.get(i);
		if (i == userindex) {
			System.out.print("\033[0;32m");
			System.out.printf("%-35s %-20s %-20s %-20s", d[0], d[1], d[2],(d[3]) + "");
			System.out.println("\033[0m");

		} else {
			System.out.printf("%-35s %-20s %-20s %-20s\n", d[0], d[1], d[2], d[3]);
		}
		if (i < leaderboard.size() - 1) {
			System.out.println(new String(new char[95]).replace("\0", "-"));
		}
	}
	}
	
/// all in one handler
	public static void handleGame(HashMap<String, String>  msg ,String username , BufferedWriter WriteServer ) {
		if ("run".equals(msg.get("payload"))) {
			 tr = new Typeracer(WriteServer);
			 lb.initializeGUI();
			new Thread(() ->
			// Typeracer.typeracer(ReadServer , username)
			tr.typeracer(username)
			).start();
		}else if("data".equals(msg.get("payload"))){
			tr.typeracer_handleData(msg);
		}else if("leaderboard".equals(msg.get("payload"))){
			if (username.equals(msg.get("id"))){
				tr.printLeaderboard(msg);
			}
		}else {
            System.out.println("Game not typeracer or ID is null " + msg.get("id"));
        }
	}

	
	public  void typeracer(String id) {
		try {
			String command = "start powershell.exe -Command \"cd src/Games/typeracer ; python dependencies.py ; python Typeracer.py\"";
			// String command = "start powershell.exe -NoExit -Command \" python dependencies.py ; python Typeracer.py\"";

			ProcessBuilder pb = new ProcessBuilder("cmd.exe", "/c", command);
			Process p = pb.start();

			p.waitFor();

			String filePath = "src/Games/typeracer/convo.txt";
			boolean stop = false;
			Thread.sleep(5000);
			while (true) {
				if (stop) {
					p.destroy();
					p.destroyForcibly();
					break;
				}
				Thread.sleep(2000);
				try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
					String line;
					String wpm = "0";
					String percentile = "0";
					String end = "false";
					String accuracy = "100";
					String leaderboard = "false";
					while ((line = reader.readLine()) != null) {
						if (line.contains("wpm")) {
							wpm = line.split("=")[1];
						}
						if (line.contains("progress")) {
							percentile = line.split("=")[1];
						}
						if (line.contains("accuracy")) {
							accuracy = line.split("=")[1];
						}
						if (line.contains("leaderboard")) {
							leaderboard = line.split("=")[1];
						}
						if (line.equals("stop")) {
							stop = true;
							end = "true";
							break;
						}
					}
					if (!wpm.equals("0") || !percentile.equals("0")) {
						HashMap<String, String> smap = new HashMap<>();
						smap.put("type", "typeracer");
						smap.put("payload", "data");
						smap.put("wpm", wpm);
						smap.put("progress", percentile);
						smap.put("accuracy", accuracy);
						smap.put("saveData", leaderboard);
						smap.put("id" , id);
						smap.put("end", end);
						String ds = ParseMap.unparse(smap);
						out.write(ds);
						out.newLine();
						out.flush();
					}
				} catch (IOException e) {
					System.err.println("Error reading file: " + e.getMessage());
				}
			}
			lb.closeGUI();
			System.out.println("Game ended");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}