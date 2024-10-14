package hazehenry.autoreport.commands;

import hazehenry.autoreport.AutoReport;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class ArAdminPanel implements CommandExecutor {

    private final String prefix = "§c§lAUTOREPORT §f";

    @Override
    public boolean onCommand(CommandSender p, Command cmd, String label, String[] args) {
        if (!p.hasPermission("ar.adminpanel")) return false;

        if (args.length < 1) {
            p.sendMessage("§c§lAUTOREPORT ADMIN PANEL");
            p.sendMessage("");
            p.sendMessage("§a/ap wordsuggestions §8- §7Review player suggested words.");
            p.sendMessage("§a/ap addword §8- §7Add a word to the blacklist.");
            p.sendMessage("§a/ap removeword §8- §7Remove a word from the blacklist.");
            p.sendMessage("§a/ap list §8- §7List all words in the list.");
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "wordsuggestions":
                if (!(p instanceof Player)) {
                    p.sendMessage("§c§lOnly players can use this command.");
                    break;
                }
                Player player = (Player) p;
                if (AutoReport.getInstance().getSuggestedWords().isEmpty()) {
                    player.sendMessage("§c§lThere are no suggested words.");
                    break;
                }
                player.sendMessage("§8§m---------------------------");
                player.sendMessage("§c§lSUGGESTED WORDS");
                for (String word : AutoReport.getInstance().getSuggestedWords()) {
                    TextComponent previousPage = new TextComponent("✔");
                    previousPage.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/arp addword " + word));
                    previousPage.setColor(ChatColor.GREEN);

                    TextComponent nextPage = new TextComponent(" ✘");
                    nextPage.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/arp rsuggestion " + word));
                    nextPage.setColor(ChatColor.RED);

                    TextComponent decorationLeft = new TextComponent(" §f" + word);
                    TextComponent decorationMiddle = new TextComponent(" §8- §r");

                    BaseComponent[] finalMessage = new BaseComponent[]{
                            decorationLeft, decorationMiddle, previousPage, nextPage
                    };

                    player.spigot().sendMessage(finalMessage);
                }
                player.sendMessage("§8§m---------------------------");
                break;
            case "addword":
                if (args.length < 2) {
                    p.sendMessage("§c§l/arp addword <szó>");
                    break;
                }
                String word = args[1];
                if (AutoReport.getInstance().getConfig().getStringList("wordlist").contains(word)) {
                    p.sendMessage("§c§lEz a szó már az adatbázisban van.");
                    break;
                }
                List<String> wordlist = AutoReport.getInstance().getConfig().getStringList("wordlist");
                wordlist.add(word);
                AutoReport.getInstance().getConfig().set("wordlist", wordlist);
                AutoReport.getInstance().saveConfig();
                p.sendMessage(prefix + "§6" + word + "§r szó sikeresen hozzáadva az adatbázishoz!");
                break;
            case "removeword":
                if (args.length < 2) {
                    p.sendMessage("§c§l/arp removeword <szó>");
                    break;
                }
                String wordToRemove = args[1];
                if (!AutoReport.getInstance().getConfig().getStringList("wordlist").contains(wordToRemove)) {
                    p.sendMessage("§c§lEz a szó nincs az adatbázisban.");
                    break;
                }
                List<String> wordlist1 = AutoReport.getInstance().getConfig().getStringList("wordlist");
                wordlist1.remove(wordToRemove);
                AutoReport.getInstance().getConfig().set("wordlist", wordlist1);
                AutoReport.getInstance().saveConfig();
                p.sendMessage(prefix + "§6" + wordToRemove + "§r szó sikeresen kivéve az adatbázisból!");
                break;
            case "rsuggestion":
                if (args.length < 2) {
                    p.sendMessage("§c§l/arp rsuggestion <szó>");
                    break;
                }
                String wordToRemove2 = args[1];
                if (!AutoReport.getInstance().getSuggestedWords().contains(wordToRemove2)) {
                    p.sendMessage("§c§lEz a szó nincs a javasolt szavak közül.");
                    break;
                }
                AutoReport.getInstance().getSuggestedWords().remove(wordToRemove2);
                AutoReport.getInstance().saveConfig();
                p.sendMessage(prefix + "§6" + wordToRemove2 + "§r javasolt szó sikeresen eltávolítva!");
                break;
            case "list":
                p.sendMessage("§8§m---------------------------");
                p.sendMessage("§c§lDATABASE WORDLIST");
                int i = 0;
                StringBuilder clampWord = new StringBuilder();
                List<String> tempList = AutoReport.getInstance().getConfig().getStringList("wordlist");
                for (String wordl : tempList) {
                    i++;
                    clampWord.append(" ").append(wordl);
                    if (i == 6) {
                        p.sendMessage(clampWord.toString());
                        clampWord = new StringBuilder();
                        i = 0;
                    }
                }
                p.sendMessage(clampWord.toString());
                p.sendMessage("§8§m---------------------------");
                break;
        }
        return true;
    }
}