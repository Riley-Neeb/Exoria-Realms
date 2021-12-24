package classes;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import main.Main;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;

public class Berserker implements Listener {
	
	public Main plugin = Main.getPlugin(Main.class);
	
	private int berserkerAction = 0;
	private String Ability;
	
	
	public boolean wearingBerserker(Player player) {
		boolean isClass = (plugin.getConfig().getString("Users." + player.getUniqueId() + ".Class").contains("Berserker"));
		
		if (isClass) {
			TextComponent failMessage = new TextComponent("Your not wearing the right armor!");
			failMessage.setColor(ChatColor.DARK_RED);
			failMessage.setBold(true);
			 
			ItemStack Helm = player.getInventory().getHelmet();
			ItemStack Chest = player.getInventory().getChestplate();
			ItemStack Legs = player.getInventory().getLeggings();
			ItemStack Boots = player.getInventory().getBoots();
			
			if (Helm == null && Boots == null) {
				if (Chest != null && Legs != null) {
					if (Chest.getType().equals(Material.LEATHER_CHESTPLATE)) {
						if (Legs.getType().equals(Material.LEATHER_LEGGINGS)) {
							return true;
						} else {
							player.spigot().sendMessage(failMessage);
						}
					} else {
						player.spigot().sendMessage(failMessage);
					}
				}
			}
			
			return false;	
		}
		
		return false;	
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
        boolean isBerserker = (plugin.getConfig().getString("Users." + event.getDamager().getUniqueId() + ".Class").contains("Berserker"));
        
		Player damager = (Player) event.getDamager();
		Player damagedEntity = (Player) event.getEntity();
		
		if (isBerserker) {
			if (isSameParty(damager, damagedEntity)) {

			} else {
	        	if (wearingBerserker((Player) event.getDamager())) { 
        			if (Ability == "Headbutt") {
        				String HeadbuttUUID = event.getDamager().getUniqueId()+"Headbutt";
        				
        				if (!plugin.cooldowns.containsKey(HeadbuttUUID)) {
    			        	plugin.masterCD = 20;
    		        		plugin.cooldowns.put(HeadbuttUUID, plugin.masterCD);

    		        		event.getDamager().sendMessage(ChatColor.BLUE + "You used " + ChatColor.GOLD + "Headbutt");
    		        		((LivingEntity) event.getEntity()).addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 60, 100));
        				}
        			}
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
			boolean isBerserker = (plugin.getConfig().getString("Users." + player.getUniqueId() + ".Class").contains("Berserker"));
			if (isBerserker) {
		        if (wearingBerserker(player)) {   
	        		if (hasAAxe(player)) {
	        			
	        			String HeadbuttUUID = player.getUniqueId()+"Headbutt";
	        			if (!plugin.cooldowns.containsKey(HeadbuttUUID)) {
	        				player.sendMessage(ChatColor.BLUE + "You ready your " + ChatColor.GOLD + "Headbutt");
			        		Ability = "Headbutt";
	        			} else {
	        				player.sendMessage(ChatColor.BLUE + "Headbutt" + ChatColor.RED + " on cooldown, " + ChatColor.YELLOW + plugin.cooldowns.get(HeadbuttUUID) + " seconds" + ChatColor.RED + " left.");
	        			}
	             		    		
	        		}
	        	}
			}
		}
	        	
    	if ((event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) && event.getHand() == EquipmentSlot.HAND) {
    		Player player = event.getPlayer();
			boolean isBerserker = (plugin.getConfig().getString("Users." + player.getUniqueId() + ".Class").contains("Berserker"));
			Material mainHand = player.getInventory().getItemInMainHand().getType();
			if (isBerserker) {
		        if (wearingBerserker(player)) {
		    		if (mainHand.equals(Material.ROTTEN_FLESH)) {
		    			berserkerAction += 1;
		       		 
		       		 	if (berserkerAction > 2) {
		       		 		berserkerAction = 1;
		              	}
		       		 
		       			if (berserkerAction == 1) {
		             		//Stunning Shout
		             		player.sendMessage(ChatColor.GREEN + "[1 / 2]" + ChatColor.GOLD + " You ready your " + ChatColor.BLUE + "Stunning Shout");
		             		player.playSound(player.getLocation(), Sound.ENTITY_COW_MILK, 2F, 1F);
		       			} else if (berserkerAction == 2) {
		       				//Berserker Rage
		       				player.sendMessage(ChatColor.GREEN + "[2 / 2]" + ChatColor.GOLD + " You ready your " + ChatColor.BLUE + "Berserker Rage");
		             		player.playSound(player.getLocation(), Sound.ENTITY_COW_MILK, 2F, 1F);
		       			}
		    		}
		        }	
    		}
        } else if ((event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) && event.getHand() == EquipmentSlot.HAND) {
        	Player player = event.getPlayer();
			boolean isBerserker = (plugin.getConfig().getString("Users." + player.getUniqueId() + ".Class").contains("Berserker"));
			Material mainHand = player.getInventory().getItemInMainHand().getType();
			
			if (isBerserker) {
		        if (wearingBerserker(player)) {
		    		if (mainHand.equals(Material.ROTTEN_FLESH)) {
		    			if (berserkerAction == 1) {
		             		//Spell Stunning Shout
		    				String ShoutUUID = player.getUniqueId()+"Shout";
		             		if (!plugin.cooldowns.containsKey(ShoutUUID)) {
		             			for(Entity getEntity : player.getNearbyEntities(12, 12, 12)){
			        			    //Example
			        			    if(getEntity.getType() == EntityType.PLAYER){
			        			        Player getPlayer = (Player) getEntity;
		        			        	plugin.masterCD = 30;
						        		plugin.cooldowns.put(ShoutUUID, plugin.masterCD);
		    			        		
						        		Player damagedEntity = (Player) getEntity;
						        		
						        		if (isSameParty(player, damagedEntity)) {
						        			
						        		} else {
						        			player.sendMessage(ChatColor.GOLD + "You casted shout on " + ChatColor.GOLD + getPlayer.getName());
			        			        	((LivingEntity) getEntity).addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 40, 100));
						        		}
						        		
		        			        	
			        			    }
			        			}
		             		} else {
		             			player.sendMessage(ChatColor.BLUE + "[Stunning Shout]" + ChatColor.RED + " on cooldown, " + ChatColor.YELLOW + plugin.cooldowns.get(ShoutUUID) + " seconds" + ChatColor.RED + " left.");	
		             		} 
		             	} else if (berserkerAction == 2) {
		             		//Spell Holy Shield
		             		String RageUUID = player.getUniqueId()+"Rage";
		             		if (!plugin.cooldowns.containsKey(RageUUID)) {
		             			plugin.masterCD = 25;
				        		plugin.cooldowns.put(RageUUID, plugin.masterCD);
				        		
				        		player.sendMessage(ChatColor.BLUE + "You have used " + ChatColor.RED + ChatColor.BOLD + "BERSERKERS RAGE");
				        		player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 60, 2));
				        		player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 80, 3));
				        		
		             		} else {
		             			player.sendMessage(ChatColor.BLUE + "[Berserker Rage]" + ChatColor.RED + " on cooldown, " + ChatColor.YELLOW + plugin.cooldowns.get(RageUUID) + " seconds" + ChatColor.RED + " left.");	
		             		}
		             		
		             	}
		    		}
		        }
    		}
        }  	   	
	}
	
	
}
