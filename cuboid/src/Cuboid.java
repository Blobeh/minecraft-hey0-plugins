import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

public class Cuboid {
	// Version 14 : 11/11 15h30 GMT+1
	// for servermod 123-125+
	static Server server = etc.getServer();
		
	static ArrayList<String> playerList = new ArrayList<String>();
	static ArrayList<Boolean> selectionStatus = new ArrayList<Boolean>();
	static ArrayList<Boolean> undoAble = new ArrayList<Boolean>();
	static ArrayList<Integer> pointsCoordinates = new ArrayList<Integer>();
	static ArrayList<int[][][]> lastCopiedCuboid = new ArrayList<int[][][]>();
	static ArrayList<int[][][]> lastSelectedCuboid = new ArrayList<int[][][]>();
	static ArrayList<int[]> pastePoint = new ArrayList<int[]>();
	
	static Object lock = new Object();
	static int[] blocksToBeQueued = {37, 38, 39, 40, 50, 55, 63, 66, 69, 75, 76, 81, 83};
		
	private static int getPlayerIndex(String playerName){
		
		boolean inList = false;
		for (String p : playerList){
			if (p==playerName)
				inList = true;
		}
		
		if (!inList){
			playerList.add(playerName);
			selectionStatus.add(false);
			undoAble.add(false);
			pointsCoordinates.add(null);
			pointsCoordinates.add(null);
			pointsCoordinates.add(null);
			pointsCoordinates.add(null);
			pointsCoordinates.add(null);
			pointsCoordinates.add(null);
			lastCopiedCuboid.add(new int[][][]{});
			lastSelectedCuboid.add(null);
			pastePoint.add(new int[]{});
		}
				
		return playerList.indexOf(playerName);
	}
	
	public static boolean isUndoAble(String playerName) {
		return undoAble.get(getPlayerIndex(playerName));
	}
	
	public static boolean setPoint(String playerName, int X, int Y, int Z){
		
		int index = getPlayerIndex(playerName);
		boolean secondPoint = selectionStatus.get(index);
		if ( !secondPoint ){
			pointsCoordinates.set(index*6, X);
			pointsCoordinates.set(index*6+1, Y);
			pointsCoordinates.set(index*6+2, Z);
			pointsCoordinates.set(index*6+3, null);
			pointsCoordinates.set(index*6+4, null);
			pointsCoordinates.set(index*6+5, null);
			undoAble.set(index, false);
			pastePoint.set(index, new int[]{X, Y, Z});
			
		}
		else{
			pointsCoordinates.set(index*6+3, X);
			pointsCoordinates.set(index*6+4, Y);
			pointsCoordinates.set(index*6+5, Z);
			lastCopiedCuboid.set(index, new int[][][]{});
			lastSelectedCuboid.set(index, null);
			pastePoint.set(index, new int[]{});
		}
		selectionStatus.set(index, !secondPoint);
		return secondPoint;
	}
	
	public static int[] getPoint(String playerName, boolean secondPoint){
		int index = getPlayerIndex(playerName);
		int[] coords;
		if (!secondPoint){
			coords = new int[]{ pointsCoordinates.get(index*6) ,pointsCoordinates.get(index*6+1) ,pointsCoordinates.get(index*6+2) };
		}
		else{
			coords = new int[]{ pointsCoordinates.get(index*6+3) ,pointsCoordinates.get(index*6+4) ,pointsCoordinates.get(index*6+5) };
		}
		return coords;
	}
	
