package net.firecraftmc.maniacore.spigot.perks;

import lombok.Getter;
import net.firecraftmc.maniacore.api.CenturionsCore;
import net.firecraftmc.maniacore.api.redis.Redis;
import net.firecraftmc.maniacore.api.stats.Stats;
import net.firecraftmc.maniacore.api.util.CenturionsUtils;
import net.firecraftmc.maniacore.spigot.user.SpigotUser;
import net.firecraftmc.maniacore.spigot.util.ItemBuilder;
import net.firecraftmc.manialib.util.Utils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

@Getter
public abstract class Perk implements Comparable<Perk> {
    
    protected String displayName;
    protected int baseCost;
    protected int chance;
    protected Material iconMaterial;
    protected PerkCategory category;
    protected String description;
    
    public Perk() {
    }
    
    public Perk(String displayName, int baseCost, int chance, Material iconMaterial, PerkCategory category, String description) {
        this.displayName = displayName;
        this.baseCost = baseCost;
        this.chance = chance;
        this.iconMaterial = iconMaterial;
        this.category = category;
        this.description = description;
    }
    
    public abstract boolean activate(SpigotUser user);
    
    public net.firecraftmc.maniacore.spigot.perks.PerkInfo create(UUID uuid) {
        return new net.firecraftmc.maniacore.spigot.perks.PerkInfo(uuid, getName(), getDefaultValue(), System.currentTimeMillis(), System.currentTimeMillis());
    }
    
    public final String getName() {
        return this.displayName.toLowerCase().replace(" ", "_");
    }
    
    public final boolean getDefaultValue() {
        return false;
    }
    
    public final boolean isNumber() {
        return false;
    }
    
    public void handlePurchase(SpigotUser user) {
        PerkInfo perkInfo = user.getPerkInfo(this);
        if (perkInfo.getValue()) {
            user.sendMessage("&cYou have already purchased that perk.");
            return;
        }

        if (user.getStat(Stats.COINS).getAsInt() < this.baseCost) {
            user.sendMessage("&cYou do not have enough coins to purchase that perk.");
            return;
        }

        user.getStat(Stats.COINS).setValue(user.getStat(Stats.COINS).getAsInt() - this.baseCost);
        perkInfo.setValue(true);
        CenturionsCore.getInstance().getDatabase().pushRecord(new PerkInfoRecord(perkInfo));
        user.sendMessage("&aYou purchased the perk " + getDisplayName());
        Redis.pushUser(user);
    }
    
    public ItemStack getIcon(SpigotUser user) {
        if (iconMaterial == null) {
            CenturionsCore.getInstance().getLogger().severe("Perk " + getName() + " does not have an icon setup.");
            return ItemBuilder.start(Material.REDSTONE_BLOCK).withLore("&cNo Icon Setup.").build();
        }
        ItemBuilder itemBuilder = ItemBuilder.start(iconMaterial).setDisplayName("&b" + Utils.capitalizeEveryWord(displayName));
        List<String> lore = new LinkedList<>();
        if (getDescription().contains("\n")) {
            String[] lines = getDescription().split("\n");
            for (String line : lines) {
                lore.add("&7&o" + line);
            }
        } else {
            lore.add("&7&o" + getDescription());
        }
        if (user.getPerkInfo(this).getValue()) {
            lore.add(CenturionsUtils.color("&a&oPurchased"));
            if (user.getPerkInfo(this).isActive()) {
                lore.add("");
                lore.add("&a&lSELECTED");
            } else {
                lore.add("");
                lore.add("&6&lRight Click &fto select this perk.");
            }
        } else {
            if (user.getStat(Stats.COINS).getAsInt() >= baseCost) {
                lore.add(CenturionsUtils.color("&e&oAvailable"));
                lore.add("");
                lore.add("&6&lLeft Click &fto purchase for " + getBaseCost() + ".");
            } else {
                lore.add(CenturionsUtils.color("&c&oLocked"));
                lore.add("&dYou do not have enough coins to purchase this perk.");
                lore.add("&bThis perk costs " + baseCost);
            }
        }
        
        itemBuilder.setLore(lore);
        return itemBuilder.build();
    }
    
    public int compareTo(Perk o) {
        return this.getName().compareTo(o.getName());
    }
    
    public enum PerkCategory {
        KILL(Material.DIAMOND_SWORD), OTHER(Material.BEDROCK);
        private Material iconMaterial;
        PerkCategory(Material iconMaterial) {
            this.iconMaterial = iconMaterial;
        }
        
        public ItemStack getIcon() {
            return ItemBuilder.start(iconMaterial).setDisplayName("&d" + Utils.capitalizeEveryWord(name() + " Perks")).build();
        }
    }
}