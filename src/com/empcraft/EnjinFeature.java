package com.empcraft;

import java.util.logging.Logger;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.enjin.officialplugin.api.EnjinAPI;
import com.enjin.officialplugin.points.ErrorConnectingToEnjinException;
import com.enjin.officialplugin.points.PlayerDoesNotExistException;

public class EnjinFeature {
	InSignsPlus ISP;
	private static final Logger log = Logger.getLogger("Minecraft");
	public EnjinFeature(InSignsPlus plugin,Plugin enjin) {
		ISP = plugin;
        ISP.addPlaceholder(new Placeholder("enjintags",enjin) { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
        	try {
				if (modifiers.length>0) {
					return StringUtils.join(EnjinAPI.getPlayerTags(modifiers[0]).keySet(),","); 
				}
        		return StringUtils.join(EnjinAPI.getPlayerTags(player.getName()).keySet(),",");
			} catch (PlayerDoesNotExistException e) {
				return "Err404";
			} catch (ErrorConnectingToEnjinException e) {
				return "Err408";
			}
		}
        @Override 
		public String getDescription() {
			return "{enjintags:*user} - Returns a list of tags for a user\nEnjin features are currently in beta and may be buggy";
		} });
        ISP.addPlaceholder(new Placeholder("enjintag",enjin) { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
    		try {
				return EnjinAPI.getPlayerTags(player.getName()).get(modifiers[0]).toString();
			} catch (PlayerDoesNotExistException e) {
				return "Err404";
			} catch (ErrorConnectingToEnjinException e) {
				return "Err408";
			}
		}
    	@Override 
		public String getDescription() {
			return "{enjintag:tag} - Returns the user's value for a tag)\nEnjin features are currently in beta and may be buggy";
		} });
        ISP.addPlaceholder(new Placeholder("enjintag",enjin) { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
    		try {
				return EnjinAPI.getPlayerTags(player.getName()).get(modifiers[0]).toString();
			} catch (PlayerDoesNotExistException e) {
				return "Err404";
			} catch (ErrorConnectingToEnjinException e) {
				return "Err408";
			}
		}
    	@Override 
		public String getDescription() {
			return "{enjintag:tag} - Returns the user's value for a tag)\nEnjin features are currently in beta and may be buggy";
		} });
        
	}
}