	public static int[] getCorners(int index, boolean twoCorners){
		int[] corners;
		if ( !twoCorners ){
			if ( pointsCoordinates.get(index*6+3)==null ){
				return new int[]{pointsCoordinates.get(index*6), pointsCoordinates.get(index*6+1), pointsCoordinates.get(index*6+2)};
			}
			corners = new int[3];
			corners[0] = ( pointsCoordinates.get(index*6) <= pointsCoordinates.get(index*6+3) ) ?
					pointsCoordinates.get(index*6) : pointsCoordinates.get(index*6+3);
			corners[1] = ( pointsCoordinates.get(index*6+1) <= pointsCoordinates.get(index*6+4) ) ?
					pointsCoordinates.get(index*6+1) : pointsCoordinates.get(index*6+4);
			corners[2] = ( pointsCoordinates.get(index*6+2) <= pointsCoordinates.get(index*6+5) ) ? 
					pointsCoordinates.get(index*6+2) : pointsCoordinates.get(index*6+5);
		}
		else{
			corners = new int[6];
			corners[0] = ( pointsCoordinates.get(index*6) <= pointsCoordinates.get(index*6+3) ) ?
					pointsCoordinates.get(index*6) : pointsCoordinates.get(index*6+3);
			corners[1] = ( pointsCoordinates.get(index*6+1) <= pointsCoordinates.get(index*6+4) ) ?
					pointsCoordinates.get(index*6+1) : pointsCoordinates.get(index*6+4);
			corners[2] = ( pointsCoordinates.get(index*6+2) <= pointsCoordinates.get(index*6+5) ) ? 
					pointsCoordinates.get(index*6+2) : pointsCoordinates.get(index*6+5);
			corners[3] = ( pointsCoordinates.get(index*6) <= pointsCoordinates.get(index*6+3)  ) ?
					pointsCoordinates.get(index*6+3) : pointsCoordinates.get(index*6);
			corners[4] = ( pointsCoordinates.get(index*6+1) <= pointsCoordinates.get(index*6+4) ) ?
					pointsCoordinates.get(index*6+4) : pointsCoordinates.get(index*6+1);
			corners[5] = ( pointsCoordinates.get(index*6+2) <= pointsCoordinates.get(index*6+5) ) ?
					pointsCoordinates.get(index*6+5) : pointsCoordinates.get(index*6+2);
		}
		return corners;
	}
	
	public static boolean isReady(String playerName, boolean deuxPoints){
		int index = getPlayerIndex(playerName);
		
		if ( deuxPoints && pointsCoordinates.get(index*6)!=null && pointsCoordinates.get(index*6+1)!=null
				&& pointsCoordinates.get(index*6+2)!=null && pointsCoordinates.get(index*6+3)!=null
				&& pointsCoordinates.get(index*6+4)!=null && pointsCoordinates.get(index*6+5)!=null ){
			selectionStatus.set(index, false);
			return true;
		}
		else if( !deuxPoints && pointsCoordinates.get(index*6)!=null && pointsCoordinates.get(index*6+1)!=null
				&& pointsCoordinates.get(index*6+2)!=null) {
			selectionStatus.set(index, false);
			return true;
		}
		return false;
	}
	
	public static void copyCuboid(String playerName, boolean manual){
		int index = getPlayerIndex(playerName);
		int[] corners = getCorners(index, true);
		int Xsize = corners[3]-corners[0]+1;
		int Ysize = corners[4]-corners[1]+1;
		int Zsize = corners[5]-corners[2]+1;
		// De Nord-Est vers Sud-Ouest
		
		int[][][] tableaux = new int[Xsize][][];
		for (int i = 0; i<Xsize; i++){
			tableaux[i] = new int[Ysize][];
			for (int j = 0; j < Ysize; ++j) {
				tableaux[i][j] = new int[Zsize];
				 for (int k = 0; k < Zsize; ++k)
					 tableaux[i][j][k] = server.getBlockIdAt( corners[0]+i,corners[1]+j,corners[2]+k );
			}
		}
		
		lastCopiedCuboid.set(index, tableaux);
		pastePoint.set(index, new int[]{corners[0], corners[1], corners[2]});
		if (!manual){
			undoAble.set(index, true);
		}
	}
	
	private static void copyCuboid(String playerName, int Xmin, int Xmax, int Ymin, int Ymax, int Zmin, int Zmax){
		int index = getPlayerIndex(playerName);
		int Xsize = Math.abs(Xmax-Xmin+1);
		int Ysize = Math.abs(Ymax-Ymin+1);
		int Zsize = Math.abs(Zmax-Zmin+1);
		
		int[][][] tableaux = new int[Xsize][][];
		for (int i = 0; i<Xsize; i++){
			tableaux[i] = new int[Ysize][];
			for (int j = 0; j < Ysize; ++j) {
				tableaux[i][j] = new int[Zsize];
				 for (int k = 0; k < Zsize; ++k)
					 tableaux[i][j][k] = server.getBlockIdAt( Xmin+i,Ymin+j,Zmin+k );
			}
		}
		lastCopiedCuboid.set(index, tableaux);
		pastePoint.set(index, new int[]{Xmin, Ymin, Zmin});
		undoAble.set(index, true);
	}
			
