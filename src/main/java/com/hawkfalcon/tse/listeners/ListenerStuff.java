package com.hawkfalcon.tse.listeners;

import au.com.addstar.monolith.MonoSpawnEgg;
import com.hawkfalcon.tse.Main;
import org.bukkit.DyeColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Egg;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerEggThrowEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.SpawnEgg;

import java.util.HashMap;
import java.util.List;


public class ListenerStuff implements Listener {
    private Main plugin;

    private boolean blackListOn;
    private List<String> blackList;

    HashMap<Egg, MonoSpawnEgg> eggs = new HashMap<Egg, MonoSpawnEgg>();




    public ListenerStuff(Main instance) {
        plugin = instance;
        this.blackListOn = plugin.getConfig().getBoolean("blacklist");
        this.blackList = plugin.getConfig().getStringList("blacklisted");
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (player.hasPermission("tse.use")) {
            if ((event.getAction().equals(Action.RIGHT_CLICK_AIR)) || (event.getAction().equals(Action.RIGHT_CLICK_BLOCK))) {
                if (event.getItem() == null) return;
                ItemStack item = event.getItem();
                if (!(item.getData() instanceof SpawnEgg)) return;
                MonoSpawnEgg megg = new MonoSpawnEgg(item);
                EntityType spawnType = EntityType.CHICKEN;
                try {
                    spawnType = megg.getMonoSpawnedType();
                }catch (Exception e){
                    e.printStackTrace();
                }
                if (blackListOn) {
                    if (blackList.contains(spawnType.toString().toLowerCase())) {
                        spawnType = EntityType.CHICKEN;
                    }
                    Egg egg = event.getPlayer().launchProjectile(Egg.class);
                    megg.setMonoSpawnedType(spawnType);
                    eggs.put(egg, megg);
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
    }

    @EventHandler
    public void throwEgg(PlayerEggThrowEvent event) {
        Egg egg = event.getEgg();
        if (eggs.containsKey(egg)) {
            MonoSpawnEgg megg = eggs.get(egg);
            eggs.remove(egg);
            EntityType type;
            if (megg.getMonoSpawnedType() ==  null){
                 type = EntityType.CHICKEN;
            } else {
                type = megg.getMonoSpawnedType();
            }
            Entity entity = egg.getWorld().spawnEntity(egg.getLocation(), type);
            if(megg.isCustomNameVisible() && megg.getCustomName() != null){
                entity.setCustomName(megg.getCustomName());
                entity.setCustomNameVisible(true);
            }
            if (entity.getType().equals("EntityHorse")) {
                Horse horse = (Horse) entity;
                horse.setStyle(Horse.Style.values()[(int) (Math.random() * (Horse.Style.values().length))]);
                horse.setColor(Horse.Color.values()[(int) (Math.random() * (Horse.Color.values().length))]);
            } else if (entity.getType().equals("Sheep")) {
                if (!plugin.getConfig().getBoolean("coloredsheep")) return;
                ((Sheep) entity).setColor(DyeColor.values()[(int) (Math.random() * (DyeColor.values().length))]);
            } else if (entity.getType().equals("Villager")) {
                Villager villager = (Villager) entity;
                villager.setProfession(Villager.Profession.values()[(int) (Math.random() * (Villager.Profession.values().length))]);
            }
            event.setHatching(false);
        }
    }
}
