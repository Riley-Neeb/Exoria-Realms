package classes;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Ageable;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Farmland;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import main.Main;
import net.md_5.bungee.api.ChatColor;

public class Farmer implements Listener {

	public Main plugin = Main.getPlugin(Main.class);
	
	//OVERALL
	private int maxMultiplier = 32;
	private int multiplierBias = 8;
	private int waterRange = 25;
	private int replantRange = 25;
	private int yieldRange = 25;
	private int growRange = 25;
	
	private int instantGrowChance = 5;
	private int multiplierReset = 25;
	//items
	private Material seedType;
	private int farmerAction = 1;
	
	//TODO
	//CHECK IF BLOCK IS CLAIMED BEFORE DOING ANYTHING
	
	public boolean blockSolid(Location location) {
		
		if (location.getBlock().getType().isSolid() == false) {
		     return true;
		}
		
		if (location.getBlock().getType().equals(Material.GRASS)) {
		     return true;
		}
		
		if (location.getBlock().getType().equals(Material.TALL_GRASS)) {
		     return true;
		}
		
		return false;	
	}

	public boolean isFullyGrown(Block block) {      
		if(block.getBlockData() instanceof Ageable) {
			Ageable crop = (Ageable) block.getBlockData();
            if (crop.getMaximumAge() == crop.getAge()) {
            	return true;
            }
        }
        
        return false;
   }
	
	public boolean isACrop(Block block) {      
		BlockData bdata = block.getBlockData();
        if(bdata instanceof Ageable){
        	return true;
        }
        
        return false;
	}
	
	public Material getCrop(Block block) {
		if(block.getBlockData() instanceof Ageable) {
			return block.getType();
		}
		
		return null;
	}
	

	public float weightedChance(int smallest, int largest, int biasNumber) {
		float ran = (float) Math.random();
		float bias = (float) Math.pow(ran, biasNumber);
				
		return Math.round(smallest + (largest - smallest) * bias);
	}
	
	public boolean hasASeed(Player player) {
		if (player.getInventory().getItemInMainHand().getType().name().toLowerCase().contains("seeds")) {
			return true;
		}
		  
		return false;
	}
	
	public Material getMaterialByName(String name) {
        for (Material material : EnumSet.allOf(Material.class)) {
            if (material.name().replace("_", "").equalsIgnoreCase(name)) {
                return material;
            }
        }
     
        return null;
    }
	
	public Material getSeedInHand(Player player) {
        if (player.getInventory().getItemInMainHand().getType().name().toLowerCase().contains("seeds")) {
        	return player.getInventory().getItemInMainHand().getType();
		}
		
		return null;
	}
	
	public boolean isBlockAir(Block block) {
		
		if (block.getType().equals(Material.AIR)) {
		     return true;
		}
		
		return false;	
	}
	
	public void changeToBlock(Block block, Material type) {
		block.setType(type);
	}

	public boolean hasSpellItem(Player player) {
		 Material mainHand = player.getInventory().getItemInMainHand().getType();
		
		 if (mainHand.equals((Material.BONE))) {
			 return true;
		 }
		 
		 return false;
	}
	
	
	public boolean hasAHoe(Player player) {
		 Material mainHand = player.getInventory().getItemInMainHand().getType();
		
		 if (mainHand.equals((Material.WOODEN_HOE))) {
			 return true;
		 } else if (mainHand.equals((Material.STONE_HOE))) {
			 return true;
		 } else if (mainHand.equals((Material.GOLDEN_HOE))) {
			 return true;
		 } else if (mainHand.equals((Material.IRON_HOE))) {
			 return true;
		 } else if (mainHand.equals((Material.DIAMOND_HOE))) {
			 return true;
		 } else if (mainHand.equals((Material.NETHERITE_HOE))) {
			 return true;
		 }
		 
		 return false;
	}
	
	public static List<Block> getRegionBlocks(World world, Location startLoc, int Range) {
		List<Block> blocks = new ArrayList<Block>();
		
		int minX = (int) (startLoc.getX()-Range);
		int maxX = (int) (startLoc.getX()+Range);
		
		int minY = (int) (startLoc.getY()-Range);
		int maxY = (int) (startLoc.getY()+Range);
		
		int minZ = (int) (startLoc.getZ()-Range);
		int maxZ = (int) (startLoc.getZ()+Range);
		
		for (int x = minX; x <= maxX; x++) {
		    for (int y = minY; y <= maxY; y++) {
		        for (int z = minZ; z <= maxZ; z++) {
		        	Location loc = new Location(world, x, y, z);
					blocks.add(loc.getBlock());
		        }
		    }
		}
		
		return blocks;
	}
	
