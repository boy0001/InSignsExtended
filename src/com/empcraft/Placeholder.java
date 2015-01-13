package com.empcraft;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public abstract class Placeholder
{
	private final String placeholder;
	private Plugin plugin;
	public Placeholder(String placeholder)
	{
		this.placeholder = placeholder;
	}
	public Placeholder(String placeholder,Plugin plugin)
	{
		this.placeholder = placeholder;
		this.plugin = plugin;
	}
	public Plugin getPlugin()
	{
		return this.plugin;
	}
	public String getKey()
	{
		return this.placeholder;
	}
	public String toString()
	{
		return this.placeholder;
	}
	public String getDescription()
	{
		return "There is currently no description";
	}
	public abstract String getValue(Player player, Location location, String[] modifiers,Boolean elevation);
}