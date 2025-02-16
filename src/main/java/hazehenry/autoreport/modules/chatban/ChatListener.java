package hazehenry.autoreport.modules.chatban;

import hazehenry.autoreport.AutoReport;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.List;

public class ChatListener implements Listener {

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        List<String> naughtyPlayers = AutoReport.getInstance().getConfig().getStringList("naughtyplayers");
        if (naughtyPlayers.contains(e.getPlayer().getName())) {
            Bukkit.getOnlinePlayers().forEach(player -> e.getRecipients().remove(player));
        }
        e.getRecipients().add(e.getPlayer());
    }
}
