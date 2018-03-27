import java.util.*;

public class TestApp{
	public static void main(String[] args){
		Scanner kbd = new Scanner(System.in);
		BoardArray board = new BoardArray();
		
		while(true){
			System.out.println(BoardArray);
			System.out.print("Enter 1 to move piece, 2 to quit:");
			int input = kbd.nextInt();
			
			if(input == 1){
				System.out.print("Enter new X:");
				int x = kbd.nextInt();
				System.out.print("Enter new Y:");
				int y = kbd.nextInt();
				BoardArray.movePiece(x,y);
			}else if(input == 2){
				break;
			}else{
				System.out.println("Enter a valid input.");
			}
		}
	}
}

