public class TestBoard
{
	public static void main(String[] args)
	{
		ControlLogic mover = new ControlLogic();

		System.out.println("Starting board:\n");
		mover.printBoard();

		System.out.println("Move [5][7] to [4][6]:\n");
		mover.move((byte)5,(byte)7,(byte)4,(byte)6);

		mover.printBoard();

		System.out.println("Move [4][6] to [5][7] (should not move as it is not a king and cannot move backwards):\n");
		mover.move((byte)4,(byte)6,(byte)5,(byte)7);

		mover.printBoard();

		System.out.println("Move [5][1] to [4][1] (should not move there as it is unplayable square):\n");
		mover.move((byte)5,(byte)1,(byte)4,(byte)1);

		mover.printBoard();

		System.out.println("Move [5][1] to [3][1] (should not move as that is a vertical move):\n");
		mover.move((byte)5,(byte)1,(byte)3,(byte)1);

		mover.printBoard();

		System.out.println("Move [5][1] to [5][3] (should not move as that is a horizontal move):\n");
		mover.move((byte)5,(byte)1,(byte)5,(byte)3);

		mover.printBoard();

		System.out.println("Move [5][1] to [3][3] (should not move as that is not a jump):\n");
		mover.move((byte)5,(byte)1,(byte)3,(byte)3);

		mover.printBoard();
	}
}