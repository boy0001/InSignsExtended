package com.empcraft;


import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class SignUpdateEvent extends Event implements Cancellable {
	private String[] lines;
	private Player player;
	private final Location location;
	private static HandlerList handlers = new HandlerList();
	private boolean cancelled = false;
	private Event event;
	
	SignUpdateEvent(Player player, Location location, String[] lines, Event causeEvent)
	{
		this.event = causeEvent;
	    this.player = player;
	    this.location = location;
	    this.lines = lines;
	}
	@Override
	public boolean isCancelled() {
		return this.cancelled;
	}
	@Override
	public void setCancelled(boolean cancelled) {
		cancelled = true;
	}
	@Override
    public HandlerList getHandlers() {
        return handlers;
    }
	
	public static HandlerList getHandlerList() {
        return handlers;
    }
	
	public Player getPlayer() {
		return this.player;
	}
	public Event getCauseEvent() {
		return this.event;
	}
	@Deprecated
	public void setPlayer(Player player) throws IllegalArgumentException {
		if (player==null) {
			throw new IllegalArgumentException("The player cannot be null");
		}
		this.player = player;
	}
	public Location getLocation() {
		return this.location;
	}
	public String[] getLines() {
		return this.lines;
	}
	public void setLines(String[] lines) throws IllegalArgumentException {
		if (lines==null) {
			throw new IllegalArgumentException("The array cannot be null");
		}
		if (lines.length==4) {
			this.lines = lines;
		}
		else {
			throw new  IllegalArgumentException("The array must have a length of four");
		}
	}
	public void setLine(int i,String line) throws IndexOutOfBoundsException {
		this.lines[i] = line;
	}
	public String getLine(int i)
	{
		try {
			return this.lines[i];
		}
		catch (Exception e) {
			return "";
		}
	}
}
