package net.firecraftmc.maniacore.spigot.communication;

import net.firecraftmc.maniacore.CenturionsCorePlugin;
import net.firecraftmc.maniacore.api.CenturionsCore;
import net.firecraftmc.maniacore.api.channel.Channel;
import net.firecraftmc.maniacore.api.chat.ChatFormatter;
import net.firecraftmc.maniacore.api.communication.MessageHandler;
import net.firecraftmc.maniacore.api.ranks.Rank;
import net.firecraftmc.maniacore.api.user.User;
import net.firecraftmc.maniacore.api.user.toggle.Toggles;
import net.firecraftmc.maniacore.api.util.CenturionsUtils;
import net.firecraftmc.maniacore.spigot.user.SpigotUser;
import net.firecraftmc.maniacore.spigot.util.SpartanUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

@SuppressWarnings("DuplicatedCode")
public class SpigotMessageHandler extends MessageHandler {
    
    private CenturionsCorePlugin plugin;
    
    public SpigotMessageHandler(CenturionsCorePlugin plugin) {
        this.plugin = plugin;
    }
    
    protected void handleStaffChat(UUID p, String message) {
        if (Bukkit.getOnlinePlayers().isEmpty()) return;
        ChatFormatter chatFormatter = CenturionsCore.getInstance().getChatManager().getChatFormatter(Channel.STAFF);
        User sender = CenturionsCore.getInstance().getUserManager().getUser(p);
        sender.setChannel(Channel.STAFF);
        for (Player player : Bukkit.getOnlinePlayers()) {
            User user = CenturionsCore.getInstance().getUserManager().getUser(player.getUniqueId());
            if (user.hasPermission(Rank.HELPER)) {
                if (user.getToggle(Toggles.STAFF_NOTIFICATIONS).getAsBoolean()) {
                    player.sendMessage(chatFormatter.format(sender, message));
                }
            }
        }
        sender.setChannel(Channel.GLOBAL);
    }
    
    protected void handleAdminChat(UUID p, String message) {
        if (Bukkit.getOnlinePlayers().isEmpty()) return;
        ChatFormatter chatFormatter = CenturionsCore.getInstance().getChatManager().getChatFormatter(Channel.ADMIN);
        User sender = CenturionsCore.getInstance().getUserManager().getUser(p);
        sender.setChannel(Channel.ADMIN);
        for (Player player : Bukkit.getOnlinePlayers()) {
            User user = CenturionsCore.getInstance().getUserManager().getUser(player.getUniqueId());
            if (user.hasPermission(Rank.ADMIN)) {
                if (user.getToggle(Toggles.ADMIN_NOTIFICATIONS).getAsBoolean()) {
                    player.sendMessage(chatFormatter.format(sender, message));
                }
            }
        }
        sender.setChannel(Channel.GLOBAL);
    }
    
    protected void handleSpartanMsg(String server, String playerName, String hack, int violation, boolean falsePositive, double tps, int ping) {
        if (Bukkit.getOnlinePlayers().isEmpty()) return;
        SpartanUtils.sendSpartanMessage(server, playerName, hack, violation, falsePositive, tps, ping);
    }
    
    protected void handleServerSwitch(UUID p, String server) {
        if (Bukkit.getOnlinePlayers().isEmpty()) return;
        SpigotUser user = (SpigotUser) plugin.getCenturionsCore().getUserManager().getUser(p);
        if (user.hasPermission(Rank.MEDIA)) {
            String format = CenturionsUtils.color(Channel.STAFF.getChatPrefix() + user.getColoredName() + " &7&l-> &6" + server);
            for (Player player : Bukkit.getOnlinePlayers()) {
                User u = CenturionsCore.getInstance().getUserManager().getUser(player.getUniqueId());
                if (u.hasPermission(Rank.HELPER)) {
                    if (u.getToggle(Toggles.STAFF_NOTIFICATIONS).getAsBoolean()) {
                        player.sendMessage(format);
                    }
                }
            }
        }
    }
    
    protected void handleNetworkLeave(UUID p) {
        if (Bukkit.getOnlinePlayers().isEmpty()) return;
        SpigotUser user = new SpigotUser(plugin.getCenturionsCore().getUserManager().getUser(p));
        if (user.hasPermission(Rank.MEDIA)) {
            String format = CenturionsUtils.color(Channel.STAFF.getChatPrefix() + user.getColoredName() + " &6left the network.");
            for (Player player : Bukkit.getOnlinePlayers()) {
                User u = CenturionsCore.getInstance().getUserManager().getUser(player.getUniqueId());
                if (u.hasPermission(Rank.HELPER)) {
                    if (u.getToggle(Toggles.STAFF_NOTIFICATIONS).getAsBoolean()) {
                        player.sendMessage(format);
                    }
                }
            }
        }
    }
}