package net.firecraftmc.hungergames.game;

import com.mojang.authlib.GameProfile;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import net.firecraftmc.hungergames.game.death.DeathInfo;
import net.firecraftmc.hungergames.game.team.GameTeam;
import net.firecraftmc.maniacore.api.CenturionsCore;
import net.firecraftmc.maniacore.api.util.CenturionsUtils;
import net.firecraftmc.maniacore.spigot.mutations.MutationType;
import net.firecraftmc.maniacore.spigot.user.SpigotUser;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Field;
import java.util.UUID;

@Getter
public class GamePlayer {
    private Game game;
    private SpigotUser user;
    private boolean forcefullyAdded;
    private CommandSender forcefullyAddedActor;
    @Setter private boolean hasMutated = false;
    @Getter(value = AccessLevel.NONE) @Setter(value = AccessLevel.NONE) private boolean hasSponsored = false;
    @Setter private UUID mutationTarget;
    @Setter private MutationType mutationType;
    @Setter private int killStreak;
    private boolean revived;
    private CommandSender revivedActor;
    @Setter private boolean spectatorByDeath = false;
    private ItemStack skull;
    @Setter private DeathInfo deathInfo = null;
    @Setter private boolean isMutating = false;
    @Setter private int kills;
    @Setter private int earnedCoins = 0;
    @Setter private long revengeTime = 0;
    
    public GamePlayer(Game game, UUID uuid) {
        this.game = game;
        this.user = (SpigotUser) CenturionsCore.getInstance().getUserManager().getUser(uuid); 
        this.skull = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
        SkullMeta skullMeta = ((SkullMeta) skull.getItemMeta());
        String playerName = Bukkit.getPlayer(uuid).getName();
        Player player = user.getBukkitPlayer();
        GameProfile mcProfile = ((CraftPlayer) player).getProfile();
        try {
            Field field = skullMeta.getClass().getDeclaredField("profile");
            field.setAccessible(true);
            field.set(skullMeta, mcProfile);
        } catch (Exception e) {}
        skullMeta.setDisplayName(CenturionsUtils.color("&f" + playerName));
        skull.setItemMeta(skullMeta);
    }
    
    public UUID getUniqueId() {
        return user.getUniqueId();
    }
    
    public boolean hasSponsored() {
        return hasSponsored;
    }
    
    public void setSponsored(boolean value) {
        this.hasSponsored = value;
    }
    
    public boolean hasMutated() {
        return hasMutated;
    }
    
    public void setForcefullAddedInfo(boolean value, CommandSender sender) {
        this.forcefullyAdded = value;
        this.forcefullyAddedActor = sender;
    }
    
    public void setRevivedInfo(boolean value, CommandSender sender) {
        this.revived = value;
        this.revivedActor = sender;
    }

    public void sendMessage(String s) {
        user.sendMessage(s);
    }

    public GameTeam getTeam() {
        return game.getGameTeam(getUniqueId());
    }
}
