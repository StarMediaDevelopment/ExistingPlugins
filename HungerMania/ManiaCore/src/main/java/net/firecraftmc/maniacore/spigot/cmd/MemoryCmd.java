package net.firecraftmc.maniacore.spigot.cmd;

import net.firecraftmc.maniacore.api.CenturionsCore;
import net.firecraftmc.maniacore.api.ranks.Rank;
import net.firecraftmc.maniacore.api.util.CenturionsUtils;
import net.firecraftmc.maniacore.api.util.ReflectionUtils;
import net.firecraftmc.maniacore.memory.MemoryHook;
import net.firecraftmc.maniacore.plugin.CenturionsPlugin;
import net.firecraftmc.maniacore.spigot.util.SpigotUtils;
import net.firecraftmc.manialib.util.Constants;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_8_R3.CraftServer;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class MemoryCmd implements CommandExecutor {
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        Rank senderRank = SpigotUtils.getRankFromSender(sender);
        if (senderRank.ordinal() <= Rank.HELPER.ordinal()) {
            sender.sendMessage(CenturionsUtils.color("&cYou are not allowed to use that command."));
            return true;
        }
    
        double[] recentTps = ((CraftServer) Bukkit.getServer()).getServer().recentTps;
        sender.sendMessage(CenturionsUtils.color("&6&l>> &dHunger Mania &bserver performance: &7&o" + CenturionsCore.getInstance().getServerManager().getCurrentServer().getName()));
        sender.sendMessage(CenturionsUtils.color("&6&l>> &5Minecraft Vanilla Performance:"));
        StringBuilder tpsString = new StringBuilder("&7TPS (1m, 5m, 15m): ");
        for (int i = 0; i < recentTps.length; i++) {
            tpsString.append("&a").append(Constants.NUMBER_FORMAT.format(recentTps[i]));
            if (i < (recentTps.length - 1)) {
                tpsString.append("&7, ");
            }
        }
        sender.sendMessage(CenturionsUtils.color("&6&l> " + tpsString));
    
        int loadedChunks = 0, totalWorldsLoaded = 0;
        for (World world : Bukkit.getWorlds()) {
            Chunk[] worldLoadedChunks = world.getLoadedChunks();
            if (worldLoadedChunks != null) {
                totalWorldsLoaded++;
                loadedChunks += worldLoadedChunks.length;
            }
        }
    
        sender.sendMessage(CenturionsUtils.color("&6&l> &7Loaded Chunks: &e" + loadedChunks + " &8&o(From " + totalWorldsLoaded + " worlds)"));
    
        NumberFormat format = new DecimalFormat("##0.#");
        sender.sendMessage(CenturionsUtils.color("&6&l>> &5Hunger Mania Performance:"));
        for (MemoryHook memoryHook : CenturionsCore.getInstance().getMemoryManager().getMemoryHooks()) {
            int total = 0, count = 0;
            for (int run : memoryHook.getRecentRuns()) {
                if (run != 0) {
                    total += run;
                    count++;
                }
            }
    
            double average = total / (count * 1.0);
            sender.sendMessage(CenturionsUtils.color("&6&l>> &7" + memoryHook.getName() + " &bHighest: " + memoryHook.getHighest() + " &eAverage: " + format.format(average)));
        }
    
        sender.sendMessage(CenturionsUtils.color("&6&l>> &5Hunger Mania Version Information"));
        sender.sendMessage(CenturionsUtils.color("&6&l>> &7Java Version: &b" + System.getProperty("java.version")));
        sender.sendMessage(CenturionsUtils.color("&6&l>> &7Minecraft Version: &b" + ReflectionUtils.getVersion()));
        sender.sendMessage(CenturionsUtils.color("&6&l>> &7Spigot Version: &b" + Bukkit.getVersion()));
        for (CenturionsPlugin centurionsPlugin : CenturionsCore.getInstance().getMemoryManager().getManiaPlugins()) {
            sender.sendMessage(CenturionsUtils.color("&6&l>> &7" + centurionsPlugin.getName() + ": &bv" + centurionsPlugin.getVersion()));
        }
    
        sender.sendMessage(CenturionsUtils.color("&6&l>> &5Java Heap Performance"));
        long totalMemory = Runtime.getRuntime().totalMemory();
        long freeMemory = Runtime.getRuntime().freeMemory();
        long usedMemory = totalMemory - freeMemory;
        double usedPercent = (usedMemory / (totalMemory * 1.0)) * 100;
        sender.sendMessage(CenturionsUtils.color("&6&l>> &7Memory:   &b" + (usedMemory / 1048576)) + " / " + (totalMemory / 1048576) + " MB " + ChatColor.DARK_GRAY + "" + ChatColor.ITALIC + " (" + format.format(usedPercent) + "% Used, " + (freeMemory / 1048576) + " Free)");
        return true;
    }
}