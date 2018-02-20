import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class TestTransmitData
{
	public static void main(String[] args) throws Exception
	{
		Scanner scan = new Scanner(System.in);

		//System.out.print("Enter dhcp address: ");
		//String ip = scan.nextLine();

		TransmitData player = new TransmitData("127.0.0.1");

		TimeUnit.SECONDS.sleep(1);

		System.out.println(player.getData());

		player.opponentStatus();

		TimeUnit.SECONDS.sleep(2);

		System.out.println(player.getData());
	}
}