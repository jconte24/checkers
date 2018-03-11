import java.util.*;

public class FrontEndSimulator
{
	public static void main(String[] args) throws Exception
	{
		Scanner kbd = new Scanner(System.in);
		byte r1 = 0;
		byte r2 = 0;
		byte c1 = 0;
		byte c2 = 0;
		boolean moved;

		System.out.println("Welcome to a fake game of checkers. Input coordinates to where you'd like to move. \n\n");
		
		System.out.print("First enter ip of server: ");
		String ip = kbd.nextLine();
		
		ControlUnit control = new ControlUnit(ip);
		
		System.out.println();
		System.out.println();
		
		//print the game board
		control.printBoard();
		
		for(;;)
		{
			System.out.print("\n\ncurrent row: ");
			r1 = kbd.nextByte();
			
			System.out.print("\ncurrent coloumn: ");
			r2 = kbd.nextByte();
			
			System.out.print("\ndesired row: ");
			c1 = kbd.nextByte();
			
			System.out.print("\ndesired coloumn: ");
			c2 = kbd.nextByte();
			
			moved = control.move(r1, r2, c1, c2, true);
			
			if(moved)
			{
				System.out.println("Move successful!");
				control.printBoard();
			}
			else
				System.out.println("Move invalid!");
		}
	}
}