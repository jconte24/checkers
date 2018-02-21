import java.util.*;
import java.util.concurrent.TimeUnit;
import java.io.*;
import java.net.*;

/**
*Class acts as a server for the checkers game. It allows players to communicate with each other.
*Class inherits from Thread for the purpose of continually pulling and pushing data
*@version 2
*@author Dan Martineau
*/
public class GameServer extends Thread
{
	//Fields
	protected DatagramSocket sock = null;
	private final int PORT = 4444;
	private ArrayList<Node> clients;
	
	/**
	*Constructor
	*/
	public GameServer() throws IOException
	{
		clients = new ArrayList<Node>(10);
		//ipList = new ArrayList<String>(5);
		sock = new DatagramSocket(PORT);
	}
	
	public static void main(String[] args) throws Exception
	{
		GameServer server = new GameServer();

		Scanner kbd = new Scanner(System.in);
		int ipSelect = 0;

		//Show the DHCP address of the server
		showIPs();

		//begin listening & accepting clients' info
		server.start();

		System.out.println("\nReady to accept clients...\n");
	}

	/**
	*Method will send client string to appropriate decoder method.
	*@param address of current player
	*@param port number of current player
	*@param the string sent by a client
	*@return node associated with data
	*/
	private Node decode(DatagramPacket myPack, String data)
	{
		Node node = null;

		if(data.charAt(0) == 's')
		{
			try
			{
				node = serverDecode(myPack, data);
			}
			catch(InterruptedException e)
			{
				System.out.println("There was an interrupted exception with the pause.");
				System.exit(0);
			}
		}
		else if(data.charAt(0) == 'c')
			node = clientDecode(data.substring(2,5), data);
		else
			System.out.print("Error: data string not intended for server nor client.");

		return node;
	}

	/**
	*Method will decode data string intended for a server and pass it where it needs to go
	*@param address of current player
	*@param port number of current player
	*@param data string
	*@return node associated with data
	*/
	private Node serverDecode(DatagramPacket myPack, String data) throws InterruptedException
	{
		String serverData = data.substring(2);		//remove first char and space
		String id = serverData.substring(0,3);		//find the address of the node
		Node node = null;							//holder node
		String enqueStr = "";

		//"jjj" means that a client wishes to join the server
		if(id.equals("jjj") && data.substring(6,7).equals("n"))
		{
			id = newPlayer(myPack);	//set id as the id of the new player created
			System.out.println("Player has been added: " + id);
			node = getNode(id);				//get the node associated with id

			//add initialization statement to the node's queue
			enqueStr = "c " + id + " i";
			(node.getMyQueue()).enque(enqueStr);
		}
		//if the client inquires about opponent status
		else if(data.substring(6,7).equals("o"))
		{
			System.out.println("Client " + id + " requested opponent status.");
			//get node associated with id
			node = getNode(id);

			if(node.getOppID() == null)
			{
				System.out.println("Client " + id + " has no opponent.");
				enqueStr = "c " + id + " o f";
				(node.getMyQueue()).enque(enqueStr); 	//enque message indicating lack of opponent
			}
			else
			{
				System.out.println("Client " + id + " has opponent " + node.getOppID() + ".");
				enqueStr = "c " + id + " o t " + node.getOppID();
				(node.getMyQueue()).enque(enqueStr); 	//enque message verifying opponent
			}
		}
		//if client requests new opponent
		else if(data.substring(6,7).equals("n"))
		{
			String newOpp = null;
			int count = 1;

			newOpp = findOpp(id);
			
			if(newOpp != null)
			{
				node = getNode(id);
				enqueStr = "c " + id + " n t " + node.getOppID();
				(node.getMyQueue()).enque(enqueStr);

				System.out.println("New opponent found: " + node.getOppID());
			}
			else
			{
				node = getNode(id);
				enqueStr = "c " + id + " n f ";
				(node.getMyQueue()).enque(enqueStr);
				System.out.println("No other players found.");
			}
		}
		else if(data.substring(6,7).equals("t"))
		{
			node = getNode(id);
			
			Node oppNode = getNode(node.getOppID());
			if(node.getOppID() != null)
			{
				oppNode.removeOpp(); 												//set opponent's node so that current node to be terminated is no longer its opponent
				(oppNode.getMyQueue()).enque("c " + oppNode.getMyID() + " d");		//enque disconnection message into opponent's queue
			}

			clients.remove(node);

			System.out.println("Player " + id + " has terminated connection with server.");

			node = null;
		}
		else
		{
			System.out.println("Error: Need to set rest of server decode operations.");
		}

		return node;
	}

