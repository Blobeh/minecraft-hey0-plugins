import java.util.ArrayList;

public class CuboidPlugin extends Plugin {
	// Version 4.2 : 29/09 14h00 GMT+2
	// for servermod100
	
	@SuppressWarnings("unused")
	private String name = "CuboidPlugin";
	
	static ArrayList<String> playerList = new ArrayList<String>();
	static ArrayList<Boolean> lastStatus = new ArrayList<Boolean>();
	static ArrayList<Block> correspondingBloc = new ArrayList<Block>();
	
	public void enable(){
		ProtectedArea.loadProtectedAreas();
	}
	
	public void disable(){
	}
	
	public boolean onCommand(Player player, String[] split) {
		String playerName = player.getName();

		if (etc.getInstance().canUseCommand(playerName, "/protect")){
			if ( split[0].equalsIgnoreCase("/protect" ) ){
				if (Cuboid.isReady(playerName, true)){
					String parameters = "";
					int paramSize = split.length;
					if (paramSize > 2){
						for (short i=1; i<paramSize-1; i++){
							parameters += " "+split[i];
						}
						String cuboidName = split[paramSize-1].trim().toLowerCase();
						short returnCode = ProtectedArea.protegerCuboid(playerName, parameters.toLowerCase(), cuboidName);
						if (returnCode==0){
							player.sendMessage(Colors.LightGreen + "New protected zone created.");
							player.sendMessage(Colors.LightGreen + "Name : "+Colors.White+cuboidName);
							player.sendMessage(Colors.LightGreen + "Owners :"+Colors.White+parameters);
						}
						else if (returnCode==1){
							player.sendMessage(Colors.Rose + "This name is already linked to a protected zone.");
						}
						else if (returnCode==0){
							player.sendMessage(Colors.Rose + "Error while adding the protected Area.");
							player.sendMessage("Check server logs for more info");
						}
					}
					else{
						player.sendMessage(Colors.Yellow + "You need to specify at least one player or group, and a name.");
					}
				}
				else{
					player.sendMessage(Colors.Rose + "No cuboid has been selected");
				}
				return true;
			}
			
			else if (split[0].equalsIgnoreCase("/removeprotected")){
				if(split.length == 2){
					short returnCode = ProtectedArea.removeProtectedZone(playerName, split[1].trim().toLowerCase() );
					if (returnCode == 0){
						player.sendMessage(Colors.Green + "The protected area has been removed");
					}
					else if (returnCode == 1){
						player.sendMessage(Colors.Rose + "No protected area has this name");
					}
					else if (returnCode == 2){
						player.sendMessage(Colors.Rose + "Exception while removing the protected area...");
					}
					else if (returnCode == 3){
						player.sendMessage(Colors.Rose + "protectedCuboids.txt has been deleted !");
					}
				}
				else{
					player.sendMessage(Colors.Rose + "Usage : /removeprotected <protected area name>");
				}
				return true;
			}
			
			else if (split[0].equalsIgnoreCase("/listprotected")){
				String cuboidList = ProtectedArea.listerCuboids();
				player.sendMessage(Colors.Yellow + "Protected areas"+Colors.White+" :"+cuboidList);
				return true;
			}
			
			else if (split[0].equalsIgnoreCase("/toggleprot")){
				ProtectedArea.toggle=!ProtectedArea.toggle;
				player.sendMessage(Colors.Yellow + "Cuboids protection : "+ (ProtectedArea.toggle ? "enabled" : "disabled"));
				return true;
			}
			
			else if (split[0].equalsIgnoreCase("/creload")){
				ProtectedArea.loadProtectedAreas();
				player.sendMessage(Colors.Green + "Cuboids coordinates reloaded");
				return true;
			}
		}
		if (etc.getInstance().canUseCommand(playerName, "/cuboid")){	
			if (split[0].equalsIgnoreCase("/csize")){
				if ( Cuboid.isReady(playerName, true) ){
					player.sendMessage(Colors.LightGreen +"The selected cuboid size is : " + Cuboid.calculerTaille(playerName) +" blocks" );
				}
				else{
					player.sendMessage(Colors.Rose + "No cuboid has been selected");
				}
				return true;
		    }
						
			else if (split[0].equalsIgnoreCase("/cdel")){
				if (Cuboid.isReady(playerName, true)){
					Cuboid.supprimerCuboid(playerName);
					player.sendMessage(Colors.LightGreen + "The cuboid is now empty");
				}
				else{
					player.sendMessage(Colors.Rose + "No cuboid has been selected");
				}
				return true;
			}
			
			else if (split[0].equalsIgnoreCase("/cfill")){
				if (Cuboid.isReady(playerName, true)){
					if (split.length>1){
						int blocID = 0;
						try {
							blocID = Integer.parseInt( split[1] );
						} catch (NumberFormatException n) {
							blocID = etc.getInstance().getDataSource().getItem( split[1] );
							if (blocID == 0){
								player.sendMessage(Colors.Rose + split[1] +" is not a valid block name.");
								return true;
							}
						}					
						if ( isValidBlockID(blocID) ){
							Cuboid.remplirCuboid(playerName, blocID);
							player.sendMessage(Colors.LightGreen + "The cuboid has been filled");
						}
						else{
							player.sendMessage(Colors.Rose +blocID+ " is not a valid block ID.");
						}
					}
					else{
						player.sendMessage(Colors.Rose + "Usage : /cfill <block id|name>");
					}
				}
				else{
					player.sendMessage(Colors.Rose + "No cuboid has been selected");
				}
				return true;
			}
			
			else if (split[0].equalsIgnoreCase("/creplace")){
				if (Cuboid.isReady(playerName, true)){

					int paramSize = split.length-1;
					if (paramSize>1){
						int[] replaceParams = new int[paramSize];
						for (int i = 0; i<paramSize; i++){
							try {
								replaceParams[i] = Integer.parseInt( split[i+1] );
							}
							catch (NumberFormatException n) {
								replaceParams[i] = etc.getInstance().getDataSource().getItem( split[i+1] );
								if ( replaceParams[i] == 0){
									player.sendMessage(Colors.Rose + split[i+1] +" is not a valid block name.");
									return true;
								}
							}
							if ( !isValidBlockID(replaceParams[i]) ){
								player.sendMessage(Colors.Rose +replaceParams[i]+ " is not a valid block ID.");
								return true;
							}
						}
						
						Cuboid.remplacerDansCuboid(playerName, replaceParams);
						player.sendMessage(Colors.LightGreen + "The blocks have been replaced");
					}
					else{
						player.sendMessage(Colors.Rose + "Usage : /creplace <block id|name> <block id|name>");
					}
				}
				else{
					player.sendMessage(Colors.Rose + "No cuboid has been selected");
				}
				return true;
			}	
			
			/*
			else if (split[0].equalsIgnoreCase("/ccopy")){
				if (Cuboid.isReady(true)){
					Cuboid.enregisterCuboid();
					player.sendMessage(Colors.LightGreen + "Le cuboid a �t� enregistr�.");
				}
				else{
					player.sendMessage(Colors.Rose + "No cuboid has been selected");
				}
				return true;
			}
			
			else if (split[0].equalsIgnoreCase("/cpaste")){
				if (Cuboid.isReady(false)){
					Cuboid.poserCuboid();
					player.sendMessage(Colors.LightGreen + "Le cuboid a �t� reconstitu�.");
				}									
				else{
					player.sendMessage(Colors.Rose + "No cuboid has been selected");
				}
				return true;
			}			
			
			else if (split[0].equalsIgnoreCase("/ccircle")){
				if (Cuboid.isReady(false)){
					int taille = 0;
					if (split[1] != null){
						taille = Integer.parseInt( split[1] );
					}
					else{
						player.sendMessage(Colors.Rose + "Soucis avec la taille en param�tre.");
					}
				}
				return true;
			}*/
		}
	    return false;
	}

