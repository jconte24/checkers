import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseMotionAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JFrame;


public class Board extends JComponent
{
   // dimension of checkerboard square (25% bigger than checker)

   private final static int SQUAREDIM = (int) (Checker.getDimension() * 1.25);

   // dimension of checkerboard (width of 8 squares)

   private final int BOARDDIM = 8 * SQUAREDIM;

   // preferred size of Board component

   private Dimension dimPrefSize;

   // dragging flag -- set to true when user presses mouse button over checker
   // and cleared to false when user releases mouse button

   private boolean inDrag = false;

   // displacement between drag start coordinates and checker center coordinates

   private int deltax, deltay;

   // reference to positioned checker at start of drag

   private PosCheck posCheck;

   // center location of checker at start of drag

   private int oldcx, oldcy;

   // list of Checker objects and their initial positions

   private List<PosCheck> posChecks;

   //INSTANCE OF CONTROLUNIT
   private ControlUnit control;

   //flag for move
   private boolean move;

   //flag for opponent move
   private boolean moveOpp;

   //flag for player's turn
   private boolean myTurn;

   private String status;
   private String oldStatus;
   private byte[] oppMove;
   private boolean complete;
   private boolean connected;

   public Board(JFrame frame) throws Exception
   {
      posChecks = new ArrayList<>();
      dimPrefSize = new Dimension(BOARDDIM, BOARDDIM);

      String dhcp = JOptionPane.showInputDialog(frame, "Please input the DHCP address of GameServer: ");
	  if(dhcp!=null && !dhcp.equals(""))
			control = new ControlUnit(dhcp);
      move = true;
	  oldStatus = null;
      oppMove = new byte[4];
      oppMove[0] = (byte)-1;
	  complete = true;
	  
	  while(status==null)
	  {
		  status = control.getStatus();
		  if(status != null)
		  {
			  connected = true;
			  JOptionPane.showMessageDialog(frame, status, "Status Update", JOptionPane.INFORMATION_MESSAGE);
		  }
		  
		  try
			{
				TimeUnit.MILLISECONDS.sleep(500);
			}
			catch(Exception e)
			{
				System.out.println(e);
			}
	  }

	  Thread appThread = new Thread(new Runnable() {
	          @Override public void run() {

	              for(;;)
	  			  {
						oppMove = control.getMove();
						status = control.getStatus();
						
						//the second condition prevents a repeated status string, which can happen occasionally
						if(status != null && !status.equals(oldStatus))
						{
							JOptionPane.showMessageDialog(frame, status, "Status Update", JOptionPane.INFORMATION_MESSAGE);
							oldStatus = status;
						}
						if(oppMove[0] != -1)
						{
							move();
							System.out.print("\nWaiting for move to complete");
							while(!complete)
							{
								System.out.print(".");
							}
							System.out.println();
						}
						
						try
						{
							TimeUnit.MILLISECONDS.sleep(500);
						}
						catch(Exception e)
						{
							System.out.println(e);
						}
	              }
			  }
	          });

		appThread.start();

      addMouseListener(new MouseAdapter()
                       {
                          @Override
                          public void mousePressed(MouseEvent me)
                          {
                             // Obtain mouse coordinates at time of press.

                             int x = me.getX();
                             int y = me.getY();

                             // Locate positioned checker under mouse press.

                             for (PosCheck posCheck: posChecks)
                                if (Checker.contains(x, y, posCheck.cx,
                                                     posCheck.cy))
                                {
                                   Board.this.posCheck = posCheck;
                                   oldcx = posCheck.cx;
                                   oldcy = posCheck.cy;
                                   deltax = x - posCheck.cx;
                                   deltay = y - posCheck.cy;
                                   inDrag = true;
                                   return;
                                }
                          }

                          @Override
                          public void mouseReleased(MouseEvent me)
                          {
                             // When mouse released, clear inDrag (to
                             // indicate no drag in progress) if inDrag is
                             // already set.

                             if (inDrag)
                                inDrag = false;
                             else
                                return;

                             // Snap checker to center of square.

                             int x = me.getX();
                             int y = me.getY();

                             posCheck.cx = (x - deltax) / SQUAREDIM * SQUAREDIM +
                                           SQUAREDIM / 2;
                             posCheck.cy = (y - deltay) / SQUAREDIM * SQUAREDIM +
                                           SQUAREDIM / 2;

							
							//convert coordinates
                             byte newX = 0;
                             byte newY = 0;
                             switch(posCheck.cx)
                             {
								 case 31:
								 	newX = 0;
								 	break;
								 case 93:
								 	newX = 1;
								 	break;
								 case 155:
								 	newX = 2;
								 	break;
								 case 217:
								 	newX = 3;
								 	break;
								 case 279:
								 	newX = 4;
								 	break;
								 case 341:
								 	newX = 5;
								 	break;
								 case 403:
								 	newX = 6;
								 	break;
								 case 465:
								 	newX = 7;
							 }

							 switch(posCheck.cy)
							 {
								 case 31:
								 	newX = 0;
								 	break;
								 case 93:
								 	newY = 1;
								 	break;
								 case 155:
								 	newY = 2;
								 	break;
								 case 217:
								 	newY = 3;
								 	break;
								 case 279:
								 	newY = 4;
								 	break;
								 case 341:
									newY = 5;
									break;
								 case 403:
								 	newY = 6;
								 	break;
								 case 465:
								 	newY = 7;
							 }

							  byte oldX = 0;
							  byte oldY = 0;
							  switch(oldcx)
							  {
								 case 31:
									oldX = 0;
									break;
								 case 93:
									oldX = 1;
									break;
								 case 155:
									oldX = 2;
									break;
								 case 217:
									oldX = 3;
									break;
								 case 279:
									oldX = 4;
									break;
								 case 341:
									oldX = 5;
									break;
								 case 403:
									oldX = 6;
									break;
								 case 465:
									oldX = 7;
							 }

							 switch(oldcy)
							 {
								 case 31:
									oldY = 0;
									break;
								 case 93:
									oldY = 1;
									break;
								 case 155:
									oldY = 2;
									break;
								 case 217:
									oldY = 3;
									break;
								 case 279:
									oldY = 4;
									break;
								 case 341:
									oldY = 5;
									break;
								 case 403:
									oldY = 6;
									break;
								 case 465:
									oldY = 7;
							 }

							 move = control.move(oldY, oldX, newY, newX);

							for (PosCheck posCheck: posChecks)
								if (!move)
								{
								   Board.this.posCheck.cx = oldcx;
								   Board.this.posCheck.cy = oldcy;
								   move = true;
								}
							 posCheck = null;
							 repaint();
                          }
                       });


      // Attach a mouse motion listener to the applet. That listener listens
      // for mouse drag events.

      addMouseMotionListener(new MouseMotionAdapter()
                             {
                                @Override
                                public void mouseDragged(MouseEvent me)
                                {
                                   if (inDrag)
                                   {
                                      // Update location of checker center.


                                      posCheck.cx = me.getX() - deltax;
                                      posCheck.cy = me.getY() - deltay;

                                      repaint();
                                   }
                                }
                             });

   }
   
