package actions;

import client.*;
import tage.*;
import tage.input.action.AbstractInputAction;
import net.java.games.input.Event;
import org.joml.*;

import a3.MyGame;

public class MoveAction extends AbstractInputAction
{
	private MyGame game;
	private GameObject av;
	private Vector3f oldPosition, newPosition;
	private Vector4f bwdDirection, fwdDirection;
	private ProtocolClient protClient;

	private Camera c;
	private Vector3f upVector, rightVector, fwdVector;

	public MoveAction(MyGame g, ProtocolClient p){
		game = g;
		protClient = p;
	}

	@Override
	public void performAction(float time, Event e){
		//System.out.println(e);
		av = game.getAvatar();
		if (e.getValue() > -.2 && e.getValue() < .2){
			return;  // deadzone
		}else if(e.getComponent().toString().equals("W") || e.getValue() < -0.2){
				//av = game.getAvatar();
				oldPosition = av.getWorldLocation();
				fwdDirection = new Vector4f(0f,0f,1f,1f);
				fwdDirection.mul(av.getWorldRotation());
				fwdDirection.mul(0.5f);
				newPosition = oldPosition.add(fwdDirection.x(), fwdDirection.y(), fwdDirection.z());
				av.setLocalLocation(newPosition);
		}else if(e.getComponent().toString().equals("S") || e.getValue() > 0.2){
				oldPosition = av.getWorldLocation();
				bwdDirection = new Vector4f(0f,0f,1f,1f);
				bwdDirection.mul(av.getWorldRotation());
				bwdDirection.mul(-0.5f);
				newPosition = oldPosition.add(bwdDirection.x(), bwdDirection.y(), bwdDirection.z());
				av.setLocalLocation(newPosition);
		}
		game.stopWalk(); 
    	game.playWalk(); 
		protClient.sendMoveMessage(av.getWorldLocation());
		//protClient.sendMoveMessage(av.getLocalRotation());
	}
}


