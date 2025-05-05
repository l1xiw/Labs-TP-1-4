package Players;

import Maps.Map;

public class Character {
    private int x;
    private int y;
    private int previousX; // Предыдущая позиция по X
    private int previousY; // Предыдущая позиция по Y
    private int energy; // Энергия
    private boolean hasHorse = false; // Есть ли у игрока лошадь
    private boolean hasHealingTent = false; // Есть ли у игрока шатёр первой помощи

    public Character(int x, int y) {
        this.x = x;
        this.y = y;
        this.previousX = x;
        this.previousY = y;
        this.energy = 9; // Начальное значение энергии
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

    public int getEnergy() {
        return energy;
    }
    public int setEnergy(int energy) {
        return energy;
    }

    public boolean hasHorse() {
        return hasHorse;
    }

    public void setHasHorse(boolean hasHorse) {
        this.hasHorse = hasHorse;
    }

    public boolean hasHealingTent() {
        return hasHealingTent;
    }

    public void setHasHealingTent(boolean hasHealingTent) {
        this.hasHealingTent = hasHealingTent;
    }

    // Метод для перемещения героя
    public boolean move(int dx, int dy, Map gameMap) {
        if (energy <= 0) {
            System.out.println("Энергия закончилась! Вы не можете перемещаться.");
            return false;
        }

        int newX = x + dx;
        int newY = y + dy;

        // Проверяем, что новое положение внутри карты
        if (newX >= 0 && newX < gameMap.getXX() && newY >= 0 && newY < gameMap.getYY()) {
            previousX = x; // Сохраняем предыдущую позицию
            previousY = y;
            x = newX;
            y = newY;
            energy--; // Тратим энергию
            return true;
        }
        return false; // Если движение невозможно (выход за границы карты), возвращаем false
    }
}