
public class CheckersPieces {
	   public static final int EMPTY = 0;
	   public static final int RED = 1;
	   public static final int BLACK = 2;
	        

	   public static int[][] checkersBoardArray;   
	   // this 2d array will represent the rows and collumns of the checkers board 

	   public CheckersPieces() {

	      checkersBoardArray = new int[8][8];
	      setUpGame();
	   }
	   
	   public void setUpGame() {
	   
	      for (int rows = 0; rows < 8; rows++) {
	         for (int collumns = 0; collumns < 8; collumns++) {
	            if ( rows % 2 == collumns % 2 ) {
	               if (rows < 3)
	                  checkersBoardArray[rows][collumns] = BLACK;
	               else if (rows > 4)
	                  checkersBoardArray[rows][collumns] = RED;
	               else
	                  checkersBoardArray[rows][collumns] = EMPTY;
	            }
	            else {
	               checkersBoardArray[rows][collumns] = EMPTY;
	            }
	         }
	      }
	   }
	   public int locationOfPiece(int rows, int collumns) {
	       
	       return checkersBoardArray[rows][collumns];
	   }
	   
	   public void makeMove(CheckersMove move) {
	         // Make the specified move.  It is assumed that move
	         // is non-null and that the move it represents is legal.
	      makeMove(move.fromRow, move.fromCol, move.toRow, move.toCol);
	   }
	  // this method is supposed to make a miove  
	   public void makeMove(int fromRow, int fromCol, int toRow, int toCol) {
	       
	      checkersBoardArray[toRow][toCol] =   checkersBoardArray[fromRow][fromCol];
	      checkersBoardArray[fromRow][fromCol] = EMPTY;
	      if (fromRow - toRow == 2 || fromRow - toRow == -2) {
	            // The move is a jump.  Remove the jumped piece from the board.
	         int jumpRow = (fromRow + toRow) / 2;  // Row of the jumped piece.
	         int jumpCol = (fromCol + toCol) / 2;  // Column of the jumped piece.
	         checkersBoardArray[jumpRow][jumpCol] = EMPTY;
	      }
	    
	   
	   }

	
	   

	 
}

