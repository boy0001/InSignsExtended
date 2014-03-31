package com.empcraft;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public abstract class Placeholder
{
	private final String placeholder;
  
    public Placeholder(String placeholder)
    {
  	this.placeholder = placeholder;
  }
  public String getKey()
  {
    return this.placeholder;
  }
  public String toString()
  {
    return this.placeholder;
  }
  public abstract String getValue(Player player, Location location, String[] modifiers,Boolean elevation);
}