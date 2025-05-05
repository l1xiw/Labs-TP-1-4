package Maps;

import java.util.List;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;
import Players.Character;
import Buildings.Warrior;


public class Battlemechanic {
    private Battlemap gamebMap; // Боевая карта
    private ArrayList<Warrior> playerWarriors; // Юниты игрока
    private ArrayList<Warrior> enemyWarriors;  // Юниты противника
    private boolean playerTurn; // Чей ход: true - игрок, false - противник
    private Random random;
    private Character hero; // Герой игрока

    public Battlemechanic(Battlemap gamebMap, ArrayList<Warrior> playerWarriors, ArrayList<Warrior> enemyWarriors, Character hero) {
        this.gamebMap = gamebMap;
        this.playerWarriors = playerWarriors;
        this.enemyWarriors = enemyWarriors;
        this.playerTurn = true; // Игрок ходит первым
        this.random = new Random();
        this.hero = hero;
    }

    // Основной цикл боя
    public void startBattle() {
        Scanner scanner = new Scanner(System.in);
        while (!playerWarriors.isEmpty() && !enemyWarriors.isEmpty()) {
            gamebMap.displaybmap(); // Отображаем карту только один раз за ход
            if (playerTurn) {
                System.out.println("\nВаш ход!");
                playerTurn(scanner);
            } else {
                System.out.println("\nХод противника!");
                enemyTurn();
            }
            playerTurn = !playerTurn; // Смена хода
        }

        if (playerWarriors.isEmpty()) {
            System.out.println("Поражение! Все ваши юниты погибли.");

        } else {
            System.out.println("Победа! Все вражеские юниты уничтожены.");

        }
    }

    // Ход игрока
    public void playerTurn(Scanner scanner) {
        System.out.println("Выберите юнита для хода:");
        for (int i = 0; i < playerWarriors.size(); i++) {
            Warrior warrior = playerWarriors.get(i);
            System.out.println((i + 1) + ". " + warrior.getName() + " (Здоровье: " + warrior.getHealth() + ")");
        }
        int choice = scanner.nextInt() - 1;

        if (choice < 0 || choice >= playerWarriors.size()) {
            System.out.println("Некорректный выбор. Пропуск хода.");
            return;
        }

        Warrior selectedWarrior = playerWarriors.get(choice);
        System.out.println("Выбран юнит: " + selectedWarrior.getName());

        System.out.print("Выберите действие: 1 - Перемещение 2 - Атака ");
        if (hero.hasHealingTent()){
            System.out.println("3 - захиллиться");
        }
        int action = scanner.nextInt();

        if (action == 1) {
            moveWarrior(selectedWarrior, scanner);
        } else if (action == 2) {
            attackEnemy(selectedWarrior, scanner);
        } else if (action == 3) {
            healWithTent(selectedWarrior);
        } else {
            System.out.println("Некорректное действие. Пропуск хода.");
        }
    }


    // Лечение с помощью шатра первой помощи
    public void healWithTent(Warrior warrior) {
        if (hero.hasHealingTent()) {
            if(warrior.getHealth()<0){
                System.out.println("Как у джина, мёртвых не воскрешаем");
            }
            else{
                int healedAmount = Math.min(20, warrior.getMaxHealth() - warrior.getHealth());
                warrior.heal(healedAmount);
                System.out.println(warrior.getName() + " восстановил " + healedAmount + " здоровья с помощью шатра первой помощи.");
            }
        } else {
            System.out.println("У вас нет шатра первой помощи!");
        }
    }

    // Перемещение юнита игрока
    public void moveWarrior(Warrior warrior, Scanner scanner) {
        System.out.println("Введите направление (вверх - w, вниз - s, влево - a, вправо - d):");
        String direction = scanner.next().toLowerCase();

        int dx = 0, dy = 0;
        switch (direction) {
            case "w":
                dx = -1;
                break;
            case "s":
                dx = 1;
                break;
            case "a":
                dy = -1;
                break;
            case "d":
                dy = 1;
                break;
            default:
                System.out.println("Некорректное направление. Пропуск хода.");
                return;
        }

        // Проверка на возможность перемещения
        if (isMoveValid(warrior, dx, dy)) {
            System.out.println(warrior.getName() + " перемещается.");
            gamebMap.updateWarriorPosition(warrior, dx, dy);
        } else {
            System.out.println("Невозможно переместиться в этом направлении.");
        }
    }

    // Метод для получения списка юнитов игрока
    public List<Warrior> getPlayerWarriors() {
        return playerWarriors;
    }

    // Метод для получения списка юнитов противника
    public List<Warrior> getEnemyWarriors() {
        return enemyWarriors;
    }

