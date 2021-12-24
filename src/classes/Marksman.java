
package classes;

import java.util.Random;

import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;

import main.Main;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;

public class Marksman implements Listener {

	public Main plugin = Main.getPlugin(Main.class);
	
	private int marksmanActive = 0;
	public boolean isLeaping = false;
	public String ability = "None";
	
	
	public boolean wearingLeather(Player player) {
		boolean isClass = (plugin.getConfig().getString("Users." + player.getUniqueId() + ".Class").contains("Marksman"));
		
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
		
		boolean isDamagerMarksman = (plugin.getConfig().getString("Users." + damager.getUniqueId() + ".Class").contains("Marksman"));
		boolean isDamagedMarksman = (plugin.getConfig().getString("Users." + damagedEntity.getUniqueId() + ".Class").contains("Marksman"));
		
		if (isDamagerMarksman) {
			if (isSameParty(damager, damagedEntity)) {
				damager.sendMessage(ChatColor.RED+"You can't hurt a party member!");
				event.setCancelled(true);
			}
		}
				
		if (isDamagedMarksman) {
			Random rand = new Random();
			int randomNum = rand.nextInt((5 - 1) + 1) + 1;
			
			if (randomNum == 1) {
				event.setCancelled(true); 
				damagedEntity.sendMessage(ChatColor.GREEN + "You just flinched");
				damager.sendMessage(ChatColor.RED + "Enemy flinched!");
			}
			
			event.setDamage(event.getDamage()/1.25);
		}
	}
	
	@EventHandler
    public void onProjectileLaunch(ProjectileLaunchEvent e) {
        Player shooter = (Player) e.getEntity().getShooter();
        Projectile proj = (Projectile) e.getEntity();
		
    	boolean isMarksman = (plugin.getConfig().getString("Users." + shooter.getUniqueId() + ".Class").contains("Marksman"));
    	
    	if (isMarksman) {
    		String ATKUUID = shooter.getUniqueId()+"ArrowToKnee";
    		String PoisonUUID = shooter.getUniqueId()+"PoisonShot";
    		
    		double helmBonus = 0;
    		double chestBonus = 0;
    		double legsBonus = 0;
    		double bootsBonus = 0;
    		double totalBonus = 0;
    		
    		ItemStack Helm = shooter.getInventory().getHelmet();
    		ItemStack Chest = shooter.getInventory().getChestplate();
    		ItemStack Legs = shooter.getInventory().getLeggings();
    		ItemStack Boots = shooter.getInventory().getBoots();
    		
    		int fireChance = (int)(Math.random() * 99 + 1);
    	    int dazeChance = (int)(Math.random() * 99 + 1);
    		
    		if (Helm != null && Helm.getType().equals(Material.LEATHER_HELMET)) {
    			helmBonus = 1;
    		} else {
    			helmBonus = 0;
    		}
    		
    		if (Chest != null && Chest.getType().equals(Material.LEATHER_CHESTPLATE)) {
				chestBonus = 1;
			} else {
				chestBonus = 0;
    		}
    		
    		if (Legs != null && Legs.getType().equals(Material.LEATHER_LEGGINGS)) {
    			legsBonus = 1;
			} else {
				legsBonus = 0;
			}
    		
    		if (Boots != null && Boots.getType().equals(Material.LEATHER_BOOTS)) {
    			bootsBonus = 1;
			} else {
				bootsBonus = 0;
    		}
    		totalBonus = (helmBonus+chestBonus+legsBonus+bootsBonus);
    		
    		if (totalBonus == 1) {
    			proj.setMetadata("bonusDamage1", new FixedMetadataValue(plugin, true));
    		} else if (totalBonus == 2) {
    			proj.setMetadata("bonusDamage2", new FixedMetadataValue(plugin, true));
    		} else if (totalBonus == 3) {
    			proj.setMetadata("bonusDamage3", new FixedMetadataValue(plugin, true));
    		} else if (totalBonus == 4) {
    			proj.setMetadata("bonusDamage4", new FixedMetadataValue(plugin, true));
    		}
    		
        	if (fireChance <= 25) {
        		proj.setMetadata("Fire", new FixedMetadataValue(plugin, true));
        	}
        	if (dazeChance <= 15) {
        		proj.setMetadata("Daze", new FixedMetadataValue(plugin, true));
        	}
        	if (shooter.isSneaking()) {
	        	if (ability.equals("ArrowToKnee")) {
	        		proj.setMetadata("ArrowToKnee", new FixedMetadataValue(plugin, true));
	        		plugin.masterCD = 8;
	        		plugin.cooldowns.put(ATKUUID, plugin.masterCD);
	        		ability = "None";
	        	}
        		if (ability.equals("PoisonShot")) {
        			proj.setMetadata("PoisonShot", new FixedMetadataValue(plugin, true));
        			plugin.masterCD = 8;
	        		plugin.cooldowns.put(PoisonUUID, plugin.masterCD);
        			ability = "None";
	        	}
        	} else {
        		shooter.sendMessage(ChatColor.RED + "You need to sneak to use this ability!");
        	}
        }
    }
	
