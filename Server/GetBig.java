import tage.ai.behaviortrees.*;

public class GetBig extends BTAction
{
	NPC npc;
  
	public GetBig(NPC n)
	{	npc = n;
	}

	protected BTStatus update(float elapsedTime)
	{	npc.getBig();
		return BTStatus.BH_SUCCESS;
	}
}
