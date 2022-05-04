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
		locationX = ((double)seedX)/4.0 - 5.0;
		locationX = 50;
		locationY = 1.5;
		locationZ = -2;
	}

	public double getX() { return locationX; }
	public double getY() { return locationY; }
	public double getZ() { return locationZ; }

	public void getBig() { size=2.0; }
	public void getSmall() { size=1.0; }
	public double getSize() { return size; }

	public void updateLocation() 
	{	
		if (locationX > -50){
			locationX-=0.2;
			System.out.println("locationXi: " + locationX);

		}else if (locationX < -50){
			locationX=0;
			System.out.println("locationX0: " + locationX);
		}else{
			locationX-=0.1;
		}
	}
}
