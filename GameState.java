import Maps.Map;
import Players.Bot;
import Players.Character;
import Buildings.Warrior;
import java.util.List;

public class GameState {
    private Map gameMap;
    private Character hero;
    private Bot bot;
    private List<Warrior> playerWarriors;
    private int gold;
    private boolean firstBattleCompleted;
    private BattleState battleState; // Убедитесь, что это поле объявлено

    // Конструктор
    public GameState(Map gameMap, Character hero, Bot bot,
                     List<Warrior> playerWarriors, int gold,
                     boolean firstBattleCompleted) {
        this.gameMap = gameMap;
        this.hero = hero;
        this.bot = bot;
        this.playerWarriors = playerWarriors;
        this.gold = gold;
        this.firstBattleCompleted = firstBattleCompleted;
        this.battleState = null; // Инициализация
    }

    // Геттеры
    public Map getGameMap() { return gameMap; }
    public Character getHero() { return hero; }
    public Bot getBot() { return bot; }
    public List<Warrior> getPlayerWarriors() { return playerWarriors; }
    public int getGold() { return gold; }
    public boolean isFirstBattleCompleted() { return firstBattleCompleted; }
    public BattleState getBattleState() { return battleState; } // Без рекурсии

    // Сеттеры (если нужно)
    public void setBattleState(BattleState battleState) {
        this.battleState = battleState;
    }
}