	/**
	*Method will decode a data string intended for a client
	*@param id of the client who sent the data string
	*@param data string
	*@return node associated with the data
	*/
	private Node clientDecode(String myID, String data)
	{
		String clientData = data.substring(2);		//remove first char and space
		String id = clientData.substring(0,3);		//find the address of the node
		Node node = null;							//holder node

		if(data.substring(6,7).equals("c"))
		{
			node = getNode(id);					//get the node associated with id

			if(node.getOppID() != null)		//must have opponent to send command to opponent
			{
				node = getNode(node.getOppID());	//set node as the opponent's node
				(node.getMyQueue()).enque(data);	//enque data string in opponent's queue
			}
		}
		else if(data.substring(6,7).equals("p"))
		{
			node = getNode(id);					//get the node associated with id
			(node.getMyQueue()).enque(data);	//enque data in node's own queue--purpose is to ping oneself
			System.out.println((node.getMyQueue()).peek());
		}
		else
		{
			System.out.println("Error: Need to set rest of client decode operations.");
		}

		return node;
	}

	/**
	*Method will instantiate a new player's cerdentials in memory
	*@param address of current player
	*@param port number of current player
	*@return the id assigned to the new player
	*/
	private String newPlayer(DatagramPacket myPack)
	{
		String myID = nextID();
		Node node = null;
		int i = clients.size();

		//create new data queue for player
		Queue myQueue = new Queue();

		//find opponent
		String oppID = findOpp(myID);

		//if there are no opponents
		if(oppID==null)
			node = new Node(myID, myPack, myQueue, null);
		//assign opponent to new player
		else
		{
			Node holder = getNode(oppID);

			if(holder != null)
			{
				node = new Node(myID, myPack, myQueue, oppID);
			}
			else
			{
				System.out.println("An opponent was found, but was null...that's weird. Gonna have to blow up the server.");
				System.exit(0);
			}
		}

		clients.add(i, node);

		return myID;
	}

	/**
	*Method generates a new user id
	*@return the new id
	*/
	private String nextID()
	{
		String id = "";

		if(clients.size() < 10)
			id = "00" + clients.size();
		else if(clients.size() < 100)
			id = "0" + clients.size();
		else
			id = "" + clients.size();

		return id;
	}

	/**
	*Method will search memory for unpaired players and return one if there is one.
	*Method will also assign current player as opponent of the new opponent
	*@param the id of the player requesing opponent
	*@return id of opponent or null if there are none
	*/
	private String findOpp(String playerID)
	{
		String id = null;;

		if(clients.size() >= 1)
		{
			for(int i = 0; i < clients.size(); i++)
			{
				if(clients.get(i).getOppID()==null && (!(clients.get(i).getMyID().equals(playerID))))
				{
					//get id of new opponent
					id = clients.get(i).getMyID();
					//set current player as opponent of the new opponent
					clients.get(i).setOppID(playerID);
				}
			}
		}

		return id;
	}

	/**
	*Method will find the node containing the specified id as "myID"
	*@param id of desired node
	*@return desired node
	*/
	private Node getNode(String myID)
	{
		Node node = null;

		for(int i = 0; i < clients.size(); i++)
		{
			if((clients.get(i).getMyID()).equals(myID))
				node = clients.get(i);
		}

		return node;
	}

	/**
	*This method was derrived and adapted from code found on a help forum on Stack Overflow
	*The method shows the host IPs available for the server
	*/
	private static void showIPs() throws Exception
	{
		System.out.println("IPv4 Addresses currently in use on the system:");

		String ip;

		Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();

		while (interfaces.hasMoreElements())
		{
			NetworkInterface iface = interfaces.nextElement();
			// filters out 127.0.0.1 and inactive interfaces
			if (iface.isLoopback() || !iface.isUp())
				continue;

			Enumeration<InetAddress> addresses = iface.getInetAddresses();
			while(addresses.hasMoreElements())
			{
				InetAddress addr = addresses.nextElement();

				// filters out ipv6 addresses
				if (addr instanceof Inet6Address) continue;

				ip = addr.getHostAddress();

				System.out.println("\t" + iface.getDisplayName() + " " + ip);
			}
		}
	}

	/**
	*Method creates a thread by extending the Thread class (of which run() is a parent method)
	*Method will send and recieve data from clients
	*/
	@Override
	public void run()
	{
		for(;;)
		{
			try
			{	
				//buffer for message
				byte[] buffer = new byte[512];

				//wait to recieve a client request
				DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
				sock.receive(packet);

				System.out.println("Packet recieved from: " + packet.getAddress());

				//takes recieved packet, converts it back to a String.
				String message = new String(packet.getData(), 0, packet.getLength());

				//decode the packet and get the appropriate node to send data to
				Node currNode = decode(packet, message);

				//send data to current node if there is data in its queue
				if((!(currNode==null)) && currNode.getMyQueue().getLength() > 0)
				{
					//retrives outgoing data and converts it to bytes
					buffer = (currNode.getMyQueue().deque()).getBytes();

					packet = currNode.getMyPack();
					packet = new DatagramPacket(buffer, buffer.length, packet.getAddress(), packet.getPort());
					
					sock.send(packet);
					System.out.println("Packet sent to: " + packet.getAddress());
				}

				System.out.println();
			}

			catch(IOException e)
			{
				System.out.println("There was an io exception.");
				break;
			}
		}

		sock.close();
	}
}