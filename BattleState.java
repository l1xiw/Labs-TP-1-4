import Maps.Battlemap;
import Buildings.Warrior;
import java.util.List;

public class BattleState {
    private Battlemap battleMap;
    private List<Warrior> playerWarriors;
    private List<Warrior> enemyWarriors;

    public BattleState(Battlemap battleMap, List<Warrior> playerWarriors, List<Warrior> enemyWarriors) {
        this.battleMap = battleMap;
        this.playerWarriors = playerWarriors;
        this.enemyWarriors = enemyWarriors;
    }

    // Геттеры
    public Battlemap getBattleMap() { return battleMap; }
    public List<Warrior> getPlayerWarriors() { return playerWarriors; }
    public List<Warrior> getEnemyWarriors() { return enemyWarriors; }
}