package a3;

import tage.*;
import org.joml.*;

public class FlashController extends NodeController
{
    private boolean txt;
    private TextureImage hi;
    private TextureImage to;

	public FlashController() { super(); }

	public FlashController(Engine e)
	{	super();
        hi = new TextureImage("prize1.png");
        to = new TextureImage("prize2.png");
		txt = true;
	}

	public void apply(GameObject go)
	{	float elapsedTime = super.getElapsedTime();
        
        if(txt && elapsedTime%15 == 0){
            go.setTextureImage(to);
            txt = false;
        } else if(txt == false && elapsedTime%15 == 0){
            go.setTextureImage(hi);
            txt = true;
        }
	}
}