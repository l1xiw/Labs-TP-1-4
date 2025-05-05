package Maps;

import org.testng.annotations.Test;
import static org.testng.AssertJUnit.*;
import Maps.Battlemap;
import Maps.Battlemechanic;
import Players.Character;
import Buildings.Warrior;

import java.util.ArrayList;

public class BattlemechanicTest {

    @Test
    public void testBattleInitialization() {
        // Создаем боевую карту размером 5x5
        Battlemap battlemap = new Battlemap(5, 5);

        // Создаем героя
        Character hero = new Character(0, 0);
        assertFalse(hero.hasHealingTent()); // Убедимся, что шатёр отсутствует

        // Создаем юнитов игрока и противника
        ArrayList<Warrior> playerWarriors = new ArrayList<>();
        playerWarriors.add(new Warrior("\uD83E\uDD3A", "Мечник", 4, 120, 50, 0, 80));
        ArrayList<Warrior> enemyWarriors = new ArrayList<>();
        enemyWarriors.add(new Warrior("\uD83C\uDFF9", "Лучник", 3, 80, 99, 5, 40));

        // Размещаем юнитов на карте
        battlemap.placeWarrior(playerWarriors.get(0), 0); // Игрок в первой строке первого столбца
        battlemap.placeEnemyWarriors(enemyWarriors); // Противник в последнем столбце

        // Создаем механику боя
        Battlemechanic battlemechanic = new Battlemechanic(battlemap, playerWarriors, enemyWarriors, hero);

        // Проверяем инициализацию
        assertNotNull(battlemechanic);
        assertEquals(1, battlemechanic.getPlayerWarriors().size());
        assertEquals(1, battlemechanic.getEnemyWarriors().size());
        assertTrue(battlemechanic.isPlayerTurn()); // Первый ход должен быть за игроком
    }


    @Test
    public void testPlayerAttack() {
        // Создаем боевую карту размером 5x5
        Battlemap battlemap = new Battlemap(5, 5);

        // Создаем героя
        Character hero = new Character(0, 0);

        // Создаем юнитов игрока и противника
        ArrayList<Warrior> playerWarriors = new ArrayList<>();
        Warrior playerWarrior = new Warrior("\uD83E\uDD3A", "Мечник", 4, 120, 50, 0, 80);
        playerWarriors.add(playerWarrior);

        ArrayList<Warrior> enemyWarriors = new ArrayList<>();
        Warrior enemyWarrior = new Warrior("\uD83C\uDFF9", "Лучник", 3, 80, 99, 5, 40);
        enemyWarriors.add(enemyWarrior);

        // Размещаем юнитов на карте
        battlemap.placeWarrior(playerWarrior, 0); // Игрок в первой строке первого столбца
        battlemap.placeEnemyWarriors(enemyWarriors); // Противник в последнем столбце

        // Сдвигаем врага ближе к игроку
        battlemap.updateWarriorPosition(enemyWarrior, -1, -1);

        // Создаем механику боя
        Battlemechanic battlemechanic = new Battlemechanic(battlemap, playerWarriors, enemyWarriors, hero);

        // Атакуем врага
        playerWarrior.attack(enemyWarrior);

        // Проверяем здоровье врага
        assertTrue(enemyWarrior.getHealth() < 99); // Здоровье должно уменьшиться
    }

    @Test
    public void testEnemyTurn() {
        // Создаем боевую карту размером 5x5
        Battlemap battlemap = new Battlemap(5, 5);

        // Создаем героя
        Character hero = new Character(0, 0);

        // Создаем юнитов игрока и противника
        ArrayList<Warrior> playerWarriors = new ArrayList<>();
        Warrior playerWarrior = new Warrior("\uD83E\uDD3A", "Мечник", 4, 120, 50, 0, 80);
        playerWarriors.add(playerWarrior);

        ArrayList<Warrior> enemyWarriors = new ArrayList<>();
        Warrior enemyWarrior = new Warrior("\uD83C\uDFF9", "Лучник", 3, 80, 99, 5, 40);
        enemyWarriors.add(enemyWarrior);

        // Размещаем юнитов на карте
        battlemap.placeWarrior(playerWarrior, 0); // Игрок в первой строке первого столбца
        battlemap.placeEnemyWarriors(enemyWarriors); // Противник в последнем столбце

        // Сдвигаем врага ближе к игроку
        battlemap.updateWarriorPosition(enemyWarrior, -1, -1);

        // Создаем механику боя
        Battlemechanic battlemechanic = new Battlemechanic(battlemap, playerWarriors, enemyWarriors, hero);

        // Выполняем ход противника
        battlemechanic.enemyTurn();

        // Проверяем здоровье игрока
        assertTrue(playerWarrior.getHealth() < 120); // Здоровье должно уменьшиться
    }

    @Test
    public void testHealWithTent() {
        // Создаем боевую карту размером 5x5
        Battlemap battlemap = new Battlemap(5, 5);

        // Создаем героя с шатром первой помощи
        Character hero = new Character(0, 0);
        hero.setHasHealingTent(true); // Активируем шатёр

        // Создаем юнита игрока
        ArrayList<Warrior> playerWarriors = new ArrayList<>();
        Warrior playerWarrior = new Warrior("\uD83E\uDD3A", "Мечник", 4, 120, 50, 0, 80);

        // Наносим урон юниту
        playerWarrior.takeDamage(30); // Здоровье становится 20
        assertEquals(20, playerWarrior.getHealth()); // Проверяем текущее здоровье

        playerWarriors.add(playerWarrior);

        // Размещаем юнита на карте
        battlemap.placeWarrior(playerWarrior, 0);

        // Создаем механику боя
        Battlemechanic battlemechanic = new Battlemechanic(battlemap, playerWarriors, new ArrayList<>(), hero);

        // Лечим юнита с помощью шатра
        battlemechanic.healWithTent(playerWarrior);

        // Проверяем здоровье юнита
        assertTrue(playerWarrior.getHealth() > 20); // Здоровье должно увеличиться
    }
}
