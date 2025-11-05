package jborg.gtdForBash.exceptions;

public class StatisticalToolsException extends Exception
{
	
	private final String msg;
	
	public StatisticalToolsException(String msg)
	{
		this.msg = msg;
	}

	public String getMessage()
	{
		return msg;
	}
}
