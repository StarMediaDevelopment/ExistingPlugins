package net.firecraftmc.hub.leaderboard;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.gmail.filoghost.holographicdisplays.api.line.HologramLine;
import com.gmail.filoghost.holographicdisplays.api.line.TextLine;
import lombok.Getter;
import lombok.Setter;
import net.firecraftmc.hub.CenturionsHub;
import net.firecraftmc.maniacore.api.CenturionsCore;
import net.firecraftmc.maniacore.api.events.EventInfo;
import net.firecraftmc.maniacore.api.util.CenturionsUtils;
import org.bukkit.Location;

import java.util.*;

@Getter
@Setter
public class Leaderboard {
    private Location location;
    private int min, max;
    private Hologram hologram;
    
    public Leaderboard(Location location, int min, int max) {
        this.location = location;
        this.min = min;
        this.max = max;
    }
    
    public void spawn() {
        this.hologram = HologramsAPI.createHologram(CenturionsHub.getInstance(), location);
        update();
    }
    
    public void update() {
        EventInfo activeEvent = CenturionsCore.getInstance().getEventManager().getActiveEvent();
        if (activeEvent == null) {
            return;
        }
//        Map<UUID, HungerGamesProfile> profiles = new HashMap<>();
//        List<IRecord> records = CenturionsCore.getInstance().getDatabase().getRecords(ProfileRecord.class, null, null);
//        for (IRecord record : records) {
//            if (record instanceof ProfileRecord) {
//                HungerGamesProfile profile = ((ProfileRecord) record).toObject();
//                if (activeEvent.getPlayers().contains(profile.getUserId())) {
//                    profiles.put(profile.getUser().getUniqueId(), profile);
//                }
//            }
//        }
//        
//        Map<UUID, Integer> totalValues = new HashMap<>();
//        for (HungerGamesProfile profile : profiles.values()) {
//            totalValues.put(profile.getUser().getUniqueId(), profile.getScore());
//        }
        
        class LeaderboardPlayer implements Comparable<LeaderboardPlayer> {
            @Getter private UUID uuid;
            @Getter private int value;
            
            public LeaderboardPlayer(UUID uuid, int value) {
                this.uuid = uuid;
                this.value = value;
            }
            @Override
            public int compareTo(LeaderboardPlayer o) {
                return -Integer.compare(value, o.value);
            }
        }
        
        SortedSet<LeaderboardPlayer> sortedSet = new TreeSet<>();
//        for (Entry<UUID, Integer> entry : totalValues.entrySet()) {
//            sortedSet.add(new LeaderboardPlayer(entry.getKey(), entry.getValue()));
//        }
        
        
        SortedMap<Integer, UUID> rankedPlayers = new TreeMap<>();
        for (LeaderboardPlayer mapEntry : sortedSet) {
            UUID uuid = mapEntry.getUuid();
            try {
                if (rankedPlayers.lastKey() != null) {
                    rankedPlayers.put(rankedPlayers.lastKey() + 1, uuid);
                } else {
                    rankedPlayers.put(1, uuid);
                }
            } catch (Exception e) {
                rankedPlayers.put(1, uuid);
            }
        }
    
        Map<Integer, UUID> leaderboardPositions = new HashMap<>();
        
        for (int i = 0; i <= 12; i++) {
            if (rankedPlayers.containsKey(i + min)) {
                leaderboardPositions.put(i, rankedPlayers.get(i + min));
            }
        }
    
        HologramLine mainLine;
        try {
            mainLine = this.hologram.getLine(0);
            if (mainLine instanceof TextLine) {
                TextLine textLine = (TextLine) mainLine;
                if (!textLine.getText().contains("LEADERBOARD")) {
                    textLine.setText(CenturionsUtils.color("&6&lLEADERBOARD (" + min + " - " + max + ")"));
                }
            }
        } catch (Exception e) {
            this.hologram.insertTextLine(0, CenturionsUtils.color("&6&lLEADERBOARD (" + min + " - " + max + ")"));
        }
    
//        for (Entry<Integer, UUID> entry : leaderboardPositions.entrySet()) {
//            HologramLine line = null;
//            try {
//                line = this.hologram.getLine(entry.getKey() + 1);
//            } catch (Exception e) {}
//            HungerGamesProfile profile = profiles.get(entry.getValue());
//            User user = profile.getUser();
//            if (line != null) {
//                if (line instanceof TextLine) {
//                    TextLine textLine = (TextLine) line;
//                    String text = textLine.getText();
//                    if (text.contains(user.getName())) {
//                        if (!text.contains(profile.getScore() + "")) {
//                            textLine.setText(user.getName() + " : " + profile.getScore());
//                        }
//                    } else {
//                        textLine.setText(user.getName() + " : " + profile.getScore());
//                    }
//                }
//            } else {
//                this.hologram.insertTextLine(entry.getKey() + 1, user.getName() + " : " + profile.getScore());
//            }
//        }
    }
}