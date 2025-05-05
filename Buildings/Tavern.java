package Buildings;

import Maps.Battlemap;
import Players.Character;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;


public class Tavern extends Building {
    private List<Warrior> availableWarriors;
    private Battlemap gamebMap;
    private int c = 0; // Индекс для размещения юнитов
    private Character hero;
    private Random random;

    public Tavern(Battlemap gamebMap, int gold, Character hero) {
        super(gold);
        this.gamebMap = gamebMap;
        this.availableWarriors = new ArrayList<>();
        this.hero = hero;
        this.random = new Random();

        createWarriors();
    }

    private void createWarriors() {
        availableWarriors.add(new Warrior("\uD83D\uDCCC", "Копьеносец", 1, 100, 999, 1, 999));
        availableWarriors.add(new Warrior("\uD83E\uDDD1\u200D\uD83E\uDDBD\u200D➡️\uFE0F", "Кавалерист", 2, 90, 999, 3, 50));
        availableWarriors.add(new Warrior("\uD83C\uDFF9", "Лучник", 3, 80, 99, 5, 40));
        availableWarriors.add(new Warrior("\uD83E\uDD3A", "Мечник", 4, 120, 99, 0, 80));
        availableWarriors.add(new Warrior("\uD83D\uDD79", "Пульт от ядерки", 5, 1000, 9999, 58, 1000));
    }

    public List<Warrior> getAvailableWarriors() {
        return availableWarriors;
    }


    @Override
    public void interact(Character hero, List<Warrior> playerWarriors) {
        Scanner scanner = new Scanner(System.in);
        int maxSlots = hero.hasHealingTent() ? 4 : 5; // Максимум 4 юнита, если есть шатёр
        // 50% шанс на мини-игру
        if (random.nextDouble() < 1) {
            System.out.println("Местный искатель приключений предлагает сыграть в Крестики-нолики777!");
            int bet = 100; // Ставка
            if (getGold() >= bet) {
                TicTacToeGame ttt = new TicTacToeGame(bet);
                int result = ttt.play();
                setGold(getGold() + result); // Обновляем золото
                System.out.println("Ваш баланс после игры: " + getGold());
            } else {
                System.out.println("Недостаточно золота для игры!");
            }
        }
        while (true) {
            showAvailableWarriors();
            System.out.println("Введите номер юнита для покупки или 0 для выхода:");
            int choice = scanner.nextInt();

            if (choice == 0) {
                System.out.println("Выход из меню покупки.");
                break;
            }

            if (choice < 1 || choice > availableWarriors.size()) {
                System.out.println("Некорректный выбор, попробуйте снова.");
                continue;
            }

            Warrior selectedWarrior = availableWarriors.get(choice - 1);
            if (selectedWarrior.getCost() <= getGold()) {
                if (c < maxSlots) { // Учитываем шатёр
                    gamebMap.placeWarrior(selectedWarrior, c);
                    setGold(getGold() - selectedWarrior.getCost());
                    playerWarriors.add(selectedWarrior);
                    c++;
                    System.out.println("Юнит " + selectedWarrior.getName() + " добавлен в армию!");
                    System.out.println("Оставшееся золото: " + getGold());
                } else {
                    System.out.println("Больше нет места для юнитов в казармах.");
                }
            } else {
                System.out.println("Недостаточно золота для покупки этого юнита.");
            }
        }
    }

    public void showAvailableWarriors() {
        int maxSlots = hero.hasHealingTent() ? 4 : 5;
        System.out.println("Вам доступно свободных казарм: " + (maxSlots - c));
        System.out.println("Доступные юниты в таверне:");

        for (int i = 0; i < availableWarriors.size(); i++) {
            Warrior warrior = availableWarriors.get(i);
            System.out.println((i + 1) + ". " + warrior.getName() + " (Стоимость: " + warrior.getCost() + ")");
        }
    }
}