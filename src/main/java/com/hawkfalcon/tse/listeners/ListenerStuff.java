package com.hawkfalcon.tse.listeners;

import com.hawkfalcon.tse.Main;
import org.bukkit.DyeColor;
import org.bukkit.GameMode;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerEggThrowEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.SpawnEgg;

import java.util.HashMap;

/**
 * Created by Nick on 5/30/14.
 */

public class ListenerStuff implements Listener{
    private Main plugin;

    HashMap<Egg, EntityType> eggs = new HashMap<Egg, EntityType>();


    public ListenerStuff(Main instance){
        plugin = instance;
    }
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (player.hasPermission("tse.use")) {
            if ((event.getAction().equals(Action.RIGHT_CLICK_AIR)) || (event.getAction().equals(Action.RIGHT_CLICK_BLOCK))) {
                if (event.getItem() == null) return;
                ItemStack item = event.getItem();
                if (!(item.getData() instanceof SpawnEgg) || item == null) return;
                SpawnEgg segg = (SpawnEgg) item.getData();
                if (plugin.getConfig().getBoolean("blacklist")) {
                    if (plugin.getConfig().getStringList("blacklisted").contains(segg.getSpawnedType().toString().toLowerCase()))
                        return;
                }
                Egg egg = event.getPlayer().launchProjectile(Egg.class);
                eggs.put(egg, segg.getSpawnedType());
                GameMode gm = event.getPlayer().getGameMode();
                if (gm.equals(GameMode.SURVIVAL) || gm == GameMode.ADVENTURE) {
                    if (item.getAmount() > 1) {
                        item.setAmount(item.getAmount() - 1);
                    } else {
                        player.getInventory().remove(item);
                    }
                }
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void throwEgg(PlayerEggThrowEvent event) {
        Egg egg = event.getEgg();
        if (eggs.containsKey(egg)) {
            EntityType entityType = eggs.get(egg);
            Entity entity = egg.getWorld().spawnEntity(egg.getLocation(), entityType);
            if (entityType == EntityType.HORSE) {
                Horse horse = (Horse) entity;
                horse.setStyle(Horse.Style.values()[(int) (Math.random() * (Horse.Style.values().length))]);
                horse.setColor(Horse.Color.values()[(int) (Math.random() * (Horse.Color.values().length))]);
            } else if (entityType == EntityType.SHEEP) {
                if (!plugin.getConfig().getBoolean("coloredsheep")) return;
                ((Sheep) entity).setColor(DyeColor.values()[(int) (Math.random() * (DyeColor.values().length))]);
            } else if (entityType == EntityType.VILLAGER) {
                Villager villager = (Villager) entity;
                villager.setProfession(Villager.Profession.values()[(int) (Math.random() * (Villager.Profession.values().length))]);
            }
            event.setHatching(false);
        }
    }
}
