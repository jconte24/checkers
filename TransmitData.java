import java.util.*;
import java.util.concurrent.TimeUnit;
import java.io.*;
import java.net.*;

/**
*This is the hub of the networking module for the LSDJ Checkers project
*@version 2
*@author Dan Martineau
*/
public class TransmitData //extends Thread
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
		//start();
		run();
	}

	/**
	*Inherits from Thread class
	*Will send and recieve data from the server
	*/
	//@Override
	protected void run()
	{
		//for(;;)
		//{
			try
			{
				long startTime;
				int count = 0;
				boolean action = false;		//wheter or not data was recieved or sent during this itteration
				
				String hold = "";
				String message = "";
				
				//There may be times when there's nothing to send or recieve, so this loop prevents getting hung up on receiving.
				startTime = System.currentTimeMillis();
				while(count == 0 && (System.currentTimeMillis() - startTime) < 1250)
				{
					if(outgoing.getLength() > 0)
					{
						hold = outgoing.deque();
						//System.out.println("Message being sent: " + hold);
						buffer = hold.getBytes();
						packet = new DatagramPacket(buffer, buffer.length, ip, PORT);
						sock.send(packet);
						
						action = true;
					}
					
					count++;
				}
				
				//reset count
				count = 0;

				//There may be times when there's nothing to send or recieve, so this loop prevents getting hung up on receiving.
				startTime = System.currentTimeMillis();
				while(count == 0 && (System.currentTimeMillis() - startTime) < 1250)
				{
					buffer2 = new byte[512];
					packet = new DatagramPacket(buffer2, buffer2.length);
					sock.receive(packet);
					
					message = new String(packet.getData(), 0, packet.getLength());
					//System.out.println("Message received: " + message);
					
					action = true;

					count++;
				}
				
				//ensure that there was sending/recieving of data. Otherwise, a bunch of junk will get enqued
				if(action)
				{
					//send new opponent string into gotNewOpponent method
					if(message.equals(newOppStr) || (message.substring(6,7).equals("o") && message.substring(8,9).equals("t")))
						gotNewOpponent(message);
					//if the other opponent disconnects
					else if((message.substring(6,7)).equals("d"))
						lostOpponent();
					
					//enque the message
					if(!message.equals(""))
						incoming.enque(message);

					if(message.length() > 4 && (message.substring(6,7)).equals("i"))
						myID = message.substring(2,5);
					
					//Update Strings for username change--must be here after myID update
					updateStrings();
				}
			}

			catch(IOException e)
			{
				System.out.println(e);
			}

		//}
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
		run();
	}

	/**
	*Requests opponent status (whether or not this player has one)
	*/
	protected void opponentStatus()
	{
		outgoing.enque(oppStatStr);
		run();
	}

	/**
	*Requests new Opponent
	*/
	protected void newOpponent()
	{
		outgoing.enque(newOppStr);
		run();
	}

	/**
	*Terminates the connection with the current instance
	*/
	protected void terminateConn()
	{
		outgoing.enque(termStr);
		run();
	}

	/**
	*Will return true if player has opponent (this just returns a boolean instead of the id)
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
	
	/**
	*Method returns player's id
	*@return myID
	*/
	protected String getMyID()
	{
		return myID;
	}

	//==============================PRIVATE METHODS===============================================

	/**
	*Method will update Strings when the default username is changed
	*This is done because Strings are immutable and will not auto-update
	*/
	private void updateStrings()
	{
		joinStr = ("s " + myID + " n");			
		oppStatStr = ("s " + myID + " o");		
		newOppStr = ("s " + myID + " n");		
		termStr = ("s " + myID + " t");			
	}

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