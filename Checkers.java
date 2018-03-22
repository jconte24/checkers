import java.awt.EventQueue;

import javax.swing.JFrame;

public class Checkers extends JFrame
{
   public Checkers(String title)throws Exception
   {
      super(title);

       Board board = new Board(this);

      setDefaultCloseOperation(EXIT_ON_CLOSE);
      addWindowListener(new java.awt.event.WindowAdapter() {
	              public void windowClosing(java.awt.event.WindowEvent evt) {
	                  board.formWindowClosing(evt);
	              }
        });
		
		board.fillBoard();

      
	  setContentPane(board);
      pack();
      setVisible(true);
	  
	  this.setTitle(title + "   |   Player: " + board.getMyID());
   }

   public static void main(String[] args)
   {
      Runnable r = new Runnable()
                   {
                      @Override
                      public void run()
                      {
						  try
						  {
                         	  new Checkers("Checkers");
					 	  }
					 	  catch(Exception e)
					 	  {
							  System.out.println(e);
						  }
                      }
                   };
      EventQueue.invokeLater(r);
   }
}