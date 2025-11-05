package jborg.gtdForBash.exceptions;

public class WeekDataException extends Exception
{
	
	private static final long serialVersionUID = 8307426001470523591L;
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
