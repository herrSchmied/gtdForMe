package jborg.gtdForBash.exceptions;

public class ToolBoxException extends Exception
{

	private final String msg;

	public ToolBoxException(String msg)
	{
		this.msg = msg;
	}
	
	public String getMessage()
	{
		return msg;
	}
}
