package Server;

import java.io.*;
import java.util.*;

public class SaveData {
    public static void main(String[] args) {
        System.out.println("Check");
    }
    public static void typeracer(String[][] data) {
        // Save data to a file
        try {
            File file = new File("userData/typeracer/leaderboard.txt");
            List<String> leaderboard = new ArrayList<>();
            if (file.exists()) {
                BufferedReader reader = new BufferedReader(new FileReader(file));
                String line;
                while ((line = reader.readLine()) != null) {
                    leaderboard.add(line);
                }
                reader.close();
            }

            for (String[] row : data) {
                if (row[0] != null) {
                    String name = row[0];
                    String progress = row[1];
                    String wpm = row[2];
                    String accuracy = row[3];
                    leaderboard.add(name + "," + progress + "," + wpm + "," + accuracy);
                }
            }

            Collections.sort(leaderboard);

            try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file))) {
                for (String entry : leaderboard) {
                    bufferedWriter.write(entry);
                    bufferedWriter.newLine();
                }
            }
        } catch (IOException e) {
        }
    }
}