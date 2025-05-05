package Buildings;

public class Warrior {
    private String emoji;
    private String name;
    private int id;
    private int cost;
    private int maxHealth;
    private int range;
    private int damage;
    private int health;
    private int x;
    private int y;

    public Warrior(String emoji, String name, int id, int cost, int maxHealth, int range, int damage) {
        this.emoji = emoji;
        this.name = name;
        this.id = id;
        this.cost = cost;
        this.maxHealth = maxHealth;
        this.range = range;
        this.damage = damage;
        this.health = maxHealth;
    }
    public void takeDamage(int damage) {
        health -= damage;
        if (health < 0) {
            health = 0; // Здоровье не может быть меньше 0
        }
    }
    @Override
    public String toString() {
        return emoji + "," + name + "," + damage + ","
                + maxHealth + ","
                + cost + "," + x + "," + y;
    }

    // Метод для десериализации из строки
    public static Warrior fromString(String data) {
        String[] parts = data.split(",");
        return new Warrior(
                parts[0],
                parts[1],
                Integer.parseInt(parts[2]),
                Integer.parseInt(parts[3]),
                Integer.parseInt(parts[4]),
                Integer.parseInt(parts[5]),
                Integer.parseInt(parts[6])
        );
    }



    public String getName() {
        return name;
    }

    public int getCost() {
        return cost;
    }

    public int getHealth() {
        return health;
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public String getEmoji() {
        return emoji;
    }

    public boolean attack(Warrior target) {
        target.health -= damage;
        if (target.health <= 0) {
            return false; // Юнит погиб
        }
        return true; // Юнит жив
    }

    // Метод для лечения
    public void heal(int amount) {
        health = Math.min(maxHealth, health + amount);
    }
}