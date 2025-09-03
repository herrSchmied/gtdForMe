package jborg.gtdForBash.exceptions;

public class WeekDataException extends Exception
{
	
	private final String msg;

	public WeekDataException(String msg)
	{
		this.msg = msg;
	}
	
	public String getMessage()
	{
		return msg;
	}
}
