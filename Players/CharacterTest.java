package Players;

import org.testng.annotations.Test;


import static org.testng.AssertJUnit.*;
import Maps.Map;


public class CharacterTest {

    @Test
    public void testCharacterInitialization() {
        // Создаем нового героя
        Character hero = new Character(2,3);

        // Проверяем начальные значения
        assertEquals(2, hero.getX()); // Начальная позиция X
        assertEquals(3, hero.getY()); // Начальная позиция Y
        assertEquals(9, hero.getEnergy()); // Начальная энергия
        assertFalse(hero.hasHorse()); // Лошадь изначально отсутствует
        assertFalse(hero.hasHealingTent()); // Шатёр изначально отсутствует
    }



    @Test
    public void testHorseAndHealingTent() {
        Character hero = new Character(2, 3);

        // Устанавливаем флаг наличия лошади
        hero.setHasHorse(true);
        assertTrue(hero.hasHorse()); // Лошадь должна быть

        // Устанавливаем флаг наличия шатра
        hero.setHasHealingTent(true);
        assertTrue(hero.hasHealingTent()); // Шатёр должен быть

        // Снимаем флаг наличия лошади
        hero.setHasHorse(false);
        assertFalse(hero.hasHorse()); // Лошадь должна быть удалена

        // Снимаем флаг наличия шатра
        hero.setHasHealingTent(false);
        assertFalse(hero.hasHealingTent()); // Шатёр должен быть удален
    }
}