package classes;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
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

public class Gladiator implements Listener {

	private Main plugin = Main.getPlugin(Main.class);
	private int riposteChance = 8;
	private int riposteModifier = 1; //default damage is 1/2, a modifier of 2 would be 100% damage return.
	
	private int powerAttackChance = 6;
	private int hamstringChance = 8;
	private int gladiatorAction = 0;
	
	public boolean wearingDiamond(Player player) {
		boolean isClass = (plugin.getConfig().getString("Users." + player.getUniqueId() + ".Class").contains("Gladiator"));
		
		if (isClass) {
			TextComponent failMessage = new TextComponent("Your not wearing the right armor!");
			failMessage.setColor(ChatColor.DARK_RED);
			failMessage.setBold(true);
			 
			ItemStack Helm = player.getInventory().getHelmet();
			ItemStack Chest = player.getInventory().getChestplate();
			ItemStack Legs = player.getInventory().getLeggings();
			ItemStack Boots = player.getInventory().getBoots();
			
			if (Helm != null && Chest != null && Legs != null && Boots != null) {
				if (Helm.getType().equals(Material.DIAMOND_HELMET)) {
					if (Chest.getType().equals(Material.DIAMOND_CHESTPLATE)) {
						if (Legs.getType().equals(Material.DIAMOND_LEGGINGS)) {
							if (Boots.getType().equals(Material.DIAMOND_BOOTS)) {
								return true;
							} else {
								player.spigot().sendMessage(failMessage);
							}
						} else {
							player.spigot().sendMessage(failMessage);
						}
					} else {
						player.spigot().sendMessage(failMessage);
					}
				} else {
					player.spigot().sendMessage(failMessage);
				}	
			} else {
				player.spigot().sendMessage(failMessage);
				return false;
			}
			
			return false;	
		}
		
		return false;	
	}
	
