package net.firecraftmc.maniacore.api.chat;

import net.firecraftmc.maniacore.api.channel.Channel;
import net.firecraftmc.maniacore.api.leveling.Level;
import net.firecraftmc.maniacore.api.nickname.Nickname;
import net.firecraftmc.maniacore.api.ranks.Rank;
import net.firecraftmc.maniacore.api.util.CenturionsUtils;
import net.firecraftmc.maniacore.api.user.User;

public class ChatFormatter {
    
    public static final String CHANNEL_HEADER = "{symbolcolor}&l[{channelcolor}{channelname}{symbolcolor}&l]"; //Space and color added during processing
    public static final String LEVEL_FORMAT = "&8[{levelcolor}{level}&8]";
    public static final String PLAYER_NAME_FORMAT = "{displayname}"; //Spaces added during processing
    public static final String MESSAGE_FORMAT = "{chatcolor}{message}";
    
    protected String format;

    public ChatFormatter(String format) {
        this.format = format;
    }
    
    public String format(User user, String message) {
        String format = this.format;
        Channel channel = user.getChannel();
        Level level = user.getLevel();
        Rank rank = user.getRank();
        Nickname nickname = user.getNickname();
        if (nickname != null && nickname.isActive()) {
            rank = nickname.getRank();
        }
        format = format.replace("{symbolcolor}", channel.getSymbolColor());
        format = format.replace("{channelname}", channel.name());
        format = format.replace("{channelcolor}", channel.getColor());
        format = format.replace("{levelcolor}", level.getNumberColor().toString());
        format = format.replace("{level}", level.getNumber() + "");
        format = format.replace("{chatcolor}", rank.getChatColor());
        format = format.replace("{message}", message);
        format = format.replace("{trueName}", user.getName());
        format = format.replace("{truePrefix}", user.getRank().getPrefix());
        format = format.replace("{displayname}", user.getDisplayName());
        format = format.replace("{truerankbasecolor}", user.getRank().getBaseColor());
        format = format.replace("{truechatcolor}", user.getRank().getChatColor());
        return CenturionsUtils.color(format);
    }
}
