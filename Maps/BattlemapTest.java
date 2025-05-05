package Maps;
import java.util.ArrayList;
import java.util.List;
import org.testng.annotations.Test;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import static org.testng.AssertJUnit.*;
import Maps.Battlemap;
import Buildings.Warrior;

public class BattlemapTest {

    @Test
    public void testBattlemapInitialization() {
        // Создаем боевую карту размером 5x5
        Battlemap battlemap = new Battlemap(5, 5);

        // Проверяем размеры карты
        assertEquals(5, battlemap.getBX()); // Размер по оси X
        assertEquals(5, battlemap.getBY()); // Размер по оси Y

        // Проверяем, что все клетки инициализированы символом "   "
        for (int x = 0; x < 5; x++) {
            for (int y = 0; y < 5; y++) {
                assertEquals("   ", battlemap.getCell(x, y).getCelltype());
            }
        }
    }

    @Test
    public void testPlaceWarrior() {
        // Создаем боевую карту размером 5x5
        Battlemap battlemap = new Battlemap(5, 5);

        // Создаем юнита
        Warrior warrior = new Warrior("\uD83E\uDD3A", "Мечник", 4, 120, 50, 0, 80);

        // Размещаем юнита на карте
        battlemap.placeWarrior(warrior, 0);

        // Проверяем, что юнит размещен в первой строке первого столбца
        assertEquals("\uD83E\uDD3A", battlemap.getCell(0, 0).getCelltype());
        assertEquals(0, warrior.getX());
        assertEquals(0, warrior.getY());
    }

    @Test
    public void testPlaceEnemyWarriors() {
        // Создаем боевую карту размером 5x5
        Battlemap battlemap = new Battlemap(5, 5);

        // Создаем список юнитов противника
        List<Warrior> enemyWarriors = new ArrayList<>();
        enemyWarriors.add(new Warrior("\uD83E\uDD3A", "Мечник", 4, 120, 50, 0, 80));
        enemyWarriors.add(new Warrior("\uD83C\uDFF9", "Лучник", 3, 80, 99, 5, 40));

        // Размещаем юнитов противника на карте
        battlemap.placeEnemyWarriors(enemyWarriors);

        // Проверяем, что юниты размещены в последнем столбце
        assertEquals("\uD83E\uDD3A", battlemap.getCell(0, 4).getCelltype());
        assertEquals("\uD83C\uDFF9", battlemap.getCell(1, 4).getCelltype());
    }

    @Test
    public void testUpdateWarriorPosition() {
        // Создаем боевую карту размером 5x5
        Battlemap battlemap = new Battlemap(5, 5);

        // Создаем юнита
        Warrior warrior = new Warrior("\uD83E\uDD3A", "Мечник", 4, 120, 50, 0, 80);

        // Размещаем юнита на карте
        battlemap.placeWarrior(warrior, 0);

        // Обновляем позицию юнита
        battlemap.updateWarriorPosition(warrior, 1, 0); // Сдвиг вниз на одну строку

        // Проверяем, что позиция обновлена
        assertEquals("   ", battlemap.getCell(0, 0).getCelltype()); // Старая позиция очищена
        assertEquals("\uD83E\uDD3A", battlemap.getCell(1, 0).getCelltype()); // Новая позиция
        assertEquals(1, warrior.getX());
        assertEquals(0, warrior.getY());
    }


}
