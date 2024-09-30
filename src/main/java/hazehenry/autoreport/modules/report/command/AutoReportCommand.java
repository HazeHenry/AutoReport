package hazehenry.autoreport.modules.report.command;

import hazehenry.autoreport.AutoReport;
import hazehenry.autoreport.data.Profile;
import hazehenry.autoreport.data.ProfileManager;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AutoReportCommand implements CommandExecutor {

    ProfileManager profileManager = AutoReport.getInstance().getProfileManager();

    private HashMap<Player, List<Player>> recentlyReported = new HashMap<>();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player p = (Player) sender;
            if (args.length < 1) {
                p.sendMessage("§cAdj meg egy játékost!");
                return true;
            }
            Player player = Bukkit.getPlayer(args[0]);
            if (player == p) {
                p.sendMessage("§cMagadat nem tudod feljelenteni.");
            } else {
                List<Player> players = recentlyReported.get(p);
                if (!recentlyReported.containsKey(player)) {
                    if (players.contains(p)) {
                        p.sendMessage("§cEzt a játékost nem tudod jelenleg feljelenteni, várnod kell kicsit.");
                        return true;
                    }
                    p.sendMessage("§cReport sikeresen elküldve, hamarosan feldolgozásra kerül...");
                    Bukkit.getScheduler().runTaskAsynchronously(AutoReport.getInstance(), () -> handleReport(player, p));
                    players.add(player);
                    recentlyReported.put(p, players);
                    Bukkit.getScheduler().runTaskLater(AutoReport.getInstance(), () -> {
                        players.remove(p);
                        if (players.isEmpty()) { recentlyReported.remove(p); return; }
                        recentlyReported.put(p, players);
                    }, 120 * 20L);
                }
                p.sendMessage("§cReport sikeresen elküldve, hamarosan feldolgozásra kerül...");
                Bukkit.getScheduler().runTaskAsynchronously(AutoReport.getInstance(), () -> handleReport(player, p));
                players.add(player);
                recentlyReported.put(p, players);
                Bukkit.getScheduler().runTaskLater(AutoReport.getInstance(), () -> {
                    players.remove(player);
                    if (players.isEmpty()) { recentlyReported.remove(p); return; }
                    recentlyReported.put(p, players);
                }, 120 * 20L);
            }
        }
        return true;
    }

    public void handleReport(Player player, Player p) {
        Profile profile = profileManager.getProfile(player.getUniqueId());
        List<String> chatmessages = profile.getChatLog();
        List<String> blacklist = AutoReport.getInstance().getConfig().getStringList("wordlist");
        p.sendMessage("§aAnalisztika elindítása...");
        boolean dContain = false;
        int i = 0;
        for (String message : chatmessages) {
            if (!message.contains(getCurrentTime())) continue;
            i++; if (i > 100) break;
            String[] splitmessage = message.split("\\s");
            for (String word : blacklist) {
                for (String fword : splitmessage) {
                    System.out.println(fword);
                    if (fword.equalsIgnoreCase(word)) {
                        dContain = true;
                        System.out.println("found word: " + fword);
                        break;
                    }
                }
            }
        }
        p.sendMessage("§aAnalisztika befejezve.");
        if (dContain) {
            p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1f, 0.5f);
            p.sendMessage("§c§lAUTO REPORT §8» §fA játékosnál §a§ntaláltunk§r §fchat violationt. A játékos §6§nnémítva§r §flett");
            int violations = profile.getChatViolations();
            int muteMinutes = violations * 30;
            profile.setChatViolations(violations + 1);
            profileManager.saveProfile(player.getUniqueId());
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "mute " + player.getName() + " " + muteMinutes + "m AutoReport - Chat Helytelen Használata (#" + violations + ") -s");
            return;
        }
        p.playSound(p.getLocation(), Sound.ENTITY_VILLAGER_NO, 1f, 1f);
        p.sendMessage("§c§lAUTO REPORT §8» §fA játékosnál §c§nnem§r §ftaláltunk semmi némíthatót.");
    }

    private String getCurrentTime() {
        LocalTime now = LocalTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("H:mm:ss");
        return now.format(formatter);
    }

}
