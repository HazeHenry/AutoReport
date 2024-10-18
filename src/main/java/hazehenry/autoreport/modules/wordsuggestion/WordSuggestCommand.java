package hazehenry.autoreport.modules.wordsuggestion;

import hazehenry.autoreport.AutoReport;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class WordSuggestCommand implements CommandExecutor {

    private List<Player> recentlySuggested = new ArrayList<>();

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) return false;
        Player p = (Player) sender;
        if (args.length < 1) {
            p.sendMessage("§c/suggestword <szó>!");
            return true;
        }

        if (recentlySuggested.contains(p)) {
            p.sendMessage("§cVárnod kell a következő szóajánláshoz.");
            return true;
        }

        String word = args[0];
        recentlySuggested.add(p);
        p.sendMessage("§c§lAUTOREPORT §8» §fKöszönjük a szóajánlást! §6" + word + "§r hamarosan egy admin átnézi az ajánlásokat!");
        p.playSound(p.getLocation(), Sound.ORB_PICKUP, 1f, 1f);
        List<String> suggestedWords = AutoReport.getInstance().getSuggestedWords();
        suggestedWords.add(word);
        AutoReport.getInstance().getConfig().set("suggestedwords", suggestedWords);
        AutoReport.getInstance().saveConfig();
        Bukkit.getScheduler().runTaskLater(AutoReport.getInstance(), () -> recentlySuggested.remove(p), 120 * 20L);
        return true;
    }
}