	public boolean hasAsword(Player player) {
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
	
	@EventHandler(priority=EventPriority.HIGH)
	private void onPlayerUse(PlayerInteractEvent event){
		
		
		if(event.getItem() == null) {
			return;
	    }
	
    	
        if ((event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) && event.getHand() == EquipmentSlot.HAND) {
        	Player player = event.getPlayer();
    		Material mainHand = player.getInventory().getItemInMainHand().getType();
			boolean isGladiator = (plugin.getConfig().getString("Users." + player.getUniqueId() + ".Class").contains("Gladiator"));
			if (isGladiator) {
		        if (wearingDiamond(player)) {   
		        	if (mainHand.equals(Material.FEATHER)) {
		        		gladiatorAction += 1;
	       		 
		       		 	if (gladiatorAction > 2) {
		       		 		gladiatorAction = 1;
		              	}
		       		 
		       		 	if (gladiatorAction == 1) {
		       		 		player.sendMessage(ChatColor.GREEN + "[1 / 2]" + ChatColor.BLUE + " You ready your " + ChatColor.GOLD + "Haze");
		       		 	} else if (gladiatorAction == 2) {
		       		 		player.sendMessage(ChatColor.GREEN + "[2 / 2]" + ChatColor.BLUE + " You ready your " + ChatColor.GOLD + "Haste");
		       		 	}
		        	}
	        	}
        	}
        } else if ((event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) && event.getHand() == EquipmentSlot.HAND) {
        	Player player = event.getPlayer();
    		Material mainHand = player.getInventory().getItemInMainHand().getType();
			boolean isGladiator = (plugin.getConfig().getString("Users." + player.getUniqueId() + ".Class").contains("Gladiator"));
			if (isGladiator) {
		        if (wearingDiamond(player)) {   
		        	if (mainHand.equals(Material.FEATHER)) {
			        	if (gladiatorAction == 1) {
		             		//Haze
			        		String HazeUUID = player.getUniqueId()+"Haze";
			        		if (!plugin.cooldowns.containsKey(HazeUUID)) {
			        			plugin.masterCD = 15;
				        		plugin.cooldowns.put(HazeUUID, plugin.masterCD);
				        		
				        		player.sendMessage(ChatColor.AQUA + "You used " + ChatColor.GOLD + "Haze");
			       				for(Entity entity : player.getNearbyEntities(20, 20, 20)) {
						            if(entity instanceof Player) {
						                Player found = (Player) entity;
						                
						                if (isSameParty(player, found)) {
						                	
						                } else {
						                	found.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 260, 1000000));
						                	found.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 60, 1));
						                	found.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 60, 0));
						                	found.sendMessage(ChatColor.GOLD + player.getName() + ChatColor.BLUE + " hit you with confusion & slow!");
						                }     
						            }
						        }
			        		} else {
			        			player.sendMessage(ChatColor.RED + "Haze On cooldown, " + ChatColor.YELLOW + plugin.cooldowns.get(HazeUUID) + " seconds" + ChatColor.RED + " left.");
			        		}
			        	} else if (gladiatorAction == 2) {
		             		//Haste
		             		String HasteUUID = player.getUniqueId()+"Haste";
		             		if (!plugin.cooldowns.containsKey(HasteUUID)) {
			        			plugin.masterCD = 20;
				        		plugin.cooldowns.put(HasteUUID, plugin.masterCD);
				        		
				        		player.sendMessage(ChatColor.AQUA + "You used " + ChatColor.GOLD + "Haste");
			             		player.setHealth(player.getHealth()-2);
			             		player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED , 140, 3));
		             		} else {
			        			player.sendMessage(ChatColor.RED + "Haste On cooldown, " + ChatColor.YELLOW + plugin.cooldowns.get(HasteUUID) + " seconds" + ChatColor.RED + " left.");
		             		}
			        	}
		        	}
	        	}
	        }
        }
	}
		             		
	@EventHandler
	private void onEntityHit(EntityDamageByEntityEvent event) {
		Player damager = (Player) event.getDamager();
		Player damagedEntity = (Player) event.getEntity();
		boolean isGladiator = (plugin.getConfig().getString("Users." + event.getDamager().getUniqueId() + ".Class").contains("Gladiator"));
		 
		if (isGladiator) {
			if (isSameParty(damager, damagedEntity)) {

			} else {
				//If wearing iron or diamond & a warrior.
				
				if (wearingDiamond(damagedEntity)) { 
					int riposteRandom = (int)(Math.random() * 99 + 1);
					if (riposteRandom <= riposteChance) { //default 12, 80 for testing.
						event.setCancelled(true);
						double halfDamage = ((event.getDamage()/2))*riposteModifier;
						damager.damage(halfDamage);
						
						damager.sendMessage(ChatColor.BLUE + damagedEntity.getName() + ChatColor.GOLD + " has riposte'd you!");
						damagedEntity.sendMessage(ChatColor.BLUE + "You have riposte'd " + ChatColor.GOLD + damager.getName());
					}
				}
				
				
				if (wearingDiamond(damager)) { 
					int powerAttackRandom = (int)(Math.random() * 99 + 1);
					int hamstringRandom = (int)(Math.random() * 99 + 1);
					
					if (powerAttackRandom <= powerAttackChance) {
						double doubleDamage = event.getDamage()*2;
						damagedEntity.damage(doubleDamage);
						
						damager.sendMessage(ChatColor.BLUE + "You have power attacked " + ChatColor.GOLD + damagedEntity.getName());
						damagedEntity.sendMessage(ChatColor.BLUE + "You have been power attacked!");
						damager.playSound(damager.getLocation(), Sound.BLOCK_ANVIL_BREAK, 2F, 1F);
					}
					
					if (hamstringRandom <= hamstringChance) {
						damagedEntity.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 50, 100));
						damager.sendMessage(ChatColor.BLUE + "You have hamstringed " + ChatColor.GOLD + damagedEntity.getName());
						damagedEntity.sendMessage(ChatColor.BLUE + "You have been hamstringed!");
						damager.playSound(damager.getLocation(), Sound.ENTITY_FOX_BITE, 2F, 1F);
					}
				}	
			}
		}
	}




}
