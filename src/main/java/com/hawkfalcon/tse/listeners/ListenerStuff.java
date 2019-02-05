package com.hawkfalcon.tse.listeners;

import com.hawkfalcon.tse.Main;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
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
    
    HashMap<Egg, EntityType> eggs = new HashMap<>();
    HashMap<Egg, Material> blocks = new HashMap<>();
    
    static final EnumSet<Material> MainHandIgnore = EnumSet.of(
                    Material.WOODEN_SWORD,
                    Material.STONE_SWORD,
                    Material.IRON_SWORD,
                    Material.DIAMOND_SWORD,
                    Material.WOODEN_AXE,
                    Material.STONE_AXE,
                    Material.IRON_AXE,
                    Material.DIAMOND_AXE,
                    Material.WOODEN_PICKAXE,
                    Material.STONE_PICKAXE,
                    Material.IRON_PICKAXE,
                    Material.DIAMOND_PICKAXE,
                    Material.IRON_INGOT,
                    Material.GOLD_INGOT
    );

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
                    String mat = item.getType().name().toUpperCase().replace("_SPAWN_EGG", "");
                    EntityType spawnType = EntityType.valueOf(mat);
                    /*switch(item.getType()){
                        case ELDER_GUARDIAN_SPAWN_EGG:
                            type = EntityType.ELDER_GUARDIAN;
                            break;
                        case BAT_SPAWN_EGG:
                            type = EntityType.BAT;break;
                        case BLAZE_SPAWN_EGG:
                            type = EntityType.BLAZE;break;
                        case CAVE_SPIDER_SPAWN_EGG:
                            type = EntityType.CAVE_SPIDER;
                            break;
                        case CHICKEN_SPAWN_EGG:
                            type = EntityType.CHICKEN;break;
                        case COD_SPAWN_EGG:
                            type = EntityType.COD;break;
                        case COW_SPAWN_EGG:
                            type = EntityType.COW;break;
                        case CREEPER_SPAWN_EGG:
                            type = EntityType.CREEPER;break;
                        case DOLPHIN_SPAWN_EGG:
                            type = EntityType.DOLPHIN;break;
                        case DONKEY_SPAWN_EGG:
                            type = EntityType.DONKEY;break;
                        case SPIDER_SPAWN_EGG:
                        case SALMON_SPAWN_EGG:
                        case SHEEP_SPAWN_EGG:
                        case SHULKER_SPAWN_EGG:
                        case DROWNED_SPAWN_EGG:
                        case ENDERMAN_SPAWN_EGG:
                        case ENDERMITE_SPAWN_EGG:
                        case SILVERFISH_SPAWN_EGG:
                        case EVOKER_SPAWN_EGG:
                        case GHAST_SPAWN_EGG:
                        case GUARDIAN_SPAWN_EGG:
                        case HORSE_SPAWN_EGG:
                        case HUSK_SPAWN_EGG:
                        case LLAMA_SPAWN_EGG:
                        case MAGMA_CUBE_SPAWN_EGG:
                        case MOOSHROOM_SPAWN_EGG:
                        case MULE_SPAWN_EGG:
                        case OCELOT_SPAWN_EGG:
                        case PARROT_SPAWN_EGG:
                        case PHANTOM_SPAWN_EGG:
                        case PIG_SPAWN_EGG:
                        case POLAR_BEAR_SPAWN_EGG:
                        case SKELETON_HORSE_SPAWN_EGG:
                        case PUFFERFISH_SPAWN_EGG:
                        case RABBIT_SPAWN_EGG:
                        case SKELETON_SPAWN_EGG:
                        case SLIME_SPAWN_EGG:
                        case SQUID_SPAWN_EGG:
                        case STRAY_SPAWN_EGG:
                        case TROPICAL_FISH_SPAWN_EGG:
                        case TURTLE_SPAWN_EGG:
                        case VEX_SPAWN_EGG:
                        case VILLAGER_SPAWN_EGG:
                        case VINDICATOR_SPAWN_EGG:
                        case WITCH_SPAWN_EGG:
                        case WITHER_SKELETON_SPAWN_EGG:
                        case WOLF_SPAWN_EGG:
                        case ZOMBIE_HORSE_SPAWN_EGG:
                        case ZOMBIE_PIGMAN_SPAWN_EGG:
                        case ZOMBIE_SPAWN_EGG:
                        case ZOMBIE_VILLAGER_SPAWN_EGG:
                    }*/
                    if (spawnType == null) return;
                    if (blackListOn && blackList.size() > 0) {
                        if (blackList.contains(spawnType)) {
                            spawnType = EntityType.CHICKEN;
                        }
                    }
                    Egg egg = event.getPlayer().launchProjectile(Egg.class);
                    egg.setCustomName(item.getItemMeta().getDisplayName());
                    eggs.put(egg, spawnType);

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
                            blocks.put(egg, item.getType());
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
            EntityType item = eggs.get(egg);
            event.setHatchingType(item);
            Entity spawn = egg.getWorld().spawnEntity(egg.getLocation(), item);
            if (spawn == null) {
                event.getPlayer().sendMessage("Spawning Error");
            } else {
                if (egg.getCustomName() != null) spawn.setCustomName(egg.getCustomName());
            }
            eggs.remove(egg);
        }
        if (blocks.containsKey(egg)) {
            boolean placed = false;
            Location loc = egg.getLocation();
            Block current = loc.getBlock();
                while (!placed) {
                    if (current.isEmpty() || current.isLiquid()) {
                        current.setType(blocks.get(egg));
                        placed = true;
                    } else {
                        loc.add(0, 1, 0);
                    }
                }
            blocks.remove(egg);
            }
            event.setHatching(false);
        }
    }
