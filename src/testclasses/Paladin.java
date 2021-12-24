package testclasses;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import main.Main;
import net.md_5.bungee.api.ChatColor;

public class Paladin implements Listener {

	public Main plugin = Main.getPlugin(Main.class);
	private int paladinAction = 0;
	private boolean invulnerable = false;
	
	public boolean wearingGold(Player player) {
		ItemStack Helm = player.getInventory().getHelmet();
		ItemStack Chest = player.getInventory().getChestplate();
		ItemStack Legs = player.getInventory().getLeggings();
		ItemStack Boots = player.getInventory().getBoots();
		
		if (Helm.getType().equals(Material.GOLDEN_HELMET)) {
			if (Chest.getType().equals(Material.GOLDEN_CHESTPLATE)) {
				if (Legs.getType().equals(Material.GOLDEN_LEGGINGS)) {
					if (Boots.getType().equals(Material.GOLDEN_BOOTS)) {
						return true;
					} 
				}
			}
		}
		return false;	
	}
	
	public boolean goldWeapon(Player player) {
		Material mainHand = player.getInventory().getItemInMainHand().getType();
		 if (mainHand.equals((Material.GOLDEN_SWORD))) {
			 return true;
		 } else if (mainHand.equals((Material.GOLDEN_AXE))) {
			 return true;
		 } else if (mainHand.equals((Material.GOLDEN_SHOVEL))) {
			 return true;
		 } else if (mainHand.equals((Material.GOLDEN_HOE))) {
			 return true;
		 } else if (mainHand.equals((Material.GOLDEN_PICKAXE))) {
			 return true;
		 }
		 
		 return false;
	}
	
	public boolean goldWep(Player player) {
		ItemStack Helm = player.getInventory().getHelmet();
		ItemStack Chest = player.getInventory().getChestplate();
		ItemStack Legs = player.getInventory().getLeggings();
		ItemStack Boots = player.getInventory().getBoots();
		
		if (Helm.getType().equals(Material.GOLDEN_HELMET)) {
			if (Chest.getType().equals(Material.GOLDEN_CHESTPLATE)) {
				if (Legs.getType().equals(Material.GOLDEN_LEGGINGS)) {
					if (Boots.getType().equals(Material.GOLDEN_BOOTS)) {
						return true;
					} 
				}
			}
		}
		return false;	
	}
	
	private boolean getLookingAt(Player player, Player player1) {
	    Location eye = player.getEyeLocation();
	    Vector toEntity = player1.getEyeLocation().toVector().subtract(eye.toVector());
	    double dot = toEntity.normalize().dot(eye.getDirection());
	   
	    return dot > 0.99D;
	}
	
	@EventHandler
	private void onItemDamage(PlayerItemDamageEvent event) {
		
		boolean isPaladin = (plugin.getConfig().getString("Users." + event.getPlayer().getUniqueId() + ".Class").contains("Paladin"));
		
		if (isPaladin) {
			if (wearingGold(event.getPlayer())) {
				if (event.getItem().getType().equals(Material.GOLDEN_HELMET)) {
					event.setDamage(event.getDamage()/200);
				}
				if (event.getItem().getType().equals(Material.GOLDEN_CHESTPLATE)) {
					event.setDamage(event.getDamage()/200);
				}
				if (event.getItem().getType().equals(Material.GOLDEN_LEGGINGS)) {
					event.setDamage(event.getDamage()/200);
				}
				if (event.getItem().getType().equals(Material.GOLDEN_BOOTS)) {
					event.setDamage(event.getDamage()/200);
				}
			}
		}
	}
	
