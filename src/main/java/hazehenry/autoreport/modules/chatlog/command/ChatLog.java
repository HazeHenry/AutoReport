package hazehenry.autoreport.modules.chatlog.command;

import hazehenry.autoreport.AutoReport;
import hazehenry.autoreport.data.Profile;
import hazehenry.autoreport.data.ProfileManager;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ChatLog implements CommandExecutor {

    ProfileManager profileManager = AutoReport.getInstance().getProfileManager();

    private final String prefix = "§c§lCHATLOG §8» §f";

    private final int MESSAGES_PER_PAGE = 12;

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player) {
            Player p = (Player) sender;
            if (args.length < 1) {
                sendChatlogMessage(p, p, 1);
                return true;
            }

            if (args.length < 2) {
                try {
                    int page = Integer.parseInt(args[0]);
                    sendChatlogMessage(p, p, page);
                    return true;
                } catch (NumberFormatException e) {
                    OfflinePlayer player = Bukkit.getOfflinePlayer(args[0]);
                    sendChatlogMessage(p, player, 1);
                    return true;
                }
            }

            OfflinePlayer player = Bukkit.getOfflinePlayer(args[0]);
            try {
                int page = Integer.parseInt(args[1]);
                sendChatlogMessage(p, player, page);
                return true;
            } catch (NumberFormatException e) {
                p.sendMessage(prefix + "§cA megadott érték nem egy szám.");
                return true;
            }
        }
        return false;
    }

    public void sendChatlogMessage(Player p, OfflinePlayer player, int page) {
        if (!player.hasPlayedBefore() && !player.isOnline()) {
            p.sendMessage(prefix +"§cEz a játékos nem játszott a szerveren.");
            return;
        }

        Profile profile = profileManager.getProfile(player.getUniqueId());
        List<String> messages = profile.getChatLog();

        if (messages.isEmpty()) {
            p.sendMessage(prefix +"§cEz a játékos nem küldött üzenetet még.");
        } else {
            int totalPages = (int) Math.ceil((double) messages.size() / MESSAGES_PER_PAGE);
            if (page < 1 || page > totalPages) {
                p.sendMessage(prefix + "§bEz az oldal §c§nnem§b létezik. Maximum oldal: §a§n" + totalPages);
                return;
            }

            p.sendMessage("§8§m---------------§r §6" + page + "/" + totalPages + " §8§m---------------§r");
            p.sendMessage("§e" + p.getName() + " §ajátékos chat logja");
            p.sendMessage("");

            int start = (page - 1) * MESSAGES_PER_PAGE;
            int end = Math.min(start + MESSAGES_PER_PAGE, messages.size());

            for (int i = start; i < end; i++) {
                String message = messages.get(i);
                message = message.replace(getCurrentTime() + " ", "");
                p.sendMessage(message);
            }

            TextComponent previousPage = new TextComponent("«");
            previousPage.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/chatlog " + player.getName() + " " + (page - 1)));
            previousPage.setColor(ChatColor.GOLD);

            TextComponent nextPage = new TextComponent("»");
            nextPage.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/chatlog " + player.getName() + " " + (page + 1)));
            nextPage.setColor(ChatColor.GOLD);

            TextComponent decorationLeft = new TextComponent("§8§m----------§r §6");
            TextComponent decorationMiddle = new TextComponent(" §8§m--------§r §6");
            TextComponent decorationRight = new TextComponent(" §8§m-----------§r");

            BaseComponent[] finalMessage = new BaseComponent[]{
                    decorationLeft, previousPage, decorationMiddle, nextPage, decorationRight
            };

            p.spigot().sendMessage(finalMessage);
        }
    }

    private String getCurrentTime() {
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return today.format(formatter);
    }
}
