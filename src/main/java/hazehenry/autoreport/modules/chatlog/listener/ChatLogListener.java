package hazehenry.autoreport.modules.chatlog.listener;

import hazehenry.autoreport.AutoReport;
import hazehenry.autoreport.data.Profile;
import hazehenry.autoreport.data.ProfileManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ChatLogListener implements Listener {

    ProfileManager profileManager = AutoReport.getInstance().getProfileManager();

    @EventHandler
    public void chatSave(AsyncPlayerChatEvent e) {
        Profile profile = profileManager.getProfile(e.getPlayer().getUniqueId());
        String message = "§7[§a" + getCurrentTime() + "§7] §e" + e.getPlayer().getName() + "§8: §f" + e.getMessage();
        List<String> currentChatlog = profile.getChatLog();
        currentChatlog.add(0, message);
        profile.setChatLog(currentChatlog);
        profileManager.saveProfile(e.getPlayer().getUniqueId());
    }

    private String getCurrentTime() {
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter1 = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalTime now = LocalTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("H:mm:ss");
        return today.format(formatter1) + " " + now.format(formatter);
    }
}
