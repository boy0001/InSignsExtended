package com.empcraft;

import org.apache.commons.lang.builder.HashCodeBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class SignPlus
{
	private final Location location;
	private final String player;
	private int clicks;
	private final boolean[] lines;
	public SignPlus(Location location, Player player, boolean[] linesToUpdate) {
		this.clicks = 0;
		this.location = location;
		this.player = player.getName();
		this.lines = linesToUpdate;
	}
	public SignPlus(Location location, Player player) {
		this.clicks = 0;
		this.location = location;
		this.player = player.getName();
		this.lines = new boolean[]{true,true,true,true};
	}
	public boolean equals(Object obj) {
		if (!(obj instanceof SignPlus)) {
			return false;
		}
	    if (obj == this) {
	    	return true;
	    }
	    if (((SignPlus) obj).getLocation().equals(this.getLocation())) {
	    	if (((SignPlus) obj).getPlayerName().equals(this.getPlayerName())) {
		    	return true;
		    }
	    }
	    return false;
	  }
	public int hashCode() {
		return new HashCodeBuilder(31, 17).append(location).append(player).toHashCode();
	}
	public Player getPlayer() {
		Player user = Bukkit.getPlayer(this.player);
		if (user!=null && user.isOnline()) {
			return Bukkit.getPlayer(this.player);
		}
		else {
			return null;
		}
	}
	public String getPlayerName() {
		return this.player;
	}
	public Location getLocation() {
		return this.location;
	}
	public int getClicks() {
		return this.clicks;
	}
	public void setClicks(int value) {
		this.clicks = value;
	}
	public boolean update() {
		Player player = getPlayer();
		if (player==null) { return false; }
		return InSignsPlus.plugin.updateSign(player, this.location);
	}
	public boolean[] getLines() {
		return this.lines;
	}
}