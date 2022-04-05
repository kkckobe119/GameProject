package a3;

import tage.*;
import tage.input.action.AbstractInputAction;
import net.java.games.input.Event;
import org.joml.*;

public class FwdAction extends AbstractInputAction
{
	private MyGame game;
	private GameObject av;
	private Vector3f oldPosition, newPosition;
	private Vector4f fwdDirection;
	private ProtocolClient protClient;
	private int count = 0;
    private float speed;

	public FwdAction(MyGame g, ProtocolClient p)
	{	game = g;
		protClient = p;
	}


	public void performAction(float time, Event e) 
    {   
		av = game.getAvatar();
        float keyValue = e.getValue();
        if (keyValue > -.2 && keyValue < .2) return;  // deadzone
        Vector3f fwd = (game.getEngine().getRenderSystem().getViewport("MAIN").getCamera()).getN();
        if(count == 0){
            speed = (float) game.getElapsedTime() * 0.009f;
            count++;
        }
        if(e.getValue() > 0){
               game.move(speed, 0.10f, "backward", fwd);
        }else{
               game.move(speed, 0.10f, "forward", fwd);
        }
		protClient.sendMoveMessage(av.getWorldLocation());

    }
}


