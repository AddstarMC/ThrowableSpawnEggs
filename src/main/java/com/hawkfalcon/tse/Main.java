package com.hawkfalcon.tse;

import com.hawkfalcon.tse.listeners.ListenerStuff;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class Main extends JavaPlugin {

    public Config config;

    @Override
    public void onEnable() {
        File configFile = new File(this.getDataFolder(), "config.yml");
        config = new Config(configFile);
        config.load();
        getServer().getPluginManager().registerEvents(new ListenerStuff(this), this);
    }

    @Override
    public void onDisable() {
        config.save();
    }


}