package Players;

import org.testng.annotations.Test;
import static org.testng.AssertJUnit.*;
import Maps.Map;

public class BotTest {

    @Test
    public void testBotInitialization() {
        // Создаем нового бота
        Bot bot = new Bot(8, 8);

        // Проверяем начальные значения
        assertEquals(8, bot.getX());
        assertEquals(8, bot.getY());
    }

    /*@Test
    public void testMoveTowards_Successful() {
        // Создаем карту размером 10x10
        Map gameMap = new Map(10, 10);
        Bot bot = new Bot(5, 5);

        // Перемещаем бота к цели (0, 0)
        boolean moved = bot.moveTowards(0, 0, gameMap);

        // Проверяем результат
        assertTrue(moved); // Движение успешно
        assertEquals(4, bot.getX()); // Бот приблизился к цели по X
        assertEquals(4, bot.getY()); // Бот приблизился к цели по Y
    }

    @Test
    public void testMoveTowards_NoMovement() {
        // Создаем карту размером 10x10
        Map gameMap = new Map(10, 10);
        Bot bot = new Bot(0, 0);

        // Попытка переместиться к цели (0, 0) (уже на месте)
        boolean moved = bot.moveTowards(0, 0, gameMap);

        // Проверяем результат
        assertFalse(moved); // Движение не требуется
        assertEquals(0, bot.getX()); // Позиция X не изменилась
        assertEquals(0, bot.getY()); // Позиция Y не изменилась
    }

    @Test
    public void testMoveTowards_Boundary() {
        // Создаем карту размером 10x10
        Map gameMap = new Map(10, 10);
        Bot bot = new Bot(9, 9);

        // Перемещаем бота к цели (0, 0)
        for (int i = 0; i < 10; i++) {
            bot.moveTowards(0, 0, gameMap);
        }

        // Проверяем, что бот не вышел за границы карты
        assertEquals(0, bot.getX()); // Бот достиг цели по X
        assertEquals(0, bot.getY()); // Бот достиг цели по Y
    }*/
}