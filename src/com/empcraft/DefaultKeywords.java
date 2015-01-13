package com.empcraft;

import java.util.Arrays;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class DefaultKeywords {

	public DefaultKeywords(final InSignsPlus ISP) {
		ISP.keywords.put("gvar", new Keyword("gvar") {
			@Override
			public String getDescription() {
				return "gvar <variable> <value>";
			}
			@Override
			public String getValue(Player player, Location location, String[] args,Boolean elevation) {
				// TODO Auto-generated method stub
				if (args.length > 0) {
					if (args.length > 1) {
						ISP.globals.put("{"+ISP.evaluate(args[0],elevation,location)+"}", ISP.evaluate(StringUtils.join(Arrays.copyOfRange(args, 1, args.length)," "),elevation,location));
					}
					else {
						ISP.globals.remove("{"+args[0]+"}");
					}
				}
				return null;
			}
		});
		ISP.keywords.put("setuser", new Keyword("setuser") {
			@Override
			public String getDescription() {
				return "setuser <username> - Evaluate as another user\nsetuser null - Evaluate as console";
			}
			@Override
			public String getValue(Player player, Location location, String[] args,Boolean elevation) {
				Player user = Bukkit.getPlayer(args[0]);
				if (user!=null) {
					ISP.setUser(user);
				}
				return null;
			}
		});
		ISP.keywords.put("\\", new Keyword("\\") {
			@Override
			public String getDescription() {
				return "\\ <message> - Force a user to send a message";
			}
			@Override
			public String getValue(Player player, Location location, String[] args,Boolean elevation) {
                if (player != null) {
                    player.chat(StringUtils.join(args," "));
                }
                else {
                    ISP.getServer().dispatchCommand(ISP.getServer().getConsoleSender(), "say "+StringUtils.join(args," "));
                }
                return null;
			}
		});
		ISP.keywords.put("do", new Keyword("do") {
			@Override
			public String getDescription() {
				return "do <command> - Force a user or console to execute a command";
			}
			@Override
			public String getValue(Player player, Location location, String[] args,Boolean elevation) {
				String command = StringUtils.join(args," ");
				if (player==null) {
					ISP.getServer().dispatchCommand(ISP.getServer().getConsoleSender(), command);
				}
				else {
					if (elevation) {
						boolean op = player.isOp();
						try {
							player.setOp(true);
							ISP.getServer().dispatchCommand(player, command);
						}
						catch (Exception e) { }
						player.setOp(op);
					}
					else {
						ISP.getServer().dispatchCommand(player, command);
					}
				}
				return null;
			}
		});
		ISP.keywords.put("cmd", new Keyword("cmd") {
			@Override
			public String getDescription() {
				return "cmd <command> - Force a user to execute a command";
			}
			@Override
			public String getValue(Player player, Location location, String[] args,Boolean elevation) {
				String command = StringUtils.join(args," ");
				if (player==null) {
					ISP.getServer().dispatchCommand(ISP.getServer().getConsoleSender(), command);
				}
				else {
					if (elevation) {
						boolean op = player.isOp();
						try {
							player.setOp(true);
							ISP.getServer().dispatchCommand(player, command);
						}
						catch (Exception e) { }
						player.setOp(op);
					}
					else {
						ISP.getServer().dispatchCommand(player, command);
					}
				}
				return null;
			}
		});
		ISP.keywords.put("cmdop", new Keyword("cmdop") {
			@Override
			public String getDescription() {
				return "cmdop <command> - Force a user to execute a command as op";
			}
			@Override
			public String getValue(Player player, Location location, String[] args,Boolean elevation) {
				String command = StringUtils.join(args," ");
				if (player==null) {
					ISP.getServer().dispatchCommand(ISP.getServer().getConsoleSender(), command);
				}
				else {
					boolean op = player.isOp();
					try {
						player.setOp(true);
						ISP.getServer().dispatchCommand(player, command);
					}
					catch (Exception e) { }
					player.setOp(op);
				}
				return null;
			}
		});
		ISP.keywords.put("cmdcon", new Keyword("cmdcon") {
			@Override
			public String getDescription() {
				return "cmdcon <command> - Force console to execute a command";
			}
			@Override
			public String getValue(Player player, Location location, String[] args,Boolean elevation) {
				String command = StringUtils.join(args," ");
				ISP.getServer().dispatchCommand(ISP.getServer().getConsoleSender(), command);
                return null;
			}
		});
		ISP.keywords.put("return", new Keyword("return") {
			@Override
			public String getDescription() {
				return "return <result> - return a value";
			}
			@Override
			public String getValue(Player player, Location location, String[] args,Boolean elevation) {
                return StringUtils.join(args," ");
			}
		});
		ISP.keywords.put("msg", new Keyword("msg") {
			@Override
			public String getDescription() {
				return "msg <message> - Send a player a message";
			}
			@Override
			public String getValue(Player player, Location location, String[] args,Boolean elevation) {
				ISP.msg(Bukkit.getPlayer(args[0]),StringUtils.join(Arrays.asList(args).subList(1, args.length)," "));
				return null;
			}
		});
		ISP.keywords.put("schedule", new Keyword("schedule") {
			@Override
			public String getDescription() {
				return "schedule <script> <diff> - NOT IMPLEMENTED YET";
			}
			@Override
			public String getValue(final Player player, final Location location, String[] args, final Boolean elevation) {
				Long time = Long.parseLong(args[0]);
				args[0] = "";
				final String line = StringUtils.join(args).trim();
				Runnable runnable = new Runnable() {
					@Override
					public void run() {
						ISP.setUser(player);
						ISP.setSender(player);
						ISP.execute(line, elevation, location);
						ISP.setUser(null);
						ISP.setSender(null);
					}
				};
				Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(ISP, runnable, time);
                return null;
			}
		});
	}
}
