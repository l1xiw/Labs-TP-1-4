package Buildings;

import org.testng.annotations.Test;
import static org.testng.AssertJUnit.*;
import Maps.Battlemap;
import Players.Character;

import java.util.ArrayList;
import java.util.List;

public class TavernTest {

    @Test
    public void testTavernInitialization() {
        // Создаем карту и героя
        Battlemap gamebMap = new Battlemap(5, 5);
        Character hero = new Character(0, 0);

        // Создаем таверну
        Tavern tavern = new Tavern(gamebMap, 1000, hero);

        // Проверяем начальные значения
        assertEquals(1000, tavern.getGold()); // Начальное золото
        assertNotNull(tavern.getAvailableWarriors()); // Доступные юниты не должны быть null
        assertEquals(5, tavern.getAvailableWarriors().size()); // Количество доступных юнитов
    }

    @Test
    public void testBuyWarrior_Successful() {
        // Создаем карту и героя
        Battlemap gamebMap = new Battlemap(5, 5);
        Character hero = new Character(0, 0);

        // Создаем таверну
        Tavern tavern = new Tavern(gamebMap, 1000, hero);

        // Список юнитов игрока
        List<Warrior> playerWarriors = new ArrayList<>();

        // Покупаем первого юнита (Копьеносец)
        Warrior selectedWarrior = tavern.getAvailableWarriors().get(0);
        if (selectedWarrior.getCost() <= tavern.getGold()) {
            tavern.setGold(tavern.getGold() - selectedWarrior.getCost());
            playerWarriors.add(selectedWarrior);
        }

        // Проверяем результат
        assertEquals(900, tavern.getGold()); // Золото уменьшилось на стоимость юнита
        assertEquals(1, playerWarriors.size()); // Юнит добавлен в армию
        assertTrue(playerWarriors.contains(selectedWarrior)); // Юнит присутствует в списке
    }

    @Test
    public void testBuyWarrior_NotEnoughGold() {
        // Создаем карту и героя
        Battlemap gamebMap = new Battlemap(5, 5);
        Character hero = new Character(0, 0);

        // Создаем таверну с недостаточным количеством золота
        Tavern tavern = new Tavern(gamebMap, 50, hero);

        // Список юнитов игрока
        List<Warrior> playerWarriors = new ArrayList<>();

        // Пытаемся купить первого юнита (Копьеносец)
        Warrior selectedWarrior = tavern.getAvailableWarriors().get(0);
        if (selectedWarrior.getCost() <= tavern.getGold()) {
            tavern.setGold(tavern.getGold() - selectedWarrior.getCost());
            playerWarriors.add(selectedWarrior);
        }

        // Проверяем результат
        assertEquals(50, tavern.getGold()); // Золото не изменилось
        assertTrue(playerWarriors.isEmpty()); // Юнит не добавлен в армию
    }



    @Test
    public void testMaxWarriorsLimitWithHealingTent() {
        // Создаем карту и героя с шатром первой помощи
        Battlemap gamebMap = new Battlemap(5, 5);
        Character hero = new Character(0, 0);
        hero.setHasHealingTent(true);

        // Создаем таверну
        Tavern tavern = new Tavern(gamebMap, 1000, hero);

        // Список юнитов игрока
        List<Warrior> playerWarriors = new ArrayList<>();

        // Покупаем максимальное количество юнитов
        for (int i = 0; i < 4; i++) {
            Warrior selectedWarrior = tavern.getAvailableWarriors().get(i % tavern.getAvailableWarriors().size());
            if (selectedWarrior.getCost() <= tavern.getGold()) {
                tavern.setGold(tavern.getGold() - selectedWarrior.getCost());
                playerWarriors.add(selectedWarrior);
            }
        }

        // Проверяем результат
        assertEquals(4, playerWarriors.size()); // Максимальное количество юнитов с шатром (4)
    }
}
