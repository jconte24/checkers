import java.util.ArrayList;

public class Queue
{
	private ArrayList<String> list;
	private int index = 0;

    public Queue()
    {
        list = new ArrayList<String>(5);
    }

	public Queue(String node)
	{
        list = new ArrayList<String>(5);
		list.add(index, node);
		index++;
	}

	public void enque(String node)
	{
		list.add(index, node);
		index++;
	}

	public String deque()
	{
		String hold = null;

		if(list.size() > 0)
		{
			hold = list.get(0);

			list.remove(0);
			list.trimToSize();

			index--;
		}

		return hold;
	}

	public String peek()
	{
		return list.get(0);
	}

    public int getLength()
    {
        return list.size();
    }
}
