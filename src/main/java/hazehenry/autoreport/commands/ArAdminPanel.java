package hazehenry.autoreport.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ArAdminPanel implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender p, Command cmd, String label, String[] args) {
        if (!p.hasPermission("ar.adminpanel")) return false;

        if (args.length < 1) {
            return true;
        }
        return true;
    }
}