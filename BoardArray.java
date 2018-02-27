import java.util.*;

public class BoardArray{
	private int[][] board;
	private int piecex, piecey;
	
	public BoardArray(){
		board = new int[8][8];
		board[0][0] = 1;
	}
	public BoardArray(int x, int y){
		board = new int[x][y];
		board[0][0] = 1;
	}
	public int[][] getBoard(){
		return board;
	}
	public void movePiece(int x, int y){
		board[piecex][piecey] = 0;
		board[x][y] = 1;
	}
	
	public String toString(){
		StringJoiner sj = new StringJoiner(System.lineSeparator());
		for (int[] row : board){
			sj.add(Arrays.toString(row));
		}
		return sj.toString();
	}
}