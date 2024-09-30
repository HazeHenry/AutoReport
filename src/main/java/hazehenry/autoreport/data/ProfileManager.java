package hazehenry.autoreport.data;

import hazehenry.autoreport.AutoReport;
import hazehenry.autoreport.data.handler.JsonLoader;
import org.bukkit.Bukkit;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ProfileManager {

    private Map<UUID, Profile> profiles = new HashMap<>();

    public ProfileManager() {
        File profilesFolder = new File(AutoReport.getInstance().getDataFolder(), "profile");
        if (!profilesFolder.exists()) {
            profilesFolder.mkdir();
        }
    }

    public Profile getProfile(UUID uuid) {
        if (profiles.containsKey(uuid)) return profiles.get(uuid);

        Profile profile = JsonLoader.loadOrDefault(AutoReport.getInstance().getDataFolder(), "profile/" + uuid + ".json", Profile.class);
        if (profile == null) {
            profile = new Profile();
        }

        if (profile.getUuid() == null)  {
            if (Bukkit.getPlayer(uuid) != null) {
                profile.setUuid(uuid);
            }
            AutoReport.getInstance().getProfileManager().saveProfile(uuid);
        }

        profiles.put(uuid, profile);
        return profiles.get(uuid);
    }

    public void saveProfile(UUID uuid) {
        if (profiles.containsKey(uuid)) {
            JsonLoader.saveConfig(AutoReport.getInstance().getDataFolder(), "profile/" + uuid + ".json", profiles.get(uuid));
        }
    }

    public void saveAll() {
        profiles.forEach((uuid, profile) -> JsonLoader.saveConfig(AutoReport.getInstance().getDataFolder(), "profile/" + uuid + ".json", profile));
    }

    public void saveProfileThenRemove(UUID uuid) {
        saveProfile(uuid);
        profiles.remove(uuid);
    }

    public Map<UUID, Profile> getProfiles() {
        return profiles;
    }

}