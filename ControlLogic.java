/**
*Class acts as part of the back end for checkers game. Makes use of Checker and MoveVerifier.
*@version 1.1
*@author Dan Martineau
*/
public class ControlLogic
{

	/*FIELDS*/
	private Checker[][] board; 		//board array object
	private MoveVerifier mover;		//instance of move verifier
	private boolean mustJump;		//whether or not a player must make a jump
	private int myScore;			//player's score
	private int pieces;				//number of pieces player has on board. Should be 12 to start.
	private boolean myTurn;			//whether or not it is player's turn

	/**
	*Constructor--No args
	*/
	public ControlLogic(boolean myTurn)
	{
		board = new Checker[8][8];
		mover = new MoveVerifier();

		mustJump = false;
		myScore = 0;
		pieces = 12;
		this.myTurn = myTurn;

		initializeBoard();
	}

	/**
	*Use this method to make a move
	*@param previous row coordinate
	*@param previous coloumn coordinate
	*@param current row coordinate
	*@param current coloumn coordinate
	*@param true if player's piece should be moved, false if opponent's piece should be moved
	*@return true if piece was moved
	*/
	protected boolean move(byte prevA, byte prevB, byte currA, byte currB, boolean whom)
	{
		boolean move = false;
		boolean king  = false;
		
		if(board[prevA][prevB] != null)
		{
			//if the piece belongs to this player
			if(board[prevA][prevB].getOwner() && whom)
			{
				king = board[prevA][prevB].getKing();

				//test to see if the move to commit is a jump
				if(mover.isJump(prevA, prevB, currA, currB, king, false))
				{
					//move piece in MoveVerifier()
					move = mover.move(prevA, prevB, currA, currB, king);

					//commit move on this board
					board[currA][currB] = board[prevA][prevB];
					board[prevA][prevB] = null;
					
					if(currA==prevA+2 && king)
					{
						if(currB==prevB+2)
							board[prevA+1][prevB+1] = null;
						else if(currB==prevB-2)
							board[prevA+1][prevB-1] = null;
					}
					else if(currA==prevA-2)
					{
						if(currB==prevB+2)
							board[prevA-1][prevB+1] = null;
						else if(currB==prevB-2)
							board[prevA-1][prevB-1] = null;
					}
					
					
					//see if player can jump again
					if(mover.otherJump(currA, currB, king))
					{
						System.out.println("You must jump again.");
						mustJump = true;
					}
					else
					{
						mustJump = false;
						
						//opponent's turn
						myTurn = false;
					}

					//add a point to score for jumping
					myScore++;
				}
				else
				{
					//test to see if player must jump
					mustJump = mover.canJump(prevA, prevB, king);
					
					if(mustJump)
					{
						System.out.println("You must jump your opponent's piece!");
						return move;
					}

					move = mover.move(prevA, prevB, currA, currB, king);
					
					//if move is valid, commit it.
					if(move)
					{
						board[currA][currB] = board[prevA][prevB];
						board[prevA][prevB] = null;
						
						//opponent's turn
						myTurn = false;
					}
				}
				
				//analyze status
				if(board[currA][currB] != null)
					status(prevA, prevB, currA, currB, board[currA][currB].getOwner());
			}
			
			//if the piece belongs to the opponent
			else if(!board[prevA][prevB].getOwner() && !whom)
			{
				king = board[prevA][prevB].getKing();
				
				//test to see if the move to commit is a jump
				if(mover.isJump(prevA, prevB, currA, currB, king, false))
				{
					move = mover.move(prevA, prevB, currA, currB, king);

					//commit move
					board[currA][currB] = board[prevA][prevB];
					board[prevA][prevB] = null;
					
					if(currA==prevA+2)
					{
						if(currB==prevB+2)
							board[prevA+1][prevB+1] = null;
						else if(currB==prevB-2)
							board[prevA+1][prevB-1] = null;
					}
					else if(currA==prevA-2 && king)
					{
						if(currB==prevB+2)
							board[prevA-1][prevB+1] = null;
						else if(currB==prevB-2)
							board[prevA-1][prevB-1] = null;
					}
					
					//analyze status
					if(board[currA][currB] != null)
						status(prevA, prevB, currA, currB, board[currA][currB].getOwner());
				}
				
				else
				{
					move = mover.move(prevA, prevB, currA, currB, king);
						
					//if move is valid, commit it.
					if(move)
					{
						board[currA][currB] = board[prevA][prevB];
						board[prevA][prevB] = null;
					}
					else
					{
						System.out.println("Opponent tried to make an invalid move: be suspicious!");
					}
					
					//analyze status
					if(board[currA][currB] != null)
						status(prevA, prevB, currA, currB, board[currA][currB].getOwner());
				}
			}
		}
		
		return move;
	}

