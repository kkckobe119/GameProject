package a3;

import tage.*;
import tage.shapes.*;
import tage.input.*;
import tage.input.action.*;

import java.lang.Math;
import java.awt.*;

import java.awt.event.*;

import java.io.*;
import javax.swing.*;
import org.joml.*;

import net.java.games.input.*;
import net.java.games.input.Component.Identifier.*;

import java.net.InetAddress;

import java.net.UnknownHostException;
import net.java.games.input.*;
import net.java.games.input.Component.Identifier.*;
import tage.networking.IGameConnection.ProtocolType;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import java.util.*;
import java.util.List;

public class MyGame extends VariableFrameRateGame
{
	private static Engine engine;
	private InputManager im;
	private GhostManager gm;

	private int counter=0;
	private Vector3f currentPosition;
	private double startTime, prevTime, elapsedTime, amt;

	private GameObject ZAxis, XAxis, YAxis, dolphin, terr, avatar;
	private ObjShape ghostS, dolS, terrS, line1, line2, line3;
	private TextureImage ghostT, doltx, hills, grass;
	private Light lightP;
	private int fluffyClouds, lakeIslands; // skyboxes
	private CameraOrbitController orbitController;

	private String serverAddress, gpName;
	private int serverPort;
	private ProtocolType serverProtocol;
	private ProtocolClient protClient;
	private boolean isClientConnected = false, visible = false;
	private double test;


	public MyGame(String serverAddress, int serverPort, String protocol) { 
		super();
		gm = new GhostManager(this);
		this.serverAddress = serverAddress;
		this.serverPort = serverPort;
		if (protocol.toUpperCase().compareTo("TCP") == 0)
			this.serverProtocol = ProtocolType.TCP;
		else
			this.serverProtocol = ProtocolType.UDP;
	}

	public static void main(String[] args){
		MyGame game = new MyGame(args[0], Integer.parseInt(args[1]), args[2]);

		ScriptEngineManager factory = new ScriptEngineManager();
		String scriptFileName = "scripts/test.js";

		// get a list of the script engines on this platform
		List<ScriptEngineFactory> list = factory.getEngineFactories();
	
		System.out.println("Script Engine Factories found:");
		for (ScriptEngineFactory f : list)
		{ System.out.println("  Name = " + f.getEngineName()
						   + "  language = " + f.getLanguageName()
						   + "  extensions = " + f.getExtensions());
		}
	
		// get the JavaScript engine
		ScriptEngine jsEngine = factory.getEngineByName("js");
		// run the script
		game.executeScript(jsEngine, scriptFileName);
		System.out.println("1---------------------------------");
		engine = new Engine(game);
		game.initializeSystem();
		game.game_loop();
	}

	private void executeScript(ScriptEngine engine, String scriptFileName)
	{
	  try
	  { FileReader fileReader = new FileReader(scriptFileName);
		engine.eval(fileReader);         //execute all the script statements in the file
		test = (Double)engine.get("test");
		fileReader.close();
	  }
	  catch (FileNotFoundException e1)
	  { System.out.println(scriptFileName + " not found " + e1); }
	  catch (IOException e2)
	  { System.out.println("IO problem with " + scriptFileName + e2); }
	  catch (ScriptException e3) 
	  { System.out.println("ScriptException in " + scriptFileName + e3); }
	  catch (NullPointerException e4)
	  { System.out.println ("Null ptr exception reading " + scriptFileName + e4); }
	}

	@Override
	public void loadShapes()
	{	dolS = new ImportedModel("dolphinHighPoly.obj");
		terrS = new TerrainPlane(1000);
		ghostS = new ImportedModel("dolphinHighPoly.obj");
		line1 = new Line(new Vector3f(-999999.0f, 0.0f, 0.0f) , new Vector3f(999999.0f, 0.0f, 0.0f));
        line2 = new Line(new Vector3f(0.0f, -999999.0f, 0.0f) , new Vector3f(0.0f, 999999.0f, 0.0f));
        line3 = new Line(new Vector3f(0.0f, 0.0f, -999999.0f) , new Vector3f(0.0f, 0.0f, 999999.0f));
	}

	@Override
	public void loadTextures()
	{	doltx = new TextureImage("Dolphin_HighPolyUV.png");
		ghostT = new TextureImage("redDolphin.jpg");
		hills = new TextureImage("hills.jpg");
		grass = new TextureImage("grass.jpg");
	}

