package com.empcraft;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.tools.DiagnosticCollector;
import javax.tools.DiagnosticListener;
import javax.tools.FileObject;
import javax.tools.JavaCompiler;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public final class InSignsPlus extends JavaPlugin implements Listener {
    public static String version = null;
    static ScriptEngine engine = (new ScriptEngineManager()).getEngineByName("JavaScript");
    
    volatile Map<String, Keyword> keywords = new ConcurrentHashMap<String, Keyword>();
    volatile Map<String, Placeholder> placeholders = new ConcurrentHashMap<String, Placeholder>();
    volatile Map<String, Placeholder> defaultplaceholders = new ConcurrentHashMap<String, Placeholder>();
    volatile Map<String, Object> globals = new ConcurrentHashMap<String, Object>();
    
    LinkedHashSet<SignPlus> updateQueue = new LinkedHashSet<SignPlus>();
    
    //	volatile List<Location> list = new ArrayList<Location>();
    //	volatile List<String> players = new ArrayList<String>();
    //	volatile List<Integer> clicks = new ArrayList<Integer>();
    
    private volatile Player currentplayer = null;
    private volatile Player currentsender = null;
    private volatile String clickType;
    
    
    static InSignsPlus plugin;
    Plugin individualmessages = null;
    Plugin protocolLibPlugin = null;
    private ProtocolClass protocolclass;
    private boolean instanceofBlockBreak = false;
    int recursion = 0;
    
    public synchronized void setClicked(String click) {
        this.clickType = click;
    }
    public synchronized String getClicked() {
        return this.clickType;
    }
    public void setUser(Player player) {
        this.currentplayer = player;
    }
    public void setSender(Player player) {
        this.currentsender = player;
    }
    public Player getUser() {
        return this.currentplayer;
    }
    public Player getSender() {
        return this.currentsender;
    }
    private String fphs(String line, Boolean elevation,Location interact) {
        Player user = getUser();
        String[] mysplit = line.substring(1,line.length()-1).split(":");
        if (mysplit.length==2) {
            if ((Bukkit.getPlayer(mysplit[1])!=null)) {
                user = Bukkit.getPlayer(mysplit[1]);
                line = StringUtils.join(mysplit,":").replace(":"+mysplit[1],"");
            }
            try {
                if ((Bukkit.getPlayer(UUID.fromString(mysplit[1]))!=null)) {
                    user = Bukkit.getPlayer(UUID.fromString(mysplit[1]));
                    line = StringUtils.join(mysplit,":").replace(":"+mysplit[1],"");
                }
            }
            catch (Exception e4) {}
        }
        try {
            String[] modifiers;
            String key = mysplit[0];
            try {
                modifiers = line.substring(2+key.length(), line.length()-1).split(":");
            }
            catch (Exception e2) {
                modifiers = new String[0];
            }
            return getPlaceholder(key).getValue(user, interact, modifiers, elevation);
        }
        catch (Exception e) {
            
        }
        for (Entry<String, Object> node : globals.entrySet()) {
            if (line.equals(node.getKey())) {
                return ""+node.getValue();
            }
        }
        return "null";
    }
    public String evaluate(String line, Boolean elevation,Location interact) {
        try {
            String[] args = line.split(" ");
            int q = 0;
            List<Integer> indicies = new ArrayList<Integer>();
            for(int i = 0; i < line.length(); i++) {
                char current = line.charAt(i);
                if (current == '{') {
                    indicies.add(i);
                    q++;
                }
            else if (current == '}') {
                if (q>0) {
                    if (recursion<513) {
                        q--;
                        int lastindx = indicies.size()-1;
                        int start = indicies.get(lastindx);
                        String result;
                        try { result = fphs(line.substring(start,i+1), elevation, interact); }
                        catch (Exception e) {
                            result = "null";
                            msg(null,"ERR(syntax) Invalid Placeholder: "+line.substring(start,i+1));
                        }
                        line = (new StringBuffer(line).replace(start, i+1, result)).toString();
                        indicies.remove(lastindx);
                        i = start;
                    }
                    else {
                        msg(null,"ERR(recursion): "+line);
                        msg(null,"^ placeholder returns another placeholder");
                    }
                }
            }
        }
        if (line.contains(",")==false)
        {
            if(line.matches(".*\\d.*")){
                boolean num = false;
                if (line.contains("+")) {
                    num = true;
                }
                else if (line.contains("-")) {
                    num = true;
                }
                else if (line.contains("*")) {
                    num = true;
                }
                else if (line.contains("/")) {
                    num = true;
                }
                else if (line.contains("%")) {
                    num = true;
                }
                else if (line.contains("=")) {
                    num = true;
                }
                else if (line.contains(">")) {
                    num = true;
                }
                else if (line.contains("<")) {
                    num = true;
                }
                else if (line.contains("|")) {
                    num = true;
                }
                else if (line.contains("&")) {
                    num = true;
                }
                if (num) {
                    line = javascript(line);
                }
            }
        }
        if (line.equals("null")) {
            return "";
        }
        return line;
    }
    catch (Exception e2) {
        e2.printStackTrace();return "";
    }
}
public String javascript(String line) {
    try {
        Object toreturn;
        if ((line.contains(".js"))&&(line.contains(" ")==false)) {
            File file = new File(getDataFolder() + File.separator + getConfig().getString("scripting.directory") + File.separator + line);
            toreturn = engine.eval(new java.io.FileReader(file));
        }
        else {
            toreturn =  engine.eval(line);
        }
        try {
            Double num = (Double) toreturn;
            if (Math.ceil(num) == Math.floor(num)) {
                line = Long.toString(Math.round(num));
            }
            else {
                throw new Exception();
            }
        }
        catch (Exception d) {
            try {
                Long num = (Long) toreturn;
                line = Long.toString(num);
            }
            catch (Exception f) {
                try {
                    Integer num = (Integer) toreturn;
                    line = Integer.toString(num);
                }
                catch (Exception g) {
                    try {
                        Float num = (Float) toreturn;
                        line = Float.toString(num);
                    }
                    catch (Exception h) {
                        try {
                            line = "" + toreturn;
                        }
                        catch (Exception i) { }
                    }
                }
            }
        }
    } catch (Exception e) { }
    return line;
}
public String getmsg(String key) {
    File yamlFile = new File(getDataFolder(), getConfig().getString("language").toLowerCase()+".yml");
    YamlConfiguration.loadConfiguration(yamlFile);
    try {
        return colorise(YamlConfiguration.loadConfiguration(yamlFile).getString(key));
    }
    catch (Exception e){
        return "";
    }
}
public boolean iswhitelisted(String lines) {
    List<String> mylist= getConfig().getStringList("signs.autoupdate.whitelist");
    for(String current:mylist){
        if(lines.contains("{"+current+"}")) {
            return true;
        }
        else if(lines.contains("{"+current+":")) {
            return true;
        }
    }
    return false;
}
String colorise(String mystring) {
    return ChatColor.translateAlternateColorCodes('&', mystring);
}
boolean checkperm(Player player,String perm) {
    boolean hasperm = false;
    String[] nodes = perm.split("\\.");
    
    String n2 = "";
    if (player==null) {
        return true;
    }
    else if (player.hasPermission(perm)) {
        hasperm = true;
    }
    else if (player.isOp()==true) {
        hasperm = true;
    }
    else {
        for(int i = 0; i < nodes.length-1; i++) {
            n2+=nodes[i]+".";
            if (player.hasPermission(n2+"*")) {
                hasperm = true;
            }
        }
    }
    return hasperm;
}
void msg(Player player,String mystring) {
    if (ChatColor.stripColor(mystring).equals("")) { return; }
    if (player==null) {
        getServer().getConsoleSender().sendMessage(colorise(mystring));
    }
    else {
        player.sendMessage(colorise(mystring));
    }
}
@Override
public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
    CommandManager commandmanager = new CommandManager();
    return commandmanager.performCommand(sender, cmd, label, args, plugin);
}
public boolean testif(String mystring, boolean elevation, Location interact) {
    String[] args;
    if (mystring.substring(0, 2).equalsIgnoreCase("if")) {
        mystring = mystring.substring(3,mystring.length());
    }
    if (mystring.equalsIgnoreCase("false")) {
        return false;
    }
    else if (mystring.equalsIgnoreCase("true")) {
        return true;
    }
    int splittype = 0;
    mystring=mystring.trim();
    if (mystring.contains("!=") == true) {
        splittype = 6;
        args = mystring.split("!=");
    }
    else if (mystring.contains(">=") == true) {
        splittype = 4;
        args = mystring.split(">=");
    }
    else if (mystring.contains("<=") == true) {
        splittype = 5;
        args = mystring.split("<=");
    }
    else if (mystring.contains("=~") == true) {
        splittype = 7;
        args = mystring.split("=~");
    }
    else if (mystring.contains("==") == true) {
        splittype = 1;
        args = mystring.split("==");
    }
    else if (mystring.contains("=") == true) {
        splittype = 1;
        args = mystring.split("=");
    }
    else if (mystring.contains(">") == true) {
        splittype = 2;
        args = mystring.split(">");
    }
    else if (mystring.contains("<") == true) {
        splittype = 3;
        args = mystring.split("<");
    }
    else if (mystring.contains("!") == true) {
        splittype = 6;
        args = mystring.split("!");
    }
    else {
        return false;
    }
    boolean toreturn = false;
    String left = args[0].trim();
    String right = args[1].trim();
    Object result1 = null;
    Object result2 = null;
    boolean evaluated = false;
    try {
        result1 = engine.eval(left);
        result2 = engine.eval(right);
        evaluated = true;
        if (splittype == 1) {return (result1.equals(result2));}
        else if (splittype == 6) {return !(result1.equals(result2));}
        if (result1 instanceof Double&&result2 instanceof Double) {
            Double result3 = (Double) result1;
            Double result4 = (Double) result2;
            if (splittype == 2) { return (result3>result4); }
            if (splittype == 3) { return (result3<result4); }
            else if (splittype == 4) { return (result3>=result4); }
            else if (splittype == 5) { return (result3<=result4); }
        }
        if (result1 instanceof Integer&&result2 instanceof Integer) {
            Integer result3 = (Integer) result1;
            Integer result4 = (Integer) result2;
            if (splittype == 2) { return (result3>result4); }
            else if (splittype == 3) { return (result3<result4); }
            else if (splittype == 4) { return (result3>=result4); }
            else if (splittype == 5) { return (result3<=result4); }
        }
        if (result1 instanceof Float&&result2 instanceof Float) {
            Float result3 = (Float) result1;
            Float result4 = (Float) result2;
            if (splittype == 2) { return (result3>result4); }
            else if (splittype == 3) { return (result3<result4); }
            else if (splittype == 4) { return (result3>=result4); }
            else if (splittype == 5) { return (result3<=result4); }
        }
        if (result1 instanceof Long&&result2 instanceof Long) {
            Long result3 = (Long) result1;
            Long result4 = (Long) result2;
            if (splittype == 2) { return (result3>result4); }
            else if (splittype == 3) { return (result3<result4); }
            else if (splittype == 4) { return (result3>=result4); }
            else if (splittype == 5) { return (result3<=result4); }
        }
    }
    catch (Exception e) {
        if (evaluated) { msg(null,"ERR(syntax): "+mystring); }
    }
    if (splittype == 1) {return (left.equals(right));}
    else if (splittype == 2) { return (left.compareTo(right)>0); }
    else if (splittype == 3) { return (left.compareTo(right)<0); }
    else if (splittype == 4) { return (left.compareTo(right)>=0); }
    else if (splittype == 5) { return (left.compareTo(right)<=0); }
    else if (splittype == 6) {return !(result1.equals(result2));}
    else if (splittype == 7) { return (left.equalsIgnoreCase(right)); }
    msg(null,"ERR(syntax): "+mystring);
    return toreturn;
}
void addgvar(String key,String value ) {
    globals.put(key, value);
}
void delgvar(String key) {
    globals.remove(key);
}
public LinkedHashSet<SignPlus> getUpdateQueue() {
    return updateQueue;
}
public void removeUpdateQueue(SignPlus sp) {
    updateQueue.remove(sp);
}
public void addUpdateQueue(Player player, Location loc, boolean[] lines) {
    SignPlus toAdd = new SignPlus(loc, player, lines);
    if (updateQueue.contains(toAdd)==false) {
        updateQueue.add(toAdd);
    }
}
public void onDisable() {
    getConfig().getConfigurationSection("scripting").set("placeholders", null);
    try {
        timer.cancel();
        timer.purge();
    }
    catch (Exception e) { }
    reloadConfig();
    saveConfig();
    msg(null,"SAVING VARIABLES!");
    try {
        for (final Entry<String, Object> node : globals.entrySet()) {
            getConfig().options().copyDefaults(true);
            getConfig().set("scripting.variables."+(""+node.getKey()).substring(1,(""+node.getKey()).length()-1), (""+node.getValue()));
            saveConfig();
        }
    }
    catch (Exception e) {}
    msg(null,"&f&oThanks for using &aInSignsPlus&f by Empire92!");
    }
