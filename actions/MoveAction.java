package actions;

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

	private Camera c;
	private Vector3f upVector, rightVector, fwdVector;

	public MoveAction(MyGame g){
		game = g;
	}

	@Override
	public void performAction(float time, Event e){
		if(e.getComponent().toString().equals("W") || e.getValue() < -0.2){
				av = game.getAvatar();
				oldPosition = av.getWorldLocation();
				fwdDirection = new Vector4f(0f,0f,1f,1f);
				fwdDirection.mul(av.getWorldRotation());
				fwdDirection.mul(0.05f);
				newPosition = oldPosition.add(fwdDirection.x(), fwdDirection.y(), fwdDirection.z());
				av.setLocalLocation(newPosition);
		}else if(e.getComponent().toString().equals("S") || e.getValue() > 0.2){
				av = game.getAvatar();
				oldPosition = av.getWorldLocation();
				bwdDirection = new Vector4f(0f,0f,1f,1f);
				bwdDirection.mul(av.getWorldRotation());
				bwdDirection.mul(-0.05f);
				newPosition = oldPosition.add(bwdDirection.x(), bwdDirection.y(), bwdDirection.z());
				av.setLocalLocation(newPosition);
		}
	}
}