	/**
	*Determines if there are any moves left
	*@return true if there are moves left for current player (not opponent)
	*/
	protected boolean movesLeft()
	{
		boolean move = false;
		
		for(byte i = 0; i < 8; i++)
		{
			for(byte j = 0; j < 8; j++)
			{
				if(canMove(i, j))
					move = true;
			}
		}
		
		return move;
	}
	
	/**
	*Helper for movesLeft()
	*Method returns true if a particualr piece can move
	*@param current row
	*@param current coloumn
	*@return true or false
	*/
	private boolean canMove(byte currA, byte currB)
	{
		boolean move = false;
		
		//make sure the current space isn't empty and that it belongs to current player
		if(board[currA][currB] != null && board[currA][currB].getOwner())
		{
			boolean king = board[currA][currB].getKing();
			boolean jump = mover.otherJump(currA, currB, king);
			
			if(jump)
			{
				move = true;
			}
			else
			{
				if(currA<7 && currB<7 && board[currA+1][currB+1] == null && mover.playableSquare((byte)(currA+1), (byte)(currB+1)) && king)
					move = true;
				else if(currA<7 && currB>0 && board[currA+1][currB-1] == null && mover.playableSquare((byte)(currA+1), (byte)(currB-1)) && king)
					move = true;
				else if(currA>0 && currB>0 && board[currA-1][currB-1] == null && mover.playableSquare((byte)(currA-1), (byte)(currB-1)))
					move = true;
				else if(currA>0 && currB<7 && board[currA-1][currB+1] == null && mover.playableSquare((byte)(currA-1), (byte)(currB+1)))
					move = true;
			}
		}
		
		return move;
	}

	/**
	*Method returns the player's score
	*@return score
	*/
	protected int getMyScore()
	{
		return myScore;
	}
	
	/**
	*Method returns who's turn it is
	*@return my turn or not
	*/
	protected boolean getMyTurn()
	{
		return myTurn;
	}

	/**
	*Method will tell if player needs to make a jump
	*@return mustJump
	*/
	protected boolean getJumpStatus()
	{
		return mustJump;
	}
	
	/**
	*Method sets who's turn it is to player's turn
	*/
	protected void setMyTurn()
	{
		myTurn = true;
	}
	
	/**
	*Passthrough for isJump() in MoveVerifier()
	*/
	protected boolean isJump(byte prevA, byte prevB, byte currA, byte currB, boolean change)
	{
		boolean king = false;
		
		if(board[prevA][prevB] != null)
			king = board[prevA][prevB].getKing();
		
		return mover.isJump(prevA, prevB, currA, currB, king, change);
	}

	/**
	*Method removes checker when player has been jumped
	*@param jumped row coordinate
	*@param jumped coloumn coordinate
	*/
	protected void gotJumped(byte posA, byte posB)
	{
		//make sure that we're removing one of our pieces
		if(board[posA][posB].getOwner())
		{
			board[posA][posB] = null;
			mover.gotJumped(posA, posB);
		}
		else
			System.out.println("You just tried to remove a jumped checker that is not your own!");
	}
	
