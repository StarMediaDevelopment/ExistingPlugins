package net.firecraftmc.hungergames.game.team;

import net.firecraftmc.hungergames.game.death.DeathInfo;
import net.firecraftmc.hungergames.game.death.DeathInfoPlayerKill;
import net.firecraftmc.hungergames.game.enums.PlayerType;
import net.firecraftmc.hungergames.game.Game;
import net.firecraftmc.hungergames.game.GamePlayer;
import net.firecraftmc.maniacore.api.CenturionsCore;
import net.firecraftmc.maniacore.api.user.User;
import net.firecraftmc.maniacore.spigot.util.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class SpectatorsTeam extends GameTeam {
    public SpectatorsTeam(Game game) {
        super("Spectators", "&c", PlayerType.SPECTATOR, game);
        setGameMode(GameMode.ADVENTURE);
        this.permissons.put(Perms.ALWAYS_MAX_FOOD, true);
    }
    
    protected SpectatorsTeam(String name, String color, PlayerType playerType, Game game) {
        super(name, color, playerType, game);
        setGameMode(GameMode.ADVENTURE);
    }
    
    public void join(UUID uuid) {
        game.addPlayer(uuid);
        Player player = Bukkit.getPlayer(uuid);
        setPlayerStats(player, false, true, true);
        ItemStack tributesBook = ItemBuilder.start(Material.ENCHANTED_BOOK).setDisplayName("&a&lTributes &7&o(Right Click)").build();
        ItemStack spectatorsBook = ItemBuilder.start(Material.ENCHANTED_BOOK).setDisplayName("&c&lSpectators &7&o(Right Click)").build();
        ItemStack mutationsBook = ItemBuilder.start(Material.ENCHANTED_BOOK).setDisplayName("&d&lMutations &7&o(Right Click)").build();
        if (playerType == PlayerType.SPECTATOR) {
            String revengeName;
            GamePlayer gamePlayer = game.getPlayer(player.getUniqueId());
            DeathInfo deathInfo = gamePlayer.getDeathInfo();
            if (deathInfo instanceof DeathInfoPlayerKill) {
                DeathInfoPlayerKill playerKill = (DeathInfoPlayerKill) deathInfo;
                if (!gamePlayer.hasMutated() && !playerKill.isMutationKill()) {
                    revengeName = "&c&lTAKE REVENGE &8|| &eTarget: &b" + game.getPlayer(playerKill.getKiller()).getUser().getName();
                } else {
                    revengeName = "&c&lNO ONE TO TAKE REVENGE ON!";
                }
            } else {
                revengeName = "&c&lNO ONE TO TAKE REVENGE ON!";
            }
            ItemStack mutateItem = ItemBuilder.start(Material.ROTTEN_FLESH).setDisplayName(revengeName).build();
            if (game.getGameSettings().isMutations()) {
                player.getInventory().setItem(3, mutateItem);
            }
        }
        
        player.getInventory().setItem(0, tributesBook);
        player.getInventory().setItem(1, spectatorsBook);
        player.getInventory().setItem(2, mutationsBook);
        
        this.members.add(uuid);
        User user = CenturionsCore.getInstance().getUserManager().getUser(uuid);
        user.sendMessage(getJoinMessage());
        user.sendMessage("&6&l>> &eYou are now spectating the game!");
        
        Set<UUID> players = new HashSet<>(game.getSpectatorsTeam().getMembers());
        players.addAll(game.getTributesTeam().getMembers());
        players.addAll(game.getMutationsTeam().getMembers());
        players.addAll(game.getHiddenStaffTeam().getMembers());
        for (UUID u : players) {
            Bukkit.getPlayer(u).hidePlayer(player);
        }
        
        for (UUID p : game.getTributesTeam()) {
            player.showPlayer(Bukkit.getPlayer(p));
        }
    }
}