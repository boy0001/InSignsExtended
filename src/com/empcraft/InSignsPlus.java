package com.empcraft;

import java.io.File;
import java.io.FilenameFilter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
import org.bukkit.Sound;
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
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
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











public final class InSignsPlus extends JavaPlugin implements Listener {
	private boolean isenabled = false;
	private static final Logger log = Logger.getLogger("Minecraft");
    private static Economy econ = null;
    private static Permission perms = null;
    private static Chat chat = null;
    private static int counter0 = 0;
    private static int counter = 0;
    private static int counter2 = 0;
    private static Map<String, Object> globals = new HashMap<String, Object>();
    private int recursion = 0;
	private static List<Location> list = new ArrayList();
	private static List<String> players = new ArrayList();
	private static List<Integer> clicks = new ArrayList();
	private long timerstart = 0;
	private boolean islagging = false;
	private int timerlast = 0;
	private static ProtocolManager protocolmanager;
	InSignsPlus plugin;
	private Object isf;
	final Map<String, Placeholder> placeholders = new HashMap<String, Placeholder>();
	final Map<String, Placeholder> defaultplaceholders = new HashMap<String, Placeholder>();
	private Player currentplayer = null;
	private Player currentsender = null;
	private String holoclicked = "false";
	
	public synchronized void setClicked(String click) {
		holoclicked = click;
	}
	public synchronized String getClicked() {
		return holoclicked;
	}
	public void setUser(Player player) {
		currentplayer = player;
	}
	
	public void setSender(Player player) {
		currentsender = player;
	}
	
	public Player getUser() {
		return currentplayer;
	}
	
	public Player getSender() {
		return currentsender;
	}
	
