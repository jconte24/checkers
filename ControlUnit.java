/**
*This class is the main class in the back end of the checkers game. It Implements ControlLogic and TransmitData. It also inherits from Thread.
*@version 1.0
*@author Dan Martineau
*/
public class ControlUnit extends Thread
{
	/*FIELDS*/
	private ControlLogic control;
	private TransmitData network;
	private String myID;
	private String oppID;
	private boolean engaged;
	private int myScore;
	private boolean mustJump;
	
	/**
	*Constructor
	*@param the DCHP of the game server
	*/
	public ControlUnit(String ip) throws Exception
	{
		control = new ControlLogic();
		network = new TransmitData(ip);
		myID = null;
		oppID = null;
		engaged = false;
		myScore = 0;
		mustJump = false;
		
		//begin thread
		start();
	}
	
	/**
	*Method will move a piece
	*@param previous row coordinate
	*@param previous coloumn coordinate
	*@param current row coordinate
	*@param current coloumn coordinate
	*@return true if piece was moved
	*/
	protected boolean move(byte prevA, byte prevB, byte currA, byte currB)
	{
		boolean move = false;
		boolean jump = false;
		
		//set jump to true if the next move a jump
		jump = control.isJump(prevA, prevB, currA, currB);
		
		//move the piece
		move = control.move(prevA, prevB, currA, currB);
		
		//find out if player must jump again
		mustJump = control.getJumpStatus();
		
		if(!move && !jump)
			System.out.println("You must jump again!");
		else if(move && jump && mustJump)
			network.sendData("c " + myID + " c " + oppID + " j " + control.getOppCoordinates("" + prevA + prevB) + control.getOppCoordinates("" + currA + currB) + " f");
		else if(move && jump && mustJump)
			network.sendData("c " + myID + " c " + oppID + " j " + control.getOppCoordinates("" + prevA + prevB) + control.getOppCoordinates("" + currA + currB) + " t");
		else if(move && !jump)
			network.sendData("c " + myID + " c " + oppID + " m " + control.getOppCoordinates("" + prevA + prevB) + control.getOppCoordinates("" + currA + currB));
		
		return move;
	}
	
	/**
	*Find a new opponent
	*/
	protected void newOpponent()
	{
		network.newOpponent();
	}
	
	/**
	*Terminate connection and end program.
	*/
	protected void endAndExit()
	{
		network.terminateConn();
		System.exit(0);
	}
	
	/**
	*Run method from Thread
	*/
	@Override 
	public void run()
	{
		for(;;)
		{
			try
			{
				//check for incoming data and decode it.
				decode(network.getData());
			}
			catch(Exception e)
			{
				continue;
			}
			
			//make sure we have opponent
			network.engagementStatus();
		}
	}
	
	/**
	*Method Decodes Recieved Strings and preforms actions based on their meanings
	*@param recieved String
	*/
	private void decode(String data)
	{
		//ensure data was intended for client
		if(data==null || data.length() < 6 || !(data.substring(0,1).equals("c")))
			return;
		else
		{
			if(data.substring(6,7).equals("d"))
			{
				//player has been disconnected from opponent
				engaged = false;
				oppID = null;
				System.out.println("\nPlayer has terminated the connection.");
			}
			else if(data.substring(6,7).equals("c"))
			{
				//player has recieved general data from opponent
				System.out.println("\nMessage from opponent: " + data.substring(11));
			}
			else if(data.substring(6,7).equals("o"))
			{
				//player has opponent
				if(data.substring(8,9).equals("t"))
				{
					engaged = true;
					oppID = data.substring(10,13);
					System.out.println("\nYou are paired with the opponent: " + oppID);
				}
				//player has no opponent
				else if(data.substring(8,9).equals("f"))
				{
					engaged = false;
					oppID = null;
					System.out.println("\nYou are not paired with any opponent.");
				}
			}
			else if(data.substring(6,7).equals("n"))
			{
				//new opponent has been found
				if(data.substring(8,9).equals("t"))
				{
					engaged = true;
					oppID = data.substring(10,13);
					System.out.println("\nOpponent found: " + oppID);
				}
				//no opponents available
				else if(data.substring(8,9).equals("f"))
				{
					engaged = false;
					oppID = null;
					System.out.println("\nThere are no opponents available at this time.");
				}
			}
			else if(data.substring(6,7).equals("i"))
			{
				//player has been initialized in the server
				myID = data.substring(2,5);
			}
		}
	}
}