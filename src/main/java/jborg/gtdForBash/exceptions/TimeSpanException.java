package jborg.gtdForBash.exceptions;

public class TimeSpanException extends Exception
{
	
	private static final long serialVersionUID = 2409942152328296286L;
	
	private final String msg;

	public TimeSpanException(String msg)
	{
		this.msg = msg;
	}

	public String getMessage()
	{
		return msg;
	}
}
