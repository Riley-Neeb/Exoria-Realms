package classes;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import main.Main;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;

public class Scout implements Listener {
	
	public Main plugin = Main.getPlugin(Main.class);
	
	private int fishAction = 1;
	private int pufferAction = 1;
	
	public void failMessage(Player player) {
		TextComponent failMessage = new TextComponent("Scout: Your not wearing the right armor!");
		failMessage.setColor(ChatColor.DARK_RED);
		failMessage.setBold(true);
		
		player.spigot().sendMessage(failMessage);
	}
	
	public boolean hasASword(Player player) {
		Material mainHand = player.getInventory().getItemInMainHand().getType();
		 if (mainHand.equals((Material.WOODEN_SWORD))) {
			 return true;
		 } else  if (mainHand.equals((Material.WOODEN_SWORD))) {
			 return true;
		 } else  if (mainHand.equals((Material.STONE_SWORD))) {
			 return true;
		 } else  if (mainHand.equals((Material.GOLDEN_SWORD))) {
			 return true;
		 } else  if (mainHand.equals((Material.IRON_SWORD))) {
			 return true;
		 } else  if (mainHand.equals((Material.DIAMOND_SWORD))) {
			 return true;
		 } else  if (mainHand.equals((Material.NETHERITE_SWORD))) {
			 return true;
		 }
		 return false;
	}
	
	public boolean wearingIron(Player player) {
		boolean isClass = (plugin.getConfig().getString("Users." + player.getUniqueId() + ".Class").contains("Scout"));
		
		if (isClass) {
			ItemStack Helm = player.getInventory().getHelmet();
			ItemStack Chest = player.getInventory().getChestplate();
			ItemStack Legs = player.getInventory().getLeggings();
			ItemStack Boots = player.getInventory().getBoots();
			
			boolean wearFail = false;
					
			if (Helm != null && Chest != null && Legs != null && Boots != null) {
				if (Helm.getType().equals(Material.GOLDEN_HELMET)) {
					if (Chest.getType().equals(Material.IRON_CHESTPLATE)) {
						if (Legs.getType().equals(Material.IRON_LEGGINGS)) {
							if (Boots.getType().equals(Material.GOLDEN_BOOTS)) {
								return true;
							} else {
								wearFail = true;
							}
						} else {
							wearFail = true;
							failMessage(player);
						}
					} else {
						failMessage(player);
						wearFail = true;
					}
				} else {
					failMessage(player);
					wearFail = true;
				}	
			} else {
				failMessage(player);
				wearFail = true;
			}
			
			if (wearFail == true) {
				failMessage(player);
				return false;
			}
		}
		
		return false;
	}
	
	public boolean isSameParty(Player player, Player player2) {
		String party1 = getPartyName(player);
		String party2 = getPartyName(player2);
		
		if (party1 != null && party2 != null) {
			if (party1.equals(party2)) {
				return true;
			} else {
				return false;
			}
		}
		
		return false;
	}
	
	public String getPartyName(Player player) {
		if (isInParty(player)) {
			for (String[] PartiesByLeader: plugin.parties.keySet()) {
				for(int i = 0; i < PartiesByLeader.length; i++){
					if (PartiesByLeader[i].equals(player.getName())) {
						String partyName = plugin.parties.get(PartiesByLeader);
						
						return partyName;
					}
				}
			}
		} else {
			return null;
		}
		
		
		return null;
	}
	
