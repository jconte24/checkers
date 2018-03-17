/**
*This class is the main class in the back end of the checkers game. It Implements ControlLogic and TransmitData. It also inherits from Thread.
*@version 2.1
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
	private String prevString;		//string previously recieved
	private Queue out;				//Queue for outgoing Strings
	private Queue status;			//Queue for status updates
	private Queue chat;				//Queue for chat messages
	private Queue oppMoves;			//Queue for opponent moves

	/**
	*Constructor
	*@param the DCHP of the game server
	*/
	public ControlUnit(String ip) throws Exception
	{
		network = new TransmitData(ip);
		myScore = 0;
		mustJump = false;
		out = new Queue();
		status = new Queue();
		chat = new Queue();
		oppMoves = new Queue();
		engaged = false;

		//makes sure that TransmitData is fully initialized so that our data will be accurate
		do
		{
			myID = network.getMyID();
			oppID = network.getOppID();
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
		if(!control.getMyTurn() && !engaged)
			return false;

		boolean move = false;
		boolean jump = false;

		//set jump to true if the next move a jump
		jump = control.isJump(prevA, prevB, currA, currB, false);

		//move the piece
		move = control.move(prevA, prevB, currA, currB, true);

		//find out if player must jump again
		mustJump = control.getJumpStatus();



		//send the move command to the opponent's instance--yet another reason not to call this method internally
		byte[] prevByte = {prevA, prevB}; String prev = new String(prevByte);
		byte[] currByte = {currA, currB}; String curr = new String(currByte);

		if(jump && mustJump)
			out.enque("c " + myID + " c " + oppID + " j " + control.getOppCoordinates(prev) + " " + control.getOppCoordinates(curr) + " f");
		else if(!jump && mustJump)
			status.enque("You must jump your opponent's piece");
		else if(jump && !mustJump)
			out.enque("c " + myID + " c " + oppID + " j " + control.getOppCoordinates(prev) + " " + control.getOppCoordinates(curr) + " t");
		else if(move && !jump)
			out.enque("c " + myID + " c " + oppID + " m " + control.getOppCoordinates(prev) + " " + control.getOppCoordinates(curr));
		else if(!move && !jump)
			status.enque("Invalid move!");

		//see if there are anhy moves left
		if(!control.movesLeft())
		{
			System.out.println("\nThere are no moves left. Opponent wins by default.\n");
			out.enque("c " + myID + " c " + oppID + " l");
		}

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
	*Tells whether or not player is engaged
	*@return engagement status
	*/
	protected boolean engaged()
	{
		return engaged;
	}

	/**
	*Sends a chat message to opponent
	*@param message string
	*/
	protected void sendMessage(String message)
	{
		if(engaged)
			out.enque("c " + myID + " c " + oppID + " c " + message);
	}

	/**
	*Fetches chat messages
	*@return message or null
	*/
	protected String getChat()
	{
		return chat.deque();
	}

	/**
	*Fetches status updates
	*@return update or null
	*/
	protected String getStatus()
	{
		return status.deque();
	}

	/**
	*Fetches the coordinates of opponent's latest move
	*@return latest move or array of -1 sentinals
	*/
	protected byte[] getMove()
	{
		byte[] move = new byte[4];
		String coordinates = oppMoves.deque();

		if(coordinates==null)
		{
			for(int i = 0; i < 4; i++)
			{
				move[i] = -1;
			}
		}
		else
		{
			for(int i = 0; i < 4; i++)
			{
				move[i] = Byte.parseByte(coordinates.substring(i, i+1));
			}
		}

		return move;
	}

	/**
	*Returns the player's ID
	*@return myID
	*/
	protected String getMyID()
	{
		return myID;
	}

	/**
	*Returns the opponent's ID
	*@return oppID
	*/
	protected String getOppID()
	{
		return oppID;
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
				//check for incoming data
				hold = network.getData();
			}
			catch(Exception e)
			{
				System.out.println("Exception recieving in ControlUnit(): " + e);
			}

			//decode revieved String if it is not a repeat of a command
			//if(hold != null && (!hold.equals(prevString)))
			//{
				//prevString = hold;
				decode(hold);
			//}

			//make sure we have opponent
			network.engagementStatus();

			try
			{
				if(engaged && out.getLength() > 0)
					network.sendData(out.deque());
			}
			catch(Exception x)
			{
				System.out.println("Exception sending in ControlUnit(): " + x);
			}

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
				status.enque("Player has terminated the connection.");
			}
			else if(data.substring(6,7).equals("c"))
			{
				if(data.substring(12,13).equals("l"))
				{
					System.out.println("\nYou've won the game by default!\n");
					status.enque("You've won the game by default!");
				}

				else if(data.substring(12,13).equals("c"))
				{
					System.out.println("\nMessage from opponent: " + data.substring(14) + "\n");

					chat.enque(data.substring(14));
					status.enque("Message recieved from opponent.");
				}

				else
				{
					//extract coordinates
					String hold = data.substring(14,15);
					boolean moved = false;
					byte r1 = Byte.parseByte(hold);
					hold = data.substring(15,16);
					byte r2 = Byte.parseByte(hold);
					hold = data.substring(17,18);
					byte c1 = Byte.parseByte(hold);
					hold = data.substring(18,19);
					byte c2 = Byte.parseByte(hold);

					if(data.substring(12,13).equals("m"))
					{
						moved = control.move(r1, r2, c1, c2, false);

						//it's my turn again
						control.setMyTurn();

						if(moved)
							oppMoves.enque("" + r1 + r2 + c1 + c2);
					}
					else if(data.substring(12,13).equals("j"))
					{
						if(data.substring(20).equals("t"))
						{
							moved = control.move(r1, r2, c1, c2, false);

							//it's my turn again
							control.setMyTurn();

							if(moved)
								oppMoves.enque("" + r1 + r2 + c1 + c2);
						}
						else if(data.substring(20).equals("f"))
						{
							moved = control.move(r1, r2, c1, c2, false);

							if(moved)
								oppMoves.enque("" + r1 + r2 + c1 + c2);
						}
					}
					//REMOVE AFTER TEST STAGE
					printBoard();

					//see if there are anhy moves left for current player
					if(!control.movesLeft())
					{
						System.out.println("\nThere are no moves left. Opponent wins by default.\n");
						out.enque("c " + myID + " c " + oppID + " l");
						status.enque("There are no moves left. Opponent wins by default.");
					}
				}
			}
			else if(data.substring(6,7).equals("o"))
			{
				//player has opponent
				if(data.substring(8,9).equals("t"))
				{
					engaged = true;
					oppID = data.substring(10,13);
					System.out.println("\nYou are paired with the opponent: " + oppID);
					status.enque("You are paired with the opponent: " + oppID);
				}
				//player has no opponent
				else if(data.substring(8,9).equals("f"))
				{
					engaged = false;
					oppID = null;
					System.out.println("\nYou are not paired with any opponent.");
					status.enque("You are not paired with any opponent.");
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
					status.enque("Opponent found: " + oppID);

					boolean gotNew = false;

					//decide who begins
					if(oppID == null || Integer.parseInt(myID) < Integer.parseInt(oppID))
						control = new ControlLogic(true);
					else
						control = new ControlLogic(false);
				}
				//no opponents available
				else if(data.substring(8,9).equals("f"))
				{
					engaged = false;
					oppID = null;
					System.out.println("\nThere are no new opponents available at this time.");
					status.enque("There are no new opponents available at this time.");
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

	//for testing purposes only
	public String toString()
	{
		return control.toString();
	}
}