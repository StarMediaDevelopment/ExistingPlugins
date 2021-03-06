package net.firecraftmc.hungergames.game.sponsoring;

import lombok.Getter;
import net.firecraftmc.manialib.util.Utils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

@Getter
public class SponsorItem {
    protected String name;
    protected Material material;
    protected int amount;
    protected SponsorType type;

    public SponsorItem(String name, Material material, int amount, SponsorType type) {
        this.name = name;
        this.material = material;
        this.amount = amount;
        this.type = type;
    }

    public SponsorItem(Material material, int amount, SponsorType type) {
        this(Utils.capitalizeEveryWord(material.name()), material, amount, type);
    }
    
    public String getLoreLine() {
        if (type == SponsorType.FOOD) {
            return amount + " of " + name;
        }
        return name;
    }

    public ItemStack getItemStack() { //TODO
        return null;
    }
}
