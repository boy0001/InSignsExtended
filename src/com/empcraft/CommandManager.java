package com.empcraft;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

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
    			if (args[0].equalsIgnoreCase("updating")) {
    				ISP.msg(player,"Signs: "+ISP.queue.size());
    				return true;
    			}
    			if (args[0].equalsIgnoreCase("if")) {
    				ISP.recursion = 0;
    				if (ISP.checkperm(player,"insignsplus.if")) {
    					ISP.setUser(player);
    	            	ISP.setSender(player);
    					String line = StringUtils.join(Arrays.copyOfRange(args, 1, args.length)," ");
	    	            line = ISP.evaluate(line,false,null);
	    	            try {
	    	            	ISP.msg(player,"if "+line+" -> "+ISP.testif(line, false, null));
						} catch (Exception e) {
							ISP.msg(player,"if "+line+" -> invalid syntax");
						}
	    	            ISP.setUser(null);
    	            	ISP.setSender(null);
	    	    		return true;
    				}
    				else {
    					ISP.msg(player,"&7You lack the permission &cinsignsplus.if");
    				}
    				return true;
    	    	}
    			else if (args[0].equalsIgnoreCase("eval")) {
    				if (ISP.checkperm(player,"insignsplus.evaluate")) {
    					ISP.setUser(player);
	            		ISP.setSender(player);
	    				String line = StringUtils.join(Arrays.copyOfRange(args, 1, args.length)," ");
	    	            ISP.msg(player, ISP.evaluate(line, false, null));
	    	            ISP.setUser(null);
	            		ISP.setSender(null);
    				}
    				else {
    					ISP.msg(player,"&7You lack the permission &cinsignsplus.evaluate");
    				}
    				return true;
    	    	}
    			else if (args[0].equalsIgnoreCase("exec")) {
    				if (ISP.checkperm(player,"insignsplus.execute")) {
	    				String line = StringUtils.join(Arrays.copyOfRange(args, 1, args.length)," ");
	    				ISP.setUser(player);
	            		ISP.setSender(player);
	    	            ISP.execute(line, false, null);
	    	            ISP.setUser(null);
	            		ISP.setSender(null);
    				}
    				else {
    					ISP.msg(player,"&7You lack the permission &cinsignsplus.execute");
    				}
    				return true;
    	    	}
    			else if (args[0].equalsIgnoreCase("player")) {
    				if (ISP.checkperm(player,"insignsplus.player")) {
	    				String line = StringUtils.join(Arrays.copyOfRange(args, 2, args.length)," ");
	    				if (args.length>2) {
	    					if ((Bukkit.getPlayer(args[2])!=null||args[2].equalsIgnoreCase("console")||args[2].equals("null"))) {
	    						Player user = Bukkit.getPlayer(args[2]);
			    				ISP.setUser(user);
			            		ISP.setSender(user);
			    	            ISP.execute(line, false, null);
			    	            ISP.setUser(null);
			            		ISP.setSender(null);
	    					}
	    					else {
	    						ISP.msg(player, "&7Player not found for &c"+args[2]+"&7.");
	    					}
	    				}
	    				else {
	    					ISP.msg(player, "&7Use &a/isp player <username> <script>");
	    				}
    				}
    				else {
    					ISP.msg(player,"&7You lack the permission &cinsignsplus.player");
    				}
    				return true;
    	    	}
    			else if (args[0].equalsIgnoreCase("all")) {
    				if (ISP.checkperm(player,"insignsplus.all")) {
	    				String line = StringUtils.join(Arrays.copyOfRange(args, 1, args.length)," ");
	    				for (Player user:Bukkit.getOnlinePlayers()) {
		    				ISP.setUser(user);
		            		ISP.setSender(user);
		    	            ISP.execute(line, false, null);
		    	            ISP.setUser(null);
		            		ISP.setSender(null);
	    				}
	    				ISP.execute(line, false, null);
    				}
    				else {
    					ISP.msg(player,"&7You lack the permission &cinsignsplus.all");
    				}
    				return true;
    	    	}
    			else if (args[0].equalsIgnoreCase("gvar")) {
    				if (ISP.individualmessages!=null) {
						new InMeHook(true, ISP.individualmessages);
					}
	    			if (args.length>1) {
	    				if (ISP.checkperm(player,"insignsplus.gvar")) {
	    					if (args.length>2) {
	    						String line = StringUtils.join(Arrays.copyOfRange(args, 2, args.length)," ");
	    		            	try {
	    		            		ISP.setUser(player);
	    		            		ISP.setSender(player);
	    		            		ISP.addgvar("{"+ISP.evaluate(args[1],false,null)+"}", ISP.evaluate(StringUtils.join(Arrays.copyOfRange(args, 2, args.length)," "),false,null));
	    		            		ISP.setUser(null);
	    		            		ISP.setSender(null);
	    		            		if (ISP.getConfig().getInt("scripting.debug-level") > 1) {
	    		            			ISP.msg(player,"global var "+args[1]+" -> "+args[2]);
	    		              			if (ISP.getConfig().getInt("scripting.debug-level") > 2) { ISP.msg(player,""+ISP.globals); }
	    		            		}
	    		            	}
	    		            	catch (Exception e) {
	    			              	if (ISP.getConfig().getInt("scripting.debug-level") > 0) {
	    			              		ISP.msg(player,line+" -> invalid syntax");
	    			            	}
	    		            	}
	    		            }
	    		            else {
	    		            	try {
	    		            		ISP.delgvar("{"+args[1]+"}");
	    		            		if (ISP.getConfig().getInt("scripting.debug-level") > 1) {
	    		            			ISP.msg(player,"removed global "+args[0]);
	    			            	}
	    		            	}
	    		            	catch (Exception e2) {
	    		            		if (ISP.getConfig().getInt("scripting.debug-level") > 0) {
	    			              		ISP.msg(player,"failed to remove global "+args[0]);
	    			            	}
	    		            	}
	    		            }
    	    			}
	    				else {
	    					ISP.msg(player,"&7You lack the permission &cinsignsplus.gvar");
	    				}
	    			}
	    			else {
	    				ISP.msg(player,"/gvar <variable> <value>");
	    				ISP.msg(player,"globals: "+ISP.globals);
	    			}
					if (ISP.individualmessages!=null) {
						new InMeHook(false, ISP.individualmessages);
					}
    	    		return true;
    	    	}
    			else if ((args[0].equalsIgnoreCase("setline"))){
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
    		    				ISP.msg(player,"&7Invalid arguments:");
        		    			ISP.msg(player,"&c/isp setline <index> <value>");
    		    			}
    		    		}
    		    		catch (Exception e) {
    		    			ISP.msg(player,"&cYou must be looking at a sign.");
    		    		}
    				}
    				else {
    					ISP.msg(player,"&6Missing requirements&7: insignsplus.setline");
    				}
    				return false;
    			}
    			if ((args[0].equalsIgnoreCase("putline"))){
    				if (player==null) {
    					return false;
    				}
    				if (ISP.checkperm(player,"isp.putline")) {
    					if (args.length<2) {
    						ISP.msg(player,"&7puts text on a line:");
    						ISP.msg(player,"&c/isp putline <index> <value>");
    						return true;
    					}
    					try {
    		    			Block block = (player.getTargetBlock(null, 200).getLocation()).getBlock();
    		    			Sign sign = (Sign) block.getState();
    		    			try {
    		    				boolean hasperm = ISP.checkperm(player,"isp.putline.override");
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
    				    			sign.setLine(Integer.parseInt(args[1])-1, line);
    				    			ISP.setSender(null);
    			    				ISP.setUser(null);
    				    			sign.update(true);
    				    			ISP.msg(player,"&7Updated sign successfully.");
    				    			return true;
    		    				}
    		    				else {
    		    					ISP.msg(player,"&6Missing requirements&7: insignsplus.putline.override");
    		    				}
    		    			}
    		    			catch (Exception e) {
    		    				ISP.msg(player,"&7Invalid arguments:");
        		    			ISP.msg(player,"&c/isp putline <index> <value>");
    		    			}
    		    		}
    		    		catch (Exception e) {
    		    			ISP.msg(player,"&cYou must be looking at a sign.");
    		    		}
    				}
    				else {
    					ISP.msg(player,"&6Missing requirements&7: insignsplus.putline");
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
    						ISP.msg(player,"&7Unknown placeholder &c"+args[1]+"&7 - Try &a/isp list");
    					}
    					else {
    						String description = "&aDescription:\n&a> &c"+placeholder.getDescription().replace("\n", "\n&a> &c"); 
    						ISP.msg(player,description);
    						if (description.contains(":STRING")) {
    							ISP.msg(player, "&f &cSTRING &7- A word, or string of characters e.g. &ahello&7");
    						}
    						if (description.contains(":BOOLEAN")) {
    							ISP.msg(player, "&f &cBOOLEAN &7- true or false");
    						}
    						if (description.contains(":FLOAT")) {
    							ISP.msg(player, "&f &cFLOAT &7- A 32bit number supporting decimals e.g. &a12.34&7");
    						}
    						if (description.contains(":INT")) {
    							ISP.msg(player, "&f &cINT &7- A whole number e.g. 6");
    						}
    						if (description.contains(":LIST")) {
    							ISP.msg(player, "&f &cLIST &7- A series of elements separated by a comma e.g. &a1,2,3&7");
    						}
    						if (description.contains(":NODE")) {
    							ISP.msg(player, "&f &cNODE &7- A permission node e.g bukkit.command.help");
    						}
    						if (description.contains(":LOCATION") || description.contains(":LOCATION")) {
    							ISP.msg(player, "&f &cLOCATION &7- world,x,y,z,*yaw,*pitch");
    						}
    						if (description.contains(":*") || description.contains(",*")) {
    							ISP.msg(player, "&f &c* &7- indicates optional arguments");
    						}
    						if (description.contains(",#") || description.contains(":#")) {
    							ISP.msg(player, "&f &c# &7- indicates a numerical index");
    						}
    					}
    					if (ISP.individualmessages!=null) {
    						new InMeHook(false, ISP.individualmessages);
    					}
    					return true;
    				}
    			}
    			else if ((args[0].equalsIgnoreCase("keywords"))){
    				ISP.msg(player, "&6Keywords for &cISP&7:");
    				for (Entry<String, Keyword> key:ISP.keywords.entrySet()) {
    					ISP.msg(player,"&7 - &9"+key.getKey()+" &7- &a"+key.getValue().getDescription());
    				}
    			}
    			else if ((args[0].equalsIgnoreCase("list"))){
    				String text = "";
    				int page = 1;
    				List<Placeholder> myph = ISP.getAllPlaceholders();
					if (args.length > 2) {
						try {
							page = Integer.parseInt(args[2]);
    						if (page<1) {
    							page = 1;
    						}
    						myph = ISP.getPlaceholders(args[1]);text = args[1];
						}
						catch (Exception e2) {
							ISP.msg(player,"&cInvalid page. &7Use /isp list *<searh> *<page>");
							return false;
						}
					}
					else if (args.length > 1) {
    					try {
    						page = Integer.parseInt(args[1]);
    						if (page<1) {
    							page = 1;
    						}
    						if (args.length > 2) {
    							myph = ISP.getPlaceholders(args[2]);
    							text = args[2];
    						}
    					}
    					catch (Exception e) {
							myph = ISP.getPlaceholders(args[1]);text = args[1];
						}
    				}
    				
    				
					if (ISP.individualmessages!=null) {
						new InMeHook(true, ISP.individualmessages);
					}
    				ISP.msg(player,"&6Placeholders for '&c"+text+"&6'&7:");
    				for (int i = Math.min((page-1)*16, myph.size());i<Math.min((page)*16, myph.size());i++) {
    					Placeholder current = myph.get(i);
    					if (ISP.placeholders.get(current.getKey())!=null) {
    						if (text.equals("")) {
    							ISP.msg(player,"&7 - &a{"+current+"}");
    						}
    						else {
    							ISP.msg(player,"&7 - &a{"+StringUtils.replace(current.getKey(), text, "&2&l"+text+"&r&a")+"}");
    						}
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
    					ISP.saveDefaultConfig();
    					InSignsPlus.plugin.reloadConfig();
    					final Map<String, Object> options = new HashMap<String, Object>();
    					ISP.getConfig().set("version", ISP.version);
    			        options.put("language","english");
    			        options.put("scripting.directory","scripts");
    			        options.put("scripting.debug-level",0);
    			        options.put("signs.autoupdate.enabled",true);
    			        options.put("signs.autoupdate.async","Set to 'true' to have a lower impact on the server - may cause instability");
    			        options.put("signs.autoupdate.buffer",1000);
    			        options.put("signs.autoupdate.updates-per-tick",25);
    			        List<String> whitelist = Arrays.asList("grounded","location","age","localtime","localtime12","display","uses","frames","pages","money","prefix","suffix","group","x","y","z","lvl","exhaustion","health","exp","hunger","air","maxhealth","maxair","gamemode","direction","biome","itemname","itemid","itemamount","durability","dead","sleeping","whitelisted","operator","sneaking","itempickup","flying","blocking","age","bed","compass","spawn","worldticks","time","date","time12","epoch","epochmilli","epochnano","online","worlds","banlist","baniplist","operators","whitelist","randchoice","rand","elevated","matchgroup","matchplayer","hasperm","js","gvar","config","passenger","lastplayed","gprefix","gsuffix");
    			        options.put("signs.autoupdate.whitelist",whitelist);
    			        for (final Entry<String, Object> node : options.entrySet()) {
    			        	 if (!ISP.getConfig().contains(node.getKey())) {
    			        		 ISP.getConfig().set(node.getKey(), node.getValue());
    			        	 }
    			        }
    			        ISP.saveConfig();
    			        File f1 = new File(InSignsPlus.plugin.getDataFolder() + File.separator + ISP.getConfig().getString("scripting.directory"));
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
    			        				ISP.msg(null,"&cError with file "+InSignsPlus.plugin.getDataFolder()+"/"+ISP.getConfig().getString("scripting.directory")+"/"+myph[i].getName()+".");
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
    			ISP.msg(player,"&7===&cCommands&7===\n&7 - &a/isp help <placeholder> \n&7 - &a/isp reload\n&7 - &a/isp save\n&7 - &a/isp list *<search> *<page>\n&7 - &a/isp enable\n&7 - &a/isp disable\n&7 - &a/isp setline <index> <value>\n&7 - &a/isp putline <index> <value>\n&7 - &a/isp gvar <var> <*value>\n&7 - &a/isp if <script>\n&7 - &a/isp eval <script>\n&7 - &a/isp exec <script>\n&7 - &a/isp player <player> <script>\n&7 - &a/isp all <script>\n&c * &7indicates optional arguments");
    		}
    	}
    	return false;
	}
}
