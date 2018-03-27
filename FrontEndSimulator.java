import java.util.*;
import java.util.concurrent.TimeUnit;
import java.io.*;

public class FrontEndSimulator
{
	public static void main(String[] args) throws Exception
	{
		Scanner kbd = new Scanner(System.in);
		Scanner in = null;
		File file;
		
		byte r1 = 0;
		byte r2 = 0;
		byte c1 = 0;
		byte c2 = 0;
		boolean moved;
		boolean connected = false;
		ControlUnit control = null;
		String ip = "";
		ArrayList<Byte> list = new ArrayList<Byte>();

		System.out.println("Welcome to a fake game of checkers. Input coordinates to where you'd like to move. \n\n");
		System.out.print("Enter name of input file: ");
		String inFile = kbd.nextLine();
		
		System.out.print("\n\nEnter name of outputFile file: ");
		String destination = kbd.nextLine();
		
		File outFile = new File(destination);
		PrintWriter outputFile = new PrintWriter(outFile);
		
		file = new File(inFile);
		if(file.exists())
		{
			boolean found = true;
			
			try
			{
				in = new Scanner(file);
			}
			catch(FileNotFoundException e)
			{
				found = false;
			}
			
			if(found && in.hasNext())
			{
				ip = in.nextLine();
				control = new ControlUnit(ip);
				control.printBoard();
				connected  = true;
				
				System.out.print("Waiting for opponent to be assigned");
				
				while(!control.engaged())
				{
					System.out.print(".");
					TimeUnit.SECONDS.sleep(1);
				}
				
				while(in.hasNext())
				{
					r1 = Byte.parseByte(in.nextLine());
					list.add(r1);
					
					r2 = Byte.parseByte(in.nextLine());
					list.add(r2);
					
					c1 = Byte.parseByte(in.nextLine());
					list.add(c1);
					
					c2 = Byte.parseByte(in.nextLine());
					list.add(c2);
					
					if(!control.getMyTurn())
						System.out.print("\nWaiting for my turn");
					
					while(!control.getMyTurn())
					{
						System.out.print(".");
						TimeUnit.SECONDS.sleep(1);
					}
					
					moved = control.move(r1, r2, c1, c2);
				
					if(moved)
					{
						System.out.println("\nMove successful!");
						control.printBoard();
					}
					else
						System.out.println("\nMove invalid!");
				}
			}
		}
		
		if(!connected)
		{
			System.out.print("First enter ip of server: ");
			ip = kbd.nextLine();
			
			control = new ControlUnit(ip);
			
			System.out.println();
			System.out.println();
			
			//print the game board
			control.printBoard();
		}
		
		for(;;)
		{
			System.out.print("\n\ncurrent row: ");
			r1 = kbd.nextByte();
			if(r1==-1)
				break;
			list.add(r1);
			
			System.out.print("\ncurrent coloumn: ");
			r2 = kbd.nextByte();
			list.add(r2);
			
			System.out.print("\ndesired row: ");
			c1 = kbd.nextByte();
			list.add(c1);
			
			System.out.print("\ndesired coloumn: ");
			c2 = kbd.nextByte();
			list.add(c2);
			
			moved = control.move(r1, r2, c1, c2);
			
			if(moved)
			{
				System.out.println("Move successful!");
				control.printBoard();
			}
			else
				System.out.println("Move invalid!");
		}
		
		in.close();
		for(int i = 0; i < list.size(); i++)
		{
			outputFile.println(list.get(i));
		}
		
		outputFile.close();
		
	}
}