package net.firecraftmc.hungergames.lobby;

import cloud.timo.TimoCloud.api.TimoCloudAPI;
import net.firecraftmc.hungergames.HungerGames;
import net.firecraftmc.maniacore.spigot.user.SpigotUser;
import net.firecraftmc.maniacore.spigot.util.SpigotUtils;
import org.bukkit.Sound;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.concurrent.TimeUnit;

@SuppressWarnings("DuplicatedCode")
public class VoteTimer extends BukkitRunnable {
    
    private Lobby lobby;
    private int totalSeconds;
    private long timerStart = 0;
    private Set<Integer> announced = new HashSet<>();
    private Set<Integer> ANNOUNCE_SECONDS = new HashSet<>(Arrays.asList(60, 45, 30, 15, 10, 3, 2, 1));
    boolean forceStarted = false;
    
    public VoteTimer(Lobby lobby, int seconds) {
        this.lobby = lobby;
        this.totalSeconds = seconds;
    }
    
    public void run() {
        int remainingSeconds = getRemainingSeconds();
        
        if (remainingSeconds == 0) {
            cancel();
            lobby.startGame();
        } else {
            TimoCloudAPI.getBukkitAPI().getThisServer().setExtra("map:Undecided;time:" + remainingSeconds + "s");
            if (!this.announced.contains(remainingSeconds)) {
                List<SpigotUser> players = new ArrayList<>(lobby.getPlayers());
                players.addAll(lobby.getHiddenStaff());
                for (SpigotUser player : players) {
                    SpigotUtils.sendActionBar(player.getBukkitPlayer(), "&aThe game begins in " + remainingSeconds + " seconds!");
                }
                if (ANNOUNCE_SECONDS.contains(remainingSeconds)) {
                    for (SpigotUser player : players) {
                        player.getBukkitPlayer().playSound(player.getBukkitPlayer().getLocation(), Sound.CLICK, 1F, 1F);
                    }
                    lobby.sendMessage("&e&lVoting closes in &b&l" + remainingSeconds + " &e&lseconds.");
                    announced.add(remainingSeconds);
                }
            }
        }
    }
    
    public int getRemainingSeconds() {
        long timerEnd = timerStart + TimeUnit.SECONDS.toMillis(totalSeconds);
        long remainingRaw = timerEnd - System.currentTimeMillis();
        return (int) TimeUnit.MILLISECONDS.toSeconds(remainingRaw);
    }
    
    public void start() {
        timerStart = System.currentTimeMillis();
        this.runTaskTimer(HungerGames.getInstance(), 0L, 1L);
    }
    
    public boolean isForceStarted() {
        return forceStarted;
    }
    
    public void setForceStarted(boolean forceStarted) {
        this.forceStarted = forceStarted;
    }
}
