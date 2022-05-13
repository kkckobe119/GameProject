package actions;

//import a1.MyGame;
import client.*;
import tage.*;
import tage.input.action.AbstractInputAction;
import net.java.games.input.Event;
import org.joml.*;

import a3.MyGame;

public class TurnAction extends AbstractInputAction
{
	private MyGame game;
	private GameObject av;
	private Vector4f oldUp;
	private Matrix4f rotAroundAvatarUp, oldRotation, newRotation;
	private ProtocolClient protClient;

	//private MyGame game;
	private Camera c;
	private Vector3f upVector, rightVector, fwdVector;

	public TurnAction(MyGame g, ProtocolClient p){
		game = g;
		protClient = p;
	}

	@Override
	public void performAction(float time, Event e){
		float keyValue = e.getValue();
		av = game.getAvatar();
		if (keyValue > -.2 && keyValue < .2){
			return;  // deadzone
		}else if(e.getComponent().toString().equals("A") || e.getValue() < -0.2){
				oldRotation = new Matrix4f(av.getWorldRotation());
				oldUp = new Vector4f(0f,1f,0f,1f).mul(oldRotation);
				rotAroundAvatarUp = new Matrix4f().rotation(0.05f, new Vector3f(oldUp.x(), oldUp.y(), oldUp.z()));
				newRotation = oldRotation;
				newRotation.mul(rotAroundAvatarUp);
				av.setLocalRotation(newRotation);
		}else if(e.getComponent().toString().equals("D") || e.getValue() > 0.2){
				oldRotation = new Matrix4f(av.getWorldRotation());
				oldUp = new Vector4f(0f,1f,0f,1f).mul(oldRotation);
				rotAroundAvatarUp = new Matrix4f().rotation(-0.05f, new Vector3f(oldUp.x(), oldUp.y(), oldUp.z()));
				newRotation = oldRotation;
				newRotation.mul(rotAroundAvatarUp);
				av.setLocalRotation(newRotation);
		}
		//protClient.sendMoveMessage(av.getLocalRotation());
	}
}


