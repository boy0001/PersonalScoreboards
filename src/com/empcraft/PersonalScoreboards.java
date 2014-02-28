package com.empcraft;
/**
 * 
 */
/**
 * @author Jesse Boyd
 *
 */
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Map.Entry;
import java.util.logging.Logger;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerBedLeaveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

public final class PersonalScoreboards extends JavaPlugin implements Listener {
    private static final Logger log = Logger.getLogger("Minecraft");
    public static Economy econ = null;
    public static Permission perms = null;
    public static Chat chat = null;
    public static int counter = 0;
    public static int counter2 = 0;
    public static Map<String, Object> globals = new HashMap<String, Object>();
    public static Map<String, Integer> kills = new HashMap<String, Integer>();
	@EventHandler
    public void onPlayerkill(PlayerDeathEvent event){
    	Player killer = event.getEntity().getKiller();
    	Player killed = event.getEntity();
    	 if((killer instanceof Player)){
 	    	try {
	    		kills.put(killer.getName(),kills.get(killer.getName())+1);
	    	}
 	    	catch (Exception e) {
 	    		kills.put(killer.getName(),1);
 	    	}
    	 }
    	 kills.put(killed.getName(),0);

    }
    public String matchgroup(String group) {
		String[] groups = (perms.getGroups());
		for (String current:groups) {
			if (group.equalsIgnoreCase(current)) {
				return current;
			}
		}
		return "";
    }
    public boolean checkperm(Player player,String perm) {
    	boolean hasperm = false;
    	String[] nodes = perm.split("\\.");
    	
    	String n2 = "";
    	if (player.hasPermission(perm)) {
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
    public String fphs(String line, Player user, Player sender) {
    	String[] mysplit = line.substring(1,line.length()-1).split(":");
    	boolean changed = false;
    	if ((Bukkit.getPlayer(mysplit[mysplit.length-1])!=null)) {
    		user = Bukkit.getPlayer(mysplit[mysplit.length]);
    		changed = true;
    	}
    	if (line.contains("{setgroup:")) {
    		boolean hasperm = false;
    		if (user==null) {
    			hasperm = true;
    		}
    		else if (sender.isOp()==true) {
    			hasperm = true;
    		}
    		else if (checkperm(sender,"signranks.setgroup")==true) {
    			hasperm = true;
    		}
    		if (hasperm) {
    		if ((mysplit.length == 2)&&(user!=null)) {
    			perms.playerAddGroup(user, mysplit[1]);
    			if (perms.getPrimaryGroup(user).equals(mysplit[1])==false) {
        			perms.playerRemoveGroup(user, perms.getPrimaryGroup(user));
        			perms.playerRemoveGroup(user, mysplit[1]);
        			perms.playerAddGroup(user, mysplit[1]);
    			}
    		}
    		else if ((mysplit.length == 3)) {
    			if ((Bukkit.getPlayer(mysplit[1])!=null)) {
    				perms.playerAddGroup(Bukkit.getPlayer(mysplit[1]), mysplit[2]);
    				if (perms.getPrimaryGroup(Bukkit.getPlayer(mysplit[1])).equals(mysplit[2])==false) {
            			perms.playerRemoveGroup(Bukkit.getPlayer(mysplit[1]), perms.getPrimaryGroup(user));
            			perms.playerRemoveGroup(Bukkit.getPlayer(mysplit[1]), mysplit[2]);
            			perms.playerAddGroup(Bukkit.getPlayer(mysplit[1]), mysplit[2]);
    				}
    			}
    			
    		}
    		else if (mysplit.length == 4){
    			try {
    				perms.playerAddGroup(Bukkit.getWorld(mysplit[3]),mysplit[1], mysplit[2]);
    				if (perms.getPrimaryGroup(Bukkit.getWorld(mysplit[3]),mysplit[1]).equals(mysplit[2])==false) {
        				perms.playerRemoveGroup(Bukkit.getWorld(mysplit[3]),mysplit[1],perms.getPrimaryGroup(Bukkit.getWorld(mysplit[3]),mysplit[1]));
        				perms.playerRemoveGroup(Bukkit.getWorld(mysplit[3]),mysplit[1],mysplit[2]);
        				perms.playerAddGroup(Bukkit.getWorld(mysplit[3]),mysplit[1], mysplit[2]);
    				}
    			}
    			catch (Exception e) {
    				System.out.println(e);
    			}
    		}
    	}
    	}
    	if (line.contains("{delsub:")) {
    		boolean hasperm = false;
    		if (user==null) {
    			hasperm = true;
    		}
    		else if (sender.isOp()==true) {
    			hasperm = true;
    		}
    		else if (checkperm(sender,"signranks.delsub")==true) {
    			hasperm = true;
    		}
    		if (hasperm) {
    		if ((mysplit.length == 2)&&(user!=null)) {
    			perms.playerRemoveGroup(user, mysplit[1]);
    		}
    		else if ((mysplit.length == 3)) {
    			if ((Bukkit.getPlayer(mysplit[1])!=null)) {
    				perms.playerRemoveGroup(Bukkit.getPlayer(mysplit[1]), mysplit[1]);
    			}
    		}
    		else if (mysplit.length == 4){
    			try {
    				perms.playerRemoveGroup(Bukkit.getWorld(mysplit[3]),mysplit[1], mysplit[2]);
    			}
    			catch (Exception e) {
    				System.out.println(e);
    			}
    		}
    	}
    	}
    	else if (line.contains("{prefix:")) {
    		boolean hasperm = false;
    		if (user==null) {
    			hasperm = true;
    		}
    		else if (sender.isOp()==true) {
    			hasperm = true;
    		}
    		else if (checkperm(sender,"signranks.prefix")==true) {
    			hasperm = true;
    		}
    		if (hasperm) {
    		if ((mysplit.length == 2)) {
    			if ((Bukkit.getPlayer(mysplit[1])!=null)) {
    				return chat.getPlayerPrefix(Bukkit.getPlayer(mysplit[1]));
    			}
    			else {
    				chat.setPlayerPrefix(user, mysplit[1]);
    			}
    		}
    		if ((mysplit.length >= 3)) {
    			if ((Bukkit.getPlayer(mysplit[1])!=null)) {
    				chat.setPlayerPrefix(Bukkit.getPlayer(mysplit[1]), mysplit[2]);
    			}
    		}
    	}
    	}
    	else if (line.contains("{suffix:")) {
    		boolean hasperm = false;
    		if (user==null) {
    			hasperm = true;
    		}
    		else if (user.isOp()==true) {
    			hasperm = true;
    		}
    		else if (checkperm(user,"signranks.suffix")==true) {
    			hasperm = true;
    		}
    		if (hasperm) {
    		if ((mysplit.length == 2)) {
    			if ((Bukkit.getPlayer(mysplit[1])!=null)) {
    				return chat.getPlayerSuffix(Bukkit.getPlayer(mysplit[1]));
    			}
    			else {
    				chat.setPlayerSuffix(user, mysplit[1]);
    			}
    		}
    		if ((mysplit.length == 3)) {
    			if ((Bukkit.getPlayer(mysplit[1])!=null)) {
    				chat.setPlayerSuffix(Bukkit.getPlayer(mysplit[1]), mysplit[2]);
    			}
    		}
    		else if (mysplit.length == 4){
    			try {
    				chat.setPlayerSuffix(Bukkit.getWorld(mysplit[3]),mysplit[1], mysplit[2]);
    			}
    			catch (Exception e) {
    				System.out.println(e);
    			}
    		}
    	}
    	}
    	else if (line.contains("{rand:")) {
    		Random random = new Random();
    		return (""+random.nextInt(Integer.parseInt(mysplit[1])));
    	}
    	else if (line.contains("{range:")) {
    		String mylist = "";
    		int start = 0;
    		int stop = 0;
    		if (mysplit.length==2) {
    			stop = Integer.parseInt(mysplit[1]);
    		}
    		else if (mysplit.length==3) {
    			start = Integer.parseInt(mysplit[1]);
    			stop = Integer.parseInt(mysplit[2]);
    		}
    		for(int i = start; i <= stop; i++) {
    			mylist+=i+",";
    		}
    		return mylist.substring(0,mylist.length()-1);
    	}
    	else if (line.contains("{matchplayer:")) {
    		List<Player> matches = getServer().matchPlayer(mysplit[1]);
    		String mymatches = "";
    		if (matches.isEmpty()==false) {
    			for (Player match:matches) {
    				mymatches+=match.getName()+",";
    			}
    			return mymatches.substring(0,mymatches.length()-1);
    		}
    		else {
    			return "null";
    		}
    	}
    	else if (line.contains("{matchgroup:")) {
    		return matchgroup(mysplit[1]);
    	}
    	else if (line.contains("{index:")) {
    		return mysplit[1].split(",")[Integer.parseInt(mysplit[2])];
    	}
    	else if (line.contains("{setindex:")) {
    		String[] mylist = mysplit[1].split(",");
    		String newlist = "";
    		int myindex = Integer.parseInt(mysplit[2]);
    		for(int i = 0; i < mylist.length; i++) {
    			if (i==myindex) {
    				newlist+=mysplit[3]+",";
    			}
    			else {
    				newlist+=mylist[i]+",";
    			}
    		}
    		return newlist.substring(0,newlist.length()-1);
    	}
    	else if (line.contains("{delindex:")) {
    		String[] mylist = mysplit[1].split(",");
    		String newlist = "";
    		int myindex = Integer.parseInt(mysplit[2]);
    		for(int i = 0; i < mylist.length; i++) {
    			if (i==myindex) {
    				
    			}
    			else {
    				newlist+=mylist[i]+",";
    			}
    		}
    		return newlist.substring(0,newlist.length()-1);
    	}
    	else if (line.contains("{sublist:")) {
    		String[] mylist = mysplit[1].split(",");
    		String newlist = "";
    		int i1 = Integer.parseInt(mysplit[2]);
    		int i2 = Integer.parseInt(mysplit[3]);
    		for(int i = 0; i < mylist.length; i++) {
    			if ((i>=i1)&&(i<=i2)) {
    				newlist+=mylist[i]+",";
    			}
    		}
    		return newlist.substring(0,newlist.length()-1);
    	}
    	else if (line.contains("{getindex:")) {
    		String[] mylist = mysplit[1].split(",");
    		String newlist = "";
    		for(int i = 0; i < mylist.length; i++) {
    			if (mylist[i].equals(mysplit[2])) {
    				newlist+=i+",";
    			}
    		}
    		if (newlist.equals("")) {
    			return "null";
    		}
    		return newlist.substring(0,newlist.length()-1);
    	}
    	else if (line.contains("{listhas:")) {
    		String[] mylist = mysplit[1].split(",");
    		String newlist = "";
    		for(int i = 0; i < mylist.length; i++) {
    			if (mylist[i].equals(mysplit[2])) {
    				return "true";
    			}
    		}
    		return "false";
    	}
    	else if (line.contains("{contains:")) {
    		if (mysplit[1].contains(mysplit[2])) {
    			return "true";
    		}
    		return "false";
    	}
    	else if (line.contains("{substring:")) {
    		return mysplit[1].substring(Integer.parseInt(mysplit[2]), Integer.parseInt(mysplit[3]));
    	}
    	else if (line.contains("{length:")) {
    		if (mysplit[1].contains(",")) {
    			return ""+mysplit[1].split(",").length;
    		}
    		else {
    			return ""+mysplit[1].length();
    		}
    	}
    	else if (line.contains("{split:")) {
    		return mysplit[1].replace(mysplit[2],",");
    	}
    	else if (line.contains("{hasperm:")) {
    		if (checkperm(user,mysplit[1])) {
    			return "true";
    		}
    		return "false";
    	}
    	else if (line.contains("{randchoice:")) {
    		String[] mylist = mysplit[1].split(",");
    		Random random = new Random();
    		return mylist[random.nextInt(mylist.length-1)];
    	}
    	else if (line.contains("{worldtype:")) {
    		return ""+Bukkit.getWorld(mysplit[1]).getWorldType();
    	}
    	else if (line.contains("{listreplace:")) {
    		String[] mylist = mysplit[1].split(",");
    		String newlist = "";
    		for(int i = 0; i < mylist.length; i++) {
    			if (mylist[i].equals(mysplit[2])) {
    				newlist+=mysplit[3]+",";
    			}
    		}
    		if (newlist.equals("")) {
    			return "null";
    		}
    		return newlist.substring(0,newlist.length()-1);
    	}
    	else if (line.contains("{replace:")) {
    		return mysplit[1].replace(mysplit[2], mysplit[3]);
    	}
    	else if (line.contains("{config:")) {
    		try {
    		return getConfig().getString(mysplit[1]);
    		}
    		catch (Exception e) {
    			return "null";
    		}
    	}
    	else if (line.contains("{count:")) {
    		if (mysplit[1].contains(",")) {
    			int count = 0;
    			String[] mylist = mysplit[1].split(",");
    			for (String mynum:mylist) {
    				if (mynum.equals(mysplit[2])) {
    					count+=1;
    				}
    			}
    			return ""+count;
    		}
    		else {
    			return ""+StringUtils.countMatches(mysplit[1],mysplit[2]);
    		}
    	}  	
    	return "null";
    }
    
    private String getDirection(Player player) {
        int degrees = (Math.round(player.getLocation().getYaw()) + 180) % 360;
        if (degrees <= 22) return "NORTH";
        if (degrees <= 67) return "NORTHEAST";
        if (degrees <= 112) return "EAST";
        if (degrees <= 157) return "SOUTHEAST";
        if (degrees <= 202) return "SOUTH";
        if (degrees <= 247) return "SOUTHWEST";
        if (degrees <= 292) return "WEST";
        if (degrees <= 337) return "NORTHWEST";
        if (degrees <= 359) return "NORTH";
        return "null";
    }
    
    public String evaluate(String line, Player user, Player sender) {
    	ScriptEngineManager mgr = new ScriptEngineManager();
    	ScriptEngine engine = mgr.getEngineByName("JavaScript");
    	final Map<String, Object> placeholders = new HashMap<String, Object>();
    	if (user != null) {
		placeholders.put("{player}", user.getName());
		placeholders.put("{sneaking}", ""+user.isSneaking());
		placeholders.put("{itempickup}", ""+user.getCanPickupItems());
		placeholders.put("{flying}", ""+user.getAllowFlight());
		placeholders.put("{blocking}", ""+user.isBlocking());
		placeholders.put("{exhaustion}", ""+user.getExhaustion());
		placeholders.put("{firstjoin}", ""+Long.toString(user.getFirstPlayed()/1000));
		placeholders.put("{hunger}", ""+user.getFoodLevel());
		placeholders.put("{maxhealth}", ""+user.getMaxHealth());
		placeholders.put("{maxair}", ""+user.getMaximumAir());
		placeholders.put("{air}", ""+(user.getRemainingAir()/20));
		placeholders.put("{age}", ""+(user.getTicksLived()/20));
		try {
			placeholders.put("{bed}", ""+user.getBedSpawnLocation().getX()+","+user.getBedSpawnLocation().getY()+","+user.getBedSpawnLocation().getZ());
		}
		catch (Exception e3) {
			
		}
		try {
			placeholders.put("{compass}", ""+user.getCompassTarget().getX()+","+user.getCompassTarget().getY()+","+user.getCompassTarget().getZ());
		}
		catch (Exception e3) {
			
		}
		try {
			if (user.getWorld().hasStorm()) {
				placeholders.put("{storm}", "true");
			}
			else {
				placeholders.put("{storm}", "false");
			}
			if (user.getWorld().isThundering()) {
				placeholders.put("{thunder}", "true");
			}
			else {
				placeholders.put("{thunder}", "false");
			}
		}
		catch (Exception e3) {
			
		}
  		try {
  			if ((""+kills.get(user.getName())).equals("null")) {
  				kills.put(user.getName(),0);
  			}
  			placeholders.put("{kills}", ""+kills.get(user.getName()));
  		}
  		catch (Exception e) {
  			placeholders.put("{kills}", "0");
  		}
		placeholders.put("{dead}", ""+user.isDead());
		placeholders.put("{sleeping}", ""+user.isSleeping());
		placeholders.put("{whitelisted}", ""+user.isWhitelisted());
        placeholders.put("{world}", user.getWorld().getName());
        placeholders.put("{x}", String.valueOf(user.getLocation().getX()));
        placeholders.put("{y}", String.valueOf(user.getLocation().getY()));
        placeholders.put("{z}", String.valueOf(user.getLocation().getZ()));
        ExperienceManager expMan = new ExperienceManager(user);
        placeholders.put("{lvl}", ""+expMan.getLevelForExp(expMan.getCurrentExp()));
        placeholders.put("{exp}", ""+expMan.getCurrentExp());
        placeholders.put("{money}", ""+econ.getBalance(user.getName()));
        placeholders.put("{prefix}", ""+chat.getPlayerPrefix(user));
        placeholders.put("{suffix}", ""+chat.getPlayerPrefix(user));
        placeholders.put("{group}", ""+perms.getPrimaryGroup(user));
        placeholders.put("{operator}", ""+user.isOp());
        placeholders.put("{worldtype}", ""+user.getWorld().getWorldType());
        placeholders.put("{itemid}", String.valueOf(user.getInventory().getItemInHand().getTypeId()));
        placeholders.put("{itemamount}", String.valueOf(user.getInventory().getItemInHand().getAmount()));
        placeholders.put("{itemname}", String.valueOf(user.getInventory().getItemInHand().getType()));
        placeholders.put("{durability}", String.valueOf(user.getInventory().getItemInHand().getDurability()));
        placeholders.put("{ip}", user.getAddress().getAddress().toString().split("/")[(user.getAddress().toString().split("/").length)-1].split(":")[0]);
        placeholders.put("{display}", ""+user.getDisplayName());
        if(user.getGameMode() == GameMode.CREATIVE){
        	placeholders.put("{gamemode}", "CREATIVE");
        }
        else if(user.getGameMode() == GameMode.SURVIVAL){
        	placeholders.put("{gamemode}", "SURVIVAL");
        }
        else if(user.getGameMode() == GameMode.ADVENTURE){
        	placeholders.put("{gamemode}", "ADVENTURE");
        }
        placeholders.put("{direction}", getDirection(user));
        placeholders.put("{biome}",user.getWorld().getBiome(user.getLocation().getBlockX(), user.getLocation().getBlockZ()).toString());
  		if (getConfig().getInt("signs.types.custom.debug-level") > 2) {
  	        user.sendMessage(ChatColor.GOLD+"======DEBUG======");
  	        user.sendMessage(""+placeholders);
		}
  		placeholders.put("{health}", String.valueOf(user.getHealth()));
        
    	}
    	else {
    		placeholders.put("{player}", "CONSOLE");
    		placeholders.put("{operator}", "true");
    	}
    	if (sender!=null) {
    		placeholders.put("{sender}", sender.getName());
    	}
        String[] args = line.split(" "); 
        for(int i = 0; i < args.length; i++) {
        	placeholders.put("{arg"+(i+1)+"}", args[i]);
        }
        placeholders.put("{line}", line);
        placeholders.put("{epoch}", Long.toString(System.currentTimeMillis()/1000));

  		String online = "";
  		for (Player qwert:getServer().getOnlinePlayers()) {
  			online+=qwert.getName()+",";
  		}
  		try {
  			placeholders.put("{online}", online.substring(0,online.length()-1));
  		}
  		catch (Exception e) {
  			
  		}
  		
  		
  		
  		
  		
  		String mylist = "";
  		placeholders.put("{motd}", ""+Bukkit.getMotd());
  		try {
  		for (OfflinePlayer clist:Bukkit.getBannedPlayers()) {
  			mylist+=clist.getName()+",";
  		}
  		placeholders.put("{banlist}", mylist.substring(0,mylist.length()-1)); 
  		}
  		catch (Exception e5) {
  			
  		}
  		try {
  		mylist = "";
  		for (String clist:Bukkit.getIPBans()) {
  			mylist+=clist+",";
  		}
  		placeholders.put("{baniplist}", mylist.substring(0,mylist.length()-1)); 
		}
		catch (Exception e5) {
			
		}
  		try {
  		mylist = "";
  		for (World clist:getServer().getWorlds()) {
  			mylist+=clist.getName()+",";
  		}
  		placeholders.put("{worlds}", mylist.substring(0,mylist.length()-1)); 
		}
		catch (Exception e5) {
			
		}
  		
  		
  		
  		
  		placeholders.put("{slots}", ""+Bukkit.getMaxPlayers());
  		placeholders.put("{port}", ""+Bukkit.getPort());
  		placeholders.put("{version}", Bukkit.getVersion().split(" ")[0]); // messy dkjaskdjas l- d-asd- (MC: 1.7.2)
  		placeholders.put("{allowflight}", ""+Bukkit.getAllowFlight());
  		placeholders.put("{viewdistance}", ""+Bukkit.getViewDistance());
  		placeholders.put("{defaultgamemode}", ""+Bukkit.getDefaultGameMode());
  		
  		mylist = "";
  		try {
  		for (OfflinePlayer clist:Bukkit.getOperators()) {
  			mylist+=clist.getName()+",";
  		}
  		placeholders.put("{operators}", mylist.substring(0,mylist.length()-1)); // square brackets
		}
		catch (Exception e5) {
			
		}
  		placeholders.put("{whitelist}", ""+Bukkit.getWhitelistedPlayers());
  		
  		
  		
  		
        
        
        
        for (final Entry<String, Object> node : globals.entrySet()) {
       	 if (line.contains(node.getKey())) {
       		 line = line.replace(node.getKey(), (CharSequence) node.getValue());
       	 }
       }
        for (final Entry<String, Object> node : placeholders.entrySet()) {
       	 if (line.contains(node.getKey())) {
       		 line = line.replace(node.getKey(), (CharSequence) node.getValue());
       	 }
        }
       //TODO Functional placeholders
       	 int bracketno = 0;
       	 int last = 0;
       	 boolean isnew = true;
       	 int q = 0;
       	while (StringUtils.countMatches(line, "{")==StringUtils.countMatches(line, "}")) {
       		q++;
       		if ((q>1000)||(StringUtils.countMatches(line, "{")==0)) {
       			break;
       		}
       	for(int i = 0; i < line.length(); i++) {
       		
       		String current = ""+line.charAt(i);
       		if (current.equals("{")) {
       			isnew = true;
       			last = i;
       			bracketno++;
       		}
       		else if (current.equals("}")) {
       			bracketno--;
       			if (isnew) {
       				String toreplace = line.substring(last,i+1);
       				String[] mysplit = line.substring(1,line.length()-1).split(":");
       				boolean replaced = false;
       				try {
       					String result = evaluate("{"+mysplit[0]+"}",Bukkit.getPlayer(mysplit[1]),sender);
       					if (result.equals("null")==false) {
       						line = line.replace(toreplace, result);
       					}
       				}
       				catch (Exception e4) {
       					
       				}
       				if (replaced==false) {
       					try {
       					line = line.replace(toreplace, fphs(toreplace,user,sender));
       					}
       					catch (Exception e) {
       						line = line.replace(toreplace, "null");
       					}
       				}
       				
       				
           			break;
       			}
       			isnew = false;
       		}
       		// {hasperm:{arg:1}}
       		
       	}
       	}			
       	
       
        try {
        	try {
        		Double num = (Double) engine.eval(line);
        		if (Math.ceil(num) == Math.floor(num)) {
        			line = Long.toString(Math.round(num));
        		}
        		else {
        			throw new Exception();
        		}
        	}
        	catch (Exception d) {
        	try {
        		Long num = (Long) engine.eval(line);
        		line = Long.toString(num);
        	}
        	catch (Exception f) {
            	try {
            		int num = (int) engine.eval(line);
            		line = Integer.toString(num);
            	}
            	catch (Exception g) {
                	try {
                		Float num = (Float) engine.eval(line);
                		line = Float.toString(num);
                	}
                	catch (Exception h) {
                    	try {
                    		line = "" + engine.eval(line);
                    	}
                    	catch (Exception i) {
                    	}
                	}
            	}
        	}
        	}
		} catch (Exception e) {
		}
    	return line;
    }
    
    public String colorise(String mystring) {
    	String[] codes = {"&1","&2","&3","&4","&5","&6","&7","&8","&9","&0","&a","&b","&c","&d","&e","&f","&r","&l","&m","&n","&o","&k"};
    	for (String code:codes) {
    		mystring = mystring.replace(code, "§"+code.charAt(1));
    	}
    	return mystring;
    }
    
    Timer timer = new Timer ();
    
	TimerTask mytask = new TimerTask () {
		@Override
	    public void run () {
			counter++;
			if (counter%1==0) {
				//TODO timer stuff
				for (Player player:getServer().getOnlinePlayers()) {
					
					Set<String> scores = getConfig().getConfigurationSection("scoreboard").getKeys(false);
					ScoreboardManager manager = Bukkit.getScoreboardManager();
					Scoreboard sidebar = manager.getNewScoreboard();
					if (checkperm(player,"SB.use")) {
						Objective objective = sidebar.registerNewObjective("test", "dummy");
						if (colorise(evaluate(getConfig().getString("title"),player,player)).length()<16) {
							objective.setDisplayName(colorise(evaluate(getConfig().getString("title"),player,player)));
						}
						else {
							objective.setDisplayName(colorise("&cStats:"));
						}
						objective.setDisplaySlot(DisplaySlot.SIDEBAR);
						for (String score:scores) {
							try {
							if ((score+evaluate(getConfig().getString("scoreboard."+score),player,player)).length()<16) {
							Score myscore = objective.getScore(Bukkit.getOfflinePlayer(colorise(evaluate(score,player,player))));
							myscore.setScore(Integer.parseInt(evaluate(getConfig().getString("scoreboard."+score),player,player)));
							}
							else {
								System.out.println("Sidebar cannot be longer than 16 characters");
							}
							}
							catch (Exception e) {
								
							}
						}
						player.setScoreboard(sidebar);
						
						

						
						
					}
					else {
						// hide it
					}
				}
			}
		}
	};
	
	
    @Override
    public void onDisable() {
    	System.out.println("[SideBar] Scoreboard saving will be implemented soon");
    	try {
    	timer.cancel();
    	timer.purge();
    	}
    	catch (IllegalStateException e) {
    		
    	}
    	catch (Throwable e) {
    		
    	}
    	this.reloadConfig();
    	this.saveConfig();
		
        System.out.println("DONE!");
    }
	@Override
    public void onEnable(){
		getConfig().options().copyDefaults(true);
        if (!setupEconomy() ) {
            log.severe(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        
        final Map<String, Object> options = new HashMap<String, Object>();
        getConfig().set("version", "0.0.1");
        //TODO config
        
        options.put("title","Stats:");
        
        
        for (final Entry<String, Object> node : options.entrySet()) {
       	 if (!getConfig().contains(node.getKey())) {
       		 getConfig().set(node.getKey(), node.getValue());
       	 }
       }
    	saveConfig();
        setupPermissions();
        setupChat();
    	this.saveDefaultConfig();
    	getServer().getPluginManager().registerEvents(this, this);
    	timer.schedule (mytask,0l, 1000);
	}
    
    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }
    private boolean setupPermissions() {
        RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
        perms = rsp.getProvider();
        return perms != null;
    }
	private boolean setupChat() {
        RegisteredServiceProvider<Chat> rsp = getServer().getServicesManager().getRegistration(Chat.class);
        chat = rsp.getProvider();
        return chat != null;
    }
	 public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
	    	String line = "";
	    	for (String i:args) {
	    		line+=i+" ";
	    	}
	    	if ((cmd.getName().equalsIgnoreCase("sb"))||(cmd.getName().equalsIgnoreCase("sidebar"))) {
	    		if (args.length > 0) {
	    			boolean hasperm = false;
	    			if (sender instanceof Player==false) {
	    				if (checkperm((Player) sender,"sb.reload")) {
	    					hasperm = true;
	    					sender.sendMessage(colorise("&cSB &7has been &creloaded&7."));
	    				}
	    				else {
	    					sender.sendMessage(colorise("&7You do not have the perm: &cSB.reload&7."));
	    				}
	    			}
	    			else {
	    				hasperm = true;
	    				System.out.println("SB has been reloaded.");
	    			}
	    			if (hasperm) {
	        			this.reloadConfig();
	        			this.saveDefaultConfig();
	    			}
	    		}
	    		else {
	    			if (sender instanceof Player==false) {
	    				sender.sendMessage(colorise("&c/SB reload"));
	    			}
	    			else {
	    				System.out.println("/SB reload");
	    			}
	    		}
	    	}
		return true;
	 }
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
}