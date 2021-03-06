package net.firecraftmc.maniacore.spigot.perks.impl;

import net.firecraftmc.maniacore.spigot.perks.TieredPerk;
import net.firecraftmc.maniacore.api.user.User;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class SharpshooterPerk extends TieredPerk {
    public SharpshooterPerk() {
        super("Sharpshooter", 1500, 100, Material.BOW, PerkCategory.KILL, "You have a chance to get your arrow back \nfter shooting someone with a bow.");
        
        this.tiers.put(1, new Tier(1, getBaseCost(), 10, "10% chance to get your arrow back.") {
            public boolean activate(User user) {
                Player player = Bukkit.getPlayer(user.getUniqueId());
                if (player == null) return false;
                player.getInventory().addItem(new ItemStack(Material.ARROW));
                return true;
            }
        });
    
        this.tiers.put(2, new Tier(2, 3000, 25, "25% chance to get your arrow back.") {
            public boolean activate(User user) {
                Player player = Bukkit.getPlayer(user.getUniqueId());
                if (player == null) return false;
                player.getInventory().addItem(new ItemStack(Material.ARROW));
                return true;
            }
        });
    }
}
