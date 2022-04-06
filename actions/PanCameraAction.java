package actions;

import a3.MyGame;

import tage.*;
import tage.input.action.AbstractInputAction;
import net.java.games.input.Event;
import org.joml.*;

public class PanCameraAction extends AbstractInputAction
{
	private MyGame game;
	private Vector3f oldPosition, newPosition;
	private Vector4f bwdDirection, fwdDirection;

	private Camera c;
	private Vector3f upVector, rightVector, fwdVector;

	public PanCameraAction(MyGame g){
		game = g;
	}

	@Override
	public void performAction(float time, Event e){
		System.out.println(e);
		if(e.getComponent().toString().equals("T")){ // Move Up
			c = (game.getEngine().getRenderSystem()).getViewport("MAIN").getCamera();
			upVector = c.getV();
			oldPosition = c.getLocation();
			upVector.mul(0.05f);
			newPosition = oldPosition.add(upVector.x(), upVector.y(), upVector.z());
			c.setLocation(newPosition);
		}
		else if(e.getComponent().toString().equals("G")){ // Move Down
			c = (game.getEngine().getRenderSystem()).getViewport("MAIN").getCamera();
			upVector = c.getV();
			oldPosition = c.getLocation();
			upVector.mul(-0.05f);
			newPosition = oldPosition.add(upVector.x(), upVector.y(), upVector.z());
			c.setLocation(newPosition);
		}
		else if(e.getComponent().toString().equals("F")){ // Move Left
			c = (game.getEngine().getRenderSystem()).getViewport("MAIN").getCamera();
			rightVector = c.getU();
			oldPosition = c.getLocation();
			rightVector.mul(-0.05f);
			newPosition = oldPosition.add(rightVector.x(), rightVector.y(), rightVector.z());
			c.setLocation(newPosition);
		}
		else if(e.getComponent().toString().equals("H")){ // Move Right
			c = (game.getEngine().getRenderSystem()).getViewport("MAIN").getCamera();
			rightVector = c.getU();
			oldPosition = c.getLocation();
			rightVector.mul(0.05f);
			newPosition = oldPosition.add(rightVector.x(), rightVector.y(), rightVector.z());
			c.setLocation(newPosition);
		}
	}
}


