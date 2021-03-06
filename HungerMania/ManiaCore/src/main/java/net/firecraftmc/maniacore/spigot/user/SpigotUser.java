package net.firecraftmc.maniacore.spigot.user;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import lombok.Getter;
import lombok.Setter;
import net.firecraftmc.maniacore.api.CenturionsCore;
import net.firecraftmc.maniacore.spigot.perks.Perk;
import net.firecraftmc.maniacore.spigot.perks.PerkInfo;
import net.firecraftmc.maniacore.spigot.perks.PerkInfoRecord;
import net.firecraftmc.maniacore.spigot.perks.Perks;
import net.firecraftmc.maniacore.api.channel.Channel;
import net.firecraftmc.maniacore.api.ranks.RankInfo;
import net.firecraftmc.maniacore.api.records.NicknameRecord;
import net.firecraftmc.maniacore.api.skin.Skin;
import net.firecraftmc.maniacore.api.user.User;
import net.firecraftmc.maniacore.api.util.CenturionsUtils;
import net.firecraftmc.manialib.sql.IRecord;
import net.md_5.bungee.api.chat.BaseComponent;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.util.*;

import static net.minecraft.server.v1_8_R3.PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER;
import static net.minecraft.server.v1_8_R3.PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER;

@Getter @Setter
public class SpigotUser extends User {
    
    private PlayerBoard scoreboard;
    private Map<String, net.firecraftmc.maniacore.spigot.perks.PerkInfo> perks = new HashMap<>();
    
    public SpigotUser(UUID uniqueId) {
        super(uniqueId);
    }
    
    public SpigotUser(UUID uniqueId, String name) {
        super(uniqueId, name);
    }
    
    public SpigotUser(Map<String, String> jedisData) {
        super(jedisData);
    }
    
    public SpigotUser(User user) {
        this(user.getId(), user.getUniqueId(), user.getName(), user.getRankInfo(), user.getChannel());
    }
    
    public SpigotUser(int id, UUID uniqueId, String name, RankInfo rank, Channel channel) {
        super(id, uniqueId, name, rank, channel);
    }
    
    public void sendMessage(BaseComponent baseComponent) {
        getBukkitPlayer().spigot().sendMessage(baseComponent);
    }
    
    public void sendMessage(String s) {
        Player player = getBukkitPlayer();
        if (player != null) {
            player.sendMessage(CenturionsUtils.color(s));
        }
    }
    
    public Player getBukkitPlayer() {
        return Bukkit.getPlayer(this.uniqueId);
    }
    
    public boolean hasPermission(String permission) {
        Player player = getBukkitPlayer();
        if (player != null) {
            if (permission == null || permission.equals("")) {
                return true;
            }
            
            return player.hasPermission(permission);
        }
        return false;
    }
    
    public boolean isOnline() {
        return getBukkitPlayer() != null;
    }
    
    public void resetNickname() {
        applySkinAndName(getSkin(), getName());
        super.resetNickname();
    }
    
    private void applySkinAndName(Skin skin, String name) {
        CraftPlayer craftPlayer = (CraftPlayer) this.getBukkitPlayer();
        EntityPlayer entityPlayer = craftPlayer.getHandle();
        GameProfile gameProfile = entityPlayer.getProfile();
        skin.updateValues();
        
        if (!(skin.getValue() == null || skin.getSignature() == null)) {
            PropertyMap properties = gameProfile.getProperties();
            properties.clear();
            properties.put("textures", new Property("textures", skin.getValue(), skin.getSignature()));
        }
        try {
            Field nameField = gameProfile.getClass().getDeclaredField("name");
            nameField.setAccessible(true);
            nameField.set(gameProfile, name);
        } catch (Exception e) {
            e.printStackTrace();
        }

        int dim = entityPlayer.getWorld().worldProvider.getDimension();
        EnumDifficulty diff = entityPlayer.getWorld().getDifficulty();
        WorldType type = entityPlayer.getWorld().worldData.getType();
        WorldSettings.EnumGamemode gamemode = WorldSettings.EnumGamemode.valueOf(craftPlayer.getGameMode().name());
        Location location = craftPlayer.getLocation().clone();
        PacketPlayOutPlayerInfo removePlayer = new PacketPlayOutPlayerInfo(REMOVE_PLAYER, entityPlayer);
        PacketPlayOutPlayerInfo addPlayer = new PacketPlayOutPlayerInfo(ADD_PLAYER, entityPlayer);
        PacketPlayOutRespawn respawn = new PacketPlayOutRespawn(dim, diff, type, gamemode);
        entityPlayer.playerConnection.sendPacket(removePlayer);
        entityPlayer.playerConnection.sendPacket(respawn);
        craftPlayer.teleport(location);
        entityPlayer.playerConnection.sendPacket(addPlayer);

        List<Player> canSee = new ArrayList<>();
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.canSee(getBukkitPlayer())) {
                canSee.add(player);
                player.hidePlayer(getBukkitPlayer());
            }
        }

        for (Player player : canSee) {
            player.showPlayer(getBukkitPlayer());
        }
        
        CenturionsCore.getInstance().getPlugin().runTaskAsynchronously(() -> new NicknameRecord(nickname).push(CenturionsCore.getInstance().getDatabase()));
    }

    public void applyNickname() {
        if (nickname.isActive()) {
            applySkinAndName(CenturionsCore.getInstance().getSkinManager().getSkin(nickname.getSkinUUID()), nickname.getName());
        }
        super.applyNickname();
    }

    public void loadPerks() {
        List<IRecord> records = CenturionsCore.getInstance().getDatabase().getRecords(net.firecraftmc.maniacore.spigot.perks.PerkInfoRecord.class, "uuid", this.uniqueId.toString());
        for (IRecord record : records) {
            if (record instanceof net.firecraftmc.maniacore.spigot.perks.PerkInfoRecord) {
                net.firecraftmc.maniacore.spigot.perks.PerkInfoRecord perkInfoRecord = (PerkInfoRecord) record;
                addPerkInfo(perkInfoRecord.toObject());
            }
        }
        
        for (net.firecraftmc.maniacore.spigot.perks.Perk perk : Perks.PERKS) {
            if (!this.perks.containsKey(perk.getName())) {
                net.firecraftmc.maniacore.spigot.perks.PerkInfo value = perk.create(this.uniqueId);
                this.perks.put(perk.getName(), value);
            }
        }
    }
    
    public net.firecraftmc.maniacore.spigot.perks.PerkInfo getPerkInfo(net.firecraftmc.maniacore.spigot.perks.Perk perk) {
        return perks.get(perk.getName());
    }
    
    public void addPerk(Perk perk) {
        this.perks.put(perk.getName(), perk.create(this.uniqueId));
    }
    
    public void addPerkInfo(net.firecraftmc.maniacore.spigot.perks.PerkInfo perkInfo) {
        this.perks.put(perkInfo.getName(), perkInfo);
    }
    
    public Collection<PerkInfo> getPerks() {
        return this.perks.values();
    }
}
