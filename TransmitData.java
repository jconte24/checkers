import java.util.*;
import java.io.*;
import java.net.*;

/**
*This is the hub of the networking module for the LSDJ Checkers project
*@version 1
*@author Dan Martineau
*/
public class TransmitData extends Thread
{

	//Fields
	private String myID = "jjj";			//default id that will prompt server handhake when passed
	private Queue incoming;					//holds all of the incoming commands
	private Queue outgoing;					//holds all of the outgoing commands
	private final int PORT = 4444;			//port used for the game
	private boolean engaged;				//whether or not this instance is connected to an opponent's instance
	private String oppID;					//opponent's ID
	private DatagramSocket sock;			//our datagram socket
	private DatagramPacket packet;			//datagram packet for sending and receiving
	private InetAddress ip;					//dhcp of the server
	private byte[] buffer;					//main buffer for the program
	private byte[] buffer2;					//secondary buffer for the program



	/*---------------------------------------------String Command Strings------------------------------------------------*/
	private String joinStr = ("s " + myID + " n");			//join server
	private String oppStatStr = ("s " + myID + " o");		//inquire about whether or not player has opponent
	private String newOppStr = ("s " + myID + " n");		//request new opponent
	private String termStr = ("s " + myID + " t");			//terrminate connection;
	/*-------------------------------------------------------------------------------------------------------------------*/



	/**
	*Constructor
	*@param the dhcp address of the server
	*/
	public TransmitData(String dhcp) throws Exception
	{
		sock = new DatagramSocket();
		ip = InetAddress.getByName(dhcp);
		incoming = new Queue();
		outgoing = new Queue();
		oppID = null;
		engaged = false;
		joinServer();
		start();
	}

	/**
	*Inherits from Thread class
	*Will send and recieve data from the server
	*/
	@Override
	public void run()
	{
		while(true)
		{	
			try
			{
				String hold = "";
				String message = "";

				if(outgoing.getLength() > 0)
				{	
					hold = outgoing.deque();
					buffer = hold.getBytes();
					packet = new DatagramPacket(buffer, buffer.length, ip, PORT);
					sock.send(packet);
					

					/*//Terminate program if termStr is sent
					if(hold.equals(termStr));
						System.exit(0);*/
				}

				//There may be times when there's nothing to send or recieve, so this loop prevents getting hung up on receiving.
				long startTime = System.currentTimeMillis(); //fetch starting time
				int count = 0;
				while(count == 0 && (System.currentTimeMillis() - startTime) < 1250)
				{
					buffer2 = new byte[512];
					packet = new DatagramPacket(buffer2, buffer2.length);
					sock.receive(packet);

					message = new String(packet.getData(), 0, packet.getLength());

					count++;
				}
				
				//send new opponent string into gotNewOpponent method or enque message if it is not a newOppStr
				/*if(hold.equals(newOppStr))
					gotNewOpponent(message);
				else*/
					incoming.enque(message);
					
				//if(((incoming.peek()).substring(6,7)).equals("i"))
					myID = (incoming.peek()).substring(2,5);
			}

			catch(IOException e)
			{
				System.out.println(e);
			}
				
		}
	}

	/*-----------------------------------------------Queue-mutating Methods-----------------------------------------------*/


	//PROTECTED METHODS

	/**
	*Gets data (commands and whatnot) from the opponent's instance
	*@return the data/command from the opponent
	*/
	protected String getData()
	{
		return incoming.deque();
	}

	/**
	*Sends data/commands to the opponent's instance
	*@param data/command to send
	*/
	protected void sendData(String command)
	{
		outgoing.enque(command);
	}

	/**
	*Requests opponent status (whether or not this player has one)
	*/
	protected void opponentStatus()
	{
		System.out.println(oppStatStr);
		outgoing.enque(oppStatStr);
	}

	/**
	*Requests new Opponent
	*/
	protected void newOpponent()
	{
		outgoing.enque(newOppStr);
	}

	/**
	*Terminates the connection with the current instance
	*/
	protected void terminateConn()
	{
		outgoing.enque(termStr);
	}

	/**
	*Will return true if player has opponent
	*@return true or false
	*/
	protected boolean engagementStatus()
	{
		return engaged;
	}

	/**
	*Method returns the id of opponent
	*@return oppID
	*/
	protected String getOppID()
	{
		return oppID;
	}

	//PRIVATE METHODS



	/**
	*Requests to join server
	*/
	private void joinServer() throws Exception
	{
		outgoing.enque(joinStr);
	}

	/**
	*Sets appropriate fields to aknowledge opponent
	*@param data string
	*/
	private void gotNewOpponent(String data)
	{
		engaged = true;
		oppID = data.substring(9,12);
	}

	/**
	*Method will set appropriate fields to aknowledge loss of opponent
	*/
	private void lostOpponent()
	{
		engaged = false;
		oppID = null;
	}
}