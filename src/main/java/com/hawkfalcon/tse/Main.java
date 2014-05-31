package com.hawkfalcon.tse;

import com.hawkfalcon.tse.listeners.ListenerStuff;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {


    public void onEnable() {
        getServer().getPluginManager().registerEvents(new ListenerStuff(this), this);
        this.saveDefaultConfig();
    }
}