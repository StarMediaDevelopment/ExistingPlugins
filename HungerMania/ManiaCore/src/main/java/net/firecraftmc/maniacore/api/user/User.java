package net.firecraftmc.maniacore.api.user;

import lombok.Getter;
import lombok.Setter;
import net.firecraftmc.maniacore.api.channel.Channel;
import net.firecraftmc.maniacore.api.leveling.Level;
import net.firecraftmc.maniacore.api.nickname.Nickname;
import net.firecraftmc.maniacore.api.ranks.Rank;
import net.firecraftmc.maniacore.api.ranks.RankInfo;
import net.firecraftmc.maniacore.api.records.IgnoreInfoRecord;
import net.firecraftmc.maniacore.api.stats.Stat;
import net.firecraftmc.maniacore.api.stats.Statistic;
import net.firecraftmc.maniacore.api.stats.Stats;
import net.firecraftmc.maniacore.api.user.toggle.Toggle;
import net.firecraftmc.maniacore.api.user.toggle.Toggles;
import net.firecraftmc.maniacore.api.ManiaCore;
import net.firecraftmc.maniacore.api.skin.Skin;
import net.firecraftmc.manialib.data.model.IRecord;
import net.firecraftmc.manialib.util.Pair;
import net.md_5.bungee.api.chat.BaseComponent;

import java.util.*;

@Getter
@Setter
public class User implements IRecord {
    private static final UUID FIRESTAR311 = UUID.fromString("3f7891ce-5a73-4d52-a2ba-299839053fdc");
    private static final UUID ASSASSINPLAYS = UUID.fromString("c292df56-5baa-4a11-87a3-cba08ce5f7a6");
    protected static final Set<net.firecraftmc.maniacore.api.stats.Stats> FAKED_STATS = new HashSet<>(Arrays.asList(net.firecraftmc.maniacore.api.stats.Stats.EXPERIENCE, net.firecraftmc.maniacore.api.stats.Stats.COINS, net.firecraftmc.maniacore.api.stats.Stats.HG_CHESTS_FOUND, net.firecraftmc.maniacore.api.stats.Stats.HG_SCORE, net.firecraftmc.maniacore.api.stats.Stats.HG_KILLS, net.firecraftmc.maniacore.api.stats.Stats.HG_WINS, net.firecraftmc.maniacore.api.stats.Stats.HG_DEATHMATCHES, net.firecraftmc.maniacore.api.stats.Stats.HG_DEATHS, net.firecraftmc.maniacore.api.stats.Stats.HG_MUTANT_DEATHS, net.firecraftmc.maniacore.api.stats.Stats.HG_GAMES, net.firecraftmc.maniacore.api.stats.Stats.HG_HIGHEST_KILL_STREAK, net.firecraftmc.maniacore.api.stats.Stats.HG_MUTANT_KILLS, net.firecraftmc.maniacore.api.stats.Stats.HG_WINSTREAK));

    protected int id = 0;
    protected final UUID uniqueId;
    protected String name;
    protected net.firecraftmc.maniacore.api.channel.Channel channel = net.firecraftmc.maniacore.api.channel.Channel.GLOBAL; //TODO Type handler (Probably just a default enum handler
    protected Set<net.firecraftmc.maniacore.api.user.IgnoreInfo> ignoredPlayers = new HashSet<>(); //TODO Set type handlers
    protected net.firecraftmc.maniacore.api.nickname.Nickname nickname;
    protected final RankInfo rankInfo;

    protected Map<String, net.firecraftmc.maniacore.api.stats.Statistic> stats = new HashMap<>();
    protected Map<net.firecraftmc.maniacore.api.user.toggle.Toggles, net.firecraftmc.maniacore.api.user.toggle.Toggle> toggles = new HashMap<>();

    protected Map<String, net.firecraftmc.maniacore.api.stats.Statistic> fakeStats = new HashMap<>();

