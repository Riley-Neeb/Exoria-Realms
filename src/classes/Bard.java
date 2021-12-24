package classes;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import main.Main;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;

public class Bard implements Listener {
	
	public Main plugin = Main.getPlugin(Main.class);
	
	private int bardAction = 1;
	private int bardInspire = 1;
	
	private int actionRange = 8;
	private int inspireRange = 50;
	
	public enum NegativeEffects{
        CONFUSION, HARM, HUNGER, POISON, SLOW_DIGGING, SLOW, WEAKNESS, WITHER;
    }

	public boolean wearingIron(Player player) {
		boolean isClass = (plugin.getConfig().getString("Users." + player.getUniqueId() + ".Class").contains("Bard"));
		
		if (isClass) {
			TextComponent failMessage = new TextComponent("Your not wearing the right armor!");
			failMessage.setColor(ChatColor.DARK_RED);
			failMessage.setBold(true);
			 
			ItemStack Helm = player.getInventory().getHelmet();
			ItemStack Chest = player.getInventory().getChestplate();
			ItemStack Legs = player.getInventory().getLeggings();
			ItemStack Boots = player.getInventory().getBoots();
			
			if (Helm != null && Chest != null && Legs != null && Boots != null) {
				if (Helm.getType().equals(Material.IRON_HELMET)) {
					if (Chest.getType().equals(Material.IRON_CHESTPLATE)) {
						if (Legs.getType().equals(Material.IRON_LEGGINGS)) {
							if (Boots.getType().equals(Material.IRON_BOOTS)) {
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
		}
		
		return false;
	}
	
	public ArrayList<String> getNearbyParty(Player player, int Distance) {
		ArrayList<String> nearbyPlayers = new ArrayList<String>();
		 
		for(Entity entity : player.getNearbyEntities(Distance, Distance, Distance)) {
			
			for (String[] PartiesByLeader: plugin.parties.keySet()) {
    			for(int i = 0; i < PartiesByLeader.length; i++){
    				if (PartiesByLeader[i] != null && PartiesByLeader[i].equals(entity.getName())) {
    					nearbyPlayers.add(entity.getName());
    				}
    			}
    		}
			
		}
			
		return nearbyPlayers;
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
	public void onBlockPlace(BlockPlaceEvent event){  
		Player player = event.getPlayer();
		boolean isBard = (plugin.getConfig().getString("Users." + player.getUniqueId() + ".Class").contains("Bard"));
		
		if (isBard) {
			if (player.getInventory().getItemInMainHand().getType().equals(Material.NOTE_BLOCK)) {
				event.setCancelled(true);
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
    		Material mainHand = player.getInventory().getItemInMainHand().getType();
			boolean isBard = (plugin.getConfig().getString("Users." + player.getUniqueId() + ".Class").contains("Bard"));
			
			if (isBard) {
		        if (wearingIron(player)) {   
		        	if (mainHand.equals(Material.NOTE_BLOCK)) {
		       			bardAction += 1;
		       		 
		       		 	if (bardAction > 5) {
		       		 		bardAction = 1;
		              	}
		       		 
		       			if (bardAction == 1) {
		             		//Hymn of strength.
		             		player.sendMessage(ChatColor.GREEN + "[1 / 5]" + ChatColor.GOLD + " You ready your " + ChatColor.BLUE + "Hymn of Strength");
		             		player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASEDRUM, 2F, 1F);
		             		
		             	} else if (bardAction == 2) {
		             		//Hymn of Speed.
		             		player.sendMessage(ChatColor.GREEN + "[2 / 5]" + ChatColor.GOLD + " You ready your " + ChatColor.BLUE + "Hymn of Speed");
		             		player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BANJO, 2F, 1F);
		             		
		             	} else if (bardAction == 3) {
		             		//Hymn of Regeneration.
		             		player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 2F, 1F);
		             		player.sendMessage(ChatColor.GREEN + "[3 / 5]" + ChatColor.GOLD + " You ready your " + ChatColor.BLUE + "Hymn of Regeneration");
		             		
		             	} else if (bardAction == 4) {
		             		//Hymn of Protection.
		             		player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 2F, 1F);
		             		player.sendMessage(ChatColor.GREEN + "[4 / 5]" + ChatColor.GOLD + " You ready your " + ChatColor.BLUE + "Hymn of Protection");
		             		
		             	} else if (bardAction == 5) {
		             		//Hymn of Resistances.
		             		  player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BIT, 2F, 1F);
		             		player.sendMessage(ChatColor.GREEN + "[5 / 5]" + ChatColor.GOLD + " You ready your " + ChatColor.BLUE + "Hymn of Resistance");
		             		
		             	}
		        	}
	    		}
			}
    	} else if ((event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) && event.getHand() == EquipmentSlot.HAND) {
    		Player player = event.getPlayer();
    		Material mainHand = player.getInventory().getItemInMainHand().getType();
			boolean isBard = (plugin.getConfig().getString("Users." + player.getUniqueId() + ".Class").contains("Bard"));
			
			if (isBard) {
		        if (wearingIron(player)) {   
		        	if (mainHand.equals(Material.NOTE_BLOCK)) {
        		
		        		String hymnUUID = player.getUniqueId()+" Hymn";
		        		if (!plugin.cooldowns.containsKey(hymnUUID)) {
		        			plugin.masterCD = 20;
			        		plugin.cooldowns.put(hymnUUID, plugin.masterCD);
			        		
			    			if (bardAction == 1) {
						  		player.sendMessage(ChatColor.YELLOW + "Hymn of Strength.");
						  		player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 820, 0));
						  		player.playSound(player.getLocation(), Sound.BLOCK_BAMBOO_BREAK, 2F, 1F);

						  		for(Entity entity : player.getNearbyEntities(actionRange, actionRange, actionRange)) {
						            if(entity instanceof Player) {
						            	Player found = (Player) entity;
					            		
						            	if (isSameParty(player, found)) {
						            		found.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 820, 1));
				            				found.sendMessage(ChatColor.GREEN + player.getName() + ChatColor.GOLD + " blessed you with damage!");
				            				player.sendMessage(ChatColor.GREEN+ "You've blessed " + found.getName() + " with damage!");
						            	}        
						            }
						        }

							} else if (bardAction == 2) {
						  		player.sendMessage(ChatColor.YELLOW + "Hymn of Speed.");
						  		player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 820, 1));
						  		player.playSound(player.getLocation(), Sound.BLOCK_BAMBOO_BREAK, 2F, 1F);

						  		for(Entity entity : player.getNearbyEntities(actionRange, actionRange, actionRange)) {
						            if(entity instanceof Player) {
						            	Player found = (Player) entity;
					            		
						            	if (isSameParty(player, found)) {
						            		found.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 820, 3));
				            				found.sendMessage(ChatColor.GREEN + player.getName() + ChatColor.GOLD + " blessed you with speed!");
				            				player.sendMessage(ChatColor.GREEN+ "You've blessed " + found.getName() + " with speed!");
						            	} else {
						            		
						            	}           
						            }
						        }
						  		
						  	} else if (bardAction == 3) {
						  		player.sendMessage(ChatColor.YELLOW + "Hymn of Regeneration.");
						  		player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 820, 1));
						  		player.playSound(player.getLocation(), Sound.BLOCK_BAMBOO_BREAK, 2F, 1F);
						  		
						  		for(Entity entity : player.getNearbyEntities(actionRange, actionRange, actionRange)) {
						            if(entity instanceof Player) {
						            	Player found = (Player) entity;
					            		
						            	if (isSameParty(player, found)) {
						            		found.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 820, 2));
				            				found.sendMessage(ChatColor.GREEN + player.getName() + ChatColor.GOLD + " blessed you with regeneration!");
				            				player.sendMessage(ChatColor.GREEN+ "You've blessed " + found.getName() + " with regeneration!");
						            	} else {
						            		
						            	}           
						            }
						        }

						  	}  else if (bardAction == 4) {
						  		player.sendMessage(ChatColor.YELLOW + "Hymn of Protection.");
						  		player.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 820, 1));
						  		player.playSound(player.getLocation(), Sound.BLOCK_BAMBOO_STEP, 2F, 1F);

						  		for(Entity entity : player.getNearbyEntities(actionRange, actionRange, actionRange)) {
						            if(entity instanceof Player) {
						            	Player found = (Player) entity;
					            		
						            	if (isSameParty(player, found)) {
						            		found.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 820, 3));
				            				found.sendMessage(ChatColor.GREEN + player.getName() + ChatColor.GOLD + " blessed you with absorption!");
				            				player.sendMessage(ChatColor.GREEN+ "You've blessed " + found.getName() + " with absorption!");
						            	} else {
						            		
						            	}           
						            }
						        }
						  		
						  	} else if (bardAction == 5) {
						  		player.sendMessage(ChatColor.YELLOW + "Hymn of Resistance.");
						  		player.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 820, 1));
						        player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 820, 1));
						        player.playSound(player.getLocation(), Sound.BLOCK_BAMBOO_BREAK, 2F, 1F);
						  		
						  		for(Entity entity : player.getNearbyEntities(actionRange, actionRange, actionRange)) {
						            if(entity instanceof Player) {
						            	Player found = (Player) entity;
					            		
						            	if (isSameParty(player, found)) {
						            		found.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 820, 3));
					            			found.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 820, 3));
				            				found.sendMessage(ChatColor.GREEN + player.getName() + ChatColor.GOLD + " blessed you with resistances!");
				            				player.sendMessage(ChatColor.GREEN+ "You've blessed " + found.getName() + " with resistances!");
						            	} else {
						            		
						            	}           
						            }
						        }
						  	}
			    			
		        		} else {
		        			player.sendMessage(ChatColor.RED + "[Hymn] On cooldown, " + ChatColor.YELLOW + plugin.cooldowns.get(hymnUUID) + " seconds" + ChatColor.RED + " left.");
		        		}
    		        }
    			}
    		}
        }
   
		//CLOCK SPELLS BARD
        if ((event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) && event.getHand() == EquipmentSlot.HAND) {
        	Player player = event.getPlayer();
    		Material mainHand = player.getInventory().getItemInMainHand().getType();
			boolean isBard = (plugin.getConfig().getString("Users." + player.getUniqueId() + ".Class").contains("Bard"));
			
			if (isBard) {
		        if (wearingIron(player)) {   
		        	if (mainHand.equals(Material.CLOCK)) {
		    			bardInspire += 1;
		       		 
		        		if (bardInspire > 3) {
		        			bardInspire = 1;
		               	}
		        		 
		        		if (bardInspire == 1) {
		               		//Spell of Inspiration.
		               		player.sendMessage(ChatColor.GREEN + "[1 / 3]" + ChatColor.BLUE + " You ready your " + ChatColor.GOLD + "Spell of Inspiration");
		               	} else if (bardInspire == 2) {
		               		//Spell of Chilling Melody.
		               		player.sendMessage(ChatColor.GREEN + "[2 / 3]" + ChatColor.BLUE + " You ready your " + ChatColor.GOLD + "Spell of Chilling Melody");
		               	} else if (bardInspire == 3) {
		               		//Spell of Cleansing.
		               		player.sendMessage(ChatColor.GREEN + "[3 / 3]" + ChatColor.BLUE + " You ready your " + ChatColor.GOLD + "Spell of Cleansing");
		               	}
		    		}
    			}
    		}
        } else if ((event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) && event.getHand() == EquipmentSlot.HAND) {
        	Player player = event.getPlayer();
    		Material mainHand = player.getInventory().getItemInMainHand().getType();
			boolean isBard = (plugin.getConfig().getString("Users." + player.getUniqueId() + ".Class").contains("Bard"));
			
			if (isBard) {
		        if (wearingIron(player)) {   
		        	if (mainHand.equals(Material.CLOCK)) {
    		        	String spellUUID = player.getUniqueId()+" Spell";
    		        	
		        		if (!plugin.cooldowns.containsKey(spellUUID)) {
		        			plugin.masterCD = 30;
			        		plugin.cooldowns.put(spellUUID, plugin.masterCD);
			        		
			        		if (bardInspire == 1) {
			        			//Spell of Inspiration. 30 second CD
			        			player.sendMessage(ChatColor.DARK_PURPLE + "Spell of Inspiration.");
			        			
						  		for(Entity entity : player.getNearbyEntities(actionRange, actionRange, actionRange)) {
						            if(entity instanceof Player) {
						            	Player found = (Player) entity;
					            		
						            	if (isSameParty(player, found)) {
						            		found.addPotionEffect(new PotionEffect(PotionEffectType.HEAL, (int) 2, 0));
						            		player.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, 300, 3));
				            				found.sendMessage(ChatColor.GREEN + player.getName() + ChatColor.GOLD + " healed you with a spell!");
				            				player.sendMessage(ChatColor.GREEN + "You've healed " + found.getName() + " with a spell!");
						            	} else {
						            		
						            	}           
						            }
						        }
			        			
				    		} else if (bardInspire == 2) {
				    			//Spell of Chilling Melody. 30 second CD
				    			player.sendMessage(ChatColor.DARK_PURPLE + "Spell of Chilling Melody.");
						  		
						  		for(Entity entity : player.getNearbyEntities(actionRange, actionRange, actionRange)) {
						            if(entity instanceof Player) {
						            	Player found = (Player) entity;
					            		
						            	if (isSameParty(player, found)) {

						            	} else {
						            		found.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 50, 100));
				            				found.sendMessage(ChatColor.RED + "You've been hit with a spell!");
				            				player.sendMessage(ChatColor.GREEN + "You've hit " + found.getName() + " with a spell!");
						            	}           
						            }
						        }
			              		
				    		} else if (bardInspire == 3) {
			              		//Spell of Cleansing. 30 second CD
				    			player.sendMessage(ChatColor.DARK_PURPLE + "Spell of Cleansing.");
				    			
				    			for(Entity entity : player.getNearbyEntities(actionRange, actionRange, actionRange)) {
						            if(entity instanceof Player) {
						            	Player found = (Player) entity;
					            		
						            	//Cleanse Player
						            	for(PotionEffect effects: player.getActivePotionEffects()){
				                            for(NegativeEffects bad: NegativeEffects.values()){
					                             if(effects.getType().getName().equalsIgnoreCase(bad.name())){
					                                 player.removePotionEffect(effects.getType());   
					                                 player.sendMessage(ChatColor.GREEN + "You've cleansed yourself!");
					                             }
				                            }           
				                        }
						            	
						            	if (isSameParty(player, found)) {
						            		//Cleanse Others
						            		for(PotionEffect effects: found.getActivePotionEffects()){
					                            for(NegativeEffects bad: NegativeEffects.values()){
						                             if(effects.getType().getName().equalsIgnoreCase(bad.name())){
						                                 found.removePotionEffect(effects.getType());   
						                                 found.sendMessage(ChatColor.GREEN + "You've been cleansed!");
						                                 player.sendMessage(ChatColor.GREEN + "You've cleansed " + found.getName() + "!");
						                             }
					                            }           
					                        }
						            	} else {

						            	}           
						            }
						        }
				    		}
		        		} else {
		        			player.sendMessage(ChatColor.RED + "On cooldown, " + ChatColor.YELLOW + plugin.cooldowns.get(spellUUID) + " seconds" + ChatColor.RED + " left.");
		        		}
		        	}
		        }
        	}
        }
    }
}
