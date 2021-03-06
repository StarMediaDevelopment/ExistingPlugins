package net.firecraftmc.maniacore.api.ranks;

import lombok.Getter;

public enum Rank {
    
    ROOT("Owner", "&4", true, "&6", 5, 5, 0),
    CONSOLE("Console", "&4&l", false, "&6", 0, 0, 0),
    OWNER("Owner", "&4", true, "&c", 5, 3, 0), 
    ADMIN("Admin", "&c", true, "&b", 4, 2, 0),
    SR_MOD("Sr Mod", "&2", true, "&e", 3, 2, 0),
    MOD("Mod", "&2", true, "&e", 3, 1.5, 0),
    HELPER("Helper", "&a", true, "&e", 2, 1.25, 0), 
    BUILDER("Builder", "&5", true, "&f", 2, 1.25, 0),
    MEDIAPLUS("Media+", "&d", true, "&f", 4, 2.25, 0),
    MEDIA("Media", "&d", true, "&f", 4, 2, 0),
    SURVIVALIST("Survivalist", "&5", true, "&f", 4, 2, 1),
    FORAGER("Forager", "&9", true, "&f", 3, 1.5, 2),
    SCAVENGER("Scavenger", "&3", true, "&f", 2, 1.25, 3),
    DEFAULT("Default", "&f", false, "&7", 1, 1, 5);
    
    
    @Getter private final String name;
    @Getter private final String baseColor;
    private final boolean prefix;
    @Getter private final String chatColor;
    @Getter private final int voteWeight;
    @Getter private final double coinMultiplier;
    @Getter private final double chatCooldown;
    
    Rank(String name, String baseColor, boolean prefix, String chatColor, int voteWeight, double coinMultiplier, double chatCooldown) {
        this.name = name;
        this.baseColor = baseColor;
        this.prefix = prefix;
        this.chatColor = chatColor;
        this.voteWeight = voteWeight;
        this.coinMultiplier = coinMultiplier;
        this.chatCooldown = chatCooldown;
    }
    
    public String getPrefix() {
        if (prefix) {
            return getBaseColor() + "&l" + getName().toUpperCase();
        } else {
            return getBaseColor();
        }
    }
}
