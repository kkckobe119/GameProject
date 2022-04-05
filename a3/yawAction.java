package a3;

import tage.*;
//import myGame.a1.*;
import tage.input.action.AbstractInputAction; 
import net.java.games.input.Event; 
import org.joml.*;


public class yawAction extends AbstractInputAction 
 { 
    private MyGame game; 
    private float count = 0;
    private float speed;

    
    public yawAction(MyGame g) { 
        game = g;
        
    } 

    @Override 
    public void performAction(float time, Event e) 
    {   
        float keyValue = e.getValue();
        if (keyValue > -.2 && keyValue < .2) return;  // deadzone
        if(count == 0){
            speed = (float) game.getElapsedTime() * 0.9f;
            count++;
        }
        if(e.getValue() > 0){
           game.yaw(speed, -0.07f);
        }else{
           game.yaw(speed, 0.07f);
        }
    }
 } 

 