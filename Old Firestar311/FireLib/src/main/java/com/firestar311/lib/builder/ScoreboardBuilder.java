package com.firestar311.lib.builder;

import com.firestar311.lib.builder.sb.SBLine;
import com.firestar311.lib.util.Utils;
import org.bukkit.Bukkit;
import org.bukkit.scoreboard.*;

import java.util.*;

public class ScoreboardBuilder {
    
    private String name, title;
    private DisplaySlot slot;
    private SortedMap<Integer, SBLine> lines = new TreeMap<>();
    
    public ScoreboardBuilder(String name) {
        this.name = name;
    }
    
    public static ScoreboardBuilder start(String name) {
        return new ScoreboardBuilder(name);
    }
    
    public ScoreboardBuilder setName(String name) {
        this.name = name;
        return this;
    }
    
    public ScoreboardBuilder setTitle(String title) {
        this.title = title;
        return this;
    }
    
    public ScoreboardBuilder setSlot(DisplaySlot slot) {
        this.slot = slot;
        return this;
    }
    
    public ScoreboardBuilder addLine(SBLine text) {
        int position;
        if (this.lines.isEmpty()) {
          position = 0;
        } else {
            position = this.lines.lastKey() + 1;
        }
        this.lines.put(position, text);
        return this;
    }
    
    public ScoreboardBuilder setLine(int line, SBLine text) {
        this.lines.put(line, text);
        return this;
    }
    
    public Scoreboard build() {
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        Objective objective;
        if (title != null) {
            objective = scoreboard.registerNewObjective(name, "dummy", Utils.color(title));
        } else {
            objective = scoreboard.registerNewObjective(name, "dummy", Utils.color(name));
        }
        objective.setDisplaySlot(slot);
        
        if (this.lines.size() > 16) {
            Bukkit.getLogger().info("Scoreboard with the name " + name + " has over 16 lines. Only the first 16 lines are supported by Minecraft");
        }
    
        for (int i = 0; i < 15; i++) {
            SBLine text = this.lines.get(i);
            if (text != null) {
                Team line = scoreboard.registerNewTeam(text.getName());
                line.setPrefix(Utils.color(text.getPrefix()));
                line.setSuffix(Utils.color(line.getSuffix()));
                line.addEntry(Utils.color(text.getName()));
                objective.getScore(Utils.color(text.getName())).setScore(15 - i);
            }
        }
        
        return scoreboard;
    }
}