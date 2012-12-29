package me.eccentric_nz.plugins.autocolour;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class AutoColourHighlighter implements Listener {

    private AutoColour plugin;
    AutoColourDatabase service = AutoColourDatabase.getInstance();

    public AutoColourHighlighter(AutoColour plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onChat(AsyncPlayerChatEvent event) {
        if (plugin.getConfig().getBoolean("herochat")) {
            return;
        }
        Player p = event.getPlayer();
        boolean enabled = this.plugin.getConfig().getBoolean("enabled");
        if (enabled && p.hasPermission("autocolour.use")) {
            // get reset colour
            String m = event.getMessage().toString();
            plugin.getServer().getConsoleSender().sendMessage(m);
            event.setMessage(plugin.utils.autocolour(event.getMessage()));
        }
    }
}