package testclasses;

import org.bukkit.Material;
import org.bukkit.Sound;
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
import org.bukkit.scheduler.BukkitRunnable;

import main.Main;
import net.md_5.bungee.api.ChatColor;

public class Martyr implements Listener {

	public static Main plugin = Main.getPlugin(Main.class);
	
	
	private int martyrAction = 0;
	private String ability;
	private boolean invulnerable = false;
	private boolean usedInvulnerability = false;
	
	public boolean wearingMartyr(Player player) {
		ItemStack Helm = player.getInventory().getHelmet();
		ItemStack Chest = player.getInventory().getChestplate();
		ItemStack Legs = player.getInventory().getLeggings();
		ItemStack Boots = player.getInventory().getBoots();
		
		if (Helm.getType().equals(Material.LEATHER_HELMET)) {
			if (Chest.getType().equals(Material.GOLDEN_CHESTPLATE)) {
				if (Legs.getType().equals(Material.GOLDEN_LEGGINGS)) {
					if (Boots.getType().equals(Material.LEATHER_BOOTS)) {
						return true;
					} 
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
			 boolean isMartyr = (plugin.getConfig().getString("Users." + player.getUniqueId() + ".Class").contains("Martyr"));
			 if (isMartyr) {
		        if (wearingMartyr(player)) {   
		        	Material mainHand = player.getInventory().getItemInMainHand().getType();

		        	if (mainHand.equals(Material.GOLDEN_SWORD)) {

		        		martyrAction += 1;
			       		 
		       		 	if (martyrAction > 4) {
		       		 		martyrAction = 1;
		              	}
		       		 
		       			if (martyrAction == 1) {
		       				
		             		player.sendMessage(ChatColor.GREEN + "[1 / 4]" + ChatColor.BLUE + " You ready your" + ChatColor.GOLD + " Flagellation");
		             		player.playSound(player.getLocation(), Sound.ENTITY_COW_MILK, 2F, 1F);
		             		ability = "Flagellation";
		             		
		       			} else if (martyrAction == 2) {
		       				
		       				String RevitalizeUUID = player.getUniqueId()+"Revitalize";
		       				
		       				if (!plugin.cooldowns.containsKey(RevitalizeUUID)) {
		       					player.sendMessage(ChatColor.GREEN + "[2 / 4]" + ChatColor.BLUE + " You ready your" + ChatColor.GOLD + " Revitalize");
			             		player.playSound(player.getLocation(), Sound.ENTITY_COW_MILK, 2F, 1F);
		       				} else {
		       					player.sendMessage(ChatColor.RED + "Revitalize on cooldown, " + ChatColor.YELLOW + plugin.cooldowns.get(RevitalizeUUID) + " seconds" + ChatColor.RED + " left.");
		       				}
		       				
		             	} else if (martyrAction == 3) {
		             		
		             		//Sacrifice
		             		String SacrificeUUID = player.getUniqueId()+"Sacrifice";
		       				if (!plugin.cooldowns.containsKey(SacrificeUUID)) {
		       					player.sendMessage(ChatColor.GREEN + "[3 / 4]" + ChatColor.BLUE + " You ready your" + ChatColor.GOLD + " Sacrifice");
			             		player.playSound(player.getLocation(), Sound.ENTITY_COW_MILK, 2F, 1F);
		       				} else {
		       					player.sendMessage(ChatColor.RED + "Sacrifice on cooldown, " + ChatColor.YELLOW + plugin.cooldowns.get(SacrificeUUID) + " seconds" + ChatColor.RED + " left.");
		       				}
		             		
		             	} else if (martyrAction == 4) {
		             		
		             		String SwitcharooUUID = player.getUniqueId()+"Switcharoo";
		             		
		             		if (!plugin.cooldowns.containsKey(SwitcharooUUID)) {
		       					player.sendMessage(ChatColor.GREEN + "[4 / 4]" + ChatColor.BLUE + " You ready your" + ChatColor.GOLD + " Switcharoo");
			             		player.playSound(player.getLocation(), Sound.ENTITY_COW_MILK, 2F, 1F);
			             		ability = "Switcharoo";
		       				} else {
		       					player.sendMessage(ChatColor.RED + "Switcharoo on cooldown, " + ChatColor.YELLOW + plugin.cooldowns.get(SwitcharooUUID) + " seconds" + ChatColor.RED + " left.");
		       				}
		             	}
		        		
		        	}
		        }
			}
		 } else if ((event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) && event.getHand() == EquipmentSlot.HAND) {
			 Player player = event.getPlayer();
			 boolean isMartyr = (plugin.getConfig().getString("Users." + player.getUniqueId() + ".Class").contains("Martyr"));
			 if (isMartyr) {
		        if (wearingMartyr(player)) {   
		        	Material mainHand = player.getInventory().getItemInMainHand().getType();
		        	if (mainHand.equals(Material.GOLDEN_SWORD)) {
		        		String RevitalizeUUID = player.getUniqueId()+"Revitalize";
		        		
	       				if (martyrAction == 2) {
			        		if (!plugin.cooldowns.containsKey(RevitalizeUUID)) {
								plugin.masterCD = 20;
				        		plugin.cooldowns.put(RevitalizeUUID, plugin.masterCD);
				        		
				        		player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 20, 5));
				        		player.sendMessage(ChatColor.BLUE + "You used" + ChatColor.GOLD + " Revitalize!");
							}
	       				} else if (martyrAction == 3) {
	       					
	       					String SacrificeUUID = player.getUniqueId()+"Sacrifice";
	       					
	       					if (!plugin.cooldowns.containsKey(SacrificeUUID)) {
		             			plugin.masterCD = 300; //5 minutes
				        		plugin.cooldowns.put(SacrificeUUID, plugin.masterCD);
				        		player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 140, 50));
				        		for(Entity entity : player.getNearbyEntities(6, 6, 6)) {
						            if(entity instanceof Player) {
						                Player found = (Player) entity;
						                
						                found.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 140, 50));
						                found.sendMessage(ChatColor.GREEN + player.getName() + ChatColor.BLUE + " blessed you with invincibility for" + ChatColor.GOLD + " 10 seconds");
						            }
						        }        		
	       					}
	       				}
		        	}
		        
		        }
			}
        }
       
	}
	
	
	@EventHandler
	private void onEntityHit(EntityDamageByEntityEvent event) {

		if (event.getEntity() instanceof Player) { //if the attacked is a player		
			boolean isDamagerMartyr = (plugin.getConfig().getString("Users." + event.getDamager().getUniqueId() + ".Class").contains("Martyr"));
			//boolean isDamagedAssassin = (plugin.getConfig().getString("Users." + event.getEntity().getUniqueId() + ".Class").contains("Assassin"));
			Player Damager = (Player) event.getDamager();
			Player Damaged = (Player) event.getEntity();
			double oldHealth = Damager.getHealth();
					
			if (isDamagerMartyr) {
				if (wearingMartyr((Player) event.getDamager())) { 
					//Bloodlust passive, more damage based on missing health.
					event.setDamage(event.getDamage()+(25/(Damager.getHealth())));
					
					if (ability == "Switcharoo") {
						String SwitcharooUUID = Damager.getUniqueId()+"Switcharoo";
						
						if (!plugin.cooldowns.containsKey(SwitcharooUUID)) {
							plugin.masterCD = 20;
			        		plugin.cooldowns.put(SwitcharooUUID, plugin.masterCD);
			        		
			        		Damager.setHealth(Damaged.getHealth());
			        		Damaged.setHealth(oldHealth);
			        		Damaged.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 10, 50));
						}
					}
					
					if (ability == "Flagellation") {
						((LivingEntity) event.getEntity()).addPotionEffect(new PotionEffect(PotionEffectType.POISON, 40, 1));
						Damager.setHealth(Damager.getHealth()-1);
						ability = "None";
					}
					
				}
			}
		}
	}
	
}