	@Override
	public void buildObjects()
	{	Matrix4f initialTranslation, initialRotation, initialScale;

		// build dolphin avatar
		dolphin = new GameObject(GameObject.root(), dolS, doltx);
		initialTranslation = (new Matrix4f()).translation(-2f,0f,-2f);
		dolphin.setLocalTranslation(initialTranslation);
		initialRotation = (new Matrix4f()).rotationY((float)java.lang.Math.toRadians(180.0f));
		dolphin.setLocalRotation(initialRotation);

		// build terrain object
		terr = new GameObject(GameObject.root(), terrS, grass);
		initialTranslation = (new Matrix4f()).translation(0f,0f,0f);
		terr.setLocalTranslation(initialTranslation);
		initialScale = (new Matrix4f()).scaling(20.0f, 1.0f, 20.0f);
		terr.setLocalScale(initialScale);
		terr.setHeightMap(hills);
	}

	@Override
	public void initializeGame(){
		System.out.println(test);
		prevTime = System.currentTimeMillis();
		startTime = System.currentTimeMillis();
		(engine.getRenderSystem()).setWindowDimensions(1900,1000);

		//----------------- adding light -----------------
		Light.setGlobalAmbient(.5f, .5f, .5f);

		lightP = new Light();
		lightP.setLocation(new Vector3f(5.0f, 5f, 0f));
		(engine.getSceneGraph()).addLight(lightP);

		// ----------------- INPUTS SECTION -----------------------------
	
		
		/*im = engine.getInputManager();
		String gpName = im.getFirstGamepadName();

		// build some action objects for doing things in response to user input
		FwdAction fwdAction = new FwdAction(this);
		TurnAction turnAction = new TurnAction(this);*/

		
		// attach the action objects to keyboard and gamepad components
		im = engine.getInputManager(); 

		// ----------------- initialize camera ----------------
		Camera c = (engine.getRenderSystem()).getViewport("MAIN").getCamera();
		if(gpName != null){
			orbitController = new CameraOrbitController(c, dolphin, gpName, engine);
		} else {
			orbitController = new CameraOrbitController(c, dolphin, im.getKeyboardName(), engine);
		}
		
		setupNetworking();
		buildActions();
	}