	public boolean onBlockCreate(Player player, Block blockPlaced, Block blockClicked, int itemInHand){	
		if ( itemInHand==269 && (etc.getInstance().canUseCommand(player.getName(), "/protect") || etc.getInstance().canUseCommand(player.getName(), "/cuboid")) ){
				boolean whichPoint = Cuboid.setPoint(player.getName(), blockClicked.getX(), blockClicked.getY(), blockClicked.getZ());
				player.sendMessage(Colors.Blue + ((!whichPoint) ? "First" : "Second")+ " point is set." );	
				return true;
		}
		else if ( itemInHand==268 ){
			String owners = ProtectedArea.inProtectedZone(blockClicked.getX(), blockClicked.getY(), blockClicked.getZ());
			if (owners != null)
				player.sendMessage(Colors.Yellow + "This area is owned by :"+Colors.White+owners);
			else
				player.sendMessage(Colors.Yellow + "This area belongs to nobody");
			return true;
		}
		else if ( (itemInHand>269 && itemInHand<280) || itemInHand==256|| itemInHand==257|| itemInHand==258|| itemInHand==290|| itemInHand==291|| itemInHand==292|| itemInHand==293 ){
			return false;
		}
		else{
			if ( ProtectedArea.toggle && !etc.getInstance().canIgnoreRestrictions(player.getName()) ){
				return isAllowed(player, blockClicked);
			}
		}
		return false;
	}

