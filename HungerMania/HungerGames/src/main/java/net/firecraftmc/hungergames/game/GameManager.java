package net.firecraftmc.hungergames.game;

import lombok.Getter;
import lombok.Setter;
import net.firecraftmc.hungergames.HungerGames;
import net.firecraftmc.hungergames.records.GameRecord;
import net.firecraftmc.manialib.sql.IRecord;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameManager {
    
    private Map<Integer, Game> games = new HashMap<>();
    private Game currentGame;
    private HungerGames plugin;
    @Getter @Setter private int gameCounter = 0;
    
    public GameManager(HungerGames plugin) {
        this.plugin = plugin;
    }
    
    public Game getGame(int id) {
        if (this.games.containsKey(id)) {
            return this.games.get(id);
        }
        
        List<IRecord> records = plugin.getCenturionsCore().getDatabase().getRecords(GameRecord.class, "id", id);
        if (records.size() > 0) {
            for (IRecord record : records) {
                if (record instanceof GameRecord) {
                    GameRecord gameRecord = (GameRecord) record;
                    if (gameRecord.getId() == id) {
                        this.games.put(id, gameRecord.toObject());
                        return gameRecord.toObject();
                    }
                }
            }
        }
        
        return null;
    }
    
    public Game getCurrentGame() {
        return currentGame;
    }
    
    public void setCurrentGame(Game currentGame) {
        if (currentGame != null) {
            if (!this.games.containsKey(currentGame.getId())) {
                this.games.put(currentGame.getId(), currentGame);
            }
        }
        this.currentGame = currentGame;
    }
}