	private static boolean shoudBeQueued(int blockType) {
		for (int shoudBeQueued : blocksToBeQueued ){
			if ( blockType == shoudBeQueued )
				return true;
		}
		return false;
	}
	
	public static byte paste(String playerName){
		synchronized(lock){
			int index = getPlayerIndex(playerName);
			int[] startPoint = pastePoint.get(index);
	
			int[][][] toPaste = lastCopiedCuboid.get(index);
			int Xsize = toPaste.length;
			if (Xsize==0){
				return 1;
			}
			int Ysize = toPaste[0].length;
			int Zsize = toPaste[0][0].length;
			// De Nord-Est vers Sud-Ouest
					
			int[][][] beforePaste = new int[Xsize][][];
			
			int curX, curY, curZ;
			HashMap<int[], Integer> queuedBlocks = new HashMap<int[], Integer>();
			
			for (int i = 0; i<Xsize; i++){
				beforePaste[i] = new int[Ysize][];
				for (int j = 0; j < Ysize; ++j) {
					beforePaste[i][j] = new int[Zsize];
					 for (int k = 0; k < Zsize; ++k){
						 curX = startPoint[0]+i;
						 curY = startPoint[1]+j;
						 curZ = startPoint[2]+k;
						 beforePaste[i][j][k] = server.getBlockIdAt(curX,curY,curZ);
						 if ( shoudBeQueued(toPaste[i][j][k]) ){
							 queuedBlocks.put( new int[]{ curX, curY, curZ}, toPaste[i][j][k]);
						 }
						 else{
							 server.setBlockAt( toPaste[i][j][k], curX, curY, curZ );
						 }
					 }
				}
			}
			
			for ( Entry<int[], Integer> queuedBlock : queuedBlocks.entrySet() ){
				server.setBlockAt( queuedBlock.getValue(), queuedBlock.getKey()[0], queuedBlock.getKey()[1], queuedBlock.getKey()[2] );
			}
			
			lastSelectedCuboid.set(index, beforePaste);
			undoAble.set(index, true);
			
			return 0;
		}
	}

	public static byte undo(String playerName){
		synchronized(lock){
			int index = getPlayerIndex(playerName);
			int[] startPoint = pastePoint.get(index);
			int[][][] toPaste;
	
			if (lastSelectedCuboid.get(index) != null){
				toPaste = lastSelectedCuboid.get(index);
			}
			else{
				toPaste = lastCopiedCuboid.get(index);
			}
			
			int Xsize = toPaste.length;
			if (Xsize==0){
				return 1;
			}
			int Ysize = toPaste[0].length;
			int Zsize = toPaste[0][0].length;

			for (int i = 0; i<Xsize; i++){
				for (int j = 0; j < Ysize; ++j) {
					 for (int k = 0; k < Zsize; ++k){
						 server.setBlockAt( toPaste[i][j][k], startPoint[0]+i,startPoint[1]+j,startPoint[2]+k );
					 }
				}
			}
			
			lastSelectedCuboid.set(index, null);
			undoAble.set(index, false);
			
			return 0;
		}
	}
	
	public static byte saveCuboid(String playerName, String cuboidName){
		int index = getPlayerIndex(playerName);
		int[] corners = getCorners(index, true);
		int Xsize = corners[3]-corners[0]+1;
		int Ysize = corners[4]-corners[1]+1;
		int Zsize = corners[5]-corners[2]+1;
		// De Nord-Est vers Sud-Ouest
		
		int[][][] tableaux = new int[Xsize][][];
		for (int i = 0; i<Xsize; i++){
			tableaux[i] = new int[Ysize][];
			for (int j = 0; j < Ysize; ++j) {
				tableaux[i][j] = new int[Zsize];
				 for (int k = 0; k < Zsize; ++k)
					 tableaux[i][j][k] = server.getBlockIdAt( corners[0]+i,corners[1]+j,corners[2]+k );
			}
		}
		return new CuboidData(playerName, cuboidName, tableaux).save();	
	}
	
