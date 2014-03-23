package com.empcraft;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Map.Entry;
import java.util.logging.Logger;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;


public final class InSignsExtended extends JavaPlugin implements Listener {
	public boolean isenabled = false;
	private static final Logger log = Logger.getLogger("Minecraft");
    public static Economy econ = null;
    public static Permission perms = null;
    public static Chat chat = null;
    public static int counter0 = 0;
    public static int counter = 0;
    public static int counter2 = 0;
    public static Map<String, Object> globals = new HashMap<String, Object>();
    public int recursion = 0;
	public static List<Location> list = new ArrayList();
	public static List<String> players = new ArrayList();
	public static List<Integer> clicks = new ArrayList();
	public long timerstart = 0;
	public boolean islagging = false;
	public int timerlast = 0;
	private static ProtocolManager protocolmanager;
	InSignsExtended plugin;
	InSignsFeature isf;
	public ScriptEngine engine = (new ScriptEngineManager()).getEngineByName("JavaScript");
	
	public Location getloc(String string,Player user) {
		if (string.contains(",")==false) {
			Player player = Bukkit.getPlayer(string);
			if (player!=null) {
				return player.getLocation();
			}
			else {
				try {
					ImprovedOfflinePlayer offlineplayer = new ImprovedOfflinePlayer(string);
					if (offlineplayer.exists()) {
						return offlineplayer.getLocation();
					}
					else {
						World world = Bukkit.getWorld(string);
						if (world!=null) {
							return world.getSpawnLocation();
						}
					}
				}
				catch (Exception e) {
					World world = Bukkit.getWorld(string);
					if (world!=null) {
						return world.getSpawnLocation();
					}
				}
			}
		}
		else {
			String[] mysplit = string.split(",");
			World world = Bukkit.getWorld(mysplit[0]);
			if (world!=null) {
				double x;double y;double z;
				if (mysplit.length==4) {
					try { x = Double.parseDouble(mysplit[1]);} catch (Exception e) {x=world.getSpawnLocation().getX();}
					try { y = Double.parseDouble(mysplit[2]);} catch (Exception e) {y=world.getSpawnLocation().getY();}
					try { z = Double.parseDouble(mysplit[3]);} catch (Exception e) {z=world.getSpawnLocation().getZ();}
					return new Location(world, x, y, z);
				}
			}
			else {
				return null;
			}
		}
		return null;
	}
    public String fphs(String line, Player user, Player sender, Boolean elevation,Location interact) {
    	String[] mysplit = line.substring(1,line.length()-1).split(":");
    	if (mysplit.length==2) {
    		if ((Bukkit.getPlayer(mysplit[1])!=null)) {
				user = Bukkit.getPlayer(mysplit[1]);
				line = StringUtils.join(mysplit,":").replace(":"+mysplit[1],"");
        	}
    	}
    	if (line.contains("{setgroup:")) {
    		boolean hasperm = false;
    		if (sender==null) {
    			hasperm = true;
    		}
    		else if (elevation) {
    			hasperm = true;
    		}
    		if (hasperm) {
    		if ((mysplit.length == 2)&&(user!=null)) {
    			perms.playerAddGroup(user, mysplit[1]);
    			if (perms.getPrimaryGroup(user).equals(mysplit[1])==false) {
        			perms.playerRemoveGroup(user, perms.getPrimaryGroup(user));
        			perms.playerRemoveGroup(user, mysplit[1]);
        			perms.playerAddGroup(user, mysplit[1]);
    			}
    		}
    		else if ((mysplit.length == 3)) {
    			if ((Bukkit.getPlayer(mysplit[1])!=null)) {
    				perms.playerAddGroup(Bukkit.getPlayer(mysplit[1]), mysplit[2]);
    				if (perms.getPrimaryGroup(Bukkit.getPlayer(mysplit[1])).equals(mysplit[2])==false) {
            			perms.playerRemoveGroup(Bukkit.getPlayer(mysplit[1]), perms.getPrimaryGroup(user));
            			perms.playerRemoveGroup(Bukkit.getPlayer(mysplit[1]), mysplit[2]);
            			perms.playerAddGroup(Bukkit.getPlayer(mysplit[1]), mysplit[2]);
    				}
    			}
    			
    		}
    		else if (mysplit.length == 4){
    			try {
    				perms.playerAddGroup(Bukkit.getWorld(mysplit[3]),mysplit[1], mysplit[2]);
    				if (perms.getPrimaryGroup(Bukkit.getWorld(mysplit[3]),mysplit[1]).equals(mysplit[2])==false) {
        				perms.playerRemoveGroup(Bukkit.getWorld(mysplit[3]),mysplit[1],perms.getPrimaryGroup(Bukkit.getWorld(mysplit[3]),mysplit[1]));
        				perms.playerRemoveGroup(Bukkit.getWorld(mysplit[3]),mysplit[1],mysplit[2]);
        				perms.playerAddGroup(Bukkit.getWorld(mysplit[3]),mysplit[1], mysplit[2]);
    				}
    			}
    			catch (Exception e) {
    				System.out.println(e);
    			}
    		}
    	}
    		return "null";
    	}
    	else if (line.contains("{delsub:")) {
    		boolean hasperm = false;
    		if (sender==null) {
    			hasperm = true;
    		}
    		else if (elevation) {
    			hasperm = true;
    		}
    		if (hasperm) {
    		if ((mysplit.length == 2)&&(user!=null)) {
    			perms.playerRemoveGroup(user, mysplit[1]);
    		}
    		else if ((mysplit.length == 3)) {
    			if ((Bukkit.getPlayer(mysplit[1])!=null)) {
    				perms.playerRemoveGroup(Bukkit.getPlayer(mysplit[1]), mysplit[1]);
    			}
    		}
    		else if (mysplit.length == 4){
    			try {
    				perms.playerRemoveGroup(Bukkit.getWorld(mysplit[3]),mysplit[1], mysplit[2]);
    			}
    			catch (Exception e) {
    				System.out.println(e);
    			}
    		}
    	}
    		return "null";
    	}
    	else if (line.contains("{delperm:")) {
    		boolean hasperm = false;
    		if (sender==null) {
    			hasperm = true;
    		}
    		else if (elevation) {
    			hasperm = true;
    		}
    		if (hasperm) {
    		if ((mysplit.length == 2)&&(sender!=null)) {
    			perms.playerRemove(user, mysplit[1]);
    		}
    		else if ((mysplit.length == 3)) {
    			if ((Bukkit.getPlayer(mysplit[1])!=null)) {
    				perms.playerRemove(Bukkit.getPlayer(mysplit[1]), mysplit[1]);
    			}
    		}
    		else if (mysplit.length == 4){
    			try {
    				perms.playerRemove(Bukkit.getWorld(mysplit[3]),mysplit[1], mysplit[2]);
    			}
    			catch (Exception e) {
    				System.out.println(e);
    			}
    		}
    	}
    		return "null";
    	}
    	else if (line.contains("{prefix:")) {
    		boolean hasperm = false;
    		if (sender==null) {
    			hasperm = true;
    		}
    		else if (elevation) {
    			hasperm = true;
    		}
    		if (hasperm) {
    		if ((mysplit.length == 2)) {
    			if ((Bukkit.getPlayer(mysplit[1])!=null)) {
    				return chat.getPlayerPrefix(Bukkit.getPlayer(mysplit[1]));
    			}
    			else {
    				chat.setPlayerPrefix(user, mysplit[1]);
    			}
    		}
    		if ((mysplit.length >= 3)) {
    			if ((Bukkit.getPlayer(mysplit[1])!=null)) {
    				chat.setPlayerPrefix(Bukkit.getPlayer(mysplit[1]), mysplit[2]);
    			}
    		}
    	}
    		return "null";
    	}
    	else if (line.contains("{suffix:")) {
    		boolean hasperm = false;
    		if (sender==null) {
    			hasperm = true;
    		}
    		else if (elevation) {
    			hasperm = true;
    		}
    		if (hasperm) {
    		if ((mysplit.length == 2)) {
    			if ((Bukkit.getPlayer(mysplit[1])!=null)) {
    				return chat.getPlayerSuffix(Bukkit.getPlayer(mysplit[1]));
    			}
    			else {
    				chat.setPlayerSuffix(user, mysplit[1]);
    			}
    		}
    		if ((mysplit.length == 3)) {
    			if ((Bukkit.getPlayer(mysplit[1])!=null)) {
    				chat.setPlayerSuffix(Bukkit.getPlayer(mysplit[1]), mysplit[2]);
    			}
    		}
    		else if (mysplit.length == 4){
    			try {
    				chat.setPlayerSuffix(Bukkit.getWorld(mysplit[3]),mysplit[1], mysplit[2]);
    			}
    			catch (Exception e) {
    				System.out.println(e);
    			}
    		}
    	}
    		return "null";
    	}
    	else if (line.contains("{rand:")) {
    		Random random = new Random();
    		return (""+random.nextInt(Integer.parseInt(mysplit[1])));
    	}
    	else if (line.contains("{msg:")) {
    		return getmsg(mysplit[1]);
    	}
    	else if (line.contains("{range:")) {
    		String mylist = "";
    		int start = 0;
    		int stop = 0;
    		if (mysplit.length==2) {
    			stop = Integer.parseInt(mysplit[1]);
    		}
    		else if (mysplit.length==3) {
    			start = Integer.parseInt(mysplit[1]);
    			stop = Integer.parseInt(mysplit[2]);
    		}
    		if (stop-start<512) {
    		for(int i = start; i <= stop; i++) {
    			mylist+=i+",";
    		}
    		}
    		return mylist.substring(0,mylist.length()-1);
    	}
    	else if (line.contains("{matchplayer:")) {
    		List<Player> matches = getServer().matchPlayer(mysplit[1]);
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
    	else if (line.contains("{matchgroup:")) {
    		return matchgroup(mysplit[1]);
    	}
    	else if (line.contains("{index:")) {
    		return mysplit[1].split(",")[Integer.parseInt(mysplit[2])];
    	}
    	else if (line.contains("{setindex:")) {
    		String[] mylist = mysplit[1].split(",");
    		String newlist = "";
    		
    		int myindex = Integer.parseInt(mysplit[2]);
    		for(int i = 0; i < mylist.length; i++) {
    			if (i==myindex) {
    				newlist+=mysplit[3]+",";
    			}
    			else {
    				newlist+=mylist[i]+",";
    			}
    		}
    		return newlist.substring(0,newlist.length()-1);
    	}
    	else if (line.contains("{delindex:")) {
    		String[] mylist = mysplit[1].split(",");
    		String newlist = "";
    		int myindex = Integer.parseInt(mysplit[2]);
    		for(int i = 0; i < mylist.length; i++) {
    			if (i==myindex) {
    				
    			}
    			else {
    				newlist+=mylist[i]+",";
    			}
    		}
    		return newlist.substring(0,newlist.length()-1);
    	}
    	else if (line.contains("{sublist:")) {
    		String[] mylist = mysplit[1].split(",");
    		String newlist = "";
    		int i1 = Integer.parseInt(mysplit[2]);
    		int i2 = Integer.parseInt(mysplit[3]);
    		for(int i = 0; i < mylist.length; i++) {
    			if ((i>=i1)&&(i<=i2)) {
    				newlist+=mylist[i]+",";
    			}
    		}
    		return newlist.substring(0,newlist.length()-1);
    	}
    	else if (line.contains("{getindex:")) {
    		String[] mylist = mysplit[1].split(",");
    		String newlist = "";
    		for(int i = 0; i < mylist.length; i++) {
    			if (mylist[i].equals(mysplit[2])) {
    				newlist+=i+",";
    			}
    		}
    		if (newlist.equals("")) {
    			return "null";
    		}
    		return newlist.substring(0,newlist.length()-1);
    	}
    	else if (line.contains("{listhas:")) {
    		String[] mylist = mysplit[1].split(",");
    		for(int i = 0; i < mylist.length; i++) {
    			if (mylist[i].equals(mysplit[2])) {
    				return "true";
    			}
    		}
    		return "false";
    	}
    	else if (line.contains("{contains:")) {
    		if (mysplit[1].contains(mysplit[2])) {
    			return "true";
    		}
    		return "false";
    	}
    	else if (line.contains("{substring:")) {
    		return mysplit[1].substring(Integer.parseInt(mysplit[2]), Integer.parseInt(mysplit[3]));
    	}
    	else if (line.contains("{length:")) {
    		if (mysplit[1].contains(",")) {
    			return ""+mysplit[1].split(",").length;
    		}
    		else {
    			return ""+mysplit[1].length();
    		}
    	}
    	else if (line.contains("{split:")) {
    		return mysplit[1].replace(mysplit[2],",");
    	}
    	else if (line.contains("{hasperm:")) {
    		if (user==null) {
    			return "true";
    		}
    		else if (mysplit.length==3) {
    			return ""+perms.playerHas(user.getWorld(),mysplit[1], mysplit[2]);
    		}
    		else if (checkperm(user,mysplit[1])) {
    			return "true";
    		}
    		return "false";
    	}
    	else if (line.contains("{randchoice:")) {
    		String[] mylist = mysplit[1].split(",");
    		Random random = new Random();
    		return mylist[random.nextInt(mylist.length-1)];
    	}
    	else if (line.contains("{worldtype:")) {
    		Location loc = getloc(mysplit[1], user);
    		return ""+loc.getWorld().getWorldType().getName();
    	}
    	else if (line.contains("{listreplace:")) {
    		String[] mylist = mysplit[1].split(",");
    		String newlist = "";
    		for(int i = 0; i < mylist.length; i++) {
    			if (mylist[i].equals(mysplit[2])) {
    				newlist+=mysplit[3]+",";
    			}
    		}
    		if (newlist.equals("")) {
    			return "null";
    		}
    		return newlist.substring(0,newlist.length()-1);
    	}
    	else if (line.contains("{worldticks}")) {
    		return Long.toString(user.getWorld().getTime());
    	}
    	else if (line.contains("{worldticks:")) {
    		Location loc = getloc(mysplit[1], user);
    		return Long.toString(loc.getWorld().getTime());
    	}
    	else if (line.contains("{time}")) {
    		Double time = user.getWorld().getTime() / 1000.0;
    		if (time>24) { time-=24; }
    		String hr = ""+time.intValue() + 6;
    		String min = ""+((int) (60*(time%1)));
    		if (min.length()==1) {
    			min = "0"+min;
    		}
    		if (hr.length()==1) {
    			hr = "0"+hr;
    		}
    		return ""+hr+":"+min;
    	}
    	else if (line.contains("{time:")) {
    		Location loc = getloc(mysplit[1], user);
    		Double time = loc.getWorld().getTime() / 1000.0;
    		if (time>24) { time-=24; }
    		String hr = ""+time.intValue() + 6;
    		String min = ""+((int) (60*(time%1)));
    		if (min.length()==1) {
    			min = "0"+min;
    		}
    		if (hr.length()==1) {
    			hr = "0"+hr;
    		}
    		return ""+hr+":"+min;
    	}
    	else if (line.contains("{time12}")) {
    		String ampm = " AM";
    		Double time = user.getWorld().getTime() / 1000.0;
    		if (time>24) { time-=24; }
    		if (time+6>12) {
    			time-=12;
    			ampm = " PM";
    		}
    		String hr = ""+time.intValue() + 6;
    		String min = ""+((int) (60*(time%1)));
    		if (min.length()==1) {
    			min = "0"+min;
    		}
    		return ""+hr+":"+min+ampm;
    	}
    	else if (line.contains("{time12:")) {
    		String ampm = " AM";
    		Location loc = getloc(mysplit[1], user);
    		Double time = loc.getWorld().getTime() / 1000.0;
    		if (time>24) { time-=24; }
    		if (time+6>12) {
    			ampm = " PM";
    			time-=12;
    		}
    		String hr = ""+time.intValue() + 6;
    		String min = ""+((int) (60*(time%1)));
    		if (min.length()==1) {
    			min = "0"+min;
    		}
    		return ""+hr+":"+min+ampm;
    	}
    	else if (line.contains("{replace:")) {
    		return mysplit[1].replace(mysplit[2], mysplit[3]);
    	}
    	else if (line.contains("{config:")) {
    		return getConfig().getString(mysplit[1]);
    	}
    	else if (line.contains("{structures:")) {
    		Location loc = getloc(mysplit[1], user);
    		return loc.getWorld().canGenerateStructures()+"";
    	}
    	else if (line.contains("{structures}")) {
    		return ""+user.getWorld().canGenerateStructures();
    	}
    	else if (line.contains("{autosave}")) {
    		return ""+user.getWorld().isAutoSave();
    	}
    	else if (line.contains("{autosave:")) {
    		Location loc = getloc(mysplit[1], user);
    		return loc.getWorld().isAutoSave()+"";
    	}
    	else if (line.contains("{animals:")) {
    		Location loc = getloc(mysplit[1], user);
    		return loc.getWorld().getAllowAnimals()+"";
    	}
    	else if (line.contains("{animals}")) {
    		return ""+user.getWorld().getAllowAnimals();
    	}
    	else if (line.contains("{monsters:")) {
    		Location loc = getloc(mysplit[1], user);
    		return loc.getWorld().getAllowMonsters()+"";
    	}
    	else if (line.contains("{monsters}")) {
    		return ""+user.getWorld().getAllowMonsters();
    	}
    	else if (line.contains("{online:")) {
    		Location loc = getloc(mysplit[1], user);
    		return ""+loc.getWorld().getPlayers();
    	}
    	else if (line.contains("{colors}")) {
    		return "&1,&2,&3,&4,&5,&6,&7,&8,&9,&0,&a,&b,&c,&d,&e,&f,&r,&l,&m,&n,&o,&k";
    	}
    	else if (line.contains("{difficulty:")) {
    		Location loc = getloc(mysplit[1], user);
    		return loc.getWorld().getDifficulty().toString();
    	}
    	else if (line.contains("{difficulty}")) {
    		return ""+user.getWorld().getDifficulty().name();
    	}
    	else if (line.contains("{weatherduration}")) {
    		return ""+user.getWorld().getWeatherDuration();
    	}
    	else if (line.contains("{weatherduration:")) {
    		Location loc = getloc(mysplit[1], user);
    		return ""+loc.getWorld().getWeatherDuration();
    	}
    	else if (line.contains("{environment:")) {
    		Location loc = getloc(mysplit[1], user);
    		return loc.getWorld().getEnvironment().toString();
    	}
    	else if (line.contains("{environment}")) {
    		return ""+user.getWorld().getEnvironment().name();
    	}
    	else if (line.contains("{player}")) {
    		if (user==null) {
    			return "CONSOLE";
    		}
    		else {
    			return user.getName();
    		}
    	}
    	else if (line.contains("{gvar}")) {
    		return StringUtils.join(globals.keySet(),",").replace("{","").replace("}", "");
    	}
    	else if (line.contains("{sender}")) {
    		if (sender==null) {
    			return "CONSOLE";
    		}
    		else {
    			return sender.getName();
    		}
    	}
    	else if (line.contains("{elevated}")) {
    		return ""+elevation;
    	}
    	else if (line.contains("{gamerules:")) {
    		Location loc = getloc(mysplit[1], user);
    		return StringUtils.join(loc.getWorld().getGameRules(),",");
    	}
    	else if (line.contains("{gamerules}")) {
    		return StringUtils.join(user.getWorld().getGameRules(),",");
    	}
    	else if (line.contains("{seed:")) {
    		Location loc = getloc(mysplit[1], user);
    		return ""+loc.getWorld().getSeed();
    	}
    	else if (line.contains("{seed}")) {
    		return ""+user.getWorld().getSeed();
    	}
    	else if (line.contains("{spawn:")) {
    		Location loc = getloc(mysplit[1], user);
    		return loc.getWorld().getName()+","+loc.getWorld().getSpawnLocation().getX()+","+loc.getWorld().getSpawnLocation().getY()+","+loc.getWorld().getSpawnLocation().getZ();
    	}
    	else if (line.contains("{difficulty}")) {
    		return ""+user.getWorld().getSpawnLocation();
    	}
    	else if (line.contains("{count:")) {
    		if (mysplit[1].contains(",")) {
    			int count = 0;
    			String[] mylist = mysplit[1].split(",");
    			for (String mynum:mylist) {
    				if (mynum.equals(mysplit[2])) {
    					count+=1;
    				}
    			}
    			return ""+count;
    		}
    		else {
    			return ""+StringUtils.countMatches(mysplit[1],mysplit[2]);
    		}
    	}
    	else if (line.equals("{epoch}")) {
    		return Long.toString(System.currentTimeMillis()/1000);
    	}
    	else if (line.contains("{js:")) {
    		return javascript(line.substring(4,line.length()-1));
    	}
    	else if (line.contains("{javascript:")) {
    		return javascript(line.substring(4,line.length()-1));
    	}
    	else if (line.equals("{epochmilli}")) {
    		return Long.toString(System.currentTimeMillis());
    	}
    	else if (line.equals("{epochnano}")) {
    		return Long.toString(System.nanoTime());
    	}
    	else if (line.equals("{online}")) {
    		String online = "";
      		for (Player qwert:Bukkit.getServer().getOnlinePlayers()) {
      			online+=qwert.getName()+",";
      		}
    		return online.substring(0,online.length()-1);
    	}
    	else if (line.equals("{motd}")) {
    		return ""+Bukkit.getMotd();
    	}
    	else if (line.equals("{banlist}")) {
    		String mylist = "";
      		for (OfflinePlayer clist:Bukkit.getBannedPlayers()) {
      			mylist+=clist.getName()+",";
      		}
    		return mylist.substring(0,mylist.length()-1);
    	}
    	else if (line.equals("{playerlist}")) {
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
    	else if (line.equals("{baniplist}")) {
    		String mylist = "";
      		for (String clist:Bukkit.getIPBans()) {
      			mylist+=clist+",";
      		}
    		return mylist.substring(0,mylist.length()-1);
    	}
    	else if (line.equals("{worlds}")) {
    		String mylist = "";
      		for (World clist:getServer().getWorlds()) {
      			mylist+=clist.getName()+",";
      		}
    		return mylist.substring(0,mylist.length()-1);
    	}
    	else if (line.equals("{slots}")) {
    		return ""+Bukkit.getMaxPlayers();
    	}
    	else if (line.equals("{port}")) {
    		return ""+Bukkit.getPort();
    	}
    	else if (line.equals("{version}")) {
    		return Bukkit.getVersion().split(" ")[0];
    	}
    	else if (line.equals("{allowflight}")) {
    		return ""+Bukkit.getAllowFlight();
    	}
    	else if (line.equals("{viewdistance}")) {
    		return ""+Bukkit.getViewDistance();
    	}
    	else if (line.equals("{defaultgamemode}")) {
    		return ""+Bukkit.getDefaultGameMode();
    	}
    	else if (line.equals("{operators}")) {
    		String mylist = "";
      		for (OfflinePlayer clist:Bukkit.getOperators()) {
      			mylist+=clist.getName()+",";
      		}
    		return mylist.substring(0,mylist.length()-1);
    	}
    	else if (line.equals("{whitelist}")) {
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
    	else if (line.equals("{plugins}")) {
    		Plugin[] myplugins = getServer().getPluginManager().getPlugins();
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
		else if (line.contains("{exhaustion:")) {
			ImprovedOfflinePlayer offlineplayer = new ImprovedOfflinePlayer(mysplit[1]);
			return ""+offlineplayer.getExhaustion();
		}
		else if (line.contains("{firstjoin:")) {
			return Long.toString(Bukkit.getOfflinePlayer(mysplit[1]).getFirstPlayed()/1000);		
		}
		else if (line.contains("{lastplayed:")) {
			return Long.toString(Bukkit.getOfflinePlayer(mysplit[1]).getLastPlayed()/1000);		
		}
		else if (line.contains("{hunger:")) {
			ImprovedOfflinePlayer offlineplayer = new ImprovedOfflinePlayer(mysplit[1]);
			return ""+offlineplayer.getFoodLevel();
		}
		else if (line.contains("{air:")) {
			ImprovedOfflinePlayer offlineplayer = new ImprovedOfflinePlayer(mysplit[1]);
			return ""+offlineplayer.getRemainingAir();
		}
		else if (line.contains("{bed:")) {
			ImprovedOfflinePlayer offlineplayer = new ImprovedOfflinePlayer(mysplit[1]);
			return ""+offlineplayer.getBedSpawnLocation().getX()+","+offlineplayer.getBedSpawnLocation().getY()+","+offlineplayer.getBedSpawnLocation().getZ();
		}
		else if (line.contains("{exp:")) {
			ImprovedOfflinePlayer offlineplayer = new ImprovedOfflinePlayer(mysplit[1]);
			return ""+offlineplayer.getTotalExperience();
		}
		else if (line.contains("{lvl:")) {
			ImprovedOfflinePlayer offlineplayer = new ImprovedOfflinePlayer(mysplit[1]);
			ExperienceManager expMan = new ExperienceManager(user);
			return ""+expMan.getLevelForExp((int) Math.floor(offlineplayer.getTotalExperience()));
		}
		else if (line.contains("{money:")) {
			return ""+econ.getBalance(mysplit[1]);
		}
		else if (line.contains("{prefix:")) {
			String myworld = "world";
			if (user!=null) {
				myworld = user.getWorld().getName();
			}
			return ""+chat.getPlayerPrefix(myworld, mysplit[1]);
		}
		else if (line.contains("{suffix:")) {
			String myworld = "world";
			if (user!=null) {
				myworld = user.getWorld().getName();
			}
			return ""+chat.getPlayerSuffix(myworld, mysplit[1]);
		}
		else if (line.contains("{group:")) {
			String myworld = "world";
			if (user!=null) {
				myworld = user.getWorld().getName();
			}
			return ""+chat.getPrimaryGroup(myworld, mysplit[1]);
		}
		else if (line.contains("{operator:")) {
			return ""+Bukkit.getOfflinePlayer(mysplit[1]).isOp();
		}
		else if (line.contains("{itemid:")) {
			//TODO item
			ImprovedOfflinePlayer offlineplayer = new ImprovedOfflinePlayer(mysplit[1]);
			return ""+offlineplayer.getItemInHand();
		}
		else if (line.contains("{itemamount:")) {
			ImprovedOfflinePlayer offlineplayer = new ImprovedOfflinePlayer(mysplit[1]);
			return ""+offlineplayer.getInventory().getItemInHand().getAmount();
		}
		else if (line.contains("{itemname:")) {
			ImprovedOfflinePlayer offlineplayer = new ImprovedOfflinePlayer(mysplit[1]);
			return ""+offlineplayer.getInventory().getItemInHand().getType().toString();
		}
		else if (line.contains("{durability:")) {
			ImprovedOfflinePlayer offlineplayer = new ImprovedOfflinePlayer(mysplit[1]);
			return ""+offlineplayer.getInventory().getItemInHand().getDurability();
		}
		else if (line.contains("{gamemode}")) {
			ImprovedOfflinePlayer offlineplayer = new ImprovedOfflinePlayer(mysplit[1]);
			if(offlineplayer.getGameMode() == GameMode.CREATIVE){
	        	return "CREATIVE";
	        }
	        else if(offlineplayer.getGameMode() == GameMode.SURVIVAL){
	        	return "SURVIVAL";
	        }
	        else {
	        	return "ADVENTURE";
	        }
		}
		else if (line.contains("{direction:")) {
			ImprovedOfflinePlayer offlineplayer = new ImprovedOfflinePlayer(mysplit[1]);
        	String tempstr = "null";
            int degrees = (Math.round(offlineplayer.getLocation().getYaw()) + 270) % 360;
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
		else if (line.contains("{health:")) {
			ImprovedOfflinePlayer offlineplayer = new ImprovedOfflinePlayer(mysplit[1]);
			return String.valueOf(offlineplayer.getHealthInt());
		}
		else if (line.contains("{biome:")) {
			Location loc = getloc(mysplit[1], user);
			return loc.getWorld().getBiome(loc.getBlockX(), loc.getBlockZ()).toString();
		}
    	
    	
    	
    	
    	
    	
    	
    	
    	
    	
		else if (line.equals("{storm:")) {
			Location loc = getloc(mysplit[1], user);
			if (loc.getWorld().hasStorm()) {
				return "true";
			}
			return "false";
		}
		else if (line.equals("{thunder:")) {
			Location loc = getloc(mysplit[1], user);
			if (loc.getWorld().isThundering()) {
				return "true";
			}
			return "false";
		}
		else if (line.contains("{x:")) {
			return String.valueOf(Math.round(getloc(mysplit[1], user).getX()));
		}
		else if (line.contains("{y:")) {
			return String.valueOf(Math.round(getloc(mysplit[1], user).getY()));
		}
		else if (line.contains("{z:")) {
			return String.valueOf(Math.round(getloc(mysplit[1], user).getZ()));
		}
    	//TODO
    	else if (user != null) {
    		String line2 = line;
    		if (line.contains(":")) {
    			line2 = line.split(":")[0]+"}";
    		}
    		if (line2.equals("{player}")) {
    			return ""+user.getName();
    		}
    		else if (line2.equals("{sneaking}")) {
    			return ""+user.isSneaking();
    		}
    		if (line2.equals("{itempickup}")) {
	          return ""+user.getCanPickupItems();
	        }
    		else if (line2.equals("{flying}")) {
    			return ""+user.getAllowFlight();
    		}
    		else if (line2.equals("{blocking}")) {
    			return ""+user.isBlocking();
    		}
    		else if (line2.equals("{exhaustion}")) {
    			return ""+user.getExhaustion();
    		}
    		else if (line2.equals("{firstjoin}")) {
    			return ""+Long.toString(user.getFirstPlayed()/1000);
    		}
    		else if (line2.equals("{hunger}")) {
    			return ""+user.getFoodLevel();
    		}
    		else if (line2.equals("{grounded}")) {
    			return ""+user.isOnGround();
    		}
    		else if (line2.equals("{passenger}")) {
    			if (user.getVehicle()==null) {
    				return "false";
    			}
    			return ""+user.getVehicle().toString();
    		}
    		else if (line2.equals("{maxhealth}")) {
    			return ""+user.getMaxHealth();
    		}
    		else if (line2.equals("{maxair}")) {
    			return ""+user.getMaximumAir();
    		}
    		else if (line2.equals("{air}")) {
    			return ""+(user.getRemainingAir()/20);
    		}
    		else if (line2.equals("{age}")) {
    			return ""+(user.getTicksLived()/20);
    		}
    		else if (line2.equals("{bed}")) {
    			return ""+user.getBedSpawnLocation().getX()+","+user.getBedSpawnLocation().getY()+","+user.getBedSpawnLocation().getZ();
    		}
    		else if (line2.equals("{compass}")) {
    			return ""+user.getCompassTarget().getX()+","+user.getCompassTarget().getY()+","+user.getCompassTarget().getZ();
    		}
    		else if (line2.equals("{storm}")) {
    			if (user.getWorld().hasStorm()) {
    				return "true";
    			}
    			return "false";
    		}
    		else if (line2.equals("{thunder}")) {
    			if (user.getWorld().isThundering()) {
    				return "true";
    			}
    			return "false";
    		}

    		else if (line2.equals("{dead}")) {
    			return ""+user.isDead();
    		}
    		else if (line2.equals("{sleeping}")) {
    			return ""+user.isSleeping();
    		}
    		else if (line2.equals("{whitelisted}")) {
    			return ""+user.isWhitelisted();
    		}
    		else if (line2.equals("{world}")) {
    			return user.getWorld().getName();
    		}
        	else if (line2.contains("{world:")) {
        		return Bukkit.getWorld(mysplit[1]).getName();
        	}
        	else if (line2.equals("{x}")) {
    			return String.valueOf(Math.round(user.getLocation().getX()));
    		}
    		else if (line2.equals("{y}")) {
    			return String.valueOf(Math.round(user.getLocation().getY()));
    		}
    		else if (line2.equals("{z}")) {
    			return String.valueOf(Math.round(user.getLocation().getZ()));
    		}

    		else if (line2.equals("{lvl}")) {
    			ExperienceManager expMan = new ExperienceManager(user);
    			return ""+expMan.getLevelForExp(expMan.getCurrentExp());
    		}
    		else if (line2.equals("{exp}")) {
    			ExperienceManager expMan = new ExperienceManager(user);
    			return ""+expMan.getCurrentExp();
    		}
    		else if (line2.equals("{money}")) {
    			return ""+econ.getBalance(user.getName());
    		}
    		else if (line2.equals("{prefix}")) {
    			return ""+chat.getPlayerPrefix(user);
    		}
    		else if (line2.equals("{suffix}")) {
    			return ""+chat.getPlayerSuffix(user);
    		}
    		else if (line2.equals("{group}")) {
    			return ""+perms.getPrimaryGroup(user);
    		}
    		else if (line2.equals("{operator}")) {
				return ""+user.isOp();
    		}
    		else if (line2.equals("{worldtype}")) {
    			return ""+user.getWorld().getWorldType();
    		}
    		else if (line2.equals("{itemid}")) {
    			return String.valueOf(user.getInventory().getItemInHand().getTypeId());
    		}
    		else if (line2.equals("{itemamount}")) {
    			return String.valueOf(user.getInventory().getItemInHand().getAmount());
    		}
    		else if (line2.equals("{itemname}")) {
    			return String.valueOf(user.getInventory().getItemInHand().getType());
    		}
    		else if (line2.equals("{durability}")) {
    			return String.valueOf(user.getInventory().getItemInHand().getDurability());
    		}
    		else if (line2.equals("{ip}")) {
    			return user.getAddress().getAddress().toString().split("/")[(user.getAddress().toString().split("/").length)-1].split(":")[0];
    		}
    		else if (line2.equals("{display}")) {
    			return ""+user.getDisplayName();
    		}
    		else if (line2.equals("{gamemode}")) {
    			if(user.getGameMode() == GameMode.CREATIVE){
    	        	return "CREATIVE";
    	        }
    	        else if(user.getGameMode() == GameMode.SURVIVAL){
    	        	return "SURVIVAL";
    	        }
    	        else {
    	        	return "ADVENTURE";
    	        }
    		}
    		else if (line2.equals("{direction}")) {
    	        	String tempstr = "null";
    	            int degrees = (Math.round(user.getLocation().getYaw()) + 270) % 360;
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
    		else if (line2.equals("{biome}")) {
    			return user.getWorld().getBiome(user.getLocation().getBlockX(), user.getLocation().getBlockZ()).toString();
    		}
    		else if (line2.equals("{health}")) {
    			return String.valueOf(user.getHealth());
    		}
    	}
    	if (interact!=null) {
    		try {
	    		if (line.equals("{uses}")) {
	    			for (int i = 0;i<list.size();i++) {
		        		if (list.get(i).equals(interact)) {
		        			return ""+clicks.get(i);
		        		}
		        	}
	    		}
	    		else if (line.contains("{uses:")) {
	    			int maxclicks = Integer.parseInt(mysplit[1]);
	    			for (int i = 0;i<list.size();i++) {
		        		if (list.get(i).equals(interact)) {
		        			int myclicks = clicks.get(i);
		        			if (myclicks > maxclicks) {
		        				return ""+(myclicks%maxclicks);
		        			}
		        			else {
		        				return ""+myclicks;
		        			}
		        		}
		        	}
	    		}
    		}
    		 catch (Exception e) {
    		 }
    	}
    	else {
    	}
    	
    	for (Entry<String, Object> node : globals.entrySet()) {
    		if (line.equals(node.getKey())) {
    			return ""+node.getValue();
    		}
        }
    	Set<String> custom = null;
    	FileConfiguration myconfig = getConfig();
		custom = myconfig.getConfigurationSection("scripting.placeholders").getKeys(false);
    	if (custom.size()>0) {
    		for (String mycustom:custom) {
    			
    			if (line.contains("{"+mycustom+":")||line.equals("{"+mycustom+"}")) {
	    			List<String> current = myconfig.getStringList("scripting.placeholders."+mycustom);
	    			String mycommands = StringUtils.join(current,";");
	    			for(int i = 0; i < mysplit.length; i++) {
	    				mycommands.replace("{arg"+i+"}", mysplit[i]);
	    			}
	    			try {
	    				String result = execute(mycommands,user,sender,elevation,interact);
	    				if (result.substring(0,3).equals("if ")) {
	    					return ""+testif(result);
	    				}
	    			return result;
	    			}
	    			catch (Exception e) {
//	    				System.out.println("F "+e);
	    			}
    			}
    		}
    	}
    	
    	return "null";
    	
    }
    public String evaluate(String line, Player user, Player sender, Boolean elevation,Location interact) {
        String[] args = line.split(" "); 
        for(int i = 0; i < args.length; i++) {
        	if (line.contains("{arg"+(i+1)+"}")){
        		line.replace("{arg"+(i+1)+"}", args[i]);
        	}
        }

        
      	 int last = 0;
      	 boolean isnew = true;
      	 int q = 0;
       	while (StringUtils.countMatches(line, "{")==StringUtils.countMatches(line, "}")) {
       		q++;
       		if ((q>1000)||(StringUtils.countMatches(line, "{")==0)) {
       			break;
       		}
       	for(int i = 0; i < line.length(); i++) {
       		
       		String current = ""+line.charAt(i);
       		if (current.equals("{")) {
       			isnew = true;
       			last = i;
       		}
       		else if (current.equals("}")) {
       			if (isnew) {
       				String toreplace = line.substring(last,i+1);
       				line.substring(1,line.length()-1).split(":");
       				boolean replaced = false;
       				if (replaced==false) {
       					try {
       						line = line.replace(toreplace, fphs(toreplace,user,sender,elevation,interact));
       					}
       					catch (Exception e) {
       						line = line.replace(toreplace, "null");
       					}
       				}
       				
           			break;
       			}
       			isnew = false;
       		}
       		
       	}
       	}	
       	if (line.contains(",")==false)
       	{
       		if(line.matches(".*\\d.*")){
       			boolean num = false;
       			if (line.contains("+")) {
       				num = true;
       			}
       			else if (line.contains("-")) {
       				num = true;
       			}
       			else if (line.contains("*")) {
       				num = true;
       			}
       			else if (line.contains("/")) {
       				num = true;
       			}
       			else if (line.contains("%")) {
       				num = true;
       			}
       			else if (line.contains("=")) {
       				num = true;
       			}
       			else if (line.contains(">")) {
       				num = true;
       			}
       			else if (line.contains("<")) {
       				num = true;
       			}
       			else if (line.contains("|")) {
       				num = true;
       			}
       			else if (line.contains("&")) {
       				num = true;
       			}
       			if (num) {
       				line = javascript(line);
       			}
       		}
       	}
        if (line.equals("null")) {
        	return "";
        }
    	return line;
    }
	public String javascript(String line) {
        try {
        	Object toreturn;
        	if ((line.contains(".js"))&&(line.contains(" ")==false)) {
        		File file = new File(getDataFolder() + File.separator + "scripts" + File.separator + line);
        		toreturn = engine.eval(new java.io.FileReader(file));
        	}
        	else {
        		toreturn =  engine.eval(line);
        	}
        	try {
        		Double num = (Double) toreturn;
        		if (Math.ceil(num) == Math.floor(num)) {
        			line = Long.toString(Math.round(num));
        		}
        		else {
        			throw new Exception();
        		}
        	}
        	catch (Exception d) {
        	try {
        		Long num = (Long) toreturn;
        		line = Long.toString(num);
        	}
        	catch (Exception f) {
            	try {
            		Integer num = (Integer) toreturn;
            		line = Integer.toString(num);
            	}
            	catch (Exception g) {
                	try {
                		Float num = (Float) toreturn;
                		line = Float.toString(num);
                	}
                	catch (Exception h) {
                    	try {
                    		line = "" + toreturn;
                    	}
                    	catch (Exception i) {
                    	}
                	}
            	}
        	}
        	}
		} catch (Exception e) { }
        return line;
	}
	public String getmsg(String key) {
		File yamlFile = new File(getDataFolder(), getConfig().getString("language").toLowerCase()+".yml"); 
		YamlConfiguration.loadConfiguration(yamlFile);
		try {
			return colorise(YamlConfiguration.loadConfiguration(yamlFile).getString(key));
		}
		catch (Exception e){
			return "";
		}
	}
	public boolean iswhitelisted(String lines) {
//		System.out.println("WHITELIST "+lines);
		List<String> mylist= getConfig().getStringList("signs.autoupdate.whitelist");
		for(String current:mylist){
			if(lines.contains("{"+current+"}")) {
				return true;
			}
			else if(lines.contains("{"+current+":")) {
				return true;
			}
		}
		return false;
	}
    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }
    private boolean setupPermissions() {
        RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
        perms = rsp.getProvider();
        return perms != null;
    }
    public String colorise(String mystring) {
    	String[] codes = {"&1","&2","&3","&4","&5","&6","&7","&8","&9","&0","&a","&b","&c","&d","&e","&f","&r","&l","&m","&n","&o","&k"};
    	for (String code:codes) {
    		mystring = mystring.replace(code, "§"+code.charAt(1));
    	}
    	return mystring;
    }
    public boolean checkperm(Player player,String perm) {
    	boolean hasperm = false;
    	String[] nodes = perm.split("\\.");
    	
    	String n2 = "";
    	if (player==null) {
    		return true;
    	}
    	else if (player.hasPermission(perm)) {
    		hasperm = true;
    	}
    	else if (player.isOp()==true) {
    		hasperm = true;
    	}
    	else {
    		for(int i = 0; i < nodes.length-1; i++) {
    			n2+=nodes[i]+".";
            	if (player.hasPermission(n2+"*")) {
            		hasperm = true;
            	}
    		}
    	}
		return hasperm;
    }
    public void msg(Player player,String mystring) {
    	if (player==null) {
    		getServer().getConsoleSender().sendMessage(colorise(mystring));
    	}
    	else if (player instanceof Player==false) {
    		getServer().getConsoleSender().sendMessage(colorise(mystring));
    	}
    	else {
    		player.sendMessage(colorise(mystring));
    	}

    }
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
    	if (cmd.getName().equalsIgnoreCase("isp")) {
    		boolean failed = true;
    		Player player;
    		if (sender instanceof Player==false) {
    			player = null;
    		}
    		else {
    			player = (Player) sender;
    		}
    		if (args.length > 0) {
    			if ((args[0].equalsIgnoreCase("reload"))){
    				failed = false;
    				if (checkperm(player,"insignsplus.reload")) {
    					this.reloadConfig();
    					getConfig().getConfigurationSection("signs").set("placeholders", null);
    			        File f1 = new File(getDataFolder() + File.separator + "scripts");
    			        File[] mysigns = f1.listFiles();
    			        for (int i = 0; i < mysigns.length; i++) {
    			        	if (mysigns[i].isFile()) {
    			        		if (mysigns[i].getName().contains(".yml")) {
    				        		FileConfiguration current = YamlConfiguration.loadConfiguration(mysigns[i]);
    				        		Set<String> values = current.getConfigurationSection("").getKeys(false);
    								for(String myval:values) {
    				        			getConfig().set("scripting.placeholders."+mysigns[i].getName().substring(0,mysigns[i].getName().length()-4), current.get(myval));
    				        		}
    			        		}
    			        	}
    			        }
    	    			try {
    		    			Set<String> vars = getConfig().getConfigurationSection("scripting.variables").getKeys(false);
    		    			for(String current : vars) {
    		    				globals.put("{"+current+"}", this.getConfig().getString("scripting.variables."+current));
    		    			}
    	    			}
    	    			catch (Exception e) {
    	    				
    	    			}
    	    			counter = 0;
    	    			this.saveDefaultConfig();
    	    			msg(player,"&aRELOADED!");
    				}
    				else {
    					msg(player,"&7You lack the permission &cinsignsplus.reload&7!");
    				}
    				
    			}
    			else if ((args[0].equalsIgnoreCase("save"))){
    				failed = false;
    				if (checkperm(player,"insignsplus.save")) {
    					getConfig().getConfigurationSection("scripting").set("variables", null);
    					counter2 = 0;
        				System.out.println("[InSignsPlus] Saving variables...");
        		        for (final Entry<String, Object> node : globals.entrySet()) {
        		        	getConfig().options().copyDefaults(true);
        		        	getConfig().set("scripting.variables."+(""+node.getKey()).substring(1,(""+node.getKey()).length()-1), (""+node.getValue()));
        		        	
        		        	this.saveConfig();
        		        	this.reloadConfig();
        		        }
            			this.saveConfig();
            			this.reloadConfig();
    	    			msg(player,"&aSAVED!");
    				}
    				else {
    					msg(player,"&7You lack the permission &cinsignsplus.save&7!");
    				}
    				
    			}
    		}
    		if (failed) {
    			msg(player,"&7Commands:\n&7 - &a/isp reload\n&7 - &a/isp save");
    		}
    	}
    	return true;
	}

    public String matchgroup(String group) {
		String[] groups = (perms.getGroups());
		for (String current:groups) {
			if (group.equalsIgnoreCase(current)) {
				return current;
			}
		}
		return "";
    }
    public boolean testif(String mystring) {
    	String[] args;
    	if (mystring.substring(0, 2).equalsIgnoreCase("if")) {
    		mystring = mystring.substring(3,mystring.length());
    	}
    	int splittype = 0;
    	mystring=mystring.trim();
    	if (mystring.contains("!=") == true) {
    		splittype = 6;
    		args = mystring.split("!=");
    	}
    	else if (mystring.contains(">=") == true) {
    		splittype = 4;
    		args = mystring.split(">=");
    	}
    	else if (mystring.contains("<=") == true) {
    		splittype = 5;
    		args = mystring.split("<=");
    	}
    	else if (mystring.contains("=~") == true) {
    		splittype = 7;
    		args = mystring.split("=~");
    	}
    	else if (mystring.contains("=") == true) {
    		splittype = 1;
    		args = mystring.split("=");
    	}
    	else if (mystring.contains(">") == true) {
    		splittype = 2;
    		args = mystring.split(">");
    	}
    	else if (mystring.contains("<") == true) {
    		splittype = 3;
    		args = mystring.split("<");
    	}
    	else if (mystring.contains("!") == true) {
    		splittype = 6;
    		args = mystring.split("!");
    	}
    	else {
    		args = "true false".split(" ");
    		splittype = 1;
    	}
    	boolean toreturn = false;
    	String left = args[0].trim();
    	String right = args[1].trim();
    	try {
    		boolean failed = false;
			int result1 = 0;
			int result2 = 1;
    		try {
    		result1 = (int) Double.parseDouble("" + engine.eval(left));
    		result2 = (int) Double.parseDouble("" + engine.eval(right));
    		}
    		catch (Exception e) {
    			failed = true;
    		}
    		if (failed == false) {
    		if (splittype == 1) { if (result1==result2) { toreturn = true; } }
    		else if (splittype == 2) { if (result1>result2) { toreturn = true; } }
    		else if (splittype == 3) { if (result1<result2) { toreturn = true; } }
    		else if (splittype == 4) { if (result1>=result2) { toreturn = true; } }
    		else if (splittype == 5) { if (result1<=result2) { toreturn = true; } }
    		else if (splittype == 6) { if (result1!=result2) { toreturn = true; } }
    		}
		} catch (Exception e) {
			
		}
    	if (toreturn == false) {
    	try {
    		boolean failed = false;
			String result1 = "true";
			String result2 = "false";
    		try {
    		result1 = left.trim();
    		result2 = right.trim();
    		}
    		catch (Exception e3) {
    			failed = true;
    		}
    		if (failed == false) {
    		if (splittype == 1) { if (result1.equals(result2)) { toreturn = true; } }
    		else if (splittype == 2) { if (result1.length()>result2.length()) { toreturn = true; } }
    		else if (splittype == 3) { if (result1.length()<result2.length()) { toreturn = true; } }
    		else if (splittype == 4) { if (result1.length()>=result2.length()) { toreturn = true; } }
    		else if (splittype == 5) { if (result1.length()<=result2.length()) { toreturn = true; } }
    		else if (splittype == 6) { if (result1.equals(result2)==false) { toreturn = true; } }
    		else if (splittype == 7) { if (result1.equalsIgnoreCase(result2)) { toreturn = true; } }
    		}
		} catch (Exception e1) {
		}
    	}   	
    	if (mystring.equalsIgnoreCase("false")) {
    		toreturn = false;
    	}
    	else if (mystring.equalsIgnoreCase("true")) {
    		toreturn = true;
    	}
    	
    	return toreturn;
    }
	public void isadd(Player player, Location loc) {
		if (list.contains(loc)==false) {
		players.add(player.getName());
		list.add(loc);
		clicks.add(0);
		}
	}
	public void onDisable() {
			getConfig().getConfigurationSection("signs").set("placeholders", null);
	    	try {
	        	timer.cancel();
	        	timer.purge();
	    	}
	    	catch (IllegalStateException e) {
	    		
	    	}
	    	catch (Throwable e) {
	    		
	    	}
	    	this.reloadConfig();
	    	this.saveConfig();
			msg(null,"f&oSAVING VARIABLES!");
			try {
	        for (final Entry<String, Object> node : globals.entrySet()) {
	        	getConfig().options().copyDefaults(true);
	        	getConfig().set("scripting.variables."+(""+node.getKey()).substring(1,(""+node.getKey()).length()-1), (""+node.getValue()));
	        	saveConfig();
	        }
	        msg(null,"&f&oThanks for using &aInSignsPlus&f by Empire92!");
			}
			catch (Exception e) {
				
			}
    }
    public String execute(String line, Player user, Player sender, Boolean elevation,Location interact) {
    	recursion++;
    	try {
    	final Map<String, Object> locals = new HashMap<String, Object>();
    	locals.put("{var}", StringUtils.join(locals.keySet(),",").replace("{","").replace("}", ""));
    	String[] mycmds = line.split(";");
		boolean hasperm = true;
		int depth = 0;
		int last = 0;
		int i2 = 0;
		String myvar = ",null";
		for(int i = 0; i < mycmds.length; i++) {
			if (i>=i2) {
			String mycommand = evaluate(mycmds[i],user,sender,elevation,interact);
            for (final Entry<String, Object> node : locals.entrySet()) {
              	 if (mycommand.contains(node.getKey())) {
              		 mycommand = mycommand.replace(node.getKey(), (CharSequence) node.getValue());
              	 }
              }
            
			if ((mycommand.equals("")||mycommand.equals("null"))==false) {
			String[] cmdargs = mycommand.split(" ");
			
            if (cmdargs[0].trim().equalsIgnoreCase("for")) {
            	if (hasperm) {
    				int mylength = 0;
    				int mode = 0;
            		String mytest = "";
            		int depth2 = 1;
            		int j = 0;
            		for(j = i+1; j < mycmds.length; j++) {
            			if (mycmds[j].split(" ")[0].trim().equals("for")) {
            				depth2+=1;
            			}
            			else if (mycmds[j].split(" ")[0].trim().equals("endloop")) {
            				depth2-=1;
            			}
            			if (depth2>0) {
            				mytest+=mycmds[j]+";";
            			}
            			else {
//            				System.out.println("END "+mycmds[j]);
            			}
            			if ((depth2 == 0)||(j==mycmds.length-1)) {
            				if (cmdargs[1].contains(":")) {
            					try {
            						mylength = Integer.parseInt(cmdargs[1].split(":")[1].trim());
            					}
            					catch (Exception e) {
            						mylength = cmdargs[1].split(":")[1].split(",").length;
            						mode = 1;
            					}
            				}
            				else {
            					try {

            					mylength = Integer.parseInt(cmdargs[1].trim());
            					}
            					catch (Exception e) {
            						mylength = 0;
            					}
            				}
            				if (mode == 1) {
            					myvar = "{"+cmdargs[1].split(":")[0]+"},"+globals.get("{"+cmdargs[1].split(":")[0]+"}");
            				}
            				if (mylength>1024) {
            					mylength = 1024;
            				}
            				break;
	            			}
	            			}
            				for(int k = 0; k < mylength; k++) {
            					if (mode == 1) {
            						globals.put("{"+cmdargs[1].split(":")[0]+"}", cmdargs[1].split(":")[1].split(",")[k]);
            					}
            					if (recursion<1024) {
            						execute(mytest,user,sender,elevation,interact);
            					}
            				}
            				if (mode == 1) {
            					if (myvar.split(",")[1].equals("null")) {
            						globals.remove("{"+cmdargs[1].split(":")[0]+"}");
            					}
            					else {
            						globals.put("{"+cmdargs[1].split(":")[0]+"}", myvar.split(",")[1]);
            					}
            				}
            				i2=j+1;
            	}
            }
            else if (cmdargs[0].equalsIgnoreCase("setuser")) {
            	Player lastuser = user;
            	try {
            		if (cmdargs[1].equals("null")) {
            			user = null;
            		}
            		else {
	            		user = Bukkit.getPlayer(cmdargs[1]);
	            		if (user==null) {
	            			user = lastuser;
	            		}
            		}
            	}
            	catch (Exception e5) {
            	}
            }
            else if (cmdargs[0].equalsIgnoreCase("if")) {
          	  if (hasperm&&(depth==last)) {
          		  last++;
          		recursion = 0;
					hasperm = testif(mycommand);
          	  }
          	  else {
          	  }
          	  depth++;
            }
              else if (cmdargs[0].equalsIgnoreCase("else")) {
            	  if (last==depth) {
            	  if (hasperm) {
            		  hasperm = false;
            	  }
            	  else {
            		  hasperm = true;
            	  }
            	  if (user != null) {
            	  }
            	  }
              }
              else if (cmdargs[0].equalsIgnoreCase("endif")) {
            	  if (depth >0) {
            		  if (last==depth) {
            			  last-=1;
            		  }
            		  depth-=1;
            		  if (last==depth) {
            			  hasperm = true;
            			  if (user != null) {
            		  }
            		  }
            	  }
              }
              else if (cmdargs[0].equalsIgnoreCase("gvar")) {
            	  if (cmdargs.length>1) {
            	  if (cmdargs.length>2) {
            	  try {
            	  globals.put("{"+evaluate(cmdargs[1],user,sender,elevation,interact)+"}", evaluate(StringUtils.join(Arrays.copyOfRange(cmdargs, 2, cmdargs.length)," "),user,sender,elevation,interact));
            	  if (user != null) {
            	  }
            	  }
            	  catch (Exception e) {
            		  if (user != null) {
            	  }
            	  }
            	  }
            	  else {
            		  try {
            		  globals.remove("{"+cmdargs[1]+"}");
            		  if (user != null) {
            		  }
            		  }
            		  catch (Exception e2) {
            			  if (user != null) {
            		  }
            		  }
            	  }
              }
              }
              else if (cmdargs[0].equalsIgnoreCase("var")) {
            	  if (cmdargs.length>1) {
            	  if (cmdargs.length>2) {
            	  try {
            		  
            	  locals.put("{"+evaluate(cmdargs[1],user,sender,elevation,interact)+"}", evaluate(StringUtils.join(Arrays.copyOfRange(cmdargs, 2, cmdargs.length)," "),user,sender,elevation,interact));
            	  if (user != null) {
            	  }
            	  }
            	  catch (Exception e) {
            		  if (user != null) {
            	  }
            	  }
              }
            	  else {
            		  try {
            		  locals.remove("{"+cmdargs[1]+"}");
            		  if (user != null) {
            		  }
            		  }
            		  catch (Exception e2) {
            			  if (user != null) {
            		  }
            		  }
            	  }
            	  }
              }
              else if (hasperm) {
                  for (final Entry<String, Object> node : locals.entrySet()) {
                    	 if (mycommand.contains(node.getKey())) {
                    		 mycommand = mycommand.replace(node.getKey(), (CharSequence) node.getValue());
                    	 }
                    }
            	  mycommand = mycommand.trim();
			if (mycommand.charAt(0)=='\\') {
				mycommand = mycommand.substring(1,mycommand.length());
				if (user != null) {
				user.chat(mycommand);
				}
				else {
					getServer().dispatchCommand(getServer().getConsoleSender(), "say "+mycommand);
				}
			}
			else if (user != null) {
			 if (cmdargs[0].equalsIgnoreCase("do")){
				mycommand = mycommand.substring(3,mycommand.length());
			if (user.isOp()) {
				Bukkit.dispatchCommand(user, mycommand);
			}
			else {
        	  try
        	  {
        		  if (elevation) {
        			  user.setOp(true);
        		  }
        	      Bukkit.dispatchCommand(user, mycommand);
        	  }
        	  catch(Exception e)
        	  {
        	      e.printStackTrace();
        	  }
        	  finally
        	  {
        		  user.setOp(false); 
        	  }
        	  
			}
			
		}
			 else if (cmdargs[0].equalsIgnoreCase("return")){
				 return mycommand.substring(7,mycommand.length());
			 }
			else {
				msg(user,colorise(evaluate(mycommand, user,sender,elevation,interact)));
			}
              }
			else {
				if (cmdargs[0].equalsIgnoreCase("do")){
					mycommand = mycommand.substring(3,mycommand.length());
					getServer().dispatchCommand(getServer().getConsoleSender(), mycommand);
				}
				else {
					System.out.println(evaluate(mycommand, user,sender,elevation,interact));
				}
			}
			
		}
			
		}
    	}
    	}
    }
        catch (Exception e2) {
        	if (user!=null) {
        	msg(user,colorise(getmsg("ERROR")+getmsg("ERROR1"))+e2);
        	
        	}
        	else {
        		System.out.println(colorise(getmsg("ERROR"))+e2);
        	}
        }
    	return "null";
    }
	


	@Override
    public void onEnable(){
		protocolmanager = ProtocolLibrary.getProtocolManager();
		plugin = this;
        if (!setupEconomy() ) {
            log.severe(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        saveResource("english.yml", true);
        Plugin insignsPlugin = getServer().getPluginManager().getPlugin("InSigns");
        if((insignsPlugin != null) && insignsPlugin.isEnabled()) {
        	if (getConfig().getBoolean("signs.autoupdate.enabled")) {
        		isenabled = true;
        	}
        	isf = new InSignsFeature(insignsPlugin,this);
            getServer().getPluginManager().registerEvents(isf,this);
            System.out.println("Plugin 'InSigns' found. Using it now.");
        } else {
            System.out.println("Plugin 'InSigns' not found. Additional features disabled.");
            isenabled = true;
        }
        File f8 = new File(getDataFolder() + File.separator+"scripts"+File.separator+"example.yml");
        if(f8.exists()!=true) {  saveResource("scripts"+File.separator+"example.yml", false); }
        File f9 = new File(getDataFolder() + File.separator+"scripts"+File.separator+"test.js");
        if(f9.exists()!=true) {  saveResource("scripts"+File.separator+"test.js", false); }
        File f1 = new File(getDataFolder() + File.separator + "scripts");
        File[] mysigns = f1.listFiles();
        for (int i = 0; i < mysigns.length; i++) {
        	if (mysigns[i].isFile()) {
        		if (mysigns[i].getName().contains(".yml")) {
	        		FileConfiguration current = YamlConfiguration.loadConfiguration(mysigns[i]);
	        		Set<String> values = current.getConfigurationSection("").getKeys(false);
					for(String myval:values) {
	        			getConfig().set("scripting.placeholders."+mysigns[i].getName().substring(0,mysigns[i].getName().length()-4), current.get(myval));
	        		}
        		}
        	}
        }
        if (isf==null) {
	        protocolmanager.addPacketListener(new PacketAdapter(this, ListenerPriority.LOW, new PacketType[] { PacketType.Play.Server.UPDATE_SIGN })
	        {
	          public void onPacketSending(PacketEvent event)
	          {
	            PacketContainer packet = event.getPacket();
	            packet = packet.shallowClone();
	            int packetx = (packet.getIntegers().read(0)).intValue();
	            short packety = (packet.getIntegers().read(1)).shortValue();
	            int packetz = (packet.getIntegers().read(2)).intValue();
	            Player player = event.getPlayer();
	            Location loc = new Location(player.getWorld(), packetx,packety,packetz);
	            
	            Sign sign = (Sign) (loc.getBlock().getState());
//	            System.out.println("LOC "+loc.toString()+" | "+loc.getBlock().getData());
//	            System.out.println("LN "+StringUtils.join(sign.getLines()));
	            String[] lines = sign.getLines();
	            String unmodified = StringUtils.join(sign.getLines());
	            if (list.contains(loc)==false) {
//	            	System.out.println(1+" "+StringUtils.join(lines));
					boolean modified = false;
					if (lines[0].equals("")==false) {
						String result = evaluate(lines[0],player,player, false,loc);
						if (result.equals(lines[0])==false) {
							lines[0] = colorise(result);
							modified = true;
						}
					}
					if (lines[1].equals("")==false) {
						String result = evaluate(lines[1],player,player, false,loc);
						if (result.equals(lines[1])==false) {
							lines[1] = colorise(result);
							modified = true;
						}
					}
					if (lines[2].equals("")==false) {
						String result = evaluate(lines[2],player,player, false,loc);
						if (result.equals(lines[2])==false) {
							lines[2] = colorise(result);
							modified = true;
						}
					}
					if (lines[3].equals("")==false) {
						String result = evaluate(lines[3],player,player, false,loc);
						if (result.equals(lines[3])==false) {
							lines[3] = colorise(result);
							modified = true;
						}
					}
					if (modified==true) {
//						System.out.println(2+" "+unmodified);
						for (int i = 0; i < 4; i++) {
		            		if (lines[i].length()>15) {
			            		if ((i < 3)) {
			            			if (lines[i+1].isEmpty()) {
			            				lines[i+1] = lines[i].substring(15);
			            			}
			            		}
			            		lines[i] = lines[i].substring(0,15);
		            		}
		            	}
						if(iswhitelisted(unmodified)) {
							isadd(player, loc);
//							System.out.println("LINES "+unmodified);
							packet.getStringArrays().write(0, lines);
			    			event.setPacket(packet);
						}
						else {
							packet.getStringArrays().write(0, lines);
			    			event.setPacket(packet);
						}
					}
					else {
//						System.out.println(3);
					}
	            }
	            else {
	            	lines[0] = colorise(evaluate(lines[0],player,player, false,loc));
	            	lines[1] = colorise(evaluate(lines[1],player,player, false,loc));
	            	lines[2] = colorise(evaluate(lines[2],player,player, false,loc));
	            	lines[3] = colorise(evaluate(lines[3],player,player, false,loc));
	            	for (int i = 0; i < 4; i++) {
	            		if (lines[i].length()>15) {
		            		if ((i < 4)) {
		            			if (lines[i+1].isEmpty()) {
		            				lines[i+1] = lines[i].substring(15);
		            			}
		            		}
		            		lines[i] = lines[i].substring(0,15);
	            		}
	            	}
//	            	System.out.println("LINES "+StringUtils.join(lines));
	            	packet.getStringArrays().write(0, lines);
	    			event.setPacket(packet);
	            }
	          }
	        });
        }
        getConfig().options().copyDefaults(true);
        final Map<String, Object> options = new HashMap<String, Object>();
        getConfig().set("version", "0.6.0");
        options.put("language","english");
        options.put("signs.autoupdate.enabled",true);
        options.put("signs.autoupdate.buffer",1000);
        options.put("signs.autoupdate.updates-per-milli",1);
        options.put("signs.autoupdate.interval",1);
        List<String> whitelist = Arrays.asList("display","uses","money","prefix","suffix","group","x","y","z","lvl","exhaustion","health","exp","hunger","air","maxhealth","maxair","gamemode","direction","biome","itemname","itemid","itemamount","durability","dead","sleeping","whitelisted","operator","sneaking","itempickup","flying","blocking","age","bed","compass","spawn","worldticks","time","time12","epoch","epochmilli","epochnano","online","worlds","banlist","baniplist","operators","whitelist","randchoice","rand","elevated","matchgroup","matchplayer","hasperm","js","config","passenger","lastplayed");
        options.put("signs.autoupdate.whitelist",whitelist);
        List<String> example = Arrays.asList("return &4Hello!");
        options.put("scripting.placeholders.example",example);
        for (final Entry<String, Object> node : options.entrySet()) {
        	 if (!getConfig().contains(node.getKey())) {
        		 getConfig().set(node.getKey(), node.getValue());
        	 }
        }
        try {
        	Set<String> vars = getConfig().getConfigurationSection("scripting.variables").getKeys(false);
        	for(String current : vars) {
        		
    			globals.put("{"+current+"}", this.getConfig().getString("scripting.variables."+current));
    		}
        }
        catch (Exception e) {
        	
        }
    	saveConfig();
        setupPermissions();
        setupChat();
    	this.saveDefaultConfig();
    	getServer().getPluginManager().registerEvents(this, this);
    	if (getConfig().getInt("signs.autoupdate.interval")>0) {
    		timer.schedule (mytask,0l, 1);
    	}
    	
	}
	private boolean setupChat() {
        RegisteredServiceProvider<Chat> rsp = getServer().getServicesManager().getRegistration(Chat.class);
        chat = rsp.getProvider();
        return chat != null;
    }
	Timer timer = new Timer ();
	TimerTask mytask = new TimerTask () {
		@Override
	    public void run () {
			counter0++;
			int toupdate = getConfig().getInt("signs.autoupdate.updates-per-milli");
			for (int i = 0;i<toupdate;i++) {
				if (isenabled&&(islagging==false)) {
					if (timerlast<list.size()) {
						if (true) {
						try {
						Location loc = list.get(timerlast);
						Player player = Bukkit.getPlayer(players.get(timerlast));
						if (player!=null) {
						if (loc.getWorld().equals(player.getLocation().getWorld())) {
							if (loc.getChunk().isLoaded()==true) {
								Sign sign = (Sign) (loc.getBlock().getState());
								if (sign!=null) {
									double dist = loc.distanceSquared(player.getLocation());
					            if (dist<96) {
//					            	System.out.println("UPDATING");
					            	if (isf==null) {
					            	PacketContainer packet = protocolmanager.createPacket(PacketType.Play.Server.UPDATE_SIGN);
					            	try {
					            		packet.getSpecificModifier(Integer.TYPE).write(0, Integer.valueOf(sign.getX()));
					            		packet.getSpecificModifier(Integer.TYPE).write(1, Integer.valueOf(sign.getY()));
					            		packet.getSpecificModifier(Integer.TYPE).write(2, Integer.valueOf(sign.getZ()));
					            		packet.getStringArrays().write(0, sign.getLines());
					            		protocolmanager.sendServerPacket(player, packet);
					            	}
					            	catch (Exception e) {
					            		e.printStackTrace();
					            	}
					            	}
					            	else {
					            		isf.sendSignChange(player,sign);
					            	}
								}
					            else if (dist > (Bukkit.getViewDistance()*24)*(Bukkit.getViewDistance()*24)) {
//					            	System.out.println("too far");
					            	list.remove(timerlast);
					            	clicks.remove(timerlast);
									players.remove(timerlast);
					            }
								}
								else {
//									System.out.println("sign=null");
					            	list.remove(timerlast);
					            	clicks.remove(timerlast);
									players.remove(timerlast);
								}
							}
							else {
//								System.out.println("unloaded chunk");
				            	list.remove(timerlast);
				            	clicks.remove(timerlast);
								players.remove(timerlast);
							}
						}
						else {
//							System.out.println("wrong map");
			            	list.remove(timerlast);
			            	clicks.remove(timerlast);
							players.remove(timerlast);
							}
						}
						else {
//							System.out.println("p=null");
			            	list.remove(timerlast);
			            	clicks.remove(timerlast);
							players.remove(timerlast);
						}
					}
					catch (Exception e) {
//						System.out.println("1 "+e);
		            	list.remove(timerlast);
		            	clicks.remove(timerlast);
						players.remove(timerlast);
//						System.out.println("ERROR "+e);
//						System.out.println("2");
					}
						timerlast++;
					}
					}
				}
				else {
				}
			}
			if (counter0%50==0) {
				if (counter0>=1000) {
					counter0=0;
//					System.out.println("SIZE: "+list.size());
					counter++;
					counter2++;
					if (counter>=getConfig().getInt("signs.autoupdate.interval")) {
						counter=0;
						timerlast = 0;
					}
				}
				Long elapsed = System.currentTimeMillis()-timerstart;
				timerstart = System.currentTimeMillis();
				if (elapsed>100) {
//					System.out.println("LAG");
					islagging = true;
					if (list.size()>getConfig().getInt("signs.autoupdate.buffer")) {
//						System.out.println("1");
						list = list.subList(10,list.size());
						players = players.subList(10,players.size());
						clicks = clicks.subList(10,clicks.size());
//						System.out.println("2");
					}
				}
				else {
					if (list.size()>getConfig().getInt("signs.autoupdate.buffer")*4) {
//						System.out.println("1");
						list = list.subList(10,list.size());
						players = players.subList(10,players.size());
						clicks = clicks.subList(10,clicks.size());
//						System.out.println("2");
					}
					islagging = false;
				}
			if (counter2 > 1200) {
				try {
				counter2 = 0;
				System.out.println("&9[&bISP&9] &fSAVING VARIABLES!");
				getConfig().getConfigurationSection("scripting").set("variables", null);
		        for (final Entry<String, Object> node : globals.entrySet()) {
		        	getConfig().options().copyDefaults(true);
		        	getConfig().set("scripting.variables."+(""+node.getKey()).substring(1,(""+node.getKey()).length()-1), (""+node.getValue()));
		        	
		        	saveConfig();
		        }
		        System.out.println("DONE!");
				}
				catch (Exception e) {
					
				}
			}
		}
	}
		
	};
	
	@EventHandler
    public void onPlayerInteract(PlayerInteractEvent event)
    {
		if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			Block block = event.getClickedBlock();
	        if ((block.getType() == Material.SIGN_POST) || (block.getType() == Material.WALL_SIGN)) {
	        	for (int i = 0;i<list.size();i++) {
	        		if (list.get(i).equals(block.getLocation())) {
	        			clicks.set(i, clicks.get(i)+1);
	        		}
	        	}
	        	
	        	if (isf==null) {
	            	PacketContainer packet = protocolmanager.createPacket(PacketType.Play.Server.UPDATE_SIGN);
	            	try {
	            		Sign sign = (Sign)block.getState();
	                    Player player = event.getPlayer();
	            		packet.getSpecificModifier(Integer.TYPE).write(0, Integer.valueOf(sign.getX()));
	            		packet.getSpecificModifier(Integer.TYPE).write(1, Integer.valueOf(sign.getY()));
	            		packet.getSpecificModifier(Integer.TYPE).write(2, Integer.valueOf(sign.getZ()));
	            		packet.getStringArrays().write(0, sign.getLines());
	            		protocolmanager.sendServerPacket(player, packet);
	            	}
	            	catch (Exception e) {
	            		e.printStackTrace();
	            	}
	            	}
	        }
		}
    }
    

	
}
