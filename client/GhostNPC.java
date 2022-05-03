package client;

import tage.*;
import org.joml.*;

public class GhostNPC extends GameObject
{
	private int id;
	
	public GhostNPC(int id, ObjShape s, TextureImage t, Vector3f p) 
	{	super(GameObject.root(), s, t);
		this.id = id;
		setPosition(p);
	}
	
	public void setSize(boolean big)
	{	if (!big) { this.setLocalScale((new Matrix4f()).scaling(0.5f)); }
		else { this.setLocalScale((new Matrix4f()).scaling(1.0f)); }
	}
	
	public int getID() { return id; }
	public void setPosition(Vector3f p) { this.setLocalLocation(p); }
	public Vector3f getPosition() { return this.getWorldLocation(); }
}
