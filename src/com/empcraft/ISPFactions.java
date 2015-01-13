package com.empcraft;

import java.util.Collection;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.FactionColls;
import com.massivecraft.factions.entity.UPlayer;

public class ISPFactions  implements Listener {
	InSignsPlus plugin;
    public ISPFactions(InSignsPlus ISP,Plugin factions) {
    	ISP.addPlaceholder(new Placeholder("faction",factions) {
	        @Override
	        public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
	        	UPlayer uplayer = UPlayer.get(player);
	            return uplayer.getFactionName();
		    }
		    @Override 
		    public String getDescription() {
		        return "{faction} - Returns the player's faction";
	    } });
        ISP.addPlaceholder(new Placeholder("fpower",factions) {
	        @Override
	        public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
	        	UPlayer uplayer = UPlayer.get(player);
	            return ""+uplayer.getPower();
		    }
		    @Override 
		    public String getDescription() {
		        return "{fpower} - Returns the player's power";
	    } });
        ISP.addPlaceholder(new Placeholder("fmaxpower",factions) {
	        @Override
	        public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
	        	UPlayer uplayer = UPlayer.get(player);
	            return ""+uplayer.getPowerMax();
		    }
		    @Override 
		    public String getDescription() {
		        return "{fmaxpower} - Returns the player's max power";
	    } });
        ISP.addPlaceholder(new Placeholder("fpowerboost",factions) {
	        @Override
	        public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
	        	UPlayer uplayer = UPlayer.get(player);
	            return ""+uplayer.getPowerBoost();
		    }
		    @Override 
		    public String getDescription() {
		        return "{fpowerboost} - Returns the player's power boost";
	    } });
        ISP.addPlaceholder(new Placeholder("fusers",factions) {
	        @Override
	        public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
	        	if (modifiers.length>0) {
	        		Faction faction = FactionColls.get().getForWorld(player.getWorld().getName()).getByName(modifiers[0]);
	        		return ""+faction.getUPlayers().size();
	        	}
	        	UPlayer uplayer = UPlayer.get(player);
	            return ""+ uplayer.getFaction().getUPlayers().size();
		    }
		    @Override 
		    public String getDescription() {
		        return "{fusers:*faction} - returns the number of users in a faction";
	    } });
        ISP.addPlaceholder(new Placeholder("fuserlist",factions) {
	        @Override
	        public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
	        	if (modifiers.length>0) {
	        		Faction faction = FactionColls.get().getForWorld(player.getWorld().getName()).getByName(modifiers[0]);
		            List<UPlayer> uplayers = faction.getUPlayers();
		        	String list = "";
		        	for (UPlayer current:uplayers) {
		        		list+=current.getName()+",";
		        	}
		        	return list.trim();
	        	}
	        	UPlayer uplayer = UPlayer.get(player);
	            List<UPlayer> uplayers = uplayer.getFaction().getUPlayers();
	        	String list = "";
	        	for (UPlayer current:uplayers) {
	        		list+=current.getName()+",";
	        	}
	        	return list.trim();
		    }
		    @Override 
		    public String getDescription() {
		        return "{fuserslist:*faction} - returns a list of users in a faction";
	    } });
        ISP.addPlaceholder(new Placeholder("flist",factions) {
	        @Override
	        public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
	        	Collection<Faction> factions = FactionColls.get().getForWorld(player.getWorld().getName()).getAll();
	        	String list = "";
	        	for (Faction current:factions) {
	        		list+=current.getName()+",";
	        	}
	        	return list.trim();
		    }
		    @Override 
		    public String getDescription() {
		        return "{flist} - Returns a list of factions";
	    } });
        ISP.addPlaceholder(new Placeholder("factions",factions) {
	        @Override
	        public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
	        	return ""+FactionColls.get().getForWorld(player.getWorld().getName()).getAll().size();
		    }
		    @Override 
		    public String getDescription() {
		        return "{factions} - Returns the number of factions";
	    } });
        ISP.addPlaceholder(new Placeholder("fdesc",factions) {
	        @Override
	        public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
	        	if (modifiers.length>0) {
	        		Faction faction = FactionColls.get().getForWorld(player.getWorld().getName()).getByName(modifiers[0]);
		            return faction.getDescription();
	        	}
	        	return UPlayer.get(player).getFaction().getDescription();
		    }
		    @Override 
		    public String getDescription() {
		        return "{fdesc} - Returns a list of factions";
	    } });
    }
}
