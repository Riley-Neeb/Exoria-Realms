package classes;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;


import classes.Bard.NegativeEffects;
import main.Main;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;

public class Shaman implements Listener {
	
	public Main plugin = Main.getPlugin(Main.class);
	
	private int maxTotems = 3;
	private int currentTotems = 0;
	private double coalRange = 12.0;
	private double warpedRange = 12.0;
	
	public boolean wearingLeather(Player player) {
		boolean isClass = (plugin.getConfig().getString("Users." + player.getUniqueId() + ".Class").contains("Shaman"));
		
		if (isClass) {
			TextComponent failMessage = new TextComponent("Your not wearing the right armor!");
			failMessage.setColor(ChatColor.DARK_RED);
			failMessage.setBold(true);
			 
			ItemStack Helm = player.getInventory().getHelmet();
			ItemStack Chest = player.getInventory().getChestplate();
			ItemStack Legs = player.getInventory().getLeggings();
			ItemStack Boots = player.getInventory().getBoots();
			
			if (Helm != null && Chest != null && Legs != null && Boots != null) {
				if (Helm.getType().equals(Material.TURTLE_HELMET)) {
					if (Chest.getType().equals(Material.LEATHER_CHESTPLATE)) {
						if (Legs.getType().equals(Material.LEATHER_LEGGINGS)) {	
							if (Boots.getType().equals(Material.LEATHER_BOOTS)) {
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
	
	public boolean checkBlock(Location location) {
		
		if (location.getBlock().getType().isSolid() == false) {
		     return true;
		}
		
		/*
		 	if (location.getBlock().getType() == Material.AIR) {
			return true;
		}
		
		if (location.getBlock().getType() == Material.SNOW) {
			return true;
		}
		
		if (location.getBlock().getType() == Material.TALL_GRASS) {
			return true;
			
		}
		 */
	
		
		
		return false;	
	}
	
	
	public boolean isSameParty(Player player, Player player2) {
		if (player.getName().equals(player2.getName())) {
			return true;
		}
		
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

	
	
	
	

	
	public static void rotateStair(Block block, BlockFace direction) {
		BlockData blockData = block.getBlockData();
		Directional blockDirectional = (Directional) blockData;
		
		blockDirectional.setFacing(direction);
		block.setBlockData(blockDirectional);
		block.getState().update(true);
	}
	
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event){  
		Player player = event.getPlayer();
		boolean isShaman = (plugin.getConfig().getString("Users." + player.getUniqueId() + ".Class").contains("Shaman"));
		
		if (isShaman && wearingLeather(player) && currentTotems < maxTotems) {
			if (player.getInventory().getItemInMainHand().getType().equals(Material.COAL_BLOCK)) {
				event.setCancelled(true);
				
				Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable(){
					 @SuppressWarnings("deprecation")
					public void run(){
							int x = event.getBlock().getX();
							int y = event.getBlock().getY();
							int z = event.getBlock().getZ();
							World world = event.getBlock().getLocation().getWorld();
							
							Location stairX1 = new Location(world,x-1,y,z);
							Location stairX2 = new Location(world,x+1,y,z);
							
							Location stairZ1 = new Location(world,x,y,z-1);
							Location stairZ2 = new Location(world,x,y,z+1);
							
							Location lowerCenter = new Location(world,x,y,z);
							Location middle = new Location(world,x,y+1,z);
							Location top = new Location(world,x,y+2,z);
							
							Random rand = new Random();
							int randomNum = rand.nextInt((2 - 1) + 1) + 1;
						
							boolean canPlace = false;
							
							if (checkBlock(stairX1) && checkBlock(stairX2)) {
								if (checkBlock(stairZ1) && checkBlock(stairZ2)) {
									if (checkBlock(lowerCenter) && checkBlock(middle)) {
										if (checkBlock(top)) {
											canPlace = true;
										}
									}
								}
							}
							
							if (randomNum == 1 && canPlace) {
								player.sendMessage(ChatColor.GREEN + "[1 / 2]" + ChatColor.GOLD + " You placed totem of " + ChatColor.BLUE + "Cleansing");
								
								stairX1.getBlock().setType(Material.POLISHED_BLACKSTONE_BRICK_STAIRS);
								rotateStair(stairX1.getBlock(), BlockFace.EAST);
								
								stairX2.getBlock().setType(Material.POLISHED_BLACKSTONE_BRICK_STAIRS);
								rotateStair(stairX2.getBlock(), BlockFace.WEST);
								
								stairZ1.getBlock().setType(Material.POLISHED_BLACKSTONE_BRICK_STAIRS);
								rotateStair(stairZ1.getBlock(), BlockFace.SOUTH);
								
								stairZ2.getBlock().setType(Material.POLISHED_BLACKSTONE_BRICK_STAIRS);
								
								lowerCenter.getBlock().setType(Material.NETHERITE_BLOCK);
								middle.getBlock().setType(Material.COAL_BLOCK);
								top.getBlock().setType(Material.EMERALD_ORE);
								
			             		player.playSound(player.getLocation(), Sound.BLOCK_ANCIENT_DEBRIS_PLACE, 2F, 1F);
			             		currentTotems+=1;

			             		new BukkitRunnable() {
			             			int count = 0;
			             			
			            			@Override
			            			public void run() {

			            				for(Entity entity : middle.getBlock().getWorld().getEntities()) {
			            					if(entity instanceof Player) {
			            						Player found = (Player) entity;
			            						double distance = entity.getLocation().distance(middle);
			            						
			            		                if(distance < coalRange) {
			            		                	if (player.equals(found)) {
				            							for(PotionEffect effects: found.getActivePotionEffects()){
				            								for(NegativeEffects bad: NegativeEffects.values()){
				            									if(effects.getType().getName().equalsIgnoreCase(bad.name())){
				            										found.removePotionEffect(effects.getType());   
				            										found.sendMessage(ChatColor.BLUE + "You've been cleansed by a totem!");
				            									 }
				            								}           
				            							}
				            						} else {
				            							if (isSameParty(player, found)) {
					            							//Cleanse Others
					            							for(PotionEffect effects: found.getActivePotionEffects()){
					            								for(NegativeEffects bad: NegativeEffects.values()){
					            									if(effects.getType().getName().equalsIgnoreCase(bad.name())){
					            										found.removePotionEffect(effects.getType());   
					            										found.sendMessage(ChatColor.BLUE + "You've been cleansed by a totem!");
					            									 }
					            								}           
					            							}
										            	}    
				            						} 
			            		                }
			            					}
			            				}
			            				
			             	        	count++;
			             	        	if (count == 50) {
			             	        		this.cancel();
			             	        	}
			            			}
			            			
			            		}.runTaskTimer(plugin, 0, 5);
			             		
			             		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable(){
			             			public void run(){
			             				currentTotems-=1;
			             				if (currentTotems < 0) {
			             					currentTotems = 0;
			             				}
			             				
			             				stairX1.getBlock().setType(Material.AIR);
			             				stairX2.getBlock().setType(Material.AIR);
			             				stairZ1.getBlock().setType(Material.AIR);
			             				stairZ2.getBlock().setType(Material.AIR);
			             				
			             				lowerCenter.getBlock().setType(Material.AIR);
			             				middle.getBlock().setType(Material.AIR);
			             				top.getBlock().setType(Material.AIR);
			             				
			             				player.sendMessage(ChatColor.GRAY+"Totem of Cleansing has expired!");
			             			}
			             		}, 20*10);	
							} else if (randomNum == 2 && canPlace) {
								player.sendMessage(ChatColor.GREEN + "[2 / 2]" + ChatColor.GOLD + " You placed totem of " + ChatColor.BLUE + "Cooldown Refresh");
								
								stairX1.getBlock().setType(Material.POLISHED_BLACKSTONE_BRICK_STAIRS);
								rotateStair(stairX1.getBlock(), BlockFace.EAST);
								
								stairX2.getBlock().setType(Material.POLISHED_BLACKSTONE_BRICK_STAIRS);
								rotateStair(stairX2.getBlock(), BlockFace.WEST);
								
								stairZ1.getBlock().setType(Material.POLISHED_BLACKSTONE_BRICK_STAIRS);
								rotateStair(stairZ1.getBlock(), BlockFace.SOUTH);
								
								stairZ2.getBlock().setType(Material.POLISHED_BLACKSTONE_BRICK_STAIRS);
								
								lowerCenter.getBlock().setType(Material.NETHERITE_BLOCK);
								middle.getBlock().setType(Material.COAL_BLOCK);
								top.getBlock().setType(Material.LAPIS_ORE);
								
			             		player.playSound(player.getLocation(), Sound.BLOCK_ANCIENT_DEBRIS_PLACE, 2F, 1F);
			             		currentTotems+=1;
			             	
			             		
			             		
			             		new BukkitRunnable() {
			             			int count = 0;
			             			
			            			@Override
			            			public void run() {

			            				for(Entity entity : middle.getBlock().getWorld().getEntities()) {
			            					if(entity instanceof Player) {
			            						Player found = (Player) entity;
			            						double distance = entity.getLocation().distance(middle);
									            
			            						if (distance <= coalRange) {
			            							//Player
			            							if (isSameParty(player, found)) {
			            								//OtherPlayers
			            								String playerUUID = found.getUniqueId().toString();
			            								
			            								for (String uuid: plugin.cooldowns.keySet()) {
			            									if (uuid.contains(playerUUID)) {
			            										
			            										int timeLeft = plugin.cooldowns.get(uuid);
		            											
		            											if (timeLeft > 0) {
		            												plugin.cooldowns.remove(uuid);
		            												found.sendMessage(ChatColor.GRAY+"Cooldown's have refreshed!");
		            											}
			            									}
			            								}
			            								
									            	}
			            						}
			            					}
			            				}
			            				
			             	        	count++;
			             	        	if (count == 2) {
			             	        		this.cancel();
			             	        	}
			            			}
			            			
			            		}.runTaskTimer(plugin, 0, 20*5);
			            		
			             		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable(){
			             			public void run(){
			             				currentTotems-=1;
			             				if (currentTotems < 0) {
			             					currentTotems = 0;
			             				}
			             				
			             				stairX1.getBlock().setType(Material.AIR);
			             				stairX2.getBlock().setType(Material.AIR);
			             				stairZ1.getBlock().setType(Material.AIR);
			             				stairZ2.getBlock().setType(Material.AIR);
			             				
			             				lowerCenter.getBlock().setType(Material.AIR);
			             				middle.getBlock().setType(Material.AIR);
			             				top.getBlock().setType(Material.AIR);
			             				
			             				player.sendMessage(ChatColor.GRAY+"Totem of Cooldown Refresh has expired!");
			             			}
			             		}, 20*10);
							}
			          }
			      }, 1*3);
				
			} else if (player.getInventory().getItemInMainHand().getType().equals(Material.WARPED_STEM)) {
				event.setCancelled(true);
				
				Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable(){
					   @SuppressWarnings("deprecation")
					public void run(){
							int x = event.getBlock().getX();
							int y = event.getBlock().getY();
							int z = event.getBlock().getZ();
							World world = event.getBlock().getLocation().getWorld();
							
							Location stairX1 = new Location(world,x-1,y,z);
							Location stairX2 = new Location(world,x+1,y,z);
							
							Location stairZ1 = new Location(world,x,y,z-1);
							Location stairZ2 = new Location(world,x,y,z+1);
							
							Location lowerCenter = new Location(world,x,y,z);
							Location middle = new Location(world,x,y+1,z);
							Location top = new Location(world,x,y+2,z);

							Random rand = new Random();
							int randomNum = rand.nextInt((2 - 1) + 1) + 1;
						
							boolean canPlace = false;
							
							if (checkBlock(stairX1) && checkBlock(stairX2)) {
								if (checkBlock(stairZ1) && checkBlock(stairZ2)) {
									if (checkBlock(lowerCenter) && checkBlock(middle)) {
										if (checkBlock(top)) {
											canPlace = true;
										}
									}
								}
							}
							
							if (randomNum == 1 && canPlace) {
								player.sendMessage(ChatColor.GREEN + "[1 / 2]" + ChatColor.GOLD + " You placed totem of " + ChatColor.RED + "Havoc");
							
								stairX1.getBlock().setType(Material.POLISHED_BLACKSTONE_BRICK_STAIRS);
								rotateStair(stairX1.getBlock(), BlockFace.EAST);
								
								stairX2.getBlock().setType(Material.POLISHED_BLACKSTONE_BRICK_STAIRS);
								rotateStair(stairX2.getBlock(), BlockFace.WEST);
								
								stairZ1.getBlock().setType(Material.POLISHED_BLACKSTONE_BRICK_STAIRS);
								rotateStair(stairZ1.getBlock(), BlockFace.SOUTH);
								
								stairZ2.getBlock().setType(Material.POLISHED_BLACKSTONE_BRICK_STAIRS);
								
								lowerCenter.getBlock().setType(Material.NETHERITE_BLOCK);
								middle.getBlock().setType(Material.WARPED_STEM);
								top.getBlock().setType(Material.NETHER_QUARTZ_ORE);
								
			             		player.playSound(player.getLocation(), Sound.BLOCK_ANCIENT_DEBRIS_PLACE, 2F, 1F);
			             		currentTotems+=1;
			             		
			             		new BukkitRunnable() {
			             			int count = 0;
			             			
			            			@Override
			            			public void run() {

			            				for(Entity entity : middle.getBlock().getWorld().getEntities()) {
			            					if (entity instanceof Player) {
			            						Player found = (Player) entity;
			            						double distance = entity.getLocation().distance(middle);

			            		                if(distance < warpedRange) {
			            		                	if (isSameParty(player, found)) {
			            		                		
									            	} else {
									            		player.sendMessage(found.getName());
								            			found.damage(1);
								            			found.sendMessage(ChatColor.DARK_RED+"You've been hit by totem of Havoc!");
								            			found.getWorld().createExplosion(found.getLocation(), 0.1F, false, false);
									            	}   
		            		                	}	
			            					}			
			            				}
			            				
			             	        	count++;
			             	        	if (count == 5) {
			             	        		this.cancel();
			             	        	}
			            			}
			            			
			            		}.runTaskTimer(plugin, 0, 40);
			             		
			             		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable(){
			             			public void run(){
			             				currentTotems-=1;
			             				if (currentTotems < 0) {
			             					currentTotems = 0;
			             				}
			             				
			             				stairX1.getBlock().setType(Material.AIR);
			             				stairX2.getBlock().setType(Material.AIR);
			             				stairZ1.getBlock().setType(Material.AIR);
			             				stairZ2.getBlock().setType(Material.AIR);
			             				
			             				lowerCenter.getBlock().setType(Material.AIR);
			             				middle.getBlock().setType(Material.AIR);
			             				top.getBlock().setType(Material.AIR);
			             			
			             				player.sendMessage(ChatColor.GRAY+"Totem of Havoc has expired!");
			             			}
			             		}, 20*10);
			             		
							} else if (randomNum == 2 && canPlace) {
								player.sendMessage(ChatColor.GREEN + "[2 / 2]" + ChatColor.GOLD + " You placed totem of " + ChatColor.RED + "Retribution");
								
								stairX1.getBlock().setType(Material.POLISHED_BLACKSTONE_BRICK_STAIRS);
								rotateStair(stairX1.getBlock(), BlockFace.EAST);
								
								stairX2.getBlock().setType(Material.POLISHED_BLACKSTONE_BRICK_STAIRS);
								rotateStair(stairX2.getBlock(), BlockFace.WEST);
								
								stairZ1.getBlock().setType(Material.POLISHED_BLACKSTONE_BRICK_STAIRS);
								rotateStair(stairZ1.getBlock(), BlockFace.SOUTH);
								
								stairZ2.getBlock().setType(Material.POLISHED_BLACKSTONE_BRICK_STAIRS);
								
								lowerCenter.getBlock().setType(Material.NETHERITE_BLOCK);
								middle.getBlock().setType(Material.WARPED_STEM);
								top.getBlock().setType(Material.NETHER_QUARTZ_ORE);
								
			             		player.playSound(player.getLocation(), Sound.BLOCK_ANCIENT_DEBRIS_PLACE, 2F, 1F);
			             		currentTotems+=1;
			             		
			             		
			             		new BukkitRunnable() {
			             			int count = 0;
			             			
			            			@Override
			            			public void run() {
			             	        	
			            				for(Entity entity : middle.getBlock().getWorld().getEntities()) {
			            					if(entity instanceof Player) {
			            						Player found = (Player) entity;
			            						double distance = entity.getLocation().distance(middle);
			            						
			            		                if(distance < warpedRange) {
									            	
			            		                	if (isSameParty(player, found)) {
	
									            	} else {
									            		if (!found.equals(player)) {
									            			found.sendMessage(ChatColor.DARK_RED+"You've been hit by totem of Retribution!");
										            		found.setFireTicks(100);
										            		found.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 40, 0));
									            		}
									            	}   
			            		                }
			            					}
			            				}
			            				
			             	        	count++;
			             	        	if (count == 5) {
			             	        		this.cancel();
			             	        	}
			            			}
			            			
			            		}.runTaskTimer(plugin, 0, 40);
			             		
			             		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable(){
			             			public void run(){
			             				currentTotems-=1;
			             				if (currentTotems < 0) {
			             					currentTotems = 0;
			             				}
			             				
			             				stairX1.getBlock().setType(Material.AIR);
			             				stairX2.getBlock().setType(Material.AIR);
			             				stairZ1.getBlock().setType(Material.AIR);
			             				stairZ2.getBlock().setType(Material.AIR);
			             				
			             				lowerCenter.getBlock().setType(Material.AIR);
			             				middle.getBlock().setType(Material.AIR);
			             				top.getBlock().setType(Material.AIR);
			             				
			             				player.sendMessage(ChatColor.GRAY+"Totem of Retribution has expired!");
			             			}
			             		}, 20*10);
							}
			          }
			      }, 1*3);
				
				
			}
		} else if (currentTotems == maxTotems) {
			event.setCancelled(true);
			player.sendMessage(ChatColor.RED + "Can't place anymore totems, you already have 3!");
		}
		
	}
	
}