	public static byte loadCuboid(String playerName, String cuboidName){
		synchronized(lock){
			int index = getPlayerIndex(playerName);
			int[] corners = getCorners(index, false);
			
			CuboidData cuboid = new CuboidData(playerName, cuboidName);
			
			if (cuboid.loadReturnCode == 0){
				int[][][] tableau = cuboid.getData();
				int Xsize = tableau.length;
				int Ysize = tableau[0].length;
				int Zsize = tableau[0][0].length;
				// De Nord-Est vers Sud-Ouest
				
				copyCuboid(playerName, corners[0], corners[0]+Xsize, corners[1], corners[1]+Ysize, corners[2], corners[2]+Zsize);
					
				for (int i = 0; i<Xsize; i++){
					for (int j = 0; j < Ysize; ++j) {
						 for (int k = 0; k < Zsize; ++k)
							 server.setBlockAt( tableau[i][j][k], corners[0]+i,corners[1]+j,corners[2]+k );
					}
				}
	
			}
			
			return cuboid.loadReturnCode;
		}
	}
	
	public static int blocksCount(String playerName){
		int index = getPlayerIndex(playerName);
		
		int Xsize = Math.abs(pointsCoordinates.get(index*6+3)-pointsCoordinates.get(index*6))+1;
		int Ysize = Math.abs(pointsCoordinates.get(index*6+4)-pointsCoordinates.get(index*6+1))+1;
		int Zsize = Math.abs(pointsCoordinates.get(index*6+5)-pointsCoordinates.get(index*6+2))+1;
		return Xsize*Ysize*Zsize;
	}
	
	public static void emptyCuboid(String playerName){
		synchronized(lock){
			int index = getPlayerIndex(playerName);
			int[] corners = getCorners(index, true);
			
			copyCuboid(playerName, false);
			
			for ( int i = corners[0]; i<= corners[3]; i++ ){
				for ( int j = corners[1]; j<= corners[4]; j++ ){
					for ( int k = corners[2]; k<= corners[5]; k++ ){
						server.setBlockAt(20,i,j,k);
						server.setBlockAt(0,i,j,k);
					}
				}
			}
			if (CuboidPlugin.logging)
				CuboidPlugin.log.info(playerName+" emptied a cuboid");
		}
	}
	
	public static void fillCuboid(String playerName, int bloctype){
		synchronized(lock){
			int index = getPlayerIndex(playerName);
			int[] corners = getCorners(index, true);
			
			copyCuboid(playerName, false);
			
			for ( int i = corners[0]; i<= corners[3]; i++ ){
				for ( int j = corners[1]; j<= corners[4]; j++ ){
					for ( int k = corners[2]; k<= corners[5]; k++ ){
						server.setBlockAt(bloctype,i,j,k);
					}
				}
			}
			if (CuboidPlugin.logging)
				CuboidPlugin.log.info(playerName+" filled a cuboid");
		}
	}
	
	public static void replaceBlocks(String playerName, int[] replaceParams){
		synchronized(lock){
			int index = getPlayerIndex(playerName);	
			int[] corners = getCorners(index, true);
			
			copyCuboid(playerName, false);
	
			int targetBlockIndex = replaceParams.length-1;
			for ( int i = corners[0]; i<= corners[3]; i++ ){
				for ( int j = corners[1]; j<= corners[4]; j++ ){
					for ( int k = corners[2]; k<= corners[5]; k++ ){
						for ( int l = 0; l < targetBlockIndex; l++ ){
							if( server.getBlockIdAt(i, j, k) == replaceParams[l] ){
								server.setBlockAt(replaceParams[targetBlockIndex],i,j,k);
							}
						}
					}
				}
			}
			if (CuboidPlugin.logging)
				CuboidPlugin.log.info(playerName+" replaced blocks inside a cuboid");
		}
	}
	
