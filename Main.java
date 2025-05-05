import Maps.Map;
import Maps.Battlemap;
import Maps.Battlemechanic;
import Players.Character;
import Buildings.Warrior;
import Buildings.Tavern;
import Buildings.Stable;
import Buildings.Hospital;
import Buildings.Cafe; // Добавлен импорт кафе
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import Players.Bot;
import Buildings.Factory;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Leaderboard.showLeaderboard();
        System.out.println("Введите имя игрока:");
        String playerName = scanner.nextLine();
        // Проверка на наличие сохранения
        GameState gameState = null;
        if (SaveManager.hasSave(playerName)) {
            System.out.println("Обнаружено сохранение. Загрузить? (да/нет)");
            String choice = scanner.nextLine().toLowerCase();
            if (choice.equals("да")) {
                gameState = SaveManager.loadGame(playerName);
            }
        }
        int k = 0;
        Map gameMap;
        Character hero;
        Bot bot;
        List<Warrior> playerWarriors = new ArrayList<>();
        int gold = 1000;
        boolean firstBattleCompleted = false;
        BattleState battleState = null;
        int rating = 0;
        if (gameState != null) {
            // Загрузка сохранения
            gameMap = gameState.getGameMap();
            hero = gameState.getHero();
            bot = gameState.getBot();
            playerWarriors = gameState.getPlayerWarriors();
            gold = gameState.getGold();
            firstBattleCompleted = gameState.isFirstBattleCompleted();
            battleState = gameState.getBattleState();
            // Обновляем ссылки в зданиях
            Tavern tavern = new Tavern(new Battlemap(5, 5), gold, hero);
            Stable stable = new Stable(gold);
            Hospital hospital = new Hospital(gold);
            Cafe cafe = new Cafe(gold, gameMap, bot,gameMap.getPlayerCastleX(), gameMap.getPlayerCastleY()); // Инициализация кафе
            Factory factory = new Factory(gold, gameMap, bot);
            // Если загружаемся в состоянии битвы
            if (battleState != null) {
                handleLoadedBattle(gameMap, hero, playerWarriors, battleState);
                return; // Выходим после завершения загруженной битвы
            }
        } else {
            // Создание новой игры
            gameMap = new Map();
            hero = gameMap.getCharacter();
            bot = gameMap.getBot();
        }
        // Инициализация зданий
        Tavern tavern = new Tavern(new Battlemap(5, 5), gold, hero);
        Stable stable = new Stable(gold);
        Hospital hospital = new Hospital(gold);
        Cafe cafe = new Cafe(gold, gameMap, bot,gameMap.getPlayerCastleX(), gameMap.getPlayerCastleY());
        Factory factory = new Factory(gold, gameMap, bot);
        boolean gameOver = false;
        while (!gameOver) {
            gameMap.displayMap();
            // Проверка на победу (достижение замка противника)
            if (hero.getX() == gameMap.getEnemyCastleX() && hero.getY() == gameMap.getEnemyCastleY()) {
                handleFinalBattle(gameMap, hero, playerWarriors, playerName);
                gameOver = true;
                break;
            }
            // Ход игрока
            System.out.println("\nХод игрока:");
            System.out.println("Осталось энергии: " + hero.getEnergy());
            System.out.println("Введите команду (w, s, a, d, q, e, z, c, save):");
            String input = scanner.nextLine().toLowerCase();
            if (input.equals("save")) {
                SaveManager.saveGame(playerName, gameMap, hero, bot, playerWarriors, gold, firstBattleCompleted);
                System.out.println("Игра сохранена!");
                continue;
            }
            // Логика перемещения
            int steps = 1; // по умолчанию 1 шаг
            // Проверка, находится ли герой на дороге
            if ((gameMap.getCellType(hero.getX() + 1, hero.getY() + 1).equals("\uD83D\uDCC9")) ||
                    (gameMap.getCellType(hero.getX() - 1, hero.getY() - 1).equals("\uD83D\uDCC9"))) {
                if (hero.hasHorse()) {
                    System.out.println("Вы на дороге. Введите количество шагов (1, 2 или 3):");
                } else {
                    System.out.println("Вы на дороге. Введите количество шагов (1 или 2):");
                }
                int chosenSteps = scanner.nextInt();
                scanner.nextLine(); // Очищаем буфер после nextInt()
                if (hero.hasHorse() && (chosenSteps == 1 || chosenSteps == 2 || chosenSteps == 3)) {
                    steps = chosenSteps;
                } else if (!hero.hasHorse() && (chosenSteps == 1 || chosenSteps == 2)) {
                    steps = chosenSteps;
                } else {
                    System.out.println("Некорректный выбор. Перемещаемся на 1 шаг.");
                }
            } else if (gameMap.getCellType(hero.getX() + 1, hero.getY() + 1).equals("\uD83C\uDF33") ||
                    gameMap.getCellType(hero.getX() - 1, hero.getY() - 1).equals("\uD83C\uDF33")) { // Если в лесу
                if (hero.hasHorse()) {
                    System.out.println("Вы в лесу. Введите количество шагов (1 или 2):");
                    int chosenSteps = scanner.nextInt();
                    scanner.nextLine(); // Очищаем буфер после nextInt()
                    if (chosenSteps == 1 || chosenSteps == 2) {
                        steps = chosenSteps;
                    } else {
                        System.out.println("Некорректный выбор. Перемещаемся на 1 шаг.");
                    }
                } else {
                    steps = 1; // В лесу без лошади можно перемещаться только на 1 клетку
                }
            }
            boolean moved = false;
            switch (input) {
                case "w" -> moved = hero.move(-steps, 0, gameMap);
                case "s" -> moved = hero.move(steps, 0, gameMap);
                case "a" -> moved = hero.move(0, -steps, gameMap);
                case "d" -> moved = hero.move(0, steps, gameMap);
                case "q" -> moved = hero.move(-steps, -steps, gameMap);
                case "e" -> moved = hero.move(-steps, steps, gameMap);
                case "z" -> moved = hero.move(steps, -steps, gameMap);
                case "c" -> moved = hero.move(steps, steps, gameMap);
                default -> System.out.println("Некорректная команда!");
            }
            if(moved){
                rating++;
                SaveManager.saveGame(playerName, gameMap, hero, bot, playerWarriors, gold, firstBattleCompleted);
            }
            if (moved) {
                gameMap.updateMap(hero, bot); // Обновляем карту после перемещения
                SaveManager.saveGame(playerName, gameMap, hero, bot, playerWarriors, gold, firstBattleCompleted);
            }
            // Проверка на нахождение в замке
            if (hero.getX() == gameMap.getPlayerCastleX() && hero.getY() == gameMap.getPlayerCastleY()) {
                boolean inCastle = true;
                while (inCastle) {
                    System.out.println("Вы в замке. Куда пойдёте?");
                    System.out.println("1. Таверна");
                    System.out.println("2. Конюшня");
                    System.out.println("3. Госпиталь");
                    System.out.println("4. Кафе «У тёти Тишки»"); // Добавлен пункт кафе
                    System.out.println("5. НА ЗАВОД!!!");
                    System.out.println("6. Выйти");
                    int choice = scanner.nextInt();
                    scanner.nextLine();
                    switch (choice) {
                        case 1 -> tavern.interact(hero, playerWarriors);
                        case 2 -> stable.interact(hero, playerWarriors);
                        case 3 -> hospital.interact(hero, playerWarriors);
                        case 4 -> cafe.interact(hero, playerWarriors); // Вызов кафе
                        case 5 -> factory.interact(hero, playerWarriors);
                        case 6 -> inCastle = false;
                        default -> System.out.println("Некорректный выбор!");
                    }
                    // Обновляем золото в зданиях
                    gold = tavern.getGold();
                    stable.setGold(gold);
                    hospital.setGold(gold);
                    cafe.setGold(gold); // Обновление золота в кафе
                }
            }
            // Проверка встречи с ботом (первое сражение)
            if (!firstBattleCompleted && bot != null && hero.getX() == bot.getX() && hero.getY() == bot.getY()) {
                System.out.println("Да начнётся рубилово!");
                Battlemap battleMap = new Battlemap(5, 5);
                List<Warrior> enemyWarriors = new ArrayList<>();
                enemyWarriors.add(new Warrior("\uD83C\uDFF9", "Лучник", 3, 80, 99, 5, 40));
                battleMap.placeEnemyWarriors(enemyWarriors);
                // Размещение юнитов игрока
                for (int i = 0; i < playerWarriors.size(); i++) {
                    battleMap.placeWarrior(playerWarriors.get(i), i);
                }
                // Запуск битвы
                battleState = new BattleState(battleMap, playerWarriors, enemyWarriors);
                Battlemechanic battle = new Battlemechanic(battleMap, (ArrayList<Warrior>) playerWarriors, (ArrayList<Warrior>) enemyWarriors, hero);
                battle.startBattle();
                // После боя
                if (playerWarriors.isEmpty()) {
                    gameOver = true;
                } else {
                    gameMap.Removeunit();
                    gameMap.setBot(null); // Удаляем бота
                    firstBattleCompleted = true;
                    battleState = null; // Сброс состояния битвы
                    gameMap.displayMap();
                }
                // Автосохранение после боя
                SaveManager.saveGame(playerName, gameMap, hero, bot, playerWarriors, gold, firstBattleCompleted);
            }
            // Ход бота (если он ещё существует)
            if (bot != null && !firstBattleCompleted) {
                System.out.println("Ход бота:");
                bot.moveTowards(gameMap.getPlayerCastleX(), gameMap.getPlayerCastleY(), gameMap);
                gameMap.updateMap(hero, bot); // Обновляем карту после хода бота
                // Проверка захвата замка ботом
                if (bot.getX() == gameMap.getPlayerCastleX() && bot.getY() == gameMap.getPlayerCastleY()) {
                    System.out.println("Противник захватил ваш замок! Вы проиграли!");
                    gameOver = true;
                }
                // Проверка на встречу после хода бота
                if (hero.getX() == bot.getX() && hero.getY() == bot.getY()) {
                    System.out.println("Да начнётся рубилово!");
                    Battlemap battleMap = new Battlemap(5, 5);
                    List<Warrior> enemyWarriors = new ArrayList<>();
                    enemyWarriors.add(new Warrior("\uD83C\uDFF9", "Лучник", 3, 80, 99, 5, 40));
                    battleMap.placeEnemyWarriors(enemyWarriors);
                    // Размещение юнитов игрока
                    for (int i = 0; i < playerWarriors.size(); i++) {
                        battleMap.placeWarrior(playerWarriors.get(i), i);
                    }
                    // Запуск битвы
                    battleState = new BattleState(battleMap, playerWarriors, enemyWarriors);
                    Battlemechanic battle = new Battlemechanic(battleMap, (ArrayList<Warrior>) playerWarriors, (ArrayList<Warrior>) enemyWarriors, hero);
                    battle.startBattle();
                    // После боя
                    if (playerWarriors.isEmpty()) {
                        gameOver = true;
                    } else {
                        //gameMap.Removeunit(bot.getX(), bot.getY());
                        gameMap.updateMap(hero,bot);
                        gameMap.setBot(null); // Удаляем бота
                        firstBattleCompleted = true;
                        battleState = null; // Сброс состояния битвы
                    }
                    // Автосохранение после боя
                    SaveManager.saveGame(playerName, gameMap, hero, bot, playerWarriors, gold, firstBattleCompleted);
                }
                // Автосохранение после хода бота
                SaveManager.saveGame(playerName, gameMap, hero, bot, playerWarriors, gold, firstBattleCompleted);
            }
        }
        Leaderboard.addRating(playerName, rating);
        Leaderboard.showLeaderboard();
        scanner.close();
    }
    private static void handleFinalBattle(Map gameMap, Character hero, List<Warrior> playerWarriors, String playerName) {
        System.out.println("Вы достигли замка противника! Начинается финальное сражение!");
        Battlemap battleMap = new Battlemap(5, 5);
        ArrayList<Warrior> enemyWarriors = new ArrayList<>();
        for (int i = 0; i < 1; i++) {
            enemyWarriors.add(new Warrior("\uD83E\uDD3A", "Мечник", 4, 120, 50, 0, 80));
        }
        battleMap.placeEnemyWarriors(enemyWarriors);
        // Сброс позиций юнитов игрока
        for (Warrior warrior : playerWarriors) {
            battleMap.removeunit(warrior.getX(), warrior.getY());
        }
        for (int i = 0; i < playerWarriors.size(); i++) {
            Warrior warrior = playerWarriors.get(i);
            battleMap.placeWarrior(warrior, i);
            warrior.setPosition(i, 0);
        }
        // Запуск боя
        Battlemechanic battle = new Battlemechanic(battleMap, new ArrayList<>(playerWarriors), enemyWarriors, hero);
        battle.startBattle();
        // После боя
        if (playerWarriors.isEmpty()) {
            System.out.println("Поражение! Игра окончена.");
        } else {
            System.out.println("Победа! Вы захватили замок противника!");
        }
        // Удаляем сохранение после завершения игры
        SaveManager.deleteSave(playerName);
    }
    private static void handleLoadedBattle(Map gameMap, Character hero, List<Warrior> playerWarriors, BattleState battleState) {
        System.out.println("Продолжаем загруженную битву!");
        Battlemechanic battle = new Battlemechanic(
                battleState.getBattleMap(),
                new ArrayList<>(playerWarriors),
                (ArrayList<Warrior>) battleState.getEnemyWarriors(),
                hero
        );
        battle.startBattle();
        // После битвы
        if (playerWarriors.isEmpty()) {
            System.out.println("Поражение! Игра окончена.");
        } else {
            System.out.println("Битва завершена! Возвращаемся на карту.");
            gameMap.displayMap();
        }
    }
}





























