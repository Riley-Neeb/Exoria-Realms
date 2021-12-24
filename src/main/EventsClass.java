package main;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;

import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;


import net.md_5.bungee.api.ChatColor;


public class EventsClass implements Listener {
	
	private Main plugin = Main.getPlugin(Main.class);
	
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
	public void onDamage(EntityDamageEvent event) {
		
		if (event.getCause() == DamageCause.FALL){
			if (plugin.getConfig().getString("Users." + event.getEntity().getUniqueId() + ".Class").contains("Marksman")) {
				if (plugin.getConfig().getString("Users." + event.getEntity().getUniqueId() + ".isLeaping").contains("true")) {
					 event.setCancelled(true);
					 plugin.getConfig().set("Users." + event.getEntity().getUniqueId() + ".isLeaping", "false");
				} else {
					int rollRandom = (int)(Math.random() * 99 + 1);
					
					if (rollRandom <= 25) {
						Player player = (Player) event.getEntity();
						 event.setCancelled(true);
						 event.getEntity().sendMessage(ChatColor.GREEN + "Rolled fall damage away");
						 player.playSound(player.getLocation(), Sound.ENTITY_BAT_TAKEOFF, 2F, 1F);
					}
				}
			}
		 }
	}
	
	@EventHandler
	public void EntityDamageEntity(EntityDamageByEntityEvent event) {
		Player damagedEntity = (Player) event.getEntity();
		Projectile proj = (Projectile) event.getDamager();
		Player shooter = (Player) proj.getShooter();
		
		if (event.getCause() == DamageCause.PROJECTILE) {
			if (isSameParty(shooter, damagedEntity)) {
				event.setCancelled(true);
				shooter.sendMessage(ChatColor.RED+"Can't hurt a friendly player!");
			} else {
				double projectile_height = proj.getLocation().getY();
				double player_bodyheight = damagedEntity.getLocation().getY()+1.5;
				
				if(projectile_height>player_bodyheight){
					boolean isMarksman = (plugin.getConfig().getString("Users." + shooter.getUniqueId() + ".Class").contains("Marksman"));
					
					if (isMarksman) {
						double distance = shooter.getLocation().distance(damagedEntity.getLocation());
						double critDamage = 1+(distance/24);
						String format = String.format("%.1f", critDamage);
						critDamage = Double.parseDouble(Double.toString(critDamage).substring(0, Double.toString(critDamage).indexOf('.') + 2));
						distance = Double.parseDouble(Double.toString(distance).substring(0, Double.toString(distance).indexOf('.') + 2));
						
						if (critDamage > 3.0) {
							critDamage = 3;
						}
						
						if (distance <= 180) {
							event.setDamage(event.getDamage()*critDamage);
							shooter.sendMessage(ChatColor.GOLD+""+critDamage+"X"+ChatColor.GREEN+ChatColor.BOLD+" HEADSHOT! "+ChatColor.GOLD+distance+" blocks away!");
							damagedEntity.sendMessage(ChatColor.RED+"You were headshotted!");
						}
					}	
				}
				
				if (proj.hasMetadata("Poison")) {
					damagedEntity.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 80, 1));
				}
		            
		        if (proj.hasMetadata("Fire")) {
		        	damagedEntity.setFireTicks(80);
		        }
		        	 
		        if (proj.hasMetadata("Daze")) {
		        	int UpDownChance = (int)(Math.random() * 99 + 1);
		    		if (UpDownChance <= 50) {
		    			damagedEntity.getLocation().setPitch(-90);
		    		} else if (UpDownChance >= 50) {
		    			damagedEntity.getLocation().setPitch(90);
		    		}
		        }
		    		
		        if (proj.hasMetadata("bonusDamage1")) {
		   			event.setDamage(event.getDamage()+1);
		        }
		   			
		        if (proj.hasMetadata("bonusDamage2")) {
		   			event.setDamage(event.getDamage()+2);
		        }
		   			
		        if (proj.hasMetadata("bonusDamage3")) {
		   			event.setDamage(event.getDamage()+3);
		        }
		   			
		        if (proj.hasMetadata("bonusDamage4")) {
		   			event.setDamage(event.getDamage()+4);
		        }
		   			
		        if (proj.hasMetadata("PoisonShot")) {
		        	shooter.sendMessage(ChatColor.GREEN+"Poisoned enemy!");
		   			event.setDamage(event.getDamage()+2);
		   			
		   			if (isSameParty(shooter, damagedEntity)) {
		   				
		   			} else {
		   				damagedEntity.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 60, 1));
		   			}
		   			
		        }
		        
		        if (proj.hasMetadata("ArrowToKnee")) {
		        	shooter.sendMessage(ChatColor.GREEN+"Slowed enemy!");
		   			event.setDamage(event.getDamage()+2);
		   			
		   			if (isSameParty(shooter, damagedEntity)) {
		   				
		   			} else {
		   				damagedEntity.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 40, 100));
		   			}
				
		        }
			}
		} else {
			Player damager = (Player) event.getDamager();
			
			if (isSameParty(damager, damagedEntity)) {
				event.setCancelled(true);
				damager.sendMessage(ChatColor.RED+"Can't hurt a friendly player!");
			}
		}
	}
	
	@EventHandler
	public void onDied(PlayerDeathEvent event) {
		Player player = event.getEntity();
		Player killer = player.getKiller();
		
		String SneakUUID = player.getUniqueId()+"Sneak";	
		
		plugin.getConfig().set("Users." + player.getUniqueId() + ".Deaths", plugin.getConfig().getInt("Users." + player.getUniqueId() + ".Deaths", 0) + 1);
		plugin.getConfig().set("Users." + killer.getUniqueId() + ".Kills", plugin.getConfig().getInt("Users." + player.getUniqueId() + ".Kills", 0) + 1);
		plugin.getConfig().set("Users." + killer.getUniqueId() + ".XP", plugin.getConfig().getInt("Users." + player.getUniqueId() + ".XP", 0) + 1);
	}
	
	@EventHandler
	public void firstJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		String playerName = player.getName();
		boolean hasPlayed = player.hasPlayedBefore();
		
		if (hasPlayed) {
			Bukkit.broadcastMessage(ChatColor.YELLOW + "Welcome back " + player.getName());
			
			for (Player toHide : Bukkit.getServer().getOnlinePlayers()) {
				for (Player players : Bukkit.getServer().getOnlinePlayers()) {	
					if (toHide.isInvisible()) {
						players.showPlayer(plugin, toHide);
						toHide.sendMessage(ChatColor.YELLOW+"Rejoined invisible, set to visible.");
					}
				}
			}
		} else {
			Bukkit.broadcastMessage(ChatColor.YELLOW + player.getName() + " has joined the game for the first time!");
			plugin.getConfig().set("Users." + player.getUniqueId(), player);
			plugin.getConfig().set("Users." + player.getUniqueId() + ".Name", playerName);
			plugin.getConfig().set("Users." + player.getUniqueId() + ".Class" , "None");
			plugin.getConfig().set("Users." + player.getUniqueId() + ".Kills" , 0);
			plugin.getConfig().set("Users." + player.getUniqueId() + ".Deaths" , 0);
			plugin.getConfig().set("Users." + player.getUniqueId() + ".Race", "None");
						
			plugin.saveConfig();
		}
	}
	
	@EventHandler
	public void playerQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		
		plugin.saveConfig();
			
		Bukkit.broadcastMessage(ChatColor.YELLOW + "Goodbye. " + player.getName());
		
	}
	
	@EventHandler
	public void playerRespawn(PlayerRespawnEvent event) {
		Player player = event.getPlayer();
		
		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable(){
			public void run(){
				if (plugin.getConfig().getString("Users." + player.getUniqueId() + ".Race").contains("Human")) {
					player.setAbsorptionAmount(0);
					
					player.sendMessage(ChatColor.LIGHT_PURPLE + "Spawned as a human");
					player.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, Integer.MAX_VALUE, 1));
					player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 0));
					player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, Integer.MAX_VALUE, 1));
				
					AttributeInstance attribute = player.getAttribute(Attribute.GENERIC_MAX_HEALTH);
				    attribute.setBaseValue(40.0D);
				     
				} else if (plugin.getConfig().getString("Users." + player.getUniqueId() + ".Race").contains("Orc")) {
					player.setAbsorptionAmount(0);
					
					player.sendMessage(ChatColor.LIGHT_PURPLE + "Spawned as an Orc");
					player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 0));
					player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, 0));
					player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, Integer.MAX_VALUE, 0));
					 
					AttributeInstance attribute = player.getAttribute(Attribute.GENERIC_MAX_HEALTH);
				    attribute.setBaseValue(40.0D);
				     
				 } else if (plugin.getConfig().getString("Users." + player.getUniqueId() + ".Race").contains("Dwarf")) {
					 player.setAbsorptionAmount(0);
						
					 player.sendMessage(ChatColor.LIGHT_PURPLE + "Spawned as a Dwarf");
					 player.addPotionEffect(new PotionEffect(PotionEffectType.LUCK, Integer.MAX_VALUE, 2));
					 player.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, Integer.MAX_VALUE, 1));
					 player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, Integer.MAX_VALUE, 0));
						 
					 AttributeInstance attribute = player.getAttribute(Attribute.GENERIC_MAX_HEALTH);
					 attribute.setBaseValue(40.0D);
				     
				 } else if (plugin.getConfig().getString("Users." + player.getUniqueId() + ".Race").contains("Undead")) {

					 player.sendMessage(ChatColor.LIGHT_PURPLE + "Spawned as an Undead");
					 player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, Integer.MAX_VALUE, 0));
					 player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, Integer.MAX_VALUE, 0));
					 player.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, Integer.MAX_VALUE, 13));
					 player.addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, Integer.MAX_VALUE, 8));
					 player.addPotionEffect(new PotionEffect(PotionEffectType.WATER_BREATHING, Integer.MAX_VALUE, 0));
					 player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, Integer.MAX_VALUE, 0));
					 
					 AttributeInstance attribute = player.getAttribute(Attribute.GENERIC_MAX_HEALTH);
					 attribute.setBaseValue(4.0D);
				 }   
			}
		}, 1*10);
	}
	
}