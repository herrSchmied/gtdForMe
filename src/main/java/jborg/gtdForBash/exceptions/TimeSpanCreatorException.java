package jborg.gtdForBash.exceptions;

public class TimeSpanCreatorException extends Exception
{

	private static final long serialVersionUID = -164376409431686751L;

	final String msg;


	public TimeSpanCreatorException(String msg)
	{
		this.msg = msg;
	}

	public String getMessage()
	{
		return msg;
	}
}
