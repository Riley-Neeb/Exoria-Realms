package classes;


//CURRENTLY EXCLUDED FROM BUILD PATH
//RIGHT CLICK > BUILD PATH > INCLUDE


import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.shade.me.lucko.helper.utils.Players;

import main.Main;
import modules.CustomMobs.CustomZombie;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.server.level.WorldServer;

public class DeathKnight implements Listener {

	public Main plugin = Main.getPlugin(Main.class);
	
	private int action = 1;
	private String Ability;
	private int linkRange = 20;
	private int drainRange = 50;
	ArrayList<String> ownedMobs = new ArrayList<String>();
	ArrayList<String> soulLinks = new ArrayList<String>();
	
	public boolean hasASword(Player player) {
		Material mainHand = player.getInventory().getItemInMainHand().getType();
		 if (mainHand.equals((Material.WOODEN_SWORD))) {
			 return true;
		 }
		 
		 return false;
	}
	
	public void damagePlayer(Player p, double damage) {
		  double points = p.getAttribute(Attribute.GENERIC_ARMOR).getValue();
		  double toughness = p.getAttribute(Attribute.GENERIC_ARMOR_TOUGHNESS).getValue();
		  PotionEffect effect = p.getPotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
		  int resistance = effect == null ? 0 : effect.getAmplifier();
		  int epf = getEPF(p.getInventory());

		  p.damage(calculateDamageApplied(damage, points, toughness, resistance, epf));
	}

	public double calculateDamageApplied(double damage, double points, double toughness, int resistance, int epf) {
		  double withArmorAndToughness = damage * (1 - Math.min(20, Math.max(points / 5, points - damage / (2 + toughness / 4))) / 25);
		  double withResistance = withArmorAndToughness * (1 - (resistance * 0.2));
		  double withEnchants = withResistance * (1 - (Math.min(20.0, epf) / 25));
		  return withEnchants;
	}

	public static int getEPF(PlayerInventory inv) {
		  ItemStack helm = inv.getHelmet();
		  ItemStack chest = inv.getChestplate();
		  ItemStack legs = inv.getLeggings();
		  ItemStack boot = inv.getBoots();

		  return (helm != null ? helm.getEnchantmentLevel(Enchantment.DAMAGE_ALL) : 0) +
		     (chest != null ? chest.getEnchantmentLevel(Enchantment.DAMAGE_ALL) : 0) +
		     (legs != null ? legs.getEnchantmentLevel(Enchantment.DAMAGE_ALL) : 0) +
		     (boot != null ? boot.getEnchantmentLevel(Enchantment.DAMAGE_ALL) : 0);
	}
		
