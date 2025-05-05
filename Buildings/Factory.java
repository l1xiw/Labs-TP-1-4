package Buildings;

import Players.Character;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import Maps.Map;
import Players.Bot;

public class Factory extends Building {
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private final Map gameMap;
    private final Bot bot;
    private boolean working = false;

    public Factory(int gold, Map gameMap, Bot bot) {
        super(gold);
        this.gameMap = gameMap;
        this.bot = bot;
    }

    @Override
    public synchronized void interact(Character hero, List<Warrior> warriors) {
        if (!working) {
            System.out.println("Вы зашли на завод. Работа началась.");
            working = true;

            scheduler.scheduleAtFixedRate(() -> {
                // Начисляем деньги
                setGold(getGold() + 100);
                System.out.println("Вы заработали +100 золота!");

                // Двигаем бота
                if (bot != null) {
                    boolean moved = bot.moveTowards(
                            gameMap.getPlayerCastleX(),
                            gameMap.getPlayerCastleY(),
                            gameMap
                    );

                    if (moved) {
                        gameMap.updateMap(null, bot);
                        gameMap.displayMap();
                    }
                }

            }, 5, 5, TimeUnit.SECONDS); // Каждые 5 секунд

        } else {
            System.out.println("Вы уже работаете на заводе.");
        }
    }

}