	@EventHandler
	public void moveEvent(PlayerMoveEvent event) {
       Player player = event.getPlayer();
       boolean isFarmer = (plugin.getConfig().getString("Users." + player.getUniqueId() + ".Class").contains("Farmer"));
       
       if (isFarmer) {
           Block block = player.getLocation().subtract(player.getLocation().getX(), player.getLocation().getY()-2, player.getLocation().getZ()).getBlock();
           
           if (block.getType() == Material.FARMLAND) {             
               block.setType(Material.FARMLAND);
           }
		}
	}
	
	@EventHandler
	private void onBlockBreak(BlockBreakEvent event){
		Player player = event.getPlayer();
		boolean isFarmer = (plugin.getConfig().getString("Users." + player.getUniqueId() + ".Class").contains("Farmer"));
		
		if (isFarmer) { 
			if (hasSpellItem(player)) {
				event.setCancelled(true);
			}
         	if (hasAHoe(player)) {
        		if (isACrop(event.getBlock())) {
        			String MultiChanceUUID = player.getUniqueId()+"MultiplyChance";
        			int resetChance = (int)(Math.random() * 99 + 1);
        			int multiplyChance = (int)(Math.random() * 99 + 1);
        			
	        		if (!plugin.cooldowns.containsKey(MultiChanceUUID)) {	
	        			if (resetChance <= multiplierReset) {
			        		if (isFullyGrown(event.getBlock())) {
	        					int amount = (int) weightedChance(1, maxMultiplier, multiplierBias);
		            			
	        					if (amount > 1) {
	        						World world = event.getBlock().getWorld();
	        						Location blockLoc = event.getBlock().getLocation();
	        						
	        						world.dropItem(blockLoc, new ItemStack(event.getBlock().getDrops().iterator().next().getType(), amount));
			            			player.sendMessage(ChatColor.GREEN+"Congratulations! You got a "+ChatColor.GOLD+amount+"X"+ChatColor.GREEN+" yield!");	
			            			player.sendMessage(ChatColor.RED+"You were unlucky and got a cooldown!");
			            			plugin.masterCD = 5;
					        		plugin.cooldowns.put(MultiChanceUUID, plugin.masterCD);
	        					}
	        				}
	        			} else if (resetChance > multiplierReset) {
	        				if (isFullyGrown(event.getBlock())) {
	        					int amount = (int) weightedChance(1, maxMultiplier, multiplierBias);
		            			
	        					if (amount > 1) {
	        						World world = event.getBlock().getWorld();
	        						Location blockLoc = event.getBlock().getLocation();
	        						
	        						world.dropItem(blockLoc, new ItemStack(event.getBlock().getDrops().iterator().next().getType(), amount));
			            			player.sendMessage(ChatColor.GREEN+"Congratulations! You got a "+ChatColor.GOLD+amount+"X"+ChatColor.GREEN+" yield!");	
	        					}
	        				}
	        			}
	        		} else {
	        			player.sendMessage(ChatColor.RED+"You're still on cooldown!");
	        		}
        		}
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
			boolean isFarmer = (plugin.getConfig().getString("Users." + player.getUniqueId() + ".Class").contains("Farmer"));
			
			if (isFarmer) { 
				if (hasASeed(player)) {
					Material getSeedType = getSeedInHand(player);
					
					seedType = getSeedType;
					player.sendMessage(ChatColor.GREEN+"Seed type is set to "+ChatColor.GOLD+seedType);
				} else if (mainHand.equals(Material.BONE)) {
	        		farmerAction += 1;
	       		 
	       		 	if (farmerAction > 4) {
	       		 		farmerAction = 1;
	              	}
	       		 
	       			if (farmerAction == 1) {
	             		//
	             		player.sendMessage(ChatColor.GREEN + "[1 / 4]" + ChatColor.GOLD + " You ready your " + ChatColor.BLUE + "Spell of Area Growth");
	             		player.playSound(player.getLocation(), Sound.ENTITY_SALMON_AMBIENT, 2F, 1F);
	             		
	             	} else if (farmerAction == 2) {
	             		//
	             		player.sendMessage(ChatColor.GREEN + "[2 / 4]" + ChatColor.GOLD + " You ready your " + ChatColor.BLUE + "Spell of Area Yield");
	             		player.playSound(player.getLocation(), Sound.ENTITY_SALMON_AMBIENT, 2F, 1F);
	             		
	             	} else if (farmerAction == 3) {
	             		//
	             		player.playSound(player.getLocation(), Sound.ENTITY_SALMON_AMBIENT, 2F, 1F);
	             		player.sendMessage(ChatColor.GREEN + "[3 / 4]" + ChatColor.GOLD + " You ready your " + ChatColor.BLUE + "Spell of Area Replant");
	             		
	             	} else if (farmerAction == 4) {
	             		//
	             		player.playSound(player.getLocation(), Sound.ENTITY_SALMON_AMBIENT, 2F, 1F);
	             		player.sendMessage(ChatColor.GREEN + "[4 / 4]" + ChatColor.GOLD + " You ready your " + ChatColor.BLUE + "Spell of Area Watering");
	             		
	             	}
	        	}
    		}
    	} else if ((event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) && event.getHand() == EquipmentSlot.HAND) {
    		Player player = event.getPlayer();
    		Material mainHand = player.getInventory().getItemInMainHand().getType();
			boolean isFarmer = (plugin.getConfig().getString("Users." + player.getUniqueId() + ".Class").contains("Farmer"));
			
			if (isFarmer) {
	        	if (mainHand.equals(Material.BONE)) {
	        		String FarmerUUID = player.getUniqueId()+"FarmerSpell";
	        		
	        		if (!plugin.cooldowns.containsKey(FarmerUUID)) {	
		        		if (farmerAction == 1) {
		             		//
		        			String FarmerSpellUUID = player.getUniqueId()+"Farmer InstantGrowth";
		    				
		    				if (!plugin.cooldowns.containsKey(FarmerSpellUUID)) {	
		    					player.sendMessage(ChatColor.YELLOW + "Spell of Instant Growth!");
			             		player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_WORK_FARMER, 2F, 1F);
			             		
			             		List<Block> blocks = getRegionBlocks(player.getWorld(), player.getLocation(), growRange);

			             		for (Block block : blocks) {
			             			if (block.getType().equals(Material.SUGAR_CANE)) {
			             				World world = block.getWorld();
			             				int X = block.getX();
			             				int Y = block.getY();
			             				int Z = block.getZ();
			             				
			             				Block baseBlock = world.getBlockAt(new Location(world, X, Y-1, Z));
			             				
			             				if (baseBlock.getType().equals(Material.GRASS_BLOCK) ||  baseBlock.getType().equals(Material.SAND)) {
			             					int chance4 = (int)(Math.random() * 99 + 1);
			             					
			             					Block baseAbove1 = world.getBlockAt(new Location(world, baseBlock.getX(), baseBlock.getY()+1, baseBlock.getZ()));
			             					Block baseAbove2 = world.getBlockAt(new Location(world, baseBlock.getX(), baseBlock.getY()+2, baseBlock.getZ()));
			             					Block baseAbove3 = world.getBlockAt(new Location(world, baseBlock.getX(), baseBlock.getY()+3, baseBlock.getZ()));
			             					Block baseAbove4 = world.getBlockAt(new Location(world, baseBlock.getX(), baseBlock.getY()+4, baseBlock.getZ()));
			             					
			             					if (isBlockAir(baseAbove1)) {
			             						changeToBlock(baseAbove1, Material.SUGAR_CANE);
			             					}
			             					if (isBlockAir(baseAbove2)) {
			             						changeToBlock(baseAbove2, Material.SUGAR_CANE);
			             					}
			             					if (isBlockAir(baseAbove3)) {
			             						changeToBlock(baseAbove3, Material.SUGAR_CANE);
			             					}
			             					if (isBlockAir(baseAbove4)) {
			             						if (chance4 <= 25) {
			             							changeToBlock(baseAbove4, Material.SUGAR_CANE);
			             						}
			             					}
			             				}
			             			} else if (block.getType().equals(Material.CACTUS)) {
			             				World world = block.getWorld();
			             				int X = block.getX();
			             				int Y = block.getY();
			             				int Z = block.getZ();
			             				
			             				Block baseBlock = world.getBlockAt(new Location(world, X, Y-1, Z));
			             				
			             				if (baseBlock.getType().equals(Material.GRASS_BLOCK) ||  baseBlock.getType().equals(Material.SAND)) {
			             					int chance4 = (int)(Math.random() * 99 + 1);
			             					
			             					Block baseAbove1 = world.getBlockAt(new Location(world, baseBlock.getX(), baseBlock.getY()+1, baseBlock.getZ()));
			             					Block baseAbove2 = world.getBlockAt(new Location(world, baseBlock.getX(), baseBlock.getY()+2, baseBlock.getZ()));
			             					Block baseAbove3 = world.getBlockAt(new Location(world, baseBlock.getX(), baseBlock.getY()+3, baseBlock.getZ()));
			             					Block baseAbove4 = world.getBlockAt(new Location(world, baseBlock.getX(), baseBlock.getY()+4, baseBlock.getZ()));
			             					
			             					if (isBlockAir(baseAbove1)) {
			             						changeToBlock(baseAbove1, Material.CACTUS);
			             					}
			             					if (isBlockAir(baseAbove2)) {
			             						changeToBlock(baseAbove2, Material.CACTUS);
			             					}
			             					if (isBlockAir(baseAbove3)) {
			             						changeToBlock(baseAbove3, Material.CACTUS);
			             					}
			             					if (isBlockAir(baseAbove4)) {
			             						if (chance4 <= 25) {
			             							changeToBlock(baseAbove4, Material.CACTUS);
			             						}
			             					}
			             				}
			             			}
			             			
			             			if (isACrop(block)) {
			             				Ageable crop = (Ageable) block.getBlockData();
			             				int maxAge = crop.getMaximumAge();
		             					
		             					Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable(){
					             			public void run(){
					             				crop.setAge(maxAge);
					             				block.setBlockData(crop);
					             			}
					             		}, 0);
			             			}
			             		}
		    				}
		    				
		             	} else if (farmerAction == 2) {
		             		//
		             		String FarmerSpellUUID = player.getUniqueId()+"Farmer Yielding";
		    				
		    				if (!plugin.cooldowns.containsKey(FarmerSpellUUID)) {	

		    				}
		    				
		             		player.sendMessage(ChatColor.YELLOW + "Spell of Yielding");
		             		player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_WORK_FARMER, 2F, 1F);
		             		
		             		List<Block> blocks = getRegionBlocks(player.getWorld(), player.getLocation(), yieldRange);

		             		for (Block block : blocks) {
		             			if (isACrop(block)) {
		             				if (isFullyGrown(block)) {
			             				Ageable crop = (Ageable) block.getBlockData();
			             				Material cropType = block.getType();
			             				ItemStack cropItem = new ItemStack(cropType, 1);
			             				
			             				//player.sendMessage(cropType.toString());
			             				Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable(){
		             						public void run(){
		             							if (cropType.toString().equals("BEETROOTS")) {
		    			             				ItemStack cropItem2 = new ItemStack(Material.BEETROOT, 1);
		    			             				
		             								player.getInventory().addItem(cropItem2);
							             			block.setType(Material.AIR);	
					             				} else {
					             					player.getInventory().addItem(cropItem);
							             			block.setType(Material.AIR);	
					             				}
		             							
					             			}
					             		}, 0);
		             				}
		             			} else {
		             				World world = block.getWorld();
		             				int X = block.getX();
		             				int Y = block.getY();
		             				int Z = block.getZ();
		             				
		             				Block blockUnder = world.getBlockAt(new Location(world, X, Y-1, Z));
		             				
		             				Material cropType = block.getType();
		             				ItemStack cropItem = new ItemStack(cropType, 1);
		             				
		             				if (block.getType().equals(Material.MELON) || block.getType().equals(Material.PUMPKIN)) {
		             					
		             					Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable(){
		             						public void run(){
		             							player.getInventory().addItem(cropItem);
		    		             				
		         								block.setType(Material.AIR);
		         								blockUnder.setType(Material.FARMLAND);
					             			}
					             		}, 0);
         							}
		             			}
		             		}
		             		
		             	} else if (farmerAction == 3) {
		             		String abilityUUID = player.getUniqueId()+"Replant";
    		    			
	    	    			if (!plugin.cooldowns.containsKey(abilityUUID)) {
	    	        			plugin.masterCD = 1800;
	    		        		plugin.cooldowns.put(abilityUUID, plugin.masterCD);
			             		player.sendMessage(ChatColor.YELLOW + "Spell of Area Replanting");
			             		player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_WORK_FARMER, 2F, 1F);
			             		
			             		List<Block> blocks = getRegionBlocks(player.getWorld(), player.getLocation(), replantRange);
			             		
			    	    		
			    		        	
			             		for (Block block : blocks) {
			             			if (block.getType().equals(Material.FARMLAND)) {
			             				World world = block.getWorld();
			             				int X = block.getX();
			             				int Y = block.getY();
			             				int Z = block.getZ();
	
			             				Block blockAboveFarmland = world.getBlockAt(new Location(world, X, Y+1, Z));
			             				
			             				if (seedType != null) {
			             					String seedName = seedType.toString();
		             						String[] splitSeed = seedName.split("_");
		             						String blockName = splitSeed[0];
		             						
		             						//player.sendMessage(blockName);
			             					Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable(){
			             						public void run(){
		         									//block.setType(Material.FARMLAND);
			             							if (blockAboveFarmland.getType().equals(Material.AIR)) {
			             								if (blockName.equals("BEETROOT")) {
				             								blockAboveFarmland.setType(Material.BEETROOTS);	
				             							} else {
					             							blockAboveFarmland.setType(getMaterialByName(blockName));	
				             							}	
			             							}
						             			}
						             		}, 0);
			             				} else {
		             						player.sendMessage(ChatColor.RED+"Seed type is nil! Right click with a seed type!");
		             					}	
			             			}
			             		}
	    	    			}
		             	} else if (farmerAction == 4) {
		             		player.sendMessage(ChatColor.YELLOW + "Spell of Area Tilling");
		             		player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_WORK_FARMER, 2F, 1F);
		             		
		             		List<Block> blocks = getRegionBlocks(player.getWorld(), player.getLocation(), waterRange);
		             		
		             		for (Block block : blocks) {
		             			if (block.getType().equals(Material.FARMLAND)) {
		             				World world = block.getWorld();
		             				int X = block.getX();
		             				int Y = block.getY();
		             				int Z = block.getZ();
		             				
		             				Farmland BD = (Farmland) block.getBlockData();
		             				
		             				Block blockAboveFarmland = world.getBlockAt(new Location(world, X, Y+1, Z));
		             				
		             				Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable(){
	             						public void run(){
             								BD.setMoisture(BD.getMaximumMoisture());
             								block.setBlockData(BD);
				             			}
				             		}, 0);
		             			
		             			} else if (block.getType().equals(Material.GRASS_BLOCK)) {
		             				World world = block.getWorld();
		             				int X = block.getX();
		             				int Y = block.getY();
		             				int Z = block.getZ();
		             				
		             				Block blockAboveLand = block.getWorld().getBlockAt(new Location(world, X, Y+1, Z));
		             				
		             				Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable(){
	             						public void run(){
         									block.setType(Material.FARMLAND);
         									blockAboveLand.setType(Material.AIR);
				             			}
				             		}, 0);
		             				
		             			} else if (block.getType().equals(Material.DIRT)) {
		             				World world = block.getWorld();
		             				int X = block.getX();
		             				int Y = block.getY();
		             				int Z = block.getZ();
		             				
		             				Block blockAboveLand = block.getWorld().getBlockAt(new Location(world, X, Y+1, Z));
		             				
		             				Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable(){
	             						public void run(){
	             							if (block.getType().equals(Material.DIRT)) {
	             								Block blockAboveDirt = block.getWorld().getBlockAt(new Location(world, block.getX(), block.getY()+1, block.getZ()));
	             								
	             								if (blockAboveDirt.getType().equals(Material.AIR)) {
	             									block.setType(Material.FARMLAND);
	             								}
	             							}
				             			}
				             		}, 0);
		             			}
		             			
		             		}
		             	}
	        		}
	        	}
			}
    	}
	}
	
	
}