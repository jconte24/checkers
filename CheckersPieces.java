
public class CheckersPieces {
	   public static final int EMPTY = 0;
	   public static final int RED = 1;
	   public static final int BLACK = 2;
	        

	   private int[][] checkersBoardArray;   
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
	   

	 
}

