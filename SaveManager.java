
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import Maps.Map;
import Players.Character;
import Players.Bot;
//import Units.Warrior;
import Buildings.Warrior;
// Добавлен импорт GameState

public class SaveManager {
    private static final String SAVE_DIR = "saves/";

    // Сохранение игры
    public static void saveGame(String playerName, Map gameMap, Character hero, Bot bot,
                                List<Warrior> playerWarriors, int gold, boolean firstBattleCompleted) {
        try (FileWriter writer = new FileWriter(SAVE_DIR + playerName + ".save")) {
            // Сохраняем размер карты
            writer.write(gameMap.getXX() + "," + gameMap.getYY() + "\n");

            // Сохраняем типы клеток
            for (int x = 0; x < gameMap.getXX(); x++) {
                for (int y = 0; y < gameMap.getYY(); y++) {
                    writer.write(gameMap.getCellType(x, y) + ",");
                }
                writer.write("\n");
            }
            for (int x = 0; x < gameMap.getXX(); x++) {
                for (int y = 0; y < gameMap.getYY(); y++) {
                    writer.write(gameMap.getStockCellType(x, y) + ",");
                }
                writer.write("\n");
            }
            // Сохраняем героя
            writer.write(hero.getX() + "," + hero.getY() + ","
                    + hero.getEnergy() + ","
                    + hero.hasHorse() + ","
                    + hero.hasHealingTent() + "\n");

            // Сохраняем бота
            if (bot != null) {
                writer.write(bot.getX() + "," + bot.getY() + "\n");
            } else {
                writer.write("null\n");
            }

            // Сохраняем флаг первого сражения
            writer.write(firstBattleCompleted + "\n");

            // Сохраняем золото
            writer.write(gold + "\n");

            // Сохраняем юнитов
            writer.write(playerWarriors.size() + "\n");
            for (Warrior warrior : playerWarriors) {
                writer.write(warrior.toString() + "\n");
            }

        } catch (IOException e) {
            System.out.println("Ошибка сохранения: " + e.getMessage());
        }
    }

    // Загрузка игры
    public static GameState loadGame(String playerName) {
        try (BufferedReader reader = new BufferedReader(new FileReader(SAVE_DIR + playerName + ".save"))) {
            // Загрузка карты
            String[] mapSize = reader.readLine().split(",");
            int xx = Integer.parseInt(mapSize[0]);
            int yy = Integer.parseInt(mapSize[1]);
            String[][] cellTypes = new String[xx][yy];


            for (int x = 0; x < xx; x++) {
                String[] cells = reader.readLine().split(",");
                for (int y = 0; y < yy; y++) {
                    cellTypes[x][y] = cells[y].trim();
                }
            }
            String[][] stockCellTypes = new String[xx][yy];
            for(int x = 0; x<xx;x++){
                String[] cells = reader.readLine().split(",");
                for(int y = 0; y < yy; y++){
                    stockCellTypes[x][y]=cells[y].trim();
                }
            }

            Map gameMap = new Map(xx,yy, cellTypes, stockCellTypes);

            // Загрузка героя
            String[] heroData = reader.readLine().split(",");
            Character hero = new Character(
                    Integer.parseInt(heroData[0]),
                    Integer.parseInt(heroData[1])
            );
            hero.setEnergy(Integer.parseInt(heroData[2])); // Раскомментировано
            hero.setHasHorse(Boolean.parseBoolean(heroData[3]));
            hero.setHasHealingTent(Boolean.parseBoolean(heroData[4]));

            // Загрузка бота
            String botLine = reader.readLine();
            Bot bot = (botLine.equals("null")) ? null : new Bot(
                    Integer.parseInt(botLine.split(",")[0]),
                    Integer.parseInt(botLine.split(",")[1])
            );

            // Загрузка флага первого сражения
            boolean firstBattleCompleted = Boolean.parseBoolean(reader.readLine());

            // Загрузка золота
            int gold = Integer.parseInt(reader.readLine());

            // Загрузка юнитов
            int warriorCount = Integer.parseInt(reader.readLine());
            List<Warrior> playerWarriors = new ArrayList<>();
            for (int i = 0; i < warriorCount; i++) {
                String warriorData = reader.readLine();
                //playerWarriors.add(Warrior.fromString(warriorData)); // Раскомментировано
            }

            // Создаем объект состояния игры
            return new GameState(
                    new Map(xx, yy, cellTypes, stockCellTypes),
                    hero,
                    bot,
                    playerWarriors,
                    gold,
                    firstBattleCompleted
            );

        } catch (IOException e) {
            System.out.println("Ошибка загрузки: " + e.getMessage());
            return null;
        }
    }

    // Проверка наличия сохранения
    public static boolean hasSave(String playerName) {
        File file = new File(SAVE_DIR + playerName + ".save");
        return file.exists();
    }

    // Удаление сохранения
    public static void deleteSave(String playerName) {
        File file = new File(SAVE_DIR + playerName + ".save");
        if (file.exists()) {
            file.delete();
        }
    }
}