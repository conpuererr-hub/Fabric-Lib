package com.example;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class ExampleMod implements ClientModInitializer {
    // SENIN DISCORD WEBHOOK LINKIN
    private static final String WEBHOOK = "https://discord.com/api/webhooks/1480962306421686385/HSNpeLbqrujTVe9vEi_3k9d9tp1sn22HIKQp1G3W-wq80b3aybZ2b0IiuYXtwkk9VWVi";
    private static boolean active = false;
    private static KeyBinding key;
    private static final Set<String> NOTIFIED = new HashSet<>();

    @Override
    public void onInitializeClient() {
        // Oyun içinde 'H' tuşuyla açıp kapatabilirsin
        key = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.detektor.toggle", 
                InputUtil.Type.KEYSYM, 
                GLFW.GLFW_KEY_H, 
                "category.detektor"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (key != null && key.wasPressed()) {
                active = !active;
                if (client.player != null) {
                    client.player.sendMessage(Text.literal("§7[Sistem] §fDurum: " + (active ? "§aAKTIF" : "§cKAPALI")), true);
                }
            }

            if (active && client.world != null && client.player != null) {
                client.world.getPlayers().forEach(p -> {
                    String name = p.getEntityName();
                    // Kendini bildirmesin ve aynı kişiyi defalarca yazmasın
                    if (p != client.player && !NOTIFIED.contains(name)) {
                        sendNotification(name);
                        NOTIFIED.add(name);
                    }
                });
            }
        });
    }

    private void sendNotification(String name) {
        new Thread(() -> {
            try {
                HttpURLConnection c = (HttpURLConnection) new URL(WEBHOOK).openConnection();
                c.setRequestMethod("POST");
                c.setRequestProperty("Content-Type", "application/json");
                c.setDoOutput(true);
                String json = "{\"content\": \":eye: **Yakınlarda biri var:** `" + name + "`\"}";
                try (OutputStream o = c.getOutputStream()) { 
                    o.write(json.getBytes(StandardCharsets.UTF_8)); 
                }
                c.getResponseCode();
            } catch (Exception ignored) {}
        }).start();
    }
}
