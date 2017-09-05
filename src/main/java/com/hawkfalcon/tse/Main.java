package com.hawkfalcon.tse;

import com.hawkfalcon.tse.listeners.ListenerStuff;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class Main extends JavaPlugin {

    private Config config;

    public void onEnable() {
        getServer().getPluginManager().registerEvents(new ListenerStuff(this), this);
        this.saveDefaultConfig();
        config = new Config(this.getConfig());

    }

    private class Config extends AutoConfig {

        protected Config(File file) {
            super(file);
        }
    }
}