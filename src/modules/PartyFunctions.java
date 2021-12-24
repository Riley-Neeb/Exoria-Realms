package modules;

import org.bukkit.entity.Player;

import main.Main;

public class PartyFunctions {
	
	public static Main plugin = Main.getPlugin(Main.class);
	
	public boolean isInParty = false;
	public String partyName = null;
	public boolean isSameParty = false;
	
	public boolean isSameParty(Player player, Player player2) {
		String party1 = getPartyName(player);
		String party2 = getPartyName(player2);
		
		if (party1 != null && party2 != null) {
			if (party1.equals(party2)) {
				return this.isSameParty = true;
			} else {
				return this.isSameParty = false;
			}
		}
		
		
		return this.isSameParty = false;
	}
	
	public String getPartyName(Player player) {
		for (String[] PartiesByLeader: plugin.parties.keySet()) {
			for(int i = 0; i < PartiesByLeader.length; i++){
				if (PartiesByLeader[i].equals(player.getName())) {
					String partyNameGet = plugin.parties.get(PartiesByLeader);
					
					return this.partyName = partyNameGet;
				}
			}
		}
		
		return this.partyName = null;
	}
	
	public void isInParty(Player player) {
		for (String[] PartiesByLeader: plugin.parties.keySet()) {
			for(int i = 0; i < PartiesByLeader.length; i++){
				if (PartiesByLeader[i].equals(player.getName())) {
					this.isInParty = true;
				}
			}
		}
		
		this.isInParty = false;
	}
}
