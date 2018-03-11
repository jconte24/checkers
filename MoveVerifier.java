/**
*This class will determine whether or not a move is valid
*@author Dan Martineau
*@version 1.1
*/

public class MoveVerifier
{
	/*FIELDS*/
	private byte[][] board;		//0 is empty, 1 is this player's checker, 2 is opponent's checker.
	
	public MoveVerifier()
	{
		board = new byte[8][8];
		initializeBoard();
	}
	
	/**
	*Call this method to request to move a piece. Method will return true if the move was completed.
	*@param previous row coordinate
	*@param previous coloumn coordinate
	*@param current row coordinate
	*@param current coloumn coordinate
	*@param true if piece is a king
	*@return true is move was completed
	*/
	protected boolean move(byte prevA, byte prevB, byte currA, byte currB, boolean king)
	{
		boolean validMove = false;
		boolean jump = false;
		
		//ensure move is valid
		validMove = verifyMove(prevA, prevB, currA, currB, king);
		jump = isJump(prevA, prevB, currA, currB, king, true);
		
		if(validMove)
		{
			board[currA][currB] = board[prevA][prevB];
			board[prevA][prevB] = 0;
			
			return true;
		}
		else if(jump)
		{
			board[currA][currB] = board[prevA][prevB];
			board[prevA][prevB] = 0;
			
			return true;
		}
		return false;
	}
	
	/**
	*Will see if a move is a jump
	*@param previous row coordinate
	*@param previous coloumn coordinate
	*@param current row coordinate
	*@param current coloumn coordinate
	*@param whether or not piece is king
	*@param true if method should alter, false if not
	*@return true if move is a jump
	*/
	protected boolean isJump(byte prevA, byte prevB, byte currA, byte currB, boolean king, boolean change)
	{
		boolean jump = false;
		
		if(playableSquare(currA, currB) && board[currA][currB] == 0 && board[prevA][prevB] == 1)
		{
			if(currA==prevA+2 && king)
			{
				if(currB==prevB+2)
				{
					if(board[prevA+1][prevB+1]==2)
					{
						if(change)
							board[prevA+1][prevB+1] = 0;
						jump = true;
					}
				}	
				else if(currB==prevB-2)
				{
					if(board[prevA+1][prevB-1]==2)
					{
						if(change)
							board[prevA+1][prevB-1] = 0;
						jump = true;
					}
				}
			}
			
			else if(currA==prevA-2)
			{
				if(currB==prevB+2)
				{
					if(board[prevA-1][prevB+1]==2)
					{
						if(change)
							board[prevA-1][prevB+1] = 0;
						jump = true;
					}
				}
				else if(currB==prevB-2)
				{
					if(board[prevA-1][prevB-1]==2)
					{
						if(change)
							board[prevA-1][prevB-1] = 0;
						jump = true;
					}
				}
			}
		}
		else if(playableSquare(currA, currB) && board[currA][currB] == 0 && board[prevA][prevB] == 2)
		{
			if(currA==prevA+2)
			{
				if(currB==prevB+2)
				{
					if(board[prevA+1][prevB+1]==1)
					{
						if(change)
							board[prevA+1][prevB+1] = 0;
						jump = true;
					}
				}	
				else if(currB==prevB-2)
				{
					if(board[prevA+1][prevB-1]==1)
					{
						if(change)
							board[prevA+1][prevB-1] = 0;
						jump = true;
					}
				}
			}
			
			else if(currA==prevA-2 && king)
			{
				if(currB==prevB+2)
				{
					if(board[prevA-1][prevB+1]==1)
					{
						if(change)
							board[prevA-1][prevB+1] = 0;
						jump = true;
					}
				}
				else if(currB==prevB-2)
				{
					if(board[prevA-1][prevB-1]==1)
					{
						if(change)
							board[prevA-1][prevB-1] = 0;
						jump = true;
					}
				}
			}
		}
		return jump;
	}
	
	/**
	*Will see if there is an additional jump
	*@param current row coordinate
	*@param current coloumn coordinate
	*@param whether or not piece is king
	*@return true if there is another jump
	*/
	protected boolean otherJump(byte currA, byte currB, boolean king)
	{
		boolean jump = false;
		
		if(currA<6 && currB<6 && board[currA+1][currB+1]==2 && playableSquare((byte)(currA+2), (byte)(currB+2)) && board[currA+2][currB+2] == 0 && king)
			jump = true;
		else if(currA<6 && currB>1 && board[currA+1][currB-1]==2 && playableSquare((byte)(currA+2), (byte)(currB-2)) && board[currA+2][currB-2] == 0 && king)
			jump = true;
		else if(currA>1 && currB>1 && board[currA-1][currB-1]==2 && playableSquare((byte)(currA-2), (byte)(currB-2)) && board[currA-2][currB-2] == 0)
			jump = true;
		else if(currA>1 && currB<6 && board[currA-1][currB+1]==2 && playableSquare((byte)(currA-2), (byte)(currB+2)) && board[currA-2][currB+2] == 0)
			jump = true;
		
		return jump;
	}
	
