import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;


import javax.swing.JPanel;

public class Board extends JPanel {
	
	private final static int DIMENSION = 80;
	// this is a variable that holds a value for the dimension of our checkers pieces 
	private final static int SQUAREDIM= (int) (DIMENSION*1.25);
	// This value will be used for the dimensions of our square which are 25% bigger than our checks pieces
	 Color colorRed= Color.RED;
	 Color colorBlack=Color.BLACK;

	 
	
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        drawCheckersBoard(g);
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
      
      // lets draw the rows of our checkers board  
        for (int boardRows = 0; boardRows < 8; boardRows++)
        {
           g.setColor(((boardRows & 1) != 0) ? Color.BLACK : Color.RED);
        // lets draw the columns
           for (int columns = 0; columns < 8; columns++)
        	  
           { 	   
              g.fillRect(columns * SQUAREDIM, boardRows * SQUAREDIM, SQUAREDIM, SQUAREDIM);
              g.setColor((g.getColor() == colorBlack) ?colorRed : colorBlack);
           }
        }
     }


   
        }
    
