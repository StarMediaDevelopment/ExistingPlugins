package net.firecraftmc.maniacore.spigot.cmd;

import net.firecraftmc.maniacore.api.CenturionsCore;
import net.firecraftmc.maniacore.api.user.User;
import net.firecraftmc.maniacore.api.util.CenturionsUtils;
import net.firecraftmc.manialib.util.Constants;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.util.Date;

@SuppressWarnings("DuplicatedCode")
public class IgnoreCmd implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(CenturionsUtils.color("&cOnly players may use that command."));
            return true;
        }
        
        Player player = (Player) sender;
        
        if (!(args.length > 0)) {
            sender.sendMessage(CenturionsUtils.color("&cUsage: /ignore <add|remove|list> [player]"));
            return true;
        }
        
        net.firecraftmc.maniacore.api.user.User user = CenturionsCore.getInstance().getUserManager().getUser(player.getUniqueId());
        
        if (CenturionsUtils.checkCmdAliases(args, 0, "add", "a")) {
            if (!(args.length > 1)) {
                sender.sendMessage(CenturionsUtils.color("&cYou must provide a player name."));
                return true;
            }
            
            net.firecraftmc.maniacore.api.user.User target = CenturionsCore.getInstance().getUserManager().getUser(args[1]);
            if (target == null) {
                sender.sendMessage(CenturionsUtils.color("&cYou provided an invalid name."));
                return true;
            }
            
            net.firecraftmc.maniacore.api.user.IgnoreResult result = user.addIgnoredPlayer(target);
            String message = "";
            switch (result) {
                case PLAYER_IS_STAFF:
                    message = "&cThat player is a staff member. You cannot ignore them.";
                    break;
                case SUCCESS:
                    message = "&aYou added " + target.getName() + " to your ignored players list";
                    break;
                case NOT_IGNORED:
                case DATABASE_ERROR:
                    message = "";
                    break;
                case ALREADY_ADDED:
                    message = "&cYou already have " + target.getName() + " on your ignored players list.";
                    break;
            }
    
            player.sendMessage(CenturionsUtils.color(message));
        } else if (CenturionsUtils.checkCmdAliases(args, 0, "remove", "r")) {
            if (!(args.length > 1)) {
                sender.sendMessage(CenturionsUtils.color("&cYou must provide a player name."));
                return true;
            }
            
            User target = CenturionsCore.getInstance().getUserManager().getUser(args[1]);
            if (target == null) {
                sender.sendMessage(CenturionsUtils.color("&cYou provided an invalid name."));
                return true;
            }
            
            net.firecraftmc.maniacore.api.user.IgnoreResult result = user.removeIgnoredPlayer(target);
            String message = "";
            switch (result) {
                case PLAYER_IS_STAFF:
                    message = "";
                    break;
                case NOT_IGNORED:
                    message = "&cThat player is not ignored.";
                    break;
                case DATABASE_ERROR:
                    message = "&cThere was an error removing that player from your ignored list.";
                    break;
                case SUCCESS:
                    message = "&aYou removed " + target.getName() + " from your ignored players list";
                    break;
            }
    
            player.sendMessage(CenturionsUtils.color(message));
        } else if (CenturionsUtils.checkCmdAliases(args, 0, "list", "l")) {
            player.sendMessage(CenturionsUtils.color("&6&l>> &bAll ignored players."));
            for (net.firecraftmc.maniacore.api.user.IgnoreInfo ignoredPlayer : user.getIgnoredPlayers()) {
                player.sendMessage(CenturionsUtils.color("&6&l> &b" + ignoredPlayer.getIgnoredName() + " &fwas ignored on &a" + Constants.DATE_FORMAT.format(new Date(ignoredPlayer.getTimestamp()))));
            }
        }
        
        return true;
    }
}
