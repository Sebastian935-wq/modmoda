package pl.nearbyalert;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class NearbyAlertClient implements ClientModInitializer {
    private static KeyBinding menuKey;
    private final Set<UUID> nearby = new HashSet<>();
    private int tick = 0;

    @Override
    public void onInitializeClient() {
        Config.load();

        menuKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.nearbyalert.menu",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_K,
                "category.nearbyalert"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (menuKey.wasPressed()) {
                if (client.currentScreen == null) {
                    client.setScreen(new NearbyAlertScreen(null));
                }
            }
            checkPlayers(client);
        });
    }

    private void checkPlayers(MinecraftClient client) {
        if (client.player == null || client.world == null) {
            nearby.clear();
            return;
        }
        if (++tick < 10) return; // sprawdzaj co pół sekundy
        tick = 0;

        if (!Config.INSTANCE.enabled) {
            nearby.clear();
            return;
        }

        double maxSq = (double) Config.INSTANCE.radius * Config.INSTANCE.radius;
        Set<UUID> current = new HashSet<>();

        for (PlayerEntity other : client.world.getPlayers()) {
            if (other == client.player) continue;
            double distSq = other.squaredDistanceTo(client.player);
            if (distSq <= maxSq) {
                current.add(other.getUuid());
                if (!nearby.contains(other.getUuid())) {
                    int dist = (int) Math.round(Math.sqrt(distSq));
                    client.player.sendMessage(Text.literal("§e[Nearby] §f" + other.getName().getString()
                            + " §7jest w pobliżu (§a" + dist + " §7kratek)"), false);
                    if (Config.INSTANCE.sound) {
                        client.getSoundManager().play(PositionedSoundInstance.master(
                                SoundEvents.BLOCK_NOTE_BLOCK_PLING.value(), 1.0f, 1.0f));
                    }
                }
            }
        }
        nearby.clear();
        nearby.addAll(current);
    }
}
