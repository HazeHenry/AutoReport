package hazehenry.autoreport;

import hazehenry.autoreport.data.ProfileListener;
import hazehenry.autoreport.data.ProfileManager;
import hazehenry.autoreport.modules.chatlog.command.ChatLog;
import hazehenry.autoreport.modules.chatlog.listener.ChatLogListener;
import hazehenry.autoreport.modules.report.command.AutoReportCommand;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class AutoReport extends JavaPlugin {

    @Getter
    public static AutoReport instance;

    @Getter
    private ProfileManager profileManager;

    @Override
    public void onEnable() {
        instance = this;

        this.profileManager = new ProfileManager();

        saveDefaultConfig();
        saveConfig();

        registerCommands();
        registerEvents();
    }

    public void registerCommands() {
        getCommand("autoreport").setExecutor(new AutoReportCommand());
        getCommand("chatlog").setExecutor(new ChatLog());
    }

    public void registerEvents() {
        Bukkit.getPluginManager().registerEvents(new ProfileListener(),this);
        Bukkit.getPluginManager().registerEvents(new ChatLogListener(),this);
    }
}
