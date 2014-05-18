package com.empcraft;

import java.io.File;
import java.io.FilenameFilter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.Map.Entry;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Sign;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.Plugin;

public class DefaultPlaceholders {
	DefaultPlaceholders(final InSignsPlus ISP) {
		ISP.addPlaceholder(new Placeholder("u") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
    		if (modifiers.length==1) {
    		try {
    			String c = ""+((char) Integer.parseInt(modifiers[0], 16));
    		return c;
    		}
    		catch (Exception e) {
    			
    		}
    		}
    		return "";
			}
			@Override 
			public String getDescription() {
				return "{u:HEX} - Enter in a unicode character";
    	} });
    	ISP.addPlaceholder(new Placeholder("rand") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
    		Random random = new Random();
    		if (modifiers.length==1) {
    			return ""+random.nextInt(Integer.parseInt(modifiers[0]));
    		}
			int start = Integer.parseInt(modifiers[0]);
			int stop = Integer.parseInt(modifiers[1]);
			return ""+random.nextInt(stop-start)+start;
			}
	    	@Override 
			public String getDescription() {
				return "{rand:X} - returns a random int from 0 to X\n{rand:X:Y} - returns a random int from X to Y";
    	} });
    	ISP.addPlaceholder(new Placeholder("msg") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
			return ISP.getmsg(modifiers[0]);
			}
	    	@Override 
			public String getDescription() {
				return "{msg:ID} - Returns the given message from the language file";
		} });
    	ISP.addPlaceholder(new Placeholder("range") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
    		String mylist = "";
    		int start = 0;
    		int stop = 0;
    		if (modifiers.length==1) {
    			stop = Integer.parseInt(modifiers[0]);
    		}
    		else if (modifiers.length==2) {
    			start = Integer.parseInt(modifiers[0]);
    			stop = Integer.parseInt(modifiers[1]);
    		}
    		if (stop-start<512) {
    		for(int i = start; i <= stop; i++) {
    			mylist+=i+",";
    		}
    		}
    		return mylist.substring(0,mylist.length()-1);
			}
	    	@Override 
			public String getDescription() {
				return "{range:X} - Returns a list from 0 to X\n{range:X,Y}	- Returns a list from X to Y";
		} });
    	ISP.addPlaceholder(new Placeholder("matchplayer") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
    		List<Player> matches = Bukkit.matchPlayer(modifiers[0]);
    		String mymatches = "";
    		if (matches.isEmpty()==false) {
    			for (Player match:matches) {
    				mymatches+=match.getName()+",";
    			}
    			return mymatches.substring(0,mymatches.length()-1);
    		}
    		else {
    			return "null";
    		}
		}
    	@Override 
		public String getDescription() {
			return "{matchplayer:STRING} - Returns the closest matching online player";
		} });
    	ISP.addPlaceholder(new Placeholder("index") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
    		return modifiers[0].split(",")[Integer.parseInt(modifiers[1])];
		}
    	@Override 
		public String getDescription() {
			return "{index:LIST:INDEX} - Returns the item at INDEX in a list";
		} });
    	ISP.addPlaceholder(new Placeholder("setindex") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
    		String[] mylist = modifiers[0].split(",");
    		String newlist = "";
    		int myindex = Integer.parseInt(modifiers[1]);
    		for(int i = 0; i < mylist.length; i++) {
    			if (i==myindex) {
    				newlist+=modifiers[2]+",";
    			}
    			else {
    				newlist+=mylist[i]+",";
    			}
    		}
    		return newlist.substring(0,newlist.length()-1);
		}
    	@Override 
		public String getDescription() {
			return "{setindex:LIST:INDEX:VALUE} - Returns a new list with the item set at index";
		} });
    	ISP.addPlaceholder(new Placeholder("delindex") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
    		String[] mylist = modifiers[0].split(",");
    		String newlist = "";
    		int myindex = Integer.parseInt(modifiers[1]);
    		for(int i = 0; i < mylist.length; i++) {
    			if (i==myindex) {
    			}
    			else {
    				newlist+=mylist[i]+",";
    			}
    		}
    		return newlist.substring(0,newlist.length()-1);
		}
    	@Override 
		public String getDescription() {
			return "{delindex:LIST:INDEX:VALUE} - Returns the list with the item deleted at the index";
		} });
    	ISP.addPlaceholder(new Placeholder("sublist") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
    		String[] mylist = modifiers[0].split(",");
    		String newlist = "";
    		int i1 = Integer.parseInt(modifiers[1]);
    		int i2 = Integer.parseInt(modifiers[2]);
    		for(int i = 0; i < mylist.length; i++) {
    			if ((i>=i1)&&(i<=i2)) {
    				newlist+=mylist[i]+",";
    			}
    		}
    		return newlist.substring(0,newlist.length()-1);
		}
    	@Override 
		public String getDescription() {
			return "{sublist:X:Y} - Returns a new list from index X to index Y";
		} });
    	ISP.addPlaceholder(new Placeholder("getindex") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
    		String[] mylist = modifiers[0].split(",");
    		String newlist = "";
    		for(int i = 0; i < mylist.length; i++) {
    			if (mylist[i].equals(modifiers[1])) {
    				newlist+=i+",";
    			}
    		}
    		if (newlist.equals("")) {
    			return "null";
    		}
    		return newlist.substring(0,newlist.length()-1);
		}
    	@Override 
		public String getDescription() {
			return "{getindex:LIST:VALUE} - Returns the index for an item in the list";
		} });
    	ISP.addPlaceholder(new Placeholder("listhas") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
    		String[] mylist = modifiers[0].split(",");
    		for(int i = 0; i < mylist.length; i++) {
    			if (mylist[i].equals(modifiers[1])) {
    				return "true";
    			}
    		}
    		return "false";
		}
    	@Override 
		public String getDescription() {
			return "{listhas:LIST:VALUE} - Returns true if a list contains a value";
		} });
    	ISP.addPlaceholder(new Placeholder("contains") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
    		if (modifiers[0].contains(modifiers[1])) {
    			return "true";
    		}
    		else if (modifiers[0].equals(modifiers[1])) {
    			return "true";
    		}
    		return "false";
		}
    	@Override 
		public String getDescription() {
			return "{contains:STRING:VALUE} - Returns true if a string contains a value";
		} });
    	ISP.addPlaceholder(new Placeholder("substring") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
    		return modifiers[0].substring(Integer.parseInt(modifiers[1]), Integer.parseInt(modifiers[2]));
		}
    	@Override 
		public String getDescription() {
			return "{substring:X:Y} - Returns part of the string from index X to index Y";
		} });
    	ISP.addPlaceholder(new Placeholder("size") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
			return ""+modifiers[0].split(",").length;
		}
    	@Override 
		public String getDescription() {
			return "{size:LIST} - Returns the size of a list";
		} });
    	ISP.addPlaceholder(new Placeholder("length") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
			return ""+modifiers[0].length();
		}
    	@Override 
		public String getDescription() {
			return "{length:STRING} - Returns the length of the string";
		} });
    	ISP.addPlaceholder(new Placeholder("split") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
    		return modifiers[0].replace(modifiers[1],",");
		}
    	@Override 
		public String getDescription() {
			return "{split:LIST:DELIMETER} - Splits a string by a specified delimeter";
		} });
    	ISP.addPlaceholder(new Placeholder("hasperm") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
    		if (player==null) {
    			return "true";
    		}
    		else if (ISP.checkperm(player,modifiers[0])) {
    			return "true";
    		}
    		return "false";
		}
    	@Override 
		public String getDescription() {
			return "{hasperm:NODE} - Returns true if a player has the permission";
		} });
    	ISP.addPlaceholder(new Placeholder("randchoice") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
    		String[] mylist = modifiers[0].split(",");
    		Random random = new Random();
    		return mylist[random.nextInt(mylist.length-1)];
		}
    	@Override 
		public String getDescription() {
			return "{randchoice:LIST} - Returns a random choice from a list";
		} });
    	ISP.addPlaceholder(new Placeholder("worldtype") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
			if (modifiers.length==1) {
	    		Location loc = ISP.getloc(modifiers[0], player);
	    		return ""+loc.getWorld().getWorldType().getName();
			}
			else {
				return ""+player.getWorld().getWorldType();
			}
		}
    	@Override 
		public String getDescription() {
			return "{worldtype:*location} - Returns the type of world at a location (e.g. FLAT, AMPLIFIED)";
		} });
    	ISP.addPlaceholder(new Placeholder("listreplace") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
    		String[] mylist = modifiers[0].split(",");
    		String newlist = "";
    		for(int i = 0; i < mylist.length; i++) {
    			if (mylist[i].equals(modifiers[1])) {
    				newlist+=modifiers[2]+",";
    			}
    		}
    		if (newlist.equals("")) {
    			return "null";
    		}
    		return newlist.substring(0,newlist.length()-1);
		}
    	@Override 
		public String getDescription() {
			return "{listreplace:VALUE:VALUE2} - Returns a new list with occurrences of VALUE replaced with VALUE2";
		} });

    	ISP.addPlaceholder(new Placeholder("worldticks") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
			if (modifiers.length==1) {
				Location loc = ISP.getloc(modifiers[0], player);
	    		return Long.toString(loc.getWorld().getTime());
			}
    		return Long.toString(player.getWorld().getTime());
		}
    	@Override 
		public String getDescription() {
			return "{worldticks:*location} - Returns the time in ticks for a world";
		} });
    	ISP.addPlaceholder(new Placeholder("time") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
    		if (modifiers.length==1) {
        		Location loc = ISP.getloc(modifiers[0], player);
        		Double time = loc.getWorld().getTime() / 1000.0;
        		Double time2 = time;
        		if (time2>18) { time2-=25; }
        		String hr = ""+(time2.intValue() + 6);
        		String min = ""+((int) (60*(time%1)));
        		if (min.length()==1) {
        			min = "0"+min;
        		}
        		if (hr.length()==1) {
        			hr = "0"+hr;
        		}
        		return ""+hr+":"+min;
    		}
    		Double time = player.getWorld().getTime() / 1000.0;
    		Double time2 = time;
    		if (time2>18) { time2-=25; }
    		String hr = ""+(time2.intValue() + 6);
    		String min = ""+((int) (60*(time%1)));
    		if (min.length()==1) {
    			min = "0"+min;
    		}
    		if (hr.length()==1) {
    			hr = "0"+hr;
    		}
    		return ""+hr+":"+min;
		}
    	@Override 
		public String getDescription() {
			return "{time:*location} - The time (24hr) in a world";
		} });
    	ISP.addPlaceholder(new Placeholder("sectotime") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
    		String toreturn = "";
    		try {
    		Long time = Long.parseLong(modifiers[0]);
    		int years = 0;
    		int weeks = 0;
    		int days = 0;
    		int hours = 0;
    		int minutes = 0;
    		if (time>=33868800) {
    			years = (int) (time/33868800);
    			time-=years*33868800;
    			toreturn+=years+"y ";
    		}
    		if (time>=604800) {
    			weeks = (int) (time/604800);
    			time-=weeks*604800;
    			toreturn+=weeks+"w ";
    		}
    		if (time>=86400) {
    			days = (int) (time/86400);
    			time-=days*86400;
    			toreturn+=days+"d ";
    		}
    		if (time>=3600) {
    			hours = (int) (time/3600);
    			time-=hours*3600;
    			toreturn+=hours+"h ";
    		}
    		if (time>=60) {
    			minutes = (int) (time/60);
    			time-=minutes*60;
    			toreturn+=minutes+"m ";
    		}
    		if (toreturn.equals("")||time>0){
    			toreturn+=(time)+"s ";
    		}
    		toreturn = toreturn.trim();
    		}
    		catch (Exception e) {
    			e.printStackTrace();
    		}
    		return toreturn;
		}
    	@Override 
		public String getDescription() {
			return "{sectotime} - converts seconds to a user friendly time e.g. 5h 15m 8s";
		} });
    	ISP.addPlaceholder(new Placeholder("localtime") { @SuppressWarnings("deprecation")
		@Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
    		Date time = Calendar.getInstance().getTime();
    		String hours = ""+time.getHours();
    		String minutes = ""+time.getMinutes();
    		String seconds = ""+time.getSeconds();
    		if (hours.length()==1) {
    			hours = "0"+hours;
    		}
    		if (minutes.length()==1) {
    			minutes = "0"+minutes;
    		}
    		if (seconds.length()==1) {
    			seconds = "0"+seconds;
    		}
    		return hours+":"+minutes+":"+seconds;
		}
    	@Override 
		public String getDescription() {
			return "{localtime} - The time (24hr) for the server";
		} });
    	ISP.addPlaceholder(new Placeholder("date") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
    		if (modifiers.length == 0) {
    			return (new SimpleDateFormat("d\\M\\yy")).format(new Date());
    		}
    		else {
    			Date date = new Date();
    			return (new SimpleDateFormat(modifiers[0])).format(date);
    			//todo convert timestamp to date.
    		}
		}
    	@Override 
		public String getDescription() {
			return "{date} - The date as d\\m\\yy\n{date:FORMATE} - The date in the format specified";
		} });
    	ISP.addPlaceholder(new Placeholder("localtime12") { @SuppressWarnings("deprecation")
		@Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
    		String ampm = " AM";
    		Date time = Calendar.getInstance().getTime();
    		int hours = time.getHours();
    		String minutes = ""+time.getMinutes();
    		String seconds = ""+time.getSeconds();
    		
    		if (hours>12) {
    			hours-=12;
    			ampm = " PM";
    		}
    		else if(hours==12) {
    			ampm = " PM";
    		}
    		else if (hours==0) {
    			hours+=12;
    		}
    		
    		if (minutes.length()==1) {
    			minutes = "0"+minutes;
    		}
    		if (seconds.length()==1) {
    			seconds = "0"+seconds;
    		}
    		
    		String hr = ""+hours;
    		if (hr.length()==1) {
    			hr = "0"+hr;
    		}
    		return hr+":"+minutes+":"+seconds+ampm;
		}
    	@Override 
		public String getDescription() {
			return "{localtime12} - The time (12hr) for the server";
		} });
    	ISP.addPlaceholder(new Placeholder("time12") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
    		if (modifiers.length==1) {
        		String ampm = " AM";
        		Location loc = ISP.getloc(modifiers[0], player);
        		Double time = loc.getWorld().getTime() / 1000.0;
        		Double time2 = time;
        		if (time2>18) { time2-=24; }
        		time2+=6;
        		if (time2.intValue()>13) {
        			time2-=12;
        			ampm = " PM";
        		}
        		else if(time2>12) {
        			ampm = " PM";
        		}
        		else if (time2 < 1) {
        			time2+=12;
        		}
        		String hr = ""+(time2.intValue());
        		String min = ""+((int) (60*(time%1)));
        		if (min.length()==1) {
        			min = "0"+min;
        		}
        		if (hr.length()==1) {
        			hr = "0"+hr;
        		}
        		return ""+hr+":"+min+ampm;
    		}
    		String ampm = " AM";
    		Double time = player.getWorld().getTime() / 1000.0;
    		Double time2 = time;
    		if (time2>18) { time2-=24; }
    		time2+=6;
    		if (time2.intValue()>13) {
    			time2-=12;
    			ampm = " PM";
    		}
    		else if(time2>12) {
    			ampm = " PM";
    		}
    		else if (time2 < 1) {
    			time2+=12;
    		}
    		String hr = ""+(time2.intValue());
    		String min = ""+((int) (60*(time%1)));
    		if (min.length()==1) {
    			min = "0"+min;
    		}
    		if (hr.length()==1) {
    			hr = "0"+hr;
    		}
    		return ""+hr+":"+min+ampm;
		}
    	@Override 
		public String getDescription() {
			return "{time12:*location} - The time (12hr) in a MC world";
		} });
    	ISP.addPlaceholder(new Placeholder("replace") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
    		return modifiers[0].replace(modifiers[1], modifiers[2]);
		}
    	@Override 
		public String getDescription() {
			return "{replace:VALUE:VALUE2} - Returns a new string with occurrences of VALUE replaced with VALUE2";
		} });
    	ISP.addPlaceholder(new Placeholder("config") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
    		return ISP.getConfig().getString(modifiers[0]);
		}
    	@Override 
		public String getDescription() {
			return "{config:NODE} - Returns the value from the config for the given node";
		} });
    	ISP.addPlaceholder(new Placeholder("structures") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
    		if (modifiers.length==1) {
        		Location loc = ISP.getloc(modifiers[0], player);
        		return loc.getWorld().canGenerateStructures()+"";
    		}
    		return ""+player.getWorld().canGenerateStructures();
		}
    	@Override 
		public String getDescription() {
			return "{structures:*location} - Returns if structure generation is enabled for a world";
		} });
    	ISP.addPlaceholder(new Placeholder("autosave") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
    		if (modifiers.length==1) {
    			Location loc = ISP.getloc(modifiers[0], player);
        		return loc.getWorld().isAutoSave()+"";
    		}
    		return ""+player.getWorld().isAutoSave();
		}
    	@Override 
		public String getDescription() {
			return "{autosave:*location} - Returns true if autosaving is enabled";
		} });
    	ISP.addPlaceholder(new Placeholder("animals") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
    		if (modifiers.length==1) {
        		Location loc = ISP.getloc(modifiers[0], player);
        		return loc.getWorld().getAllowAnimals()+"";
    		}
    		return ""+player.getWorld().getAllowAnimals();
		}
    	@Override 
		public String getDescription() {
			return "{animals:*location} - Returns true if animals are enabled";
		} });
    	ISP.addPlaceholder(new Placeholder("monsters") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
    		if (modifiers.length==1) {
        		Location loc = ISP.getloc(modifiers[0], player);
        		return loc.getWorld().getAllowMonsters()+"";
    		}
    		return ""+player.getWorld().getAllowMonsters();
		}
    	@Override 
		public String getDescription() {
			return "{monsters:*location} - Returns true if monsters are enabled";
		} });
    	ISP.addPlaceholder(new Placeholder("online") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
    		if (modifiers.length==1) {
        		ISP.getloc(modifiers[0], player);
        		String online = "";
        		for (Player user:Bukkit.getServer().getOnlinePlayers()) {
          			online+=user.getName()+",";
          		}
        		return online.substring(0,online.length()-1);
    		}
    		String online = "";
      		for (Player qwert:Bukkit.getServer().getOnlinePlayers()) {
      			online+=qwert.getName()+",";
      		}
    		return online.substring(0,online.length()-1);
		}
    	@Override 
		public String getDescription() {
			return "{online} - Returns the list of online players\n{online:*location} - Returns the list of players in a world";
		} });
    	ISP.addPlaceholder(new Placeholder("colors") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
    		return "&1,&2,&3,&4,&5,&6,&7,&8,&9,&0,&a,&b,&c,&d,&e,&f,&r,&l,&m,&n,&o,&k";
		}
    	@Override 
		public String getDescription() {
			return "{colors} - Returns a list of color codes";
		} });
    	ISP.addPlaceholder(new Placeholder("difficulty") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
    		if (modifiers.length==1) {
        		Location loc = ISP.getloc(modifiers[0], player);
        		return loc.getWorld().getDifficulty().toString();
    		}
    		return ""+player.getWorld().getDifficulty().name();
		}
    	@Override 
		public String getDescription() {
			return "{difficulty:*location} - Returns the difficulty for a world";
		} });
    	ISP.addPlaceholder(new Placeholder("weatherduration") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
    		if (modifiers.length==1) {
        		Location loc = ISP.getloc(modifiers[0], player);
        		return ""+loc.getWorld().getWeatherDuration();
    		}
    		return ""+player.getWorld().getWeatherDuration();
		}
    	@Override 
		public String getDescription() {
			return "{weatherduration:*location} - Returns the duration in ticks of the weather";
		} });
    	ISP.addPlaceholder(new Placeholder("environment") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
    		if (modifiers.length==1) {
        		Location loc = ISP.getloc(modifiers[0], player);
        		return loc.getWorld().getEnvironment().toString();
    		}
    		return ""+player.getWorld().getEnvironment().name();
		}
    	@Override 
		public String getDescription() {
			return "{environment:*location} - Returns the environment at a location(e.g. NETHER, END)";
		} });
    	ISP.addPlaceholder(new Placeholder("player") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
    		if (modifiers.length==1) {
    			return Bukkit.getOfflinePlayer(UUID.fromString(modifiers[0])).getName();
    		}
    		if (player==null) {
    			return "CONSOLE";
    		}
    		else {
    			return player.getName();
    		}
		}
    	@Override 
		public String getDescription() {
			return "{environment} - Returns the players name.";
		} });
    	ISP.addPlaceholder(new Placeholder("gvar") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
    		return StringUtils.join(ISP.globals.keySet(),",").replace("{","").replace("}", "");
		}
    	@Override 
		public String getDescription() {
			return "{gvar} - Returns a list of global variables";
		} });
    	ISP.addPlaceholder(new Placeholder("sender") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
    		Player sender = ISP.getSender();
    		if (sender==null) {
    			return "CONSOLE";
    		}
    		else {
    			return sender.getName();
    		}
		}
    	@Override 
		public String getDescription() {
			return "{sender} - Returns the original player who called the script";
		} });
    	ISP.addPlaceholder(new Placeholder("elevated") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
    		return ""+elevation;
		}
    	@Override 
		public String getDescription() {
			return "{elevated} - Returns true if the script is elevated";
		} });
    	ISP.addPlaceholder(new Placeholder("gamerules") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
    		if (modifiers.length==1) {
        		Location loc = ISP.getloc(modifiers[0], player);
        		return StringUtils.join(loc.getWorld().getGameRules(),",");
    		}
    		return StringUtils.join(player.getWorld().getGameRules(),",");
		}
    	@Override 
		public String getDescription() {
			return "{gamerules} - Returns the list of gamerules";
		} });
    	ISP.addPlaceholder(new Placeholder("seed") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
    		if (modifiers.length==1) {
        		Location loc = ISP.getloc(modifiers[0], player);
        		return ""+loc.getWorld().getSeed();
    		}
    		return ""+player.getWorld().getSeed();
		}
    	@Override 
		public String getDescription() {
			return "{seed:*location} - Returns the seed for a world";
		} });
    	ISP.addPlaceholder(new Placeholder("spawn") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
    		if (modifiers.length==1) {
    			Location loc = ISP.getloc(modifiers[0], player);
        		return loc.getWorld().getName()+","+loc.getWorld().getSpawnLocation().getX()+","+loc.getWorld().getSpawnLocation().getY()+","+loc.getWorld().getSpawnLocation().getZ();
    		}
    		return location.getWorld().getName()+","+location.getWorld().getSpawnLocation().getX()+","+location.getWorld().getSpawnLocation().getY()+","+location.getWorld().getSpawnLocation().getZ();
		}
    	@Override 
		public String getDescription() {
			return "{spawn:*location} - Returns the spawn location for a world";
		} });
    	ISP.addPlaceholder(new Placeholder("count") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
    		if (modifiers[0].contains(",")) {
    			int count = 0;
    			String[] mylist = modifiers[0].split(",");
    			for (String mynum:mylist) {
    				if (mynum.equals(modifiers[1])) {
    					count+=1;
    				}
    			}
    			return ""+count;
    		}
    		else {
    			return ""+StringUtils.countMatches(modifiers[0],modifiers[1]);
    		}
		}
    	@Override 
		public String getDescription() {
			return "{count:LIST:VALUE} - Returns the number of times a value appears in a list";
		} });
    	ISP.addPlaceholder(new Placeholder("epoch") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
    		return Long.toString(System.currentTimeMillis()/1000);
		}
    	@Override 
		public String getDescription() {
			return "{epoch} - Returns the seconds since the epoch";
		} });
    	ISP.addPlaceholder(new Placeholder("js") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
    		return ISP.javascript(StringUtils.join(modifiers,":"));
		}
    	@Override 
		public String getDescription() {
			return "{js:SCRIPT} - Useful for basic math e.g. {js:1+1} but can do any javascript action\n{jsg:FILE.js} - Javascript files are located in the scripts folder for the plugin";
		} });
    	ISP.addPlaceholder(new Placeholder("javascript") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
    		return ISP.javascript(StringUtils.join(modifiers,":"));
		}
    	@Override 
		public String getDescription() {
			return "{javascript:SCRIPT} - Useful for basic math e.g. {javascript:1+1} but can do any javascript action\n{jsg:FILE.js} - Javascript files are located in the scripts folder for the plugin";
		} });
    	ISP.addPlaceholder(new Placeholder("epochmilli") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
    		return Long.toString(System.currentTimeMillis());
		}
    	@Override 
		public String getDescription() {
			return "{epochmilli} - Returns the milliseconds since the epoch";
		} });
    	ISP.addPlaceholder(new Placeholder("epochnano") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
    		return Long.toString(System.nanoTime());
		}
    	@Override 
		public String getDescription() {
			return "{epochnano} - Returns the nanoseconds since the epoch";
		} });
    	ISP.addPlaceholder(new Placeholder("motd") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
    		return ""+Bukkit.getMotd();
		}
    	@Override 
		public String getDescription() {
			return "{motd} - Returns the server MOTD";
		} });
    	ISP.addPlaceholder(new Placeholder("banlist") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
    		String mylist = "";
      		for (OfflinePlayer clist:Bukkit.getBannedPlayers()) {
      			mylist+=clist.getName()+",";
      		}
    		return mylist.substring(0,mylist.length()-1);
		}
    	@Override 
		public String getDescription() {
			return "{banlist} - Returns the list of banned players";
		} });
    	ISP.addPlaceholder(new Placeholder("uuidlist") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
			List<String> names = new ArrayList<String>();
            File playersFolder = new File("world" + File.separator + "playerdata");
            String[] dat = playersFolder.list(new FilenameFilter() {
            	public boolean accept(File f, String s) {
                    return s.endsWith(".dat");
                }
            });
            for (String current : dat) {
                names.add(current.replaceAll(".dat$", ""));
            }
            return StringUtils.join(names,",");
		}
    	@Override 
		public String getDescription() {
			return "{uuidlist} - Returns the whole list of uuids (very long)";
		} });
    	ISP.addPlaceholder(new Placeholder("playerlist") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
			List<String> names = new ArrayList<String>();
            File playersFolder = new File("world" + File.separator + "players");
            String[] dat = playersFolder.list(new FilenameFilter() {
            	public boolean accept(File f, String s) {
                    return s.endsWith(".dat");
                }
            });
            for (String current : dat) {
                names.add(current.replaceAll(".dat$", ""));
            }
            return StringUtils.join(names,",");
		}
    	@Override 
		public String getDescription() {
			return "{playerlist} - Returns the whole list of players (very long)";
		} });
    	ISP.addPlaceholder(new Placeholder("baniplist") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
    		String mylist = "";
      		for (String clist:Bukkit.getIPBans()) {
      			mylist+=clist+",";
      		}
    		return mylist.substring(0,mylist.length()-1);
		}
    	@Override 
		public String getDescription() {
			return "{baniplist} - Returns the list of banned IPs";
		} });
    	ISP.addPlaceholder(new Placeholder("worlds") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
    		String mylist = "";
      		for (World clist:Bukkit.getServer().getWorlds()) {
      			mylist+=clist.getName()+",";
      		}
    		return mylist.substring(0,mylist.length()-1);
		}
    	@Override 
		public String getDescription() {
			return "{worlds} - Returns the list of worlds";
		} });
    	ISP.addPlaceholder(new Placeholder("slots") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
    		return ""+Bukkit.getMaxPlayers();
		}
    	@Override 
		public String getDescription() {
			return "{slots} - Returns the max slots on the server";
		} });
    	ISP.addPlaceholder(new Placeholder("port") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
    		return ""+Bukkit.getPort();
		}
    	@Override 
		public String getDescription() {
			return "{port} - Returns the port the server is running on";
		} });
    	ISP.addPlaceholder(new Placeholder("version") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
    		return Bukkit.getVersion().split(" ")[0];
		}
    	@Override 
		public String getDescription() {
			return "{version} - Returns the minecraft version";
		} });
    	ISP.addPlaceholder(new Placeholder("allowflight") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
    		return ""+Bukkit.getAllowFlight();
		}
    	@Override 
		public String getDescription() {
			return "{allowflight} - Returns true if flying is allowed";
		} });
    	ISP.addPlaceholder(new Placeholder("viewdistance") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
    		return ""+Bukkit.getViewDistance();
		}
    	@Override 
		public String getDescription() {
			return "{viewdistance} - Returns the server view distance";
		} });
    	ISP.addPlaceholder(new Placeholder("defaultgamemode") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
    		return ""+Bukkit.getDefaultGameMode();
		}
    	@Override 
		public String getDescription() {
			return "{defaultgamemode} - Returns the server's default gamemode";
		} });
    	ISP.addPlaceholder(new Placeholder("operators") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
    		String mylist = "";
      		for (OfflinePlayer clist:Bukkit.getOperators()) {
      			mylist+=clist.getName()+",";
      		}
    		return mylist.substring(0,mylist.length()-1);
		}
    	@Override 
		public String getDescription() {
			return "{operators} - Returns the list of operators";
		} });
    	ISP.addPlaceholder(new Placeholder("whitelist") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
    		Set<OfflinePlayer> mylist = Bukkit.getWhitelistedPlayers();
    		String mystr = "";
    		Iterator<OfflinePlayer> it = mylist.iterator();
    		for (int i=0;i<mylist.size();i++) {
    			if (i==0) {
    				mystr+=it.next().getName();
    			}
    			else {
    				mystr+=","+it.next().getName();
    			}
    		}
    		return mystr;
		}
    	@Override 
		public String getDescription() {
			return "{whitelist} - Returns the whitelist";
		} });
    	ISP.addPlaceholder(new Placeholder("plugins") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
    		Plugin[] myplugins = Bukkit.getServer().getPluginManager().getPlugins();
    		String mystr = "";
    		for (int i=0;i<myplugins.length;i++) {
    			if (i==0) {
    				mystr+=myplugins[i].getName();
    			}
    			else {
    				mystr+=","+myplugins[i].getName();
    			}
    		}
    		return mystr;
		}
    	@Override 
		public String getDescription() {
			return "{plugins} - Returns the list of plugins";
		} });
    	ISP.addPlaceholder(new Placeholder("uuid") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
    		if (modifiers.length==1) { return Bukkit.getOfflinePlayer(modifiers[0]).getUniqueId().toString(); }
    		else { return player.getUniqueId().toString(); }
    	}
    	@Override 
		public String getDescription() {
			return "{uuid:*player} - Returns the UUID for the player";
		} });
    	ISP.addPlaceholder(new Placeholder("exhaustion") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
    		if (modifiers.length==1) {
    			if (Bukkit.getPlayer(modifiers[0])==null) {
    	    		try {
    	    			IOP_1_7_9 offlineplayer = new IOP_1_7_9(modifiers[0]);
	        			return ""+offlineplayer.getExhaustion();
    	    		}
    	    		catch (Exception e1) {
    				try {
    	    			ImprovedOfflinePlayer offlineplayer = new ImprovedOfflinePlayer(modifiers[0]);
    	    			return ""+offlineplayer.getExhaustion();
    	        		}
	        		catch (Exception e) {
	        			IOP_1_7_2 offlineplayer = new IOP_1_7_2(modifiers[0]);
	        			return ""+offlineplayer.getExhaustion();
	        		}
    			}
    			}
				return ""+Bukkit.getPlayer(modifiers[0]).getExhaustion();
    		}
			return ""+player.getExhaustion();
		}
    	@Override 
		public String getDescription() {
			return "{exhaustion:*username} - Returns the player's exhaustion";
		} });
    	ISP.addPlaceholder(new Placeholder("display") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
    		if (modifiers.length==1) {
    			if (Bukkit.getPlayer(modifiers[0])==null) {
    	    		try {
    	    			String nick = (new EssentialsFeature()).displayName(modifiers[0]);
    	    			if (nick.equals("")) {
    	    				return modifiers[0];
    	    			}
    	    			return nick;
    	    		}
    	    		catch (Exception e) {
    	    			return modifiers[0];
    	    		}
    			}
    			return ""+Bukkit.getPlayer(modifiers[0]).getDisplayName();
    		}
			return ""+player.getDisplayName();
		}
    	@Override 
		public String getDescription() {
			return "{display:*username} - Returns the player's nickname";
		} });
    	ISP.addPlaceholder(new Placeholder("firstjoin") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
    		return Long.toString(Bukkit.getOfflinePlayer(modifiers[0]).getFirstPlayed()/1000);		
		}
    	@Override 
		public String getDescription() {
			return "{firstjoin:*username} - Returns the timestamp (seconds) for when the player joined";
		} });
    	ISP.addPlaceholder(new Placeholder("lastplayed") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
    		if (Bukkit.getPlayer(modifiers[0])!=null) {
    			return "0";
    		}
    		return Long.toString(Bukkit.getOfflinePlayer(modifiers[0]).getLastPlayed()/1000);
		}
    	@Override 
		public String getDescription() {
			return "{lastplayed:*username} - Returns the time since the player last played.";
		} });
    	ISP.addPlaceholder(new Placeholder("hunger") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
    		if (modifiers.length==1) {
    			if (Bukkit.getPlayer(modifiers[0])==null) {
    	    		try {
    	    			IOP_1_7_9 offlineplayer = new IOP_1_7_9(modifiers[0]);
	        			return ""+offlineplayer.getFoodLevel();
    	    		}
    	    		catch (Exception e1) {
    				try {
    	    			ImprovedOfflinePlayer offlineplayer = new ImprovedOfflinePlayer(modifiers[0]);
    	    			return ""+offlineplayer.getFoodLevel();
    	        		}
    	        		catch (Exception e) {
    	        			IOP_1_7_2 offlineplayer = new IOP_1_7_2(modifiers[0]);
    	        			return ""+offlineplayer.getFoodLevel();
    	        		}
    	    		}
    			}
    			return ""+Bukkit.getPlayer(modifiers[0]).getFoodLevel();
    		}
			return ""+player.getFoodLevel();
		}
    	@Override 
		public String getDescription() {
			return "{hunger:*username} - Returns a player's hunger";
		} });
    	ISP.addPlaceholder(new Placeholder("air") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
    		if (modifiers.length==1) {
    			if (Bukkit.getPlayer(modifiers[0])==null) {
    	    		try {
    	    			IOP_1_7_9 offlineplayer = new IOP_1_7_9(modifiers[0]);
	        			return ""+offlineplayer.getRemainingAir();
    	    		}
    	    		catch (Exception e1) {
    				try {
    	    			ImprovedOfflinePlayer offlineplayer = new ImprovedOfflinePlayer(modifiers[0]);
    	    			return ""+offlineplayer.getRemainingAir();
    	        		}
    	        		catch (Exception e) {
    	        			IOP_1_7_2 offlineplayer = new IOP_1_7_2(modifiers[0]);
    	        			return ""+offlineplayer.getRemainingAir();
    	        		}
    	    		}
    			}
    			return ""+Bukkit.getPlayer(modifiers[0]).getRemainingAir();
    		}
			return ""+player.getRemainingAir();
		}
    	@Override 
		public String getDescription() {
			return "{air:*username} - Returns a player's air";
		} });
    	ISP.addPlaceholder(new Placeholder("bed") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
    		if (modifiers.length==1) {
    			if (Bukkit.getPlayer(modifiers[0])==null) {
    	    		try {
    	    			IOP_1_7_9 offlineplayer = new IOP_1_7_9(modifiers[0]);
    	    			return ""+offlineplayer.getBedSpawnLocation().getX()+","+offlineplayer.getBedSpawnLocation().getY()+","+offlineplayer.getBedSpawnLocation().getZ();
    	    		}
    	    		catch (Exception e1) {
    				try {
    	    			ImprovedOfflinePlayer offlineplayer = new ImprovedOfflinePlayer(modifiers[0]);
    	    			return ""+offlineplayer.getBedSpawnLocation().getX()+","+offlineplayer.getBedSpawnLocation().getY()+","+offlineplayer.getBedSpawnLocation().getZ();
    	        		}
    	        		catch (Exception e) {
    	        			IOP_1_7_2 offlineplayer = new IOP_1_7_2(modifiers[0]);
    	        			return ""+offlineplayer.getBedSpawnLocation().getX()+","+offlineplayer.getBedSpawnLocation().getY()+","+offlineplayer.getBedSpawnLocation().getZ();
    	        		}
    	    		}
    			}
    			player = Bukkit.getPlayer(modifiers[0]);
    			return ""+player.getBedSpawnLocation().getX()+","+player.getBedSpawnLocation().getY()+","+player.getBedSpawnLocation().getZ();
    		}
    		return ""+player.getBedSpawnLocation().getX()+","+player.getBedSpawnLocation().getY()+","+player.getBedSpawnLocation().getZ();
		}
    	@Override 
		public String getDescription() {
			return "{bed:*username} - The location of a player's bed";
		} });
    	ISP.addPlaceholder(new Placeholder("exp") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
    		if (modifiers.length==1) {
    			if (Bukkit.getPlayer(modifiers[0])==null) {
    				try {
    	    			IOP_1_7_9 offlineplayer = new IOP_1_7_9(modifiers[0]);
    	    			return ""+offlineplayer.getTotalExperience();
    	    		}
    	    		catch (Exception e1) {
    				try {
    	    			ImprovedOfflinePlayer offlineplayer = new ImprovedOfflinePlayer(modifiers[0]);
    	    			return ""+offlineplayer.getTotalExperience();
    	        		}
    	        		catch (Exception e) {
    	        			IOP_1_7_2 offlineplayer = new IOP_1_7_2(modifiers[0]);
    	        			return ""+offlineplayer.getTotalExperience();
    	        		}
    	    		}
    			}
    			ExperienceManager expMan = new ExperienceManager(Bukkit.getPlayer(modifiers[0]));
    			return ""+expMan.getCurrentExp();
    		}
			ExperienceManager expMan = new ExperienceManager(player);
			return ""+expMan.getCurrentExp();
		}
    	@Override 
		public String getDescription() {
			return "{exp:*username} - Returns a player's experience";
		} });
    	ISP.addPlaceholder(new Placeholder("lvl") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
    		if (modifiers.length==1) {
    			if (Bukkit.getPlayer(modifiers[0])==null) {
    				try {
    	    			IOP_1_7_9 offlineplayer = new IOP_1_7_9(modifiers[0]);
    	    			ExperienceManager expMan = new ExperienceManager(player);
	        			return ""+expMan.getLevelForExp((int) Math.floor(offlineplayer.getTotalExperience()));
    	    		}
    	    		catch (Exception e1) {
    				try {
    	    			ImprovedOfflinePlayer offlineplayer = new ImprovedOfflinePlayer(modifiers[0]);
    	    			ExperienceManager expMan = new ExperienceManager(player);
    	    			return ""+expMan.getLevelForExp((int) Math.floor(offlineplayer.getTotalExperience()));
    	        		}
    	        		catch (Exception e) {
    	        			IOP_1_7_2 offlineplayer = new IOP_1_7_2(modifiers[0]);
    	        			ExperienceManager expMan = new ExperienceManager(player);
    	        			return ""+expMan.getLevelForExp((int) Math.floor(offlineplayer.getTotalExperience()));
    	        		}
    	    		}
    			}
    			ExperienceManager expMan = new ExperienceManager(Bukkit.getPlayer(modifiers[0]));
    			return ""+expMan.getLevelForExp(expMan.getCurrentExp());
    		}
			ExperienceManager expMan = new ExperienceManager(player);
			return ""+expMan.getLevelForExp(expMan.getCurrentExp());
		}
    	@Override 
		public String getDescription() {
			return "{lvl:*username} - Returns a player's experience level";
		} });
    	ISP.addPlaceholder(new Placeholder("operator") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
    		if (modifiers.length==1) {
    			return ""+Bukkit.getOfflinePlayer(modifiers[0]).isOp();
    		}
			return ""+player.isOp();
		}
    	@Override 
		public String getDescription() {
			return "{operator:*username} - Returns true if the player is Op";
		} });
    	ISP.addPlaceholder(new Placeholder("itemid") { @SuppressWarnings("deprecation")
		@Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
    		if (modifiers.length==1) {
    			if (Bukkit.getPlayer(modifiers[0])==null) {
    				try {
    	    			IOP_1_7_9 offlineplayer = new IOP_1_7_9(modifiers[0]);
    	    			return ""+offlineplayer.getItemInHand();
    	    		}
    	    		catch (Exception e1) {
    				try {
    	    			ImprovedOfflinePlayer offlineplayer = new ImprovedOfflinePlayer(modifiers[0]);
    	    			return ""+offlineplayer.getItemInHand();
    	        		}
    	        		catch (Exception e) {
    	        			IOP_1_7_2 offlineplayer = new IOP_1_7_2(modifiers[0]);
    	        			return ""+offlineplayer.getItemInHand();
    	        		}
    	    		}
    			}
    			return ""+Bukkit.getPlayer(modifiers[0]).getItemInHand().getTypeId();
    		}
			return ""+player.getItemInHand().getTypeId();
		}
    	@Override 
		public String getDescription() {
			return "{itemid:*username} - Returns the ID the player is holding";
		} });
    	ISP.addPlaceholder(new Placeholder("itemamount") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
    		if (modifiers.length==1) {
    			if (Bukkit.getPlayer(modifiers[0])==null) {
    				try {
    	    			IOP_1_7_9 offlineplayer = new IOP_1_7_9(modifiers[0]);
    	    			return ""+offlineplayer.getInventory().getItemInHand().getAmount();
    	    		}
    	    		catch (Exception e1) {
    				try {
    	    			ImprovedOfflinePlayer offlineplayer = new ImprovedOfflinePlayer(modifiers[0]);
    	    			return ""+offlineplayer.getInventory().getItemInHand().getAmount();
    	        		}
    	        		catch (Exception e) {
    	        			IOP_1_7_2 offlineplayer = new IOP_1_7_2(modifiers[0]);
    	        			return ""+offlineplayer.getInventory().getItemInHand().getAmount();
    	        		}
    	    		}
    			}
    			return ""+Bukkit.getPlayer(modifiers[0]).getItemInHand().getAmount();
    		}
			return ""+player.getItemInHand().getAmount();
		}
    	@Override 
		public String getDescription() {
			return "{itemamount:*username} - Returns the number of items a player is holding";
		} });
    	ISP.addPlaceholder(new Placeholder("itemname") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
    		if (modifiers.length==1) {
    			if (Bukkit.getPlayer(modifiers[0])==null) {
    				try {
    	    			IOP_1_7_9 offlineplayer = new IOP_1_7_9(modifiers[0]);
    	    			return ""+offlineplayer.getInventory().getItemInHand().getType().toString();
    	    		}
    	    		catch (Exception e1) {
    				try {
    	    			ImprovedOfflinePlayer offlineplayer = new ImprovedOfflinePlayer(modifiers[0]);
    	    			return ""+offlineplayer.getInventory().getItemInHand().getType().toString();
    	        		}
    	        		catch (Exception e) {
    	        			IOP_1_7_2 offlineplayer = new IOP_1_7_2(modifiers[0]);
    	        			return ""+offlineplayer.getInventory().getItemInHand().getType().toString();
    	        		}
    	    		}
    			}
    			return ""+Bukkit.getPlayer(modifiers[0]).getItemInHand().getType().toString();
    		}
			return ""+player.getItemInHand().getType().toString();
		}
    	@Override 
		public String getDescription() {
			return "{itemname:*username} - Returns the name of the item a player is holding";
		} });
    	ISP.addPlaceholder(new Placeholder("sound") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
    		if (modifiers.length>0) {
    			Location loc = location;
    			int volume = 15;
    			int pitch = 0;
    			if (modifiers.length>1) {
    				volume = Integer.parseInt(modifiers[1]);
    				if (modifiers.length>2) {
        				pitch = Integer.parseInt(modifiers[2]);
        			}
    			}
    			if (loc==null) {
    				loc = player.getLocation();
    			}
    			for(Sound current:Sound.values()) {
    				if (current.name().toLowerCase().equalsIgnoreCase(modifiers[0].toLowerCase())) {
    					player.playSound(loc, current, volume, pitch);
    					return "";
    				}
    				else if (current.name().toLowerCase().equalsIgnoreCase(modifiers[0].toLowerCase().replace("\\.", "_"))) {
    					player.playSound(loc, current, volume, pitch);
    					return "";
    				}
    			}
    			player.playSound(loc, modifiers[0], volume, pitch);
    		}
		return "";
		}
    	@Override 
		public String getDescription() {
			return "{sound:SOUND} - Plays a specific minecraft sound";
		} });
    	ISP.addPlaceholder(new Placeholder("inventory") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
    		PlayerInventory inventory;
    		if (modifiers.length==3) {
    			try {
    				inventory = Bukkit.getPlayer(modifiers[2]).getInventory();
    			}
    			catch (Exception e) {
    				try {
    	    			IOP_1_7_9 offlineplayer = new IOP_1_7_9(modifiers[0]);
    	    			inventory = offlineplayer.getInventory();
    	    		}
    	    		catch (Exception e1) {
    				try {
    	    			ImprovedOfflinePlayer offlineplayer = new ImprovedOfflinePlayer(modifiers[0]);
    	    			inventory = offlineplayer.getInventory();
	        		}
	        		catch (Exception e2) {
	        			IOP_1_7_2 offlineplayer = new IOP_1_7_2(modifiers[0]);
	        			inventory = offlineplayer.getInventory();
	        		}
    	    		}
    			}
    		}
    		else {
    			inventory = player.getInventory();
    		}
    		if (modifiers.length>1) {
    			ItemStack item = null;
    			try {
    				item = inventory.getItem(Integer.parseInt(modifiers[0]));
    			}
    			catch (Exception e) {
    				if (modifiers[0].toLowerCase().contains("enderchest")) {
    					item = player.getEnderChest().getItem(Integer.parseInt(modifiers[0].split(",")[1]));
    				}
    				else if (modifiers[0].equalsIgnoreCase("leggings")) {
    					item = inventory.getLeggings();
    				}
    				else if (modifiers[0].equalsIgnoreCase("helmet")) {
    					item = inventory.getHelmet();
    				}
    				else if (modifiers[0].equalsIgnoreCase("boots")) {
    					item = inventory.getBoots();
    				}
    				else if (modifiers[0].equalsIgnoreCase("chestplate")) {
    					item = inventory.getChestplate();
    				}
    				else if (modifiers[0].equalsIgnoreCase("hand")) {
    					item = inventory.getItemInHand();
    				}
    			}
    			if (item!=null) {
    				if (modifiers[1].equalsIgnoreCase("id")) {
    					return ""+item.getTypeId();
    				}
    				if (modifiers[1].equalsIgnoreCase("amount")) {
    					return ""+item.getAmount();
    				}
    				if (modifiers[1].equalsIgnoreCase("durability")) {
    					return ""+item.getDurability();
    				}
    				if (modifiers[1].equalsIgnoreCase("damage")) {
    					return ""+item.getDurability();
    				}
    				if (modifiers[1].equalsIgnoreCase("maxsize")) {
    					return ""+item.getMaxStackSize();
    				}
    				if (modifiers[1].equalsIgnoreCase("enchantments")) {
    					String enchantments = "";
    					for (Enchantment current:item.getEnchantments().keySet()) {
    						enchantments += current.getName()+",";
    					}
    					return enchantments.trim();
    				}
    				if (modifiers[1].equalsIgnoreCase("levels")) {
    					String enchantments = "";
    					for (Entry<Enchantment, Integer> current:item.getEnchantments().entrySet()) {
    						enchantments += current.getValue()+",";
    					}
    					return enchantments.trim();
    				}
    				if (modifiers[1].equalsIgnoreCase("name")) {
    					return item.getType().name();
    				}
    				if (modifiers[1].equalsIgnoreCase("description")) {
    					if (item.hasItemMeta()) {
    						if (item.getItemMeta().hasDisplayName()) {
    							return item.getItemMeta().getDisplayName();
    						}
    					}
    					return "";
    				}
    				if (modifiers[1].toLowerCase().contains("lore")) {
    					if (item.hasItemMeta()) {
    						if (item.getItemMeta().hasLore()) {
    							
    							return item.getItemMeta().getLore().get(Integer.parseInt(modifiers[1].split(",")[1]));
    							
    						}
    					}
    					return "";
    				}
    			}
    			}
    			return "";
		}
    	@Override 
		public String getDescription() {
			String toreturn = "{inventory:enderchest,SLOTID:...} - Specifies to work with the enderchest\n{inventory:SLOTID:...} - Specifies to work with an inventory slot\n{inventory:helmet:...} - Specifies to work with the helmet\n{inventory:leggings:...} - Specifies to work with the leggings\n{inventory:boots:...} - Specifies to work with the boots\n{inventory:chestplate:...} - Specifies to work with the chestplate\n{inventory:hand:...} - Specifies to work with the hand";
			toreturn+="\n{inventory:...:id} - Returns the ID of the item\n{inventory:...:amount} - Returns the amount of the item\n{inventory:...:durability} - Returns the durability of the item\n{inventory:...:maxsize} - Returns the maxsize of the itemstack\n{inventory:...:name} - Returns the name of the item\n{inventory:...:enchantments} - Returns the enchantments of the item\n{inventory:...:levels} - Returns the enchantment levels of the item\n{inventory:...:lore} - Returns the lore for the item";
			return toreturn;
    	} });
    	ISP.addPlaceholder(new Placeholder("durability") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
    		if (modifiers.length==1) {
    			if (Bukkit.getPlayer(modifiers[0])==null) {
    				try {
    	    			IOP_1_7_9 offlineplayer = new IOP_1_7_9(modifiers[0]);
    	    			return ""+offlineplayer.getInventory().getItemInHand().getDurability();
    	    		}
    	    		catch (Exception e1) {
    				try {
    	    			ImprovedOfflinePlayer offlineplayer = new ImprovedOfflinePlayer(modifiers[0]);
    	    			return ""+offlineplayer.getInventory().getItemInHand().getDurability();
    	        		}
    	        		catch (Exception e) {
    	        			IOP_1_7_2 offlineplayer = new IOP_1_7_2(modifiers[0]);
    	        			return ""+offlineplayer.getInventory().getItemInHand().getDurability();
    	        		}
    	    		}
    			}
    			return ""+Bukkit.getPlayer(modifiers[0]).getInventory().getItemInHand().getDurability();
    		}
			return ""+player.getInventory().getItemInHand().getDurability();
		}
    	@Override 
		public String getDescription() {
			return "{durability:*username} - Returns the item durability for what a player is holding";
		} });
    	ISP.addPlaceholder(new Placeholder("gamemode") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
    		if (modifiers.length==1) {
    			if (Bukkit.getPlayer(modifiers[0])==null) {
    				try {
    	    			IOP_1_7_9 offlineplayer = new IOP_1_7_9(modifiers[0]);
    	    			return ""+offlineplayer.getGameMode().toString();
    	    		}
    	    		catch (Exception e1) {
    				try {
    	    			ImprovedOfflinePlayer offlineplayer = new ImprovedOfflinePlayer(modifiers[0]);
    	    			return ""+offlineplayer.getGameMode().toString();
    	        		}
    	        		catch (Exception e) {
    	        			IOP_1_7_2 offlineplayer = new IOP_1_7_2(modifiers[0]);
    	        			return ""+offlineplayer.getGameMode().toString();
    	        		}
    	    		}
    			}
    			return ""+Bukkit.getPlayer(modifiers[0]).getGameMode().toString();
    		}
			return ""+player.getGameMode().toString();
		}
    	@Override 
		public String getDescription() {
			return "{gamemode:*username} - Returns the player's gamemode";
		} });
    	ISP.addPlaceholder(new Placeholder("direction") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
    		if (modifiers.length==1) {
    			if (Bukkit.getPlayer(modifiers[0])==null) {
    				int degrees = 0;
    				try {
    	    			IOP_1_7_9 offlineplayer = new IOP_1_7_9(modifiers[0]);
    	                degrees = (Math.round(offlineplayer.getLocation().getYaw()) + 270) % 360;
    	    		}
    	    		catch (Exception e1) {
    	    		try {
    					ImprovedOfflinePlayer offlineplayer = new ImprovedOfflinePlayer(modifiers[0]);
    		            degrees = (Math.round(offlineplayer.getLocation().getYaw()) + 270) % 360;
    	    		}
    	    		catch (Exception e) {
    	    			IOP_1_7_2 offlineplayer = new IOP_1_7_2(modifiers[0]);
    	                degrees = (Math.round(offlineplayer.getLocation().getYaw()) + 270) % 360;
    	    		}
    	    		}
    				String tempstr = "null";
	                if (degrees <= 22)  {tempstr="WEST";}
	                else if (degrees <= 67) {tempstr="NORTHWEST";}
	                else if (degrees <= 112) {tempstr="NORTH";}
	                else if (degrees <= 157) {tempstr="NORTHEAST";}
	                else if (degrees <= 202) {tempstr="EAST";}
	                else if (degrees <= 247) {tempstr="SOUTHEAST";}
	                else if (degrees <= 292) {tempstr="SOUTH";}
	                else if (degrees <= 337) {tempstr="SOUTHWEST";}
	                else if (degrees <= 359) {tempstr="WEST";}
	                return tempstr;
    			}
    			player = Bukkit.getPlayer(modifiers[0]);
    			String tempstr = "null";
    			int degrees = (Math.round(player.getLocation().getYaw()) + 270) % 360;
                if (degrees <= 22)  {tempstr="WEST";}
                else if (degrees <= 67) {tempstr="NORTHWEST";}
                else if (degrees <= 112) {tempstr="NORTH";}
                else if (degrees <= 157) {tempstr="NORTHEAST";}
                else if (degrees <= 202) {tempstr="EAST";}
                else if (degrees <= 247) {tempstr="SOUTHEAST";}
                else if (degrees <= 292) {tempstr="SOUTH";}
                else if (degrees <= 337) {tempstr="SOUTHWEST";}
                else if (degrees <= 359) {tempstr="WEST";}
                return tempstr;
    		}
    		String tempstr = "null";
			int degrees = (Math.round(player.getLocation().getYaw()) + 270) % 360;
            if (degrees <= 22)  {tempstr="WEST";}
            else if (degrees <= 67) {tempstr="NORTHWEST";}
            else if (degrees <= 112) {tempstr="NORTH";}
            else if (degrees <= 157) {tempstr="NORTHEAST";}
            else if (degrees <= 202) {tempstr="EAST";}
            else if (degrees <= 247) {tempstr="SOUTHEAST";}
            else if (degrees <= 292) {tempstr="SOUTH";}
            else if (degrees <= 337) {tempstr="SOUTHWEST";}
            else if (degrees <= 359) {tempstr="WEST";}
            return tempstr;
		}
    	@Override 
		public String getDescription() {
			return "{direction:*username} - Returns the player's facing direction";
		} });
    	ISP.addPlaceholder(new Placeholder("health") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
    		if (modifiers.length==1) {
    			if (Bukkit.getPlayer(modifiers[0])==null) {
    				try {
    	    			IOP_1_7_9 offlineplayer = new IOP_1_7_9(modifiers[0]);
    	    			return String.valueOf(offlineplayer.getHealthInt());
    	    		}
    	    		catch (Exception e1) {
    				try {
    	    			ImprovedOfflinePlayer offlineplayer = new ImprovedOfflinePlayer(modifiers[0]);
    	    			return String.valueOf(offlineplayer.getHealthInt());
    	        		}
    	        		catch (Exception e) {
    	        			IOP_1_7_2 offlineplayer = new IOP_1_7_2(modifiers[0]);
    	        			return String.valueOf(offlineplayer.getHealthInt());
    	        		}
    	    		}
    			}
    			return ""+Bukkit.getPlayer(modifiers[0]).getHealth();
    		}
			return ""+player.getHealth();
		}
    	@Override 
		public String getDescription() {
			return "{health:*username} - Returns the player's health";
		} });
    	ISP.addPlaceholder(new Placeholder("biome") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
    		if (modifiers.length==1) {
    			Location loc = ISP.getloc(modifiers[0], player);
    			return loc.getWorld().getBiome(loc.getBlockX(), loc.getBlockZ()).toString();
    		}
    		return player.getWorld().getBiome(player.getLocation().getBlockX(), player.getLocation().getBlockZ()).toString();
		}
    	@Override 
		public String getDescription() {
			return "{biome:*location} - Returns the biome at a location";
		} });
    	ISP.addPlaceholder(new Placeholder("location") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
    		if (modifiers.length==1) {
    			Location loc = ISP.getloc(modifiers[0], player);
    			return loc.getWorld().getName()+","+loc.getBlockX()+","+loc.getBlockY()+","+loc.getBlockZ();
    		}
    		Location loc = player.getLocation();
    		return loc.getWorld().getName()+","+loc.getBlockX()+","+loc.getBlockY()+","+loc.getBlockZ();
		}
    	@Override 
		public String getDescription() {
			return "{location:*username} - Returns a player's location in the format W,X,Y,Z";
		} });
    	ISP.addPlaceholder(new Placeholder("storm") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
    		if (modifiers.length==1) {
    			Location loc = ISP.getloc(modifiers[0], player);
    			return ""+loc.getWorld().hasStorm();
    		}
    			return ""+player.getLocation().getWorld().hasStorm();
		}
    	@Override 
		public String getDescription() {
			return "{storm:*location} - Returns true if there is a storm at a location";
		} });
    	ISP.addPlaceholder(new Placeholder("thunder") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
    		if (modifiers.length==1) {
    			Location loc = ISP.getloc(modifiers[0], player);
    			return ""+loc.getWorld().isThundering();
    		}
    			return ""+player.getLocation().getWorld().isThundering();
		}
    	@Override 
		public String getDescription() {
			return "{thunder:*location} - Returns true if there is thunder at a location";
		} });
    	ISP.addPlaceholder(new Placeholder("x") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
    		if (modifiers.length==1) {
				return String.valueOf(Math.floor(ISP.getloc(modifiers[0], player).getX()));
    		}
			return String.valueOf(Math.floor(player.getLocation().getX()));
		}
    	@Override 
		public String getDescription() {
			return "{x} - Returns a player's x coordinate";
		} });
    	ISP.addPlaceholder(new Placeholder("y") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
    		if (modifiers.length==1) {
				return String.valueOf(Math.floor(ISP.getloc(modifiers[0], player).getZ()));
    		}
			return String.valueOf(Math.floor(player.getLocation().getY()));
		}
    	@Override 
		public String getDescription() {
			return "{y} - Returns a player's y coordinate";
		} });
    	ISP.addPlaceholder(new Placeholder("z") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
    		if (modifiers.length==1) {
				return String.valueOf(Math.floor(ISP.getloc(modifiers[0], player).getZ()));
    		}
			return String.valueOf(Math.floor(player.getLocation().getZ()));
		}
    	@Override 
		public String getDescription() {
			return "{z} - Returns a player's z coordinate";
		} });
    	ISP.addPlaceholder(new Placeholder("sneaking") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
    		if (modifiers.length==1) {
    			return ""+Bukkit.getPlayer(modifiers[0]).isSneaking();
    		}
			return ""+player.isSneaking();
		}
    	@Override 
		public String getDescription() {
			return "{sneaking} - Returns true if the player is sneaking";
		} });
    	ISP.addPlaceholder(new Placeholder("itempickup") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
    		if (modifiers.length==1) {
    			return ""+Bukkit.getPlayer(modifiers[0]).getCanPickupItems();
    		}
			return ""+player.getCanPickupItems();
		}
    	@Override 
		public String getDescription() {
			return "{itempickup:*username} - Returns true if a player can pick up items";
		} });
    	ISP.addPlaceholder(new Placeholder("flying") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
    		if (modifiers.length==1) {
    			return ""+Bukkit.getPlayer(modifiers[0]).getAllowFlight();
    		}
			return ""+player.getAllowFlight();
		}
    	@Override 
		public String getDescription() {
			return "{flying:*username} - Returns true if the player is flying";
		} });
    	ISP.addPlaceholder(new Placeholder("grounded") { @SuppressWarnings("deprecation")
		@Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
    		if (modifiers.length==1) {
    			if (Bukkit.getPlayer(modifiers[0])==null) {
    				try {
    	    			IOP_1_7_9 offlineplayer = new IOP_1_7_9(modifiers[0]);
    	    			return ""+offlineplayer.getIsOnGround();
    	    		}
    	    		catch (Exception e1) {
    				try {
    	    			ImprovedOfflinePlayer offlineplayer = new ImprovedOfflinePlayer(modifiers[0]);
    	    			return ""+offlineplayer.getIsOnGround();
    	        		}
    	        		catch (Exception e) {
    	        			IOP_1_7_2 offlineplayer = new IOP_1_7_2(modifiers[0]);
    	        			return ""+offlineplayer.getIsOnGround();
    	        		}
    	    		}
    			}
    			return ""+Bukkit.getPlayer(modifiers[0]).isOnGround();
    		}
			return ""+player.isOnGround();
		}
    	@Override 
		public String getDescription() {
			return "{grounded:*username} - Returns true if the player is on the ground";
		} });
    	ISP.addPlaceholder(new Placeholder("blocking") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
    		if (modifiers.length==1) {
    			return ""+Bukkit.getPlayer(modifiers[0]).isBlocking();
    		}
			return ""+player.isBlocking();
		}
    	@Override 
		public String getDescription() {
			return "{blocking:*username} - Returns true if the player is blocking";
		} });
    	ISP.addPlaceholder(new Placeholder("passenger") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
    		if (modifiers.length==1) {
    			player =Bukkit.getPlayer(modifiers[0]);
    			if (player.getVehicle()==null) {
    				return "false";
    			}
    			return ""+player.getVehicle().toString();
    		}
			if (player.getVehicle()==null) {
				return "false";
			}
			return ""+player.getVehicle().toString();
		}
    	@Override 
		public String getDescription() {
			return "{passenger:*username} - Returns the vehicle the player is in or false";
		} });
    	ISP.addPlaceholder(new Placeholder("maxhealth") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
    		if (modifiers.length==1) {
    			return ""+Bukkit.getPlayer(modifiers[0]).getMaxHealth();
    		}
			return ""+player.getMaxHealth();
		}
    	@Override 
		public String getDescription() {
			return "{maxhealth:*username} - Returns a player's max health";
		} });
    	ISP.addPlaceholder(new Placeholder("maxair") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
    		if (modifiers.length==1) {
    			return ""+Bukkit.getPlayer(modifiers[0]).getMaximumAir();
    		}
			return ""+player.getMaximumAir();
		}
    	@Override 
		public String getDescription() {
			return "{maxair:*username} - Returns a player's max air";
		} });
    	ISP.addPlaceholder(new Placeholder("age") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
    		if (modifiers.length==1) {
    			return Integer.toString((Bukkit.getPlayer(modifiers[0]).getTicksLived()/20));
    		}
    		return Integer.toString(player.getTicksLived()/20);
		}
    	@Override 
		public String getDescription() {
			return "{age:*username} - Returns the time since the player joined in seconds.";
		} });
    	ISP.addPlaceholder(new Placeholder("compass") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
    		if (modifiers.length==1) {
    			player = Bukkit.getPlayer(modifiers[0]);
    			return ""+player.getCompassTarget().getX()+","+player.getCompassTarget().getY()+","+player.getCompassTarget().getZ();
    		}
    		return ""+player.getCompassTarget().getX()+","+player.getCompassTarget().getY()+","+player.getCompassTarget().getZ();
		}
    	@Override 
		public String getDescription() {
			return "{compass:*username} - The location a player's compass points";
		} });
    	ISP.addPlaceholder(new Placeholder("sleeping") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
    		if (modifiers.length==1) {
    			if (Bukkit.getPlayer(modifiers[0])==null) {
    				try {
    	    			IOP_1_7_9 offlineplayer = new IOP_1_7_9(modifiers[0]);
    	    			return ""+offlineplayer.getIsSleeping();
    	    		}
    	    		catch (Exception e1) {
    				try {
    	    			ImprovedOfflinePlayer offlineplayer = new ImprovedOfflinePlayer(modifiers[0]);
    	    			return ""+offlineplayer.getTotalExperience();
    	        		}
    	        		catch (Exception e) {
    	        			IOP_1_7_2 offlineplayer = new IOP_1_7_2(modifiers[0]);
    	        			return ""+offlineplayer.getIsSleeping();
    	        		}
    	    		}
    			}
    			return ""+Bukkit.getPlayer(modifiers[0]).isSleeping();
    		}
			return ""+player.isSleeping();
		}
    	@Override 
		public String getDescription() {
			return "{sleeping:*username} - Returns true if player is using a bed";
		} });
    	ISP.addPlaceholder(new Placeholder("dead") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
    		if (modifiers.length==1) {
    			return ""+Bukkit.getPlayer(modifiers[0]).isDead();
    		}
			return ""+player.isDead();
		}
    	@Override 
		public String getDescription() {
			return "{dead:*username} - Returns true/false if player is dead/alive";
		} });
    	ISP.addPlaceholder(new Placeholder("whitelisted") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
    		if (modifiers.length==1) {
    			if (Bukkit.getPlayer(modifiers[0])==null) {
    				return ""+Bukkit.getOfflinePlayer(modifiers[0]).isWhitelisted();
    			}
    			return ""+Bukkit.getPlayer(modifiers[0]).isWhitelisted();
    		}
			return ""+player.isWhitelisted();
		}
    	@Override 
		public String getDescription() {
			return "{whitelisted:*username} - Return true if player is whitelisted";
		} });
    	ISP.addPlaceholder(new Placeholder("world") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
    		if (modifiers.length==1) {
    			return ISP.getloc(modifiers[0], player).getWorld().getName();
    		}
			return ""+player.getWorld().getName();
		}
    	@Override 
		public String getDescription() {
			return "{world:*username} - Returns the name of the world";
		} });
    	ISP.addPlaceholder(new Placeholder("ip") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
    		if (modifiers.length==1) {
    			player = Bukkit.getPlayer(modifiers[0]);
    			return player.getAddress().getAddress().toString().split("/")[(player.getAddress().toString().split("/").length)-1].split(":")[0];
    		}
    		return player.getAddress().getAddress().toString().split("/")[(player.getAddress().toString().split("/").length)-1].split(":")[0];
		}
    	@Override 
		public String getDescription() {
			return "{ip:*username} - Returns the player's IP address";
		} });
    	ISP.addPlaceholder(new Placeholder("wrap") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
    		if (modifiers.length==2) {
    			int a = Integer.parseInt(modifiers[0]);
    			int b = Integer.parseInt(modifiers[1]);
    			return ""+a%b;
    		}
			return "0";
		} });
    	ISP.addPlaceholder(new Placeholder("isclick") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
    		return ""+ISP.getClicked();
		}
    	@Override 
		public String getDescription() {
			return "{isclick} - right, left or false depending on if the update was caused by a click event";
		} });
    	ISP.addPlaceholder(new Placeholder("line1") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
    		Sign sign = (Sign) (location.getBlock().getState());
    		String line = sign.getLine(0);
    		if (line.equals("{line1}")) {
    			return "";
    		}
    		if (line.contains("{line1}")) {
    			return "";
    		}
    		if (modifiers.length==0) {
    			return line;
    		}
    		return "";
		} });
    	ISP.addPlaceholder(new Placeholder("line2") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
    		Sign sign = (Sign) (location.getBlock().getState());
    		String line = sign.getLine(1);
    		if (line.equals("{line2}")) {
    			return "";
    		}
    		if (line.contains("{line2}")) {
    			return "";
    		}
    		if (modifiers.length==0) {
    			return line;
    		}
    		return "";
		} });
    	ISP.addPlaceholder(new Placeholder("line3") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
    		Sign sign = (Sign) (location.getBlock().getState());
    		String line = sign.getLine(2);
    		if (line.equals("{line3}")) {
    			return "";
    		}
    		if (line.contains("{line3}")) {
    			return "";
    		}
    		if (modifiers.length==0) {
    			return line;
    		}
    		return "";
		} });
    	ISP.addPlaceholder(new Placeholder("line4") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
    		Sign sign = (Sign) (location.getBlock().getState());
    		String line = sign.getLine(3);
    		if (line.equals("{line4}")) {
    			return "";
    		}
    		if (line.contains("{line4}")) {
    			return "";
    		}
    		if (modifiers.length==0) {
    			return line;
    		}
    		return "";
		} });
    	ISP.addPlaceholder(new Placeholder("blockloc") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
    		return location.getWorld().getName()+","+location.getBlockX()+","+location.getBlockY()+","+location.getBlockZ();
		} });
    	ISP.addPlaceholder(new Placeholder("uses") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
    		if (modifiers.length==1) {
    			int maxclicks = Integer.parseInt(modifiers[0]);
    			for (int i = 0;i<ISP.list.size();i++) {
	        		if (ISP.list.get(i).equals(location)&&ISP.players.get(i).equals(player.getName())) {
	        			int myclicks = ISP.clicks.get(i);
	        			if (myclicks > maxclicks) {
	        				return ""+(myclicks%maxclicks);
	        			}
	        			else {
	        				return ""+myclicks;
	        			}
	        		}
	        	}
    			return "0";
    		}
    		for (int i = 0;i<ISP.list.size();i++) {
        		if (ISP.list.get(i).equals(location)&&ISP.players.get(i).equals(player.getName())) {
        			return ""+ISP.clicks.get(i);
        		}
        	}
			return "0";
		}
    	@Override 
		public String getDescription() {
			return "{uses} - returns the number of times the sign has been used by a player";
		} });
	}
}
