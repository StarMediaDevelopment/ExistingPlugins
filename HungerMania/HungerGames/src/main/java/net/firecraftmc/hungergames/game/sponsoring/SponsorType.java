package net.firecraftmc.hungergames.game.sponsoring;

import lombok.Getter;
import org.bukkit.Material;

@Getter
public enum SponsorType {
    FOOD("Food Item", Material.PORK, 2, 5, "Sponsor a random food item!"), 
    WEAPON("Weapon Item", Material.IRON_SWORD, 5, 10, "Sponsor a random weapon item!"), 
    ARMOR("Armor Item", Material.CHAINMAIL_CHESTPLATE, 5, 10, "Sponsor a random armor item!"), 
    POTION("Potion Effect", Material.EXP_BOTTLE, 10, 20, "Sponsor a random potion effect!");
    
    private String name;
    private Material display;
    private int priceCoins, priceScore;
    private String description;

    SponsorType(String name, Material display, int priceCoins, int priceScore, String description) {
        this.name = name;
        this.display = display;
        this.priceCoins = priceCoins;
        this.priceScore = priceScore;
        this.description = description;
    }
}
