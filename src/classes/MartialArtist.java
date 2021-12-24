package classes;

import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import main.Main;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;

public class MartialArtist implements Listener {

public static Main plugin = Main.getPlugin(Main.class);
	
	private int martialAction = 1;
	private String ability;
	
	public boolean wearingNothing(Player player) {
		boolean isClass = (plugin.getConfig().getString("Users." + player.getUniqueId() + ".Class").contains("MartialArtist"));
		
		if (isClass) {
			TextComponent failMessage = new TextComponent("Your can't wear armor!");
			failMessage.setColor(ChatColor.DARK_RED);
			failMessage.setBold(true);
			 
			ItemStack Helm = player.getInventory().getHelmet();
			ItemStack Chest = player.getInventory().getChestplate();
			ItemStack Legs = player.getInventory().getLeggings();
			ItemStack Boots = player.getInventory().getBoots();
			
			if (Helm == null && Chest == null && Legs == null && Boots == null) {
				return true;
			} else {
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
	
	
	@EventHandler(priority=EventPriority.HIGH)
	private void onMove(PlayerMoveEvent event){
		Player player = event.getPlayer();
		boolean isMartialArtist = (plugin.getConfig().getString("Users." + player.getUniqueId() + ".Class").contains("MartialArtist"));
		
		if (isMartialArtist) {
	        player.setWalkSpeed(0.25F);
	    }
	}
	
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event){  
		Player player = event.getPlayer();
		boolean isMartialArtist = (plugin.getConfig().getString("Users." + player.getUniqueId() + ".Class").contains("MartialArtist"));
		
		if (isMartialArtist) {
			if (player.getInventory().getItemInMainHand().getType().equals(Material.BAMBOO)) {
				event.setCancelled(true);
			}
		}
		
	}
	
	@EventHandler(priority=EventPriority.HIGH)
	private void onPlayerUse(PlayerInteractEvent event) {

		if(event.getItem() == null) {
			return;
	    }
		
		 if ((event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) && event.getHand() == EquipmentSlot.HAND) {
			 Player player = event.getPlayer();
			 boolean isMartialArtist = (plugin.getConfig().getString("Users." + player.getUniqueId() + ".Class").contains("MartialArtist"));
			 if (isMartialArtist) {
		        if (wearingNothing(player)) {   
		        	Material mainHand = player.getInventory().getItemInMainHand().getType();
		        	if (mainHand.equals(Material.STICK)) {

		        		martialAction += 1;
			       		 
		       		 	if (martialAction > 4) {
		       		 		martialAction = 1;
		              	}
		       		 
		       			if (martialAction == 1) {
		       				String DragonkickUUID = player.getUniqueId()+"DragonKick";
		       				
		       				if (!plugin.cooldowns.containsKey(DragonkickUUID)) {
		       					player.sendMessage(ChatColor.GREEN + "[1 / 4]" + ChatColor.BLUE + " You ready your" + ChatColor.GOLD + " Dragon Kick");
			             		player.playSound(player.getLocation(), Sound.ENTITY_ITEM_BREAK, 2F, 1F);
			             		ability = "Dragon Kick";
		    				} else {
		    					player.sendMessage(ChatColor.RED + "[Dragon Kick] on cooldown, " + ChatColor.YELLOW + plugin.cooldowns.get(DragonkickUUID) + " seconds" + ChatColor.RED + " left.");
		    				}
		             		
		             		
		       			} else if (martialAction == 2) {
		       				String DragonPunchUUID = player.getUniqueId()+"DragonPunch";
		       				
		       				if (!plugin.cooldowns.containsKey(DragonPunchUUID)) {
		       					player.sendMessage(ChatColor.GREEN + "[2 / 4]" + ChatColor.BLUE + " You ready your" + ChatColor.GOLD + " Dragon Punch");
			             		player.playSound(player.getLocation(), Sound.ENTITY_ITEM_BREAK, 2F, 1F);
			             		ability = "Dragon Punch";
		       				} else {
		    					player.sendMessage(ChatColor.RED + "[Dragon Punch] on cooldown, " + ChatColor.YELLOW + plugin.cooldowns.get(DragonPunchUUID) + " seconds" + ChatColor.RED + " left.");
		    				}
		       				
		             		
		       			} else if (martialAction == 3) {
		       				String UppercutUUID = player.getUniqueId()+"Uppercut";
		       				
		       				if (!plugin.cooldowns.containsKey(UppercutUUID)) {
			       				player.sendMessage(ChatColor.GREEN + "[3 / 4]" + ChatColor.BLUE + " You ready your" + ChatColor.GOLD + " Uppercut");
			             		player.playSound(player.getLocation(), Sound.ENTITY_ITEM_BREAK, 2F, 1F);
			             		ability = "Uppercut";
		       				} else {
		    					player.sendMessage(ChatColor.RED + "[Uppercut] on cooldown, " + ChatColor.YELLOW + plugin.cooldowns.get(UppercutUUID) + " seconds" + ChatColor.RED + " left.");
		    				}
		             		
		       			} else if (martialAction == 4) {
		       				String HiddenLotusUUID = player.getUniqueId()+"HiddenLotus";
		       				
		       				if (!plugin.cooldowns.containsKey(HiddenLotusUUID)) {
			       				player.sendMessage(ChatColor.GREEN + "[4 / 4]" + ChatColor.BLUE + " You ready your" + ChatColor.GOLD + " Hidden Lotus");
			             		player.playSound(player.getLocation(), Sound.ENTITY_ITEM_BREAK, 2F, 1F);
			             		ability = "Hidden Lotus";
		       				} else {
		       					player.sendMessage(ChatColor.RED + "[Hidden Lotus] on cooldown, " + ChatColor.YELLOW + plugin.cooldowns.get(HiddenLotusUUID) + " seconds" + ChatColor.RED + " left.");
		       				}
		       				
		       			}
		        	}
		        }
			 }
		 }
	}
	
	@EventHandler
	public void onDamage(EntityDamageEvent event) {
		if (event.getCause() == DamageCause.FALL){
			Player player = (Player) event.getEntity();
			if (wearingNothing(player)) {
				if (plugin.getConfig().getString("Users." + player.getUniqueId() + ".Class").contains("MartialArtist")) {
					int rollRandom = (int)(Math.random() * 99 + 1);
					int evadeRandom = (int)(Math.random() * 99 + 1);
					
					if (rollRandom <= 20) {
						event.setCancelled(true);
					}
					if (evadeRandom <= 33) {
						event.setCancelled(true);
					}
				}
			}
		 }
	}
	
	@EventHandler
	private void onEntityHit(EntityDamageByEntityEvent event) {

		if (event.getEntity() instanceof Player) { //if the attacked is a player		
			Player Damager = (Player) event.getDamager();
			Player Damaged = (Player) event.getEntity();
			
			boolean isDamagerMartialArtist = (plugin.getConfig().getString("Users." + Damager.getUniqueId() + ".Class").contains("MartialArtist"));
			boolean isDamagedMartialArtist = (plugin.getConfig().getString("Users." + Damaged.getUniqueId() + ".Class").contains("MartialArtist"));
    		
			if (isDamagerMartialArtist) {
				if (wearingNothing(Damager)) { 
					Material mainHand = Damager.getInventory().getItemInMainHand().getType();
					if (mainHand.equals(Material.STICK)) {
						
						if (ability == "None") {
							event.setDamage(event.getDamage()*2);
						}

						String DragonkickUUID = Damager.getUniqueId()+"DragonKick";
	       				String DragonPunchUUID = Damager.getUniqueId()+"DragonPunch";
	       				String UppercutUUID = Damager.getUniqueId()+"Uppercut";
	       				String HiddenLotusUUID = Damager.getUniqueId()+"HiddenLotus";
	       				
						if (ability == "Dragon Kick") {
							if (isSameParty(Damager, Damaged)) {
								  
							} else {
								for (double z = 0; z <= 5; z += 0.01) {
									double adjustX = 3 * Math.cos(z);
									double adjustY = 3 * Math.sin(z);
									
									event.getDamager().getWorld().spawnParticle(Particle.FLAME, Damager.getLocation(), (int) adjustX, (int) adjustY, (int) z, 0, 0);
									event.getDamager().getWorld().spawnParticle(Particle.LAVA, Damager.getLocation(), (int) adjustX, (int) adjustY, (int) z, 0, 0);
								}
								
								Vector dir = Damager.getEyeLocation().getDirection();
								Vector vec = new Vector(dir.getX() * 1.8D, 0.2D, dir.getZ() * 1.8D);
								
								Vector dir2 = Damager.getEyeLocation().getDirection();
								Vector vec2 = new Vector(dir2.getX() * 1.8D, 0D, dir2.getZ() * 1.8D);
								
								Damaged.setVelocity(vec);
								Damaged.setFireTicks(40);
								Damaged.damage(event.getDamage()+2);
								
								event.getDamager().setVelocity(vec2);
								
								ability = "None";
								plugin.masterCD = 3;
				        		plugin.cooldowns.put(DragonkickUUID, plugin.masterCD);
							}
							
							
						} else if (ability == "Dragon Punch") {
							if (isSameParty(Damager, Damaged)) {
								  
							} else {
								Damaged.setFireTicks(40);
								Damaged.damage(event.getDamage()+2);
								
								ability = "None";
								plugin.masterCD = 3;
				        		plugin.cooldowns.put(DragonPunchUUID, plugin.masterCD);
							}
							
			        		
						} else if (ability == "Uppercut") {
							if (isSameParty(Damager, Damaged)) {
								  
							} else {
								Vector vec = new Vector(0, .65, 0);
								Damaged.setVelocity(vec);
								Damaged.damage(event.getDamage()+3);
								
								ability = "None";
								plugin.masterCD = 3;
				        		plugin.cooldowns.put(UppercutUUID, plugin.masterCD);
							}
							
			        		
						} else if (ability == "Hidden Lotus") {
							if (isSameParty(Damager, Damaged)) {
								  
							} else {
								Vector vec = new Vector(0, 1.35, 0);
								
								Damaged.setVelocity(vec);
								Damager.setVelocity(vec);
								Damaged.damage(event.getDamage()+5);
								Damaged.setFireTicks(40);
								
								ability = "None";
								plugin.getConfig().set("Users." + Damager.getUniqueId() + ".isLeaping", "true");
								plugin.masterCD = 10;
				        		plugin.cooldowns.put(HiddenLotusUUID, plugin.masterCD);	
							}
							
						}
							
					}
				} else if (isDamagedMartialArtist) {
					int ironSkin = (int)(Math.random() * 99 + 1);
					
					if (wearingNothing(Damaged)) {
						if (ironSkin <= 25) {
							event.setDamage(event.getDamage()/12);
							Damaged.sendMessage(ChatColor.GREEN+"Iron Skin!");
						} else {
							event.setDamage(event.getDamage()/6);
						}
					}
				}
			}
		}
	}
	
}
