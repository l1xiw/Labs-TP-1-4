package Buildings;

import org.testng.annotations.Test;
import static org.testng.AssertJUnit.*;
import Players.Character;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class StableTest {

    @Test
    public void testStableInitialization() {
        // Создаем конюшню с начальным количеством золота
        Stable stable = new Stable(1000);

        // Проверяем начальные значения
        assertEquals(1000, stable.getGold()); // Начальное золото
        assertFalse(isHorsePurchased(stable)); // Лошадь еще не куплена
    }

    @Test
    public void testBuyHorse_Successful() {
        // Создаем героя
        Character hero = new Character(0, 0);
        assertFalse(hero.hasHorse()); // Убедимся, что у героя изначально нет лошади

        // Создаем конюшню
        Stable stable = new Stable(1000);

        // Покупаем лошадь
        stable.buyHorse(hero);

        // Проверяем результат
        assertEquals(800, stable.getGold()); // Золото уменьшилось на 200
        assertTrue(hero.hasHorse()); // Герой теперь имеет лошадь
        assertTrue(isHorsePurchased(stable)); // Флаг покупки лошади установлен
    }

    @Test
    public void testBuyHorse_NotEnoughGold() {
        // Создаем героя
        Character hero = new Character(0, 0);
        assertFalse(hero.hasHorse()); // Убедимся, что у героя изначально нет лошади

        // Создаем конюшню с недостаточным количеством золота
        Stable stable = new Stable(100);

        // Пытаемся купить лошадь
        stable.buyHorse(hero);

        // Проверяем результат
        assertEquals(100, stable.getGold()); // Золото не изменилось
        assertFalse(hero.hasHorse()); // Герой по-прежнему не имеет лошади
        assertFalse(isHorsePurchased(stable)); // Флаг покупки лошади не установлен
    }

    @Test
    public void testBuyHorse_AlreadyPurchased() {
        // Создаем героя
        Character hero = new Character(0, 0);
        assertFalse(hero.hasHorse()); // Убедимся, что у героя изначально нет лошади

        // Создаем конюшню
        Stable stable = new Stable(1000);

        // Первый раз покупаем лошадь
        stable.buyHorse(hero);

        // Второй раз пытаемся купить лошадь
        stable.buyHorse(hero);

        // Проверяем результат
        assertEquals(800, stable.getGold()); // Золото не изменилось после второй попытки
        assertTrue(hero.hasHorse()); // Герой по-прежнему имеет лошадь
        assertTrue(isHorsePurchased(stable)); // Флаг покупки лошади остается установленным
    }




































    // Вспомогательный метод для получения значения приватного поля horsePurchased через рефлексию
    private boolean isHorsePurchased(Stable stable) {
        try {
            Field field = Stable.class.getDeclaredField("horsePurchased");
            field.setAccessible(true);
            return field.getBoolean(stable);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException("Ошибка при доступе к полю horsePurchased", e);
        }
    }
}