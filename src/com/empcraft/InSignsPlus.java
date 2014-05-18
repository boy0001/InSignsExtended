package com.empcraft;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;
public final class InSignsPlus extends JavaPlugin implements Listener {
	private static ScriptEngine engine = (new ScriptEngineManager()).getEngineByName("JavaScript");
	volatile Map<String, Placeholder> placeholders = new ConcurrentHashMap<String, Placeholder>();
	volatile Map<String, Placeholder> defaultplaceholders = new ConcurrentHashMap<String, Placeholder>();
	volatile Map<String, Object> globals = new ConcurrentHashMap<String, Object>();
	volatile List<Location> list = new ArrayList<Location>();
	volatile List<String> players = new ArrayList<String>();
	volatile List<Integer> clicks = new ArrayList<Integer>();
	private volatile Player currentplayer = null;
	private volatile Player currentsender = null;
	private volatile String clickType;
	static InSignsPlus plugin;
	Plugin individualmessages = null;
	private ProtocolClass protocolclass;
    private static int counter0 = 0;
    int counter = 0;
    int counter2 = 0;
	private long timerstart = 0;
	private boolean islagging = false;
	private int timerlast = 0;
	int recursion = 0;
	public synchronized void setClicked(String click) {
		clickType = click;
	}
	public synchronized String getClicked() {
		return clickType;
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
	public Location getloc(String string,Player user) {
		if (string.contains(",")==false) {
			Player player = Bukkit.getPlayer(string);
			if (player!=null) {
				return player.getLocation();
			}
			else {
				try { return new IOP_1_7_9(string).getLocation(); }
				catch (Exception e1) {
					try { return new ImprovedOfflinePlayer(string).getLocation(); }
					catch (Exception e) {
						try { return new IOP_1_7_2(string).getLocation(); }
						catch (Exception e2) {
						}
					}
				}
				World world = Bukkit.getWorld(string);
				if (world!=null) { return world.getSpawnLocation(); }
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
			else { return null; }
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
    		try {
	    		if ((Bukkit.getPlayer(UUID.fromString(mysplit[1]))!=null)) {
					user = Bukkit.getPlayer(UUID.fromString(mysplit[1]));
					line = StringUtils.join(mysplit,":").replace(":"+mysplit[1],"");
	        	}
    		}
    		catch (Exception e4) {}
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
    	return "null";
    }
    public String evaluate(String line, Boolean elevation,Location interact) {
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
                    	catch (Exception i) { }
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
    	return ChatColor.translateAlternateColorCodes('&', mystring);
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
    	CommandManager commandmanager = new CommandManager();
    	return commandmanager.performCommand(sender, cmd, label, args, plugin);
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
	void add(Player player, Location loc) {
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
	    	catch (Exception e) { }
	    	reloadConfig();
	    	saveConfig();
			msg(null,"f&oSAVING VARIABLES!");
			try {
	        for (final Entry<String, Object> node : globals.entrySet()) {
	        	getConfig().options().copyDefaults(true);
	        	getConfig().set("scripting.variables."+(""+node.getKey()).substring(1,(""+node.getKey()).length()-1), (""+node.getValue()));
	        	saveConfig();
	        }
			}
			catch (Exception e) {}
	        msg(null,"&f&oThanks for using &aInSignsPlus&f by Empire92!");
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
		String version = "0.7.10";
		Plugin protocolLibPlugin = Bukkit.getServer().getPluginManager().getPlugin("ProtocolLib");
		if (protocolLibPlugin==null) {
			msg(null,"&c[SEVERE] &fInSignsPlus requires ProtocolLib to run properly. Please install it.!");
			msg(null,"&c[SEVERE] &fPlease do not ignore this message!");
		}
		else {
			protocolclass = new ProtocolClass(this);
		}
		plugin = this;
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
        boolean isupdate = false;
        try {
        	if (getConfig().getString("version").equals(version)==false) {
        		msg(null,"&7Thanks for updating &aInSignsPlus&7!");
        		isupdate  = true;
        	}
        } catch (Exception e) {isupdate  = true;}
        if (isupdate) {
	        File f8 = new File(getDataFolder() + File.separator+"scripts"+File.separator+"example.yml");
	        if(f8.exists()!=true) {  saveResource("scripts"+File.separator+"example.yml", false); }
	        File f9 = new File(getDataFolder() + File.separator+"scripts"+File.separator+"test.js");
	        if(f9.exists()!=true) {  saveResource("scripts"+File.separator+"test.js", false); }
        }
        new DefaultPlaceholders(this);
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
    					if (asconsole) { setUser(null);	}
    					String toreturn = execute(lines, myelevation, location);
    					if (asconsole) { setUser(player); }
    					return toreturn;
	        			}
	        			@Override 
	        			public String getDescription() { return description; }
    				});
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
        catch (Exception e) { }
    	saveConfig();
    	saveDefaultConfig();
    	Bukkit.getServer().getPluginManager().registerEvents(this, this);
    	if (getConfig().getInt("signs.autoupdate.interval")>0) {
    		timer.schedule (mytask,0l, 1);
    	}
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
			boolean toRemove = false;
			counter0++;
			int toupdate = getConfig().getInt("signs.autoupdate.updates-per-milli");
			for (int i = 0;i<toupdate;i++) {
				if (islagging==false) {
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
								            if (dist<96) { updateSign(player, loc,null);}
								            else if (dist > (Bukkit.getViewDistance()*24)*(Bukkit.getViewDistance()*24)) {toRemove = true;}
											}else {toRemove = true;}
										}else {toRemove = true;}
									}else {toRemove = true;}
								}else {toRemove = true;}
							} catch (Exception e) {toRemove = true;}
						if (toRemove) {
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
				catch (Exception e) {}
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
		            					lines[i+1] = ChatColor.getLastColors(lines[i].substring(0,15))+lines[i].substring(lines[i].indexOf("\n")+1);
		            				}
		            			}
		            			lines[i] = lines[i].substring(0,lines[i].indexOf("\n"));
		            		}
		            		if (lines[i].length()>15) {
			            		if ((i < 3)) {
			            			if (lines[i+1].isEmpty()) {
			            				lines[i+1] = ChatColor.getLastColors(lines[i].substring(0,15))+lines[i].substring(15);
			            			}
			            		}
			            		lines[i] = lines[i].substring(0,15);
		            		}
		            	}
						if(iswhitelisted(original)) {
							add(player, location);
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
	            					lines[i+1] = ChatColor.getLastColors(lines[i].substring(0,15))+lines[i].substring(lines[i].indexOf("\n")+1);
	            				}
	            			}
	            			lines[i] = lines[i].substring(0,lines[i].indexOf("\n"));
	            		}
	            		if (lines[i].length()>15) {
		            		if ((i < 3)) {
		            			if (lines[i+1].isEmpty()) {
		            				lines[i+1] = ChatColor.getLastColors(lines[i].substring(0,15))+lines[i].substring(15);
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