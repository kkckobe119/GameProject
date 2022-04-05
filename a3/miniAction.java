package a3;

import tage.*;
//import myGame.a1.*;
import tage.input.action.AbstractInputAction; 
import net.java.games.input.Event; 
import org.joml.*;


public class miniAction extends AbstractInputAction 
 { 
    private MyGame game;
    private int dir;
    private float zoom;
    private float pan;
    
    public miniAction(MyGame g, int dir, float zoom, float pan) { 
        game = g;
        this.dir = dir;
        this.zoom = zoom;
        this.pan = pan;

        
    } 

    @Override 
    public void performAction(float time, Event e) 
    {   
        //game.adjustMinimap(pan, zoom, dir);
    
    }
 } 
