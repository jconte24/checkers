public class DanChecker
{
	private boolean mine;
	private boolean king;

	public DanChecker(boolean mine)
	{
		this.mine = mine;
		king = false;
	}

	//returns true if the checker is player's or false if it's the opponent's
	public boolean getOwner()
	{
		return mine;
	}

	//return true if checker is a king
	public boolean getKing()
	{
		return king;
	}

	//make checker a king
	public void kingMe()
	{
		king = true;
	}

	public String toString()
	{
		String str = "";

		if(mine && !king)
			str = "m";
		else if(mine && king)
			str = "M";
		else if(!mine && !king)
			str = "o";
		else if(!mine && king)
			str = "O";

		return str;
	}
}