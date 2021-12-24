package classes;

import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
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

public class Assassin implements Listener {

	public static Main plugin = Main.getPlugin(Main.class);
	
	private int bleedChance = 8; //8
	private int evadeChance = 22;
	private String ability;
	public boolean isInvisible = false;
	
	
	public boolean hasBleed(Player player) {
		String bleedUUID1 = player.getUniqueId()+"Bleeding1";
		String bleedUUID2 = player.getUniqueId()+"Bleeding2";
		String bleedUUID3 = player.getUniqueId()+"Bleeding3";
		String bleedUUID4 = player.getUniqueId()+"Bleeding4";
		String bleedUUID5 = player.getUniqueId()+"Bleeding5";
		
		if (plugin.cooldowns.containsKey(bleedUUID1)) {
			return true;
		}
		
		if (plugin.cooldowns.containsKey(bleedUUID2)) {
			return true;
		}
		
		if (plugin.cooldowns.containsKey(bleedUUID3)) {
			return true;
		}
		
		if (plugin.cooldowns.containsKey(bleedUUID4)) {
			return true;
		}

		if (plugin.cooldowns.containsKey(bleedUUID5)) {
			return true;
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
	
	public boolean wearingLeather(Player player) {
		boolean isClass = (plugin.getConfig().getString("Users." + player.getUniqueId() + ".Class").contains("Assassin"));
		
		if (isClass) {
			TextComponent failMessage = new TextComponent("Your not wearing the right armor!");
			failMessage.setColor(ChatColor.DARK_RED);
			failMessage.setBold(true);
			 
			ItemStack Helm = player.getInventory().getHelmet();
			ItemStack Chest = player.getInventory().getChestplate();
			ItemStack Legs = player.getInventory().getLeggings();
			ItemStack Boots = player.getInventory().getBoots();
			
			if (Helm != null && Chest != null && Legs != null && Boots != null) {
				if (Helm.getType().equals(Material.LEATHER_HELMET)) {
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
	
	@EventHandler
	private void onItemDamage(PlayerItemDamageEvent event) {
		Player player = event.getPlayer();
		boolean isAssassin = (plugin.getConfig().getString("Users." + player.getUniqueId() + ".Class").contains("Assassin"));
		
		if (isAssassin) {
			if (wearingLeather(player)) {
				if (event.getItem().getType().equals(Material.LEATHER_HELMET)) {
					event.setDamage(event.getDamage()/2);
				}
				if (event.getItem().getType().equals(Material.LEATHER_CHESTPLATE)) {
					event.setDamage(event.getDamage()/2);
				}
				if (event.getItem().getType().equals(Material.LEATHER_LEGGINGS)) {
					event.setDamage(event.getDamage()/2);
				}
				if (event.getItem().getType().equals(Material.LEATHER_BOOTS)) {
					event.setDamage(event.getDamage()/2);
				}
			}
		}
	}
	
	@EventHandler(priority=EventPriority.HIGH)
	private void onPlayerUse(PlayerInteractEvent event){
		Player player = event.getPlayer();
		Material mainHand = player.getInventory().getItemInMainHand().getType();
		boolean isAssassin = (plugin.getConfig().getString("Users." + player.getUniqueId() + ".Class").contains("Assassin"));
		
		if(event.getItem() == null) {
			return;
	    }

		 if ((event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) && event.getHand() == EquipmentSlot.HAND) {
				if (isAssassin) {
			        if (wearingLeather(player) && hasAsword(player)) {   
	        			String AnkleCutterUUID = player.getUniqueId()+"AnkleCutter";
	        			
	        			if (!plugin.cooldowns.containsKey(AnkleCutterUUID)) {
	        				player.sendMessage(ChatColor.BLUE + "You ready your" + ChatColor.GOLD + " Ankle Cutter");
	        				ability = "AnkleCutter";
	        			} else {
	        				player.sendMessage(ChatColor.RED + "Ankle Cutter on cooldown, " + ChatColor.YELLOW + plugin.cooldowns.get(AnkleCutterUUID) + " seconds" + ChatColor.RED + " left.");
	        			}
			        }
				}
		 } else if ((event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) && event.getHand() == EquipmentSlot.HAND) {
			if (isAssassin) {
		        if (wearingLeather(player)) {   
		        	if (mainHand.equals(Material.GHAST_TEAR)) {
		        		
	    				String SneakUUID = player.getUniqueId()+"Sneak";
		    			if (!plugin.cooldowns.containsKey(SneakUUID)) {
		        			plugin.masterCD = 40;
			        		plugin.cooldowns.put(SneakUUID, plugin.masterCD);
			        		plugin.getConfig().set("Users." + player.getUniqueId() + ".isInvisible", "true");
			        		player.hidePlayer(plugin, player);
			        		plugin.invisList.put(SneakUUID, true);
			        		
			        		player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 400, 1));
			        		player.playSound(player.getLocation(), Sound.ENTITY_WITCH_DRINK, 2F, 1F);
			        		player.sendMessage(ChatColor.BLUE + "You are " + ChatColor.GOLD + "sneaking.");
		    			} else {
	    					player.sendMessage(ChatColor.RED + "Sneak On cooldown, " + ChatColor.YELLOW + plugin.cooldowns.get(SneakUUID) + " seconds" + ChatColor.RED + " left.");
		    			}
		        	}
    			}
	        }
		 } else if ((event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) && event.getHand() == EquipmentSlot.HAND) {
			 if (isAssassin) {
				 if (wearingLeather(player)) {   
		        	if (mainHand.equals(Material.GHAST_TEAR)) {
		        		String SneakUUID = player.getUniqueId()+"Sneak";
		        		
		        		if (!plugin.cooldowns.containsKey(SneakUUID)) {
		        			player.sendMessage(ChatColor.GREEN + "Sneak is available");
		        		} else {
	    					player.sendMessage(ChatColor.RED + "Sneak On cooldown, " + ChatColor.YELLOW + plugin.cooldowns.get(SneakUUID) + " seconds" + ChatColor.RED + " left.");
		    			}
		        	}
		        }
			 }
		 }
       
	}

	@EventHandler
	private void onEntityDamaged(EntityDamageByEntityEvent event) {
		Player damager = (Player) event.getDamager();
		Player damagedEntity = (Player) event.getEntity();
		boolean isAssassin = (plugin.getConfig().getString("Users." + damager.getUniqueId() + ".Class").contains("Assassin"));
		boolean damagedAssassin = (plugin.getConfig().getString("Users." + damagedEntity.getUniqueId() + ".Class").contains("Assassin"));
		
		String DamagerSneakUUID = damagedEntity.getUniqueId()+"Sneak";	
		String DamagedSneakUUID = damagedEntity.getUniqueId()+"Sneak";	
		
		if (damagedAssassin) {
			if (wearingLeather(damagedEntity)) { 
				
				int evade = (int)(Math.random() * 99 + 1);
				if (evade <= evadeChance) {
					event.setCancelled(true);
					event.getEntity().sendMessage(ChatColor.BLUE + "You have evaded!");
				}
				
				if (wearingLeather(damagedEntity)) {
					event.setDamage(event.getDamage()/1.25);
				}
				
			}
			
		} else if (isAssassin) {
			if (isSameParty(damager, damagedEntity)) {

			} else {
				String SneakUUID = damager.getUniqueId()+"Sneak";	
				
				for (Player toHide : Bukkit.getServer().getOnlinePlayers()) {
					for (Player players : Bukkit.getServer().getOnlinePlayers()) {	
						if (toHide.isInvisible()) {
							players.showPlayer(plugin, toHide);
							toHide.sendMessage(ChatColor.YELLOW+"Rejoined invisible, set to visible.");
						}
					}
				}
				
				if (plugin.invisList.containsKey(SneakUUID)) {
					plugin.invisList.remove(SneakUUID);
					damager.sendMessage("Sneak has been removed on death!");
				}
				
				if (wearingLeather(damager)) { 
					
					if (damager.isSneaking()) {
	    				if (ability == "AnkleCutter") {
	    					
							String AnkleCutterUUID = event.getDamager().getUniqueId()+"AnkleCutter";
							if (!plugin.cooldowns.containsKey(AnkleCutterUUID)) {
								plugin.masterCD = 20;
				        		plugin.cooldowns.put(AnkleCutterUUID, plugin.masterCD);
				        		
				        		((LivingEntity) event.getEntity()).addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 100, 5));
				        		event.getDamager().sendMessage(ChatColor.GOLD + "Ankle Cut!");
				        		
								ability = "None";
							}
						}	
					}
					
					int bleed = (int)(Math.random() * 99 + 1);
					if (bleed <= bleedChance) {
						damager.sendMessage("Bleed Enemy");
						
						boolean isBleeding = hasBleed(damagedEntity);
						String playerUUID = damagedEntity.getUniqueId().toString();
						
						String bleedUUID1 = damagedEntity.getUniqueId()+"Bleeding1";
						String bleedUUID2 = damagedEntity.getUniqueId()+"Bleeding2";
						String bleedUUID3 = damagedEntity.getUniqueId()+"Bleeding3";
						String bleedUUID4 = damagedEntity.getUniqueId()+"Bleeding4";
						String bleedUUID5 = damagedEntity.getUniqueId()+"Bleeding5";
						
						
						if (isBleeding == false) {
							if (!plugin.cooldowns.containsKey(bleedUUID1)) {
								plugin.masterCD = 10;
				        		plugin.cooldowns.put(bleedUUID1, plugin.masterCD);
				        		damager.sendMessage(ChatColor.DARK_RED+"Enemy is lightly bleeding");
				        		damagedEntity.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 200, 0));
							}
						} else {
							for (String uuid: plugin.cooldowns.keySet()) {
								String[] arrOfStr = uuid.split(" ");
								 
								 for (String getString : arrOfStr) {
									 if (getString.contains(playerUUID)) {
										 if (getString.contains("Bleeding1")) {
											 plugin.cooldowns.remove(bleedUUID1);
											 
											 plugin.masterCD = 10;
								        	 plugin.cooldowns.put(bleedUUID2, plugin.masterCD);
								        	 damager.sendMessage(ChatColor.DARK_RED+"Enemy is lightly bleeding");
								        	 damagedEntity.sendMessage(ChatColor.DARK_RED+"Your lightly bleeding!");
								        	 damagedEntity.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 200, 1));
								        	 
										 } else if (getString.contains("Bleeding2")) {
											 plugin.cooldowns.remove(bleedUUID2);
											 
											 plugin.masterCD = 10;
								        	 plugin.cooldowns.put(bleedUUID3, plugin.masterCD);
								        	 damager.sendMessage(ChatColor.DARK_RED+"Enemy is moderately bleeding");
								        	 damagedEntity.sendMessage(ChatColor.DARK_RED+"Your moderately bleeding!");
								        	 damagedEntity.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 200, 2));
								        	 
										 } else if (getString.contains("Bleeding3")) {
											 plugin.cooldowns.remove(bleedUUID3);
											 
											 plugin.masterCD = 10;
								        	 plugin.cooldowns.put(bleedUUID4, plugin.masterCD);
								        	 
								        	 damager.sendMessage(ChatColor.DARK_RED+"Enemy is heavily bleeding");
								        	 damagedEntity.sendMessage(ChatColor.DARK_RED+"Your heavily bleeding!");
								        	 damagedEntity.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 200, 3));
								        	 
										 } else if (getString.contains("Bleeding4")) {
											 plugin.cooldowns.remove(bleedUUID4);
											 
											 plugin.masterCD = 10;
								        	 plugin.cooldowns.put(bleedUUID5, plugin.masterCD);
								        	 
								        	 damager.sendMessage(ChatColor.DARK_RED+"Enemy is severely bleeding");
								        	 damagedEntity.sendMessage(ChatColor.DARK_RED+"Your severely bleeding!");
								        	 damagedEntity.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 200, 4));
								        	 
										 } else if (getString.contains("Bleeding5")) {
											 plugin.masterCD = 10;
											 plugin.cooldowns.put(bleedUUID5, plugin.masterCD);
											 
											 damager.sendMessage(ChatColor.DARK_RED+"Enemy is severely bleeding");
											 damagedEntity.sendMessage(ChatColor.DARK_RED+"Your severely bleeding!");
											 damagedEntity.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 200, 5));
										 }
									 }
								 }
							}	
						}	
					}

    				if (plugin.invisList.containsKey(DamagerSneakUUID)) {
						damager.removePotionEffect(PotionEffectType.SPEED);
						
						boolean isBleeding = hasBleed(damagedEntity);
						String playerUUID = damagedEntity.getUniqueId().toString();
						
						String bleedUUID1 = damagedEntity.getUniqueId()+"Bleeding1";
						String bleedUUID2 = damagedEntity.getUniqueId()+"Bleeding2";
						String bleedUUID3 = damagedEntity.getUniqueId()+"Bleeding3";
						String bleedUUID4 = damagedEntity.getUniqueId()+"Bleeding4";
						String bleedUUID5 = damagedEntity.getUniqueId()+"Bleeding5";
						
						
						if (isBleeding == false) {
							if (!plugin.cooldowns.containsKey(bleedUUID4)) {
								plugin.masterCD = 10;
				        		plugin.cooldowns.put(bleedUUID3, plugin.masterCD);
				        		damager.sendMessage(ChatColor.DARK_RED+"Enemy is severely bleeding");
				        		damagedEntity.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 200, 4));
							}
						} else {
							for (String uuid: plugin.cooldowns.keySet()) {
								String[] arrOfStr = uuid.split(" ");
								 
								 for (String getString : arrOfStr) {
									 if (getString.contains(playerUUID)) {
										 if (getString.contains("Bleeding1")) {
											 plugin.cooldowns.remove(bleedUUID1);
											 
											 plugin.masterCD = 10;
								        	 plugin.cooldowns.put(bleedUUID2, plugin.masterCD);
								        	 damager.sendMessage(ChatColor.DARK_RED+"Enemy is lightly bleeding");
								        	 damagedEntity.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 200, 1));
								        	 
										 } else if (getString.contains("Bleeding2")) {
											 plugin.cooldowns.remove(bleedUUID2);
											 
											 plugin.masterCD = 10;
								        	 plugin.cooldowns.put(bleedUUID3, plugin.masterCD);
								        	 damager.sendMessage(ChatColor.DARK_RED+"Enemy is moderately bleeding");
								        	 damagedEntity.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 200, 2));
								        	 
										 } else if (getString.contains("Bleeding3")) {
											 plugin.cooldowns.remove(bleedUUID3);
											 
											 plugin.masterCD = 10;
								        	 plugin.cooldowns.put(bleedUUID4, plugin.masterCD);
								        	 
								        	 damager.sendMessage(ChatColor.DARK_RED+"Enemy is heavily bleeding");
								        	 damagedEntity.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 200, 3));
								        	 
										 } else if (getString.contains("Bleeding4")) {
											 plugin.cooldowns.remove(bleedUUID4);
											 
											 plugin.masterCD = 10;
								        	 plugin.cooldowns.put(bleedUUID5, plugin.masterCD);
								        	 
								        	 damager.sendMessage(ChatColor.DARK_RED+"Enemy is severely bleeding");
								        	 damagedEntity.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 200, 4));
								        	 
										 } else if (getString.contains("Bleeding5")) {
											 plugin.masterCD = 10;
											 plugin.cooldowns.put(bleedUUID5, plugin.masterCD);
											 
											 damager.sendMessage(ChatColor.DARK_RED+"Enemy is severely bleeding");
											 damagedEntity.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 200, 5));
										 }
									 }
								 }
							}	
						}
						
						damagedEntity.damage(event.getDamage()*3);
						damagedEntity.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 100, 5));
						damagedEntity.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 100, 1));
						
						plugin.invisList.remove(DamagerSneakUUID);
					}
				}
			}
		}
	}
	
	@EventHandler
	public void onDied(PlayerDeathEvent event) {
		Player player = event.getEntity();
		
		String SneakUUID = player.getUniqueId()+"Sneak";	
		
		if (plugin.invisList.containsKey(SneakUUID)) {
			plugin.invisList.remove(SneakUUID);
			player.sendMessage("Sneak has been removed on death!");
		}
	}
	
}
