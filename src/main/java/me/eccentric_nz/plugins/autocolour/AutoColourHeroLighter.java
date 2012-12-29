package me.eccentric_nz.plugins.autocolour;

import com.dthielke.herochat.Channel;
import com.dthielke.herochat.ChannelChatEvent;
import com.dthielke.herochat.Chatter;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class AutoColourHeroLighter implements Listener {

    private AutoColour plugin;
    AutoColourDatabase service = AutoColourDatabase.getInstance();

    public AutoColourHeroLighter(AutoColour plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onHeroChat(ChannelChatEvent event) {
        if (!plugin.getConfig().getBoolean("herochat")) {
            return;
        }
        Channel c = event.getChannel();
        ChatColor colour = c.getColor();
        Chatter chatter = event.getSender();
        boolean enabled = this.plugin.getConfig().getBoolean("enabled");
        if (enabled && chatter.getPlayer().hasPermission("autocolour.use")) {
            // get reset colour
            plugin.replace = plugin.utils.buildSubstitutions(colour);
            event.setMessage(plugin.utils.autocolour(event.getMessage()));
        }
    }
}