/*import Maps.Map;
import Maps.Battlemap;
import Maps.Battlemechanic;
import Players.Character;
import Buildings.Warrior;
import Buildings.Tavern;
import Buildings.Stable;
import Buildings.Hospital;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import Players.Bot;


public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Leaderboard.showLeaderboard();
        System.out.println("Введите имя игрока:");
        String playerName = scanner.nextLine();

        // Проверка на наличие сохранения
        GameState gameState = null;
        if (SaveManager.hasSave(playerName)) {
            System.out.println("Обнаружено сохранение. Загрузить? (да/нет)");
            String choice = scanner.nextLine().toLowerCase();
            if (choice.equals("да")) {
                gameState = SaveManager.loadGame(playerName);
            }
        }
        int k = 0;
        Map gameMap;
        Character hero;
        Bot bot;
        List<Warrior> playerWarriors = new ArrayList<>();
        int gold = 1000;
        boolean firstBattleCompleted = false;
        BattleState battleState = null;
        int rating = 0;

        if (gameState != null) {
            // Загрузка сохранения
            gameMap = gameState.getGameMap();
            hero = gameState.getHero();
            bot = gameState.getBot();
            playerWarriors = gameState.getPlayerWarriors();
            gold = gameState.getGold();
            firstBattleCompleted = gameState.isFirstBattleCompleted();
            battleState = gameState.getBattleState();
            //rating = gameState.getRating();

            // Обновляем ссылки в зданиях
            Tavern tavern = new Tavern(new Battlemap(5, 5), gold, hero);
            Stable stable = new Stable(gold);
            Hospital hospital = new Hospital(gold);

            // Если загружаемся в состоянии битвы
            if (battleState != null) {
                handleLoadedBattle(gameMap, hero, playerWarriors, battleState);
                return; // Выходим после завершения загруженной битвы
            }

        } else {
            // Создание новой игры
            gameMap = new Map();
            hero = gameMap.getCharacter();
            bot = gameMap.getBot();
        }

        // Инициализация зданий
        Tavern tavern = new Tavern(new Battlemap(5, 5), gold, hero);
        Stable stable = new Stable(gold);
        Hospital hospital = new Hospital(gold);

        boolean gameOver = false;
        while (!gameOver) {
            gameMap.displayMap();

            // Проверка на победу (достижение замка противника)
            if (hero.getX() == gameMap.getEnemyCastleX() && hero.getY() == gameMap.getEnemyCastleY()) {
                handleFinalBattle(gameMap, hero, playerWarriors, playerName);
                gameOver = true;
                break;
            }


            // Ход игрока
            System.out.println("\nХод игрока:");
            System.out.println("Осталось энергии: " + hero.getEnergy());
            System.out.println("Введите команду (w, s, a, d, q, e, z, c, save):");
            String input = scanner.nextLine().toLowerCase();

            if (input.equals("save")) {
                SaveManager.saveGame(playerName, gameMap, hero, bot, playerWarriors, gold, firstBattleCompleted);
                System.out.println("Игра сохранена!");
                continue;
            }

            // Логика перемещения
            int steps = 1; // по умолчанию 1 шаг

            // Проверка, находится ли герой на дороге
            if ((gameMap.getCellType(hero.getX() + 1, hero.getY() + 1).equals("\uD83D\uDCC9")) ||
                    (gameMap.getCellType(hero.getX() - 1, hero.getY() - 1).equals("\uD83D\uDCC9"))) {
                if (hero.hasHorse()) {
                    System.out.println("Вы на дороге. Введите количество шагов (1, 2 или 3):");
                } else {
                    System.out.println("Вы на дороге. Введите количество шагов (1 или 2):");
                }
                int chosenSteps = scanner.nextInt();
                scanner.nextLine(); // Очищаем буфер после nextInt()
                if (hero.hasHorse() && (chosenSteps == 1 || chosenSteps == 2 || chosenSteps == 3)) {
                    steps = chosenSteps;
                } else if (!hero.hasHorse() && (chosenSteps == 1 || chosenSteps == 2)) {
                    steps = chosenSteps;
                } else {
                    System.out.println("Некорректный выбор. Перемещаемся на 1 шаг.");
                }
            } else if (gameMap.getCellType(hero.getX() + 1, hero.getY() + 1).equals("\uD83C\uDF33") ||
                    gameMap.getCellType(hero.getX() - 1, hero.getY() - 1).equals("\uD83C\uDF33")) { // Если в лесу
                if (hero.hasHorse()) {
                    System.out.println("Вы в лесу. Введите количество шагов (1 или 2):");
                    int chosenSteps = scanner.nextInt();
                    scanner.nextLine(); // Очищаем буфер после nextInt()
                    if (chosenSteps == 1 || chosenSteps == 2) {
                        steps = chosenSteps;
                    } else {
                        System.out.println("Некорректный выбор. Перемещаемся на 1 шаг.");
                    }
                } else {
                    steps = 1; // В лесу без лошади можно перемещаться только на 1 клетку
                }
            }
            boolean moved = false;
            switch (input) {
                case "w" -> moved = hero.move(-steps, 0, gameMap);
                case "s" -> moved = hero.move(steps, 0, gameMap);
                case "a" -> moved = hero.move(0, -steps, gameMap);
                case "d" -> moved = hero.move(0, steps, gameMap);
                case "q" -> moved = hero.move(-steps, -steps, gameMap);
                case "e" -> moved = hero.move(-steps, steps, gameMap);
                case "z" -> moved = hero.move(steps, -steps, gameMap);
                case "c" -> moved = hero.move(steps, steps, gameMap);
                default -> System.out.println("Некорректная команда!");
            }
            if(moved){
                rating++;
                SaveManager.saveGame(playerName, gameMap, hero, bot, playerWarriors, gold, firstBattleCompleted);
            }

            if (moved) {
                gameMap.updateMap(hero, bot); // Обновляем карту после перемещения
                SaveManager.saveGame(playerName, gameMap, hero, bot, playerWarriors, gold, firstBattleCompleted);
            }

            // Проверка на нахождение в замке
            if (hero.getX() == gameMap.getPlayerCastleX() && hero.getY() == gameMap.getPlayerCastleY()) {
                boolean inCastle = true;
                while (inCastle) {
                    System.out.println("Вы в замке. Куда пойдёте?");
                    System.out.println("1. Таверна\n2. Конюшня\n3. Госпиталь\n4. Выйти");
                    int choice = scanner.nextInt();
                    scanner.nextLine();

                    switch (choice) {
                        case 1 -> tavern.interact(hero, playerWarriors);
                        case 2 -> stable.interact(hero, playerWarriors);
                        case 3 -> hospital.interact(hero, playerWarriors);
                        case 4 -> inCastle = false;
                        default -> System.out.println("Некорректный выбор!");
                    }

                    // Обновляем золото в зданиях
                    gold = tavern.getGold();
                    stable.setGold(gold);
                    hospital.setGold(gold);

                    // Автосохранение после взаимодействия с зданиями
                    //SaveManager.saveGame(playerName, gameMap, hero, bot, playerWarriors, gold, firstBattleCompleted);
                }
            }

            // Проверка встречи с ботом (первое сражение)
            if (!firstBattleCompleted && bot != null && hero.getX() == bot.getX() && hero.getY() == bot.getY()) {
                System.out.println("Да начнётся рубилово!");
                Battlemap battleMap = new Battlemap(5, 5);
                List<Warrior> enemyWarriors = new ArrayList<>();
                enemyWarriors.add(new Warrior("\uD83C\uDFF9", "Лучник", 3, 80, 99, 5, 40));
                battleMap.placeEnemyWarriors(enemyWarriors);

                // Размещение юнитов игрока
                for (int i = 0; i < playerWarriors.size(); i++) {
                    battleMap.placeWarrior(playerWarriors.get(i), i);
                }

                // Запуск битвы
                battleState = new BattleState(battleMap, playerWarriors, enemyWarriors);
                Battlemechanic battle = new Battlemechanic(battleMap, (ArrayList<Warrior>) playerWarriors, (ArrayList<Warrior>) enemyWarriors, hero);
                battle.startBattle();

                // После боя
                if (playerWarriors.isEmpty()) {
                    gameOver = true;
                } else {
                    gameMap.Removeunit();
                    gameMap.setBot(null); // Удаляем бота
                    firstBattleCompleted = true;
                    battleState = null; // Сброс состояния битвы
                    gameMap.displayMap();
                }

                // Автосохранение после боя
                SaveManager.saveGame(playerName, gameMap, hero, bot, playerWarriors, gold, firstBattleCompleted);
            }

            // Ход бота (если он ещё существует)
            if (bot != null && !firstBattleCompleted) {
                System.out.println("Ход бота:");
                bot.moveTowards(gameMap.getPlayerCastleX(), gameMap.getPlayerCastleY(), gameMap);
                gameMap.updateMap(hero, bot); // Обновляем карту после хода бота

                // Проверка захвата замка ботом
                if (bot.getX() == gameMap.getPlayerCastleX() && bot.getY() == gameMap.getPlayerCastleY()) {
                    System.out.println("Противник захватил ваш замок! Вы проиграли!");
                    gameOver = true;
                }
                // Проверка на встречу после хода бота
                if (hero.getX() == bot.getX() && hero.getY() == bot.getY()) {
                    System.out.println("Да начнётся рубилово!");
                    Battlemap battleMap = new Battlemap(5, 5);
                    List<Warrior> enemyWarriors = new ArrayList<>();
                    enemyWarriors.add(new Warrior("\uD83C\uDFF9", "Лучник", 3, 80, 99, 5, 40));
                    battleMap.placeEnemyWarriors(enemyWarriors);

                    // Размещение юнитов игрока
                    for (int i = 0; i < playerWarriors.size(); i++) {
                        battleMap.placeWarrior(playerWarriors.get(i), i);
                    }

                    // Запуск битвы
                    battleState = new BattleState(battleMap, playerWarriors, enemyWarriors);
                    Battlemechanic battle = new Battlemechanic(battleMap, (ArrayList<Warrior>) playerWarriors, (ArrayList<Warrior>) enemyWarriors, hero);
                    battle.startBattle();

                    // После боя
                    if (playerWarriors.isEmpty()) {
                        gameOver = true;
                    } else {
                        //gameMap.Removeunit(bot.getX(), bot.getY());
                        gameMap.updateMap(hero,bot);
                        gameMap.setBot(null); // Удаляем бота

                        firstBattleCompleted = true;
                        battleState = null; // Сброс состояния битвы
                    }

                    // Автосохранение после боя
                    SaveManager.saveGame(playerName, gameMap, hero, bot, playerWarriors, gold, firstBattleCompleted);
                }



                // Автосохранение после хода бота
                SaveManager.saveGame(playerName, gameMap, hero, bot, playerWarriors, gold, firstBattleCompleted);
            }
        }
        Leaderboard.addRating(playerName, rating);
        Leaderboard.showLeaderboard();
        scanner.close();
    }

    private static void handleFinalBattle(Map gameMap, Character hero, List<Warrior> playerWarriors, String playerName) {
        System.out.println("Вы достигли замка противника! Начинается финальное сражение!");
        Battlemap battleMap = new Battlemap(5, 5);
        ArrayList<Warrior> enemyWarriors = new ArrayList<>();
        for (int i = 0; i < 1; i++) {
            enemyWarriors.add(new Warrior("\uD83E\uDD3A", "Мечник", 4, 120, 50, 0, 80));
        }
        battleMap.placeEnemyWarriors(enemyWarriors);

        // Сброс позиций юнитов игрока
        for (Warrior warrior : playerWarriors) {
            battleMap.removeunit(warrior.getX(), warrior.getY());
        }
        for (int i = 0; i < playerWarriors.size(); i++) {
            Warrior warrior = playerWarriors.get(i);
            battleMap.placeWarrior(warrior, i);
            warrior.setPosition(i, 0);
        }

        // Запуск боя
        Battlemechanic battle = new Battlemechanic(battleMap, new ArrayList<>(playerWarriors), enemyWarriors, hero);
        battle.startBattle();

        // После боя
        if (playerWarriors.isEmpty()) {
            System.out.println("Поражение! Игра окончена.");
        } else {
            System.out.println("Победа! Вы захватили замок противника!");

        }

        // Удаляем сохранение после завершения игры
        SaveManager.deleteSave(playerName);
    }

    private static void handleLoadedBattle(Map gameMap, Character hero, List<Warrior> playerWarriors, BattleState battleState) {
        System.out.println("Продолжаем загруженную битву!");
        Battlemechanic battle = new Battlemechanic(
                battleState.getBattleMap(),
                new ArrayList<>(playerWarriors),
                (ArrayList<Warrior>) battleState.getEnemyWarriors(),
                hero
        );
        battle.startBattle();

        // После битвы
        if (playerWarriors.isEmpty()) {
            System.out.println("Поражение! Игра окончена.");
        } else {
            System.out.println("Битва завершена! Возвращаемся на карту.");
            gameMap.displayMap();
        }

    }

}





























/*import Buildings.Hospital;
import Buildings.Stable;
import Buildings.Tavern;
import Maps.Battlemap;
import Maps.Battlemechanic;
import Maps.Map;
import Players.Bot;
import Players.Character;
import Buildings.Warrior;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;







public class Main {
    public static void main(String[] args) {

        Map gameMap = new Map();
        Battlemap gamebMap = new Battlemap(5, 5);
        Players.Character hero = gameMap.getCharacter();
        //gameMap.setCharacter(hero);
        Bot bot = gameMap.getBot(); // Начальная позиция бота
        //gameMap.setBot(bot);
        gameMap.displayMap();
        Scanner scanner = new Scanner(System.in);

        // Инициализация таверны, конюшни и госпиталя
        Tavern tavern = new Tavern(gamebMap,1000, hero);
        Stable stable = new Stable(tavern.getGold());
        Hospital hospital = new Hospital(tavern.getGold()); // Добавляем госпиталь

        List<Warrior> playerWarriors = new ArrayList<>(); // Используем интерфейс List
        boolean gameOver = false;
        boolean firstBattleCompleted = false; // Флаг завершения первого сражения

        while (!gameOver) {
            // Ход игрока
            System.out.println("\nХод игрока:");
            System.out.println("Осталось энергии: " + hero.getEnergy());

            // Запрашиваем ввод направления движения от игрока
            System.out.println("Введите команду для движения героя (вверх - w, вниз - s, влево - a, вправо - d, вверх-влево - q, вверх-вправо - e, вниз-влево - z, вниз-вправо - c):");
            String input = scanner.nextLine().toLowerCase();

            // Определяем, на сколько клеток перемещаться
            int steps = 1; // по умолчанию 1 шаг

            // Проверка, находится ли герой на дороге
            if ((gameMap.getCellType(hero.getX() + 1, hero.getY() + 1).equals("\uD83D\uDCC9")) ||
                    (gameMap.getCellType(hero.getX() - 1, hero.getY() - 1).equals("\uD83D\uDCC9"))) {
                if (hero.hasHorse()) {
                    System.out.println("Вы на дороге. Введите количество шагов (1, 2 или 3):");
                } else {
                    System.out.println("Вы на дороге. Введите количество шагов (1 или 2):");
                }
                int chosenSteps = scanner.nextInt();
                scanner.nextLine(); // Очищаем буфер после nextInt()
                if (hero.hasHorse() && (chosenSteps == 1 || chosenSteps == 2 || chosenSteps == 3)) {
                    steps = chosenSteps;
                } else if (!hero.hasHorse() && (chosenSteps == 1 || chosenSteps == 2)) {
                    steps = chosenSteps;
                } else {
                    System.out.println("Некорректный выбор. Перемещаемся на 1 шаг.");
                }
            } else if (gameMap.getCellType(hero.getX() + 1, hero.getY() + 1).equals("\uD83C\uDF33") ||
                    gameMap.getCellType(hero.getX() - 1, hero.getY() - 1).equals("\uD83C\uDF33")) { // Если в лесу
                if (hero.hasHorse()) {
                    System.out.println("Вы в лесу. Введите количество шагов (1 или 2):");
                    int chosenSteps = scanner.nextInt();
                    scanner.nextLine(); // Очищаем буфер после nextInt()
                    if (chosenSteps == 1 || chosenSteps == 2) {
                        steps = chosenSteps;
                    } else {
                        System.out.println("Некорректный выбор. Перемещаемся на 1 шаг.");
                    }
                } else {
                    steps = 1; // В лесу без лошади можно перемещаться только на 1 клетку
                }
            }

            // Перемещение героя в зависимости от ввода
            boolean moved = false;
            switch (input) {
                case "w": // Вверх
                    moved = hero.move(-steps, 0, gameMap);

                    break;
                case "s": // Вниз
                    moved = hero.move(steps, 0, gameMap);
                    break;
                case "a": // Влево
                    moved = hero.move(0, -steps, gameMap);
                    break;
                case "d": // Вправо
                    moved = hero.move(0, steps, gameMap);
                    break;
                case "q": // Вверх-влево
                    moved = hero.move(-steps, -steps, gameMap);
                    break;
                case "e": // Вверх-вправо
                    moved = hero.move(-steps, steps, gameMap);
                    break;
                case "z": // Вниз-влево
                    moved = hero.move(steps, -steps, gameMap);
                    break;
                case "c": // Вниз-вправо
                    moved = hero.move(steps, steps, gameMap);
                    break;
                default:
                    System.out.println("Неверная команда. Попробуйте снова.");
            }

            if (moved) {
                gameMap.displayMap();
                System.out.println("Осталось энергии: " + hero.getEnergy());
            }

            // Вход в замок (0, 0)
            if (hero.getX() == gameMap.getPlayerCastleX() && hero.getY() == gameMap.getPlayerCastleY()) {
                boolean inCastle = true; // Флаг, что игрок находится в замке
                while (inCastle) {
                    System.out.println("Вы в замке. Куда хотите пойти?");
                    System.out.println("1. В таверну");
                    System.out.println("2. В конюшню");
                    System.out.println("3. В госпиталь"); // Добавлен госпиталь
                    System.out.println("4. Выйти из замка");

                    int choice = scanner.nextInt();
                    scanner.nextLine();

                    switch (choice) {
                        case 1: // Таверна
                            tavern.interact(hero, playerWarriors);
                            break;
                        case 2: // Конюшня
                            stable.interact(hero, playerWarriors);
                            tavern.setGold(stable.getGold()); // Обновляем золото в таверне
                            break;
                        case 3: // Госпиталь
                            hospital.interact(hero, playerWarriors);
                            tavern.setGold(hospital.getGold()); // Обновляем золото в таверне
                            break;
                        case 4: // Выход из замка
                            System.out.println("Вы вышли из замка.");
                            inCastle = false; // Выходим из цикла
                            break;
                        default:
                            System.out.println("Некорректный выбор. Попробуйте снова.");
                    }
                }
            }

            // Проверка на встречу с ботом (первое сражение)
            if (!firstBattleCompleted && hero.getX() == bot.getX() && hero.getY() == bot.getY()) {
                System.out.println("Да начнётся рубилово!");

                // Создаём юнитов для противника (5 мечников)
                // Создаём юнитов для противника (5 мечников)
                List<Warrior> enemyWarriors = new ArrayList<>();
                for (int i = 0; i < 1; i++) { // Было 1, теперь 5
                    enemyWarriors.add(new Warrior("\uD83E\uDD3A", "Мечник", 4, 120, 50, 0, 80));
                }

                // Размещаем юнитов противника на карте
                gamebMap.placeEnemyWarriors(enemyWarriors);

                // Запускаем бой
                // Запускаем бой
                Battlemechanic battle = new Battlemechanic(gamebMap, new ArrayList<>(playerWarriors), new ArrayList<>(enemyWarriors), hero);
                battle.startBattle();

                // После первого сражения удаляем бота и возвращаемся на карту 10x10
                //gameMap.setBot(null); // Удаляем бота
                firstBattleCompleted = true; // Помечаем, что первое сражение завершено
                if(playerWarriors.isEmpty()){
                    gameOver = true;
                    break;
                }
                System.out.println("Возвращаемся на основную карту...");
                gameMap.displayMap();

            //поправить рейтинг
            //поправить удаление бота
            }

            // Захват родного замка
            if (bot.getX() == gameMap.getPlayerCastleX() && bot.getY() == gameMap.getPlayerCastleY()) {
                System.out.println("Противник достиг замка! Вы проиграли! Лошня куда ты полез");
                gameOver = true;
            }

            // После завершения боя проверяем наличие шатра


            // Проверка на достижение замка противника (9,9) для второго сражения
            if (firstBattleCompleted && hero.getX() == gameMap.getEnemyCastleX() && hero.getY() == gameMap.getEnemyCastleY()) {
                System.out.println("Вы достигли замка противника! Начинается финальное сражение!");
                gamebMap.displaybmap();

                // Сбрасываем позиции юнитов игрока в начало карты
                for (Warrior warrior : playerWarriors) {
                    gamebMap.removeunit(warrior.getX(), warrior.getY()); // Сбрасываем позиции
                }

                // Размещаем юнитов игрока в начале карты
                for (int i = 0; i < playerWarriors.size(); i++) {
                    Warrior warrior = playerWarriors.get(i);
                    gamebMap.placeWarrior(warrior, i);
                    warrior.setPosition(i, 0); // Первый столбец, строка i
                }

                // Создаём юнитов для противника (5 мечников)
                List<Warrior> enemyWarriors = new ArrayList<>();
                for (int i = 0; i < 1; i++) {
                    enemyWarriors.add(new Warrior("\uD83E\uDD3A", "Мечник", 4, 120, 50, 0, 80));
                }

                // Размещаем юнитов противника на карте
                gamebMap.placeEnemyWarriors(enemyWarriors);

                // Запускаем бой
                // Запускаем бой
                Battlemechanic battle = new Battlemechanic(gamebMap, new ArrayList<>(playerWarriors), new ArrayList<>(enemyWarriors), hero);
                battle.startBattle();

                // После второго сражения игра завершается
                gameOver = true;
            }

            // Ход бота (только если первое сражение ещё не завершено)
            if (!firstBattleCompleted) {
                System.out.println("\nХод бота:");
                if (bot.moveTowards(0, 0, gameMap)) {
                    gameMap.displayMap();
                }

                // Проверка на встречу после хода бота
                if (hero.getX() == bot.getX() && hero.getY() == bot.getY()) {
                    System.out.println("Да начнётся рубилово!");
                    gamebMap.displaybmap();

                    // Создаём юнитов для противника (5 мечников)
                    List<Warrior> enemyWarriors = new ArrayList<>();
                    for (int i = 0; i < 1; i++) {
                        enemyWarriors.add(new Warrior("\uD83E\uDD3A", "Мечник", 4, 120, 50, 0, 80));
                    }

                    // Размещаем юнитов противника на карте
                    gamebMap.placeEnemyWarriors(enemyWarriors);

                    // Запускаем бой
                    // Запускаем бой
                    Battlemechanic battle = new Battlemechanic(gamebMap, new ArrayList<>(playerWarriors), new ArrayList<>(enemyWarriors), hero);
                    battle.startBattle();

                    // После первого сражения удаляем бота и возвращаемся на карту 10x10
                    //gameMap.setBot(null); // Удаляем бота
                    firstBattleCompleted = true; // Помечаем, что первое сражение завершено
                    if(playerWarriors.isEmpty()){
                        gameOver = true;
                        break;
                    }
                    System.out.println("Возвращаемся на основную карту...");
                    gameMap.displayMap();
                }
            }
        }
        scanner.close();
    }
}*/


