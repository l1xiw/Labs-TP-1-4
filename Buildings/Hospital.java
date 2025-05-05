package Buildings;

import Players.Character;

import java.util.List;
import java.util.Scanner;

public class Hospital extends Building {
    private boolean tentPurchased = false; // Флаг, куплен ли шатёр

    public Hospital(int gold) {
        super(gold); // Передаём золото в родительский класс
    }

    @Override
    public void interact(Players.Character hero, List<Warrior> playerWarriors) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Добро пожаловать в госпиталь!");
        System.out.println("Здесь вы можете купить шатёр первой помощи за 300 монет.");
        System.out.println("1. Купить шатёр первой помощи");
        System.out.println("2. Выйти");

        int choice = scanner.nextInt();
        scanner.nextLine(); // Очищаем буфер

        if (choice == 1) {
            buyHealingTent(hero, playerWarriors);
        } else {
            System.out.println("Вы вышли из госпиталя.");
        }
    }

    private void buyHealingTent(Character hero, List<Warrior> playerWarriors) {
        if (tentPurchased) {
            System.out.println("У вас уже есть шатёр первой помощи!");
            return;
        }

        if (getGold() >= 300) {
            setGold(getGold() - 300);
            System.out.println("Вы купили шатёр первой помощи! Он будет лечить ваших юнитов после каждого хода.");
            tentPurchased = true;
            hero.setHasHealingTent(true); // Добавляем флаг, что у героя есть шатёр
        } else {
            System.out.println("Недостаточно золота для покупки шатра первой помощи.");
        }
    }
}