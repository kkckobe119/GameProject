import java.io.IOException;
import java.net.InetAddress;
import java.util.UUID;

import tage.networking.server.GameConnectionServer;
import tage.networking.server.IClientInfo;

public class GameAIServerUDP extends GameConnectionServer<UUID> 
{
	NPCcontroller npcCtrl;

	public GameAIServerUDP(int localPort, NPCcontroller npc) throws IOException 
	{		super(localPort, ProtocolType.UDP);
			npcCtrl = npc;
	}

	public void sendCheckForAvatarNear()
	{	try 
		{	String message = new String("isnr");
			message += "," + (npcCtrl.getNPC()).getX();
			message += "," + (npcCtrl.getNPC()).getY();
			message += "," + (npcCtrl.getNPC()).getZ();
			message += "," + (npcCtrl.getCriteria());
			sendPacketToAll(message);
		} 
		catch (IOException e) 
		{	System.out.println("couldnt send isnr message");
				e.printStackTrace();
		}
	}

	// ------------------  NPC SETUP ---------------
	
	/**
	 * Informs all clients of the new positions of every NPC.
	 * <p>
	 * Message Format: (mnpc,npcID,x,y,z,state) where x, y, and z represent the position.
	 */
	public void sendNPCinfo()
	{	try 
		{	String message = new String("mnpc");
			message += "," + (npcCtrl.getNPC()).getX();
			message += "," + (npcCtrl.getNPC()).getY();
			message += "," + (npcCtrl.getNPC()).getZ();
			message += "," + (npcCtrl.getNPC()).getSize();
			sendPacketToAll(message);
		}
		catch (IOException e) 
		{	System.out.println("clients not ready for NPCs yet");
			e.printStackTrace();
		}
	}
	
	public void sendNPCstart(UUID clientID)
	{	try 
		{	String message = new String("createNPC");
			message += "," + (npcCtrl.getNPC()).getX();
			message += "," + (npcCtrl.getNPC()).getY();
			message += "," + (npcCtrl.getNPC()).getZ();
			sendPacket(message,clientID);
		} 
		catch (IOException e) 
		{	System.out.println("this client not ready for NPCs yet");
			e.printStackTrace();
		}
	}

	// ----------- PROCESSING INCOMING PACKETS ----------------
	
	@Override
	public void processPacket(Object o, InetAddress senderIP, int senderPort)
	{
		String message = (String)o;
		String[] messageTokens = message.split(",");
		
		if(messageTokens.length > 0)
		{	// JOIN -- Case where client just joined the server
			// Received Message Format: (join,localId)
			if(messageTokens[0].compareTo("join") == 0)
			{	try 
				{	IClientInfo ci;					
					ci = getServerSocket().createClientInfo(senderIP, senderPort);
					UUID clientID = UUID.fromString(messageTokens[1]);
					addClient(ci, clientID);
					System.out.println("Join request received from - " + clientID.toString());
					sendJoinedMessage(clientID, true);
				} 
				catch (IOException e) 
				{	e.printStackTrace();
			}	}

			// Case where clients leaves the server
			// Received Message Format: (bye,localId)
			if(messageTokens[0].compareTo("bye") == 0)
			{	UUID clientID = UUID.fromString(messageTokens[1]);
				sendByeMessages(clientID);
				removeClient(clientID);
			}
			
			// Case where server receives a CREATE message
			// Received Message Format: (create,localId,x,y,z)
			if(messageTokens[0].compareTo("create") == 0)
			{	UUID clientID = UUID.fromString(messageTokens[1]);
				String[] pos = {messageTokens[2], messageTokens[3], messageTokens[4]};
				sendCreateMessages(clientID, pos);
				sendWantsDetailsMessages(clientID);
			}
			
			// Case where server receives a DETAILS-FOR message
			// Received Message Format: (dsfr,remoteId,localId,x,y,z)
			if(messageTokens[0].compareTo("dsfr") == 0)
			{	UUID clientID = UUID.fromString(messageTokens[1]);
				UUID remoteID = UUID.fromString(messageTokens[2]);
				String[] pos = {messageTokens[3], messageTokens[4], messageTokens[5]};
				sendDetailsForMessage(clientID, remoteID, pos);
			}
			
			// Case where server receives a MOVE message
			// Received Message Format: (move,localId,x,y,z)
			if(messageTokens[0].compareTo("move") == 0)
			{	UUID clientID = UUID.fromString(messageTokens[1]);
				String[] pos = {messageTokens[2], messageTokens[3], messageTokens[4]};
				sendMoveMessages(clientID, pos);
			}

			// -------- RECEIVING NPC MESSAGES SECTION --------------------

			// Case where server receives request for NPCs
			// Received Message Format: (needNPC,id)
			if(messageTokens[0].compareTo("needNPC") == 0)
			{	System.out.println("server got a needNPC message");
				UUID clientID = UUID.fromString(messageTokens[1]);
				sendNPCstart(clientID);
			}

			// Case where server receives notice that an av is close to the npc
			// Received Message Format: (isnear,id)
			if(messageTokens[0].compareTo("isnear") == 0)
			{	UUID clientID = UUID.fromString(messageTokens[1]);
				handleNearTiming(clientID);
			}
		}
	}