	@EventHandler(priority=EventPriority.HIGH)
	private void onPlayerUse(PlayerInteractEvent event){
		
		if(event.getItem() == null) {
			return;
	    }
	
    	
        if ((event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) && event.getHand() == EquipmentSlot.HAND) {
        	Player player = event.getPlayer();
        	boolean isPaladin = (plugin.getConfig().getString("Users." + player.getUniqueId() + ".Class").contains("Paladin"));
        	if (isPaladin) {
    	        if (wearingGold(player)) {   
    	        	Material mainHand = player.getInventory().getItemInMainHand().getType();
		    		if (mainHand.equals(Material.GOLDEN_SHOVEL)) {
		    			paladinAction += 1;
		       		 
		       		 	if (paladinAction > 3) {
		       		 		paladinAction = 1;
		              	}
		       		 
		       			if (paladinAction == 1) {
		             		//Spell Hand of Light
		             		player.sendMessage(ChatColor.GREEN + "[1 / 3]" + ChatColor.BLUE + " You ready your spell " + ChatColor.GOLD + "Hand of Light");
		             		player.playSound(player.getLocation(), Sound.ENTITY_COW_MILK, 2F, 1F);
		             		
		             	} else if (paladinAction == 2) {
		             		//Spell Holy Remedy
		             		player.sendMessage(ChatColor.GREEN + "[2 / 3]" + ChatColor.BLUE + " You ready your spell " + ChatColor.GOLD + "Holy Remedy");
		             		player.playSound(player.getLocation(), Sound.ENTITY_COW_MILK, 2F, 1F);
		             		
		             	} else if (paladinAction == 3) {
		             		//Spell Holy Shield
		             		player.sendMessage(ChatColor.GREEN + "[3 / 3]" + ChatColor.BLUE + " You ready your spell " + ChatColor.GOLD + "Holy Shield");
		             		player.playSound(player.getLocation(), Sound.ENTITY_COW_MILK, 2F, 1F);
		             	}
		    		}
    	        }
        	}
        } else if ((event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) && event.getHand() == EquipmentSlot.HAND) {
        	Player player = event.getPlayer();
        	boolean isPaladin = (plugin.getConfig().getString("Users." + player.getUniqueId() + ".Class").contains("Paladin"));
        	if (isPaladin) {
    	        if (wearingGold(player)) {   
    	        	Material mainHand = player.getInventory().getItemInMainHand().getType();
		    		if (mainHand.equals(Material.GOLDEN_SHOVEL)) {
		    			if (paladinAction == 1) {
		             		//Spell Hand of Light
		    				String HandOfLightUUID = player.getUniqueId()+"HandOfLight";
		    				if (!plugin.cooldowns.containsKey(HandOfLightUUID)) {
		    					if (player.getInventory().containsAtLeast(new ItemStack(Material.REDSTONE), 1)) {
						  			player.getInventory().removeItem(new ItemStack(Material.REDSTONE,1));
			    				
					        		for(Entity getEntity : player.getNearbyEntities(20, 20, 20)){
				        			    //Example
				        			    if(getEntity.getType() == EntityType.PLAYER){
				        			        Player getPlayer = (Player) getEntity;
				        			        if (getLookingAt(player, getPlayer)) {
				        			        	plugin.masterCD = 10;
								        		plugin.cooldowns.put(HandOfLightUUID, plugin.masterCD);
								        		//player.playSound(player.getLocation(), Sound.ENTITY_RAVAGER_DEATH, 2F, 1F);
				        			        	player.sendMessage(ChatColor.BLUE + "You casted Hand of Light on " + ChatColor.GOLD + getPlayer.getName());
				        			        	getPlayer.addPotionEffect(new PotionEffect(PotionEffectType.HEAL, (int) 7, 0));
				        			        	player.setHealth(player.getHealth()+4);
				        			        }
				        			    }
				        			}
		    					}
		    				} else {
		    					player.sendMessage(ChatColor.BLUE + "[Spell] Hand of Light" + ChatColor.RED + " on cooldown, " + ChatColor.YELLOW + plugin.cooldowns.get(HandOfLightUUID) + " seconds" + ChatColor.RED + " left.");	
		    				}
    				
    				
		             	} else if (paladinAction == 2) {
		             		//Spell Holy Remedy
		             		String HolyRemedyUUID = player.getUniqueId()+"HolyRemedy";
		             		if (!plugin.cooldowns.containsKey(HolyRemedyUUID)) {
		    					if (player.getInventory().containsAtLeast(new ItemStack(Material.REDSTONE), 1)) {
						  			player.getInventory().removeItem(new ItemStack(Material.REDSTONE,1));
						  			plugin.masterCD = 20;
					        		plugin.cooldowns.put(HolyRemedyUUID, plugin.masterCD);
					        		player.setHealth(player.getHealth()+6);
		    					}
		             		} else {
		             			player.sendMessage(ChatColor.BLUE + "[Spell] Holy Remedy" + ChatColor.RED + " on cooldown, " + ChatColor.YELLOW + plugin.cooldowns.get(HolyRemedyUUID) + " seconds" + ChatColor.RED + " left.");	
		             		}
		             		
		             	} else if (paladinAction == 3) {
		             		//Spell Holy Shield
		             		String HolyShieldUUID = player.getUniqueId()+"HolyShield";
		             		
		             		if (!plugin.cooldowns.containsKey(HolyShieldUUID)) {
		             			plugin.masterCD = 120; //5 minutes
				        		plugin.cooldowns.put(HolyShieldUUID, plugin.masterCD);

				        		player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 140, 50));
		             		}
		             	}
		    		}
    	        }
    		}
        }
    }
	
	
}

