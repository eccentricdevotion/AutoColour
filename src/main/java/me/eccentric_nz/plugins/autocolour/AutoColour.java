package me.eccentric_nz.plugins.autocolour;

import java.io.File;
import java.io.IOException;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class AutoColour extends JavaPlugin implements Listener {

    protected static AutoColour plugin;
    AutoColourDatabase service = AutoColourDatabase.getInstance();
    private AutoColourCommands commando;
    PluginManager pm = Bukkit.getServer().getPluginManager();
    public String[][] replace;

    @Override
    public void onEnable() {
        plugin = this;
        this.saveDefaultConfig();

        if (!getDataFolder().exists()) {
            if (!getDataFolder().mkdir()) {
                System.err.println(AutoColourConstants.MY_PLUGIN_NAME + " Could not create directory!");
                System.out.println(AutoColourConstants.MY_PLUGIN_NAME + " Requires you to manually make the AutoColour/ directory!");
            }
            getDataFolder().setWritable(true);
            getDataFolder().setExecutable(true);
        }

        try {
            String path = getDataFolder() + File.separator + "AutoColour.db";
            service.setConnection(path);
            service.createTables();
        } catch (Exception e) {
            System.err.println(AutoColourConstants.MY_PLUGIN_NAME + " Connection and Tables Error: " + e);
        }

        AutoColourHighlighter highlighter = new AutoColourHighlighter(plugin);
        replace = highlighter.buildSubstitutions();
        pm.registerEvents(highlighter, plugin);
        commando = new AutoColourCommands(plugin);
        getCommand("autocolour").setExecutor(commando);
        getCommand("aclist").setExecutor(commando);
        getCommand("acremove").setExecutor(commando);

        try {
            MetricsLite metrics = new MetricsLite(this);
            metrics.start();
        } catch (IOException e) {
            // Failed to submit the stats :-(
        }
    }

    @Override
    public void onDisable() {
        this.saveConfig();
        try {
            service.connection.close();
        } catch (Exception e) {
            System.err.println(AutoColourConstants.MY_PLUGIN_NAME + " Could not close database connection: " + e);
        }
    }
}