   protected void fillBoard()
		{
			if(connected)
			  {
				  add(new Checker(CheckerType.RED_REGULAR), 6, 2);
				  add(new Checker(CheckerType.RED_REGULAR), 6, 4);
				  add(new Checker(CheckerType.RED_REGULAR), 6, 6);
				  add(new Checker(CheckerType.RED_REGULAR), 6, 8);
				  add(new Checker(CheckerType.RED_REGULAR), 7, 1);
				  add(new Checker(CheckerType.RED_REGULAR), 7, 3);
				  add(new Checker(CheckerType.RED_REGULAR), 7, 5);
				  add(new Checker(CheckerType.RED_REGULAR), 7, 7);
				  add(new Checker(CheckerType.RED_REGULAR), 8, 2);
				  add(new Checker(CheckerType.RED_REGULAR), 8, 4);
				  add(new Checker(CheckerType.RED_REGULAR), 8, 6);
				  add(new Checker(CheckerType.RED_REGULAR), 8, 8);

				  add(new Checker(CheckerType.BLACK_REGULAR), 1, 1);
				  add(new Checker(CheckerType.BLACK_REGULAR), 1, 3);
				  add(new Checker(CheckerType.BLACK_REGULAR), 1, 5);
				  add(new Checker(CheckerType.BLACK_REGULAR), 1, 7);
				  add(new Checker(CheckerType.BLACK_REGULAR), 2, 2);
				  add(new Checker(CheckerType.BLACK_REGULAR), 2, 4);
				  add(new Checker(CheckerType.BLACK_REGULAR), 2, 6);
				  add(new Checker(CheckerType.BLACK_REGULAR), 2, 8);
				  add(new Checker(CheckerType.BLACK_REGULAR), 3, 1);
				  add(new Checker(CheckerType.BLACK_REGULAR), 3, 3);
				  add(new Checker(CheckerType.BLACK_REGULAR), 3, 5);
				  add(new Checker(CheckerType.BLACK_REGULAR), 3, 7);
			  }
		}
		
	protected String getMyID()
	{
		return control.getMyID();
	}

	protected void formWindowClosing(java.awt.event.WindowEvent evt) {
	       control.endAndExit();
    }