	public boolean isInParty(Player player) {
		for (String[] PartiesByLeader: plugin.parties.keySet()) {
			for(int i = 0; i < PartiesByLeader.length; i++){
				if (PartiesByLeader[i].equals(player.getName())) {
					return true;
				}
			}
		}
		
		return false;
	}
	
	
	@EventHandler
	private void onEntityHit(EntityDamageByEntityEvent event) {
        Player damager = (Player) event.getDamager();
		Player damagedEntity = (Player) event.getEntity();
		
		boolean isScout = (plugin.getConfig().getString("Users." + damager.getUniqueId() + ".Class").contains("Scout"));
		
		if (isScout) {
			if (isSameParty(damager, damagedEntity)) {

			} else {
				if (fishAction == 1) {
					String PoisonUUID = damager.getUniqueId()+"Poison";
					
					if (!plugin.cooldowns.containsKey(PoisonUUID)) {
	        			plugin.masterCD = 3;
		        		plugin.cooldowns.put(PoisonUUID, plugin.masterCD);
		        		
		        		damagedEntity.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 40, 1));
		        		damager.sendMessage(ChatColor.GREEN+"You poisoned "+damagedEntity.getName()+"!");
					}
				} else if (fishAction == 2) {
					String IckyFeelingUUID = damager.getUniqueId()+"IckyFeeling";
					
					if (!plugin.cooldowns.containsKey(IckyFeelingUUID)) {
						plugin.masterCD = 3;
		        		plugin.cooldowns.put(IckyFeelingUUID, plugin.masterCD);
		        		
						damagedEntity.sendMessage(ChatColor.ITALIC+"You got an icky feeling...");
						damagedEntity.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 200, 3));
					}
				}	
			}
		}
        
	}
	
	@EventHandler(priority=EventPriority.HIGH)
	private void onMove(PlayerMoveEvent event){
		Player player = event.getPlayer();
		boolean isScout = (plugin.getConfig().getString("Users." + player.getUniqueId() + ".Class").contains("Scout"));
		
		if (isScout) {
	        player.setWalkSpeed(0.4F);
	    }
	}
	        
	@EventHandler(priority=EventPriority.HIGH)
	private void onPlayerUse(PlayerInteractEvent event){
	
		if(event.getItem() == null) {
			return;
	    }
	
    	
		if ((event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) && event.getHand() == EquipmentSlot.HAND) {
			Player player = event.getPlayer();
			boolean isScout = (plugin.getConfig().getString("Users." + player.getUniqueId() + ".Class").contains("Scout"));
			Material mainHand = player.getInventory().getItemInMainHand().getType();
			
			if (isScout) {
		        if (wearingIron(player)) {   
        			if (mainHand.equals(Material.PUFFERFISH)) {
		       			pufferAction += 1;
		       		 
		       		 	if (pufferAction > 3) {
		       		 		pufferAction = 1;
		              	}
		       		 
		       			if (pufferAction == 1) {
		             		//Neurotoxin!
		             		player.sendMessage(ChatColor.GREEN + "[1 / 2]" + ChatColor.GOLD + " You ready your weapons " + ChatColor.BLUE + "Neurotoxin");
		             		player.playSound(player.getLocation(), Sound.ITEM_BUCKET_EMPTY_FISH, 2F, 1F);
		             		
		             	} else if (pufferAction == 2) {
		             		//Fish to The Face!
		             		player.sendMessage(ChatColor.GREEN + "[2 / 2]" + ChatColor.GOLD + " You ready your weapons " + ChatColor.BLUE + "Fish to The Face!");
		             		player.playSound(player.getLocation(), Sound.ITEM_BUCKET_EMPTY_FISH, 2F, 1F);
		             		
		             	}
		        	} else if (mainHand.equals(Material.TROPICAL_FISH)) {
		       			fishAction += 1;
		       		 
		       		 	if (fishAction > 3) {
		       		 	fishAction = 1;
		              	}
		       		 
		       			if (fishAction == 1) {
		             		//Slippery Feet
		             		player.sendMessage(ChatColor.GREEN + "[1 / 3]" + ChatColor.GOLD + " You ready your " + ChatColor.BLUE + "Slippery Feet");
		             		player.playSound(player.getLocation(), Sound.ENTITY_BOAT_PADDLE_WATER, 2F, 1F);
		             		
		             	} else if (fishAction == 2) {
		             		//Slide
		             		player.sendMessage(ChatColor.GREEN + "[2 / 3]" + ChatColor.GOLD + " You ready your " + ChatColor.BLUE + "Power Slide");
		             		
		             		player.playSound(player.getLocation(), Sound.ENTITY_BOAT_PADDLE_WATER, 2F, 1F);
		             	} else if (fishAction == 3) {
		             		//That Lovely Feeling...
		             		player.playSound(player.getLocation(), Sound.ENTITY_BOAT_PADDLE_WATER, 2F, 1F);
		             		player.sendMessage(ChatColor.GREEN + "[3 / 3]" + ChatColor.GOLD + " You ready your " + ChatColor.BLUE + "That Lovely Feeling...");
		             	}
		        	}
	        	}
			}
		} else if ((event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) && event.getHand() == EquipmentSlot.HAND) {
			Player player = event.getPlayer();
			boolean isScout = (plugin.getConfig().getString("Users." + player.getUniqueId() + ".Class").contains("Scout"));
			Material mainHand = player.getInventory().getItemInMainHand().getType();
			
			if (isScout) {
		        if (wearingIron(player)) {   
        			if (mainHand.equals(Material.TROPICAL_FISH)) {
        				if (fishAction == 1) {
		             		//Slippery Feet
        					String SFUUID = player.getUniqueId()+"Slippery Feet";
		       				
		       				if (!plugin.cooldowns.containsKey(SFUUID)) {
			        			plugin.masterCD = 30;
				        		plugin.cooldowns.put(SFUUID, plugin.masterCD);
				        		
				        		player.sendMessage(ChatColor.YELLOW + "Slippery Feet");
			             		player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 200, 2));
			             		player.playSound(player.getLocation(), Sound.ENTITY_COW_MILK, 2F, 1F);
		       				} else {
			        			player.sendMessage(ChatColor.RED + "[Slippery Feet] On cooldown, " + ChatColor.YELLOW + plugin.cooldowns.get(SFUUID) + " seconds" + ChatColor.RED + " left.");
		       				}
		             		
		             	} else if (fishAction == 2) {
		             		//Power Slide
		             		String PSUUID = player.getUniqueId()+"Power Slide";
		       				
		       				if (!plugin.cooldowns.containsKey(PSUUID)) {
			        			plugin.masterCD = 15;
				        		plugin.cooldowns.put(PSUUID, plugin.masterCD);
				        		
				        		Vector unitVector = new Vector(player.getLocation().getDirection().getX(), 0, player.getLocation().getDirection().getZ());
			             		unitVector = unitVector.normalize();             
			             		player.setVelocity(unitVector.multiply(5));
			             		
			             		player.sendMessage(ChatColor.YELLOW + "Power Slide");
			             		player.playSound(player.getLocation(), Sound.ENTITY_COW_MILK, 2F, 1F);
		       				} else {
			        			player.sendMessage(ChatColor.RED + "[Power Slide] On cooldown, " + ChatColor.YELLOW + plugin.cooldowns.get(PSUUID) + " seconds" + ChatColor.RED + " left.");
		       				}

		             	} else if (fishAction == 3) {
		             		//That Lovely Feeling...
		             		String TLFUUID = player.getUniqueId()+"That Lovely Feeling...";
		       				
		       				if (!plugin.cooldowns.containsKey(TLFUUID)) {
			        			plugin.masterCD = 60;
				        		plugin.cooldowns.put(TLFUUID, plugin.masterCD);
				        		
				        		player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 40, 5));
			             		player.playSound(player.getLocation(), Sound.ENTITY_COW_MILK, 2F, 1F);
			             		
			             		player.sendMessage(ChatColor.YELLOW + "That Lovely Feeling...");
			             		player.playSound(player.getLocation(), Sound.ENTITY_COW_MILK, 2F, 1F);
		       				} else {
			        			player.sendMessage(ChatColor.RED + "[That Lovely Feeling...] On cooldown, " + ChatColor.YELLOW + plugin.cooldowns.get(TLFUUID) + " seconds" + ChatColor.RED + " left.");
		       				}
		             	}
        			}
		        }
			}
		}
	}
	
	
	
	
	
	
	
	
	
	
}