package net.firecraftmc.hub;

import net.firecraftmc.maniacore.spigot.gui.GUIButton;
import net.firecraftmc.maniacore.spigot.gui.Gui;
import net.firecraftmc.maniacore.spigot.util.ItemBuilder;
import org.bukkit.Material;

public class GameBrowserGui extends Gui {
    public GameBrowserGui(CenturionsHub plugin) {
        super(plugin, "&6&lCenturions &r- Where to?", false, 9);
    
        GUIButton hungerGamesButton = new GUIButton(ItemBuilder.start(Material.DIAMOND_SWORD).setDisplayName("&6&lHUNGER GAMES").build());
        hungerGamesButton.setListener(e -> new HungerGamesGui(plugin).openGUI(e.getWhoClicked()));
        setButton(4, hungerGamesButton);
    }
}
