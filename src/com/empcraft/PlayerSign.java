package com.empcraft;

import org.bukkit.Location;

public class PlayerSign {
    
    public final int x;
    public final int y;
    public final int z;
    public final String name;
    
    public PlayerSign(String name, Location loc) {
        this.name = name;
        this.x = loc.getBlockX();
        this.y = loc.getBlockY();
        this.z = loc.getBlockZ();
    }
    
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof PlayerSign)) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        PlayerSign other = (PlayerSign) obj;
        return other.x == this.x && other.y == this.y && other.z == this.z && other.name.equals(this.name); 
    }
    
    private int hash;
    
    @Override
    public int hashCode() {
        if (this.hash == 0) {
            this.hash = name.hashCode() * 31 + x * 23 + y + 11 + z;
        }
        return hash;
    }
}
