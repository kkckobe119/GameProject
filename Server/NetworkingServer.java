import java.io.IOException;
import tage.networking.IGameConnection.ProtocolType;

public class NetworkingServer 
{
	private GameServerUDP thisUDPServer;
	private GameServerTCP thisTCPServer;

	private NPCcontroller npcCtrl;

	public NetworkingServer(int serverPort, String protocol) 
	{	
		npcCtrl = new NPCcontroller();
		try 
		{	
			if(protocol.toUpperCase().compareTo("TCP") == 0)
			{	thisTCPServer = new GameServerTCP(serverPort, npcCtrl);
				System.out.println("tcp");
			}
			else
			{	thisUDPServer = new GameServerUDP(serverPort, npcCtrl);
				System.out.println("udp");
				npcCtrl.start(thisUDPServer);
			}
		} 
		catch (IOException e) 
		{	e.printStackTrace();
		}

	}

	public static void main(String[] args) 
	{	if(args.length > 1)
		{	
			//NetworkingServer app = new NetworkingServer(Integer.parseInt(args[0]));
			NetworkingServer app = new NetworkingServer(Integer.parseInt(args[0]), args[1]);
		}
	}

}
