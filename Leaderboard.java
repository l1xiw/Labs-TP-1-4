import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class Leaderboard {
    private static final String LEADERBOARD_FILE = "saves/rating.txt";
    private static Map<String, Integer> ratings = new HashMap<>();

    static {
        loadLeaderboard();
    }

    private static void loadLeaderboard() {
        try (BufferedReader reader = new BufferedReader(new FileReader(LEADERBOARD_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":");
                if (parts.length == 2) {
                    ratings.put(parts[0], Integer.parseInt(parts[1]));
                }
            }
        } catch (IOException e) {
            saveLeaderboard(); // Создаем файл, если его нет
        }
    }

    public static void saveLeaderboard() {
        try (FileWriter writer = new FileWriter(LEADERBOARD_FILE)) {
            for (Map.Entry<String, Integer> entry : ratings.entrySet()) {
                writer.write(entry.getKey() + ":" + entry.getValue() + "\n");
            }
        } catch (IOException e) {
            System.out.println("Ошибка сохранения рейтинга: " + e.getMessage());
        }
    }

    // Исправленный метод: рейтинг = 100 - очки, обновляется только если новый рейтинг выше
    public static void addRating(String playerName, int earnedPoints) {
        int newRating =  earnedPoints; // Вычисляем рейтинг
        Integer existingRating = ratings.get(playerName);

        // Обновляем только если новый рейтинг выше
        if (existingRating == null || newRating > existingRating) {
            ratings.put(playerName, newRating);
            saveLeaderboard();
        }
    }

    public static void showLeaderboard() {
        System.out.println("\n🏆 Таблица лидеров:");
        ratings.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .forEach(entry -> System.out.println(entry.getKey() + ": " + entry.getValue()));
    }
}