package pl.nearbyalert;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.nio.file.Files;
import java.nio.file.Path;

public class Config {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path PATH = FabricLoader.getInstance().getConfigDir().resolve("nearbyalert.json");

    public static Config INSTANCE = new Config();

    public boolean enabled = true;
    public boolean sound = true;
    public int radius = 30; // w kratkach (blokach)

    public static void load() {
        try {
            if (Files.exists(PATH)) {
                Config c = GSON.fromJson(Files.readString(PATH), Config.class);
                if (c != null) INSTANCE = c;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        INSTANCE.radius = Math.max(1, Math.min(256, INSTANCE.radius));
    }

    public static void save() {
        try {
            Files.writeString(PATH, GSON.toJson(INSTANCE));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
