package classes;

import java.util.Set;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
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
import org.bukkit.util.Vector;

import main.Main;
import modules.Vector3D;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;

public class ChaosCrusader implements Listener {

	private Main plugin = Main.getPlugin(Main.class);
	
	private int stunRange = 30;
	private int strikeRange = 50;
	private int switchRange = 50;
	
	private int chaosAction = 0;
	private String ability;
	
	public boolean wearingDiamond(Player player) {
		boolean isClass = (plugin.getConfig().getString("Users." + player.getUniqueId() + ".Class").contains("ChaosCrusader"));
		
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
		}

		return false;
	}

	private boolean hasIntersection(Vector3D p1, Vector3D p2, Vector3D min, Vector3D max) {
        final double epsilon = 0.0001f;

        Vector3D d = p2.subtract(p1).multiply(0.5);
        Vector3D e = max.subtract(min).multiply(0.5);
        Vector3D c = p1.add(d).subtract(min.add(max).multiply(0.5));
        Vector3D ad = d.abs();

        if (Math.abs(c.x) > e.x + ad.x)
            return false;
        if (Math.abs(c.y) > e.y + ad.y)
            return false;
        if (Math.abs(c.z) > e.z + ad.z)
            return false;

        if (Math.abs(d.y * c.z - d.z * c.y) > e.y * ad.z + e.z * ad.y + epsilon)
            return false;
        if (Math.abs(d.z * c.x - d.x * c.z) > e.z * ad.x + e.x * ad.z + epsilon)
            return false;
        if (Math.abs(d.x * c.y - d.y * c.x) > e.x * ad.y + e.y * ad.x + epsilon)
            return false;

        return true;
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
	private void onItemDamage(PlayerItemDamageEvent event) {
		Player player = event.getPlayer();
		boolean isChaosCrusader = (plugin.getConfig().getString("Users." + player.getUniqueId() + ".Class").contains("ChaosCrusader"));
		
		if (isChaosCrusader) {
			if (event.getItem().getType().equals(Material.GOLDEN_SWORD)) {
				event.setDamage(event.getDamage()/4);
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
        	boolean isChaosCrusader = (plugin.getConfig().getString("Users." + player.getUniqueId() + ".Class").contains("ChaosCrusader"));
        	if (isChaosCrusader) {
        		if (wearingDiamond(player)) {  
    	        	Material mainHand = player.getInventory().getItemInMainHand().getType();
		    		if (mainHand.equals(Material.GOLDEN_SWORD)) {
		    			chaosAction += 1;
		       		 
		       		 	if (chaosAction > 3) {
		       		 		chaosAction = 1;
		              	}
		       		 
		       			if (chaosAction == 1) {
		             		//Spell Hand of Light
		             		player.sendMessage(ChatColor.GREEN + "[1 / 3]" + ChatColor.BLUE + " You ready your" + ChatColor.GOLD + " Chaos Stun");
		             		player.playSound(player.getLocation(), Sound.ENTITY_COW_MILK, 2F, 1F);
		             		
		             	} else if (chaosAction == 2) {
		             		//Spell Holy Remedy
		             		player.sendMessage(ChatColor.GREEN + "[2 / 3]" + ChatColor.BLUE + " You ready your" + ChatColor.GOLD + " Chaos Strike");
		             		player.playSound(player.getLocation(), Sound.ENTITY_COW_MILK, 2F, 1F);
		             		
		             	} else if (chaosAction == 3) {
		             		//Spell Chaos Switch
		             		player.sendMessage(ChatColor.GREEN + "[3 / 3]" + ChatColor.BLUE + " You ready your" + ChatColor.GOLD + " Chaos Switch");
		             		player.playSound(player.getLocation(), Sound.ENTITY_COW_MILK, 2F, 1F);
		             	}
		    		}
    	        }
        	}
        } else if ((event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) && event.getHand() == EquipmentSlot.HAND) {
        	Player player = event.getPlayer();
        	boolean isChaosCrusader = (plugin.getConfig().getString("Users." + player.getUniqueId() + ".Class").contains("ChaosCrusader"));
        	if (isChaosCrusader) {
        		if (wearingDiamond(player)) {
    	        	Material mainHand = player.getInventory().getItemInMainHand().getType();
		    		if (mainHand.equals(Material.GOLDEN_SWORD)) {
		    			if (chaosAction == 1) {
		    				String ChaosStunUUID = player.getUniqueId()+"ChaosStun";
		    				
		    				Location position = player.getEyeLocation();
							Vector3D direction = new Vector3D(position.getDirection());
							
							Vector3D start = new Vector3D(position);
							Vector3D end =  start.add(direction.multiply(stunRange));
		    				
							Block targetBlock = player.getTargetBlock((Set<Material>) null, 6);
							
		    				if (!plugin.cooldowns.containsKey(ChaosStunUUID)) {
		    					for (Player target : player.getWorld().getPlayers()) {
		    						Vector3D targetPos = new Vector3D(target.getLocation());
									Vector3D minimum = targetPos.add(-0.5, 0, -0.5);
				                    Vector3D maximum = targetPos.add(0.5, 1.67, 0.5);
				                    
									if (isSameParty(player, target)) {
										if (hasIntersection(start, end, minimum, maximum)) {
				    						if (targetBlock.getType().equals(Material.AIR)) {
				    							if (target != player && player.hasLineOfSight(target)) {
				    								player.sendMessage(ChatColor.RED+"Can't stun a friendly player!");
				    							}
				    						}
										}
									} else {
				    					if (hasIntersection(start, end, minimum, maximum)) {
				    						if (targetBlock.getType().equals(Material.AIR)) {
				    							if (target != player && player.hasLineOfSight(target)) {
									        		//player.playSound(player.getLocation(), Sound.ENTITY_RAVAGER_DEATH, 2F, 1F);
				    								target.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 40, 100));
				    								target.setVelocity(new Vector(0, 0.025, 0));
									        		player.sendMessage(ChatColor.BLUE + "You used Chaos Stun on " + ChatColor.GOLD + target.getName());
									        		
										        	plugin.masterCD = 20;
									        		plugin.cooldowns.put(ChaosStunUUID, plugin.masterCD);
				    							}
				    						}
				    					}
				    					
									}	
		    					}
		    				} else {
				        		player.sendMessage(ChatColor.BLUE + "[Chaos Stun]" + ChatColor.RED + " on cooldown, " + ChatColor.YELLOW + plugin.cooldowns.get(ChaosStunUUID) + " seconds" + ChatColor.RED + " left.");
				        	}
		    			} else if (chaosAction == 2) {
		    				String ChaosStrikeUUID = player.getUniqueId()+"ChaosStrike";
		    				
				        	if (!plugin.cooldowns.containsKey(ChaosStrikeUUID)) {
				        		
				        		Location position = player.getEyeLocation();
								Vector3D direction = new Vector3D(position.getDirection());
								
								Vector3D start = new Vector3D(position);
								Vector3D end =  start.add(direction.multiply(strikeRange));
			    				
								Block targetBlock = player.getTargetBlock((Set<Material>) null, 6);
								
			    				if (!plugin.cooldowns.containsKey(ChaosStrikeUUID)) {
			    					for (Player target : player.getWorld().getPlayers()) {
			    						Vector3D targetPos = new Vector3D(target.getLocation());
			    						Vector3D minimum = targetPos.add(-0.5, 0, -0.5);
					                    Vector3D maximum = targetPos.add(0.5, 1.67, 0.5);
					                    
										if (isSameParty(player, target)) {
											if (hasIntersection(start, end, minimum, maximum)) {
					    						if (targetBlock.getType().equals(Material.AIR)) {
					    							if (target != player && player.hasLineOfSight(target)) {
					    								player.sendMessage(ChatColor.RED+"Can't stun a friendly player!");
					    							}
					    						}
											}
										} else {
					    					if (hasIntersection(start, end, minimum, maximum)) {
					    						if (targetBlock.getType().equals(Material.AIR)) {
					    							if (target != player && player.hasLineOfSight(target)) {
					    								
										        		player.teleport(target.getLocation());
										        		player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 2F, 1F);

										        		target.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 120, 100));
										        		target.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 120, 0));
										        		target.setHealth(target.getHealth()-4);
					
										        		player.sendMessage(ChatColor.BLUE + "You used Chaos Strike on " + ChatColor.GOLD + target.getName());
										        		
										        		plugin.masterCD = 20;
										        		plugin.cooldowns.put(ChaosStrikeUUID, plugin.masterCD);
					    							}
					    						}
					    					}
										}
										
				    					
									}
								}
							} else {
				        		player.sendMessage(ChatColor.BLUE + "[Chaos Strike]" + ChatColor.RED + " on cooldown, " + ChatColor.YELLOW + plugin.cooldowns.get(ChaosStrikeUUID) + " seconds" + ChatColor.RED + " left.");
				        	}
		    			} else if (chaosAction == 3) {
		    				String ChaosSwitchUUID = player.getUniqueId()+"ChaosSwitch";
				        	if (!plugin.cooldowns.containsKey(ChaosSwitchUUID)) {
					        	
				        		Location position = player.getEyeLocation();
								Vector3D direction = new Vector3D(position.getDirection());
								
								Vector3D start = new Vector3D(position);
								Vector3D end =  start.add(direction.multiply(switchRange));
			    				
								Block targetBlock = player.getTargetBlock((Set<Material>) null, 6);
								Location oldLoc = player.getLocation();
								
			    				if (!plugin.cooldowns.containsKey(ChaosSwitchUUID)) {
			    					for (Player target : player.getWorld().getPlayers()) {
			    						Vector3D targetPos = new Vector3D(target.getLocation());
			    						Vector3D minimum = targetPos.add(-0.5, 0, -0.5);
					                    Vector3D maximum = targetPos.add(0.5, 1.67, 0.5);
					                    
										if (isSameParty(player, target)) {
											if (hasIntersection(start, end, minimum, maximum)) {
					    						if (targetBlock.getType().equals(Material.AIR)) {
					    							if (target != player && player.hasLineOfSight(target)) {
					    								player.sendMessage(ChatColor.RED+"Can't stun a friendly player!");
					    							}
					    						}
											}
										} else {
					    					if (hasIntersection(start, end, minimum, maximum)) {
					    						if (targetBlock.getType().equals(Material.AIR)) {
					    							if (target != player && player.hasLineOfSight(target)) {
					    								
							    						plugin.masterCD = 20;
							    		        		plugin.cooldowns.put(ChaosSwitchUUID, plugin.masterCD);
							    		        		
										        		player.teleport(target.getLocation());
										        		player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 2F, 1F);
										        		
										        		target.teleport(oldLoc);
										        		target.playSound(target.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 2F, 1F);
					
										        		player.sendMessage(ChatColor.BLUE + "You used Chaos Switch on " + ChatColor.GOLD + target.getName());
					    							}
					    						}
					    					}
										}
				    					
									}		
			    			    }
				        	} else {
				        		player.sendMessage(ChatColor.BLUE + "[Chaos Switch]" + ChatColor.RED + " on cooldown, " + ChatColor.YELLOW + plugin.cooldowns.get(ChaosSwitchUUID) + " seconds" + ChatColor.RED + " left.");
				        	}
		    			}
		    		}
    	        }
    		}
        }
    }
	
}
