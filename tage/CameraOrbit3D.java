package tage;

import tage.*;
import tage.input.*;
import tage.input.action.AbstractInputAction;
import net.java.games.input.Event;
import org.joml.*;
import java.lang.Math;

import java.awt.event.*;

import java.util.ArrayList;
import tage.input.InputManager;

import net.java.games.input.*;
import net.java.games.input.Component.Identifier.*;



/**
* Controls for camera that orbits around the avatar
*/
public class CameraOrbit3D
{	private Engine engine;
	private Camera camera;		//the camera being controlled
	private GameObject avatar;	//the target avatar the camera looks at
	private float cameraAzimuth;	//rotation of camera around target Y axis
	private float cameraElevation;	//elevation of camera above target
	private float cameraRadius;	//distance between camera and target

	/**
	* Constructor for CameraOrbit3D
	*/
	public CameraOrbit3D(Camera cam, GameObject av, String gpName, Engine e){
		engine = e;
		camera = cam;
		avatar = av;
		cameraAzimuth = 0.0f;		// start from BEHIND and ABOVE the target
		cameraElevation = 20.0f;	// elevation is in degrees
		cameraRadius = 2.0f;		// distance from camera to avatar
		setupInputs(gpName);
		updateCameraPosition();
	}

	/**
	* Creates controls for orbitting around the camera
	*/
	private void setupInputs(String gp)
	{	OrbitAzimuthAction azmAction = new OrbitAzimuthAction();
		OrbitRadiusAction radAction = new OrbitRadiusAction();
		OrbitElevationAction eleAction = new OrbitElevationAction();

		InputManager im = engine.getInputManager();

        ArrayList<Controller> controllers = im.getControllers();

		for (Controller con : controllers){
			if (con.getType() == Controller.Type.KEYBOARD){
                im.associateAction(con, net.java.games.input.Component.Identifier.Key.LEFT,
                azmAction, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
				im.associateAction(con, net.java.games.input.Component.Identifier.Key.RIGHT,
                azmAction, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
                im.associateAction(con, net.java.games.input.Component.Identifier.Key.Z,
                radAction, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
                im.associateAction(con, net.java.games.input.Component.Identifier.Key.X,
                radAction, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
                im.associateAction(con, net.java.games.input.Component.Identifier.Key.UP,
                eleAction, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
				im.associateAction(con, net.java.games.input.Component.Identifier.Key.DOWN,
                eleAction, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
			}
		}

	}

	// Updates the camera position by computing its azimuth, elevation, and distance 
	// relative to the target in spherical coordinates, then converting those spherical 
	// coords to world Cartesian coordinates and setting the camera position from that.

	public void updateCameraPosition(){
			float totalAz = cameraAzimuth;
			double theta = Math.toRadians(totalAz);	// rotation around target
			double phi = Math.toRadians(cameraElevation);	// altitude angle
			float x = cameraRadius * (float)(Math.cos(phi) * Math.sin(theta));
			float y = cameraRadius * (float)(Math.sin(phi));
			float z = cameraRadius * (float)(Math.cos(phi) * Math.cos(theta));
			camera.setLocation(new Vector3f(x,y,z).add(avatar.getWorldLocation()));
			camera.lookAt(avatar);
	}
	/**
	* Moves camera left/right
	*/
	private class OrbitAzimuthAction extends AbstractInputAction{
		public void performAction(float time, Event event){
			float rotAmount;
			if (event.getComponent().toString().equals("Left")){
				rotAmount=-0.2f;
			}else{
				if (event.getComponent().toString().equals("Right")){
					rotAmount=0.2f;
				}else{
					rotAmount=0.0f;
				}
			}
			cameraAzimuth += rotAmount;
			cameraAzimuth = cameraAzimuth % 360;
			updateCameraPosition();
		}
	}

	/**
	* Zooms camera in/out
	*/
	private class OrbitRadiusAction extends AbstractInputAction{
		public void performAction(float time, Event event){
			float rotAmount;
			if (event.getComponent().toString().equals("Z")){
				rotAmount=-0.1f;
			}else{
				if (event.getComponent().toString().equals("X")){
					rotAmount=0.1f;
				}else{
					rotAmount=0.0f;
				}
			}
			cameraRadius += rotAmount;
			cameraRadius = cameraRadius % 360;
			updateCameraPosition();
		}
	}

	/**
	* Rotates camera above/below
	*/
	private class OrbitElevationAction extends AbstractInputAction{
		public void performAction(float time, Event event){
		float rotAmount;
			if (event.getComponent().toString().equals("Up")){
				rotAmount=-0.2f;
			}else{
				if (event.getComponent().toString().equals("Down")){
					rotAmount=0.2f;
				}else{	
					rotAmount=0.0f;
				}
			}
			cameraElevation += rotAmount;
			cameraElevation = cameraElevation % 360;
			updateCameraPosition();
		}
	}

}