	public static void buildCuboidFaces(String playerName, int bloctype, boolean sixFaces){
		synchronized(lock){
			int index = getPlayerIndex(playerName);
			int[] corners = getCorners(index, true);
			
			copyCuboid(playerName, false);
			
			for ( int i = corners[0]; i<= corners[3]; i++ ){
				for ( int j = corners[1]; j<= corners[4]; j++ ){
					server.setBlockAt(bloctype,i,j,corners[2]);
					server.setBlockAt(bloctype,i,j,corners[5]);
				}
			}		
			for ( int i = corners[1]; i<= corners[4]; i++ ){
				for ( int j = corners[2]; j<= corners[5]; j++ ){
					server.setBlockAt(bloctype,corners[0],i,j);
					server.setBlockAt(bloctype,corners[3],i,j);
				}
			}
			if (sixFaces){
				for ( int i = corners[0]; i<= corners[3]; i++ ){
					for ( int j = corners[2]; j<= corners[5]; j++ ){
						server.setBlockAt(bloctype,i,corners[1],j);
						server.setBlockAt(bloctype,i,corners[4],j);
					}
				}
			}
			if (CuboidPlugin.logging)
				CuboidPlugin.log.info(playerName+" built the "+((sixFaces)? "faces" : "walls")+" of a cuboid");
		}
	}
	
	public static void rotateCuboidContent(String playerName, int rotationType){
		synchronized(lock){
			int index = getPlayerIndex(playerName);
			int[] corners = getCorners(index, true);
			copyCuboid(playerName, false);
			int[][][] cuboidContent = lastCopiedCuboid.get(index);
			
			if ( rotationType == 0){	// 90� clockwise
				for ( int i = corners[0]; i<= corners[3]; i++ ){
					for ( int j = corners[1]; j<= corners[4]; j++ ){
						for ( int k = corners[2]; k<= corners[5]; k++ ){
							server.setBlockAt(cuboidContent[i][j][k], i, j, k);
						}
					}
				}
			}
			if ( rotationType == 1){	// 90� counet-clockwise
				
			}
			if ( rotationType == 2){	//	180�
				
			}
			if ( rotationType == 3){	// upside-down
							
			}
			
			// TODO
			
			if (CuboidPlugin.logging)
				CuboidPlugin.log.info(playerName+"");
		}
	}
	
