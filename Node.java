import java.net.*;

/**
 *Class will hold the attributes of a player.
 * @author Dan Martineau
 */

 public class Node
 {
	private String myID;			//player's id
	private InetAddress myIP;		//player's ip
    private int myPort;				//player's port
	private Queue myQueue;			//player's data queue
	private String oppID;			//opponent's id
    private DatagramPacket myPack;	//Datagram packet last sent from this client


	/**
    *Constructor.
	*@param The id of the player
	*@param The DHCP address of the player
	*@param The id of the opponent (-1 is unpaired)
	*@param The DHCP address of the opponent
	*/
	public Node(String myID, InetAddress myIP, int myPort, Queue myQueue, String oppID)
	{
		this.myID = myID;
		this.myIP = myIP;
		this.myPort = myPort;
		this.myQueue = myQueue;
		this.oppID = oppID;
		//this.oppIP = oppIP;
	}

	/**
	*Constructor.
	*@param The id of the player
	*@param The DHCP address of the player
	*@param The id of the opponent (-1 is unpaired)
	*@param The DHCP address of the opponent
	*/
	public Node(String myID, DatagramPacket myPack, Queue myQueue, String oppID)
	{
		this.myID = myID;
		this.myPack = myPack;
		this.myIP = myPack.getAddress();
		this.myPort = myPack.getPort();
		this.myQueue = myQueue;
		this.oppID = oppID;
	}

	/**
	*Get player id.
	*@return id
	*/
	public String getMyID()
	{
		return myID;
	}

	/**
	*Get player DHCP address.
	*@return ip
	*/
	public InetAddress getMyIP()
	{
		return myIP;
	}

    /**
	*Get player port number.
	*@return port
	*/
	public int getMyPort()
	{
		return myPort;
	}

	/**
	*Get player's data queue
	*@return the queue
	*/
	public Queue getMyQueue()
	{
		return myQueue;
	}

	/**
	*Get player's packet
	*@return the packet
	*/
	public DatagramPacket getMyPack()
	{
		return myPack;
	}

	/**
	*Get opponent id.
	*@return id
	*/
	public String getOppID()
	{
		return oppID;
	}

	/**
	*Change Opponent's ID--when player gets new opponent
	*@param new opponent's id
	*/
	public void setOppID(String id)
	{
		oppID = id;
	}

	/**
	*Method removes opponent credentials from node
	*/
	public void removeOpp()
	{
		oppID = null;
	}
 }

