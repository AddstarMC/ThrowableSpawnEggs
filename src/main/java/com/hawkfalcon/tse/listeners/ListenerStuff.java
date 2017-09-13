package com.hawkfalcon.tse.listeners;

import com.hawkfalcon.tse.Main;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerEggThrowEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SpawnEggMeta;

import java.util.*;

public class ListenerStuff implements Listener {
    private Main plugin;
    private boolean blackListOn;
    private List<String> blackList;
    private List<String> throwableBlocks;

    HashMap<Egg, ItemStack> eggs = new HashMap<>();

    static final Set<Material> MainHandIgnore = new HashSet<Material>(
            Arrays.asList(new Material[]{
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
        this.throwableBlocks = plugin.getConfig().getStringList("blockthrow");
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (player.hasPermission("tse.use") || player.hasPermission("tse.blockthrow")) {
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
                if (item.getItemMeta() instanceof SpawnEggMeta) {

                    SpawnEggMeta sMeta = (SpawnEggMeta) item.getItemMeta();
                    EntityType spawnType = sMeta.getSpawnedType();

                    if (spawnType == null) return;
                    if (blackListOn && blackList.size() > 0) {
                        if (blackList.contains(spawnType)) {
                            spawnType = EntityType.CHICKEN;
                        }
                    }
                    Egg egg = event.getPlayer().launchProjectile(Egg.class);
                    sMeta.setSpawnedType(spawnType);
                    item.setItemMeta(sMeta);
                    eggs.put(egg, item);

                    // Deplete the relevant item stack
                    depleteStack(player,item,useMainHand);
                    event.setCancelled(true);
                    return;
                }else{
                    if(player.hasPermission("tse.blockthrow")){
                        if ((event.getAction().equals(Action.RIGHT_CLICK_BLOCK))) {
                            return;
                        }
                        if (throwableBlocks.contains(item.getType().name().toLowerCase()) && item.getType().isBlock()) {
                            Egg egg = event.getPlayer().launchProjectile(Egg.class);
                            eggs.put(egg,item);
                            depleteStack(player,item,useMainHand);
                            event.setCancelled(true);
                            return;
                        }
                    }
                }
            }
            return;
        }
        event.getPlayer().sendMessage("No Permission to throw eggs");
    }

    private void depleteStack(Player player, ItemStack item, boolean useMainHand){
        GameMode gm = player.getGameMode();
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
    }

     //todo Spawn complete entity not just the type.
    @EventHandler()
    public void throwEgg(PlayerEggThrowEvent event) {
        Egg egg = event.getEgg();
        if (eggs.containsKey(egg)) {
            ItemStack item = eggs.get(egg);
            if(item.getItemMeta() instanceof SpawnEggMeta) {
                SpawnEggMeta tEgg = (SpawnEggMeta) item.getItemMeta();
                Entity spawn = egg.getWorld().spawnEntity(egg.getLocation(), tEgg.getSpawnedType());
                if (spawn == null) {
                    event.getPlayer().sendMessage("Spawning Error");
                } else {
                    if (tEgg.getDisplayName() != null) spawn.setCustomName(tEgg.getDisplayName());
                }
            }else{
                boolean placed = false;
                Location loc = egg.getLocation();
                Block current = loc.getBlock();
                while (!placed) {
                    if (current.isEmpty() || current.isLiquid()) {
                        current.setType(item.getType());
                        placed = true;
                    } else {
                        loc.add(0, 1, 0);
                    }
                }
            }
            eggs.remove(egg);
            event.setHatching(false);
        }
    }
}
