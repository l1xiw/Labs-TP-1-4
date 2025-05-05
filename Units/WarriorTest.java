package Buildings;

import org.testng.annotations.Test;
import static org.testng.AssertJUnit.*;

public class WarriorTest {

    @Test
    public void testWarriorInitialization() {
        // Создаем нового юнита
        Warrior warrior = new Warrior("⚔️", "Мечник", 1, 100, 120, 2, 30);

        // Проверяем начальные значения
        assertEquals("⚔️", warrior.getEmoji());
        assertEquals("Мечник", warrior.getName());

        assertEquals(100, warrior.getCost());
        assertEquals(120, warrior.getMaxHealth());
        assertEquals(120, warrior.getHealth()); // Здоровье изначально равно максимальному

        assertEquals(0, warrior.getX()); // Начальная позиция X
        assertEquals(0, warrior.getY()); // Начальная позиция Y
    }







      @Test
    public void testSetPosition() {
        // Создаем юнита
        Warrior warrior = new Warrior("⚔️", "Мечник", 1, 100, 120, 2, 30);

        // Устанавливаем новую позицию
        warrior.setPosition(5, 7);

        // Проверяем результат
        assertEquals(5, warrior.getX());
        assertEquals(7, warrior.getY());
    }
}
