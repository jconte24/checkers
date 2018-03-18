import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.List;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.ArrayList;

import javax.swing.JPanel;

public class Board extends JPanel {
	
	private final static int DIMENSION = 80;
	// this is a variable that holds a value for the dimension of our checkers pieces 
	private final static int SQUAREDIM= (int) (DIMENSION*1.25);
	// This value will be used for the dimensions of our square which are 25% bigger than our checks pieces
	 Color colorRed= Color.RED;
	 Color colorBlack=Color.BLACK;

	   private final int BOARDDIM = 8 * SQUAREDIM;
     CheckersPieces board;
     private int oldcx, oldcy;
     private boolean inDrag = false;
     private int deltax, deltay;
     int currentPlayer;
     int selectedRow, selectedCol;
     private Dimension dimPrefSize;
     
     CheckersMove[] legalMoves;
     
     
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        drawCheckersBoard(g);
     
    }
    
  public Board() {
	  board = new CheckersPieces();
	  board.setUpGame();
	  

	  
  }

     
/*
 * This method uses the Graphics class to paint the board of our checkers Game 
 */
    private void drawCheckersBoard(Graphics g) {

        Graphics2D g2d = (Graphics2D) g;

        RenderingHints rh
                = new RenderingHints(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);

        rh.put(RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY);

        g2d.setRenderingHints(rh);

        /*this is to center the screen but for some reasons cant get it to work at the monent 
         * Dimension size = getSize();
        
        double w = size.getWidth();
        double h = size.getHeight(); */

        g2d.setStroke(new BasicStroke(1));
      
      // lets draw the rowss of our checkers board   and the pieces 
  
        
        /* Draw the squares of the checkerboard and the checkers. */
        
        for (int rows = 0; rows < 8; rows++) {
          //  g.setColor(((rows & 1) != 0) ? Color.BLACK : Color.WHITE);

           for (int columns = 0; columns < 8; columns++) {
                if ( rows % 2 == columns % 2 )
                  g.setColor(Color.GRAY);
                else
                 g.setColor(Color.WHITE);
               g.fillRect(2 + columns*SQUAREDIM, 2 + rows*SQUAREDIM, SQUAREDIM, SQUAREDIM);
               switch (board.locationOfPiece(rows,columns)) {
                  case CheckersPieces.RED:
                     g.setColor(Color.red);
                     g.fillOval(4 + columns*100, 4 + rows*100, DIMENSION, DIMENSION);
                     break;
                  case CheckersPieces.BLACK:
                     g.setColor(Color.black);
                     g.fillOval(4 + columns*100, 4 + rows*100, DIMENSION, DIMENSION);
                     break;
               
                    
               }
               
           }
           
           
           
        }
        
        
        
     
     

}
    void doClickSquare(int row, int col) {
   

     for (int i = 0; i < legalMoves.length; i++)
        if (legalMoves[i].fromRow == row && legalMoves[i].fromCol == col) {
           selectedRow = row;
           selectedCol = col;
           if (currentPlayer == CheckersPieces.RED)
         
              
           repaint();
           return;
        }

     if (selectedRow < 0) {
       
         return;
     }
     
   

     for (int i = 0; i < legalMoves.length; i++)
        if (legalMoves[i].fromRow == selectedRow && legalMoves[i].fromCol == selectedCol
                && legalMoves[i].toRow == row && legalMoves[i].toCol == col) {
           movepieces(legalMoves[i]);
           return;
        }
   
   

  } 
  

  void movepieces(CheckersMove move) {
       
     board.makeMove(move);
     
   
     
     if (move.isJump()) {

        if (legalMoves != null) {
           if (currentPlayer == CheckersPieces.RED)
           
          
           selectedRow = move.toRow; 
           selectedCol = move.toCol;
           repaint();
           return;
        }
     }
     
  
     
     if (legalMoves != null) {
        boolean sameStartSquare = true;
        for (int i = 1; i < legalMoves.length; i++)
           if (legalMoves[i].fromRow != legalMoves[0].fromRow
                                || legalMoves[i].fromCol != legalMoves[0].fromCol) {
               sameStartSquare = false;
               break;
           }
        if (sameStartSquare) {
           selectedRow = legalMoves[0].fromRow;
           selectedCol = legalMoves[0].fromCol;
        }
     }
     
     
     repaint();
     
  }  // 


    


    public void mousePressed(MouseEvent evt) {
     
     
        int col = (evt.getX() - 2) / 20;
        int row = (evt.getY() - 2) / 20;
        if (col >= 0 && col < 8 && row >= 0 && row < 8)
           doClickSquare(row,col);
     
  }
  

  public void mouseReleased(MouseEvent evt) { }
  public void mouseClicked(MouseEvent evt) { }
  public void mouseEntered(MouseEvent evt) { }
  public void mouseExited(MouseEvent evt) { }

       

     }  
    
   


   
        
    
