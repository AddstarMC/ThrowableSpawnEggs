package com.hawkfalcon.tse;

import java.util.HashMap;

import org.bukkit.GameMode;
import org.bukkit.entity.Egg;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerEggThrowEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.SpawnEgg;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin implements Listener {

	HashMap<Egg, EntityType> eggs = new HashMap<Egg, EntityType>();

	public void onDisable(){}

	public void onEnable(){
		getServer().getPluginManager().registerEvents(this, this);
	}


	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		if (player.hasPermission("tse.use")){
			ItemStack item = event.getItem();
			if ((event.getAction().equals(Action.RIGHT_CLICK_AIR)) || (event.getAction().equals(Action.RIGHT_CLICK_BLOCK))){
					if (item != null) {
						if (!(item.getData() instanceof SpawnEgg)) return;
						Egg egg = (Egg)event.getPlayer().launchProjectile(Egg.class);
						SpawnEgg segg = (SpawnEgg)item.getData();
						this.eggs.put(egg, segg.getSpawnedType());
						if (event.getPlayer().getGameMode().equals(GameMode.SURVIVAL)) {
							if(item.getAmount() > 1){
							item.setAmount(item.getAmount() - 1);
							}
							else {
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
		if (this.eggs.containsKey(egg)) {
		EntityType entity = (EntityType)this.eggs.get(egg);
		egg.getWorld().spawnEntity(egg.getLocation(), entity);
		}
	}
}