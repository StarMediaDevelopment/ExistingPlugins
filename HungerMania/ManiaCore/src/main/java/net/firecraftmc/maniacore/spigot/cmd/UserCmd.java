package net.firecraftmc.maniacore.spigot.cmd;

import net.firecraftmc.maniacore.spigot.util.SpigotUtils;
import net.firecraftmc.maniacore.CenturionsCorePlugin;
import net.firecraftmc.maniacore.api.CenturionsCore;
import net.firecraftmc.maniacore.api.ranks.Rank;
import net.firecraftmc.maniacore.api.records.UserRecord;
import net.firecraftmc.maniacore.api.user.User;
import net.firecraftmc.maniacore.api.util.CenturionsUtils;
import net.firecraftmc.manialib.sql.IRecord;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class UserCmd implements CommandExecutor {
    
    private CenturionsCorePlugin plugin;

    public UserCmd(CenturionsCorePlugin plugin) {
        this.plugin = plugin;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        Rank senderRank = SpigotUtils.getRankFromSender(sender);
        if (senderRank.ordinal() > Rank.ADMIN.ordinal()) {
            sender.sendMessage(CenturionsUtils.color("&cYou are not allowed to use that command."));
            return true;
        }
        
        if (!(args.length > 0)) {
            sender.sendMessage(CenturionsUtils.color("&cUsage: /user <name> <subcommand>"));
            return true;
        }
        
        if (CenturionsUtils.checkCmdAliases(args, 0, "file")) {
            new BukkitRunnable() {
                public void run() {
                    Set<User> users = new HashSet<>();
                    sender.sendMessage(CenturionsUtils.color("&aProcessing your request..."));
                    List<IRecord> records = CenturionsCore.getInstance().getDatabase().getRecords(UserRecord.class, null, null);
                    sender.sendMessage(CenturionsUtils.color("&aFound a total of " + records.size() + " entries in the database"));
                    for (IRecord record : records) {
                        if (record instanceof UserRecord) {
                            users.add(((UserRecord) record).toObject());
                        }
                    }
                    sender.sendMessage(CenturionsUtils.color("&aFound a total of " + users.size() + " users in the database"));
                    File folder = new File(plugin.getDataFolder() + File.separator + "output");
                    if (!folder.exists()) {
                        folder.mkdirs();
                    }
                    File output = new File(folder + File.separator + "users-" + System.currentTimeMillis() + ".txt");
                    if (!output.exists()) {
                        try {
                            output.createNewFile();
                        } catch (IOException e) {
                            sender.sendMessage(CenturionsUtils.color("&cCould not create the output file: " + e.getMessage()));
                            return;
                        }
                    }

                    try (FileOutputStream fileOut = new FileOutputStream(output); PrintWriter out = new PrintWriter(fileOut)) {
                        for (User user : users) {
                            out.println(user.getUniqueId() + " - " + user.getName() + " - " + user.getRank().name());
                        }
                        out.flush();
                    } catch (Exception e) {
                        sender.sendMessage(CenturionsUtils.color("&cThere was an error saving user data to the file " + e.getMessage()));
                    } 
                    sender.sendMessage(CenturionsUtils.color("&aSuccessfully saved to the file " + output.getName()));
                }
            }.runTaskAsynchronously(plugin);
        }
        return true;
    }
}
