import java.util.Random;
import java.util.UUID;

public class NPC{

	double locationX, locationY, locationZ;
	double dir = 0.1;
	double size = 1.0;

	public NPC()
	{	locationX=0.0;
		locationY=0.0;
		locationZ=0.0;
	}

	public void randomizeLocation(int seedX, int seedZ)
	{	
		//locationX = ((double)seedX)/4.0 - 5.0;
		locationX = 25;
		locationY = 2;
		locationZ = 0;
	}

	public double getX() { return locationX; }
	public double getY() { return locationY; }
	public double getZ() { return locationZ; }

	public void getBig() { size=2.0; }
	public void getSmall() { size=1.0; }
	public double getSize() { return size; }

	public void updateLocation() 
    {    
		if (locationZ > 25) dir=-0.1;
		if (locationZ < -25) dir=0.1;
		locationZ = locationZ + dir;
    }
}
