package net.firecraftmc.hungergames.game.death;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.firecraftmc.hungergames.HungerGames;
import net.firecraftmc.hungergames.game.Game;
import net.firecraftmc.maniacore.api.CenturionsCore;
import net.firecraftmc.maniacore.spigot.user.SpigotUser;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

@Getter @AllArgsConstructor
public class DeathInfo {
    protected UUID player;
    protected DeathType type;
    protected String deathMessage, teamColor;
    
    public DeathInfo(UUID player, DeathType type) {
        this.player = player;
        this.type = type;
        this.teamColor = HungerGames.getInstance().getGameManager().getCurrentGame().getGameTeam(player).getColor();
    }
    
    public String getDeathMessage(Game game) {
        String message = deathMessage;
        if (message != null) {
            SpigotUser user = (SpigotUser) CenturionsCore.getInstance().getUserManager().getUser(this.player);
            String name = "";
            if (user.getNickname().isActive()) {
                name = user.getNickname().getName();
            } else {
                name = user.getName();
            }
            message = message.replace("%playername%", teamColor + name);
        }
        return message;
    }
    
    public static String getKillerName(Game game, UUID killer) {
        String teamColor = game.getGameTeam(killer).getColor();
        return teamColor + game.getPlayer(killer).getUser().getName();
    }
    
    public static String getHandItem(ItemStack handItem) {
        String itemName;
        if (handItem != null && !handItem.getType().equals(Material.AIR)) {
            if (!handItem.hasItemMeta() || handItem.getItemMeta().getDisplayName() == null) {
                itemName = handItem.getType().name().toLowerCase().replace("_", " ");
            } else {
                itemName = handItem.getItemMeta().getDisplayName();
            }
        } else {
            itemName = "their fists";
        }
        return itemName;
    }
}