package Buildings;

import Players.Character;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import Maps.Map;
import Players.Bot;

public class Cafe extends Building {
    private final int MAX_VISITORS = 12;
    private int currentVisitors = 0;
    private int queueLength = 0;
    private boolean queueInitialized = false; // Флаг инициализации очереди
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private final Map gameMap;
    private final Bot bot;
    private final int playerCastleX;
    private final int playerCastleY;

    public Cafe(int gold, Map gameMap, Bot bot, int playerCastleX, int playerCastleY) {
        super(gold);
        this.gameMap = gameMap;
        this.bot = bot;
        this.playerCastleX = playerCastleX;
        this.playerCastleY = playerCastleY;
    }

    @Override
    public synchronized void interact(Character hero, List<Warrior> warriors) {
        if (!queueInitialized) {
            // Инициализация очереди только один раз
            Random random = new Random();
            queueLength = random.nextInt(20) + 1; // Генерация очереди от 1 до 20
            queueInitialized = true; // Устанавливаем флаг
        }

        System.out.println("В очереди " + queueLength + " человек. Желаете подождать? (да/нет)");
        Scanner scanner = new Scanner(System.in);
        String choice = scanner.nextLine().toLowerCase();

        if (choice.equals("да")) {
            startQueueProcessing(hero, warriors);
        } else {
            System.out.println("Вы вышли из кафе.");
        }
    }

    private void startQueueProcessing(Character hero, List<Warrior> warriors) {
        scheduler.scheduleAtFixedRate(() -> {
            if (queueLength > 0) {
                queueLength--;
                System.out.println("Осталось ждать: " + queueLength + " человек.");

                // Каждые 3 обслуженных посетителя двигаем бота
                if (queueLength % 3 == 0) {
                    moveBotDiagonally();
                }

                if (queueLength == 0) {
                    serveCustomer(hero, warriors);
                    scheduler.shutdown();
                    queueInitialized = false; // Сбрасываем флаг после завершения очереди
                }
            }
        }, 0, 10, TimeUnit.SECONDS); // 10 секунд реального времени = 1 минута игрового времени
    }

    private synchronized void serveCustomer(Character hero, List<Warrior> warriors) {
        if (currentVisitors < MAX_VISITORS) {
            currentVisitors++;
            System.out.println("Выберите услугу:");
            System.out.println("1. Кофе (+20 энергии) - 50 золота");

            Scanner scanner = new Scanner(System.in);
            int option = scanner.nextInt();

            switch (option) {
                case 1:
                    if (getGold() >= 50) {
                        hero.setEnergy(hero.getEnergy() + 20);
                        setGold(getGold() - 50);
                        System.out.println("Выпили кофе! Энергия увеличена на 20.");
                    } else {
                        System.out.println("Недостаточно золота!");
                    }
                    break;

                default:
                    System.out.println("Неверный выбор!");
            }
            currentVisitors--;
        } else {
            System.out.println("Все столики заняты. Приходите позже.");
        }
    }

    private void moveBotDiagonally() {
        if (bot != null) {
            System.out.println("Бот приближается к замку!");

            // Перемещаем бота
            boolean moved = bot.moveTowards(playerCastleX, playerCastleY, gameMap);

            if (moved) {
                // Обновляем карту и отображаем изменения
                gameMap.updateMap(null, bot);
                gameMap.displayMap();
            }
        }
    }
}