public String execute(String line, Boolean elevation,Location interact) {
    Player user = getUser();
    recursion++;
    try {
        String[] mycmds = line.split(";");
        boolean hasperm = true;
        int depth = 0;
        int last = 0;
        int i2 = 0;
        String myvar = "null";
        for(int i = 0; i < mycmds.length; i++) {
            if (i>=i2) {
                String mycommand = evaluate(mycmds[i],elevation,interact);
                if (!(mycommand.equals("")||mycommand.equals("null"))) {
                    String[]	 cmdargs = mycommand.split(" ");
                    if (cmdargs[0].trim().equalsIgnoreCase("for")) {
                        if (hasperm) {
                            int mylength = 0;
                            int mode = 0;
                            String mytest = "";
                            int depth2 = 1;
                            int j = 0;
                            for(j = i+1; j < mycmds.length; j++) {
                                if (mycmds[j].split(" ")[0].trim().equals("for")) {
                                    depth2+=1;
                                }
                                else if (mycmds[j].split(" ")[0].trim().equals("endloop")) {
                                    depth2-=1;
                                }
                                if (depth2>0) {
                                    mytest+=mycmds[j]+";";
                                }
                                else {
                                }
                                if ((depth2 == 0)||(j==mycmds.length-1)) {
                                    if (cmdargs[1].contains(":")) {
                                        try {
                                            mylength = Integer.parseInt(cmdargs[1].split(":")[1].trim());
                                        }
                                        catch (Exception e) {
                                            mylength = cmdargs[1].split(":")[1].split(",").length;
                                            mode = 1;
                                        }
                                    }
                                    else {
                                        try {
                                            
                                            mylength = Integer.parseInt(cmdargs[1].trim());
                                        }
                                        catch (Exception e) {
                                            mylength = 0;
                                        }
                                    }
                                    if (mode == 1) {
                                        myvar = "{"+cmdargs[1].split(":")[0]+"},"+globals.get("{"+cmdargs[1].split(":")[0]+"}");
                                    }
                                    if (mylength>512) {
                                        mylength = 512;
                                    }
                                    break;
                                }
                            }
                            for(int k = 0; k < mylength; k++) {
                                if (mode == 1) {
                                    globals.put("{"+cmdargs[1].split(":")[0]+"}", cmdargs[1].split(":")[1].split(",")[k]);
                                }
                                if (recursion<512) {
                                    execute(mytest,elevation,interact);
                                }
                            }
                            if (mode == 1) {
                                if (myvar.split(",")[1].equals("null")) {
                                    globals.remove("{"+cmdargs[1].split(":")[0]+"}");
                                }
                                else {
                                    globals.put("{"+cmdargs[1].split(":")[0]+"}", myvar.split(",")[1]);
                                }
                            }
                            i2=j+1;
                        }
                    }
                    else if (cmdargs[0].equalsIgnoreCase("if")) {
                        if (hasperm&&(depth==last)) {
                            last++;
                            hasperm = testif(mycommand, elevation, interact);
                        }
                        else {
                        }
                        depth++;
                    }
                    else if (cmdargs[0].equalsIgnoreCase("else")) {
                        if (last==depth) {
                            if (hasperm) {
                                hasperm = false;
                            }
                            else {
                                hasperm = true;
                            }
                        }
                    }
                    else if (cmdargs[0].equalsIgnoreCase("endif")) {
                        if (depth >0) {
                            if (last==depth) {
                                hasperm = true;
                                if (user != null) {
                                }
                            }
                            if (last==depth) {
                                last-=1;
                            }
                            depth-=1;
                        }
                        else {
                        }
                    }
                    else if (hasperm) {
                    	if (mycommand.charAt(0)=='\\') {
                    		mycommand = "\\ "+mycommand.substring(1).trim();
                    	}
                    	if (mycommand.charAt(0)=='/') {
                    		mycommand = "/ "+mycommand.substring(1).trim();
                    	}
                    	if (keywords.containsKey(cmdargs[0])) {
                    		String value = keywords.get(cmdargs[0]).getValue(user, interact, Arrays.copyOfRange(cmdargs, 1, cmdargs.length), elevation);
                    		if (value!=null) {
                    			return value;
                    		}
                    	}
                    	else {
                    		msg(user,line.trim());
                    		//TODO send message to player;
                    	}
                        if (true) {
                            mycommand = mycommand.trim();
                            if (user != null) {
                                if (cmdargs[0].equalsIgnoreCase("do")){
                                    mycommand = mycommand.substring(3,mycommand.length());
                                    if (user.isOp()) {
                                        Bukkit.dispatchCommand(user, mycommand);
                                    }
                                    else {
                                        try
                                        {
                                            if (elevation) {
                                                user.setOp(true);
                                            }
                                            Bukkit.dispatchCommand(user, mycommand);
                                        }
                                        catch(Exception e)
                                        {
                                            e.printStackTrace();
                                        }
                                        
                                        finally
                                        {
                                            user.setOp(false);
                                        }
                                        
                                    }
                                    
                                }
                                else if (cmdargs[0].equalsIgnoreCase("return")){
                                    return mycommand.substring(7,mycommand.length());
                                }
                                else {
                                    msg(user,colorise(evaluate(mycommand, elevation,interact)));
                                }
                            }
                            else {
                                if (cmdargs[0].equalsIgnoreCase("return")){
                                    return mycommand.substring(7,mycommand.length());
                                }
                                else if (cmdargs[0].equalsIgnoreCase("do")){
                                    mycommand = mycommand.substring(3,mycommand.length());
                                    getServer().dispatchCommand(getServer().getConsoleSender(), mycommand);
                                }
                                else {
                                    msg(null,evaluate(mycommand, elevation,interact));
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    catch (Exception e2) {
        if (user!=null) {
            msg(user,colorise(getmsg("ERROR")+getmsg("ERROR1"))+e2);
            
        }
        else {
            msg(null,colorise(getmsg("ERROR"))+e2);
        }
    }
    return "null";
}
public synchronized List<Placeholder> getPlaceholders() {
    return new ArrayList<Placeholder>(placeholders.values());
}
public synchronized List<Placeholder> getPlaceholders(Plugin myplugin) {
    ArrayList<Placeholder> toReturn = new ArrayList<Placeholder>();
    for (Placeholder current:placeholders.values()) {
        if (current.getPlugin()!=null) {
            if (current.getPlugin().equals(myplugin)) {
                toReturn.add(current);
            }
        }
    }
    return toReturn;
}
public synchronized List<Placeholder> getPlaceholders(String search) {
    ArrayList<Placeholder> toReturn = new ArrayList<Placeholder>();
    int index = 0;
    for (Placeholder current:placeholders.values()) {
        if (current.getKey().equalsIgnoreCase(search)) {
            toReturn.add(0,current);
            index+=1;
        }
        else if (current.getKey().toLowerCase().contains(search.toLowerCase())) {
            toReturn.add(index,current);
        }
        else if (current.getDescription().toLowerCase().contains(search.toLowerCase())) {
            toReturn.add(current);
        }
        else if (current.getPlugin()!=null) {
            if (current.getPlugin().getName().equalsIgnoreCase(search)) {
                toReturn.add(0,current);
            }
        }
    }
    return toReturn;
}
public synchronized List<String> getPlaceholderKeys() {
    return new ArrayList<String>(placeholders.keySet());
}
public synchronized List<Placeholder> getAllPlaceholders() {
    return new ArrayList<Placeholder>(defaultplaceholders.values());
}
public synchronized Placeholder removePlaceholder (Placeholder placeholder) {
    return placeholders.remove(placeholder.getKey());
}
public synchronized Placeholder removePlaceholder (String key) {
    return placeholders.remove(key);
}
public synchronized void whitelistPlaceholder(String key) {
    List<String> mylist;
    try {
        mylist = getConfig().getStringList("signs.autoupdate.whitelist");
        
    }
    catch (Exception e) {
        mylist = new ArrayList<String>();
        mylist.add(key);
    }
    getConfig().set("signs.autoupdate.whitelist", mylist);
    saveConfig();
}
public synchronized void whitelistPlaceholder(Placeholder placeholder) {
    String key = placeholder.getKey();
    List<String> mylist;
    try {
        mylist = getConfig().getStringList("signs.autoupdate.whitelist");
        
    }
    catch (Exception e) {
        mylist = new ArrayList<String>();
        mylist.add(key);
    }
    getConfig().set("signs.autoupdate.whitelist", mylist);
    saveConfig();
}
public synchronized boolean addPlaceholder (String key) {
    Placeholder placeholder = defaultplaceholders.get(key);
    if (placeholder!=null) {
        placeholders.put(placeholder.getKey(), placeholder);
        return true;
    }
    return false;
}
public synchronized void addPlaceholder (Placeholder placeholder) {
    defaultplaceholders.put(placeholder.getKey(), placeholder);
    placeholders.put(placeholder.getKey(), placeholder);
}
public synchronized Placeholder getPlaceholder (String key) {
    return placeholders.get(key);
}
@Override
public void onEnable(){
    version = getDescription().getVersion();
    plugin = this;
    saveResource("english.yml", true);
    Plugin insignsPlugin = Bukkit.getServer().getPluginManager().getPlugin("InSigns");
    if((insignsPlugin != null)) {
        if (insignsPlugin.isEnabled()) {
            msg(null,"&c[SEVERE] &f'InSigns' is not required if you have 'InSignsPlus' already installed (which you do).");
        }
    }
    Plugin inmePlugin = Bukkit.getServer().getPluginManager().getPlugin("IndividualMessages");
    if((inmePlugin != null)) {
        if (inmePlugin.isEnabled()) {
            msg(null,"&7[Info] Plugin '&aIndividualMessages&7' detected. Hooking into it now.");
            individualmessages = inmePlugin;
        }
    }
    boolean isupdate = false;
    try {
        if (getConfig().getString("version").equals(version)==false) {
            msg(null,"&7Thanks for updating &aInSignsPlus&7!");
                isupdate  = true;
        }
    } catch (Exception e) {isupdate  = true;}
    getConfig().options().copyDefaults(true);
    final Map<String, Object> options = new HashMap<String, Object>();
    getConfig().set("version", version);
    options.put("language","english");
    options.put("protocolLib.use",true);
    options.put("scripting.directory","scripts");
    options.put("scripting.advanced-placeholders",true);
    options.put("scripting.debug-level",0);
    options.put("signs.autoupdate.enabled",true);
    options.put("signs.autoupdate.async","Set to 'true' to have a lower impact on the server - may cause instability");
    options.put("signs.autoupdate.buffer",1000);
    options.put("signs.autoupdate.updates-per-tick",25);
    List<String> whitelist = Arrays.asList("grounded","location","age","localtime","localtime12","display","uses","money","prefix","suffix","group","x","y","z","lvl","exhaustion","health","exp","hunger","air","maxhealth","maxair","gamemode","direction","biome","itemname","itemid","itemamount","durability","dead","sleeping","whitelisted","operator","sneaking","itempickup","flying","blocking","age","bed","compass","spawn","worldticks","time","date","time12","epoch","epochmilli","epochnano","online","worlds","banlist","baniplist","operators","whitelist","randchoice","rand","elevated","matchgroup","matchplayer","hasperm","js","gvar","config","passenger","lastplayed","gprefix","gsuffix");
    options.put("signs.autoupdate.whitelist",whitelist);
    for (final Entry<String, Object> node : options.entrySet()) {
        if (!getConfig().contains(node.getKey())) {
            getConfig().set(node.getKey(), node.getValue());
        }
    }
    if (getConfig().getBoolean("protocolLib.use")) {
        protocolLibPlugin = Bukkit.getServer().getPluginManager().getPlugin("ProtocolLib");
        if (protocolLibPlugin==null) {
            msg(null,"&c[SEVERE] &fInSignsPlus will run better with ProtocolLib installed!");
            msg(null,"&c[SEVERE] &fPlease do not ignore this message!");
        }
        else {
            protocolclass = new ProtocolClass(this);
        }
    }
    try {
        Set<String> vars = getConfig().getConfigurationSection("scripting.variables").getKeys(false);
        for(String current : vars) {
            
            globals.put("{"+current+"}", getConfig().getString("scripting.variables."+current));
        }
    }
    catch (Exception e) { }
    saveConfig();
    saveDefaultConfig();
    if (isupdate&&getConfig().getString("scripting.directory").equalsIgnoreCase("scripts")) {
        File f8 = new File(getDataFolder() + File.separator+"scripts"+File.separator+"example.yml");
        if(f8.exists()!=true) {  saveResource("scripts"+File.separator+"example.yml", false); }
        File f9 = new File(getDataFolder() + File.separator+"scripts"+File.separator+"test.js");
        if(f9.exists()!=true) {  saveResource("scripts"+File.separator+"test.js", false); }
    }
    new DefaultPlaceholders(this);
    new DefaultKeywords(this);
    File f1 = new File(getDataFolder() + File.separator + getConfig().getString("scripting.directory"));
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
                    addPlaceholder(new Placeholder(name) { @Override public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) {
                        if (asconsole) { setUser(null);	}
                        String toreturn = execute(lines, myelevation, location);
                        if (asconsole) { setUser(player); }
                        return toreturn;
                    }
                    @Override
                    public String getDescription() { return description; }
                });
            }
            catch (Exception e2) {
                msg(null,"&cError with file "+getDataFolder()+"/"+getConfig().getString("scripting.directory")+"/"+myph[i].getName()+".");
            }
        }
    }
}
Bukkit.getServer().getPluginManager().registerEvents(this, this);
timer.schedule (mytask,1200000, 1200000);
Plugin factionsPlugin = Bukkit.getServer().getPluginManager().getPlugin("Factions");
Plugin mcorePlugin = Bukkit.getServer().getPluginManager().getPlugin("Mcore");
if (factionsPlugin!=null&&mcorePlugin!=null) {
    new ISPFactions(this,factionsPlugin);
    msg(null,"&a[Yay] InSignsPlus detected Factions. Additional placeholders have been added");
}
Plugin vaultPlugin = Bukkit.getServer().getPluginManager().getPlugin("Vault");
if (vaultPlugin!=null) {
    new VaultFeature(this,vaultPlugin);
    msg(null,"&a[Yay] InSignsPlus detected Vault. Additional placeholders have been added");
}
Plugin enjinPlugin = Bukkit.getServer().getPluginManager().getPlugin("EnjinMinecraftPlugin");
if (enjinPlugin!=null) {
    new EnjinFeature(this,enjinPlugin);
    msg(null,"&a[Yay] InSignsPlus detected Enjin. Additional placeholders have been added");
}
if (getConfig().getBoolean("signs.autoupdate.enabled")) {
    Runnable runnable = new Runnable() {
        final int updateAmount = getConfig().getInt("signs.autoupdate.updates-per-tick");
        @Override
        public void run() {
            recursion = 0;
            int size = updateQueue.size();
            if (size>getConfig().getInt("signs.autoupdate.buffer")*4) {
                updateQueue.remove(updateQueue.iterator().next());
            }
            for (int i=0;i<Math.min(updateAmount, size);i++) {
                SignPlus sp = updateQueue.iterator().next();
                boolean result = sp.update();
                updateQueue.remove(sp);
                if (result) {
                    updateQueue.add(sp);
                }
            }
        }
    };
    if (getConfig().getString("signs.autoupdate.async").equalsIgnoreCase("true")) {
        Bukkit.getServer().getScheduler().scheduleAsyncRepeatingTask(this, runnable  ,0L,1L);
    }
    else {Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(this, runnable  ,0L,1L); }
}
}
Timer timer = new Timer ();
TimerTask mytask = new TimerTask () {
    @Override
    public void run () {
        try {
            msg(null,"&9[&bISP&9] &fSAVING VARIABLES!");
            getConfig().getConfigurationSection("scripting").set("variables", null);
            for (final Entry<String, Object> node : globals.entrySet()) {
                getConfig().options().copyDefaults(true);
                getConfig().set("scripting.variables."+(""+node.getKey()).substring(1,(""+node.getKey()).length()-1), (""+node.getValue()));
                saveConfig();
            }
            msg(null,"DONE!");
        }
        catch (Exception e) {}
    }
};
@Override
public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {
    List<String> toReturn = new ArrayList<String>();
    if (args.length==2) {
        if (args[0].equalsIgnoreCase("help")||args[0].equalsIgnoreCase("disable")) {
            List<String> myCommands = getPlaceholderKeys();
            for (String current:myCommands) {
                if (current.toLowerCase().startsWith(args[1])) {
                    toReturn.add(current);
                }
            }
        }
        else if (args[0].equalsIgnoreCase("enable")) {
            List<Placeholder> all = getAllPlaceholders();
            List<Placeholder> enabled = getPlaceholders();
            for (Placeholder current:all) {
                if (enabled.contains(current)==false) {
                    if (current.getKey().toLowerCase().startsWith(args[1])) {
                        toReturn.add(current.getKey());
                    }
                }
            }
        }
        else if (args[0].equalsIgnoreCase("gvar")) {
            for (String current:globals.keySet()) {
                if (current.toLowerCase().startsWith(args[1])) {
                    toReturn.add(current);
                }
            }
        }
    }
    else if (args.length==1) {
        String[] myCommands = {"help","reload","save","list","keywords","enable","disable","setline","putline","gvar","if","eval","exec","player","all"};
        for (String current:myCommands) {
            if (current.toLowerCase().startsWith(args[0])) {
                toReturn.add(current);
            }
        }
    }
    return toReturn;
}
public void updateAllSigns(Player player,Location loc) {
    List<BlockState> states = new ArrayList<BlockState>();
    World world = player.getWorld();
    List<Chunk> chunks = Arrays.asList(loc.getChunk(),world.getChunkAt(loc.add(16,0,0)),world.getChunkAt(loc.add(16,0,16)),world.getChunkAt(loc.add(0,0,16)),world.getChunkAt(loc.add(-16,0,0)),world.getChunkAt(loc.add(-16,0,-16)),world.getChunkAt(loc.add(0,0,-16)),world.getChunkAt(loc.add(16,0,-16)),world.getChunkAt(loc.add(-16,0,16)));
    for (Chunk chunk:chunks) {
        for (BlockState state:chunk.getTileEntities()) {
            states.add(state);
        }
    }
    for (BlockState current:states) {
        if (current instanceof Sign) {
            updateSign(player, current.getLocation());
        }
    }
    states = null;
}
public void updateAllSigns(Player player) {
    updateAllSigns(player,player.getLocation());
}
@EventHandler
public void onPlayerMove(final PlayerMoveEvent event) {
    if (protocolLibPlugin!=null) { return; }
    if (event.getFrom().getChunk().equals(event.getTo().getChunk())==false) {
        final Player player = event.getPlayer();
        final Location loc = event.getTo();
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
            @Override
            public void run() {
                if (player.isOnline()) {
                    updateAllSigns(player,loc);
                }
            }
        },5L);
    }
}
@EventHandler
public void onPlayerTeleport(final PlayerTeleportEvent event) {
    if (protocolLibPlugin!=null) { return; }
    if (event.getFrom().getChunk().equals(event.getTo().getChunk())==false) {
        final Player player = event.getPlayer();
        final Location loc = event.getTo();
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
            @Override
            public void run() {
                if (player.isOnline()) {
                    updateAllSigns(player,loc);
                }
            }
        },5L);
    }
}
@EventHandler
public void onPlayerJoin(final PlayerJoinEvent event) {
    if (protocolLibPlugin!=null) { return; }
    final Player player = event.getPlayer();
    Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
        @Override
        public void run() {
            if (player.isOnline()) {
                updateAllSigns(player,player.getLocation());
            }
        }
    },5L);
}
@EventHandler
public void onSignChange(SignChangeEvent event) {
    try {
        String lines = StringUtils.join(event.getLines());
        if (lines.length()<2) {
            return;
        }
        if (lines.contains("{")==false) {
            return;
        }
    if (lines.contains("}")==false) {
        return;
    }
    List<String> tocheck = new ArrayList<String>();
    List<String> mylist = getPlaceholderKeys();
    for(String current:mylist){
        if(lines.contains("{"+current+"}")) {
            tocheck.add(current);
        }
        else if(lines.contains("{"+current+":")) {
            tocheck.add(current);
        }
    }
    final Player player = event.getPlayer();
    if (checkperm(player, "insignsplus.create")==false) {
        msg(player,"&6Missing requirements&7: insignsplus.create");
        event.setCancelled(true);
        return;
    }
    boolean tocancel = false;
    for(String current:tocheck) {
        if (checkperm(player, "insignsplus.create."+current)) {
            if (protocolLibPlugin!=null) { return; }
            final Location loc = event.getBlock().getLocation(); World world = loc.getWorld();
            List<Chunk> chunks = Arrays.asList(loc.getChunk(),world.getChunkAt(loc.add(16,0,0)),world.getChunkAt(loc.add(16,0,16)),world.getChunkAt(loc.add(0,0,16)),world.getChunkAt(loc.add(-16,0,0)),world.getChunkAt(loc.add(-16,0,-16)),world.getChunkAt(loc.add(0,0,-16)),world.getChunkAt(loc.add(16,0,-16)),world.getChunkAt(loc.add(-16,0,16)));
            List<Player> myplayers = new ArrayList<Player>();
            for (Chunk chunk:chunks) {
                for (Entity entity:chunk.getEntities()) {
                    if (entity instanceof Player) { if (myplayers.contains((Player) entity)==false) { myplayers.add((Player) entity); } }
                }
            }
            for (final Player user:myplayers) {
                Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
                    @Override
                    public void run() {
                        updateSign(user, loc);
                    }
                },5L);
            }
            return;
        }
        else {
            if (tocancel==false) {
                msg(player,"&6Missing requirements&7:\n");
            }
            msg(player,"&7 - &7insignsplus.create."+current);
            tocancel = true;
        }
    }
    if (tocancel) {
        event.setCancelled(true);
        return;
    }
}
catch (Exception e) {
}
}
private synchronized boolean sendUpdatePacket(SignPlus sp, Event causeEvent) {
    try {
        Location location = sp.getLocation();
        if (location.getBlock()==null) {
            return false;
        }
        if ((location.getBlock().getState() instanceof Sign) == false) {
            return false;
        }
        Sign sign = (Sign) location.getBlock().getState();
        Player player = sp.getPlayer();
        if ((player == null) || (!player.isOnline())) {
            return false;
        }
        if (location.getWorld().equals(player.getWorld())==false) {
            return false;
        }
        if (location.getChunk().isLoaded()==false) {
            return false;
        }
        Boolean contains;
        if (updateQueue.contains(sp)) {
            double dist = location.distanceSquared(player.getLocation());
            if (dist>96) {
                return false;
            }
            contains = true;
        }
        else {
            contains = false;
        }
        if (protocolLibPlugin!=null) {
            player.sendSignChange(sign.getLocation(), sign.getLines());
//            protocolclass.sendPacket(sign, sign.getLines(), player);
            return true;
        }
        else {
            boolean modified = false;
            recursion=0;
            String[] lines = sign.getLines();
            setUser(player);
            setSender(player);
            SignUpdateEvent myevent = new SignUpdateEvent(player, location, lines, null);
            getServer().getPluginManager().callEvent(myevent);
            if (myevent.isCancelled()) {
                myevent.setCancelled(true);
                return false;
            }
            if (myevent.getLines().equals(lines)==false) {
                lines = myevent.getLines();
                modified = true;
            }
            boolean ln1 = false;
            boolean ln2 = false;
            boolean ln3 = false;
            boolean ln4 = false;
            if ((contains)==false) {
                if (lines[0].equals("")==false) {
                    ln1 = iswhitelisted(lines[0]);
                    String result = evaluate(lines[0], false,location);
                    if (result.equals(lines[0])==false) {
                        lines[0] = colorise(result);
                        modified = true;
                    }
                }
                if (lines[1].equals("")==false) {
                    ln2 = iswhitelisted(lines[1]);
                    String result = evaluate(lines[1], false,location);
                    if (result.equals(lines[1])==false) {
                        lines[1] = colorise(result);
                        modified = true;
                    }
                }
                if (lines[2].equals("")==false) {
                    ln3 = iswhitelisted(lines[2]);
                    String result = evaluate(lines[2], false,location);
                    if (result.equals(lines[2])==false) {
                        lines[2] = colorise(result);
                        modified = true;
                    }
                }
                if (lines[3].equals("")==false) {
                    ln4 = iswhitelisted(lines[3]);
                    String result = evaluate(lines[3], false,location);
                    if (result.equals(lines[3])==false) {
                        lines[3] = colorise(result);
                        modified = true;
                    }
                }
                if (modified==true) {
                    for (int i = 0; i < 4; i++) {
                        if (lines[i].contains("\n")) {
                            if ((i < 3)) {
                                if (lines[i+1].isEmpty()) {
                                    lines[i+1] = ChatColor.getLastColors(lines[i].substring(0,15))+lines[i].substring(lines[i].indexOf("\n")+1);
                                }
                            }
                            lines[i] = lines[i].substring(0,lines[i].indexOf("\n"));
                        }
                        if (lines[i].length()>15) {
                            if ((i < 3)) {
                                if (lines[i+1].isEmpty()) {
                                    lines[i+1] = ChatColor.getLastColors(lines[i].substring(0,15))+lines[i].substring(15);
                                }
                            }
                            lines[i] = lines[i].substring(0,15);
                        }
                    }
                    if(ln1||ln2||ln3||ln4) {
                        addUpdateQueue(player, location, new boolean[]{ln1,ln2,ln3,ln4});
                        player.sendSignChange(location, lines);
                    }
                    else {
                        player.sendSignChange(location, lines);
                    }
                }
            }
            else {
                boolean[] linesToUpdate = sp.getLines();
                if (linesToUpdate[0]) { lines[0] = colorise(evaluate(lines[0], false,location)); }
                if (linesToUpdate[1]) { lines[1] = colorise(evaluate(lines[1], false,location)); }
                if (linesToUpdate[2]) { lines[2] = colorise(evaluate(lines[2], false,location)); }
                if (linesToUpdate[3]) { lines[3] = colorise(evaluate(lines[3], false,location)); }
                for (int i = 0; i < 4; i++) {
                    if (lines[i].contains("\n")) {
                        if ((i < 3)) {
                            if (lines[i+1].isEmpty()) {
                                lines[i+1] = ChatColor.getLastColors(lines[i].substring(0,15))+lines[i].substring(lines[i].indexOf("\n")+1);
                            }
                        }
                        lines[i] = lines[i].substring(0,lines[i].indexOf("\n"));
                    }
                    if (lines[i].length()>15) {
                        if ((i < 3)) {
                            if (lines[i+1].isEmpty()) {
                                lines[i+1] = ChatColor.getLastColors(lines[i].substring(0,15))+lines[i].substring(15);
                            }
                        }
                        lines[i] = lines[i].substring(0,15);
                    }
                }
                player.sendSignChange(location, lines);
                return true;
            }
            setUser(null);
            setSender(null);
        }
    }
    catch (Exception e) {
        e.printStackTrace();
    }
    return false;
}
public synchronized boolean updateSign(Player player,Location location, Event event) {
    SignPlus sp = new SignPlus(location, player);
    return sendUpdatePacket(sp, event);
}
public synchronized boolean updateSign(Player player,Location location) {
    SignPlus sp = new SignPlus(location, player);
    return sendUpdatePacket(sp,null);
}
@EventHandler(ignoreCancelled=true,priority=EventPriority.MONITOR)
private void onBlockBreakEvent(BlockBreakEvent event)
{
    instanceofBlockBreak = true;
}

