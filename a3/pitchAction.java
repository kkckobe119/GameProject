package a3;

import tage.*;
//import myGame.a1.*;
import tage.input.action.AbstractInputAction; 
import net.java.games.input.Event; 
import org.joml.*;

public class pitchAction extends AbstractInputAction 
 { 
    private MyGame game; 
    private float count = 0;
    private float speed;

    
    public pitchAction(MyGame g) { 
        game = g;
        
    } 

    @Override 
    public void performAction(float time, Event e) 
    {   
        float keyValue = e.getValue();
        if (keyValue > -.2 && keyValue < .2) return;  // deadzone
        if(count == 0){
            speed = (float) game.getElapsedTime() * 0.003f;
            count++;
        }
        if(e.getValue() > 0){
                //game.pitch(speed, 0.01f);
        }else{
                //game.pitch(speed, -0.01f);
        }

    }
 } 