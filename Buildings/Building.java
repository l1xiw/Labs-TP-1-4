package Buildings;



import Players.Character;

import java.util.List;

public abstract class Building {
    protected int gold;

    public Building(int gold) {
        this.gold = gold;
    }

    public abstract void interact(Character hero, List<Warrior> playerWarriors);

    // Геттеры и сеттеры для золота
    public int getGold() {
        return gold;
    }

    public void setGold(int gold) {
        this.gold = gold;
    }
}