	public boolean onBlockDestroy(Player player, Block block) {
		int targetBlockType = block.getType();
		if ( targetBlockType == 64 || targetBlockType == 69 || targetBlockType == 77 ){
			// Wood doors, buttons and levers
			return false;
		}
		else if (ProtectedArea.toggle && !etc.getInstance().canIgnoreRestrictions(player.getName()) ){
			String playerName = player.getName();
			boolean inList = false;
			for (String p : playerList){
				if (p==playerName)
					inList = true;
			}
			
			if (inList){
				Block lastTouchedBlock = correspondingBloc.get(playerList.indexOf(playerName));
				if (lastTouchedBlock.getX() != block.getX() || lastTouchedBlock.getY() != block.getY() || lastTouchedBlock.getZ() != block.getZ() ){
					
					int indexInList = playerList.indexOf(playerName);
					correspondingBloc.set(indexInList, block);
					lastStatus.set(indexInList, isAllowed(player, block));
					
					return lastStatus.get(playerList.indexOf(playerName));
				}
				else{
					return lastStatus.get(playerList.indexOf(playerName));
				}
			}
			else{
				playerList.add(playerName);
				correspondingBloc.add(block);
				lastStatus.add(isAllowed(player, block));
				return lastStatus.get(playerList.indexOf(playerName));
			}
		}
		return false;
	}

	private boolean isAllowed(Player player, Block block){		
		String cuboidOwners = ProtectedArea.inProtectedZone(block);
		if ( cuboidOwners != null && cuboidOwners.indexOf(" "+player.getName().toLowerCase())==-1 ){
			
			int groupIndex = cuboidOwners.indexOf(" g:")+3;
			int endIndex = -1;
			String groupName ="";
			while ( groupIndex >= 3 ){
				endIndex = cuboidOwners.indexOf(" ", groupIndex);
				if (endIndex == -1)
					endIndex = cuboidOwners.length();
				groupName = cuboidOwners.substring(groupIndex, endIndex);
				
				for (String group : player.getUser().Groups){
					if (group.indexOf(groupName) != -1){
						return false;
					}
				}
				cuboidOwners = cuboidOwners.substring(0, groupIndex-3) + cuboidOwners.substring(endIndex, cuboidOwners.length());
				groupIndex = cuboidOwners.indexOf(" g:")+3;
			}
	
			player.sendMessage(Colors.Rose+"This block is protected !" );
			return true;
		}
		return false;
	}
	
	private boolean isValidBlockID(int blocID){
		boolean validity = true;
		
		if ( (blocID > 20 && blocID < 35) || blocID==36 ){
			validity = false;
		}
		
		return validity;
	}

}