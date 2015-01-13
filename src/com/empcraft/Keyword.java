package com.empcraft;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

abstract class Keyword
{
	private final String key;
	public Keyword(String key)
	{
		this.key = key;
	}
	public String getKey()
	{
		return this.key;
	}
	public String toString()
	{
		return this.key;
	}
	public String getDescription()
	{
		return "There is currently no description";
	}
	public abstract String getValue(Player player, Location location, String[] args,Boolean elevation);
}