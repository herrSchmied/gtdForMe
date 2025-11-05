package jborg.gtdForBash.exceptions;

@SuppressWarnings("serial")
public class CLICMDException extends Exception
{

	private final String msg;
	
	public CLICMDException(String msg)
	{
		this.msg = msg;
	}
	
	public String getMessage()
	{
		return msg;
	}
}