	/**
	*Method will convert a player's coordinates to opponent's coordinates
	*@param input string of coordinates
	*@return output string of coordinates
	*/
	protected String getOppCoordinates(String input)
	{
		String output = null;
		
		if(input.length() <= 3)
		{
			output = getOppCoorHelper(input);
		}
		else if(input.length() >= 5)
		{
			output = getOppCoorHelper(input.substring(0,2));
			output += getOppCoorHelper(input.substring(3));
		}
		
		return output;
	}
	
	/**
	*Helper for getOppCoordinates()
	*@param input string
	*@return output string 
	*/
	private String getOppCoorHelper(String in)
	{
		String out = "";
		
		if(in.charAt(0)==0)
			out += 7;
		else if(in.charAt(0)==1)
			out += 6;
		else if(in.charAt(0)==2)
			out += 5;
		else if(in.charAt(0)==3)
			out += 4;
		else if(in.charAt(0)==4)
			out += 3;
		else if(in.charAt(0)==5)
			out += 2;
		else if(in.charAt(0)==6)
			out += 1;
		else if(in.charAt(0)==7)
			out += 0;
	
		if(in.charAt(1)==0)
			out += 7;
		else if(in.charAt(1)==1)
			out += 6;
		else if(in.charAt(1)==2)
			out += 5;
		else if(in.charAt(1)==3)
			out += 4;
		else if(in.charAt(1)==4)
			out += 3;
		else if(in.charAt(1)==5)
			out += 2;
		else if(in.charAt(1)==6)
			out += 1;
		else if(in.charAt(1)==7)
			out += 0;
		
		return out;
	}

	/**
	*Method will assess what a move means in the context of the game
	*e.g. jump, king, win, loss, etc.
	*@param previous row coordinate
	*@param previous coloumn coordinate
	*@param current row coordinate
	*@param current coloumn coordinate
	*@param if the piece belongs to this player
	*/
	protected void status(byte prevA, byte prevB, byte currA, byte currB, boolean mine)
	{
		kingPiece(currA, currB);
	}

	/*********************************Helper methods for status********************************************/
	/**
	*Method will determine if a piece is kinged
	*/
	private void kingPiece(byte currA, byte currB)
	{
		//if the piece moves into opponent's home row, king it.
		if(currA==0 && !board[currA][currB].getKing())
		{
			board[currA][currB].kingMe();
			System.out.println("You have been kinged!");
			
			//add 2 to score
			myScore+=2;
		}
		else if(currA==7 && !board[currA][currB].getKing())
		{
			board[currA][currB].kingMe();
		}
	}
	/**********************************************************************************************************/

	/**
	*Method fills board with pieces at start of game
	*/
	private void initializeBoard()
	{
		//initialize opponent pieces
		for(byte i = 0; i < 3; i++)
		{
			for(byte j = 0; j < 8; j++)
			{
				if(mover.playableSquare(i, j))
				{
					Checker checker = new Checker(false);
					board[i][j] = checker;
				}
			}
		}

		//initialize player pieces
		for(byte i = 5; i < 8; i++)
		{
			for(byte j = 0; j < 8; j++)
			{
				if(mover.playableSquare(i, j))
				{
					Checker checker = new Checker(true);
					board[i][j] = checker;
				}
			}
		}
	}

	//for testing purposes only
	public void printBoard()
	{
		System.out.println();
		for(byte i = 0; i < 8; i++)
		{
			for(byte j = 0; j < 8; j++)
			{
				if(board[i][j] != null)
					System.out.print(" " + board[i][j].toString() + " ");
				else
					System.out.print(" - ");
			}
			System.out.println();
		}
	}
	
	//for testing purposes only
	public String toString()
	{
		String str = "\n";
		
		for(byte i = 0; i < 8; i++)
		{
			for(byte j = 0; j < 8; j++)
			{
				if(board[i][j] != null)
					str+=("  " + board[i][j].toString() + "  ");
				else
					str+=("  _  ");
			}
			str+=("\n");
		}
		
		return str;
	}
}