   public void add(Checker checker, int row, int col)
   {
      if (row < 1 || row > 8)
         throw new IllegalArgumentException("row out of range: " + row);
      if (col < 1 || col > 8)
         throw new IllegalArgumentException("col out of range: " + col);
      PosCheck posCheck = new PosCheck();
      posCheck.checker = checker;

      posCheck.cx = (col - 1) * SQUAREDIM + SQUAREDIM / 2;
      posCheck.cy = (row - 1) * SQUAREDIM + SQUAREDIM / 2;
      for (PosCheck _posCheck: posChecks)
         if (posCheck.cx == _posCheck.cx && posCheck.cy == _posCheck.cy)
            throw new AlreadyOccupiedException("square at (" + row + "," +
                                               col + ") is occupied");
      posChecks.add(posCheck);
   }


	public boolean move()
	   {
		   boolean m = false;
		   complete = false;

	      if (oppMove[0]==-1)
			  return m;


								int newX = 0;
	                             int newY = 0;
	                             switch((int)oppMove[3])
	                             {
									 case 0:
									 	newX = 31;
									 	break;
									 case 1:
									 	newX = 93;
									 	break;
									 case 2:
									 	newX = 155;
									 	break;
									 case 3:
									 	newX = 217;
									 	break;
									 case 4:
									 	newX = 279;
									 	break;
									 case 5:
									 	newX = 341;
									 	break;
									 case 6:
									 	newX = 403;
									 	break;
									 case 7:
									 	newX = 465;
								 }

								 switch((int)oppMove[2])
								 {
									 case 0:
									 	newX = 31;
									 	break;
									 case 1:
									 	newY = 93;
									 	break;
									 case 2:
									 	newY = 155;
									 	break;
									 case 3:
									 	newY = 217;
									 	break;
									 case 4:
									 	newY = 279;
									 	break;
									 case 5:
										newY = 341;
										break;
									 case 6:
									 	newY = 403;
									 	break;
									 case 7:
									 	newY = 465;
								 }

								  int oldX = 0;
								  int oldY = 0;
								  switch((int)oppMove[1])
								  {
									 case 0:
										oldX = 31;
										break;
									 case 1:
										oldX = 93;
										break;
									 case 2:
										oldX = 155;
										break;
									 case 3:
										oldX = 217;
										break;
									 case 4:
										oldX = 279;
										break;
									 case 5:
										oldX = 341;
										break;
									 case 6:
										oldX = 403;
										break;
									 case 7:
										oldX = 465;
								 }

								 switch(oppMove[0])
								 {
									 case 0:
										oldY = 31;
										break;
									 case 1:
										oldY = 93;
										break;
									 case 2:
										oldY = 155;
										break;
									 case 3:
										oldY = 217;
										break;
									 case 4:
										oldY = 279;
										break;
									 case 5:
										oldY = 341;
										break;
									 case 6:
										oldY = 403;
										break;
									 case 7:
										oldY = 465;
								 }

	      for (PosCheck posCheckA: posChecks)
			if (posCheckA.cx == oldX && posCheckA.cy == oldY)
			{
			   posChecks.remove(posCheckA);
			   break;
			}

		  PosCheck posCheck2 = new PosCheck();
	      posCheck2.checker = new Checker(CheckerType.BLACK_REGULAR);

	      posCheck2.cx = newX;
	      posCheck2.cy = newY;

	      posChecks.add(posCheck2);
	      m = true;

		  repaint();
		  
		  complete = true;

		  return m;
   }


   @Override
   public Dimension getPreferredSize()
   {
      return dimPrefSize;
   }

   @Override
   protected void paintComponent(Graphics g)
   {
      paintCheckerBoard(g);
      for (PosCheck posCheck: posChecks)
         if (posCheck != Board.this.posCheck)
            posCheck.checker.draw(g, posCheck.cx, posCheck.cy);

      // Draw dragged checker last so that it appears over any underlying
      // checker.

      if (posCheck != null && move)
         posCheck.checker.draw(g, posCheck.cx, posCheck.cy);
   }

   private void paintCheckerBoard(Graphics g)
   {
      ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                        RenderingHints.VALUE_ANTIALIAS_ON);

      // Paint checkerboard.

      for (int row = 0; row < 8; row++)
      {
         g.setColor(((row & 1) != 0) ? Color.BLACK : Color.WHITE);
         for (int col = 0; col < 8; col++)
         {
            g.fillRect(col * SQUAREDIM, row * SQUAREDIM, SQUAREDIM, SQUAREDIM);
            g.setColor((g.getColor() == Color.BLACK) ? Color.WHITE : Color.BLACK);
         }
      }
   }

   // positioned checker helper class

   private class PosCheck
   {
      public Checker checker;
      public int cx;
      public int cy;
   }
}