	public void handleNearTiming(UUID clientID)
	{	npcCtrl.setNearFlag(true);
	}

	/**
	 * Informs the client who just requested to join the server if their if their 
	 * request was able to be granted.
	 * <p>
	 * Message Format: (join,success) or (join,failure)
	 */
	public void sendJoinedMessage(UUID clientID, boolean success)
	{	try 
		{	String message = new String("join,");
			if(success)
				message += "success";
			else
				message += "failure";
			sendPacket(message, clientID);
		} 
		catch (IOException e) 
		{	e.printStackTrace();
		}
	}
	
	/**
	 * Informs a client that the avatar with the identifier remoteId has left the server. 
	 * This message is meant to be sent to all client currently connected to the server 
	 * when a client leaves the server.
	 * <p>
	 * Message Format: (bye,remoteId)
	 */
	public void sendByeMessages(UUID clientID)
	{	try 
		{	String message = new String("bye," + clientID.toString());
			forwardPacketToAll(message, clientID);
		} 
		catch (IOException e) 
		{	e.printStackTrace();
		}
	}
	
	/**
	 * Informs a client that a new avatar has joined the server with the unique identifier 
	 * remoteId. This message is intended to be send to all clients currently connected to 
	 * the server when a new client has joined the server and sent a create message to the 
	 * server. This message also triggers WANTS_DETAILS messages to be sent to all client 
	 * connected to the server. 
	 * <p>
	 * Message Format: (create,remoteId,x,y,z) where x, y, and z represent the position
	 */
	public void sendCreateMessages(UUID clientID, String[] position)
	{	try 
		{	String message = new String("create," + clientID.toString());
			message += "," + position[0];
			message += "," + position[1];
			message += "," + position[2];
			forwardPacketToAll(message, clientID);
		} 
		catch (IOException e) 
		{	e.printStackTrace();
		}
	}
	
	/**
	 * Informs a client of the details for a remote client�s avatar. This message is in response 
	 * to the server receiving a DETAILS_FOR message from a remote client. That remote client�s 
	 * message�s localId becomes the remoteId for this message, and the remote client�s message�s 
	 * remoteId is used to send this message to the proper client. 
	 * <p>
	 * Message Format: (dsfr,remoteId,x,y,z) where x, y, and z represent the position.
	 */
	public void sendDetailsForMessage(UUID clientID, UUID remoteId, String[] position)
	{	try 
		{	String message = new String("dsfr," + remoteId.toString());
			message += "," + position[0];
			message += "," + position[1];
			message += "," + position[2];
			sendPacket(message, clientID);
		} 
		catch (IOException e) 
		{	e.printStackTrace();
		}
	}
	
	/**
	 * Informs a local client that a remote client wants the local client�s avatar�s information. 
	 * This message is meant to be sent to all clients connected to the server when a new client 
	 * joins the server. 
	 * <p>
	 * Message Format: (wsds,remoteId)
	 */
	public void sendWantsDetailsMessages(UUID clientID)
	{	try 
		{	String message = new String("wsds," + clientID.toString());
			forwardPacketToAll(message, clientID);
		} 
		catch (IOException e) 
		{	e.printStackTrace();
		}
	}
	
	/**
	 * Informs a client that a remote client�s avatar has changed position. x, y, and z represent 
	 * the new position of the remote avatar. This message is meant to be forwarded to all clients
	 * connected to the server when it receives a MOVE message from the remote client.   
	 * <p>
	 * Message Format: (move,remoteId,x,y,z) where x, y, and z represent the position.
	 */
	public void sendMoveMessages(UUID clientID, String[] position)
	{	try 
		{	String message = new String("move," + clientID.toString());
			message += "," + position[0];
			message += "," + position[1];
			message += "," + position[2];
			forwardPacketToAll(message, clientID);
		} 
		catch (IOException e) 
		{	e.printStackTrace();
		}
	}

	// ------------  SENDING NPC MESSAGES -----------------

	/**
	 * Informs clients of the whereabouts of the NPCs. This message is intended to be sent
	 * to any client at the time that they connect to the game.
	 * <p>
	 * Message Format: (createNPC,id,x,y,z) where x, y, and z represent the position
	 */
	public void sendCreateNPCmsg(UUID clientID, String[] position)
	{	try 
		{	System.out.println("server telling clients about an NPC");
			String message = new String("createNPC," + clientID.toString());
			message += "," + position[0];
			message += "," + position[1];
			message += "," + position[2];
			forwardPacketToAll(message, clientID);
		} 
		catch (IOException e) 
		{	e.printStackTrace();
		}
	}
}
