package classes;

import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;

import main.Main;
import net.md_5.bungee.api.ChatColor;

public class Gatherer implements Listener {

	public Main plugin = Main.getPlugin(Main.class);
	
	//OVERALL
	private int treasureFindChance = 1;
	//LOGS
	private int doubleLogChance = 50;
	//ORE
	private int doubleOreChance = 25;
	private int smeltChance = 25;
	
	public boolean brokeTree(Block block) {
		if (block.getType().name().toLowerCase().contains("log")) {
		     return true;
		}
		
		return false;
	}
	
	public boolean brokeOre(Block block) {
		if (block.getType().name().toLowerCase().contains("ore")) {
		     return true;
		}
		
		return false;
	}
	
	public void dropOre(Block block) {
		Material blockMat = block.getType();
		ItemStack blockItem = new ItemStack(blockMat, 1);	
		
		block.getWorld().dropItemNaturally(block.getLocation(), blockItem);
	}
	
	
	public boolean hasAAxe(Player player) {
		 Material mainHand = player.getInventory().getItemInMainHand().getType();
		
		 if (mainHand.equals((Material.WOODEN_AXE))) {
			 return true;
		 } else if (mainHand.equals((Material.STONE_AXE))) {
			 return true;
		 } else if (mainHand.equals((Material.GOLDEN_AXE))) {
			 return true;
		 } else if (mainHand.equals((Material.IRON_AXE))) {
			 return true;
		 } else if (mainHand.equals((Material.DIAMOND_AXE))) {
			 return true;
		 } else if (mainHand.equals((Material.NETHERITE_AXE))) {
			 return true;
		 }
		 
		 return false;
	}
	
	public boolean hasAPickaxe(Player player) {
		 Material mainHand = player.getInventory().getItemInMainHand().getType();
		
		 if (mainHand.equals((Material.WOODEN_PICKAXE))) {
			 return true;
		 } else if (mainHand.equals((Material.STONE_PICKAXE))) {
			 return true;
		 } else if (mainHand.equals((Material.GOLDEN_PICKAXE))) {
			 return true;
		 } else if (mainHand.equals((Material.IRON_PICKAXE))) {
			 return true;
		 } else if (mainHand.equals((Material.DIAMOND_PICKAXE))) {
			 return true;
		 } else if (mainHand.equals((Material.NETHERITE_PICKAXE))) {
			 return true;
		 }
		 
		 return false;
	}
	
	@EventHandler(priority=EventPriority.HIGH)
	private void onPlayerUse(BlockBreakEvent event){
		Player player = event.getPlayer();
		boolean isGatherer = (plugin.getConfig().getString("Users." + player.getUniqueId() + ".Class").contains("Gatherer"));
		
		int randomChance = (int)(Math.random() * 99 + 1);

		if (isGatherer) {
			if (randomChance <= treasureFindChance) { //default 12, 80 for testing.
				event.getPlayer().sendMessage(ChatColor.GREEN + "You have found treasure!");
				
				int count = 0;
				
				Random rand = new Random();
				int randomNum = rand.nextInt((719 - 1) + 1) + 1;
				
				for(Material material : Material.values() ) {
					count += 1;
					
					if (count == randomNum) {
						int randomAmount = rand.nextInt((5 - 1) + 1) + 1;
						event.getPlayer().getInventory().addItem(new ItemStack(material, randomAmount));
					}
					
				}
			}
			
			if (hasAAxe(event.getPlayer())) {
				if (brokeTree(event.getBlock())) {
					if (randomChance <= doubleLogChance) {
						dropOre(event.getBlock());
						event.getPlayer().sendMessage(ChatColor.GREEN + "You got lucky and obtained twice the logs!");
					}
				}
			}
			
			if (hasAPickaxe(event.getPlayer())) {
				if (randomChance <= doubleOreChance) {
					if (brokeOre(event.getBlock())) {
						
						if (randomChance <= smeltChance) {
							if (event.getBlock().getType() == Material.IRON_ORE) {
								event.getPlayer().getInventory().addItem(new ItemStack(Material.IRON_INGOT, 1));
								event.getPlayer().sendMessage(ChatColor.GREEN + "The ore was too hot, and you found an ingot instead!");
								
							} else if (event.getBlock().getType() == Material.GOLD_ORE) {
								event.getPlayer().getInventory().addItem(new ItemStack(Material.GOLD_INGOT, 1));
								event.getPlayer().sendMessage(ChatColor.GREEN + "The ore was too hot, and you found an ingot instead!");
								
							} else if (event.getBlock().getType() == Material.NETHER_GOLD_ORE) {
								event.getPlayer().getInventory().addItem(new ItemStack(Material.GOLD_INGOT, 1));
								event.getPlayer().sendMessage(ChatColor.GREEN + "The ore was too hot, and you found an ingot instead!");
								
							} else if (event.getBlock().getType() == Material.NETHER_QUARTZ_ORE) {
								event.getPlayer().getInventory().addItem(new ItemStack(Material.QUARTZ, 1));
								event.getPlayer().sendMessage(ChatColor.GREEN + "The ore was too hot, and you found a quartz instead!");
								
							}
						} else {
							if (event.getBlock().getType() == Material.EMERALD_ORE) {
								event.getPlayer().getInventory().addItem(new ItemStack(Material.EMERALD, 1));
								event.getPlayer().sendMessage(ChatColor.GREEN + "You got lucky and obtained twice the emeralds!");
								
							} else if (event.getBlock().getType() == Material.DIAMOND_ORE) {
								event.getPlayer().getInventory().addItem(new ItemStack(Material.DIAMOND, 1));
								event.getPlayer().sendMessage(ChatColor.GREEN + "You got lucky and obtained twice the diamonds!");
							} else {
								dropOre(event.getBlock());
								event.getPlayer().sendMessage(ChatColor.GREEN + "You got lucky and obtained twice the ore!");
							}
						}
					} else {
						if (randomChance <= smeltChance) {
							if (event.getBlock().getType() == Material.IRON_ORE) {
								event.getPlayer().getInventory().addItem(new ItemStack(Material.IRON_INGOT, 1));
								event.getPlayer().sendMessage(ChatColor.GREEN + "The ore was too hot, and you found an ingot instead!");
								
							} else if (event.getBlock().getType() == Material.GOLD_ORE) {
								event.getPlayer().getInventory().addItem(new ItemStack(Material.GOLD_INGOT, 1));
								event.getPlayer().sendMessage(ChatColor.GREEN + "The ore was too hot, and you found an ingot instead!");
								
							} else if (event.getBlock().getType() == Material.NETHER_GOLD_ORE) {
								event.getPlayer().getInventory().addItem(new ItemStack(Material.GOLD_INGOT, 1));
								event.getPlayer().sendMessage(ChatColor.GREEN + "The ore was too hot, and you found an ingot instead!");
								
							} else if (event.getBlock().getType() == Material.NETHER_QUARTZ_ORE) {
								event.getPlayer().getInventory().addItem(new ItemStack(Material.QUARTZ, 1));
								event.getPlayer().sendMessage(ChatColor.GREEN + "The ore was too hot, and you found a quartz instead!");
								
							}
						}
					}
				}
			}
		}
	}
	
	
}
