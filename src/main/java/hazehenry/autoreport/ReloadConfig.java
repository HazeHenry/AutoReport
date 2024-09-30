package hazehenry.autoreport;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ReloadConfig implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        AutoReport.getInstance().reloadConfig();
        AutoReport.getInstance().saveConfig();
        sender.sendMessage("§aConfiguration reloaded.");
        return true;
    }
}
