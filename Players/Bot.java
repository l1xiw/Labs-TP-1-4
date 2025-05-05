package Players;

import Maps.Map;

public class Bot {
    private int x;
    private int y;
    private int previousX; // Предыдущая позиция по X
    private int previousY; // Предыдущая позиция по Y

    public Bot(int x, int y) {
        this.x = x;
        this.y = y;
        this.previousX = x;
        this.previousY = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getPreviousX() {
        return previousX;
    }

    public int getPreviousY() {
        return previousY;
    }

    // Метод для перемещения бота к замку игрока
    public boolean moveTowards(int targetX, int targetY, Map gameMap) {
        boolean moved = false;

        previousX = x; // Сохраняем предыдущую позицию
        previousY = y;

        if (x < targetX && x + 1 < gameMap.getXX()) {
            x++;
            moved = true;
        } else if (x > targetX && x - 1 >= 0) {
            x--;
            moved = true;
        }

        if (y < targetY && y + 1 < gameMap.getYY()) {
            y++;
            moved = true;
        } else if (y > targetY && y - 1 >= 0) {
            y--;
            moved = true;
        }

        return moved;
    }
}