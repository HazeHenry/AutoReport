package hazehenry.autoreport.data;

import hazehenry.autoreport.AutoReport;
import org.bukkit.event.Listener;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;

public class ProfileListener implements Listener {

    private final AutoReport instance = AutoReport.getInstance();
    private final ProfileManager profileManager = instance.getProfileManager();

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        Bukkit.getScheduler().runTaskLater(AutoReport.getInstance(), () -> {
            if (!player.isOnline()) return;
            profileManager.getProfile(player.getUniqueId());
        }, 20L);
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent e) {
        UUID uuid = e.getPlayer().getUniqueId();
        Bukkit.getScheduler().runTaskLater(AutoReport.getInstance(), () -> {
            profileManager.saveProfileThenRemove(uuid);
        }, 1L);
    }
}