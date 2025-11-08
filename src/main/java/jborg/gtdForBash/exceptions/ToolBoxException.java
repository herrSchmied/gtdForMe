package jborg.gtdForBash.exceptions;

public class ToolBoxException extends Exception
{

	private static final long serialVersionUID = 6818535650238364169L;
	
	
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
