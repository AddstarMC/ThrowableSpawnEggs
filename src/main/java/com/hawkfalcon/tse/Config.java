package com.hawkfalcon.tse;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;

import java.io.File;
import java.util.*;

/**
 * AutoConfiguration System
 * <p>
 * Allows for simple config file creation and handling.
 * Any class that extends this class can specify its fields to be config values using {@link ConfigField}
 * <p>
 * You should specify a default value for each field
 * <p>
 * Valid primitive field types are:
 * <ul>
 * <li>Short</li>
 * <li>Integer</li>
 * <li>Long</li>
 * <li>Float</li>
 * <li>Double</li>
 * <li>Boolean</li>
 * <li>String</li>
 * </ul>
 * <p>
 * Valid Complex field types are:
 * <ul>
 * <li>Array of any primitive type</li>
 * <li>List of any primitive type</li>
 * <li>Set of any primitive type</li>
 * </ul>
 * <p>
 * NOTE: You cannot use an abstract type for the type of a config field. The loader needs to know what class to instantiate.
 *
 * @author Schmoller
 * @version 1.5
 */

public class Config extends AutoConfig {

    protected Config(File file) {
        super(file);
    }

    @ConfigField(name = "blacklist", comment = "if set true will prevent the  use of defined blacklisted spawn eggs")
    public boolean blackList = true;
    @ConfigField(name = "blacklisted", comment = "the mobs that are prevented from being spawned with tse")
    public List<String> blackListed = new ArrayList<>(
            Arrays.asList(
                    EntityType.CREEPER.name(),
                    EntityType.GHAST.name()
            )
    );
    @ConfigField(name = "thrownblocks", comment = "Allow Block Throw")
    public boolean throwBlocks = false;
    @ConfigField(name = "throwables", comment = "blocks that can be thrown")
    public List<String> blockThrow = new ArrayList<>(
            Collections.singletonList(
                    Material.COBBLESTONE.name()
            )
    );
    @ConfigField(name = "main-hand-ignore", comment = "List of Materails that are ignore if in main hand and we will look at offhand")
    public Set<String> mainHandIgnore = new HashSet<>(
            Arrays.asList(Material.WOOD_SWORD.name(),
                    Material.STONE_SWORD.name(),
                    Material.IRON_SWORD.name(),
                    Material.DIAMOND_SWORD.name(),
                    Material.WOOD_AXE.name(),
                    Material.STONE_AXE.name(),
                    Material.IRON_AXE.name(),
                    Material.DIAMOND_AXE.name(),
                    Material.WOOD_PICKAXE.name(),
                    Material.STONE_PICKAXE.name(),
                    Material.IRON_PICKAXE.name(),
                    Material.DIAMOND_PICKAXE.name(),
                    Material.IRON_INGOT.name(),
                    Material.GOLD_INGOT.name()));
}