    // Метод для проверки чьё это ход
    public boolean isPlayerTurn() {
        return playerTurn; // Предполагается, что переменная playerTurn определена в классе
    }

    // Атака врага
    public void attackEnemy(Warrior attacker, Scanner scanner) {
        System.out.println("Выберите врага для атаки:");
        Warrior yo = attacker;
        for (int i = 0; i < enemyWarriors.size(); i++) {
            Warrior enemy = enemyWarriors.get(i);
            if (enemy.getY() - 1 == yo.getY() && enemy.getX() == yo.getX()) {
                System.out.println((i + 1) + ". " + enemy.getName() + " (Здоровье: " + enemy.getHealth() + ")");
            }
        }
        int targetIndex = scanner.nextInt() - 1;

        if (targetIndex < 0 || targetIndex >= enemyWarriors.size()) {
            System.out.println("Некорректный выбор. Пропуск хода.");
            return;
        }

        Warrior target = enemyWarriors.get(targetIndex);
        boolean isAlive = attacker.attack(target);

        if (!isAlive) {
            System.out.println(target.getName() + " погиб!");
            enemyWarriors.remove(target);
            gamebMap.removeunit(target.getX(), target.getY());
        }
    }

    // Ход противника
    public void enemyTurn() {
        for (Warrior enemy : enemyWarriors) {
            // Проверяем левую клетку
            int targetX = enemy.getX();
            int targetY = enemy.getY() - 1; // Левая клетка

            // Ищем юнита игрока на левой клетке
            Warrior target = null;
            for (Warrior playerWarrior : playerWarriors) {
                if (playerWarrior.getX() == targetX && playerWarrior.getY() == targetY) {
                    target = playerWarrior;
                    break;
                }
            }

            if (target != null) {
                // Если на левой клетке есть юнит игрока, атакуем
                System.out.println(enemy.getName() + " атакует " + target.getName() + "!");
                boolean isAlive = enemy.attack(target);
                if (!isAlive) {
                    System.out.println(target.getName() + " погиб!");
                    playerWarriors.remove(target);
                    gamebMap.removeunit(target.getX(), target.getY());
                }
                return; // Завершаем ход, так как атака уже произошла
            }
        }

        // Если никто не может атаковать слева, выбираем случайного юнита противника
        Warrior enemy = enemyWarriors.get(random.nextInt(enemyWarriors.size()));
        System.out.println("Противник выбирает юнита: " + enemy.getName());

        // Находим ближайшего юнита игрока
        Warrior target = findNearestPlayerWarrior(enemy);

        if (target == null) {
            System.out.println("Нет доступных целей для атаки.");
            return;
        }

        // Определяем направление движения к цели
        int dx = Integer.compare(target.getX(), enemy.getX());
        int dy = Integer.compare(target.getY(), enemy.getY());

        // Если противник уже на одной клетке с целью, атакуем
        if (enemy.getX() == target.getX() && enemy.getY() == target.getY()) {
            System.out.println(enemy.getName() + " атакует " + target.getName() + "!");
            boolean isAlive = enemy.attack(target);
            if (!isAlive) {
                System.out.println(target.getName() + " погиб!");
                playerWarriors.remove(target);
                gamebMap.removeunit(target.getX(), target.getY());
            }
        } else {
            // Если не на одной клетке, двигаемся к цели
            System.out.println(enemy.getName() + " двигается к " + target.getName() + ".");
            if (isMoveValid(enemy, dx, dy)) {
                gamebMap.updateWarriorPosition(enemy, dx, dy);
            } else {
                System.out.println("Невозможно переместиться в этом направлении.");
            }
        }
    }

    // Находит ближайшего юнита игрока
    private Warrior findNearestPlayerWarrior(Warrior enemy) {
        Warrior nearest = null;
        double minDistance = Double.MAX_VALUE;

        for (Warrior playerWarrior : playerWarriors) {
            double distance = Math.sqrt(Math.pow(playerWarrior.getX() - enemy.getX(), 2) +
                    Math.pow(playerWarrior.getY() - enemy.getY(), 2));
            if (distance < minDistance) {
                minDistance = distance;
                nearest = playerWarrior;
            }
        }

        return nearest;
    }

    // Проверка на возможность перемещения
    public boolean isMoveValid(Warrior warrior, int dx, int dy) {
        int newX = warrior.getX() + dx;
        int newY = warrior.getY() + dy;

        // Проверяем, что новые координаты находятся в пределах карты
        if (newX >= 0 && newX < gamebMap.getBX() && newY >= 0 && newY < gamebMap.getBY()) {
            // Проверяем, что клетка свободна
            return gamebMap.getCell(newX, newY).getCelltype().equals("   ");
        }
        return false;
    }
}