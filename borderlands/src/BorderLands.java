public class BorderLands extends SuperPlugin
{
  public final BorderLands.BorderListener listener = new BorderLands.BorderListener();
  private String unfortunateQuote;
  private String teleportQuote;
  private String borderQuote;
  private double distanceToBorder;
  private boolean useRadiusOnly;

  public BorderLands()
  {
    super("borderLands");
  }
  public void initializeExtra() {
    etc.getLoader().addListener(PluginLoader.Hook.PLAYER_MOVE, this.listener, this, PluginListener.Priority.HIGH);
    etc.getLoader().addListener(PluginLoader.Hook.TELEPORT, this.listener, this, PluginListener.Priority.HIGH);
  }

  public void reloadExtra() {
    this.distanceToBorder = this.config.getInt("distanceToBorder", 500);
    this.useRadiusOnly = this.config.getBoolean("useRadiusOnly", false);
	this.unfortunateQuote = this.config.getString("unfortunateQuote", "Somehow you were out of bounds, and thus returned to spawn.");
    this.teleportQuote = this.config.getString("teleportQuote", "You cannot teleport outside the map borders.");
    this.borderQuote = this.config.getString("borderQuote", "You have reached the border.");
	
	try {
		this.distanceToBorder =  etc.getInstance().getLimits()[2] - 16; // 1 chunk from map soft limit
	}
	catch (Exception e) {
		// do nothing
	}
  }

  private double getDistanceFromOrigin(Location a) {
	int[] mapLimits = new int[] {0,0,0};
	mapLimits = etc.getInstance().getLimits();
    if (!this.useRadiusOnly)
      return Math.max(Math.abs(a.x - mapLimits[0]), Math.abs(a.z - mapLimits[1]));
    return Math.sqrt(Math.pow(a.x - mapLimits[0], 2.0D) + Math.pow(a.z - mapLimits[1], 2.0D));
  }

  private class BorderListener extends PluginListener
  {
    private BorderListener()
    {
    }

    public void onPlayerMove(Player player, Location from, Location to)
    {
	  if (BorderLands.this.getDistanceFromOrigin(to) >= BorderLands.this.distanceToBorder)
        if (BorderLands.this.getDistanceFromOrigin(from) >= BorderLands.this.distanceToBorder) {
          Location spawn = etc.getServer().getSpawnLocation();
          player.sendMessage("§4" + BorderLands.this.unfortunateQuote);
          player.teleportTo(spawn);
        } else {
          player.sendMessage("§e" + BorderLands.this.borderQuote);
          player.teleportTo(from);
        }
    }

    public boolean onTeleport(Player player, Location from, Location to)
    {
      if (BorderLands.this.getDistanceFromOrigin(to) >= BorderLands.this.distanceToBorder) {
        if (BorderLands.this.getDistanceFromOrigin(from) >= BorderLands.this.distanceToBorder) {
          Location spawn = etc.getServer().getSpawnLocation();
          player.sendMessage("§4" + BorderLands.this.unfortunateQuote);
          player.teleportTo(spawn);
        } else {
          player.sendMessage("§e" + BorderLands.this.teleportQuote);
        }
        return true;
      }
      return false;
    }
  }
}