/**
*This class is the main class in the back end of the checkers game. It Implements ControlLogic and TransmitData. It also inherits from Thread.
*@version 1.0
*@author Dan Martineau
*/

import java.util.concurrent.TimeUnit;

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
		network = new TransmitData(ip);
		myScore = 0;
		mustJump = false;
		
		//makes sure that TransmitData is fully initialized so that our data will be accurate
		do
		{
			myID = network.getMyID();
			oppID = network.getOppID();
			engaged = network.engagementStatus();
		}while(!network.initialized());
		
		//decide who begins
		if(oppID == null || Integer.parseInt(network.getMyID()) < Integer.parseInt(network.getOppID()))
			control = new ControlLogic(true);
		else
			control = new ControlLogic(false);
		
		//begin thread
		start();
	}

	/**
	*Method will move a piece--USE EXTERNALLY FROM THIS CLASS (in this class use control.move() only)
	*@param previous row coordinate
	*@param previous coloumn coordinate
	*@param current row coordinate
	*@param current coloumn coordinate
	*@return true if piece was moved
	*/
	protected boolean move(byte prevA, byte prevB, byte currA, byte currB)
	{
		//don't allow a move if it's not player's turn--this is one reason not to use this method internally
		if(!control.getMyTurn())
			return false;

		boolean move = false;
		boolean jump = false;

		//set jump to true if the next move a jump
		jump = control.isJump(prevA, prevB, currA, currB, false);

		//move the piece
		move = control.move(prevA, prevB, currA, currB);

		//find out if player must jump again
		mustJump = control.getJumpStatus();



		//send the move command to the opponent's instance--yet another reason not to call this method internally
		byte[] prevByte = {prevA, prevB}; String prev = new String(prevByte);
		byte[] currByte = {currA, currB}; String curr = new String(currByte);

		if(jump && mustJump)
			network.sendData("c " + myID + " c " + oppID + " j " + control.getOppCoordinates(prev) + " " + control.getOppCoordinates(curr) + " f");
		else if(jump && !mustJump)
			network.sendData("c " + myID + " c " + oppID + " j " + control.getOppCoordinates(prev) + " " + control.getOppCoordinates(curr) + " t");
		else if(move && !jump)
			network.sendData("c " + myID + " c " + oppID + " m " + control.getOppCoordinates(prev) + " " + control.getOppCoordinates(curr));

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
	*Return whether or not it is player's turn
	*@return player's turn
	*/
	protected boolean getMyTurn()
	{
		return control.getMyTurn();
	}

	/**
	*Run method from Thread
	*/
	@Override
	public void run()
	{
		String hold = null;

		for(;;)
		{
			try
			{
				//check for incoming data and decode it if there is any.
				hold = network.getData();
			}
			catch(Exception e)
			{
				System.out.println("There's been an exception recieving network data in ControlUnit.");
			}
			
			//decode revieved String
			if(hold != null)
				decode(hold);
			
			//make sure we have opponent
			network.engagementStatus();

			//reset hold
			hold = null;
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
				//extract coordinates
				String hold = data.substring(14,15);
				byte r1 = Byte.parseByte(hold);
				hold = data.substring(15,16);
				byte r2 = Byte.parseByte(hold);
				hold = data.substring(17,18);
				byte c1 = Byte.parseByte(hold);
				hold = data.substring(18,19);
				byte c2 = Byte.parseByte(hold);

				if(data.substring(12,13).equals("m"))
				{
					control.move(r1, r2, c1, c2);

					//it's my turn again
					control.setMyTurn();
				}
				else if(data.substring(12,13).equals("j"))
				{
					if(data.substring(20).equals("t"))
					{
						control.move(r1, r2, c1, c2);

						//it's my turn again
						control.setMyTurn();
					}
					else if(data.substring(20).equals("f"))
					{
						control.move(r1, r2, c1, c2);
					}
				}
				//REMOVE AFTER TEST STAGE
				printBoard();
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

	//for testing purposes only
	public void printBoard()
	{
		control.printBoard();
	}
}