import java.util.Scanner;

/**
*YOU MUST START SERVER BEFORE ENTERING DHCP!
*Class is a test application for TransmitData--it works well, but there is a bug for option "c." (Sending instance gets hung up)

@version 1.1
@author Dan Martineau
*/
public class TestClient
{
	private static TransmitData player;
	private static Scanner kbd;
	private static String oppID = null;
	private static String myID = null;
	private static boolean engaged = false;
	
	public static void main(String[] args) throws Exception
	{
		kbd = new Scanner(System.in);
		
		System.out.print("Enter DHCP address of server: ");
		String ip = kbd.nextLine();
		
		//create new instance of player
		player = new TransmitData(ip);
		
		
		//get initialization status
		String hold = player.getData();
		if((hold.substring(6,7)).equals("i"))
			System.out.println("You are connected to the server with the id: " + hold.substring(2,5));
		
		//see if we have an opponent
		player.opponentStatus();
		decode(player.getData());
		
		
		
		//Let user control program
		String choice = "";
		while(!choice.equals("d") && !choice.equals("D"))
		{
			System.out.print("\n\nWhat would y'all like to do next?\n");
			System.out.print("a) Get opponent status\nb) Get a new opponent\nc) Send message to thine opponent\nd) terminate connection\nChoice: ");
			choice = kbd.nextLine();
			
			if(choice.equals("a") || choice.equals("A"))
			{
				//get opponent status
				player.opponentStatus();
				decode(player.getData());
			}
			else if(choice.equals("b") || choice.equals("B"))
			{
				//get new opponent 
				player.newOpponent();
				decode(player.getData());
			}
			else if(choice.equals("c") || choice.equals("C"))
			{	
				if(!engaged)
				{
					System.out.println("You have no opponent to send to!\n");
				}
				else
				{
					//Send opponent message
					System.out.print("\nSend message: ");
					String message = kbd.nextLine();
					
					player.sendData("c " + player.getMyID() + " c " + oppID + " " + message);
					System.out.println("Message has been sent.\n");
				}
			}
			else if(choice.equals("d") || choice.equals("D"))
			{	
				//terminate connection
				player.terminateConn();
				System.out.println("Connection Terminated.\n");
			}
			else
				System.out.println("WTF, learn how to type!\n");
		}
		
		
	}
	
	private static void decode(String data)
	{
		//ensure data was intended for client
		if(data==null || data.length() < 6 || !(data.substring(0,1).equals("c")))
			return;
		else
		{
			if(data.substring(6,7).equals("d"))
			{
				//player has been disconnected from opponent
				engaged = false;
				oppID = null;
				System.out.println("\nPlayer has terminated the connection.");
			}
			else if(data.substring(6,7).equals("c"))
			{
				//player has recieved general data from opponent
				System.out.println("\nMessage from opponent: " + data.substring(11));
				
				//message might be extraneous, so decode a second time to ensure decoding does not occur out of order
				decode(player.getData());
			}
			else if(data.substring(6,7).equals("o"))
			{
				//player has opponent
				if(data.substring(8,9).equals("t"))
				{
					engaged = true;
					oppID = data.substring(10,13);
					System.out.println("\nYou are paired with the opponent: " + oppID);
				}
				//player has no opponent
				else if(data.substring(8,9).equals("f"))
				{
					engaged = false;
					oppID = null;
					System.out.println("\nYou are not paired with any opponent.");
				}
			}
			else if(data.substring(6,7).equals("n"))
			{
				//new opponent has been found
				if(data.substring(8,9).equals("t"))
				{
					engaged = true;
					oppID = data.substring(10,13);
					System.out.println("\nOpponent found: " + oppID);
				}
				//no opponents available
				else if(data.substring(8,9).equals("f"))
				{
					engaged = false;
					oppID = null;
					System.out.println("\nThere are no opponents available at this time.");
				}
			}
		}
	}
}