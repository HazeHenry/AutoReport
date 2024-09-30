package hazehenry.autoreport.modules.chatlog.command;

import hazehenry.autoreport.AutoReport;
import hazehenry.autoreport.data.Profile;
import hazehenry.autoreport.data.ProfileManager;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class ChatLog implements CommandExecutor {

    ProfileManager profileManager = AutoReport.getInstance().getProfileManager();

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            Profile profile = profileManager.getProfile(player.getUniqueId());
            List<String> messages = profile.getChatLog();

            final int MESSAGES_PER_PAGE = 12;

            if (messages.isEmpty()) {
                player.sendMessage("§cEz a játékos nem küldött üzenetet még.");
            } else {
                int page = 1;
                if (args.length > 0) {
                    try {
                        page = Integer.parseInt(args[0]);
                    } catch (NumberFormatException e) {
                        player.sendMessage("A megadott érték nem szám.");
                        return true;
                    }
                }

                int totalPages = (int) Math.ceil((double) messages.size() / MESSAGES_PER_PAGE);
                if (page < 1 || page > totalPages) {
                    player.sendMessage("Ez az oldal nem létezik. Maximum oldal: " + totalPages);
                    return true;
                }

                player.sendMessage("§8§m-------------§r" + page + "/" + totalPages + "§8§m-------------§r");
                player.sendMessage("§e" + player.getName() + " §ajátékos chat logja");
                player.sendMessage("");

                int start = (page - 1) * MESSAGES_PER_PAGE;
                int end = Math.min(start + MESSAGES_PER_PAGE, messages.size());

                for (int i = start; i < end; i++) {
                    player.sendMessage(messages.get(i));
                }

                TextComponent previousPage = new TextComponent("<<");
                previousPage.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/chatlog " + (page - 1)));
                previousPage.setColor(net.md_5.bungee.api.ChatColor.BLUE);

                TextComponent nextPage = new TextComponent(">>");
                nextPage.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/chatlog " + (page + 1)));
                nextPage.setColor(net.md_5.bungee.api.ChatColor.BLUE);

                TextComponent decorationLeft = new TextComponent("§8§m----------§r §6 ");
                TextComponent decorationRight = new TextComponent(" §6§8§m-----------§r");

                TextComponent finalMessage = new TextComponent();
                finalMessage.addExtra(decorationLeft);
                finalMessage.addExtra(previousPage);
                finalMessage.addExtra(new TextComponent("§8§m--------§r §6"));
                finalMessage.addExtra(nextPage);
                finalMessage.addExtra(decorationRight);

                player.spigot().sendMessage(finalMessage);
            }
            return true;
        }
        return false;
    }
}