	public void buildActions(){
        //build actions
		//String gpName = "unloaded";
        //im = engine.getInputManager(); 
        if(im.getFirstGamepadName() != null)
            gpName = im.getFirstGamepadName();
        FwdAction fwdAction = new FwdAction(this, protClient);
		yawAction ywAction = new yawAction(this);
        miniAction zoomInAction = new miniAction(this, 0, 0.1f, 0);
        miniAction zoomOutAction = new miniAction(this, 1, 0.1f, 0);
        miniAction panInAction = new miniAction(this, 0, 0, 0.1f);
        miniAction panOutAction = new miniAction(this, 1, 0, 0.1f);
        Axis toggle = new Axis(this);
        //assign actions to controller
        if(gpName != null){
            im.associateAction(gpName, 
            net.java.games.input.Component.Identifier.Axis.X, ywAction, 
            InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN); 
            im.associateAction(gpName, 
            net.java.games.input.Component.Identifier.Axis.Y, fwdAction, 
            InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
            im.associateAction(gpName, 
            net.java.games.input.Component.Identifier.Button._0, zoomInAction, 
            InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
            im.associateAction(gpName, 
            net.java.games.input.Component.Identifier.Button._3, zoomOutAction, 
            InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
            im.associateAction(gpName, 
            net.java.games.input.Component.Identifier.Button._1, panInAction, 
            InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
            im.associateAction(gpName, 
            net.java.games.input.Component.Identifier.Button._2, panOutAction, 
            InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
            im.associateAction(gpName, 
            net.java.games.input.Component.Identifier.Button._7, toggle, 
            InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);

            
        }

        im.associateAction(im.getKeyboardName(), net.java.games.input.Component.Identifier.Key.E, zoomInAction,
		InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		
		im.associateAction(im.getKeyboardName(),net.java.games.input.Component.Identifier.Key.R, zoomOutAction,
			InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		
		im.associateAction(im.getKeyboardName(), net.java.games.input.Component.Identifier.Key.T, panInAction,
			InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
            im.associateAction(im.getKeyboardName(), net.java.games.input.Component.Identifier.Key.Y, panOutAction,
		InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		
		im.associateAction(im.getKeyboardName(), net.java.games.input.Component.Identifier.Key.U, toggle,
			InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
	
	}

	public void toggleAxis(){
        if(visible == false){
            XAxis = new GameObject(GameObject.root(),line1);
            YAxis = new GameObject(GameObject.root(), line2);
            ZAxis = new GameObject(GameObject.root(), line3);
            (XAxis.getRenderStates()).setColor(new Vector3f(1f,0f,0f)); 
            (YAxis.getRenderStates()).setColor(new Vector3f(0f,1f,0f)); 
            (ZAxis.getRenderStates()).setColor(new Vector3f(0f,0f,1f));
            visible = true;
        } else {
            GameObject.root().removeChild(XAxis);
            GameObject.root().removeChild(YAxis);
            GameObject.root().removeChild(ZAxis); 
            visible = false; 
        } 
    }

	public void move(float speed, float con, String d, Vector3f fwd){
		switch(d){
			case "forward":
			dolphin.setLocalLocation(dolphin.getLocalLocation().add(dolphin.getLocalForwardVector().mul(con*speed)));
			break;
			case "backward":
			dolphin.setLocalLocation(dolphin.getLocalLocation().add(dolphin.getLocalForwardVector().mul(-con*speed)));
			break;
		}
	}

	public void yaw(float speed, float con){
		dolphin.setLocalRotation(dolphin.getLocalRotation().rotateY((float) Math.toRadians(con*speed)));
   }

	@Override
	public void loadSkyBoxes()
	{	lakeIslands = (engine.getSceneGraph()).loadCubeMap("lakeIslands");
		(engine.getSceneGraph()).setActiveSkyBoxTexture(lakeIslands);
		(engine.getSceneGraph()).setSkyBoxEnabled(true);
	}

	public GameObject getAvatar() { return dolphin; }

	@Override
	public void update()
	{	elapsedTime = System.currentTimeMillis() - prevTime;
		prevTime = System.currentTimeMillis();
		amt += elapsedTime;
		Camera c = (engine.getRenderSystem()).getViewport("MAIN").getCamera();
		
		// build and set HUD
		int elapsTimeSec = Math.round((float)(System.currentTimeMillis()-startTime)/1000.0f);
		String elapsTimeStr = Integer.toString(elapsTimeSec);
		String counterStr = Integer.toString(counter);
		String dispStr1 = "Time = " + elapsTimeStr;
		String dispStr2 = "camera position = "
			+ (c.getLocation()).x()
			+ ", " + (c.getLocation()).y()
			+ ", " + (c.getLocation()).z();
		Vector3f hud1Color = new Vector3f(0,0,1);
		Vector3f hud2Color = new Vector3f(1,1,1);
		(engine.getHUDmanager()).setHUD1(dispStr1, hud1Color, 15, 15);
		(engine.getHUDmanager()).setHUD2(dispStr2, hud2Color, 500, 15);

		// update inputs and camera
		im.update((float)elapsedTime);

		// update altitude of dolphin based on height map
	//	Vector3f loc = dolphin.getWorldLocation();
	//	float height = terr.getHeight(loc.x(), loc.z());
	//	dolphin.setLocalLocation(new Vector3f(loc.x(), height, loc.z()));

		orbitController.updateCameraPosition();
		processNetworking((float)elapsedTime);
		
	}

	public double getElapsedTime() {
		return elapsedTime;
	}


	// ---------- NETWORKING SECTION ----------------

	public ObjShape getGhostShape() { return ghostS; }
	public TextureImage getGhostTexture() { return ghostT; }
	public GhostManager getGhostManager() { return gm; }
	public Engine getEngine() { return engine; }
	public ProtocolClient getProtClient() {return protClient; }
	
	private void setupNetworking() {
		isClientConnected = false;	
		try 
		{	protClient = new ProtocolClient(InetAddress.getByName(serverAddress), serverPort, serverProtocol, this);
		} 	catch (UnknownHostException e) 
		{	e.printStackTrace();
		}	catch (IOException e) 
		{	e.printStackTrace();
		}
		if (protClient == null)
		{	System.out.println("missing protocol host");
		}
		else
		{	// Send the initial join message with a unique identifier for this client
			System.out.println("sending join message to protocol host");
			protClient.sendJoinMessage();
		}
	}
	
	protected void processNetworking(float elapsTime)
	{	// Process packets received by the client from the server
		if (protClient != null)
			protClient.processPackets();
	}

	public Vector3f getPlayerPosition() { return dolphin.getWorldLocation(); }

	public void setIsConnected(boolean value) { this.isClientConnected = value; }
	
	private class SendCloseConnectionPacketAction extends AbstractInputAction
	{	@Override
		public void performAction(float time, net.java.games.input.Event evt) 
		{	if(protClient != null && isClientConnected == true)
			{	protClient.sendByeMessage();
			}
		}
	}
}