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








import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scoreboard.Scoreboard;

public final class InSignsPlus extends JavaPlugin implements Listener {
	private boolean isenabled = false;
    private static int counter0 = 0;
    private static int counter = 0;
    private static int counter2 = 0;
    private static Map<String, Object> globals = new HashMap<String, Object>();
    int recursion = 0;
	List<Location> list = new ArrayList();
	List<String> players = new ArrayList();
	private List<Integer> clicks = new ArrayList();
	private long timerstart = 0;
	private boolean islagging = false;
	private int timerlast = 0;
	InSignsPlus plugin;
	final Map<String, Placeholder> placeholders = new HashMap<String, Placeholder>();
	final Map<String, Placeholder> defaultplaceholders = new HashMap<String, Placeholder>();
	private Player currentplayer = null;
	private Player currentsender = null;
	private String holoclicked = "false";
	private Plugin individualmessages = null;
	private ProtocolClass protocolclass;
//	final Map<String, String> customplaceholders = new HashMap<String, String>();
	
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
					IOP_1_7_9 offlineplayer = new IOP_1_7_9(string);
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
				catch (Exception e1) {
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
//    	try {
//			if (customplaceholders.containsKey(mysplit[0])) {
//				String mycommands = customplaceholders.get(mysplit[0]);
//				if (mysplit.length>1) {
//					for(int i = 0; i < mysplit.length; i++) {
//	    				mycommands = mycommands.replace("{arg"+i+"}", mysplit[i]);
//	    			}
//		    	}
//				return execute(mycommands,elevation,interact);
//			}
//		}
//		catch (Exception e) {
//
//		}
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
    String colorise(String mystring) {
    	String[] codes = {"&1","&2","&3","&4","&5","&6","&7","&8","&9","&0","&a","&b","&c","&d","&e","&f","&r","&l","&m","&n","&o","&k"};
    	for (String code:codes) {
    		mystring = mystring.replace(code, "§"+code.charAt(1));
    	}
    	return mystring;
    }
    boolean checkperm(Player player,String perm) {
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
    void msg(Player player,String mystring) {
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
    			if ((args[0].equalsIgnoreCase("setline"))){
    				if (player==null) {
    					return false;
    				}
    				if (checkperm(player,"isp.setline")) {
    					if (args.length<2) {
    						msg(player,"&7Evaluate and set a line of a sign:");
    						msg(player,"&c/isp setline <index> <value>");
    						return false;
    					}
    					try {
    		    			Block block = (player.getTargetBlock(null, 200).getLocation()).getBlock();
    		    			Sign sign = (Sign) block.getState();
    		    			try {
    		    				boolean hasperm = checkperm(player,"isp.setline.override");
    		    				if (hasperm == false) {
    		    					BlockBreakEvent mybreak = new BlockBreakEvent(block, player);
    		    					Bukkit.getServer().getPluginManager().callEvent(mybreak);
    		    					if (mybreak.isCancelled()) {
    		    						hasperm = false;
    		    					}
    		    					else {
    		    						hasperm = true;
    		    					}
    		    					BlockPlaceEvent place = new BlockPlaceEvent(block, block.getState(), block, null, player, true);
    		    					Bukkit.getServer().getPluginManager().callEvent(place);
    		    					if (place.isCancelled()) {
    		    						hasperm = false;
    		    					}
    		    				}
    		    				else {
    		    				}
    		    				if (hasperm) {
    			    				String line = "";
    			    				for(int i = 2; i < args.length; i++) {
    			    					line+=args[i]+" ";
    			    				}
    			    				setSender(player);
    			    				setUser(player);
    			    				if (line.contains("{line}")) {
    			    					line = line.replace("{line}", sign.getLine(Integer.parseInt(args[1])-1));
    			    				}
    				    			sign.setLine(Integer.parseInt(args[1])-1, colorise(evaluate(line, false, block.getLocation())).trim());
    				    			setSender(null);
    			    				setUser(null);
    				    			sign.update(true);
    				    			msg(player,"&7Updated sign successfully.");
    				    			return true;
    		    				}
    		    				else {
    		    					msg(player,"&6Missing requirements&7: insignsplus.setline.override");
    		    				}
    		    			}
    		    			catch (Exception e) {
    		    				msg(player,"&cYou must be looking at a sign.");
    		    			}
    		    		}
    		    		catch (Exception e) {
    		    			msg(player,"&7Invalid arguments:");
    		    			msg(player,"&c/isp setline <index> <value>");
    		    		}
    				}
    				else {
    					msg(player,"&6Missing requirements&7: insignsplus.setline");
    				}
    				return false;
    			}
    			else if ((args[0].equalsIgnoreCase("help"))){
    				if (args.length == 2) {
    					try {
    						if (args[1].contains("{")) {
    							if (args[1].contains("}")) {
    								args[1] = args[1].substring(1,args[1].length()-1);
    							}
    						}
    					}
    					catch (Exception e) {
    						
    					}
    					if (individualmessages!=null) {
    						new InMeHook(true, individualmessages);
    					}
    					Placeholder placeholder = getPlaceholder(args[1]);
    					if (placeholder==null) {
    						msg(player,"&7Unknown placeholder &c"+args[1]+"&7 - Try &a/im list");
    					}
    					else {
    						String description = "&aDescription:\n&a> &c"+placeholder.getDescription().replace("\n", "\n&a> &c"); 
    						msg(player,description);
    						if (description.contains(":*")) {
    							msg(player, "&f &c* &7indicates optional arguments");
    						}
    					}
    					if (individualmessages!=null) {
    						new InMeHook(false, individualmessages);
    					}
    					return true;
    				}
    			}
    			else if ((args[0].equalsIgnoreCase("list"))){
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
    					if (individualmessages!=null) {
    						new InMeHook(true, individualmessages);
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
    					if (individualmessages!=null) {
    						new InMeHook(false, individualmessages);
    					}
        				return true;
    				}
    				else {
    					if (individualmessages!=null) {
    						new InMeHook(true, individualmessages);
    					}
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
        				if (individualmessages!=null) {
    						new InMeHook(false, individualmessages);
    					}
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
    			        File[] myph = f1.listFiles();
    			        for (int i = 0; i < myph.length; i++) {
    			        	if (myph[i].isFile()) {
    			        		if (myph[i].getName().contains(".yml")) {
    			        			try {
    			        			final FileConfiguration yml = YamlConfiguration.loadConfiguration(myph[i]);
    			        			String name = myph[i].getName().substring(0,myph[i].getName().length()-4);
    			        			final String lines = StringUtils.join(yml.getStringList("script"),";");
    			        			final String description;
    			        			if (yml.contains("description" )) {
    			    					description = yml.getString("description");
    			    				}
    			        			else {
    			        				 description = "There is currently no description";
    			        			}
    			        			final boolean myelevation;
    			        			final boolean asconsole;
    			        			if (yml.contains("elevation")) {
    			        				if (yml.getString("elevation").equalsIgnoreCase("operator")) {
    			        					myelevation = true;
    			        					asconsole = false;
    			        				}
    			        				else if (yml.getString("elevation").equalsIgnoreCase("console")) {
    			        					myelevation = true;
    			        					asconsole = true;
    			        				}
    			        				else {
    			        					myelevation = true;
    			        					asconsole = false;
    			        				}
    			    				}
    			        			else {
    			        				asconsole = false;
    			        				myelevation = false;
    			        			}
    			        			addPlaceholder(new Placeholder(name) { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
    			    					if (asconsole) {
    			    						setUser(null);
    			    					}
    			    					String toreturn = execute(lines, myelevation, location);
    			    					if (asconsole) {
    			    						setUser(player);
    			    					}
    			    					return toreturn;
    			        			}
    			        			@Override 
    			        			public String getDescription() {
    			    					return description;
    			        			}
    			        			
    			        			});
//    			        			customplaceholders.put(name, lines);
    			        			}
    			        			catch (Exception e2) {
    			        				msg(null,"&cError with file "+getDataFolder()+"/scripts/"+myph[i].getName()+".");
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
    			msg(player,"&7Commands:\n&7 - &a/isp help <placeholder> \n&7 - &a/isp reload\n&7 - &a/isp save\n&7 - &a/isp list\n&7 - &a/isp enable\n&7 - &a/isp disable");
    		}
    	}
    	return true;
	}

    
    public boolean testif(String mystring, boolean elevation, Location interact) {
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
			if (splittype == 1) { if (result3.equals(result4)) { toreturn = true; } }
    		else if (splittype == 2) { if (result3>result4) { toreturn = true; } }
    		else if (splittype == 3) { if (result3<result4) { toreturn = true; } }
    		else if (splittype == 4) { if (result3>=result4) { toreturn = true; } }
    		else if (splittype == 5) { if (result3<=result4) { toreturn = true; } }
    		else if (splittype == 6) { if (result3.equals(result4)==false) { toreturn = true; } }
			return toreturn;
		}
		catch (Exception e) {
			
		}
		try {
			Integer result3 = Integer.parseInt(""+result1);
			Integer result4 = Integer.parseInt(""+result2);
			if (splittype == 1) { if (result3.equals(result4)) { toreturn = true; } }
    		else if (splittype == 2) { if (result3>result4) { toreturn = true; } }
    		else if (splittype == 3) { if (result3<result4) { toreturn = true; } }
    		else if (splittype == 4) { if (result3>=result4) { toreturn = true; } }
    		else if (splittype == 5) { if (result3<=result4) { toreturn = true; } }
    		else if (splittype == 6) { if (result3.equals(result4)==false) { toreturn = true; } }
			return toreturn;
		}
		catch (Exception e) {
			
		}
		try {
			Float result3 = Float.parseFloat(""+result1);
			Float result4 = Float.parseFloat(""+result2);
			if (splittype == 1) { if (result3.equals(result4)) { toreturn = true; } }
    		else if (splittype == 2) { if (result3>result4) { toreturn = true; } }
    		else if (splittype == 3) { if (result3<result4) { toreturn = true; } }
    		else if (splittype == 4) { if (result3>=result4) { toreturn = true; } }
    		else if (splittype == 5) { if (result3<=result4) { toreturn = true; } }
    		else if (splittype == 6) { if (result3.equals(result4)==false) { toreturn = true; } }
			return toreturn;
		}
		catch (Exception e) {
			
		}
		try {
			Long result3 = Long.parseLong(""+result1);
			Long result4 = Long.parseLong(""+result2);
			if (splittype == 1) { if (result3.equals(result4)) { toreturn = true; } }
    		else if (splittype == 2) { if (result3>result4) { toreturn = true; } }
    		else if (splittype == 3) { if (result3<result4) { toreturn = true; } }
    		else if (splittype == 4) { if (result3>=result4) { toreturn = true; } }
    		else if (splittype == 5) { if (result3<=result4) { toreturn = true; } }
    		else if (splittype == 6) { if (result3.equals(result4)==false) { toreturn = true; } }
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
	void isadd(Player player, Location loc) {
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
				  hasperm = testif(mycommand, elevation, interact);
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
				if (cmdargs[0].equalsIgnoreCase("return")){
					 return mycommand.substring(7,mycommand.length());
				 }
				else if (cmdargs[0].equalsIgnoreCase("do")){
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
    
    public synchronized void whitelistPlaceholder(String key) {
    	List<String> mylist;
    	try {
    		mylist = getConfig().getStringList("signs.autoupdate.whitelist");
    		
    	}
    	catch (Exception e) {
    		mylist = new ArrayList<String>();
    		mylist.add(key);
    	}
    	getConfig().set("signs.autoupdate.whitelist", mylist);
    	saveConfig();
    }
    public synchronized void whitelistPlaceholder(Placeholder placeholder) {
    	String key = placeholder.getKey();
    	List<String> mylist;
    	try {
    		mylist = getConfig().getStringList("signs.autoupdate.whitelist");
    		
    	}
    	catch (Exception e) {
    		mylist = new ArrayList<String>();
    		mylist.add(key);
    	}
    	getConfig().set("signs.autoupdate.whitelist", mylist);
    	saveConfig();
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
		String version = "0.7.9";
		Plugin protocolLibPlugin = Bukkit.getServer().getPluginManager().getPlugin("ProtocolLib");
		if (protocolLibPlugin==null) {
			msg(null,"&c[SEVERE] &fInSignsPlus requires ProtocolLib to run properly. Please install it.!");
			msg(null,"&c[SEVERE] &fPlease do not ignore this message!");
//			Bukkit.getServer().getPluginManager().disablePlugin(this);
//			return;
		}
		else {
			protocolclass = new ProtocolClass(this);
		}
		plugin = this;
		
		//TODO SEND MESSAGE IF VAULT IS NOT ENABLED.
		
        saveResource("english.yml", true);
        Plugin insignsPlugin = Bukkit.getServer().getPluginManager().getPlugin("InSigns");
        if((insignsPlugin != null)) {
        	if (insignsPlugin.isEnabled()) {
	            msg(null,"&7[Info] Plugin '&aInSigns&7' detected.");
        	}
        }
        Plugin inmePlugin = Bukkit.getServer().getPluginManager().getPlugin("IndividualMessages");
        if((inmePlugin != null)) {
        	if (inmePlugin.isEnabled()) {
	            msg(null,"&7[Info] Plugin '&aIndividualMessages&7' detected. Hooking into it now.");
	            individualmessages = inmePlugin;
        	}
        }
        isenabled = true;
        boolean toupdate = false;
        try {
        	if (getConfig().getString("version").equals(version)==false) {
        		msg(null,"&7Thanks for updating &aInSignsPlus&7!");
        		toupdate = true;
        	}
        }
        catch (Exception e) {
        	toupdate = true;
        }
        if (toupdate) {
	        File f8 = new File(getDataFolder() + File.separator+"scripts"+File.separator+"example.yml");
	        if(f8.exists()!=true) {  saveResource("scripts"+File.separator+"example.yml", false); }
	        File f9 = new File(getDataFolder() + File.separator+"scripts"+File.separator+"test.js");
	        if(f9.exists()!=true) {  saveResource("scripts"+File.separator+"test.js", false); }
        }
        File f1 = new File(getDataFolder() + File.separator + "scripts");
        File[] myph = f1.listFiles();
        for (int i = 0; i < myph.length; i++) {
        	if (myph[i].isFile()) {
        		if (myph[i].getName().contains(".yml")) {
        			try {
        			final FileConfiguration yml = YamlConfiguration.loadConfiguration(myph[i]);
        			String name = myph[i].getName().substring(0,myph[i].getName().length()-4);
        			final String lines = StringUtils.join(yml.getStringList("script"),";");
        			final String description;
        			if (yml.contains("description" )) {
    					description = yml.getString("description");
    				}
        			else {
        				 description = "There is currently no description";
        			}
        			final boolean myelevation;
        			final boolean asconsole;
        			if (yml.contains("elevation")) {
        				if (yml.getString("elevation").equalsIgnoreCase("operator")) {
        					myelevation = true;
        					asconsole = false;
        				}
        				else if (yml.getString("elevation").equalsIgnoreCase("console")) {
        					myelevation = true;
        					asconsole = true;
        				}
        				else {
        					myelevation = true;
        					asconsole = false;
        				}
    				}
        			else {
        				asconsole = false;
        				myelevation = false;
        			}
        			addPlaceholder(new Placeholder(name) { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
    					if (asconsole) {
    						setUser(null);
    					}
    					String toreturn = execute(lines, myelevation, location);
    					if (asconsole) {
    						setUser(player);
    					}
    					return toreturn;
        			}
        			@Override 
        			public String getDescription() {
    					return description;
        			}
        			
        			});
//        			customplaceholders.put(name, lines);
        			}
        			catch (Exception e2) {
        				msg(null,"&cError with file "+getDataFolder()+"/scripts/"+myph[i].getName()+".");
        			}
        		}
        	}
        }
	        
        getConfig().options().copyDefaults(true);
        final Map<String, Object> options = new HashMap<String, Object>();
        getConfig().set("version", version);
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
    	saveDefaultConfig();
    	Bukkit.getServer().getPluginManager().registerEvents(this, this);
    	if (getConfig().getInt("signs.autoupdate.interval")>0) {
    		timer.schedule (mytask,0l, 1);
    	}
    	
    	//TODO ADD PLACEHOLDERS
    	addPlaceholder(new Placeholder("u") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
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
    	addPlaceholder(new Placeholder("rand") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
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
    	addPlaceholder(new Placeholder("msg") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
			return getmsg(modifiers[0]);
			}
	    	@Override 
			public String getDescription() {
				return "{msg:ID} - Returns the given message from the language file";
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
			}
	    	@Override 
			public String getDescription() {
				return "{range:X} - Returns a list from 0 to X\n{range:X,Y}	- Returns a list from X to Y";
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
		}
    	@Override 
		public String getDescription() {
			return "{matchplayer:STRING} - Returns the closest matching online player";
		} });
    	addPlaceholder(new Placeholder("index") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
    		return modifiers[0].split(",")[Integer.parseInt(modifiers[1])];
		}
    	@Override 
		public String getDescription() {
			return "{index:LIST:INDEX} - Returns the item at INDEX in a list";
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
		}
    	@Override 
		public String getDescription() {
			return "{setindex:LIST:INDEX:VALUE} - Returns a new list with the item set at index";
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
		}
    	@Override 
		public String getDescription() {
			return "{delindex:LIST:INDEX:VALUE} - Returns the list with the item deleted at the index";
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
		}
    	@Override 
		public String getDescription() {
			return "{sublist:X:Y} - Returns a new list from index X to index Y";
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
		}
    	@Override 
		public String getDescription() {
			return "{getindex:LIST:VALUE} - Returns the index for an item in the list";
		} });
    	addPlaceholder(new Placeholder("listhas") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
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
    	addPlaceholder(new Placeholder("contains") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
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
    	addPlaceholder(new Placeholder("substring") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
    		return modifiers[0].substring(Integer.parseInt(modifiers[1]), Integer.parseInt(modifiers[2]));
		}
    	@Override 
		public String getDescription() {
			return "{substring:X:Y} - Returns part of the string from index X to index Y";
		} });
    	addPlaceholder(new Placeholder("size") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
			return ""+modifiers[0].split(",").length;
		}
    	@Override 
		public String getDescription() {
			return "{size:LIST} - Returns the size of a list";
		} });
    	addPlaceholder(new Placeholder("length") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
			return ""+modifiers[0].length();
		}
    	@Override 
		public String getDescription() {
			return "{length:STRING} - Returns the length of the string";
		} });
    	addPlaceholder(new Placeholder("split") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
    		return modifiers[0].replace(modifiers[1],",");
		}
    	@Override 
		public String getDescription() {
			return "{split:LIST:DELIMETER} - Splits a string by a specified delimeter";
		} });
    	addPlaceholder(new Placeholder("hasperm") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
    		if (player==null) {
    			return "true";
    		}
    		else if (checkperm(player,modifiers[0])) {
    			return "true";
    		}
    		return "false";
		}
    	@Override 
		public String getDescription() {
			return "{hasperm:NODE} - Returns true if a player has the permission";
		} });
    	addPlaceholder(new Placeholder("randchoice") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
    		String[] mylist = modifiers[0].split(",");
    		Random random = new Random();
    		return mylist[random.nextInt(mylist.length-1)];
		}
    	@Override 
		public String getDescription() {
			return "{randchoice:LIST} - Returns a random choice from a list";
		} });
    	addPlaceholder(new Placeholder("worldtype") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
			if (modifiers.length==1) {
	    		Location loc = getloc(modifiers[0], player);
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
		}
    	@Override 
		public String getDescription() {
			return "{listreplace:VALUE:VALUE2} - Returns a new list with occurrences of VALUE replaced with VALUE2";
		} });

    	addPlaceholder(new Placeholder("worldticks") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
			if (modifiers.length==1) {
				Location loc = getloc(modifiers[0], player);
	    		return Long.toString(loc.getWorld().getTime());
			}
    		return Long.toString(player.getWorld().getTime());
		}
    	@Override 
		public String getDescription() {
			return "{worldticks:*location} - Returns the time in ticks for a world";
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
		}
    	@Override 
		public String getDescription() {
			return "{time:*location} - The time (24hr) in a world";
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
    	addPlaceholder(new Placeholder("localtime") { @SuppressWarnings("deprecation")
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
    	addPlaceholder(new Placeholder("date") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
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
    	addPlaceholder(new Placeholder("localtime12") { @SuppressWarnings("deprecation")
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
		}
    	@Override 
		public String getDescription() {
			return "{time12:*location} - The time (12hr) in a MC world";
		} });
    	addPlaceholder(new Placeholder("replace") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
    		return modifiers[0].replace(modifiers[1], modifiers[2]);
		}
    	@Override 
		public String getDescription() {
			return "{replace:VALUE:VALUE2} - Returns a new string with occurrences of VALUE replaced with VALUE2";
		} });
    	addPlaceholder(new Placeholder("config") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
    		return getConfig().getString(modifiers[0]);
		}
    	@Override 
		public String getDescription() {
			return "{config:NODE} - Returns the value from the config for the given node";
		} });
    	addPlaceholder(new Placeholder("structures") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
    		if (modifiers.length==1) {
        		Location loc = getloc(modifiers[0], player);
        		return loc.getWorld().canGenerateStructures()+"";
    		}
    		return ""+player.getWorld().canGenerateStructures();
		}
    	@Override 
		public String getDescription() {
			return "{structures:*location} - Returns if structure generation is enabled for a world";
		} });
    	addPlaceholder(new Placeholder("autosave") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
    		if (modifiers.length==1) {
    			Location loc = getloc(modifiers[0], player);
        		return loc.getWorld().isAutoSave()+"";
    		}
    		return ""+player.getWorld().isAutoSave();
		}
    	@Override 
		public String getDescription() {
			return "{autosave:*location} - Returns true if autosaving is enabled";
		} });
    	addPlaceholder(new Placeholder("animals") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
    		if (modifiers.length==1) {
        		Location loc = getloc(modifiers[0], player);
        		return loc.getWorld().getAllowAnimals()+"";
    		}
    		return ""+player.getWorld().getAllowAnimals();
		}
    	@Override 
		public String getDescription() {
			return "{animals:*location} - Returns true if animals are enabled";
		} });
    	addPlaceholder(new Placeholder("monsters") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
    		if (modifiers.length==1) {
        		Location loc = getloc(modifiers[0], player);
        		return loc.getWorld().getAllowMonsters()+"";
    		}
    		return ""+player.getWorld().getAllowMonsters();
		}
    	@Override 
		public String getDescription() {
			return "{monsters:*location} - Returns true if monsters are enabled";
		} });
    	addPlaceholder(new Placeholder("online") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
    		if (modifiers.length==1) {
        		getloc(modifiers[0], player);
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
    	addPlaceholder(new Placeholder("colors") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
    		return "&1,&2,&3,&4,&5,&6,&7,&8,&9,&0,&a,&b,&c,&d,&e,&f,&r,&l,&m,&n,&o,&k";
		}
    	@Override 
		public String getDescription() {
			return "{colors} - Returns a list of color codes";
		} });
    	addPlaceholder(new Placeholder("difficulty") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
    		if (modifiers.length==1) {
        		Location loc = getloc(modifiers[0], player);
        		return loc.getWorld().getDifficulty().toString();
    		}
    		return ""+player.getWorld().getDifficulty().name();
		}
    	@Override 
		public String getDescription() {
			return "{difficulty:*location} - Returns the difficulty for a world";
		} });
    	addPlaceholder(new Placeholder("weatherduration") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
    		if (modifiers.length==1) {
        		Location loc = getloc(modifiers[0], player);
        		return ""+loc.getWorld().getWeatherDuration();
    		}
    		return ""+player.getWorld().getWeatherDuration();
		}
    	@Override 
		public String getDescription() {
			return "{weatherduration:*location} - Returns the duration in ticks of the weather";
		} });
    	addPlaceholder(new Placeholder("environment") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
    		if (modifiers.length==1) {
        		Location loc = getloc(modifiers[0], player);
        		return loc.getWorld().getEnvironment().toString();
    		}
    		return ""+player.getWorld().getEnvironment().name();
		}
    	@Override 
		public String getDescription() {
			return "{environment:*location} - Returns the environment at a location(e.g. NETHER, END)";
		} });
    	addPlaceholder(new Placeholder("player") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
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
    	addPlaceholder(new Placeholder("gvar") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
    		return StringUtils.join(globals.keySet(),",").replace("{","").replace("}", "");
		}
    	@Override 
		public String getDescription() {
			return "{gvar} - Returns a list of global variables";
		} });
    	addPlaceholder(new Placeholder("sender") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
    		Player sender = getSender();
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
    	addPlaceholder(new Placeholder("elevated") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
    		return ""+elevation;
		}
    	@Override 
		public String getDescription() {
			return "{elevated} - Returns true if the script is elevated";
		} });
    	addPlaceholder(new Placeholder("gamerules") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
    		if (modifiers.length==1) {
        		Location loc = getloc(modifiers[0], player);
        		return StringUtils.join(loc.getWorld().getGameRules(),",");
    		}
    		return StringUtils.join(player.getWorld().getGameRules(),",");
		}
    	@Override 
		public String getDescription() {
			return "{gamerules} - Returns the list of gamerules";
		} });
    	addPlaceholder(new Placeholder("seed") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
    		if (modifiers.length==1) {
        		Location loc = getloc(modifiers[0], player);
        		return ""+loc.getWorld().getSeed();
    		}
    		return ""+player.getWorld().getSeed();
		}
    	@Override 
		public String getDescription() {
			return "{seed:*location} - Returns the seed for a world";
		} });
    	addPlaceholder(new Placeholder("spawn") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
    		if (modifiers.length==1) {
    			Location loc = getloc(modifiers[0], player);
        		return loc.getWorld().getName()+","+loc.getWorld().getSpawnLocation().getX()+","+loc.getWorld().getSpawnLocation().getY()+","+loc.getWorld().getSpawnLocation().getZ();
    		}
    		return location.getWorld().getName()+","+location.getWorld().getSpawnLocation().getX()+","+location.getWorld().getSpawnLocation().getY()+","+location.getWorld().getSpawnLocation().getZ();
		}
    	@Override 
		public String getDescription() {
			return "{spawn:*location} - Returns the spawn location for a world";
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
		}
    	@Override 
		public String getDescription() {
			return "{count:LIST:VALUE} - Returns the number of times a value appears in a list";
		} });
    	addPlaceholder(new Placeholder("epoch") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
    		return Long.toString(System.currentTimeMillis()/1000);
		}
    	@Override 
		public String getDescription() {
			return "{epoch} - Returns the seconds since the epoch";
		} });
    	addPlaceholder(new Placeholder("js") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
    		return javascript(StringUtils.join(modifiers,":"));
		}
    	@Override 
		public String getDescription() {
			return "{js:SCRIPT} - Useful for basic math e.g. {js:1+1} but can do any javascript action\n{jsg:FILE.js} - Javascript files are located in the scripts folder for the plugin";
		} });
    	addPlaceholder(new Placeholder("javascript") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
    		return javascript(StringUtils.join(modifiers,":"));
		}
    	@Override 
		public String getDescription() {
			return "{javascript:SCRIPT} - Useful for basic math e.g. {javascript:1+1} but can do any javascript action\n{jsg:FILE.js} - Javascript files are located in the scripts folder for the plugin";
		} });
    	addPlaceholder(new Placeholder("epochmilli") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
    		return Long.toString(System.currentTimeMillis());
		}
    	@Override 
		public String getDescription() {
			return "{epochmilli} - Returns the milliseconds since the epoch";
		} });
    	addPlaceholder(new Placeholder("epochnano") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
    		return Long.toString(System.nanoTime());
		}
    	@Override 
		public String getDescription() {
			return "{epochnano} - Returns the nanoseconds since the epoch";
		} });
    	addPlaceholder(new Placeholder("motd") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
    		return ""+Bukkit.getMotd();
		}
    	@Override 
		public String getDescription() {
			return "{motd} - Returns the server MOTD";
		} });
    	addPlaceholder(new Placeholder("banlist") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
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
		}
    	@Override 
		public String getDescription() {
			return "{playerlist} - Returns the whole list of players (very long)";
		} });
    	addPlaceholder(new Placeholder("baniplist") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
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
    	addPlaceholder(new Placeholder("worlds") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
    		String mylist = "";
      		for (World clist:getServer().getWorlds()) {
      			mylist+=clist.getName()+",";
      		}
    		return mylist.substring(0,mylist.length()-1);
		}
    	@Override 
		public String getDescription() {
			return "{worlds} - Returns the list of worlds";
		} });
    	addPlaceholder(new Placeholder("slots") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
    		return ""+Bukkit.getMaxPlayers();
		}
    	@Override 
		public String getDescription() {
			return "{slots} - Returns the max slots on the server";
		} });
    	addPlaceholder(new Placeholder("port") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
    		return ""+Bukkit.getPort();
		}
    	@Override 
		public String getDescription() {
			return "{port} - Returns the port the server is running on";
		} });
    	addPlaceholder(new Placeholder("version") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
    		return Bukkit.getVersion().split(" ")[0];
		}
    	@Override 
		public String getDescription() {
			return "{version} - Returns the minecraft version";
		} });
    	addPlaceholder(new Placeholder("allowflight") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
    		return ""+Bukkit.getAllowFlight();
		}
    	@Override 
		public String getDescription() {
			return "{allowflight} - Returns true if flying is allowed";
		} });
    	addPlaceholder(new Placeholder("viewdistance") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
    		return ""+Bukkit.getViewDistance();
		}
    	@Override 
		public String getDescription() {
			return "{viewdistance} - Returns the server view distance";
		} });
    	addPlaceholder(new Placeholder("defaultgamemode") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
    		return ""+Bukkit.getDefaultGameMode();
		}
    	@Override 
		public String getDescription() {
			return "{defaultgamemode} - Returns the server's default gamemode";
		} });
    	addPlaceholder(new Placeholder("operators") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
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
		}
    	@Override 
		public String getDescription() {
			return "{whitelist} - Returns the whitelist";
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
		}
    	@Override 
		public String getDescription() {
			return "{plugins} - Returns the list of plugins";
		} });
    	addPlaceholder(new Placeholder("exhaustion") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
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
		}
    	@Override 
		public String getDescription() {
			return "{display:*username} - Returns the player's nickname";
		} });
    	addPlaceholder(new Placeholder("firstjoin") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
    		return Long.toString(Bukkit.getOfflinePlayer(modifiers[0]).getFirstPlayed()/1000);		
		}
    	@Override 
		public String getDescription() {
			return "{firstjoin:*username} - Returns the timestamp (seconds) for when the player joined";
		} });
    	addPlaceholder(new Placeholder("lastplayed") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
    		if (Bukkit.getPlayer(modifiers[0])!=null) {
    			return "0";
    		}
    		return Long.toString(Bukkit.getOfflinePlayer(modifiers[0]).getLastPlayed()/1000);
		}
    	@Override 
		public String getDescription() {
			return "{lastplayed:*username} - Returns the time since the player last played.";
		} });
    	addPlaceholder(new Placeholder("hunger") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
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
    	addPlaceholder(new Placeholder("air") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
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
    	addPlaceholder(new Placeholder("bed") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
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
    	addPlaceholder(new Placeholder("exp") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
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
    	addPlaceholder(new Placeholder("lvl") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
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
    	addPlaceholder(new Placeholder("operator") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
    		if (modifiers.length==1) {
    			return ""+Bukkit.getOfflinePlayer(modifiers[0]).isOp();
    		}
			return ""+player.isOp();
		}
    	@Override 
		public String getDescription() {
			return "{operator:*username} - Returns true if the player is Op";
		} });
    	addPlaceholder(new Placeholder("itemid") { @SuppressWarnings("deprecation")
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
    	addPlaceholder(new Placeholder("itemamount") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
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
    	addPlaceholder(new Placeholder("itemname") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
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
    	addPlaceholder(new Placeholder("sound") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
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
    	addPlaceholder(new Placeholder("inventory") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
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
    	addPlaceholder(new Placeholder("durability") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
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
    	addPlaceholder(new Placeholder("gamemode") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
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
    	addPlaceholder(new Placeholder("direction") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
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
    	addPlaceholder(new Placeholder("health") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
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
    	addPlaceholder(new Placeholder("biome") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
    		if (modifiers.length==1) {
    			Location loc = getloc(modifiers[0], player);
    			return loc.getWorld().getBiome(loc.getBlockX(), loc.getBlockZ()).toString();
    		}
    		return player.getWorld().getBiome(player.getLocation().getBlockX(), player.getLocation().getBlockZ()).toString();
		}
    	@Override 
		public String getDescription() {
			return "{biome:*location} - Returns the biome at a location";
		} });
    	addPlaceholder(new Placeholder("location") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
    		if (modifiers.length==1) {
    			Location loc = getloc(modifiers[0], player);
    			return loc.getWorld().getName()+","+loc.getBlockX()+","+loc.getBlockY()+","+loc.getBlockZ();
    		}
    		Location loc = player.getLocation();
    		return loc.getWorld().getName()+","+loc.getBlockX()+","+loc.getBlockY()+","+loc.getBlockZ();
		}
    	@Override 
		public String getDescription() {
			return "{location:*username} - Returns a player's location in the format W,X,Y,Z";
		} });
    	addPlaceholder(new Placeholder("storm") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
    		if (modifiers.length==1) {
    			Location loc = getloc(modifiers[0], player);
    			return ""+loc.getWorld().hasStorm();
    		}
    			return ""+player.getLocation().getWorld().hasStorm();
		}
    	@Override 
		public String getDescription() {
			return "{storm:*location} - Returns true if there is a storm at a location";
		} });
    	addPlaceholder(new Placeholder("thunder") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
    		if (modifiers.length==1) {
    			Location loc = getloc(modifiers[0], player);
    			return ""+loc.getWorld().isThundering();
    		}
    			return ""+player.getLocation().getWorld().isThundering();
		}
    	@Override 
		public String getDescription() {
			return "{thunder:*location} - Returns true if there is thunder at a location";
		} });
    	addPlaceholder(new Placeholder("x") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
    		if (modifiers.length==1) {
				return String.valueOf(Math.floor(getloc(modifiers[0], player).getX()));
    		}
			return String.valueOf(Math.floor(player.getLocation().getX()));
		}
    	@Override 
		public String getDescription() {
			return "{x} - Returns a player's x coordinate";
		} });
    	addPlaceholder(new Placeholder("y") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
    		if (modifiers.length==1) {
				return String.valueOf(Math.floor(getloc(modifiers[0], player).getZ()));
    		}
			return String.valueOf(Math.floor(player.getLocation().getY()));
		}
    	@Override 
		public String getDescription() {
			return "{y} - Returns a player's y coordinate";
		} });
    	addPlaceholder(new Placeholder("z") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
    		if (modifiers.length==1) {
				return String.valueOf(Math.floor(getloc(modifiers[0], player).getZ()));
    		}
			return String.valueOf(Math.floor(player.getLocation().getZ()));
		}
    	@Override 
		public String getDescription() {
			return "{z} - Returns a player's z coordinate";
		} });
    	addPlaceholder(new Placeholder("sneaking") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
    		if (modifiers.length==1) {
    			return ""+Bukkit.getPlayer(modifiers[0]).isSneaking();
    		}
			return ""+player.isSneaking();
		}
    	@Override 
		public String getDescription() {
			return "{sneaking} - Returns true if the player is sneaking";
		} });
    	addPlaceholder(new Placeholder("itempickup") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
    		if (modifiers.length==1) {
    			return ""+Bukkit.getPlayer(modifiers[0]).getCanPickupItems();
    		}
			return ""+player.getCanPickupItems();
		}
    	@Override 
		public String getDescription() {
			return "{itempickup:*username} - Returns true if a player can pick up items";
		} });
    	addPlaceholder(new Placeholder("flying") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
    		if (modifiers.length==1) {
    			return ""+Bukkit.getPlayer(modifiers[0]).getAllowFlight();
    		}
			return ""+player.getAllowFlight();
		}
    	@Override 
		public String getDescription() {
			return "{flying:*username} - Returns true if the player is flying";
		} });
    	addPlaceholder(new Placeholder("grounded") { @SuppressWarnings("deprecation")
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
    	addPlaceholder(new Placeholder("blocking") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
    		if (modifiers.length==1) {
    			return ""+Bukkit.getPlayer(modifiers[0]).isBlocking();
    		}
			return ""+player.isBlocking();
		}
    	@Override 
		public String getDescription() {
			return "{blocking:*username} - Returns true if the player is blocking";
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
		}
    	@Override 
		public String getDescription() {
			return "{passenger:*username} - Returns the vehicle the player is in or false";
		} });
    	addPlaceholder(new Placeholder("maxhealth") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
    		if (modifiers.length==1) {
    			return ""+Bukkit.getPlayer(modifiers[0]).getMaxHealth();
    		}
			return ""+player.getMaxHealth();
		}
    	@Override 
		public String getDescription() {
			return "{maxhealth:*username} - Returns a player's max health";
		} });
    	addPlaceholder(new Placeholder("maxair") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
    		if (modifiers.length==1) {
    			return ""+Bukkit.getPlayer(modifiers[0]).getMaximumAir();
    		}
			return ""+player.getMaximumAir();
		}
    	@Override 
		public String getDescription() {
			return "{maxair:*username} - Returns a player's max air";
		} });
    	addPlaceholder(new Placeholder("age") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
    		if (modifiers.length==1) {
    			return Integer.toString((Bukkit.getPlayer(modifiers[0]).getTicksLived()/20));
    		}
    		return Integer.toString(player.getTicksLived()/20);
		}
    	@Override 
		public String getDescription() {
			return "{age:*username} - Returns the time since the player joined in seconds.";
		} });
    	addPlaceholder(new Placeholder("compass") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
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
    	addPlaceholder(new Placeholder("sleeping") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
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
    	addPlaceholder(new Placeholder("dead") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
    		if (modifiers.length==1) {
    			return ""+Bukkit.getPlayer(modifiers[0]).isDead();
    		}
			return ""+player.isDead();
		}
    	@Override 
		public String getDescription() {
			return "{dead:*username} - Returns true/false if player is dead/alive";
		} });
    	addPlaceholder(new Placeholder("whitelisted") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
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
    	addPlaceholder(new Placeholder("world") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
    		if (modifiers.length==1) {
    			return getloc(modifiers[0], player).getWorld().getName();
    		}
			return ""+player.getWorld().getName();
		}
    	@Override 
		public String getDescription() {
			return "{world:*username} - Returns the name of the world";
		} });
    	addPlaceholder(new Placeholder("ip") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
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
    	addPlaceholder(new Placeholder("wrap") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
    		if (modifiers.length==2) {
    			int a = Integer.parseInt(modifiers[0]);
    			int b = Integer.parseInt(modifiers[1]);
    			return ""+a%b;
    		}
			return "0";
		} });
    	addPlaceholder(new Placeholder("isclick") { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
    		return ""+getClicked();
		}
    	@Override 
		public String getDescription() {
			return "{isclick} - right, left or false depending on if the update was caused by a click event";
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
		}
    	@Override 
		public String getDescription() {
			return "{uses} - returns the number of times the sign has been used by a player";
		} });
    	Plugin vaultPlugin = Bukkit.getServer().getPluginManager().getPlugin("Vault");
		if (vaultPlugin!=null) {
			new VaultFeature(this);
		}
		else {
			msg(null,"&c[Warning] InSignsPlus did not detect Vault. Some placeholders may not work.");
		}
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
					            	updateSign(player, loc,null);
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
				if (counter0>=999) {
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
	
	private synchronized boolean sendUpdatePacket(Player player,Location location, Event causeEvent) {
		try {
			Sign sign;
			try {
				sign = (Sign) (location.getBlock().getState());
			}
			catch (Exception e) {
				return false;
			}
		    if ((player == null) || (!player.isOnline())) {
		        return false;
		    }
		    if (sign == null) {
		        return false;
		    }
		    if (location.getWorld().equals(player.getWorld())==false) {
		    	return false;
		    }
	        try {
	        	protocolclass.sendPacket(sign, sign.getLines(), player);
	        return true;
	        }
	        catch (Exception e2) {
	        	boolean modified = false;
	    	    recursion=0;
	        	String[] lines = sign.getLines();
	        	setUser(player);
	            setSender(player);
				SignUpdateEvent myevent = new SignUpdateEvent(player, location, lines, null);
		        getServer().getPluginManager().callEvent(myevent);
		        if (myevent.isCancelled()) {
		        	myevent.setCancelled(true);
		        	return false;
		        }
		        if (myevent.getLines().equals(lines)==false) {
		        	lines = myevent.getLines();
		        	modified = true;
		        }
		        String original = StringUtils.join(lines);
	            boolean contains = false;
		        for (int i = 0;i<list.size();i++) {
		        	Location current = list.get(i);
		        	if (current.equals(location)) {
		        		if (players.get(i).equals(player)) {
		        			contains = true;
		        			break;
		        		}
		        	}
		        }
		        if ((contains)==false) {
					if (lines[0].equals("")==false) {
						String result = evaluate(lines[0], false,location);
						if (result.equals(lines[0])==false) {
							lines[0] = colorise(result);
							modified = true;
						}
					}
					if (lines[1].equals("")==false) {
						String result = evaluate(lines[1], false,location);
						if (result.equals(lines[1])==false) {
							lines[1] = colorise(result);
							modified = true;
						}
					}
					if (lines[2].equals("")==false) {
						String result = evaluate(lines[2], false,location);
						if (result.equals(lines[2])==false) {
							lines[2] = colorise(result);
							modified = true;
						}
					}
					if (lines[3].equals("")==false) {
						String result = evaluate(lines[3], false,location);
						if (result.equals(lines[3])==false) {
							lines[3] = colorise(result);
							modified = true;
						}
					}
					if (modified==true) {
						for (int i = 0; i < 4; i++) {
							if (lines[i].contains("\n")) {
		            			if ((i < 3)) {
		            				if (lines[i+1].isEmpty()) {
		            					lines[i+1] = lines[i].substring(lines[i].indexOf("\n")+1);
		            				}
		            			}
		            			lines[i] = lines[i].substring(0,lines[i].indexOf("\n"));
		            		}
		            		if (lines[i].length()>15) {
			            		if ((i < 3)) {
			            			if (lines[i+1].isEmpty()) {
			            				lines[i+1] = lines[i].substring(15);
			            			}
			            		}
			            		lines[i] = lines[i].substring(0,15);
		            		}
		            	}
						if(iswhitelisted(original)) {
							isadd(player, location);
							player.sendSignChange(location, lines);
						}
						else {
							player.sendSignChange(location, lines);
						}
					}
					else {
					}
	            }
	            else {
	            	lines[0] = colorise(evaluate(lines[0], false,location));
	            	lines[1] = colorise(evaluate(lines[1], false,location));
	            	lines[2] = colorise(evaluate(lines[2], false,location));
	            	lines[3] = colorise(evaluate(lines[3], false,location));
					for (int i = 0; i < 4; i++) {
	            		if (lines[i].contains("\n")) {
	            			if ((i < 3)) {
	            				if (lines[i+1].isEmpty()) {
	            					lines[i+1] = lines[i].substring(lines[i].indexOf("\n")+1);
	            				}
	            			}
	            			lines[i] = lines[i].substring(0,lines[i].indexOf("\n"));
	            		}
	            		if (lines[i].length()>15) {
		            		if ((i < 3)) {
		            			if (lines[i+1].isEmpty()) {
		            				lines[i+1] = lines[i].substring(15);
		            			}
		            		}
		            		lines[i] = lines[i].substring(0,15);
	            		}
	            	}
	            	player.sendSignChange(location, lines);
	            }
	            setUser(null);
	            setSender(null);
	        }
    	}
    	catch (Exception e) {
    		e.printStackTrace();
    	}
		return false;
	}
	
	public synchronized boolean updateSign(Player player,Location location, Event event) {
		return sendUpdatePacket(player, location, event);
	}
	public synchronized boolean updateSign(Player player,Location location) {
		return sendUpdatePacket(player, location, null);
	}
	
	@EventHandler(ignoreCancelled=true)
    private void onPlayerInteract(final PlayerInteractEvent event)
    {
		if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
			setClicked("right");
		}
		if (event.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
			setClicked("left");
		}
		if (event.isCancelled()) {
			return;
		}
		if (event.getAction() == Action.RIGHT_CLICK_BLOCK||event.getAction() == Action.LEFT_CLICK_BLOCK) {
			Block block = event.getClickedBlock();
	        if ((block.getType() == Material.SIGN_POST) || (block.getType() == Material.WALL_SIGN)) {
	        	for (int i = 0;i<list.size();i++) {
	        		if (list.get(i).equals(block.getLocation())&&players.get(i).equals(event.getPlayer().getName())) {
	        			clicks.set(i, clicks.get(i)+1);
	        		}
	        	}
        		final Player player = event.getPlayer();
        		final Location location = event.getClickedBlock().getLocation();
    			BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
    	        scheduler.scheduleSyncDelayedTask(this, new Runnable() {

    				@Override
    				public void run() {
    					updateSign(player, location, event);
    					
    				}
    	        }, 1L);
	        }
		}
    }
}