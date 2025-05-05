package Maps;

import java.util.ArrayList;
import java.util.List;
import Buildings.Warrior;

public class Battlemap {
    private Cell[][] bm;
    private int bx;
    private int by;
    private ArrayList<Warrior> warriors; // Список юнитов на боевой карте

    public Battlemap(int bx, int by) {
        this.bx = bx;
        this.by = by;
        this.bm = new Cell[bx][by];
        this.warriors = new ArrayList<>();
        createbmap();
    }

    private void createbmap() {
        for (int x = 0; x < bx; x++) {
            for (int y = 0; y < by; y++) {
                bm[x][y] = new Cell("   "); // Установим символ по умолчанию
            }
        }
    }

    public void displaybmap() {
        for (int x = 0; x < bx; x++) {
            for (int y = 0; y < by; y++) {
                String cell = bm[x][y].getCelltype();
                System.out.print("[ " + cell + " ] ");
            }
            System.out.println();
        }
    }

    // Метод для размещения юнита на карте
    public void placeWarrior(Warrior warrior, int index) {
        if (index < bx) {
            bm[index][0] = new Cell(warrior.getEmoji()); // Размещение юнита в первом столбце
            warrior.setPosition(index, 0); // Устанавливаем координаты юнита
            warriors.add(warrior);  // Добавляем юнита в список
        }
    }

    // Метод для размещения юнитов противника на карте
    // Метод для размещения юнитов противника на карте
    public void placeEnemyWarriors(List<Warrior> enemyWarriors) {
        for (int i = 0; i < enemyWarriors.size(); i++) {
            Warrior enemy = enemyWarriors.get(i);
            bm[i][by - 1] = new Cell(enemy.getEmoji()); // Размещаем в последнем столбце
            enemy.setPosition(i, by - 1); // Устанавливаем координаты юнита
            this.warriors.add(enemy); // Добавляем юнита в общий список
        }
    }

    // Метод для получения клетки по координатам
    public Cell getCell(int x, int y) {
        if (x >= 0 && x < bx && y >= 0 && y < by) {
            return bm[x][y];
        }
        return null;
    }

    // Метод для обновления позиции юнита на карте
    public void updateWarriorPosition(Warrior warrior, int dx, int dy) {
        // Находим текущую позицию юнита
        int oldX = warrior.getX();
        int oldY = warrior.getY();

        // Очищаем старую позицию
        bm[oldX][oldY].setCelltype("   ");

        // Устанавливаем новую позицию
        int newX = oldX + dx;
        int newY = oldY + dy;

        if (newX >= 0 && newX < bx && newY >= 0 && newY < by) {
            bm[newX][newY].setCelltype(warrior.getEmoji());
            warrior.setPosition(newX, newY); // Обновляем координаты юнита
        } else {
            // Если новая позиция за пределами карты, возвращаем юнита на старую позицию
            bm[oldX][oldY].setCelltype(warrior.getEmoji());
        }
    }
    public void removeunit(int x, int y){
        bm[x][y].setCelltype("-");
    }

    // Геттеры для размеров карты
    public int getBX() {
        return bx;
    }

    public int getBY() {
        return by;
    }
}