	public static void moveCuboidContent(Player player, String movementType, int value){
		synchronized(lock){
			String playerName = player.getName();
			int index = getPlayerIndex(playerName);
			int[] corners = getCorners(index, true);
			copyCuboid(playerName, false);
			int[][][] cuboidContent = lastCopiedCuboid.get(index);
						
			if( movementType.equalsIgnoreCase("North") ){
				copyCuboid(playerName, corners[0], corners[3], corners[1], corners[4], corners[2]-value, corners[5]);
				int deleteIterator = 0;
				for ( int k = corners[5]; k>= corners[2]; k-- ){
					for ( int i = corners[0]; i<= corners[3]; i++ ){
						for ( int j = corners[1]; j<= corners[4]; j++ ){
							server.setBlockAt(cuboidContent[i-corners[0]][j-corners[1]][k-corners[2]], i, j, k-value);
							if ( deleteIterator<value ){
								server.setBlockAt(0, i, j, k);	
							}
						}
					}
					deleteIterator++;
				}
			}
			else if( movementType.equalsIgnoreCase("East") ){
				copyCuboid(playerName, corners[0]-value, corners[3], corners[1], corners[4], corners[2], corners[5]);
				int deleteIterator = 0;
				for ( int i = corners[3]; i>= corners[0]; i-- ){
					for ( int j = corners[1]; j<= corners[4]; j++ ){
						for ( int k = corners[2]; k<= corners[5]; k++ ){
							server.setBlockAt(cuboidContent[i-corners[0]][j-corners[1]][k-corners[2]], i-value, j, k);
							if ( deleteIterator<value ){
								server.setBlockAt(0, i, j, k);	
							}
						}
					}
					deleteIterator++;
				}
			}
			else if( movementType.equalsIgnoreCase("South") ){
				copyCuboid(playerName, corners[0], corners[3]+value, corners[1], corners[4], corners[2], corners[5]);
				int deleteIterator = 0;
				for ( int i = corners[0]; i<= corners[3]; i++ ){
					for ( int j = corners[1]; j<= corners[4]; j++ ){
						for ( int k = corners[2]; k<= corners[5]; k++ ){
							server.setBlockAt(cuboidContent[i-corners[0]][j-corners[1]][k-corners[2]], i+value, j, k);
							if ( deleteIterator<value ){
								server.setBlockAt(0, i, j, k);	
							}
						}
					}
					deleteIterator++;
				}
			}
			else if( movementType.equalsIgnoreCase("West") ){
				copyCuboid(playerName, corners[0], corners[3], corners[1], corners[4], corners[2], corners[5]+value);
				int deleteIterator = 0;
				for ( int k = corners[2]; k<= corners[5]; k++ ){
					for ( int i = corners[0]; i<= corners[3]; i++ ){
						for ( int j = corners[1]; j<= corners[4]; j++ ){
							server.setBlockAt(cuboidContent[i-corners[0]][j-corners[1]][k-corners[2]], i, j, k+value);
							if ( deleteIterator<value ){
								server.setBlockAt(0, i, j, k);	
							}
						}
					}
					deleteIterator++;
				}
			}
			else if( movementType.equalsIgnoreCase("Up") ){
				copyCuboid(playerName, corners[0], corners[3], corners[1], corners[4]+value, corners[2], corners[5]);
				int deleteIterator = 0;
				for ( int j = corners[1]; j<= corners[4]; j++ ){
					for ( int i = corners[0]; i<= corners[3]; i++ ){
						for ( int k = corners[2]; k<= corners[5]; k++ ){
							server.setBlockAt(cuboidContent[i-corners[0]][j-corners[1]][k-corners[2]], i, j+value, k);
							if ( deleteIterator<value ){
								server.setBlockAt(0, i, j, k);	
							}
						}
					}
					deleteIterator++;
				}
			}
			else if( movementType.equalsIgnoreCase("Down") ){
				copyCuboid(playerName, corners[0], corners[3], corners[1]-value, corners[4], corners[2], corners[5]);
				int deleteIterator = 0;
				for ( int j = corners[4]; j>= corners[1]; j-- ){
					for ( int i = corners[0]; i<= corners[3]; i++ ){
						for ( int k = corners[2]; k<= corners[5]; k++ ){
							server.setBlockAt(cuboidContent[i-corners[0]][j-corners[1]][k-corners[2]], i, j-value, k);
							if ( deleteIterator<value ){
								server.setBlockAt(0, i, j, k);	
							}
						}
					}
					deleteIterator++;
				}
			}
			else{
				player.sendMessage(Colors.Rose + "Wrong parameter : " + movementType);
				return;
			}
			
			player.sendMessage(Colors.LightGreen + "Cuboid successfuly moved.");
			if (CuboidPlugin.logging)
				CuboidPlugin.log.info(playerName+" moved a cuboid : "+value+" block(s) " + movementType);
		}
	}
	
	public static void buildCircle(String playerName,int radius, int blocktype, int height, boolean fill){
		
		synchronized(lock){
		int index = getPlayerIndex(playerName);
		int Xcenter = pointsCoordinates.get(index*6);
		int Ycenter = pointsCoordinates.get(index*6+1);
		int Zcenter = pointsCoordinates.get(index*6+2);
		int Xmin=Xcenter-radius;
		int Xmax=Xcenter+radius;
		int Zmin=Zcenter-radius;
		int Zmax=Zcenter+radius;
		int Ymin = (height+Ycenter >= Ycenter) ? Ycenter : height+Ycenter;
		int Ymax = (height+Ycenter <= Ycenter) ? Ycenter : height+Ycenter;
		
		copyCuboid(playerName, Xmin, Xmax, Ymin, Ymax, Zmin, Zmax);

		for ( int i = Xmin; i<= Xmax; i++ ){
			for ( int j = Ymin; j<= Ymax; j++ ){
				for (int k = Zmin; k <=Zmax ; k++){
				    double diff = Math.sqrt( Math.pow(i-Xcenter, 2.0D) + Math.pow(k-Zcenter, 2.0D) );
				    if( diff<radius+0.5 && ( fill || (!fill && diff>radius-0.5) ) ){
				    	server.setBlockAt(blocktype,i,j,k);
				    }
				}
			}
		}
		if (CuboidPlugin.logging)
			CuboidPlugin.log.info(playerName+" built a "+((height!=0)? "cylinder" : "circle") );
		}
		
	}
	
