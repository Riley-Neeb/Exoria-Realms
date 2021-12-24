package main;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import classes.Assassin;
import classes.Bard;
import classes.Berserker;
import classes.ChaosCrusader;
import classes.Gatherer;
import classes.Gladiator;
import classes.Marksman;
import classes.MartialArtist;
import classes.Scout;
import classes.Shaman;
//import classes.DeathKnight;
import classes.Farmer;
import net.md_5.bungee.api.ChatColor;

public final class Main extends JavaPlugin implements Listener {
	
	private static Plugin plugin;
	public HashMap<String, Integer> cooldowns = new HashMap<String, Integer>();
	public HashMap<String, Boolean> invisList = new HashMap<String, Boolean>();
	public HashMap<String[], String> parties = new HashMap<String[], String>();
	public HashMap<String, Integer> outgoingInvites = new HashMap<String, Integer>();
	
	public int masterCD;
	
	@Override
	public void onEnable() {
		getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "\n\nExoria Realms has Initialized");
		getServer().getPluginManager().registerEvents(new EventsClass(), this);
		//CLASSES
		getServer().getPluginManager().registerEvents(new Berserker(), this); //Finished.
		getServer().getPluginManager().registerEvents(new Marksman(), this); //Finished.
		getServer().getPluginManager().registerEvents(new Bard(), this); //Finished.
		getServer().getPluginManager().registerEvents(new Gladiator(), this); //Finished.
		getServer().getPluginManager().registerEvents(new Assassin(), this); //Finished.
		getServer().getPluginManager().registerEvents(new Shaman(), this);  //Finished. - may need to add more
		getServer().getPluginManager().registerEvents(new Scout(), this); //Finished.
		getServer().getPluginManager().registerEvents(new MartialArtist(), this); //Finished.
		getServer().getPluginManager().registerEvents(new ChaosCrusader(), this); //Finished.
		//getServer().getPluginManager().registerEvents(new DeathKnight(), this); //Finished.
		
		getServer().getPluginManager().registerEvents(new Gatherer(), this); //50%
		getServer().getPluginManager().registerEvents(new Farmer(), this); //50%
		//getServer().getPluginManager().registerEvents(new Enchanter(), this); //0%
		
		//COMMANDS
		this.getCommand("class").setExecutor(new Commands());
		this.getCommand("race").setExecutor(new Commands());
		this.getCommand("setclass").setExecutor(new Commands());
		this.getCommand("setrace").setExecutor(new Commands());
		this.getCommand("setconfig").setExecutor(new Commands());
		this.getCommand("heal").setExecutor(new Commands());
		this.getCommand("feed").setExecutor(new Commands());
		this.getCommand("cleanup").setExecutor(new Commands());
		this.getCommand("item").setExecutor(new Commands());
		
		this.getCommand("party").setExecutor(new PartySystem());
		//SETTINGS
		cooldowns.clear();
		invisList.clear();
		outgoingInvites.clear();
		plugin = this;
		checkRunnable();
		loadConfig();
		
		for (Player player : Bukkit.getServer().getOnlinePlayers()) {
			player.sendMessage(ChatColor.AQUA + "Server has been reloaded!");
			
			for (Player otherPlayers : Bukkit.getServer().getOnlinePlayers()) {
				if (player.isInvisible()) {
					otherPlayers.showPlayer(plugin, otherPlayers);
					player.sendMessage(ChatColor.YELLOW+"Rejoined invisible, set to visible.");
				}
			}
		}
	}
	
	@Override
	public void onDisable() {
		getServer().getConsoleSender().sendMessage(ChatColor.RED + "\n\nSilathar's Speedrun Gamemode has Uninitialized");
		loadConfig();
		reloadConfig();
	}
	
	public void loadConfig() {
		getConfig().options().copyDefaults(true);
		saveConfig();
	}
	
	public void checkRunnable() {
		
		new BukkitRunnable() {
			//COOLDOWNS SYSTEM
			@Override
			public void run() {
				if (cooldowns.isEmpty()) {
					return;
				}
				
				if (cooldowns.keySet() != null) {
					for (String uuid: cooldowns.keySet()) {
						int timeLeft = cooldowns.get(uuid);
						
						if (timeLeft < 1) {
							cooldowns.remove(uuid);
						} else {
							cooldowns.put(uuid, timeLeft - 1);
						}
					}
				}
			}
			
		}.runTaskTimer(this, 0, 20);
		
		new BukkitRunnable() {
			//COOLDOWNS SYSTEM
			@SuppressWarnings("deprecation")
			@Override
			public void run() {
				for (Player toHide : Bukkit.getServer().getOnlinePlayers()) {
					for (Player player : Bukkit.getServer().getOnlinePlayers()) {		
						//SNEAK/ASSASSIN
						String SneakUUID = toHide.getUniqueId()+"Sneak";
						
						if (cooldowns.containsKey(SneakUUID)) {
							int sneakLeft = cooldowns.get(SneakUUID);
							
							if (invisList.containsKey(SneakUUID)) {
								if (sneakLeft > 20) {
									player.hidePlayer(toHide);
									player.hidePlayer(plugin, toHide);
								} else if (sneakLeft == 20 && player == toHide) {
									player.sendMessage(ChatColor.BLUE + "Your sneak has worn off" );
								} else if (sneakLeft < 20) {
									player.showPlayer(toHide);
									player.showPlayer(plugin, toHide);
								}
							} else if (!invisList.containsKey(SneakUUID)) {
								player.showPlayer(toHide);
								player.showPlayer(plugin, toHide);
							}
						}
					}
				}
			}
			
		}.runTaskTimer(this, 0, 20);
		
		new BukkitRunnable() {
			//PARTIES/INVITES SYSTEM
			@Override
			public void run() {
				if (outgoingInvites.isEmpty()) {
					return;
				}
				
				for (String uuid: outgoingInvites.keySet()) {
					int timeLeft = outgoingInvites.get(uuid);
					
					if (timeLeft < 1) {
						outgoingInvites.remove(uuid);
					} else {
						outgoingInvites.put(uuid, timeLeft - 1);
					}
				}
				
				
			}
			
		}.runTaskTimer(this, 0, 20);
	}
}
