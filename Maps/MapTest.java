package Maps;

import org.testng.annotations.Test;
import static org.testng.AssertJUnit.*;
import Maps.Map;

public class MapTest {

    @Test
    public void testGetCellTypeOutOfBounds() {
        // Создаем карту размером 10x10
        Map map = new Map();

        // Проверяем, что метод возвращает пустую строку для координат за пределами карты
        assertEquals("", map.getCellType(-1, -1)); // За пределами слева-сверху
        assertEquals("", map.getCellType(10, 10)); // За пределами справа-снизу
    }
}
