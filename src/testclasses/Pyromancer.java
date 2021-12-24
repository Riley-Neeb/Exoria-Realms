package testclasses;

import java.util.Set;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import main.Main;
import modules.Vector3D;
import net.md_5.bungee.api.ChatColor;

public class Pyromancer implements Listener {
	
	public Main plugin = Main.getPlugin(Main.class);
	private int pyromancerAction = 0;
	
	public boolean wearingMage(Player player) {
		ItemStack Helm = player.getInventory().getHelmet();
		ItemStack Chest = player.getInventory().getChestplate();
		ItemStack Legs = player.getInventory().getLeggings();
		ItemStack Boots = player.getInventory().getBoots();
		
		if (Helm.getType().equals(Material.DIAMOND_HELMET)) {
			if (Chest.getType().equals(Material.LEATHER_CHESTPLATE)) {
				if (Legs.getType().equals(Material.LEATHER_LEGGINGS)) {
					if (Boots.getType().equals(Material.DIAMOND_BOOTS)) {
						return true;
					} 
				}
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
	
	
	//@EventHandler
    //public void walkEvent(PlayerMoveEvent event) {
        //Player player = event.getPlayer();
        
       // int radius = 6;
       // Block middle = event.getTo().getBlock();
       // for (int x = radius; x >= -radius; x--) {
          //  for (int y = radius; y >= -radius; y--) {
              //  for (int z = radius; z >= -radius; z--) {
                   // if (middle.getRelative(x, y, z) instanceof Block) {
                    	// ((Block) middle.getWorld()).getRelative(BlockFace.UP).setType(Material.FIRE);
                   // }
               // }
           // }
       // } 
   // }
	
	
	@EventHandler(priority=EventPriority.HIGH)
	private void mageInteract(PlayerInteractEvent event){
		if(event.getItem() == null) {
			return;
	    }
	
        if ((event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) && event.getHand() == EquipmentSlot.HAND) {  
        	Player player = event.getPlayer();
    		Material mainHand = player.getInventory().getItemInMainHand().getType();
    		boolean isPyromancer = (plugin.getConfig().getString("Users." + player.getUniqueId() + ".Class").contains("Pyromancer"));
			
			if (isPyromancer) {
		        if (wearingMage(player)) {   
		    		if (mainHand.equals(Material.BLAZE_ROD)) {
		    			pyromancerAction += 1;
			       		 
		       		 	if (pyromancerAction > 4) {
		       		 	pyromancerAction = 1;
		              	}
		       		 
		       			if (pyromancerAction == 1) {
		             		player.sendMessage(ChatColor.GREEN + "[1 / 4]" + ChatColor.BLUE + " You ready your " + ChatColor.GOLD + "Spell of Flame Tempest");
		             	} else if (pyromancerAction == 2) {
		             		player.sendMessage(ChatColor.GREEN + "[2 / 4]" + ChatColor.BLUE + " You ready your " + ChatColor.GOLD + "Spell of Fireball");
		             	} else if (pyromancerAction == 3) {
		             		player.sendMessage(ChatColor.GREEN + "[3 / 4]" + ChatColor.BLUE + " You ready your " + ChatColor.GOLD + "Spell of Combustion");	
		             	} else if (pyromancerAction == 4) {
		             		player.sendMessage(ChatColor.GREEN + "[4 / 4]" + ChatColor.BLUE + " You ready your " + ChatColor.GOLD + "Spell of Amplification");	
		             	}
		             	
		    		}	
				}
	    	}
	    	
	    } else if ((event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) && event.getHand() == EquipmentSlot.HAND) {
	    	Player player = event.getPlayer();
	    	Material mainHand = player.getInventory().getItemInMainHand().getType();
	    	if (mainHand.equals(Material.BLAZE_ROD)) {
	    		if (pyromancerAction == 1) {
	    			//Flame Tempest
	    			String FlameTempest1UUID = player.getUniqueId()+"Flame Tempest";
	    			if (!plugin.cooldowns.containsKey(FlameTempest1UUID)) {
			        	plugin.masterCD = 15;
		        		plugin.cooldowns.put(FlameTempest1UUID, plugin.masterCD);
		        		
		    			new BukkitRunnable() {
		        			double t = Math.PI/4;
		        			Location loc = player.getLocation();
		        			
		        			@Override
		        			public void run() {
		        				t = t + 0.1*Math.PI;
		        				
		        				for (double theta = 0; theta <= 2*Math.PI; theta = theta + Math.PI/32) {
		        					double x = t*Math.cos(theta);
		        					double y = Math.exp(-0.1*t) * Math.sin(t) + 1.5;
		        					double z = t*Math.sin(theta);
		        					loc.add(x,y,z);
		        					player.getWorld().spawnParticle(Particle.LAVA, loc, 0, 0, 0, 0);
		        					loc.subtract(x,y,z);
		        					
		        					theta = theta + Math.PI/64;
		        					
		        					x = t*Math.cos(theta);
		        					y = 2*Math.exp(-0.1*t) * Math.sin(t) + 1.5;
		        					z = t*Math.sin(theta);
		        					loc.add(x,y,z);
		        					player.getWorld().spawnParticle(Particle.FLAME, loc, 0, 0, 0, 0);
		        					loc.subtract(x,y,z);
	
		        			
		        					if (t > 10) {
		        						this.cancel();
		        					}
		        				}
		        			}
	        			
	        		    }.runTaskTimerAsynchronously(plugin, 0, 1);
					
						for(Entity getEntity : player.getNearbyEntities(10, 10, 10)){
							if (getEntity instanceof LivingEntity) {
								
				        		String AmplificationUUID = player.getUniqueId()+"Amplification";
				        		int ampLeft = plugin.cooldowns.get(AmplificationUUID);
							        		
				        		if (ampLeft > 30) {
				        			((LivingEntity) getEntity).setFireTicks(200);
				        		} else {
				        			((LivingEntity) getEntity).setFireTicks(100);
				        		}
				        		//player.playSound(player.getLocation(), Sound.ENTITY_RAVAGER_DEATH, 2F, 1F);
				        		
				        		player.sendMessage(ChatColor.BLUE + "You casted " + ChatColor.GOLD + "Combust" + ChatColor.BLUE + "on " + ChatColor.GOLD + getEntity.getName());
				        	}
				        }
	    			} else {
	    				player.sendMessage(ChatColor.BLUE + "[Spell] Flame Tempest" + ChatColor.RED + " on cooldown, " + ChatColor.YELLOW + plugin.cooldowns.get(FlameTempest1UUID) + " seconds" + ChatColor.RED + " left.");
	    			}
	    		} else if (pyromancerAction == 2) {
	    			//Fireball
	    			if (player.getInventory().containsAtLeast(new ItemStack(Material.REDSTONE), 2) && (player.getInventory().containsAtLeast(new ItemStack(Material.CHARCOAL), 2))) {
			  			player.getInventory().removeItem(new ItemStack(Material.REDSTONE, 2));
			  			player.getInventory().removeItem(new ItemStack(Material.CHARCOAL, 2));
			  			
			  			String FireballUUID = player.getUniqueId()+"Fireball";
			  			if (!plugin.cooldowns.containsKey(FireballUUID)) {
				        	plugin.masterCD = 15;
			        		plugin.cooldowns.put(FireballUUID, plugin.masterCD);
			        		
			        		Location eye = player.getEyeLocation();
			        		Location loc = eye.add(eye.getDirection().multiply(1.2));
			        		
			        		String AmplificationUUID = player.getUniqueId()+"Amplification";
			        		int ampLeft = plugin.cooldowns.get(AmplificationUUID);
			        		
			        		if (ampLeft > 30) {
			        			Fireball fireball = (Fireball) loc.getWorld().spawnEntity(loc.add(loc.getDirection()), EntityType.FIREBALL);
				        	    fireball.setVelocity(loc.getDirection().multiply(4));
				        	    fireball.setIsIncendiary(false);
				        	    fireball.setMetadata("AmplifiedFireball", new FixedMetadataValue(plugin, true));
				        	    fireball.setShooter(player);
				        	   
			        		} else {
			        			Fireball fireball = (Fireball) loc.getWorld().spawnEntity(loc.add(loc.getDirection()), EntityType.FIREBALL);
				        	    fireball.setVelocity(loc.getDirection().multiply(2));
				        	    fireball.setIsIncendiary(false);
				        	    fireball.setMetadata("Fireball", new FixedMetadataValue(plugin, true));
				        	    fireball.setShooter(player);
				        	    
			        		}
			        		
			        		
			  			} else {
			        		player.sendMessage(ChatColor.BLUE + "[Spell] Fireball" + ChatColor.RED + " on cooldown, " + ChatColor.YELLOW + plugin.cooldowns.get(FireballUUID) + " seconds" + ChatColor.RED + " left.");
			        	}
			  			
	    			} else {
	    				player.sendMessage(ChatColor.RED + "Not enough " + ChatColor.GOLD + "Charcoal or Redstone!");
	    			}
	    		} else if (pyromancerAction == 3) {
	    			//Combustion
	    			if (player.getInventory().containsAtLeast(new ItemStack(Material.REDSTONE), 1) && (player.getInventory().containsAtLeast(new ItemStack(Material.CHARCOAL), 1))) {
			  			player.getInventory().removeItem(new ItemStack(Material.REDSTONE, 1));
			  			player.getInventory().removeItem(new ItemStack(Material.CHARCOAL, 1));
			  			

			  			Location position = player.getEyeLocation();
						Vector3D direction = new Vector3D(position.getDirection());
						Vector3D start = new Vector3D(position);
						Vector3D end =  start.add(direction.multiply(30));
					
						Block targetBlock = player.getTargetBlock((Set<Material>) null, 30);
						
						for(Entity getEntity : player.getNearbyEntities(30, 30, 30)){
							if (getEntity instanceof LivingEntity) {
		    					Vector3D targetPos = new Vector3D(getEntity.getLocation());
		                        Vector3D minimum = targetPos.add(-0.5, 0, -0.5);
		                        Vector3D maximum = targetPos.add(0.5, 1.67, 0.5);
		    					if (hasIntersection(start, end, minimum, maximum)) {
		    						if (targetBlock.getType().equals(Material.AIR)) {
		    							if (player.hasLineOfSight(getEntity)) {
		    								
			        			        	String Combustion3UUID = player.getUniqueId()+"Combustion";
			        			        	if (!plugin.cooldowns.containsKey(Combustion3UUID)) {
				        			        	plugin.masterCD = 15;
								        		plugin.cooldowns.put(Combustion3UUID, plugin.masterCD);
								        		
								        		String AmplificationUUID = player.getUniqueId()+"Amplification";
								        		int ampLeft = plugin.cooldowns.get(AmplificationUUID);
								        		if (ampLeft > 30) {
								        			((LivingEntity) getEntity).setFireTicks(200);
								        			((LivingEntity) getEntity).damage(2);
								        		} else {
								        			((LivingEntity) getEntity).setFireTicks(100);
								        		}
								        		//player.playSound(player.getLocation(), Sound.ENTITY_RAVAGER_DEATH, 2F, 1F);
								        		
								        		player.sendMessage(ChatColor.BLUE + "You casted " + ChatColor.GOLD + "Combustion" + ChatColor.BLUE + "on " + ChatColor.GOLD + getEntity.getName());
			        			        	} else {
			        			        		player.sendMessage(ChatColor.BLUE + "[Spell] Combustion" + ChatColor.RED + " on cooldown, " + ChatColor.YELLOW + plugin.cooldowns.get(Combustion3UUID) + " seconds" + ChatColor.RED + " left.");
			        			        	}
							        		
			        			        }
		    						}
		    					}
	        			    }
	        			}
	    			} else {
	    				player.sendMessage(ChatColor.RED + "Not enough " + ChatColor.GOLD + "Charcoal or Redstone!");
	    			}
	    		} else if (pyromancerAction == 4) {
	    			//Amplification
	    			if (player.getInventory().containsAtLeast(new ItemStack(Material.REDSTONE), 10)) {
			  			player.getInventory().removeItem(new ItemStack(Material.REDSTONE, 10));
			  			
						String AmplificationUUID = player.getUniqueId()+"Amplification";
			        	if (!plugin.cooldowns.containsKey(AmplificationUUID)) {
    			        	plugin.masterCD = 40;
			        		plugin.cooldowns.put(AmplificationUUID, plugin.masterCD);
			        		
			        		int ampLeft = plugin.cooldowns.get(AmplificationUUID);
			        		
			        		if (ampLeft > 30) {	
				        		new BukkitRunnable() {
				        			double a = 0;
				        			
				        			@Override
				        			public void run() {
				        				a += Math.PI / 16;
				        				Location loc = player.getLocation();
				        				Location first = loc.clone().add( Math.cos(a), Math.sin(a) + 1, Math.sin(a));
				        				Location second = loc.clone().add( Math.cos(a + Math.PI), Math.sin(a) + 1, Math.sin(a + Math.PI));
				        				
				        				player.getWorld().spawnParticle(Particle.DRAGON_BREATH, first, 0, 0, 0, 0, 0);
				        				player.getWorld().spawnParticle(Particle.DRAGON_BREATH, second, 0, 0, 0, 0, 0);	
				        			}
				        			
				        		}.runTaskTimerAsynchronously(plugin, 0, 1);
			        		}
			        		
			        		player.damage(2);
			        		player.sendMessage(ChatColor.BLUE + "You casted " + ChatColor.GOLD + "Amplification");
			        	} else {
			        		player.sendMessage(ChatColor.BLUE + "[Spell] Amplification" + ChatColor.RED + " on cooldown, " + ChatColor.YELLOW + plugin.cooldowns.get(AmplificationUUID) + " seconds" + ChatColor.RED + " left.");
			        	}
	
	    			}
	    		}	
	    		
	    	}
	    }
	}
	
	
}
