package hazehenry.autoreport.modules.report;

import hazehenry.autoreport.AutoReport;
import hazehenry.autoreport.data.Profile;
import hazehenry.autoreport.data.ProfileManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.time.LocalDate;
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

            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(args[0]);
            if (!offlinePlayer.hasPlayedBefore() && !offlinePlayer.isOnline()) {
                p.sendMessage("§cEz a játékos nem játszott a szerveren.");
                return true;
            }
            if (!offlinePlayer.isOnline()) {
                p.sendMessage("§cOffline játékost nem tudsz feljelenteni.");
                return true;
            }

            Player player = Bukkit.getPlayer(args[0]);

            if (player == p) {
                p.sendMessage("§cMagadat nem tudod feljelenteni.");
                return false;
            }

            if (player.hasPermission("bc.staff")) {
                p.sendMessage("§cEzt a játékost nem tudod feljelenteni!");
                return false;
            }

            List<Player> players;
            if (!recentlyReported.containsKey(p)) {
                players = new ArrayList<>();
            } else {
                players = recentlyReported.get(p);
            }

            if (recentlyReported.containsKey(p)) {
                if (players.contains(player)) {
                    p.sendMessage("§cEzt a játékost nem tudod jelenleg feljelenteni, várnod kell kicsit.");
                    return true;
                }
                p.sendMessage("§aReport sikeresen elküldve, hamarosan feldolgozásra kerül...");
                Bukkit.getScheduler().runTaskAsynchronously(AutoReport.getInstance(), () -> handleReport(player, p));
                players.add(player);
                recentlyReported.put(p, players);
                Bukkit.getScheduler().runTaskLater(AutoReport.getInstance(), () -> {
                    players.remove(p);
                    if (players.isEmpty()) {
                        recentlyReported.remove(p);
                        return;
                    }
                    recentlyReported.put(p, players);
                }, 120 * 20L);
            }
            p.sendMessage("§aReport sikeresen elküldve, hamarosan feldolgozásra kerül...");
            Bukkit.getScheduler().runTaskAsynchronously(AutoReport.getInstance(), () -> handleReport(player, p));
            players.add(player);
            recentlyReported.put(p, players);
            Bukkit.getScheduler().runTaskLater(AutoReport.getInstance(), () -> {
                players.remove(player);
                if (players.isEmpty()) {
                    recentlyReported.remove(p);
                    return;
                }
                recentlyReported.put(p, players);
            }, 120 * 20L);
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
            message = ChatColor.stripColor(message);
            if (profile.getChatLogFiltered().contains(message)) continue;
            i++; if (i > 75) break;
            if (capsChecker(message)) {
                dContain = true;
                profile.getChatLogFiltered().add(message);
                break;
            }
            String[] splitmessage = message.split("\\s");
            for (String word : blacklist) {
                for (String fword : splitmessage) {
                    if (fword.equalsIgnoreCase(word)) {
                        dContain = true;
                        profile.getChatLogFiltered().add(message);
                        break;
                    }
                }
            }
        }
        p.sendMessage("§aAnalisztika befejezve.");
        if (dContain) {
            p.playSound(p.getLocation(), Sound.LEVEL_UP, 1f, 0.5f);
            p.sendMessage("§c§lAUTO REPORT §8» §fA játékosnál §a§ntaláltunk§r §fchat violationt. A játékos §6§nnémítva§r §flett. Köszönjük a segítséget!");
            int violations = profile.getChatViolations();
            violations++;
            int muteMinutes = violations * 30;
            profile.setChatViolations(violations);
            profileManager.saveProfile(player.getUniqueId());
            int finalViolations = violations;
            Bukkit.getScheduler().runTask(AutoReport.getInstance(), () -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "mute " + player.getName() + " " + muteMinutes + "m AutoReport - Chat Helytelen Használata (#" + finalViolations + ") -s"));
            for (Player staff : Bukkit.getOnlinePlayers()) {
                if (staff.hasPermission("bc.staff")) {
                    staff.sendMessage("§c§lAUTOREPORT §8» §6Játékos §b§n" + player + "§r §cnémítva lett report által.");
                }
            }
            return;
        }
        p.playSound(p.getLocation(), Sound.VILLAGER_NO, 1f, 1f);
        p.sendMessage("§c§lAUTO REPORT §8» §fA játékosnál §c§nnem§r §ftaláltunk chat violationt.");
    }

    private String getCurrentTime() {
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return today.format(formatter);
    }

    public boolean capsChecker(String message) {
        int caps = 0;
        int length = message.length();
        String[] splitMessage = message.split("");
        for (String entry : splitMessage) {
            if (entry.equals(entry.toUpperCase())) {
                caps++;
            }
        }
        return caps >= length * 0.7;
    }

}
