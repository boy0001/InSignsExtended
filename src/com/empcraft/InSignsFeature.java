package com.empcraft;

import de.blablubbabc.insigns.InSigns;
import de.blablubbabc.insigns.SignSendEvent;

import org.bukkit.Location;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;


public class InSignsFeature  implements Listener {	
	Plugin insignsPlugin;
	InSignsExtended plugin;
		
	@EventHandler
	public void SignSendEvent(SignSendEvent event) {
		Location loc = event.getLocation();
		if (InSignsExtended.list.contains(loc)==false) {
				Player player = event.getPlayer();
				String l1 = event.getLine(0);
				String l2 = event.getLine(1);
				String l3 = event.getLine(2);
				String l4 = event.getLine(3);
				boolean modified = false;
				if (l1.equals("")==false) {
					String result = plugin.evaluate(l1,player,player, false);
					if (result.equals(l1)==false) {
						event.setLine(0,plugin.colorise(result));
						modified = true;
					}
				}
				if (l2.equals("")==false) {
					String result = plugin.evaluate(l2,player,player, false);
					if (result.equals(l2)==false) {
						event.setLine(1,plugin.colorise(result));
						modified = true;
					}
				}
				if (l3.equals("")==false) {
					String result = plugin.evaluate(l3,player,player, false);
					if (result.equals(l3)==false) {
						event.setLine(2,plugin.colorise(result));
						modified = true;
					}
				}
				if (l4.equals("")==false) {
					String result = plugin.evaluate(l4,player,player, false);
					if (result.equals(l4)==false) {
						event.setLine(3,plugin.colorise(result));
						modified = true;
					}
				}
				if (modified==true) {
					if(plugin.iswhitelisted(l1+l2+l3+l4)) {
						plugin.isadd(player, loc);	
//						System.out.println("WHITELISTeD");
					}
					else {
//						System.out.println("blackLISTeD");
					}
				}
		}
		else {
			Player player = event.getPlayer();
			event.setLine(0,plugin.colorise(plugin.evaluate(event.getLine(0),player,player, false)));
			event.setLine(1,plugin.colorise(plugin.evaluate(event.getLine(1),player,player, false)));
			event.setLine(2,plugin.colorise(plugin.evaluate(event.getLine(2),player,player, false)));
			event.setLine(3,plugin.colorise(plugin.evaluate(event.getLine(3),player,player, false)));
		}
		
		
	}
	
	public void sendSignChange(Player player, Sign sign) {
		InSigns.sendSignChange(player, sign);
	}
	
    public InSignsFeature(Plugin p2,InSignsExtended p3) {
    	insignsPlugin = p2;
    	plugin = p3;
    	
    }
}