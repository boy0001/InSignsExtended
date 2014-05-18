package com.empcraft;

import java.io.File;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

public class CommandManager {
	CommandManager(){
	}
	boolean performCommand(CommandSender sender, Command cmd, String label, String[] args,final InSignsPlus ISP) {
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
    				if (ISP.checkperm(player,"isp.setline")) {
    					if (args.length<2) {
    						ISP.msg(player,"&7Evaluate and set a line of a sign:");
    						ISP.msg(player,"&c/isp setline <index> <value>");
    						return true;
    					}
    					try {
    		    			Block block = (player.getTargetBlock(null, 200).getLocation()).getBlock();
    		    			Sign sign = (Sign) block.getState();
    		    			try {
    		    				boolean hasperm = ISP.checkperm(player,"isp.setline.override");
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
    			    				ISP.setSender(player);
    			    				ISP.setUser(player);
    			    				if (line.contains("{line}")) {
    			    					line = line.replace("{line}", sign.getLine(Integer.parseInt(args[1])-1));
    			    				}
    				    			sign.setLine(Integer.parseInt(args[1])-1, ISP.colorise(ISP.evaluate(line, false, block.getLocation())).trim());
    				    			ISP.setSender(null);
    			    				ISP.setUser(null);
    				    			sign.update(true);
    				    			ISP.msg(player,"&7Updated sign successfully.");
    				    			return true;
    		    				}
    		    				else {
    		    					ISP.msg(player,"&6Missing requirements&7: insignsplus.setline.override");
    		    				}
    		    			}
    		    			catch (Exception e) {
    		    				ISP.msg(player,"&cYou must be looking at a sign.");
    		    			}
    		    		}
    		    		catch (Exception e) {
    		    			ISP.msg(player,"&7Invalid arguments:");
    		    			ISP.msg(player,"&c/isp setline <index> <value>");
    		    		}
    				}
    				else {
    					ISP.msg(player,"&6Missing requirements&7: insignsplus.setline");
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
    					if (ISP.individualmessages!=null) {
    						new InMeHook(true, ISP.individualmessages);
    					}
    					Placeholder placeholder = ISP.getPlaceholder(args[1]);
    					if (placeholder==null) {
    						ISP.msg(player,"&7Unknown placeholder &c"+args[1]+"&7 - Try &a/im list");
    					}
    					else {
    						String description = "&aDescription:\n&a> &c"+placeholder.getDescription().replace("\n", "\n&a> &c"); 
    						ISP.msg(player,description);
    						if (description.contains(":*")) {
    							ISP.msg(player, "&f &c* &7indicates optional arguments");
    						}
    					}
    					if (ISP.individualmessages!=null) {
    						new InMeHook(false, ISP.individualmessages);
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
    					catch (Exception e) { }
    					if (ISP.individualmessages!=null) {
    						new InMeHook(true, ISP.individualmessages);
    					}
        				ISP.msg(player,"&6ISP.placeholders for '&cISP&6'&7:");
        				List<Placeholder> myph = ISP.getAllPlaceholders();
        				for (int i = Math.min((page-1)*16, myph.size());i<Math.min((page)*16, myph.size());i++) {
        					Placeholder current = myph.get(i);
        					if (ISP.placeholders.get(current.getKey())!=null) {
        						ISP.msg(player,"&7 - &a{"+current+"}");
        					}
        					else {
        						ISP.msg(player,"&7 - &c{"+current+"}");
        					}
        				}
        				ISP.msg(player,"&6Page &c"+(page)+"&6 of &c"+((int) Math.ceil(myph.size()/16)+1)+"&6.");
    					if (ISP.individualmessages!=null) {
    						new InMeHook(false, ISP.individualmessages);
    					}
        				return false;
    				}
    				else {
    					if (ISP.individualmessages!=null) {
    						new InMeHook(true, ISP.individualmessages);
    					}
        				ISP.msg(player,"&6ISP.placeholders for '&cISP&6'&7:");
        				List<Placeholder> myph = ISP.getAllPlaceholders();
        				for (int i = 0;i<16;i++) {
        					Placeholder current = myph.get(i);
        					if (ISP.placeholders.get(current.getKey())!=null) {
        						ISP.msg(player,"&7 - &a{"+current+"}");
        					}
        					else {
        						ISP.msg(player,"&7 - &c{"+current+"}");
        					}
        				}
        				ISP.msg(player,"&6Page &c1&6 of &c"+((int) Math.ceil(myph.size()/16)+1)+"&6.");
        				if (ISP.individualmessages!=null) {
    						new InMeHook(false, ISP.individualmessages);
    					}
        				return true;
    				}
    			}
    			if ((args[0].equalsIgnoreCase("enable"))){
    				if (ISP.checkperm(player,"insignsplus.enable")) {
    					if (args.length>1) {
    						boolean placeholder = ISP.addPlaceholder(args[1]);
    						if (placeholder==false) {
    							ISP.msg(player,"Invalid placeholder: /isp list");
    							return false;
    						}
    						ISP.msg(player,"&7Enabled placeholder &c"+args[1]);
    						return true;
    					}
    					else {
    						ISP.msg(player,"/isp enable <key>");
    					}
    				}
    				else {
    					ISP.msg(player,"&7You lack the permission &cinsignsplus.enable");
    				}
    				return false;
    			}
    			if ((args[0].equalsIgnoreCase("disable"))){
    				if (ISP.checkperm(player,"insignsplus.disable")) {
    					if (args.length>1) {
    						Placeholder placeholder = ISP.removePlaceholder(args[1]);
    						if (placeholder==null) {
    							ISP.msg(player,"Invalid placeholder: /isp list");
    							return false;
    						}
    						ISP.msg(player,"&7Disabled placeholder &c"+args[1]);
    						return true;
    					}
    					else {
    						ISP.msg(player,"/isp disable <key>");
    					}
    				}
    				else {
    					ISP.msg(player,"&7You lack the permission &cinsignsplus.disable");
    				}
    				return false;
    			}
    			if ((args[0].equalsIgnoreCase("reload"))){
    				failed = false;
    				if (ISP.checkperm(player,"insignsplus.reload")) {
    					InSignsPlus.plugin.reloadConfig();
    					InSignsPlus.plugin.getConfig().getConfigurationSection("scripting").set("ISP.placeholders", null);
    			        File f1 = new File(InSignsPlus.plugin.getDataFolder() + File.separator + "scripts");
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
    			        			ISP.addPlaceholder(new Placeholder(name) { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
    			    					if (asconsole) {
    			    						ISP.setUser(null);
    			    					}
    			    					String toreturn = ISP.execute(lines, myelevation, location);
    			    					if (asconsole) {
    			    						ISP.setUser(player);
    			    					}
    			    					return toreturn;
    			        			}
    			        			@Override 
    			        			public String getDescription() {
    			    					return description;
    			        			}
    			        			
    			        			});
    			        			}
    			        			catch (Exception e2) {
    			        				ISP.msg(null,"&cError with file "+InSignsPlus.plugin.getDataFolder()+"/scripts/"+myph[i].getName()+".");
    			        			}
    			        		}
    			        	}
    			        }
    	    			try {
    		    			Set<String> vars = InSignsPlus.plugin.getConfig().getConfigurationSection("scripting.variables").getKeys(false);
    		    			for(String current : vars) {
    		    				ISP.globals.put("{"+current+"}", InSignsPlus.plugin.getConfig().getString("scripting.variables."+current));
    		    			}
    	    			}
    	    			catch (Exception e) {
    	    				
    	    			}
    	    			ISP.counter = 0;
    	    			InSignsPlus.plugin.saveDefaultConfig();
    	    			ISP.msg(player,"&aRELOADED!");
    				}
    				else {
    					ISP.msg(player,"&7You lack the permission &cinsignsplus.reload&7!");
    				}
    				
    			}
    			else if ((args[0].equalsIgnoreCase("save"))){
    				failed = false;
    				if (ISP.checkperm(player,"insignsplus.save")) {
    					ISP.getConfig().getConfigurationSection("scripting").set("variables", null);
    					ISP.counter2 = 0;
    					ISP.msg(null,"[InSignsPlus] Saving variables...");
        		        for (final Entry<String, Object> node : ISP.globals.entrySet()) {
        		        	ISP.getConfig().options().copyDefaults(true);
        		        	ISP.getConfig().set("scripting.variables."+(""+node.getKey()).substring(1,(""+node.getKey()).length()-1), (""+node.getValue()));
        		        	
        		        	ISP.saveConfig();
        		        	ISP.reloadConfig();
        		        }
            			ISP.saveConfig();
            			ISP.reloadConfig();
    	    			ISP.msg(player,"&aSAVED!");
    				}
    				else {
    					ISP.msg(player,"&7You lack the permission &cinsignsplus.save&7!");
    				}
    				
    			}
    		}
    		if (failed) {
    			ISP.msg(player,"&7Commands:\n&7 - &a/isp help <placeholder> \n&7 - &a/isp reload\n&7 - &a/isp save\n&7 - &a/isp list\n&7 - &a/isp enable\n&7 - &a/isp disable");
    		}
    	}
    	return false;
	}
}
