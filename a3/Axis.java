package a3;

import tage.*;
//import myGame.a1.*;
import tage.input.action.AbstractInputAction; 
import net.java.games.input.Event; 
import org.joml.*;


public class Axis extends AbstractInputAction 
 { 
    private MyGame game;
    
    public Axis(MyGame g) { 
        game = g;

        
    } 

    @Override 
    public void performAction(float time, Event e) 
    {   
        game.toggleAxis();
    
    }
 } 
