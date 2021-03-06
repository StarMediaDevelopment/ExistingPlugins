package net.firecraftmc.hungergames.listeners;

import net.firecraftmc.hungergames.HungerGames;
import net.firecraftmc.hungergames.game.gui.SpectatorInventoryGui;
import net.firecraftmc.maniacore.spigot.gui.GUIButton;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.scheduler.BukkitRunnable;

public class InventoryListeners implements Listener {
    
    @EventHandler
    public void onInventoryClick(InventoryInteractEvent e) {
        Player player = (Player) e.getWhoClicked();
        new BukkitRunnable() {
            public void run() {
                for (Inventory guiInstance : SpectatorInventoryGui.getGuiInstances()) {
                    SpectatorInventoryGui gui = (SpectatorInventoryGui) guiInstance.getHolder();
                    if (gui.getTarget().getUniqueId().equals(player.getUniqueId())) {
                        for (int i = SpectatorInventoryGui.OFFSET; i < 54; i++) {
                            GUIButton button = gui.getButton(i);
                            button.setItem(e.getInventory().getItem(i - SpectatorInventoryGui.OFFSET));
                            guiInstance.setItem(i - SpectatorInventoryGui.OFFSET, button.getItem());
                        }
                    }
                }
            }
        }.runTaskLater(HungerGames.getInstance(), 1L);
    }
    
    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e) {
        if (e.getInventory().getHolder() instanceof SpectatorInventoryGui) {
            SpectatorInventoryGui.getGuiInstances().remove(e.getInventory());
        }
    }
}