    public User(UUID uniqueId) {
        this.uniqueId = uniqueId;
        this.nickname = new net.firecraftmc.maniacore.api.nickname.Nickname(uniqueId);
        this.rankInfo = new RankInfo(uniqueId);
    }
    
    public void setRank(net.firecraftmc.maniacore.api.ranks.Rank rank, long expire) {
        if (getUniqueId().equals(FIRESTAR311)) {
            this.rankInfo.setRank(net.firecraftmc.maniacore.api.ranks.Rank.ROOT);
            this.rankInfo.setExpire(-1);
        } else if (getUniqueId().equals(ASSASSINPLAYS)) {
            this.rankInfo.setRank(net.firecraftmc.maniacore.api.ranks.Rank.OWNER);
            this.rankInfo.setExpire(-1);
        } else {
            this.rankInfo.setPreviousRank(this.rankInfo.getRank());
            this.rankInfo.setRank(rank);
            this.rankInfo.setExpire(expire);
        }
    }
    
    public void setRank(net.firecraftmc.maniacore.api.ranks.Rank rank) {
        setRank(rank, -1);
    }

    public String generateActionBar() {
        boolean nicked, vanished = false, incognito = false;
        nicked = getNickname().isActive();

        net.firecraftmc.maniacore.api.user.toggle.Toggle vanishedToggle = getToggle(net.firecraftmc.maniacore.api.user.toggle.Toggles.VANISHED);
        if (vanishedToggle != null) {
            vanished = vanishedToggle.getAsBoolean();
        }
        net.firecraftmc.maniacore.api.user.toggle.Toggle incognitoToggle = getToggle(net.firecraftmc.maniacore.api.user.toggle.Toggles.INCOGNITO);
        if (incognitoToggle != null) {
            incognito = incognitoToggle.getAsBoolean();
        }

        if (!nicked && !vanished && !incognito) {
            return "";
        }

        String actionBar = "&fYou are currently ";
        if (nicked) {
            actionBar += "&cNICKED";
        }

        if (vanished) {
            if (!nicked) {
                actionBar += "&cVANISHED";
            } else {
                actionBar += "&f, &cVANISHED";
            }
        }

        if (incognito) {
            if (!nicked && !vanished) {
                actionBar += "&cINCOGNITO";
            } else {
                actionBar += "&f, &cINCOGNITO";
            }
        }

        return actionBar;
    }

    public void applyNickname() {
        generateFakeStats();
    }

    public net.firecraftmc.maniacore.api.nickname.Nickname getNickname() {
        return nickname;
    }

    public void resetNickname() {
        this.fakeStats.clear();
    }

    public User(UUID uniqueId, String name) {
        this(uniqueId);
        this.name = name;
    }