	private ScriptEngine engine = (new ScriptEngineManager()).getEngineByName("JavaScript");
	
	
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
					try {
						IOP_1_7_2 offlineplayer = new IOP_1_7_2(string);
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
					catch (Exception e2) {
						World world = Bukkit.getWorld(string);
						if (world!=null) {
							return world.getSpawnLocation();
						}
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
    private String fphs(String line, Boolean elevation,Location interact) {
    	Player user = getUser();
    	String[] mysplit = line.substring(1,line.length()-1).split(":");
    	if (mysplit.length==2) {
    		if ((Bukkit.getPlayer(mysplit[1])!=null)) {
				user = Bukkit.getPlayer(mysplit[1]);
				line = StringUtils.join(mysplit,":").replace(":"+mysplit[1],"");
        	}
    	}
    	try {
    		String[] modifiers;
    		String key = mysplit[0];
    		try {
    			modifiers = line.substring(2+key.length(), line.length()-1).split(":");
    		}
    		catch (Exception e2) {
    			modifiers = new String[0];
    		}
    		return getPlaceholder(key).getValue(user, interact, modifiers, elevation);
    	}
    	catch (Exception e) {
    		
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
	    				mycommands = mycommands.replace("{arg"+i+"}", mysplit[i]);
	    			}
	    			try {
	    				String result = execute(mycommands,elevation,interact);
	    				if (result.substring(0,Math.min(3, result.length())).equals("if ")) {
	    					return ""+testif(result);
	    				}
	    			return result;
	    			}
	    			catch (Exception e) {
	    			}
    			}
    		}
    	}
    	return "null";
    }
    public String evaluate(String line, Boolean elevation,Location interact) {
    	Player user = getUser();
    	try {
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
       						if (recursion<512) {
       							line = line.replace(toreplace, fphs(toreplace,elevation,interact));
       						}
       						else {
       							
       						}
       					}
   						catch (Exception e) {
   							e.printStackTrace();
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
    	catch (Exception e2) {
    		e2.printStackTrace();return "";
    	}
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
    			if ((args[0].equalsIgnoreCase("list"))){
    				if (args.length > 1) {
    					int page = 0;
    					try {
    						page = Integer.parseInt(args[1]);
    						if (page<1) {
    							page = 1;
    						}
    					}
    					catch (Exception e) {
    						
    					}
        				msg (player,"&6Placeholders for '&cISP&6'&7:");
        				List<Placeholder> myph = getAllPlaceholders();
        				for (int i = Math.min((page-1)*16, myph.size());i<Math.min((page)*16, myph.size());i++) {
        					Placeholder current = myph.get(i);
        					if (placeholders.get(current.getKey())!=null) {
        						msg(player,"&7 - &a{"+current+"}");
        					}
        					else {
        						msg(player,"&7 - &c{"+current+"}");
        					}
        				}
        				msg(player,"&6Page &c"+(page)+"&6 of &c"+((int) Math.ceil(myph.size()/16)+1)+"&6.");
        				return true;
    				}
    				else {
        				msg (player,"&6Placeholders for '&cISP&6'&7:");
        				List<Placeholder> myph = getAllPlaceholders();
        				for (int i = 0;i<16;i++) {
        					Placeholder current = myph.get(i);
        					if (placeholders.get(current.getKey())!=null) {
        						msg(player,"&7 - &a{"+current+"}");
        					}
        					else {
        						msg(player,"&7 - &c{"+current+"}");
        					}
        				}
        				msg(player,"&6Page &c1&6 of &c"+((int) Math.ceil(myph.size()/16)+1)+"&6.");
        				return true;
    				}
    			}
    			if ((args[0].equalsIgnoreCase("enable"))){
    				if (checkperm(player,"insignsplus.enable")) {
    					if (args.length>1) {
    						boolean placeholder = addPlaceholder(args[1]);
    						if (placeholder==false) {
    							msg(player,"Invalid placeholder: /isp list");
    							return false;
    						}
    						msg(player,"&7Enabled placeholder &c"+args[1]);
    						return true;
    					}
    					else {
    						msg(player,"/isp enable <key>");
    					}
    				}
    				else {
    					msg(player,"&7You lack the permission &cinsignsplus.enable");
    				}
    				return false;
    			}
    			if ((args[0].equalsIgnoreCase("disable"))){
    				if (checkperm(player,"insignsplus.disable")) {
    					if (args.length>1) {
    						Placeholder placeholder = removePlaceholder(args[1]);
    						if (placeholder==null) {
    							msg(player,"Invalid placeholder: /isp list");
    							return false;
    						}
    						msg(player,"&7Disabled placeholder &c"+args[1]);
    						return true;
    					}
    					else {
    						msg(player,"/isp disable <key>");
    					}
    				}
    				else {
    					msg(player,"&7You lack the permission &cinsignsplus.disable");
    				}
    				return false;
    			}
    			if ((args[0].equalsIgnoreCase("reload"))){
    				failed = false;
    				if (checkperm(player,"insignsplus.reload")) {
    					reloadConfig();
    					getConfig().getConfigurationSection("scripting").set("placeholders", null);
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
    		    				globals.put("{"+current+"}", getConfig().getString("scripting.variables."+current));
    		    			}
    	    			}
    	    			catch (Exception e) {
    	    				
    	    			}
    	    			counter = 0;
    	    			saveDefaultConfig();
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
        				msg(null,"[InSignsPlus] Saving variables...");
        		        for (final Entry<String, Object> node : globals.entrySet()) {
        		        	getConfig().options().copyDefaults(true);
        		        	getConfig().set("scripting.variables."+(""+node.getKey()).substring(1,(""+node.getKey()).length()-1), (""+node.getValue()));
        		        	
        		        	saveConfig();
        		        	reloadConfig();
        		        }
            			saveConfig();
            			reloadConfig();
    	    			msg(player,"&aSAVED!");
    				}
    				else {
    					msg(player,"&7You lack the permission &cinsignsplus.save&7!");
    				}
    				
    			}
    		}
    		if (failed) {
    			msg(player,"&7Commands:\n&7 - &a/isp reload\n&7 - &a/isp save\n&7 - &a/isp list\n&7 - &a/isp enable\n&7 - &a/isp disable");
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
    	if (mystring.equalsIgnoreCase("false")) {
    		return false;
    	}
    	else if (mystring.equalsIgnoreCase("true")) {
    		return true;
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
    	try {
    	String left = args[0].trim();
    	String right = args[1].trim();
		boolean failed = false;
		try {
		Object result1 = null;
		Object result2 = null;
		result1 = engine.eval(left);
		result2 = engine.eval(right);
		try {
			Double result3 = Double.parseDouble(""+result1);
			Double result4 = Double.parseDouble(""+result2);
			if (splittype == 1) { if (result3==result4) { toreturn = true; } }
    		else if (splittype == 2) { if (result3>result4) { toreturn = true; } }
    		else if (splittype == 3) { if (result3<result4) { toreturn = true; } }
    		else if (splittype == 4) { if (result3>=result4) { toreturn = true; } }
    		else if (splittype == 5) { if (result3<=result4) { toreturn = true; } }
    		else if (splittype == 6) { if (result3!=result4) { toreturn = true; } }
			return toreturn;
		}
		catch (Exception e) {
			
		}
		try {
			Integer result3 = Integer.parseInt(""+result1);
			Integer result4 = Integer.parseInt(""+result2);
			if (splittype == 1) { if (result3==result4) { toreturn = true; } }
    		else if (splittype == 2) { if (result3>result4) { toreturn = true; } }
    		else if (splittype == 3) { if (result3<result4) { toreturn = true; } }
    		else if (splittype == 4) { if (result3>=result4) { toreturn = true; } }
    		else if (splittype == 5) { if (result3<=result4) { toreturn = true; } }
    		else if (splittype == 6) { if (result3!=result4) { toreturn = true; } }
			return toreturn;
		}
		catch (Exception e) {
			
		}
		try {
			Float result3 = Float.parseFloat(""+result1);
			Float result4 = Float.parseFloat(""+result2);
			if (splittype == 1) { if (result3==result4) { toreturn = true; } }
    		else if (splittype == 2) { if (result3>result4) { toreturn = true; } }
    		else if (splittype == 3) { if (result3<result4) { toreturn = true; } }
    		else if (splittype == 4) { if (result3>=result4) { toreturn = true; } }
    		else if (splittype == 5) { if (result3<=result4) { toreturn = true; } }
    		else if (splittype == 6) { if (result3!=result4) { toreturn = true; } }
			return toreturn;
		}
		catch (Exception e) {
			
		}
		try {
			Long result3 = Long.parseLong(""+result1);
			Long result4 = Long.parseLong(""+result2);
			if (splittype == 1) { if (result3==result4) { toreturn = true; } }
    		else if (splittype == 2) { if (result3>result4) { toreturn = true; } }
    		else if (splittype == 3) { if (result3<result4) { toreturn = true; } }
    		else if (splittype == 4) { if (result3>=result4) { toreturn = true; } }
    		else if (splittype == 5) { if (result3<=result4) { toreturn = true; } }
    		else if (splittype == 6) { if (result3!=result4) { toreturn = true; } }
			return toreturn;
		}
		catch (Exception e) {
			
		}
		try {
    		if (splittype == 1) { if (left.equals(right)) { toreturn = true; } }
    		else if (splittype == 2) { if (left.compareTo(right)>0) { toreturn = true; } }
    		else if (splittype == 3) { if (left.compareTo(right)<0) { toreturn = true; } }
    		else if (splittype == 4) { if (left.compareTo(right)>=0) { toreturn = true; } }
    		else if (splittype == 5) { if (left.compareTo(right)<=0) { toreturn = true; } }
    		else if (splittype == 6) { if (left.equals(right)==false) { toreturn = true; } }
    		else if (splittype == 7) { if (left.equalsIgnoreCase(right)) { toreturn = true; } }
			return toreturn;
		}
		catch (Exception e) {
		}
		}
		catch (Exception e) {
			try {
	    		if (splittype == 1) { if (left.equals(right)) { toreturn = true; } }
	    		else if (splittype == 2) { if (left.compareTo(right)>0) { toreturn = true; } }
	    		else if (splittype == 3) { if (left.compareTo(right)<0) { toreturn = true; } }
	    		else if (splittype == 4) { if (left.compareTo(right)>=0) { toreturn = true; } }
	    		else if (splittype == 5) { if (left.compareTo(right)<=0) { toreturn = true; } }
	    		else if (splittype == 6) { if (left.equals(right)==false) { toreturn = true; } }
	    		else if (splittype == 7) { if (left.equalsIgnoreCase(right)) { toreturn = true; } }
				return toreturn;
			}
			catch (Exception e2) {
			}
		}
    	}
    	catch (Exception e3) {
    	}
    	return toreturn;
    }
	private void isadd(Player player, Location loc) {
		if ((list.contains(loc)&&players.contains(player.getName()))==false) {
		players.add(player.getName());
		list.add(loc);
		clicks.add(0);
		}
	}
	public void onDisable() {
			getConfig().getConfigurationSection("scripting").set("placeholders", null);
	    	try {
	        	timer.cancel();
	        	timer.purge();
	    	}
	    	catch (IllegalStateException e) {
	    		
	    	}
	    	catch (Throwable e) {
	    		
	    	}
	    	reloadConfig();
	    	saveConfig();
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
    public String execute(String line, Boolean elevation,Location interact) {
    	Player user = getUser();
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
			String mycommand = evaluate(mycmds[i],elevation,interact);
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
            				if (mylength>512) {
            					mylength = 512;
            				}
            				break;
	            			}
	            			}
            				for(int k = 0; k < mylength; k++) {
            					if (mode == 1) {
            						globals.put("{"+cmdargs[1].split(":")[0]+"}", cmdargs[1].split(":")[1].split(",")[k]);
            					}
            					if (recursion<512) {
            						execute(mytest,elevation,interact);
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
            			setUser(user);
            		}
            		else {
	            		user = Bukkit.getPlayer(cmdargs[1]);
	            		if (user==null) {
	            			user = lastuser;
	            			setUser(user);
	            		}
            		}
            	}
            	catch (Exception e5) {
            	}
            }
            else if (cmdargs[0].equalsIgnoreCase("if")) {
          	  if (hasperm&&(depth==last)) {
          		  last++;
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
            			  hasperm = true;
            			  if (user != null) {
            		  }
            		  }
            		  if (last==depth) {
            			  last-=1;
            		  }
            		  depth-=1;
            	  }
            	  else {
            	  }
              }
              else if (cmdargs[0].equalsIgnoreCase("gvar")) {
            	  if (cmdargs.length>1) {
            	  if (cmdargs.length>2) {
            	  try {
            	  globals.put("{"+evaluate(cmdargs[1],elevation,interact)+"}", evaluate(StringUtils.join(Arrays.copyOfRange(cmdargs, 2, cmdargs.length)," "),elevation,interact));
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
            		  
            	  locals.put("{"+evaluate(cmdargs[1],elevation,interact)+"}", evaluate(StringUtils.join(Arrays.copyOfRange(cmdargs, 2, cmdargs.length)," "),elevation,interact));
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
				msg(user,colorise(evaluate(mycommand, elevation,interact)));
			}
              }
			else {
				if (cmdargs[0].equalsIgnoreCase("do")){
					mycommand = mycommand.substring(3,mycommand.length());
					getServer().dispatchCommand(getServer().getConsoleSender(), mycommand);
				}
				else {
					msg(null,evaluate(mycommand, elevation,interact));
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
        		msg(null,colorise(getmsg("ERROR"))+e2);
        	}
        }
    	return "null";
    }
    public synchronized List<Placeholder> getPlaceholders() {
    	return new ArrayList<Placeholder>(placeholders.values());
    }
    public synchronized List<String> getPlaceholderKeys() {
    	return new ArrayList<String>(placeholders.keySet());
    }
    public synchronized List<Placeholder> getAllPlaceholders() {
    	return new ArrayList<Placeholder>(defaultplaceholders.values());
    }
    
    public synchronized Placeholder removePlaceholder (Placeholder placeholder) {
    	return placeholders.remove(placeholder.getKey());
    }
    public synchronized Placeholder removePlaceholder (String key) {
    	return placeholders.remove(key);
    }
    
    public synchronized boolean addPlaceholder (String key) {
    	Placeholder placeholder = defaultplaceholders.get(key);
    	if (placeholder!=null) {
    		placeholders.put(placeholder.getKey(), placeholder);
    		return true;
    	}
		return false;
    }
    
    public synchronized void addPlaceholder (Placeholder placeholder) {
		defaultplaceholders.put(placeholder.getKey(), placeholder);
    	placeholders.put(placeholder.getKey(), placeholder);
    }
    
    public synchronized Placeholder getPlaceholder (String key) {
    	return placeholders.get(key);
    }
	@Override
	public void onEnable(){
		
		
		protocolmanager = ProtocolLibrary.getProtocolManager();
		plugin = this;
        if (!setupEconomy() ) {
            log.severe(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
            Bukkit.getServer().getPluginManager().disablePlugin(this);
            return;
        }
        saveResource("english.yml", true);
        Plugin insignsPlugin = Bukkit.getServer().getPluginManager().getPlugin("InSigns");
        if((insignsPlugin != null)) {
        	if (insignsPlugin.isEnabled()) {
	            msg(null,"&c[Info] Plugin 'InSigns' is no longer required..");
        	}
        } else {
            
        }
        isenabled = true;
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
	    	    recursion=0;
	            PacketContainer packet = event.getPacket();
	            packet = packet.shallowClone();
	            int packetx = (packet.getIntegers().read(0)).intValue();
	            short packety = (packet.getIntegers().read(1)).shortValue();
	            int packetz = (packet.getIntegers().read(2)).intValue();
	            Player player = event.getPlayer();
	            Location loc = new Location(player.getWorld(), packetx,packety,packetz);
	            
	            Sign sign = (Sign) (loc.getBlock().getState());
	            String[] lines = sign.getLines();
	            String unmodified = StringUtils.join(sign.getLines());
	            setUser(player);
	            setSender(player);
	            if ((list.contains(loc)&&players.contains(player.getName()))==false) {
					boolean modified = false;
					if (lines[0].equals("")==false) {
						
						String result = evaluate(lines[0], false,loc);
						if (result.equals(lines[0])==false) {
							lines[0] = colorise(result);
							modified = true;
						}
					}
					if (lines[1].equals("")==false) {
						String result = evaluate(lines[1], false,loc);
						if (result.equals(lines[1])==false) {
							lines[1] = colorise(result);
							modified = true;
						}
					}
					if (lines[2].equals("")==false) {
						String result = evaluate(lines[2], false,loc);
						if (result.equals(lines[2])==false) {
							lines[2] = colorise(result);
							modified = true;
						}
					}
					if (lines[3].equals("")==false) {
						String result = evaluate(lines[3], false,loc);
						if (result.equals(lines[3])==false) {
							lines[3] = colorise(result);
							modified = true;
						}
					}
					if (modified==true) {
						for (int i = 0; i < 4; i++) {
		            		if (lines[i].length()>15) {
			            		if ((i < 3)) {
			            			if (lines[i+1].isEmpty()) {
			            				lines[i+1] = lines[i].substring(15);
			            			}
			            		}
			            		lines[i] = lines[i].substring(0,15);
		            		}
		            		else if (lines[i].contains("\\n")) {
		            			if ((i < 3)) {
		            				if (lines[i+1].isEmpty()) {
		            					lines[i+1] = lines[i].substring(lines[i].indexOf("\\n")+2);
		            				}
		            			}
		            			lines[i] = lines[i].substring(0,lines[i].indexOf("\\n")-1);
		            		}
		            	}
						if(iswhitelisted(unmodified)) {
							isadd(player, loc);
							packet.getStringArrays().write(0, lines);
			    			event.setPacket(packet);
						}
						else {
							packet.getStringArrays().write(0, lines);
			    			event.setPacket(packet);
						}
					}
					else {
					}
	            }
	            else {
	            	lines[0] = colorise(evaluate(lines[0], false,loc));
	            	lines[1] = colorise(evaluate(lines[1], false,loc));
	            	lines[2] = colorise(evaluate(lines[2], false,loc));
	            	lines[3] = colorise(evaluate(lines[3], false,loc));
					for (int i = 0; i < 4; i++) {
	            		if (lines[i].length()>15) {
		            		if ((i < 3)) {
		            			if (lines[i+1].isEmpty()) {
		            				lines[i+1] = lines[i].substring(15);
		            			}
		            		}
		            		lines[i] = lines[i].substring(0,15);
	            		}
	            		else if (lines[i].contains("\\n")) {
	            			if ((i < 3)) {
	            				if (lines[i+1].isEmpty()) {
	            					lines[i+1] = lines[i].substring(lines[i].indexOf("\\n")+2);
	            				}
	            			}
	            			lines[i] = lines[i].substring(0,lines[i].indexOf("\\n")-1);
	            		}
	            	}
	            	packet.getStringArrays().write(0, lines);
	    			event.setPacket(packet);
	            }
	            setUser(null);
	            setSender(null);
	          }
	        });
        }
        getConfig().options().copyDefaults(true);
        final Map<String, Object> options = new HashMap<String, Object>();
        getConfig().set("version", "0.7.4");
        options.put("language","english");
        options.put("signs.autoupdate.enabled",true);
        options.put("signs.autoupdate.buffer",1000);
        options.put("signs.autoupdate.updates-per-milli",1);
        options.put("signs.autoupdate.interval",1);
        List<String> whitelist = Arrays.asList("grounded","location","age","localtime","localtime12","display","uses","money","prefix","suffix","group","x","y","z","lvl","exhaustion","health","exp","hunger","air","maxhealth","maxair","gamemode","direction","biome","itemname","itemid","itemamount","durability","dead","sleeping","whitelisted","operator","sneaking","itempickup","flying","blocking","age","bed","compass","spawn","worldticks","time","date","time12","epoch","epochmilli","epochnano","online","worlds","banlist","baniplist","operators","whitelist","randchoice","rand","elevated","matchgroup","matchplayer","hasperm","js","config","passenger","lastplayed");
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
        		
    			globals.put("{"+current+"}", getConfig().getString("scripting.variables."+current));
    		}
        }
        catch (Exception e) {
        	
        }
    	saveConfig();
        setupPermissions();
        setupChat();
    	saveDefaultConfig();
    	Bukkit.getServer().getPluginManager().registerEvents(this, this);
    	if (getConfig().getInt("signs.autoupdate.interval")>0) {
    		timer.schedule (mytask,0l, 1);
    	}
    	
    	//TODO ADD PLACEHOLDERS
    	
    	addPlaceholder(new Placeholder("rand") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
    		Random random = new Random();
    		if (modifiers.length==1) {
    			return ""+random.nextInt(Integer.parseInt(modifiers[0]));
    		}
			int start = Integer.parseInt(modifiers[0]);
			int stop = Integer.parseInt(modifiers[1]);
			return ""+random.nextInt(stop-start)+start;
		} });
    	addPlaceholder(new Placeholder("msg") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
			return getmsg(modifiers[0]);
		} });
    	addPlaceholder(new Placeholder("range") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
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
		} });
    	addPlaceholder(new Placeholder("matchplayer") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
    		List<Player> matches = getServer().matchPlayer(modifiers[0]);
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
		} });
    	addPlaceholder(new Placeholder("matchgroup") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
    		return matchgroup(modifiers[0]);
		} });
    	addPlaceholder(new Placeholder("index") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
    		return modifiers[0].split(",")[Integer.parseInt(modifiers[1])];
		} });
    	addPlaceholder(new Placeholder("setindex") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
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
		} });
    	addPlaceholder(new Placeholder("delindex") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
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
		} });
    	addPlaceholder(new Placeholder("sublist") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
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
		} });
    	addPlaceholder(new Placeholder("getindex") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
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
		} });
    	addPlaceholder(new Placeholder("listhas") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
    		String[] mylist = modifiers[0].split(",");
    		for(int i = 0; i < mylist.length; i++) {
    			if (mylist[i].equals(modifiers[1])) {
    				return "true";
    			}
    		}
    		return "false";
		
		} });
    	addPlaceholder(new Placeholder("contains") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
    		if (modifiers[0].contains(modifiers[1])) {
    			return "true";
    		}
    		else if (modifiers[0].equals(modifiers[1])) {
    			return "true";
    		}
    		return "false";
		} });
    	addPlaceholder(new Placeholder("substring") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
    		return modifiers[0].substring(Integer.parseInt(modifiers[1]), Integer.parseInt(modifiers[2]));
		} });
    	addPlaceholder(new Placeholder("size") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
			return ""+modifiers[0].split(",").length;
		} });
    	addPlaceholder(new Placeholder("length") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
			return ""+modifiers[0].length();
		} });
    	addPlaceholder(new Placeholder("split") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
    		return modifiers[0].replace(modifiers[1],",");
		} });
    	addPlaceholder(new Placeholder("hasperm") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
    		if (player==null) {
    			return "true";
    		}
    		else if (modifiers.length==2) {
    			return ""+perms.playerHas(player.getWorld(),modifiers[0], modifiers[1]);
    		}
    		else if (checkperm(player,modifiers[0])) {
    			return "true";
    		}
    		return "false";
		} });
    	addPlaceholder(new Placeholder("randchoice") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
    		String[] mylist = modifiers[0].split(",");
    		Random random = new Random();
    		return mylist[random.nextInt(mylist.length-1)];
		} });
    	addPlaceholder(new Placeholder("worldtype") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
			if (modifiers.length==1) {
	    		Location loc = getloc(modifiers[0], player);
	    		return ""+loc.getWorld().getWorldType().getName();
			}
			else {
				return ""+player.getWorld().getWorldType();
			}
		} });
    	addPlaceholder(new Placeholder("listreplace") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
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
		} });
    	addPlaceholder(new Placeholder("prefix") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
	    		if (modifiers.length==1) {
	    			if (Bukkit.getPlayer(modifiers[0])==null) {
	    				try {
	    	    			ImprovedOfflinePlayer offlineplayer = new ImprovedOfflinePlayer(modifiers[0]);
	    	    			return chat.getPlayerPrefix(offlineplayer.getLocation().getWorld(), modifiers[0]);
	    	        		}
	    	        		catch (Exception e) {
	    	        			IOP_1_7_2 offlineplayer = new IOP_1_7_2(modifiers[0]);
	    	        			return chat.getPlayerPrefix(offlineplayer.getLocation().getWorld(), modifiers[0]);
	    	        		}
	    			}
	    			return chat.getPlayerPrefix(Bukkit.getPlayer(modifiers[0]));
	    		}
	    		return chat.getPlayerPrefix(player);
		} });
    	addPlaceholder(new Placeholder("suffix") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
    		if (modifiers.length==1) {
    			if (Bukkit.getPlayer(modifiers[0])==null) {
    				try {
    	    			ImprovedOfflinePlayer offlineplayer = new ImprovedOfflinePlayer(modifiers[0]);
    	    			return chat.getPlayerSuffix(offlineplayer.getLocation().getWorld(), modifiers[0]);
    	        		}
    	        		catch (Exception e) {
    	        			IOP_1_7_2 offlineplayer = new IOP_1_7_2(modifiers[0]);
    	        			return chat.getPlayerSuffix(offlineplayer.getLocation().getWorld(), modifiers[0]);
    	        		}
    			}
    			return chat.getPlayerSuffix(Bukkit.getPlayer(modifiers[0]));
    		}
    		return chat.getPlayerSuffix(player);
    	} });
    	addPlaceholder(new Placeholder("worldticks") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
			if (modifiers.length==1) {
				Location loc = getloc(modifiers[0], player);
	    		return Long.toString(loc.getWorld().getTime());
			}
    		return Long.toString(player.getWorld().getTime());
		} });
    	addPlaceholder(new Placeholder("time") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
    		if (modifiers.length==1) {
        		Location loc = getloc(modifiers[0], player);
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
		} });
    	addPlaceholder(new Placeholder("sectotime") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
    		String toreturn = "";
    		try {
    		Long time = Long.parseLong(modifiers[0]);
    		int years = 0;
    		int weeks = 0;
    		int days = 0;
    		int hours = 0;
    		int minutes = 0;
    		int seconds = 0;
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
		} });
    	addPlaceholder(new Placeholder("localtime") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
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
		} });
    	addPlaceholder(new Placeholder("date") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
    		if (modifiers.length == 0) {
    			return (new SimpleDateFormat("yy\\M\\d")).format(new Date());
    		}
    		else {
    			Date date = new Date(Integer.parseInt(modifiers[0]));
    			return (new SimpleDateFormat("yy\\M\\d")).format(date);
    			//todo convert timestamp to date.
    		}
		} });
    	addPlaceholder(new Placeholder("localtime12") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
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
		} });
    	addPlaceholder(new Placeholder("time12") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
    		if (modifiers.length==1) {
        		String ampm = " AM";
        		Location loc = getloc(modifiers[0], player);
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
		} });
    	addPlaceholder(new Placeholder("replace") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
    		return modifiers[0].replace(modifiers[1], modifiers[2]);
		} });
    	addPlaceholder(new Placeholder("config") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
    		return getConfig().getString(modifiers[0]);
		} });
    	addPlaceholder(new Placeholder("structures") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
    		if (modifiers.length==1) {
        		Location loc = getloc(modifiers[0], player);
        		return loc.getWorld().canGenerateStructures()+"";
    		}
    		return ""+player.getWorld().canGenerateStructures();
		} });
    	addPlaceholder(new Placeholder("autosave") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
    		if (modifiers.length==1) {
    			Location loc = getloc(modifiers[0], player);
        		return loc.getWorld().isAutoSave()+"";
    		}
    		return ""+player.getWorld().isAutoSave();
		} });
    	addPlaceholder(new Placeholder("animals") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
    		if (modifiers.length==1) {
        		Location loc = getloc(modifiers[0], player);
        		return loc.getWorld().getAllowAnimals()+"";
    		}
    		return ""+player.getWorld().getAllowAnimals();
		} });
    	addPlaceholder(new Placeholder("monsters") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
    		if (modifiers.length==1) {
        		Location loc = getloc(modifiers[0], player);
        		return loc.getWorld().getAllowMonsters()+"";
    		}
    		return ""+player.getWorld().getAllowMonsters();
		} });
    	addPlaceholder(new Placeholder("online") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
    		if (modifiers.length==1) {
        		Location loc = getloc(modifiers[0], player);
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
		} });
    	addPlaceholder(new Placeholder("colors") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
    		return "&1,&2,&3,&4,&5,&6,&7,&8,&9,&0,&a,&b,&c,&d,&e,&f,&r,&l,&m,&n,&o,&k";
		} });
    	addPlaceholder(new Placeholder("difficulty") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
    		if (modifiers.length==1) {
        		Location loc = getloc(modifiers[0], player);
        		return loc.getWorld().getDifficulty().toString();
    		}
    		return ""+player.getWorld().getDifficulty().name();
		} });
    	addPlaceholder(new Placeholder("weatherduration") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
    		if (modifiers.length==1) {
        		Location loc = getloc(modifiers[0], player);
        		return ""+loc.getWorld().getWeatherDuration();
    		}
    		return ""+player.getWorld().getWeatherDuration();
		} });
    	addPlaceholder(new Placeholder("environment") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
    		if (modifiers.length==1) {
        		Location loc = getloc(modifiers[0], player);
        		return loc.getWorld().getEnvironment().toString();
    		}
    		return ""+player.getWorld().getEnvironment().name();
		} });
    	addPlaceholder(new Placeholder("player") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
    		if (player==null) {
    			return "CONSOLE";
    		}
    		else {
    			return player.getName();
    		}
		} });
    	addPlaceholder(new Placeholder("gvar") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
    		return StringUtils.join(globals.keySet(),",").replace("{","").replace("}", "");
		} });
    	addPlaceholder(new Placeholder("sender") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
    		Player sender = getSender();
    		if (sender==null) {
    			return "CONSOLE";
    		}
    		else {
    			return sender.getName();
    		}
		} });
    	addPlaceholder(new Placeholder("elevated") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
    		return ""+elevation;
		} });
    	addPlaceholder(new Placeholder("gamerules") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
    		if (modifiers.length==1) {
        		Location loc = getloc(modifiers[0], player);
        		return StringUtils.join(loc.getWorld().getGameRules(),",");
    		}
    		return StringUtils.join(player.getWorld().getGameRules(),",");
		} });
    	addPlaceholder(new Placeholder("seed") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
    		if (modifiers.length==1) {
        		Location loc = getloc(modifiers[0], player);
        		return ""+loc.getWorld().getSeed();
    		}
    		return ""+player.getWorld().getSeed();
		} });
    	addPlaceholder(new Placeholder("spawn") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
    		if (modifiers.length==1) {
    			Location loc = getloc(modifiers[0], player);
        		return loc.getWorld().getName()+","+loc.getWorld().getSpawnLocation().getX()+","+loc.getWorld().getSpawnLocation().getY()+","+loc.getWorld().getSpawnLocation().getZ();
    		}
    		return location.getWorld().getName()+","+location.getWorld().getSpawnLocation().getX()+","+location.getWorld().getSpawnLocation().getY()+","+location.getWorld().getSpawnLocation().getZ();
		} });
    	addPlaceholder(new Placeholder("count") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
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
		} });
    	addPlaceholder(new Placeholder("epoch") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
    		return Long.toString(System.currentTimeMillis()/1000);
		} });
    	addPlaceholder(new Placeholder("js") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
    		return javascript(StringUtils.join(modifiers,":"));
		} });
    	addPlaceholder(new Placeholder("javascript") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
    		return javascript(StringUtils.join(modifiers,":"));
		} });
    	addPlaceholder(new Placeholder("epochmilli") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
    		return Long.toString(System.currentTimeMillis());
		} });
    	addPlaceholder(new Placeholder("epochnano") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
    		return Long.toString(System.nanoTime());
		} });
    	addPlaceholder(new Placeholder("motd") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
    		return ""+Bukkit.getMotd();
		} });
    	addPlaceholder(new Placeholder("banlist") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
    		String mylist = "";
      		for (OfflinePlayer clist:Bukkit.getBannedPlayers()) {
      			mylist+=clist.getName()+",";
      		}
    		return mylist.substring(0,mylist.length()-1);
		} });
    	addPlaceholder(new Placeholder("playerlist") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
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
		} });
    	addPlaceholder(new Placeholder("baniplist") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
    		String mylist = "";
      		for (String clist:Bukkit.getIPBans()) {
      			mylist+=clist+",";
      		}
    		return mylist.substring(0,mylist.length()-1);
		} });
    	addPlaceholder(new Placeholder("worlds") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
    		String mylist = "";
      		for (World clist:getServer().getWorlds()) {
      			mylist+=clist.getName()+",";
      		}
    		return mylist.substring(0,mylist.length()-1);
		} });
    	addPlaceholder(new Placeholder("slots") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
    		return ""+Bukkit.getMaxPlayers();
		} });
    	addPlaceholder(new Placeholder("port") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
    		return ""+Bukkit.getPort();
		} });
    	addPlaceholder(new Placeholder("version") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
    		return Bukkit.getVersion().split(" ")[0];
		} });
    	addPlaceholder(new Placeholder("allowflight") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
    		return ""+Bukkit.getAllowFlight();
		} });
    	addPlaceholder(new Placeholder("viewdistance") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
    		return ""+Bukkit.getViewDistance();
		} });
    	addPlaceholder(new Placeholder("defaultgamemode") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
    		return ""+Bukkit.getDefaultGameMode();
		} });
    	addPlaceholder(new Placeholder("operators") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
    		String mylist = "";
      		for (OfflinePlayer clist:Bukkit.getOperators()) {
      			mylist+=clist.getName()+",";
      		}
    		return mylist.substring(0,mylist.length()-1);
		} });
    	addPlaceholder(new Placeholder("whitelist") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
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
		} });
    	addPlaceholder(new Placeholder("plugins") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
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
		} });
    	addPlaceholder(new Placeholder("exhaustion") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
    		if (modifiers.length==1) {
    			if (Bukkit.getPlayer(modifiers[0])==null) {
    	    		try {
    	    			ImprovedOfflinePlayer offlineplayer = new ImprovedOfflinePlayer(modifiers[0]);
    	    			return ""+offlineplayer.getExhaustion();
    	        		}
    	        		catch (Exception e) {
    	        			IOP_1_7_2 offlineplayer = new IOP_1_7_2(modifiers[0]);
    	        			return ""+offlineplayer.getExhaustion();
    	        		}
    			}
				return ""+Bukkit.getPlayer(modifiers[0]).getExhaustion();
    		}
			return ""+player.getExhaustion();
		} });
    	addPlaceholder(new Placeholder("display") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
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
		} });
    	addPlaceholder(new Placeholder("firstjoin") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
    		return Long.toString(Bukkit.getOfflinePlayer(modifiers[0]).getFirstPlayed()/1000);		
		} });
    	addPlaceholder(new Placeholder("lastplayed") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
    		if (Bukkit.getPlayer(modifiers[0])!=null) {
    			return "0";
    		}
    		return Long.toString(Bukkit.getOfflinePlayer(modifiers[0]).getLastPlayed()/1000);
		} });
    	addPlaceholder(new Placeholder("hunger") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
    		if (modifiers.length==1) {
    			if (Bukkit.getPlayer(modifiers[0])==null) {
    	    		try {
    	    			ImprovedOfflinePlayer offlineplayer = new ImprovedOfflinePlayer(modifiers[0]);
    	    			return ""+offlineplayer.getFoodLevel();
    	        		}
    	        		catch (Exception e) {
    	        			IOP_1_7_2 offlineplayer = new IOP_1_7_2(modifiers[0]);
    	        			return ""+offlineplayer.getFoodLevel();
    	        		}
    			}
    			return ""+Bukkit.getPlayer(modifiers[0]).getFoodLevel();
    		}
			return ""+player.getFoodLevel();
		} });
    	addPlaceholder(new Placeholder("air") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
    		if (modifiers.length==1) {
    			if (Bukkit.getPlayer(modifiers[0])==null) {
    	    		try {
    	    			ImprovedOfflinePlayer offlineplayer = new ImprovedOfflinePlayer(modifiers[0]);
    	    			return ""+offlineplayer.getRemainingAir();
    	        		}
    	        		catch (Exception e) {
    	        			IOP_1_7_2 offlineplayer = new IOP_1_7_2(modifiers[0]);
    	        			return ""+offlineplayer.getRemainingAir();
    	        		}
    			}
    			return ""+Bukkit.getPlayer(modifiers[0]).getRemainingAir();
    		}
			return ""+player.getRemainingAir();
		} });
    	addPlaceholder(new Placeholder("bed") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
    		if (modifiers.length==1) {
    			if (Bukkit.getPlayer(modifiers[0])==null) {
    	    		try {
    	    			ImprovedOfflinePlayer offlineplayer = new ImprovedOfflinePlayer(modifiers[0]);
    	    			return ""+offlineplayer.getBedSpawnLocation().getX()+","+offlineplayer.getBedSpawnLocation().getY()+","+offlineplayer.getBedSpawnLocation().getZ();
    	        		}
    	        		catch (Exception e) {
    	        			IOP_1_7_2 offlineplayer = new IOP_1_7_2(modifiers[0]);
    	        			return ""+offlineplayer.getBedSpawnLocation().getX()+","+offlineplayer.getBedSpawnLocation().getY()+","+offlineplayer.getBedSpawnLocation().getZ();
    	        		}
    			}
    			player = Bukkit.getPlayer(modifiers[0]);
    			return ""+player.getBedSpawnLocation().getX()+","+player.getBedSpawnLocation().getY()+","+player.getBedSpawnLocation().getZ();
    		}
    		return ""+player.getBedSpawnLocation().getX()+","+player.getBedSpawnLocation().getY()+","+player.getBedSpawnLocation().getZ();
		} });
    	addPlaceholder(new Placeholder("exp") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
    		if (modifiers.length==1) {
    			if (Bukkit.getPlayer(modifiers[0])==null) {
    	    		try {
    	    			ImprovedOfflinePlayer offlineplayer = new ImprovedOfflinePlayer(modifiers[0]);
    	    			return ""+offlineplayer.getTotalExperience();
    	        		}
    	        		catch (Exception e) {
    	        			IOP_1_7_2 offlineplayer = new IOP_1_7_2(modifiers[0]);
    	        			return ""+offlineplayer.getTotalExperience();
    	        		}
    			}
    			ExperienceManager expMan = new ExperienceManager(Bukkit.getPlayer(modifiers[0]));
    			return ""+expMan.getCurrentExp();
    		}
			ExperienceManager expMan = new ExperienceManager(player);
			return ""+expMan.getCurrentExp();
		} });
    	addPlaceholder(new Placeholder("lvl") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
    		if (modifiers.length==1) {
    			if (Bukkit.getPlayer(modifiers[0])==null) {
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
    			ExperienceManager expMan = new ExperienceManager(Bukkit.getPlayer(modifiers[0]));
    			return ""+expMan.getLevelForExp(expMan.getCurrentExp());
    		}
			ExperienceManager expMan = new ExperienceManager(player);
			return ""+expMan.getLevelForExp(expMan.getCurrentExp());
		} });
    	addPlaceholder(new Placeholder("money") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
    		if (modifiers.length==1) {
    			return ""+econ.getBalance(modifiers[0]);
    		}
    		return ""+econ.getBalance(player.getName());
		} });
    	addPlaceholder(new Placeholder("group") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
    		if (modifiers.length==1) {
    			if (Bukkit.getPlayer(modifiers[0])==null) {
    	    		try {
    	    			ImprovedOfflinePlayer offlineplayer = new ImprovedOfflinePlayer(modifiers[0]);
    	    			return ""+perms.getPrimaryGroup(offlineplayer.getLocation().getWorld(), modifiers[0]);
    	    		}
    	        		catch (Exception e) {
    	        			IOP_1_7_2 offlineplayer = new IOP_1_7_2(modifiers[0]);
    	        			return ""+perms.getPrimaryGroup(offlineplayer.getLocation().getWorld(), modifiers[0]);
    	        		}
    			}
    			return ""+perms.getPrimaryGroup(Bukkit.getPlayer(modifiers[0]));
    		}
    		return ""+perms.getPrimaryGroup(player);
		} });
    	addPlaceholder(new Placeholder("operator") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
    		if (modifiers.length==1) {
    			return ""+Bukkit.getOfflinePlayer(modifiers[0]).isOp();
    		}
			return ""+player.isOp();
		} });
    	addPlaceholder(new Placeholder("itemid") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
    		if (modifiers.length==1) {
    			if (Bukkit.getPlayer(modifiers[0])==null) {
    	    		try {
    	    			ImprovedOfflinePlayer offlineplayer = new ImprovedOfflinePlayer(modifiers[0]);
    	    			return ""+offlineplayer.getItemInHand();
    	        		}
    	        		catch (Exception e) {
    	        			IOP_1_7_2 offlineplayer = new IOP_1_7_2(modifiers[0]);
    	        			return ""+offlineplayer.getItemInHand();
    	        		}
    			}
    			return ""+Bukkit.getPlayer(modifiers[0]).getItemInHand().getTypeId();
    		}
			return ""+player.getItemInHand().getTypeId();
		} });
    	addPlaceholder(new Placeholder("itemamount") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
    		if (modifiers.length==1) {
    			if (Bukkit.getPlayer(modifiers[0])==null) {
    	    		try {
    	    			ImprovedOfflinePlayer offlineplayer = new ImprovedOfflinePlayer(modifiers[0]);
    	    			return ""+offlineplayer.getInventory().getItemInHand().getAmount();
    	        		}
    	        		catch (Exception e) {
    	        			IOP_1_7_2 offlineplayer = new IOP_1_7_2(modifiers[0]);
    	        			return ""+offlineplayer.getInventory().getItemInHand().getAmount();
    	        		}
    			}
    			return ""+Bukkit.getPlayer(modifiers[0]).getItemInHand().getAmount();
    		}
			return ""+player.getItemInHand().getAmount();
		} });
    	addPlaceholder(new Placeholder("itemname") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
    		if (modifiers.length==1) {
    			if (Bukkit.getPlayer(modifiers[0])==null) {
    	    		try {
    	    			ImprovedOfflinePlayer offlineplayer = new ImprovedOfflinePlayer(modifiers[0]);
    	    			return ""+offlineplayer.getInventory().getItemInHand().getType().toString();
    	        		}
    	        		catch (Exception e) {
    	        			IOP_1_7_2 offlineplayer = new IOP_1_7_2(modifiers[0]);
    	        			return ""+offlineplayer.getInventory().getItemInHand().getType().toString();
    	        		}
    			}
    			return ""+Bukkit.getPlayer(modifiers[0]).getItemInHand().getType().toString();
    		}
			return ""+player.getItemInHand().getType().toString();
		} });
    	addPlaceholder(new Placeholder("durability") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
    		if (modifiers.length==1) {
    			if (Bukkit.getPlayer(modifiers[0])==null) {
    	    		try {
    	    			ImprovedOfflinePlayer offlineplayer = new ImprovedOfflinePlayer(modifiers[0]);
    	    			return ""+offlineplayer.getInventory().getItemInHand().getDurability();
    	        		}
    	        		catch (Exception e) {
    	        			IOP_1_7_2 offlineplayer = new IOP_1_7_2(modifiers[0]);
    	        			return ""+offlineplayer.getInventory().getItemInHand().getDurability();
    	        		}
    			}
    			return ""+Bukkit.getPlayer(modifiers[0]).getInventory().getItemInHand().getDurability();
    		}
			return ""+player.getInventory().getItemInHand().getDurability();
		} });
    	addPlaceholder(new Placeholder("gamemode") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
    		if (modifiers.length==1) {
    			if (Bukkit.getPlayer(modifiers[0])==null) {
    	    		try {
    	    			ImprovedOfflinePlayer offlineplayer = new ImprovedOfflinePlayer(modifiers[0]);
    	    			return ""+offlineplayer.getGameMode().toString();
    	        		}
    	        		catch (Exception e) {
    	        			IOP_1_7_2 offlineplayer = new IOP_1_7_2(modifiers[0]);
    	        			return ""+offlineplayer.getGameMode().toString();
    	        		}
    			}
    			return ""+Bukkit.getPlayer(modifiers[0]).getGameMode().toString();
    		}
			return ""+player.getGameMode().toString();
		} });
    	addPlaceholder(new Placeholder("direction") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
    		if (modifiers.length==1) {
    			if (Bukkit.getPlayer(modifiers[0])==null) {
    	    		try {
    					ImprovedOfflinePlayer offlineplayer = new ImprovedOfflinePlayer(modifiers[0]);
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
    	    		catch (Exception e) {
    	    			IOP_1_7_2 offlineplayer = new IOP_1_7_2(modifiers[0]);
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
		} });
    	addPlaceholder(new Placeholder("health") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
    		if (modifiers.length==1) {
    			if (Bukkit.getPlayer(modifiers[0])==null) {
    	    		try {
    	    			ImprovedOfflinePlayer offlineplayer = new ImprovedOfflinePlayer(modifiers[0]);
    	    			return String.valueOf(offlineplayer.getHealthInt());
    	        		}
    	        		catch (Exception e) {
    	        			IOP_1_7_2 offlineplayer = new IOP_1_7_2(modifiers[0]);
    	        			return String.valueOf(offlineplayer.getHealthInt());
    	        		}
    			}
    			return ""+Bukkit.getPlayer(modifiers[0]).getHealth();
    		}
			return ""+player.getHealth();
		} });
    	addPlaceholder(new Placeholder("biome") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
    		if (modifiers.length==1) {
    			Location loc = getloc(modifiers[0], player);
    			return loc.getWorld().getBiome(loc.getBlockX(), loc.getBlockZ()).toString();
    		}
    		return player.getWorld().getBiome(player.getLocation().getBlockX(), player.getLocation().getBlockZ()).toString();
		} });
    	addPlaceholder(new Placeholder("location") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
    		if (modifiers.length==1) {
    			Location loc = getloc(modifiers[0], player);
    			return loc.getWorld().getName()+","+loc.getBlockX()+","+loc.getBlockY()+","+loc.getBlockZ();
    		}
    		Location loc = player.getLocation();
    		return loc.getWorld().getName()+","+loc.getBlockX()+","+loc.getBlockY()+","+loc.getBlockZ();
		} });
    	addPlaceholder(new Placeholder("storm") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
    		if (modifiers.length==1) {
    			Location loc = getloc(modifiers[0], player);
    			return ""+loc.getWorld().hasStorm();
    		}
    			return ""+player.getLocation().getWorld().hasStorm();
		} });
    	addPlaceholder(new Placeholder("thunder") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
    		if (modifiers.length==1) {
    			Location loc = getloc(modifiers[0], player);
    			return ""+loc.getWorld().isThundering();
    		}
    			return ""+player.getLocation().getWorld().isThundering();
		} });
    	addPlaceholder(new Placeholder("x") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
    		if (modifiers.length==1) {
				return String.valueOf(Math.floor(getloc(modifiers[0], player).getX()));
    		}
			return String.valueOf(Math.floor(player.getLocation().getX()));
		} });
    	addPlaceholder(new Placeholder("y") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
    		if (modifiers.length==1) {
				return String.valueOf(Math.floor(getloc(modifiers[0], player).getZ()));
    		}
			return String.valueOf(Math.floor(player.getLocation().getY()));
		} });
    	addPlaceholder(new Placeholder("z") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
    		if (modifiers.length==1) {
				return String.valueOf(Math.floor(getloc(modifiers[0], player).getZ()));
    		}
			return String.valueOf(Math.floor(player.getLocation().getZ()));
		} });
    	addPlaceholder(new Placeholder("sneaking") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
    		if (modifiers.length==1) {
    			return ""+Bukkit.getPlayer(modifiers[0]).isSneaking();
    		}
			return ""+player.isSneaking();
		} });
    	addPlaceholder(new Placeholder("itempickup") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
    		if (modifiers.length==1) {
    			return ""+Bukkit.getPlayer(modifiers[0]).getCanPickupItems();
    		}
			return ""+player.getCanPickupItems();
		} });
    	addPlaceholder(new Placeholder("flying") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
    		if (modifiers.length==1) {
    			return ""+Bukkit.getPlayer(modifiers[0]).getAllowFlight();
    		}
			return ""+player.getAllowFlight();
		} });
    	addPlaceholder(new Placeholder("grounded") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
    		if (modifiers.length==1) {
    			if (Bukkit.getPlayer(modifiers[0])==null) {
    				try {
    	    			ImprovedOfflinePlayer offlineplayer = new ImprovedOfflinePlayer(modifiers[0]);
    	    			return ""+offlineplayer.getIsOnGround();
    	        		}
    	        		catch (Exception e) {
    	        			IOP_1_7_2 offlineplayer = new IOP_1_7_2(modifiers[0]);
    	        			return ""+offlineplayer.getIsOnGround();
    	        		}
    			}
    			return ""+Bukkit.getPlayer(modifiers[0]).isOnGround();
    		}
			return ""+player.isOnGround();
		} });
    	addPlaceholder(new Placeholder("blocking") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
    		if (modifiers.length==1) {
    			return ""+Bukkit.getPlayer(modifiers[0]).isBlocking();
    		}
			return ""+player.isBlocking();
		} });
    	addPlaceholder(new Placeholder("passenger") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
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
		} });
    	addPlaceholder(new Placeholder("maxhealth") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
    		if (modifiers.length==1) {
    			return ""+Bukkit.getPlayer(modifiers[0]).getMaxHealth();
    		}
			return ""+player.getMaxHealth();
		} });
    	addPlaceholder(new Placeholder("maxair") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
    		if (modifiers.length==1) {
    			return ""+Bukkit.getPlayer(modifiers[0]).getMaximumAir();
    		}
			return ""+player.getMaximumAir();
		} });
    	addPlaceholder(new Placeholder("age") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
    		if (modifiers.length==1) {
    			return ""+(Bukkit.getPlayer(modifiers[0]).getTicksLived()/20);
    		}
    		return ""+(player.getTicksLived()/20);
		} });
    	addPlaceholder(new Placeholder("sound") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
    		if (getClicked().equals("false")==false) {
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
    		}
    		return "";
		} });
    	addPlaceholder(new Placeholder("compass") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
    		if (modifiers.length==1) {
    			player = Bukkit.getPlayer(modifiers[0]);
    			return ""+player.getCompassTarget().getX()+","+player.getCompassTarget().getY()+","+player.getCompassTarget().getZ();
    		}
    		return ""+player.getCompassTarget().getX()+","+player.getCompassTarget().getY()+","+player.getCompassTarget().getZ();
		} });
    	addPlaceholder(new Placeholder("sleeping") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
    		if (modifiers.length==1) {
    			if (Bukkit.getPlayer(modifiers[0])==null) {
    				try {
    	    			ImprovedOfflinePlayer offlineplayer = new ImprovedOfflinePlayer(modifiers[0]);
    	    			return ""+offlineplayer.getTotalExperience();
    	        		}
    	        		catch (Exception e) {
    	        			IOP_1_7_2 offlineplayer = new IOP_1_7_2(modifiers[0]);
    	        			return ""+offlineplayer.getIsSleeping();
    	        		}
    			}
    			return ""+Bukkit.getPlayer(modifiers[0]).isSleeping();
    		}
			return ""+player.isSleeping();
		} });
    	addPlaceholder(new Placeholder("dead") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
    		if (modifiers.length==1) {
    			return ""+Bukkit.getPlayer(modifiers[0]).isDead();
    		}
			return ""+player.isDead();
		} });
    	addPlaceholder(new Placeholder("isclick") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
    		return ""+getClicked();
		} });
    	addPlaceholder(new Placeholder("whitelisted") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
    		if (modifiers.length==1) {
    			if (Bukkit.getPlayer(modifiers[0])==null) {
    				return ""+Bukkit.getOfflinePlayer(modifiers[0]).isWhitelisted();
    			}
    			return ""+Bukkit.getPlayer(modifiers[0]).isWhitelisted();
    		}
			return ""+player.isWhitelisted();
		} });
    	addPlaceholder(new Placeholder("world") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
    		if (modifiers.length==1) {
    			return getloc(modifiers[0], player).getWorld().getName();
    		}
			return ""+player.getWorld().getName();
		} });
    	addPlaceholder(new Placeholder("ip") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
    		if (modifiers.length==1) {
    			player = Bukkit.getPlayer(modifiers[0]);
    			return player.getAddress().getAddress().toString().split("/")[(player.getAddress().toString().split("/").length)-1].split(":")[0];
    		}
    		return player.getAddress().getAddress().toString().split("/")[(player.getAddress().toString().split("/").length)-1].split(":")[0];
		} });
    	addPlaceholder(new Placeholder("line1") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
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
    	addPlaceholder(new Placeholder("line2") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
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
    	addPlaceholder(new Placeholder("line3") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
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
    	addPlaceholder(new Placeholder("line4") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
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
    	addPlaceholder(new Placeholder("blockloc") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
    		return location.getWorld().getName()+","+location.getBlockX()+","+location.getBlockY()+","+location.getBlockZ();
		} });
    	addPlaceholder(new Placeholder("uses") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
    		if (modifiers.length==1) {
    			int maxclicks = Integer.parseInt(modifiers[0]);
    			for (int i = 0;i<list.size();i++) {
	        		if (list.get(i).equals(location)&&players.get(i).equals(player.getName())) {
	        			int myclicks = clicks.get(i);
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
    		for (int i = 0;i<list.size();i++) {
        		if (list.get(i).equals(location)&&players.get(i).equals(player.getName())) {
        			return ""+clicks.get(i);
        		}
        	}
			return "0";
		} });
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
					            	if (isf==null) {
					            	PacketContainer packet = protocolmanager.createPacket(PacketType.Play.Server.UPDATE_SIGN);
					            	try {
					            		packet.getSpecificModifier(Integer.TYPE).write(0, Integer.valueOf(sign.getX()));
					            		packet.getSpecificModifier(Integer.TYPE).write(1, Integer.valueOf(sign.getY()));
					            		packet.getSpecificModifier(Integer.TYPE).write(2, Integer.valueOf(sign.getZ()));
					            		packet.getStringArrays().write(0, sign.getLines());
					            		setClicked("false");
					            		protocolmanager.sendServerPacket(player, packet);
					            	}
					            	catch (Exception e) {
					            		e.printStackTrace();
					            	}
					            	}
					            	else {
//					            		isf.sendSignChange(player,sign);
					            	}
								}
					            else if (dist > (Bukkit.getViewDistance()*24)*(Bukkit.getViewDistance()*24)) {
					            	list.remove(timerlast);
					            	clicks.remove(timerlast);
									players.remove(timerlast);
					            }
								}
								else {
					            	list.remove(timerlast);
					            	clicks.remove(timerlast);
									players.remove(timerlast);
								}
							}
							else {
				            	list.remove(timerlast);
				            	clicks.remove(timerlast);
								players.remove(timerlast);
							}
						}
						else {
			            	list.remove(timerlast);
			            	clicks.remove(timerlast);
							players.remove(timerlast);
							}
						}
						else {
			            	list.remove(timerlast);
			            	clicks.remove(timerlast);
							players.remove(timerlast);
						}
					}
					catch (Exception e) {
		            	list.remove(timerlast);
		            	clicks.remove(timerlast);
						players.remove(timerlast);
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
					islagging = true;
					if (list.size()>getConfig().getInt("signs.autoupdate.buffer")) {
						list = list.subList(10,list.size());
						players = players.subList(10,players.size());
						clicks = clicks.subList(10,clicks.size());
					}
				}
				else {
					if (list.size()>getConfig().getInt("signs.autoupdate.buffer")*4) {
						list = list.subList(10,list.size());
						players = players.subList(10,players.size());
						clicks = clicks.subList(10,clicks.size());
					}
					islagging = false;
				}
			if (counter2 > 1200) {
				try {
				counter2 = 0;
				msg(null,"&9[&bISP&9] &fSAVING VARIABLES!");
				getConfig().getConfigurationSection("scripting").set("variables", null);
		        for (final Entry<String, Object> node : globals.entrySet()) {
		        	getConfig().options().copyDefaults(true);
		        	getConfig().set("scripting.variables."+(""+node.getKey()).substring(1,(""+node.getKey()).length()-1), (""+node.getValue()));
		        	
		        	saveConfig();
		        }
		        msg(null,"DONE!");
				}
				catch (Exception e) {
					
				}
			}
		}
	}
		
	};
	@EventHandler
	public void onSignChange(SignChangeEvent event) {
		try {
		String lines = StringUtils.join(event.getLines());
		if (lines.length()<2) {
			return;
		}
		if (lines.contains("{")==false) {
			return;
		}
		if (lines.contains("}")==false) {
			return;
		}
		List<String> tocheck = new ArrayList();
		List<String> mylist = getPlaceholderKeys();
		for(String current:mylist){
			if(lines.contains("{"+current+"}")) {
				tocheck.add(current);
			}
			else if(lines.contains("{"+current+":")) {
				tocheck.add(current);
			}
		}
		Player player = event.getPlayer();
		if (checkperm(player, "insignsplus.create.*")) {
			return;
		}
		if (checkperm(player, "insignsplus.create")==false) {
			msg(player,"&6Missing requirements&7: insignsplus.create");
			event.setCancelled(true);
			return;
		}
		boolean tocancel = false;
		for(String current:tocheck) {
			if (checkperm(player, "insignsplus.create."+current)) {
				
			}
			else {
				if (tocancel==false) {
					msg(player,"&6Missing requirements&7:\n");
				}
				msg(player,"&7 - &7insignsplus.create."+current);
				tocancel = true;
			}
		}
		if (tocancel) {
			event.setCancelled(true);
			return;
		}
		}
		catch (Exception e) {
		}
	}
	
	
	public void updateSign(Player player,Location location) {
		PacketContainer packet = protocolmanager.createPacket(PacketType.Play.Server.UPDATE_SIGN);
    	try {
    		Sign sign = (Sign) (location.getBlock().getState());
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
	
	@EventHandler
    private void onPlayerInteract(PlayerInteractEvent event)
    {
		if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
			setClicked("true");
		}
		if (event.isCancelled()) {
			return;
		}
		if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			Block block = event.getClickedBlock();
	        if ((block.getType() == Material.SIGN_POST) || (block.getType() == Material.WALL_SIGN)) {
	        	for (int i = 0;i<list.size();i++) {
	        		if (list.get(i).equals(block.getLocation())&&players.get(i).equals(event.getPlayer().getName())) {
	        			clicks.set(i, clicks.get(i)+1);
	        		}
	        	}
	        	if (isf==null) {
	            	try {
	            		Sign sign = (Sign)block.getState();
	            		if (sign==null) {
	            			return;
	            		}
	            		sign.getLines();
	            		PacketContainer packet = protocolmanager.createPacket(PacketType.Play.Server.UPDATE_SIGN);
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