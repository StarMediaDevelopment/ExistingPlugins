package net.firecraftmc.hungergames.game.gui;

import net.firecraftmc.hungergames.HungerGames;
import net.firecraftmc.hungergames.game.Game;
import net.firecraftmc.hungergames.game.GamePlayer;
import net.firecraftmc.maniacore.api.CenturionsCore;
import net.firecraftmc.maniacore.api.stats.Stats;
import net.firecraftmc.maniacore.api.user.User;
import net.firecraftmc.maniacore.api.util.CenturionsUtils;
import net.firecraftmc.maniacore.spigot.gui.GUIButton;
import net.firecraftmc.maniacore.spigot.gui.Gui;
import net.firecraftmc.maniacore.spigot.mutations.Mutation;
import net.firecraftmc.maniacore.spigot.mutations.MutationStatus;
import net.firecraftmc.maniacore.spigot.mutations.MutationType;
import net.firecraftmc.maniacore.spigot.mutations.Mutations;
import net.firecraftmc.maniacore.spigot.util.ItemBuilder;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Material;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;

public class MutateGui extends Gui {
    public MutateGui(UUID mutator, UUID target) {
        super(HungerGames.getInstance(), "Mutate as...", false, 27);
        
        GUIButton unlockedWool = new GUIButton(ItemBuilder.start(Material.WOOL, 1, (short) 13).setDisplayName("&a&lAvailable").build());
        GUIButton purchasableWool = new GUIButton(ItemBuilder.start(Material.WOOL, 1, (short) 4).setDisplayName("&e&lPurchasable").build());
        GUIButton lockedWool = new GUIButton(ItemBuilder.start(Material.WOOL, 1, (short) 14).setDisplayName("&c&lLocked").build());
        
        GUIButton unlockedPane = new GUIButton(ItemBuilder.start(Material.STAINED_GLASS_PANE, 1, (short) 5).setDisplayName("&a").build());
        GUIButton purchasablePane = new GUIButton(ItemBuilder.start(Material.STAINED_GLASS_PANE, 1, (short) 4).setDisplayName("&e").build());
        GUIButton lockedPane = new GUIButton(ItemBuilder.start(Material.STAINED_GLASS_PANE, 1, (short) 14).setDisplayName("&c").build());
        
        setButton(0, unlockedWool);
        setButton(1, unlockedPane);
        setButton(9, purchasableWool);
        setButton(10, purchasablePane);
        setButton(18, lockedWool);
        setButton(19, lockedPane);
        
        Mutation[] mutations = Mutations.MUTATIONS.values().toArray(new Mutation[0]);
        //ItemStack[] mutationStacks = new ItemStack[mutations.length];
        Map<MutationStatus, Map<MutationType, ItemStack>> mutationStacks = new HashMap<>();
        Game game = HungerGames.getInstance().getGameManager().getCurrentGame();
        if (game == null) { return; }
        GamePlayer gamePlayer = game.getPlayer(mutator);
        String[] rawUnlocked = gamePlayer.getUser().getStat(Stats.HG_UNLOCKED_MUTATIONS).getAsString().split(";");
        Set<MutationType> unlockedTypes = new HashSet<>();
        for (String s : rawUnlocked) {
            unlockedTypes.add(MutationType.valueOf(s.toUpperCase()));
        }
        for (Mutation mutation : mutations) {
            MutationStatus status;
            
            if (unlockedTypes.contains(mutation.getType())) {
                status = MutationStatus.AVAILABLE;
            } else {
                if (gamePlayer.getUser().getStat(Stats.COINS).getAsInt() >= mutation.getUnlockCost()) {
                    status = MutationStatus.PURCHASABLE;
                } else {
                    status = MutationStatus.LOCKED;
                }
            }
            
            String availableLine = "";
            switch (status) {
                case AVAILABLE:
                    availableLine = "&a&oAvailable";
                    break;
                case PURCHASABLE:
                    availableLine = "&e&oPurchasable";
                    break;
                case LOCKED:
                    availableLine = "&c&oLocked";
                    break;
            }
            
            ItemBuilder itemBuilder = ItemBuilder.start(mutation.getIcon()).setDisplayName("&a&l" + mutation.getName().toUpperCase()).withLore(availableLine, "", "&2&lBuffs&a&l:");
            for (String buff : mutation.getBuffs()) {
                itemBuilder.addLoreLine("&8- &a" + buff);
            }
            itemBuilder.addLoreLine("&4&lDEBUFFS&c:");
            for (String debuff : mutation.getDebuffs()) {
                itemBuilder.addLoreLine("&8- &c" + debuff);
            }
            itemBuilder.addLoreLine("").addLoreLine("&7Max HP: &e" + mutation.getMaxHP()).addLoreLine("&7Defense: &e" + mutation.getDefenseType().name()).addLoreLine("");
            switch (status) {
                case AVAILABLE:
                    itemBuilder.addLoreLine("&6&lLeft Click &7to use &a" + mutation.getName() + " &efor &a" + mutation.getUseCost() + " &fcoins.");
                    break;
                case PURCHASABLE:
                    itemBuilder.addLoreLine("&6&lRight Click &fto purchase &a" + mutation.getName() + " &ffor &e" + mutation.getUnlockCost() + " &fcoins.");
                    break;
                case LOCKED:
                    break;
            }
            if (mutationStacks.containsKey(status)) {
                mutationStacks.get(status).put(mutation.getType(), itemBuilder.build());
            } else {
                mutationStacks.put(status, new HashMap<MutationType, ItemStack>() {{
                    put(mutation.getType(), itemBuilder.build());
                }});
            }
        }
        
        AtomicInteger availableCounter = new AtomicInteger(2), purchasableCounter = new AtomicInteger(11), lockedCounter = new AtomicInteger(20);
        mutationStacks.forEach((status, items) -> {
            if (status == MutationStatus.AVAILABLE) {
                for (Entry<MutationType, ItemStack> entry : items.entrySet()) {
                    setButton(availableCounter.get(), new GUIButton(entry.getValue()).setListener((e) -> {
                        User user = CenturionsCore.getInstance().getUserManager().getUser(e.getWhoClicked().getUniqueId());
                        if (user.getStat(Stats.COINS).getAsInt() < Mutations.MUTATIONS.get(entry.getKey()).getUseCost()) {
                            e.getWhoClicked().closeInventory();
                            e.getWhoClicked().sendMessage(CenturionsUtils.color("&cYou do not have enough coins to use that mutation."));
                            return;
                        }
                        game.mutatePlayer(e.getWhoClicked().getUniqueId(), Mutations.MUTATIONS.get(entry.getKey()), target);
                        e.getWhoClicked().closeInventory();
                    }));
                    availableCounter.getAndIncrement();
                }
            } else if (status == MutationStatus.PURCHASABLE) {
                for (Entry<MutationType, ItemStack> entry : items.entrySet()) {
                    setButton(purchasableCounter.get(), new GUIButton(entry.getValue()).setListener(e -> {
                        if (e.getClick() == ClickType.RIGHT) {
                            User user = CenturionsCore.getInstance().getUserManager().getUser(e.getWhoClicked().getUniqueId());
                            Mutation mutation = Mutations.MUTATIONS.get(entry.getKey());
                            if (user.getStat(Stats.COINS).getAsInt() >= mutation.getUnlockCost()) {
                                user.getStat(Stats.COINS).setValue((user.getStat(Stats.COINS).getAsInt() - mutation.getUnlockCost()) + "");
                                unlockedTypes.add(entry.getKey());
                                user.setStat(Stats.HG_UNLOCKED_MUTATIONS, StringUtils.join(unlockedTypes, ";"));
                                e.getWhoClicked().closeInventory();
                                new MutateGui(mutator, target).openGUI(e.getWhoClicked());
                            } else {
                                user.sendMessage("&cYou do not have enough funds to purchase that mutation type.");
                            }
                        }
                    }));
                    purchasableCounter.getAndIncrement();
                }
            } else if (status == MutationStatus.LOCKED) {
                for (Entry<MutationType, ItemStack> entry : items.entrySet()) {
                    setButton(lockedCounter.get(), new GUIButton(entry.getValue()));
                    lockedCounter.getAndIncrement();
                }
            }
        });
    }
}
