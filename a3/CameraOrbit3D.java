package a3;

import tage.*;
import tage.input.*;
import tage.input.action.AbstractInputAction;
import net.java.games.input.Component;
import net.java.games.input.Event;
import org.joml.*;
import java.lang.Math;

public class CameraOrbit3D
{	private Engine engine;
	private Camera camera;		//the camera being controlled
	private GameObject avatar;	//the target avatar the camera looks at
	private float cameraAzimuth;	//rotation of camera around target Y axis
	private float cameraElevation;	//elevation of camera above target
	private float cameraRadius;	//distance between camera and target

	public CameraOrbit3D(Camera cam, GameObject av, String gpName, Engine e)
	{	engine = e;
		camera = cam;
		avatar = av;
		cameraAzimuth = 0.0f;		// start from BEHIND and ABOVE the target
		cameraElevation = 20.0f;	// elevation is in degrees
		cameraRadius = 10.0f;		// distance from camera to avatar
		if(gpName != null)
			setupInputs(gpName);
		updateCameraPosition();
	}

	private void setupInputs(String gp)
	{	OrbitAzimuthAction azmAction = new OrbitAzimuthAction();
		OrbitRadiusAction radAction = new OrbitRadiusAction();
		OrbitElevationAction elAction = new OrbitElevationAction();
		InputManager im = engine.getInputManager();
		im.associateAction(gp,
			net.java.games.input.Component.Identifier.Axis.RX,
			azmAction, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);

		im.associateAction(gp,
			net.java.games.input.Component.Identifier.Axis.RY,
			radAction, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);

		im.associateAction(gp,
			net.java.games.input.Component.Identifier.Axis.POV,
			elAction, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);

		im.associateAction(im.getKeyboardName(), Component.Identifier.Key.F, azmAction,
		InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		
		im.associateAction(im.getKeyboardName(),Component.Identifier.Key.G, radAction,
			InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		
		im.associateAction(im.getKeyboardName(),Component.Identifier.Key.H, elAction,
			InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
	}

	// Updates the camera position by computing its azimuth, elevation, and distance 
	// relative to the target in spherical coordinates, then converting those spherical 
	// coords to world Cartesian coordinates and setting the camera position from that.

	public void updateCameraPosition()
	{	Vector3f avatarRot = avatar.getWorldForwardVector();
		double avatarAngle = Math.toDegrees((double)avatarRot.angleSigned(new Vector3f(0,0,-1), new Vector3f(0,1,0)));
		float totalAz = cameraAzimuth - (float)avatarAngle;
		double theta = Math.toRadians(totalAz);	// rotation around target
		double phi = Math.toRadians(cameraElevation);	// altitude angle
		float x = cameraRadius * (float)(Math.cos(phi) * Math.sin(theta));
		float y = cameraRadius * (float)(Math.sin(phi));
		float z = cameraRadius * (float)(Math.cos(phi) * Math.cos(theta));
		camera.setLocation(new Vector3f(x,y,z).add(avatar.getWorldLocation()));
		camera.lookAt(avatar);
	}

	private class OrbitAzimuthAction extends AbstractInputAction
	{	public void performAction(float time, Event event)
		{	float rotAmount;
			if (event.getValue() < -0.2)
			{	rotAmount=-0.2f;
			}
			else
			{	if (event.getValue() > 0.2)
				{	rotAmount=0.2f;
				}
				else
				{	rotAmount=0.0f;
				}
			}
			cameraAzimuth += rotAmount;
			cameraAzimuth = cameraAzimuth % 360;
			updateCameraPosition();
		}
	}


  
	private class OrbitRadiusAction extends AbstractInputAction {
		public void performAction(float time, Event event)
		{	float rotAmount;
			if (event.getValue() < -0.2) {	
				rotAmount=-0.2f;
			}
			else
			{	if (event.getValue() > 0.2){
					rotAmount=0.2f;
				} else{	
					rotAmount=0.0f;
				}
			}
			cameraRadius += rotAmount;
			//cameraAzimuth = cameraAzimuth % 360;
			updateCameraPosition();
		}
	}
  
	private class OrbitElevationAction extends AbstractInputAction {
		public void performAction(float time, Event event)
		{	float rotAmount;
			if (event.getValue() < -0.2) {	
				rotAmount=-0.2f;
			}
			else
			{	if (event.getValue() > 0.2){
					rotAmount=0.2f;
				} else{	
					rotAmount=0.0f;
				}
			}
			cameraElevation += rotAmount;
			cameraElevation = cameraElevation % 360;
			updateCameraPosition();
		}
	}

}