	@EventHandler
	private void MarksmanInteract(PlayerInteractEvent event) {
	
		if ((event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) && event.getHand() == EquipmentSlot.HAND) {
			Player player = event.getPlayer();
			Material mainHand = player.getInventory().getItemInMainHand().getType();
			if (mainHand.equals(Material.BOW)) {
				boolean isMarksman = (plugin.getConfig().getString("Users." + player.getUniqueId() + ".Class").contains("Marksman"));
				if (isMarksman) {
					if (wearingLeather(player)) {
			    		
		    			marksmanActive += 1;
		    			
		    			if (marksmanActive > 2) {
		    				marksmanActive = 1;
		    			}
		    			
		    			if (marksmanActive == 1) {
		             		//Hymn of strength.
		    				String ATKUUID = player.getUniqueId()+"ArrowToKnee";
		    				
		    				if (!plugin.cooldowns.containsKey(ATKUUID)) {
			             		player.sendMessage(ChatColor.GREEN + "[1 / 2]" + ChatColor.BLUE + " You ready your " + ChatColor.GOLD + "Arrow to the knee");
			             		ability = "ArrowToKnee";
		    				} else {
		    					player.sendMessage(ChatColor.RED + "Arrow to Knee on cooldown, " + ChatColor.YELLOW + plugin.cooldowns.get(ATKUUID) + " seconds" + ChatColor.RED + " left.");
		    				}
		             	} else if (marksmanActive == 2) {
		             		//Hymn of Speed.
		             		String PoisonUUID = player.getUniqueId()+"PoisonShot";
		             		if (!plugin.cooldowns.containsKey(PoisonUUID)) {
			             		player.sendMessage(ChatColor.GREEN + "[2 / 2]" + ChatColor.BLUE + " You ready your " + ChatColor.GOLD + "Poison Shot");
			             		ability = "PoisonShot";
		             		} else {
		             			player.sendMessage(ChatColor.RED + "Poison Arrow on cooldown, " + ChatColor.YELLOW + plugin.cooldowns.get(PoisonUUID) + " seconds" + ChatColor.RED + " left.");
		             		}
		             	}
					}
				}
			} else if (mainHand.equals(Material.FEATHER)) {	
				boolean isMarksman = (plugin.getConfig().getString("Users." + player.getUniqueId() + ".Class").contains("Marksman"));
				if (isMarksman && wearingLeather(player)) {
	    			String leapUUID = player.getUniqueId()+"Leap";
		    			
	    			if (!plugin.cooldowns.containsKey(leapUUID)) {
	        			plugin.masterCD = 10;
		        		plugin.cooldowns.put(leapUUID, plugin.masterCD);
		        		
		        		Vector dir = player.getEyeLocation().getDirection();
		        		Vector vec = new Vector(dir.getX() * 1.8D, 1.7D, dir.getZ() * 1.6D);
		        		
		    			player.sendMessage(ChatColor.BLUE + "You have used " + ChatColor.GOLD + "leap!");
		    			player.setVelocity(vec);
		    			plugin.getConfig().set("Users." + player.getUniqueId() + ".isLeaping", "true");
	    			} else {
	    				player.sendMessage(ChatColor.RED + "Leap on cooldown, " + ChatColor.YELLOW + plugin.cooldowns.get(leapUUID) + " seconds" + ChatColor.RED + " left.");
	    			}
				}
			}
		}
	}
	
}