	public static void buildShpere(String playerName,int radius, int blocktype, boolean fill){
		
		int index = getPlayerIndex(playerName);
		int Xcenter = pointsCoordinates.get(index*6);
		int Ycenter = pointsCoordinates.get(index*6+1);
		int Zcenter = pointsCoordinates.get(index*6+2);
		int Xmin=Xcenter-radius;
		int Xmax=Xcenter+radius;
		int Ymin=Ycenter-radius;
		int Ymax=Ycenter+radius;
		int Zmin=Zcenter-radius;
		int Zmax=Zcenter+radius;
		
		copyCuboid(playerName, Xmin, Xmax, Ymin, Ymax, Zmin, Zmax);
		
		for ( int i = Xmin; i<= Xmax; i++ ){
			for ( int j = Ymin; j<= Ymax; j++ ){
				for (int k = Zmin; k <=Zmax ; k++){
				    double diff = Math.sqrt( Math.pow(i-Xcenter, 2.0D) + Math.pow(j-Ycenter, 2.0D) + Math.pow(k-Zcenter, 2.0D) );
				    if( diff<radius+0.5 && (fill || (!fill && diff>radius-0.5) ) ){
				    	server.setBlockAt(blocktype,i,j,k);
				    }
				}
			}
		}
		if (CuboidPlugin.logging)
			CuboidPlugin.log.info(playerName+" built a "+((fill)? "ball" : "sphere") );
	}
	
	public static void buildPyramid(String playerName, int radius, int blockType, boolean fill){
		
		int index = getPlayerIndex(playerName);
		int Xcenter = pointsCoordinates.get(index*6);
		int Ycenter = pointsCoordinates.get(index*6+1);
		int Zcenter = pointsCoordinates.get(index*6+2);
		int Xmin = Xcenter-radius;
		int Xmax = Xcenter+radius;
		int Zmin = Zcenter-radius;
		int Zmax = Zcenter+radius;
		int Ymin = Ycenter;
		int Ymax = Ycenter+radius;
		
		copyCuboid(playerName, Xmin, Xmax, Ymin, Ymax, Zmin, Zmax);

		for (int j = Ymin; j <= Ymax; j++){
			for (int i = Xmin; i <= Xmax; i++){
				for (int k = Zmin; k <= Zmax; k++){
					server.setBlockAt(blockType, i, j, k);
				}
			}
			Xmin += 1;
			Xmax -= 1;
			Zmin += 1;
			Zmax -= 1;
		}
		
		if (!fill && radius > 2){	// easy, but destructive way
			Xmin = Xcenter-radius+2;
			Xmax = Xcenter+radius-2;
			Zmin = Zcenter-radius+2;
			Zmax = Zcenter+radius-2;
			Ymin = Ycenter+1;
			Ymax = Ycenter+radius-1;
			for (int j = Ymin; j <= Ymax; j++){
				for (int i = Xmin; i <= Xmax; i++){
					for (int k = Zmin; k <= Zmax; k++){
						server.setBlockAt(0, i, j, k);
					}
				}
				Xmin += 1;
				Xmax -= 1;
				Zmin += 1;
				Zmax -= 1;
			}
		}
		
		if (CuboidPlugin.logging)
			CuboidPlugin.log.info(playerName+" built a "+((fill)? "filled " : "") + "pyramid." );		
	}
	
	public static void updateChestsState(int firstX, int firstY, int firstZ, int secondX, int secondY, int secondZ){
		synchronized(lock){
		int startX = ( firstX <= secondX ) ? firstX : secondX;
		int startY = ( firstY <= secondY ) ? firstY : secondY;
		int startZ = ( firstZ <= secondZ ) ? firstZ : secondZ;
		
		int endX = ( firstX <= secondX  ) ? secondX : firstX;
		int endY = ( firstY <= secondY ) ? secondY : firstY;
		int endZ = ( firstZ <= secondZ ) ? secondZ : firstZ;
		
		for ( int i = startX; i<= endX; i++ ){
			for ( int j = startY; j<= endY; j++ ){
				for ( int k = startZ; k<= endZ; k++ ){
					if ( server.getBlockIdAt(i, j, k)==54 && server.getComplexBlock(i, j, k)!=null ){
						server.getComplexBlock(i, j, k).update();
					}
					
				}
			}
		}
		}
	}

}
