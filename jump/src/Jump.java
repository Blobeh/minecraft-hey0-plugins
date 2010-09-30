import java.util.ArrayList;
import java.util.List;

import java.util.logging.Level;
import java.util.logging.Logger;

public class Jump extends Plugin {


	static final Logger log = Logger.getLogger("Minecraft");

	
    public void enable() {
		log.info("[Jump] Mod Enabled.");
			etc.getInstance().addCommand("/jump", "<Username> - Request a jump to a player");
			etc.getInstance().addCommand("/accept", "<Username> - Accept a jump from a player");
    }

    public void disable() {
		etc.getInstance().removeCommand("/jump");
		etc.getInstance().removeCommand("/accept");
		log.info("[Jump] Mod Disabled");
    }
    


    public boolean onCommand(Player player, String[] split) {
        if (!etc.getInstance().canUseCommand(player.getName(), split[0])) {
            return false;
        }

        if (split[0].equalsIgnoreCase("/jump")) {
        	
        	if (split.length < 2) {
          		 player.sendMessage(Colors.Red + split[0] + " <Username>");
          		 return true;
              }
          	
               List<Player> players = getPlayersWithPrefix(split[1]);
               
              
       		if (players.size() < 1) {
       		   	player.sendMessage(Colors.Red + "No player with that name.");
       		   	return true;
       		} else if (players.size() > 1) {
       		 	player.sendMessage(Colors.Red + "More than one player with that name, please try again with the full username.");
       		  	return true;
       		}
       		
       	   	Player p = players.get(0);
        	
        	if (etc.getServer().isTimerExpired(player.getName().toLowerCase() + p.getName().toLowerCase())) {
        		player.sendMessage(Colors.Red + "You can request a jump to a player once every 30 seconds");
        		return true;
        	}
        	else
        	{
        		p.sendMessage(Colors.Red + player.getName() + " wants to jump to your location. Use the command /accept " + player.getName() + " to accept, you have 30 seconds.");
        		player.sendMessage(Colors.Red + "Request sent to " + p.getName() + ".");
        		etc.getServer().setTimer(player.getName().toLowerCase() + p.getName().toLowerCase(), 500);
        		return true;
        	}
        
        } else if (split[0].equalsIgnoreCase("/accept")) {
        	
        	if (split.length < 2) {
          		 player.sendMessage(Colors.Red + split[0] + " <Username>");
          		 return true;
              }
          	
               List<Player> players = getPlayersWithPrefix(split[1]);
               
              
       		if (players.size() < 1) {
       		   	player.sendMessage(Colors.Red + "No player with that name.");
       		   	return true;
       		} else if (players.size() > 1) {
       		 	player.sendMessage(Colors.Red + "More than one player with that name, please try again with the full username.");
       		  	return true;
       		}
       		
       	   	Player p = players.get(0);
        	
			if (! etc.getServer().isTimerExpired(p.getName().toLowerCase() + player.getName().toLowerCase()))
			{
				player.sendMessage(Colors.Red + "FAILED: The player must request a teleport and you must respond within 30 seconds");
				return true;
			}
			else {
				p.teleportTo(player);
				p.sendMessage(Colors.Red + "Woosh!");
				etc.getServer().setTimer(p.getName().toLowerCase() + player.getName().toLowerCase(), 1);
				return true;
			}
            
        }
        else {
            return false;
        }
    }

    public String onLoginChecks(String user) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void onLogin(Player player) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean onChat(Player player, String message) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    public List<Player> getPlayersWithPrefix(String playerName)
    {
    	List<Player> players = new ArrayList<Player>();
    	for (Player p : etc.getServer().getPlayerList()) {
            if (p != null) {
                if (p.getName().toLowerCase().startsWith(playerName)) {
                   players.add(p);
                }
                	
            }
        }
    	return players;
    }

    public void onBan(Player player, String reason) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void onIpBan(Player player, String reason) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void onKick(Player player, String reason) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

	public boolean onBlockCreate(Player arg0, Block arg1) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public boolean onBlockDestroy(Player arg0, Block arg1) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

}
