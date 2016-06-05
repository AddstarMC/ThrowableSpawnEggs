package com.hawkfalcon.tse.listeners;

import au.com.addstar.monolith.MonoSpawnEgg;

import com.hawkfalcon.tse.Main;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Egg;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerEggThrowEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.SpawnEgg;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ListenerStuff implements Listener {
    private Main plugin;

    private boolean blackListOn;
    private List<String> blackList;

    HashMap<Egg, MonoSpawnEgg> eggs = new HashMap<Egg, MonoSpawnEgg>();

    static final Set<Material> MainHandIgnore = new HashSet<Material>(
    	Arrays.asList(new Material[] {
	    	Material.WOOD_SWORD,
	    	Material.STONE_SWORD,
	    	Material.IRON_SWORD,
	    	Material.DIAMOND_SWORD,
	    	Material.WOOD_AXE,
	    	Material.STONE_AXE,
	    	Material.IRON_AXE,
	    	Material.DIAMOND_AXE,
	    	Material.WOOD_PICKAXE,
	    	Material.STONE_PICKAXE,
	    	Material.IRON_PICKAXE,
	    	Material.DIAMOND_PICKAXE,
	    	Material.IRON_INGOT,
	    	Material.GOLD_INGOT
	    }));

    public ListenerStuff(Main instance) {
        plugin = instance;
        this.blackListOn = plugin.getConfig().getBoolean("blacklist");
        this.blackList = plugin.getConfig().getStringList("blacklisted");
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (player.hasPermission("tse.use")) {
            if ((event.getAction().equals(Action.RIGHT_CLICK_AIR)) || (event.getAction().equals(Action.RIGHT_CLICK_BLOCK))) {
            	// Decide which item to use (prefer main hand over off hand)
            	// We ignore certain items in main hand and use off hand instead
            	boolean useMainHand = true;
            	ItemStack item = player.getInventory().getItemInMainHand();
            	if ((item == null) || (item.getType() == Material.AIR) || (MainHandIgnore.contains(item))) {
            		item = player.getInventory().getItemInOffHand();
            		useMainHand = false;
            	}
            	if ((item == null) || (item.getType() == Material.AIR)) return;
                if (!(item.getData() instanceof SpawnEgg)) return;

                // Prepare and launch the egg
                MonoSpawnEgg mEgg = new MonoSpawnEgg(item);
                EntityType spawnType;
                try {
                    spawnType = mEgg.getMonoSpawnedType();
                } catch (Exception e) {
                    e.printStackTrace();
                    spawnType = EntityType.CHICKEN;
                }
                if (spawnType == null) return;
                if (blackListOn && blackList.size() > 0) {
                    if (blackList.contains(spawnType.getName().toLowerCase())) {
                        spawnType = EntityType.CHICKEN;
                        mEgg.setMonoSpawnedType(spawnType);
                    }
                }
                Egg egg = event.getPlayer().launchProjectile(Egg.class);
                eggs.put(egg, mEgg);

                // Deplete the relevant item stack
                GameMode gm = event.getPlayer().getGameMode();
                if (gm.equals(GameMode.SURVIVAL) || gm == GameMode.ADVENTURE) {
                    if (item.getAmount() > 1) {
                    	// Decrement the item stack quantity
                        item.setAmount(item.getAmount() - 1);
                    } else {
                    	// Spawn egg is depleted, remove it
                    	if (useMainHand) {
                    		player.getInventory().setItemInMainHand(null);
                    	} else {
                    		player.getInventory().setItemInOffHand(null);
                    	}
                    }
                }
                event.setCancelled(true);
                return;
            }
            return;
        }
        event.getPlayer().sendMessage("No Permission to throw eggs");
        return;
    }
    //todo Allow eggs to be updated with complete entity.
    /*@EventHandler
    public void updateEgg(PlayerInteractEntityEvent event){
        Player player = event.getPlayer();
        if (!player.hasPermission("tse.update")) {
            player.sendMessage("No Permission");
            return;
        }
       ItemStack item =  event.getPlayer().getInventory().getItemInMainHand();
        if(item.getData() instanceof SpawnEgg){
            MonoSpawnEgg mEgg = new MonoSpawnEgg(item);
            if (mEgg.getMonoSpawnedType() == null){
                Entity entity = event.getRightClicked();
                if(entity != null) {
                    if (mEgg.(entity)) {
                        ItemStack newEgg = mEgg.toItemStack();
                        newEgg.getItemMeta().setDisplayName("Custom Spawn Egg");
                        player.getInventory().setItemInMainHand(mEgg.());
                        player.sendMessage("Your egg has been updated!!!");
                        entity.remove();
                        player.playSound(player.getLocation(), Sound.BLOCK_IRON_DOOR_CLOSE, (float) 5.0, (float) 1.0);
                    }
                    player.sendMessage("update is false");
                }
                player.sendMessage("entity is null");
            }
            player.sendMessage("Egg isnt blank");
        }
        player.sendMessage("Not an egg");
    }*/
//todo Spawn complete entity not just the type.
    @EventHandler()
    public void throwEgg(PlayerEggThrowEvent event) {
        Egg egg = event.getEgg();
        if (eggs.containsKey(egg)) {
            MonoSpawnEgg mEgg = eggs.get(egg);
            egg.getLocation();
            Entity spawn = egg.getWorld().spawnEntity(egg.getLocation(), mEgg.getMonoSpawnedType());
            if (spawn == null)event.getPlayer().sendMessage("Spawning Error");
            if(mEgg.getCustomName() != null) spawn.setCustomName(mEgg.getCustomName());
            spawn.setCustomNameVisible(mEgg.isCustomNameVisible());
            eggs.remove(egg);
            event.setHatching(false);
        }
    }
}
