package Buildings;

import Players.Character;

import java.util.List;
import java.util.Scanner;

public class Stable extends Building {

    private boolean horsePurchased = false; // Флаг, куплена ли лошадь

    public Stable(int gold) {
        super(gold); // Передаём золото в родительский класс
    }

    @Override
    public void interact(Players.Character hero, List<Warrior> playerWarriors) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Добро пожаловать в конюшню!");
        System.out.println("Здесь вы можете купить лошадь за 200 монет.");
        System.out.println("1. Купить лошадь");
        System.out.println("2. Выйти");

        int choice = scanner.nextInt();
        scanner.nextLine(); // Очищаем буфер

        if (choice == 1) {
            buyHorse(hero);
        } else {
            System.out.println("Вы вышли из конюшни.");
        }
    }

    public void buyHorse(Character hero) {
        if (horsePurchased) {
            System.out.println("У вас уже есть лошадь!");
            return;
        }

        if (getGold() >= 200) {
            setGold(getGold() - 200);
            hero.setHasHorse(true);
            horsePurchased = true;
            System.out.println("Вы купили лошадь! Теперь вы можете перемещаться быстрее.");
            System.out.println("Оставшееся золото: " + getGold());
        } else {
            System.out.println("Недостаточно золота для покупки лошади.");
        }
    }
}