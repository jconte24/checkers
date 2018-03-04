/**
*Class acts as part of the back end for checkers game. Makes use of Checker and MoveVerifier.
*@version 1.0
*@author Dan Martineau
*/
public class ControlLogic
{

	/*FIELDS*/
	private String ip;				//DHCP of server
	private Checker[][] board; 		//board array object
	private MoveVerifier mover;		//instance of move verifier
	private boolean mustJump;		//whether or not a player must make a jump
	private int myScore;			//player's score
	private int pieces;				//number of pieces player has on board. Should be 12 to start.

	/**
	*Constructor--No args
	*/
	public ControlLogic()
	{
		this.ip = ip;

		board = new Checker[8][8];
		mover = new MoveVerifier();

		mustJump = false;
		myScore = 0;
		pieces = 12;

		initializeBoard();
	}

	/**
	*Use this method to make a move
	*@param previous row coordinate
	*@param previous coloumn coordinate
	*@param current row coordinate
	*@param current coloumn coordinate
	*@param true if piece is a king
	*/
	protected void move(byte prevA, byte prevB, byte currA, byte currB)
	{
		boolean move = false;
		boolean king  = false;
		
		if(board[prevA][prevB] != null)
			king = board[prevA][prevB].getKing();

		//test to see if the move to commit is a jump
		if(mover.isJump(prevA, prevB, currA, currB, king))
		{
			//see if player can jump again
			if(mover.otherJump(currA, currB))
			{
				System.out.println("You must jump again.");
				mustJump = true;
			}
			else
				mustJump = false;

			//call VerifyMove to make sure move is valid
			move = mover.move(prevA, prevB, currA, currB, king);

			//if move is valid, commit it.
			if(move)
			{
				board[currA][currB] = board[prevA][prevB];
				board[prevA][prevB] = null;
			}

			//add a point to score for jumping
			myScore++;
		}
		else
		{
			//test to see if player must jump
			if(mustJump)
			{
				System.out.println("You must jump your opponent's piece!");
				return;
			}

			move = mover.move(prevA, prevB, currA, currB, king);
			
			//if move is valid, commit it.
			if(move)
			{
				board[currA][currB] = board[prevA][prevB];
				board[prevA][prevB] = null;
			}
		}

		//analyze status
		status(prevA, prevB, currA, currB);
	}

	/**
	*Determines if there are any moves left
	*@return true if there are moves left
	*/
	protected boolean movesLeft()
	{
		return false;
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
	*Method will tell if player needs to make a jump
	*@return mustJump
	*/
	protected boolean getJumpStatus()
	{
		return mustJump;
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

	/**Method will assess what a move means in the context of the game
	*e.g. jump, king, win, loss, etc.
	*@param previous row coordinate
	*@param previous coloumn coordinate
	*@param current row coordinate
	*@param current coloumn coordinate
	*/
	protected void status(byte prevA, byte prevB, byte currA, byte currB)
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
		}

		//add 2 to score
		myScore+=2;
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
	protected void printBoard()
	{
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
}