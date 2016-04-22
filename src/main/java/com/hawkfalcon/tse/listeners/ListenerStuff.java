package com.hawkfalcon.tse.listeners;

import au.com.addstar.monolith.util.NBTItem;
import com.hawkfalcon.tse.Main;
import com.hawkfalcon.tse.objects.ThrownEgg;
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


public class ListenerStuff implements Listener {
    private Main plugin;

    HashMap<Egg, ThrownEgg> eggs = new HashMap<Egg, ThrownEgg>();




    public ListenerStuff(Main instance) {
        plugin = instance;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (player.hasPermission("tse.use")) {
            if ((event.getAction().equals(Action.RIGHT_CLICK_AIR)) || (event.getAction().equals(Action.RIGHT_CLICK_BLOCK))) {
                if (event.getItem() == null) return;
                ItemStack item = event.getItem();
                if (!(item.getData() instanceof SpawnEgg)) return;
                SpawnEgg segg = (SpawnEgg) item.getData();
                NBTItem nbtItem = new NBTItem(segg.toItemStack());
                String mob = nbtItem.getString("mob");
                String mobType = null;
                if (plugin.getConfig().getBoolean("blacklist")) {
                    if (plugin.getConfig().getStringList("blacklisted").contains(mobType.toLowerCase()))
                        return;
                }
                Egg egg = event.getPlayer().launchProjectile(Egg.class);
                ThrownEgg eType = new ThrownEgg(mob,mobType);
                eggs.put(egg, eType);
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
            ThrownEgg eType = eggs.get(egg);
            EntityType type = EntityType.fromName(eType.getType());
            Entity entity = egg.getWorld().spawnEntity(egg.getLocation(), type);
            if (eType.getType().equals("EntityHorse")) {
                Horse horse = (Horse) entity;
                horse.setStyle(Horse.Style.values()[(int) (Math.random() * (Horse.Style.values().length))]);
                horse.setColor(Horse.Color.values()[(int) (Math.random() * (Horse.Color.values().length))]);
            } else if (eType.getType().equals("Sheep")) {
                if (!plugin.getConfig().getBoolean("coloredsheep")) return;
                ((Sheep) entity).setColor(DyeColor.values()[(int) (Math.random() * (DyeColor.values().length))]);
            } else if (eType.getType().equals("Villager")) {
                Villager villager = (Villager) entity;
                villager.setProfession(Villager.Profession.values()[(int) (Math.random() * (Villager.Profession.values().length))]);
            }
            event.setHatching(false);
        }
    }
}