	/**
	*Will determine if there is a jump to be made (a player must jump if the oppertunity exists)
	*@param current row coordinate
	*@param current coloumn coordinate
	*/
	protected boolean canJump(byte currA, byte currB)
	{
		return false;
	}
	
	/**
	*Method removes checker when player has been jumped
	*@param jumped row coordinate
	*@param jumped coloumn coordinate
	*/
	protected void gotJumped(byte posA, byte posB)
	{
		//make sure that we're removing one of our pieces
		if(board[posA][posB]==1)
			board[posA][posB] = 0;
	}
	
	/**
	*Method will accept the coordinates (old and new) of a move and verify if it is valid.
	*It will return true or false based on the validity of the move.
	*If valid, it will update the local board array and send the move to the status module
	*@param previous row coordinate
	*@param previous coloumn coordinate
	*@param current row coordinate
	*@param current coloumn coordinate
	*@param true if piece is a king
	*@return true is move is valid
	*/
	private boolean verifyMove(byte prevA, byte prevB, byte currA, byte currB, boolean king)
	{
		boolean valid = true;
		
		//check to see if piece belongs to player
		if(board[prevA][prevB]==1)
		{
			if(!(playableSquare(currA, currB)))
				valid = false;
			
			if(!(diagonal(prevA, prevB, currA, currB, king, true)))
				valid = false;
			
			if(taken(currA, currB)==true)
				valid = false;
		}
		//check to see if piece belongs to opponent
		else if(board[prevA][prevB]==2)
		{
			if(!(playableSquare(currA, currB)))
				valid = false;
			
			if(!(diagonal(prevA, prevB, currA, currB, king, false)))
				valid = false;
			
			if(taken(currA, currB)==true)
				valid = false;
		}
		else
			valid = false;
		
		return valid;
	}
	
	/*********************************Helper methods for verifyMove***************************************/
	
	/**
	*Make sure piece moves diagonal and one row/coloumn at a time
	*@param previous row coordinate
	*@param previous coloumn coordinate
	*@param current row coordinate
	*@param current coloumn coordinate
	*@param true if piece is a king
	*@param false if opponent or true if player
	*@return true if diagonal
	*/
	private boolean diagonal(byte prevA, byte prevB, byte currA, byte currB, boolean king, boolean player)
	{
		boolean diagonal = false;
		
		//player moves
		if(player)
		{
			//down
			if(currA==prevA + 1 && king)
			{
				//left
				if(currB==prevB - 1)
					diagonal = true;
				//right
				else if(currB==prevB + 1)
					diagonal = true;
			}
			
			//up
			else if(currA==prevA - 1)
			{
				//left
				if(currB==prevB - 1)
					diagonal = true;
				//right
				else if(currB==prevB + 1)
					diagonal = true;
			}
		}
		//opponent moves
		else
		{
			//down
			if(currA==prevA + 1)
			{
				//left
				if(currB==prevB - 1)
					diagonal = true;
				//right
				else if(currB==prevB + 1)
					diagonal = true;
			}
			
			//up
			else if(currA==prevA - 1 && king)
			{
				//left
				if(currB==prevB - 1)
					diagonal = true;
				//right
				else if(currB==prevB + 1)
					diagonal = true;
			}
		}
		
		return diagonal;
	}
	
	/**
	*Make sure that there is no piece occupying new square
	*@param current row coordinate
	*@param current coloumn coordinate
	*@return true if taken 
	*/
	private boolean taken(byte currA, byte currB)
	{
		boolean taken = false;
		
		if(board[currA][currB] == 1 || board[currA][currB] == 2)
			taken = true;
			
		return taken;
	}
	
	/**
	*Make sure that player does not try to move to whitespace
	*@param current row coordinate
	*@param current coloumn coordinate
	*@return true if square is playable
	*/
	protected boolean playableSquare(byte currA, byte currB)
	{
		boolean row = false;
		boolean col  = false;
		boolean playable = false;
		
		if (((int)currA & 1) == 0)  //set row to true if even
			row = true;
		if (((int)currB & 1) == 0)  //set col to true if even
			col = true;
		
		if(row && col)
			playable = true;
		else if(!row && !col)
			playable = true;
		
		return playable;
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
				if(playableSquare(i, j))
					board[i][j] = 2;
			}
		}
		
		//initialize player pieces
		for(byte i = 5; i < 8; i++)
		{
			for(byte j = 0; j < 8; j++)
			{
				if(playableSquare(i, j))
					board[i][j] = 1;
			}
		}
	}
	
	//for testing purposes only
	public void printBoard()
	{
		for(byte i = 0; i < 8; i++)
		{
			for(byte j = 0; j < 8; j++)
			{
				System.out.print(board[i][j] + ", ");
			}
			System.out.println();
		}
	}
}