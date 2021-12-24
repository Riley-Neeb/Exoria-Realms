package fatul;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;


import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.zcore.MPlugin;

public class Factions extends MPlugin {
	private Main plugin = Main.getPlugin(Main.class);
	
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
}

