import java.util.Random;
import java.util.UUID;
import tage.ai.behaviortrees.*;

public class NPCcontroller
{
	private NPC npc;
	private NPC npc1;
	Random rn = new Random();
	BehaviorTree bt = new BehaviorTree(BTCompositeType.SELECTOR);
	boolean nearFlag = false;
	long thinkStartTime, tickStartTime, lastThinkUpdateTime, lastTickUpdateTime;
	GameServerUDP server;
	double criteria = 2.0;

	public void updateNPCs(){
		npc.updateLocation();
		npc1.updateLocation();
	}

	public void start(GameServerUDP s)
	{	
		thinkStartTime = System.nanoTime();
		tickStartTime = System.nanoTime();
		lastThinkUpdateTime = thinkStartTime;
		lastTickUpdateTime = tickStartTime;
		server = s;
		setupNPCs();
		setupBehaviorTree();
		npcLoop();
	}

	public NPC getNPC() { return npc; }
	public NPC getNPC1() { return npc1; }
	public void setNearFlag(boolean b) { nearFlag=b; }
	public boolean getNearFlag() { return nearFlag; }
	public double getCriteria() { return criteria; }

	public void setupNPCs() {	
		npc = new NPC();
		npc.randomizeLocation(rn.nextInt(40),rn.nextInt(40));

		npc1 = new NPC();
		npc1.randomizeLocation(rn.nextInt(20),rn.nextInt(40));
	}

	public void npcLoop()
	{	while (true)
		{	long currentTime = System.nanoTime();
			float elapsedThinkMilliSecs = (currentTime-lastThinkUpdateTime)/(1000000.0f);
			float elapsedTickMilliSecs = (currentTime-lastTickUpdateTime)/(1000000.0f);

			if (elapsedTickMilliSecs >= 25.0f)
			{	lastTickUpdateTime = currentTime;
				npc.updateLocation();
				npc1.updateLocation();
				//System.out.println("NPC: " + npc.getX() + " " + npc.getY() + " " + npc.getZ());
				//System.out.println("NPC1: " + npc1.getX() + " " + npc1.getY() + " " + npc1.getZ());
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
	{	
		bt.insertAtRoot(new BTSequence(10));
		bt.insertAtRoot(new BTSequence(20));
		bt.insert(10, new OneSecPassed(this,npc,false));
		bt.insert(10, new GetSmall(npc));
		bt.insert(20, new AvatarNear(server,this,npc,false));
		bt.insert(20, new GetBig(npc));
	}
}
