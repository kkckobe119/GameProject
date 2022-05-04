import java.util.Random;
import java.util.UUID;
import tage.ai.behaviortrees.*;

public class NPCcontroller
{
	private NPC npc;
	Random rn = new Random();
	BehaviorTree bt = new BehaviorTree(BTCompositeType.SELECTOR);
	boolean nearFlag = false;
	long thinkStartTime, tickStartTime, lastThinkUpdateTime, lastTickUpdateTime;
	//GameAIServerUDP server;
	GameServerUDP server;
	double criteria = 2.0;

	public void updateNPCs()
	{	npc.updateLocation();
	}

	// public void start(GameAIServerUDP s)
	// {	thinkStartTime = System.nanoTime();
	// 	tickStartTime = System.nanoTime();
	// 	lastThinkUpdateTime = thinkStartTime;
	// 	lastTickUpdateTime = tickStartTime;
	// 	server = s;
	// 	setupNPCs();
	// 	setupBehaviorTree();
	// 	npcLoop();
	// }

	public void start(GameServerUDP s)
	{	
		System.out.println("test1");
		thinkStartTime = System.nanoTime();
		tickStartTime = System.nanoTime();
		System.out.println("test2");
		lastThinkUpdateTime = thinkStartTime;
		lastTickUpdateTime = tickStartTime;
		System.out.println("test3");
		server = s;
		System.out.println("test4");
		setupNPCs();
		setupBehaviorTree();
		npcLoop();
	}

	// public void start(GameServerTCP s)
	// {	thinkStartTime = System.nanoTime();
	// 	tickStartTime = System.nanoTime();
	// 	lastThinkUpdateTime = thinkStartTime;
	// 	lastTickUpdateTime = tickStartTime;
	// 	server = s;
	// 	setupNPCs();
	// 	setupBehaviorTree();
	// 	npcLoop();
	// }

	public NPC getNPC() { return npc; }
	public void setNearFlag(boolean b) { nearFlag=b; }
	public boolean getNearFlag() { return nearFlag; }
	public double getCriteria() { return criteria; }

	public void setupNPCs() 
	{	npc = new NPC();
		npc.randomizeLocation(rn.nextInt(40),rn.nextInt(40));
	}

	public void npcLoop()
	{	while (true)
		{	long currentTime = System.nanoTime();
			float elapsedThinkMilliSecs = (currentTime-lastThinkUpdateTime)/(1000000.0f);
			float elapsedTickMilliSecs = (currentTime-lastTickUpdateTime)/(1000000.0f);

			if (elapsedTickMilliSecs >= 25.0f)
			{	lastTickUpdateTime = currentTime;
				npc.updateLocation();
				server.sendNPCinfo();
				System.out.println("tick");
			}
	  
			if (elapsedThinkMilliSecs >= 250.0f)
			{	lastThinkUpdateTime = currentTime;
				bt.update(elapsedThinkMilliSecs);
				System.out.println("----------- THINK ------------");
			}
			Thread.yield();
		}
	}

	public void setupBehaviorTree()
	{	bt.insertAtRoot(new BTSequence(10));
		bt.insertAtRoot(new BTSequence(20));
		bt.insert(10, new OneSecPassed(this,npc,false));
		bt.insert(10, new GetSmall(npc));
		bt.insert(20, new AvatarNear(server,this,npc,false));
		bt.insert(20, new GetBig(npc));
	}
}
