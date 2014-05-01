package com.empcraft;

import java.io.File;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.logging.Logger;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginLoader;

import com.avaje.ebean.EbeanServer;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;

public class ProtocolClass {
	InSignsPlus ISP;
	ProtocolManager protocolmanager = null;
	public void sendPacket(Sign sign,String[] lines, Player player) throws InvocationTargetException {
		PacketContainer packet = protocolmanager.createPacket(PacketType.Play.Server.UPDATE_SIGN);
		packet.getSpecificModifier(Integer.TYPE).write(0, Integer.valueOf(sign.getX()));
		packet.getSpecificModifier(Integer.TYPE).write(1, Integer.valueOf(sign.getY()));
		packet.getSpecificModifier(Integer.TYPE).write(2, Integer.valueOf(sign.getZ()));
		packet.getStringArrays().write(0, lines);
		protocolmanager.sendServerPacket(player, packet);
	}
	public ProtocolClass(InSignsPlus plugin) {
		 ISP = plugin;
		 protocolmanager = ProtocolLibrary.getProtocolManager();
		 protocolmanager.addPacketListener(new PacketAdapter(ISP, ListenerPriority.LOW, new PacketType[] { PacketType.Play.Server.UPDATE_SIGN })
	        {
	        	public void onPacketSending(PacketEvent event)
	          {
        		boolean modified = false;
	    	    ISP.recursion=0;
	            PacketContainer packet = event.getPacket();
	            packet = packet.shallowClone();
	            int packetx = (packet.getIntegers().read(0)).intValue();
	            short packety = (packet.getIntegers().read(1)).shortValue();
	            int packetz = (packet.getIntegers().read(2)).intValue();
	            String[] lines = (packet.getStringArrays().read(0));
	            Player player = event.getPlayer();
	            Location loc = new Location(player.getWorld(), packetx,packety,packetz);
	            
				String original = StringUtils.join(lines);
				if (lines==null) {
					return;
				}
				ISP.setUser(player);
	            ISP.setSender(player);
				SignUpdateEvent myevent = new SignUpdateEvent(player, loc, lines, null);
		        ISP.getServer().getPluginManager().callEvent(myevent);
		        if (myevent.isCancelled()) {
		        	myevent.setCancelled(true);
		        	return;
		        }
		        if (myevent.getLines().equals(lines)==false) {
		        	lines = myevent.getLines();
		        	modified = true;
		        }
	            boolean contains = false;
		        for (int i = 0;i<ISP.list.size();i++) {
		        	Location current = ISP.list.get(i);
		        	if (current.equals(loc)) {
		        		if (ISP.players.get(i).equals(player)) {
		        			contains = true;
		        			break;
		        		}
		        	}
		        }
	            if ((contains)==false) {
					if (lines[0].equals("")==false) {
						String result = ISP.evaluate(lines[0], false,loc);
						if (result.equals(lines[0])==false) {
							lines[0] = ISP.colorise(result);
							modified = true;
						}
					}
					if (lines[1].equals("")==false) {
						String result = ISP.evaluate(lines[1], false,loc);
						if (result.equals(lines[1])==false) {
							lines[1] = ISP.colorise(result);
							modified = true;
						}
					}
					if (lines[2].equals("")==false) {
						String result = ISP.evaluate(lines[2], false,loc);
						if (result.equals(lines[2])==false) {
							lines[2] = ISP.colorise(result);
							modified = true;
						}
					}
					if (lines[3].equals("")==false) {
						String result = ISP.evaluate(lines[3], false,loc);
						if (result.equals(lines[3])==false) {
							lines[3] = ISP.colorise(result);
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
						if(ISP.iswhitelisted(original)) {
							ISP.isadd(player, loc);
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
	            	//TODO CHECK IF MODIFIED
	            	//IF NOT, DON'T SEND PACKET
	            	lines[0] = ISP.colorise(ISP.evaluate(lines[0], false,loc));
	            	lines[1] = ISP.colorise(ISP.evaluate(lines[1], false,loc));
	            	lines[2] = ISP.colorise(ISP.evaluate(lines[2], false,loc));
	            	lines[3] = ISP.colorise(ISP.evaluate(lines[3], false,loc));
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
	            	packet.getStringArrays().write(0, lines);
	    			event.setPacket(packet);
	            }
	            ISP.setUser(null);
	            ISP.setSender(null);
	          }
	        });
	 }
}