@EventHandler(ignoreCancelled=true,priority=EventPriority.MONITOR)
private void onPlayerInteract(final PlayerInteractEvent event)
{
    if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
        setClicked("right");
    }
    if (event.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
        setClicked("left");
    }
    if (event.isCancelled()) {
        return;
    }
    if (event.getAction() == Action.RIGHT_CLICK_BLOCK||event.getAction() == Action.LEFT_CLICK_BLOCK) {
        Block block = event.getClickedBlock();
        if (block!=null) {
            BlockState state = block.getState();
            if (state instanceof Sign) {
                SignPlus sp = new SignPlus(block.getLocation(), event.getPlayer());
                if (updateQueue.contains(sp)) {
                    for (SignPlus current:updateQueue) {
                        if (current.equals(sp)) {
                            sp.setClicks(current.getClicks()+1);
                            updateQueue.remove(current);
                            updateQueue.add(sp);
                            break;
                        }
                    }
                    if (event.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
                        return;
                    }
                }
                final Player player = event.getPlayer();
                final Location location = event.getClickedBlock().getLocation();
                Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
                    @Override
                    public void run() {
                        if (instanceofBlockBreak==false) {
                            updateSign(player, location, event);
                        }
                        else {
                            instanceofBlockBreak = false;
                        }
                    }
                },0L);
                
            } else { instanceofBlockBreak = false;}
        } else { instanceofBlockBreak = false;}
    } else { instanceofBlockBreak = false;}
}
}