	public boolean wearingDK(Player player) {
		boolean isClass = (plugin.getConfig().getString("Users." + player.getUniqueId() + ".Class").contains("DeathKnight"));
		
		if (isClass) {
			TextComponent failMessage = new TextComponent("Your not wearing the right armor!");
			failMessage.setColor(ChatColor.DARK_RED);
			failMessage.setBold(true);
			 
			ItemStack Helm = player.getInventory().getHelmet();
			ItemStack Chest = player.getInventory().getChestplate();
			ItemStack Legs = player.getInventory().getLeggings();
			ItemStack Boots = player.getInventory().getBoots();

			if (Helm != null && Chest != null && Legs != null && Boots != null) {
				if (Helm.getType().equals(Material.NETHERITE_HELMET)) {
					if (Chest.getType().equals(Material.NETHERITE_CHESTPLATE)) {
						if (Legs.getType().equals(Material.NETHERITE_LEGGINGS)) {
							if (Boots.getType().equals(Material.NETHERITE_BOOTS)) {
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
		
	public boolean isSameFaction(Player player, Player player2) {
        FPlayer fPlayer1 = FPlayers.getInstance().getByPlayer(player);
        FPlayer fPlayer2 = FPlayers.getInstance().getByPlayer(player2);
        
        Faction faction1 = fPlayer1.getFaction();
        Faction faction2 = fPlayer2.getFaction();
        
        String factionName1 = faction1.getTag();
        String factionName2 = faction2.getTag();
        
        if (factionName1 != null && factionName2 != null) {
            if (factionName1.equals(factionName2)) {
                return true;
            } else {
                return false;
            }
        }
        
        return false;
    }
	
	@EventHandler
	private void onEntityHit(EntityTargetEvent event) {
		Player targetedPlayer = (Player) event.getTarget();
		
		 for (Player players : Bukkit.getServer().getOnlinePlayers()) {
			 for (int i = 0; i < ownedMobs.size() ;i++){
				 String ownerName = players.getName();
				 
				 if (ownedMobs.get(i).contains(ownerName)) {
					 if (isSameFaction(players, targetedPlayer) || isSameParty(players, targetedPlayer)) {
						event.setCancelled(true);
					 } else {
	            		
					 }    
				 }
			 }
			 
		 }
	}
	
	
	@EventHandler
	private void onPlayerQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		
		for (int i = 0; i < soulLinks.size() ;i++){
			
			if (soulLinks.get(i).contains(player.getName())) {
				soulLinks.remove(i);
			}
		}	 
	}
	
	@EventHandler
	private void onEntityHit(EntityDamageByEntityEvent event) {
		Player damager = (Player) event.getDamager();
		Player damagedEntity = (Player) event.getEntity();
		
        boolean isDeathKnight = (plugin.getConfig().getString("Users." + damager.getUniqueId() + ".Class").contains("DeathKnight"));
        boolean damagedDeathKnight = (plugin.getConfig().getString("Users." + damagedEntity.getUniqueId() + ".Class").contains("DeathKnight"));
		
        if (damagedDeathKnight) {
        	int configPower = (int) plugin.getConfig().get("Users." + damagedEntity.getUniqueId() + ".SoulPower");
			
			if (configPower > 0) {
				plugin.getConfig().set("Users." + damagedEntity.getUniqueId() + ".SoulPower", configPower-2);
			} else if (configPower < 0) {
				plugin.getConfig().set("Users." + damagedEntity.getUniqueId() + ".SoulPower", 0);
			}
        }
        
		if (isDeathKnight) {
			if (isSameFaction(damager, damagedEntity) || isSameParty(damager, damagedEntity)) {

			} else {
				int configPower = (int) plugin.getConfig().get("Users." + damager.getUniqueId() + ".SoulPower");
				plugin.getConfig().set("Users." + damager.getUniqueId() + ".SoulPower", configPower+2);

	        	if (wearingDK(damager)) { 
        			if (Ability == "Soul Swipe") {
        				String HeadbuttUUID = event.getDamager().getUniqueId()+"Soul Swipe";
        				
        				if (!plugin.cooldowns.containsKey(HeadbuttUUID)) {
    			        	plugin.masterCD = 8;
    		        		plugin.cooldowns.put(HeadbuttUUID, plugin.masterCD);
    		        		
    		        		plugin.getConfig().set("Users." + damager.getUniqueId() + ".SoulPower", configPower+8);
    		        		damager.sendMessage(ChatColor.GREEN + "Gained " + ChatColor.GOLD + "10" + ChatColor.GREEN + " Soul Power!");
    		        		damagedEntity.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 160, 0));
        				}
        			}
	        	}
			}     
		}
		
		for (int i = 0; i < soulLinks.size() ;i++){
			int division = soulLinks.size();
			
			 for (Player players : Bukkit.getServer().getOnlinePlayers()) {
				 if (soulLinks.get(i).contains(damagedEntity.getName())) {
					 damagePlayer(players, event.getDamage()/division);
				 }
			 }
			 
			 /*
			  if (soulLinks.get(i).contains(damagedEntity.getName())) {
				 Player linkedPlayer = damagedEntity;
				 event.setCancelled(true);
				 linkedPlayer.damage(event.getDamage()/division);
				 
				 for (Player players : Bukkit.getServer().getOnlinePlayers()) {
					 String linkerName = players.getUniqueId().toString();
					 Player linker = Bukkit.getPlayer(players.getUniqueId());
					 
					 if (soulLinks.get(i).contains(linkerName)) {
						 linker.sendMessage(ChatColor.GREEN+"Shared damage with linked player!");
						 damagePlayer(linker, event.getDamage()/2);
					 }
				 }
			 }
			 */ 
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
			boolean isDeathKnight = (plugin.getConfig().getString("Users." + player.getUniqueId() + ".Class").contains("DeathKnight"));
			
			if (isDeathKnight) {
		        if (wearingDK(player)) {   
		        	if (mainHand.equals(Material.WOODEN_SWORD)) {
		        		if (player.isSneaking()) {
		        			int configPower = (int) plugin.getConfig().get("Users." + player.getUniqueId() + ".SoulPower");
		        			player.sendMessage(ChatColor.GREEN+"You have "+ChatColor.GOLD+configPower+" Soul Power");
		        		} else {
		        			Ability = "Soul Swipe";
			        		player.sendMessage(ChatColor.BLUE + "You ready your " + ChatColor.GOLD + "Soul Swipe");
		        		}
		        	} else if (mainHand.equals(Material.QUARTZ)) {
		        		action += 1;
		       		 
		       		 	if (action > 4) {
		       		 		action = 1;
		              	}
		       		 
		       			if (action == 1) {
		             		//Hellspawn
		             		player.sendMessage(ChatColor.GREEN + "[1 / 4]" + ChatColor.GOLD + " You ready your " + ChatColor.BLUE + "Hellspawn");
		             		player.playSound(player.getLocation(), Sound.BLOCK_SOUL_SAND_STEP, 2F, 1F);
		             		
		             	} else if (action == 2) {
		             		//Soul Link
		             		player.sendMessage(ChatColor.GREEN + "[2 / 4]" + ChatColor.GOLD + " You ready your " + ChatColor.BLUE + "Soul Link");
		             		player.playSound(player.getLocation(), Sound.BLOCK_SOUL_SAND_STEP, 2F, 1F);
		             		
		            	} else if (action == 3) {
		             		//Soul Link
		             		player.sendMessage(ChatColor.GREEN + "[3 / 4]" + ChatColor.GOLD + " You ready your " + ChatColor.BLUE + "Soul Drain");
		             		player.playSound(player.getLocation(), Sound.BLOCK_SOUL_SAND_STEP, 2F, 1F);
		             		
		             	} else if (action == 4) {
		             		//Bankai
		             		player.playSound(player.getLocation(), Sound.BLOCK_SOUL_SAND_STEP, 2F, 1F);
		             		player.sendMessage(ChatColor.GREEN + "[4 / 4]" + ChatColor.GOLD + " You ready your " + ChatColor.BLUE + "Bankai");
		             	}
		        	}
	    		}
			}
        } else if ((event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) && event.getHand() == EquipmentSlot.HAND) {
        	Player player = event.getPlayer();
    		Material mainHand = player.getInventory().getItemInMainHand().getType();
			boolean isDeathKnight = (plugin.getConfig().getString("Users." + player.getUniqueId() + ".Class").contains("DeathKnight"));
			
			if (isDeathKnight) {
		        if (wearingDK(player)) {   
		        	if (mainHand.equals(Material.QUARTZ)) {
		        		if (action == 1) {
		        			//Hellspawn
		        			int configPower = (int) plugin.getConfig().get("Users." + player.getUniqueId() + ".SoulPower");
		        			if (configPower >= 100) {
		        				plugin.getConfig().set("Users." + player.getUniqueId() + ".SoulPower", configPower-100);
			        			WorldServer world = ((CraftWorld) player.getWorld()).getHandle();
			        			 
		        		         ItemStack head = new ItemStack(Material.LEATHER_HELMET);
		        		         ItemStack chest = new ItemStack(Material.LEATHER_CHESTPLATE);
		        		         ItemStack legs = new ItemStack(Material.LEATHER_LEGGINGS);
		        		         ItemStack boots = new ItemStack(Material.LEATHER_BOOTS);
		        		         ItemStack weapon = new ItemStack(Material.DIAMOND_SWORD);
	
			        			 for (int i = 0; i < 2 ;i++){
			        				 CustomZombie spawnedMob = new CustomZombie(player.getLocation(), world);
			        				 
			        				 LivingEntity mob = (LivingEntity) spawnedMob.getBukkitEntity();
				                     EntityEquipment equip = mob.getEquipment();
				                     equip.setHelmet(head);
				                     equip.setChestplate(chest);
				                     equip.setLeggings(legs);
				                     equip.setBoots(boots);
				                     equip.setItemInMainHand(weapon);
				                    
				                     AttributeInstance maxHealth = mob.getAttribute(Attribute.GENERIC_MAX_HEALTH);
				                     maxHealth.setBaseValue(20F);
				                     
				                     AttributeInstance moveSpeed = mob.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED);
				                     moveSpeed.setBaseValue(0.2F);
				                     
				                     mob.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 3));
				                     mob.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, Integer.MAX_VALUE, 1));
				                     mob.addPotionEffect(new PotionEffect(PotionEffectType.WATER_BREATHING, Integer.MAX_VALUE, 1));
				                     world.addEntity(spawnedMob);
	
				                     Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
				                         @Override
				                         public void run() {
							            	 
				                        	 for(Entity entity : player.getNearbyEntities(25, 25, 25)) {
									            	Player found = (Player) entity;
								            		
									            	if (isSameFaction(player, found) || isSameParty(player, found)) {
									            		
									            	} else {
									            		if (found != null) {
									            			 double distance = player.getLocation().distance(mob.getLocation());
												            	
								                        	 boolean nearbyEnemies = false;
											            	 if (distance > 25) {
											            		 if (!nearbyEnemies) {
											            			 spawnedMob.getNavigation().a(player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ(), 2F); 
											            		 } else {
											            			 spawnedMob.getNavigation().a(found.getLocation().getX(), found.getLocation().getY(), found.getLocation().getZ(), 2F);  
											            		 }
											            		
											            	 }
									            		}
									            	}           
										        }
				                        	 
				                        	
				                         }
				                     }, 0, 5);
				                     
				                     Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable(){
					             			public void run(){
					             				spawnedMob.killEntity();
					             				player.sendMessage(ChatColor.GRAY+"Hellspawn has expired!");
					             			}
					             		}, 20*120);	
				                     ownedMobs.add(player.getName() + " " + spawnedMob.getUniqueID().toString());
			        			 }
		        			} else {
		        				player.sendMessage(ChatColor.RED+"Not enough soul power, need "+ChatColor.GOLD+(100-configPower)+ChatColor.RED+" more!");
		        			}
		        			
		        		} else if (action == 2) {
		        			//Soul Link
		        			int foundPlayers = 0;
		        			int configPower = (int) plugin.getConfig().get("Users." + player.getUniqueId() + ".SoulPower");
		        			
		        			if (configPower >= 25) {
		        				plugin.getConfig().set("Users." + player.getUniqueId() + ".SoulPower", configPower-25);
		        				for(Entity entity : player.getNearbyEntities(linkRange, linkRange, linkRange)) {
						            if(entity instanceof Player) {
						            	Player found = (Player) entity;
						            	if (isSameFaction(player, found) || isSameParty(player, found)) {
						            		if (found.getHealth() > 10) {
						            			foundPlayers+=1;
						            		}
						            	}        
						            }
						        }
			        			
			        			for(Entity entity : player.getNearbyEntities(linkRange, linkRange, linkRange)) {
						            if(entity instanceof Player) {
						            	Player found = (Player) entity;
						            	
						            	if (isSameFaction(player, found) || isSameParty(player, found)) {
						            		if (found.getHealth() > 10) {
							            		found.setHealth(found.getHealth()-10);
					            				found.sendMessage(ChatColor.GREEN + "You've been soul linked with " + player.getName());
					            				player.sendMessage(ChatColor.GREEN + "You soul linked with " + found.getName());
					            				
					            				configPower+=foundPlayers*6;
					            				soulLinks.add(player.getUniqueId().toString() + " " + found.getName());
					            				
					            				Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable(){
					            					public void run(){
					            						soulLinks.remove(found.getName());
					            					}
					            				}, 20*30);	
						            		}
						            	}        
						            } else {
						            	for (int i = 0; i < ownedMobs.size() ;i++){
						            		String ownerName = player.getName();
						   				 
						            		if (ownedMobs.get(i).contains(ownerName)) {
						            			if (((Damageable) entity).getHealth() > 10) {
							            			foundPlayers+=1;
							            			soulLinks.add(player.getUniqueId().toString() + " " + entity.getUniqueId().toString());

							            			Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable(){
						            					public void run(){
						            						for (int i = 0; i < soulLinks.size() ;i++){
						            							 if (soulLinks.get(i).equals(player.getUniqueId().toString() + " " + entity.getUniqueId().toString())) {
						            								soulLinks.remove(i);
						            							 }
						            						}
						            					}
						            				}, 20*30);	
							            		}
						   					}
						   			 	}
						            }
						        }
		        			} else {
		        				player.sendMessage(ChatColor.RED+"Not enough soul power, need "+ChatColor.GOLD+(25-configPower)+ChatColor.RED+" more!");
		        			}
		        			
		        		} else if (action == 3) {
		        			//Soul Drain
		        			for(Entity entity : player.getNearbyEntities(drainRange, drainRange, drainRange)) {
		        				
		        				if (entity != player) {
		        					for (int i = 0; i < soulLinks.size() ;i++){
				        				 if (soulLinks.get(i).contains(entity.getUniqueId().toString())) {
				        					 LivingEntity Ent = (LivingEntity) entity;
				        					 
				        					 if (Ent.getHealth() > 1.5) {
				        						 double maxHealth = player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue();
				        						 
				        						 if (player.getHealth() < maxHealth) {
				        							 player.setHealth(player.getHealth()+1.5);
				        							 Ent.setHealth(Ent.getHealth()-1.5);
				        							 player.sendMessage("Removed soul link with " + soulLinks.get(i)+"!");
				        							 soulLinks.remove(i);
				        							 
				        						 } else if (player.getHealth() >= maxHealth) {
				        							 player.setAbsorptionAmount(player.getAbsorptionAmount()+1.5);
				        							 Ent.setHealth(Ent.getHealth()-1.5);
				        							 player.sendMessage("Removed soul link with " + soulLinks.get(i)+"!");
				        							 soulLinks.remove(i);
				        						 }
				        					 } 
				        				 }
				        			}
		        				}
		        			}
		        			
		        		} else if (action == 4) {
		        			//Bankai
	        				int configPower = (int) plugin.getConfig().get("Users." + player.getUniqueId() + ".SoulPower");
	        				
	        				if (configPower > 0) {
	        					player.sendMessage(ChatColor.GREEN+"BANKAI!");
	        					
	        					int time = (int) Math.floor(configPower/15);
		        				int strengthAffect = (int) Math.floor(configPower/30);
		        				
		        				player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, time*20, strengthAffect));
		        				player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, time*20, strengthAffect));
		        				player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, time*20, strengthAffect));
		        				player.sendMessage(ChatColor.YELLOW+"Consumed "+ChatColor.GOLD+configPower+ " soul power!");
		        				plugin.getConfig().set("Users." + player.getUniqueId() + ".SoulPower", 0);
	        				} else {
	        					player.sendMessage(ChatColor.RED+"You have no spirit power!");
	        				}
	        			
		        		}
		        	}
		        }
			}
        }
	}
			
	 @EventHandler
	 public void onKill(PlayerDeathEvent event) {
		 Player killed = event.getEntity();
		 Player killer = event.getEntity().getKiller();
		 
		 boolean isDeathKnight = (plugin.getConfig().getString("Users." + killer.getUniqueId() + ".Class").contains("DeathKnight"));
		 boolean isKilledDeathKnight = (plugin.getConfig().getString("Users." + killed.getUniqueId() + ".Class").contains("DeathKnight"));
		 int configPower = (int) plugin.getConfig().get("Users." + killer.getUniqueId() + ".SoulPower");
		 
		 for (int i = 0; i < soulLinks.size() ;i++){
			
			 if (soulLinks.get(i).contains(killed.getName())) {
				 soulLinks.remove(i);
			 }
			 
		 }	 
		
		 if (isDeathKnight) {
			 if (isKilledDeathKnight) {
				 configPower+=100;
			 } else {
				 configPower+=25;
			 }
			
			 
			 World world = killer.getWorld();
			 
			 ItemStack head = new ItemStack(Material.LEATHER_HELMET);
	         ItemStack chest = new ItemStack(Material.LEATHER_CHESTPLATE);
	         ItemStack legs = new ItemStack(Material.LEATHER_LEGGINGS);
	         ItemStack boots = new ItemStack(Material.LEATHER_BOOTS);
	         ItemStack weapon = new ItemStack(Material.DIAMOND_SWORD);
	         
			 CustomZombie spawnedMob = new CustomZombie(null, killer.getLocation(), world);
			 LivingEntity mob = (LivingEntity) spawnedMob.getBukkitEntity();
             EntityEquipment equip = mob.getEquipment();
             equip.setHelmet(head);
             equip.setChestplate(chest);
             equip.setLeggings(legs);
             equip.setBoots(boots);
             equip.setItemInMainHand(weapon);
            
             AttributeInstance moveSpeed = mob.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED);
             moveSpeed.setBaseValue(0.3F);
             
             mob.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 3));
             mob.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, Integer.MAX_VALUE, 1));
             mob.addPotionEffect(new PotionEffect(PotionEffectType.WATER_BREATHING, Integer.MAX_VALUE, 1));
             world.addEntity(spawnedMob);

             Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
                 @Override
                 public void run() {
                	 spawnedMob.getNavigation().a(killer.getLocation().getX(), killer.getLocation().getY(), killer.getLocation().getZ(), 1.25F);
                 }
             }, 0, 2 * 20);
             
             Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable(){
         			public void run(){
         				ownedMobs.remove(spawnedMob);
         				
         				if (spawnedMob.isAlive()) {
         					spawnedMob.killEntity();
             				killer.sendMessage(ChatColor.GRAY+"Hellspawn has expired!");
         				}
         				
         			}
         		}, 20*120);	
             ownedMobs.add(killer.getName() + " " + spawnedMob.getUniqueID().toString());
		 }

	 }
}