    public User(Map<String, String> jedisData) {
        try {
            this.id = Integer.parseInt(jedisData.get("id"));
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        this.uniqueId = UUID.fromString(jedisData.get("uniqueId"));
        this.name = jedisData.get("name");
        net.firecraftmc.maniacore.api.ranks.Rank rank = net.firecraftmc.maniacore.api.ranks.Rank.valueOf(jedisData.get("rank"));
        this.nickname = new net.firecraftmc.maniacore.api.nickname.Nickname(uniqueId);
        this.rankInfo = new RankInfo(uniqueId);
        this.rankInfo.setRank(rank);
    }

    public boolean isOnline() {
        return false;
    }

    public void setStats(Map<String, net.firecraftmc.maniacore.api.stats.Statistic> stats) {
        if (stats == null) {
            this.stats = new HashMap<>();
        } else {
            this.stats = stats;
        }
    }

    public net.firecraftmc.maniacore.api.stats.Statistic getFakedStat(net.firecraftmc.maniacore.api.stats.Stat stat) {
        if (fakeStats.isEmpty()) {
            generateFakeStats();
        }
        net.firecraftmc.maniacore.api.stats.Statistic s = null;
        try {
            s = fakeStats.get(stat.getName());
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
        if (s != null) {
            if (!this.fakeStats.containsKey(stat.getName())) {
                this.fakeStats.put(stat.getName(), s);
            }
        } else {
            if (FAKED_STATS.contains(stat)) {
                generateFakeStats();
                s = fakeStats.get(stat.getName());
            }
        }
        return s;
    }

    private void generateFakeStats() {
        Random random = new Random();
        for (net.firecraftmc.maniacore.api.stats.Stats fakedStat : FAKED_STATS) {
            if (fakedStat == net.firecraftmc.maniacore.api.stats.Stats.HG_SCORE) {
                net.firecraftmc.maniacore.api.stats.Statistic value = fakedStat.create(uniqueId);
                value.setValue(random.nextInt(90) + 10);
                this.fakeStats.put(fakedStat.getName(), value);
            } else {
                net.firecraftmc.maniacore.api.stats.Statistic stat = getStat(fakedStat);
                net.firecraftmc.maniacore.api.stats.Statistic value = fakedStat.create(uniqueId);
                if (stat.getAsInt() != 0) {
                    value.setValue(random.nextInt(stat.getAsInt()));
                } else {
                    value.setValue(0);
                }
                this.fakeStats.put(value.getName(), value);
            }
        }
    }

    public void setToggles(Map<net.firecraftmc.maniacore.api.user.toggle.Toggles, net.firecraftmc.maniacore.api.user.toggle.Toggle> toggles) {
        if (toggles == null) {
            this.toggles = new HashMap<>();
        } else {
            this.toggles = toggles;
        }
    }

    public Toggle getToggle(Toggles type) {
        return this.toggles.get(type);
    }

    public void incrementStat(net.firecraftmc.maniacore.api.stats.Stat stat) {
        net.firecraftmc.maniacore.api.stats.Statistic statistic = this.stats.getOrDefault(stat.getName(), stat.create(this.getUniqueId()));
        statistic.increment();
        if (!this.stats.containsKey(stat.getName())) {
            this.stats.put(stat.getName(), statistic);
        }
        if (this.fakeStats.containsKey(stat.getName())) {
            this.fakeStats.get(stat.getName()).increment();
        }
    }

    public User(int id, UUID uniqueId, String name, RankInfo rank, net.firecraftmc.maniacore.api.channel.Channel channel) {
        this.uniqueId = uniqueId;
        this.id = id;
        this.name = name;
        this.rankInfo = rank;
        this.channel = channel;
        this.nickname = new Nickname(uniqueId);
    }

    public void addNetworkExperience(int exp) {
        net.firecraftmc.maniacore.api.stats.Statistic stat = getStat(net.firecraftmc.maniacore.api.stats.Stats.EXPERIENCE);
        net.firecraftmc.maniacore.api.leveling.Level current = ManiaCore.getInstance().getLevelManager().getLevel(stat.getAsInt());
        stat.setValue((stat.getAsInt() + exp) + "");
        net.firecraftmc.maniacore.api.leveling.Level newLevel = ManiaCore.getInstance().getLevelManager().getLevel(stat.getAsInt());
        if (current.getNumber() < newLevel.getNumber()) {
            sendMessage("&a&lLevel Up! " + current.getNumber() + " -> " + newLevel.getNumber());
            sendMessage("  &7&oThis message is temporary");
        }
    }

    public void setIgnoredPlayers(Set<net.firecraftmc.maniacore.api.user.IgnoreInfo> ignoredPlayers) {
        this.ignoredPlayers = ignoredPlayers;
    }

    public net.firecraftmc.maniacore.api.channel.Channel getChannel() {
        return (channel != null) ? channel : Channel.GLOBAL;
    }

    public Set<net.firecraftmc.maniacore.api.user.IgnoreInfo> getIgnoredPlayers() {
        return new HashSet<>(ignoredPlayers);
    }

    public Pair<Integer, String> addCoins(int coins, boolean coinMultiplier) {
        net.firecraftmc.maniacore.api.stats.Statistic stat = getStat(net.firecraftmc.maniacore.api.stats.Stats.COINS);
        double multiplier = 1;
        String multiplierString = "";
        if (coinMultiplier) {
            if (getRank().getCoinMultiplier() > 1) {
                multiplier = getRank().getCoinMultiplier();
                multiplierString = getRank().getBaseColor() + "&lx" + getRank().getCoinMultiplier() + " " + getRank().getName().toUpperCase() + " BONUS";
            }
        }
        int totalCoins = (int) Math.round(coins * multiplier);
        stat.setValue((stat.getAsInt() + totalCoins) + "");
        return new Pair<>(coins, multiplierString);
    }

    public IgnoreResult addIgnoredPlayer(User user) {
        for (net.firecraftmc.maniacore.api.user.IgnoreInfo ignoredPlayer : this.ignoredPlayers) {
            if (ignoredPlayer.getIgnored().equals(user.getUniqueId())) {
                return IgnoreResult.ALREADY_ADDED;
            }
        }

        if (user.hasPermission(net.firecraftmc.maniacore.api.ranks.Rank.HELPER)) {
            return IgnoreResult.PLAYER_IS_STAFF;
        }

        net.firecraftmc.maniacore.api.user.IgnoreInfo ignoreInfo = new net.firecraftmc.maniacore.api.user.IgnoreInfo(this.getUniqueId(), user.getUniqueId(), System.currentTimeMillis(), user.getName());
        this.ignoredPlayers.add(ignoreInfo);
        ManiaCore.getInstance().getDatabase().pushRecord(new net.firecraftmc.maniacore.api.records.IgnoreInfoRecord(ignoreInfo));
        return IgnoreResult.SUCCESS;
    }

    public IgnoreResult removeIgnoredPlayer(User user) {
        boolean ignored = false;
        for (net.firecraftmc.maniacore.api.user.IgnoreInfo ignoredPlayer : this.ignoredPlayers) {
            if (ignoredPlayer.getIgnored().equals(user.getUniqueId())) {
                ignored = true;
                break;
            }
        }

        if (!ignored) {
            return IgnoreResult.NOT_IGNORED;
        }

        net.firecraftmc.maniacore.api.user.IgnoreInfo ignoreInfo = null;
        for (net.firecraftmc.maniacore.api.user.IgnoreInfo ignoredPlayer : this.ignoredPlayers) {
            if (ignoredPlayer.getIgnored().equals(user.getUniqueId())) {
                ignoreInfo = ignoredPlayer;
                break;
            }
        }
        if (ignoreInfo != null) {
            this.ignoredPlayers.remove(ignoreInfo);
            boolean status = ManiaCore.getInstance().getDatabase().deleteRecord(new IgnoreInfoRecord(ignoreInfo));
            if (status) {
                return IgnoreResult.SUCCESS;
            } else {
                return IgnoreResult.DATABASE_ERROR;
            }
        }

        return IgnoreResult.NOT_IGNORED;
    }

    public void sendMessage(BaseComponent baseComponent) {
    }

    public void sendMessage(String s) {
    }

    public boolean hasPermission(String permission) {
        return false;
    }

    public boolean hasPermission(net.firecraftmc.maniacore.api.ranks.Rank rank) {
        return getRank().ordinal() <= rank.ordinal();
    }

    public String getDisplayName() {
        net.firecraftmc.maniacore.api.ranks.Rank rank;
        String name;
        if (nickname.isActive()) {
            rank = nickname.getRank();
            name = nickname.getName();
        } else {
            rank = getRank();
            name = getName();
        }

        String displayName = rank.getPrefix() + rank.getBaseColor();
        if (rank != net.firecraftmc.maniacore.api.ranks.Rank.DEFAULT && rank != net.firecraftmc.maniacore.api.ranks.Rank.CONSOLE) {
            displayName += " ";
        }
        displayName += name;

        return displayName;
    }

    public String getColoredName() {
        if (getNickname().isActive()) {
            return nickname.getRank().getBaseColor() + nickname.getName();
        }
        return getRank().getBaseColor() + getName();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        User user = (User) o;
        return Objects.equals(uniqueId, user.uniqueId);
    }

    public int hashCode() {
        return Objects.hash(uniqueId);
    }

    public net.firecraftmc.maniacore.api.ranks.Rank getRank() {
        if (getUniqueId().equals(FIRESTAR311)) {
            this.rankInfo.setRank(net.firecraftmc.maniacore.api.ranks.Rank.ROOT);
            this.rankInfo.setExpire(-1);
        } else if (getUniqueId().equals(ASSASSINPLAYS)) {
            this.rankInfo.setRank(net.firecraftmc.maniacore.api.ranks.Rank.OWNER);
            this.rankInfo.setExpire(-1);
        }
        
        if (this.rankInfo.isExpired()) {
            return this.rankInfo.getPreviousRank();
        }
        return this.rankInfo.getRank();
    }

    public void addIgnoredInfo(net.firecraftmc.maniacore.api.user.IgnoreInfo toObject) {
        this.ignoredPlayers.add(toObject);
    }

    public void incrementOnlineTime() {
        net.firecraftmc.maniacore.api.stats.Statistic stat = getStat(net.firecraftmc.maniacore.api.stats.Stats.ONLINE_TIME);
        stat.increment();

        if (stat.getAsInt() % 600 == 0) {
            int multiplier = 1;
            if (hasPermission(net.firecraftmc.maniacore.api.ranks.Rank.SCAVENGER)) {
                multiplier = 2;
            }
            if (hasPermission(net.firecraftmc.maniacore.api.ranks.Rank.MEDIA)) {
                multiplier = 3;
            }
            if (hasPermission(net.firecraftmc.maniacore.api.ranks.Rank.HELPER)) {
                multiplier = 4;
            }
            if (hasPermission(Rank.ROOT)) {
                multiplier = 5;
            }

            int exp = 10 * multiplier;
            addNetworkExperience(exp);
            sendMessage("&e&l>> &7&o+" + exp + " XP - +10m of online time");
        }
    }

    public net.firecraftmc.maniacore.api.stats.Statistic getStat(net.firecraftmc.maniacore.api.stats.Stat stat) {
        net.firecraftmc.maniacore.api.stats.Statistic s = null;
        try {
            s = stats.getOrDefault(stat.getName(), stat.create(this.uniqueId));
        } catch (IllegalStateException e) {
        }
        if (s != null) {
            if (!this.stats.containsKey(stat.getName())) {
                this.stats.put(stat.getName(), s);
            }
        }
        return s;
    }

    public void setStat(net.firecraftmc.maniacore.api.stats.Stat stat, int value) {
        net.firecraftmc.maniacore.api.stats.Statistic s = stats.getOrDefault(stat.getName(), stat.create(this.uniqueId));
        s.setValue(value + "");
        if (!this.stats.containsKey(stat.getName())) {
            this.stats.put(stat.getName(), s);
        }
    }

    public void setStat(Stat stat, String value) {
        Statistic s = stats.getOrDefault(stat.getName(), stat.create(this.uniqueId));
        s.setValue(value);
        if (!this.stats.containsKey(stat.getName())) {
            this.stats.put(stat.getName(), s);
        }
    }

    public Level getLevel() {
        return ManiaCore.getInstance().getLevelManager().getLevel(this.getStat(Stats.EXPERIENCE).getAsInt());
    }

    public boolean isIgnoring(UUID target) {
        for (IgnoreInfo ignoredPlayer : this.ignoredPlayers) {
            if (ignoredPlayer.getPlayer().equals(target)) {
                return true;
            }
        }
        return false;
    }

    public Skin getSkin() {
        return ManiaCore.getInstance().getSkinManager().getSkin(getUniqueId());
    }
    
    public String toString() {
        return "User{" +
                "uniqueId=" + uniqueId +
                ", name='" + name + '\'' +
                '}';
    }
}