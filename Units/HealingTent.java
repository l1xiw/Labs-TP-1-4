package Units;

import Buildings.Warrior; // Убедитесь, что Warrior импортирован из правильного пакета

public class HealingTent extends Warrior {
    public HealingTent() {
        super("⛺", "Шатёр первой помощи", 0, 300, 50, 0, 0); // Эмодзи ⛺
    }

    @Override
    public boolean attack(Warrior target) {
        System.out.println("Шатёр первой помощи не может атаковать!");
        return true; // Шатёр не теряет здоровья
    }

    @Override
    public void setPosition(int x, int y) {
        super.setPosition(x, y);
        System.out.println("Шатёр первой помощи зафиксирован на позиции: (" + x + ", " + y + ")");
    }
}