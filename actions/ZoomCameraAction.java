package actions;

import a3.MyGame;

import tage.*;
import tage.input.action.AbstractInputAction;
import net.java.games.input.Event;
import org.joml.*;

public class ZoomCameraAction extends AbstractInputAction
{
	private MyGame game;
	private Vector3f oldPosition, newPosition;
	private Vector4f bwdDirection, fwdDirection;

	private Camera c;
	private Vector3f upVector, rightVector, fwdVector;

	public ZoomCameraAction(MyGame g){
		game = g;
	}

	@Override
	public void performAction(float time, Event e){
		if (e.getValue() > -.2 && e.getValue() < .2){
			return;  // deadzone
		}else if(e.getComponent().toString().equals("C")){ // Zoom In
			c = (game.getEngine().getRenderSystem()).getViewport("RIGHT").getCamera();
			fwdVector = c.getN();
			oldPosition = c.getLocation();
			fwdVector.mul(0.01f);
			newPosition = oldPosition.add(fwdVector.x(), fwdVector.y(), fwdVector.z());
			c.setLocation(newPosition);
		}else if(e.getComponent().toString().equals("V")){ // Zoom Out
			c = (game.getEngine().getRenderSystem()).getViewport("RIGHT").getCamera();
			fwdVector = c.getN();
			oldPosition = c.getLocation();
			fwdVector.mul(-0.01f);
			newPosition = oldPosition.add(fwdVector.x(), fwdVector.y(), fwdVector.z());
			c.setLocation(newPosition);
		}
	}
}


