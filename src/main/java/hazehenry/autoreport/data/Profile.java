package hazehenry.autoreport.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Profile {

    private UUID uuid;

    private List<String> chatLog = new ArrayList<>();
    private List<String> chatLogFiltered = new ArrayList<>();
    private int chatViolations = 0;


    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }
}