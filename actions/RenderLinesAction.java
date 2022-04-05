package actions;

import tage.*;
import tage.input.action.AbstractInputAction;
import net.java.games.input.Event;
import org.joml.*;

import a3.MyGame;

public class RenderLinesAction extends AbstractInputAction
{
	private MyGame game;

	public RenderLinesAction(MyGame g){	
        game = g;
	}

	@Override
	public void performAction(float time, Event e){
        game.setLineStatus();
	}
}


