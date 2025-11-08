package jborg.gtdForBash.exceptions;

public class StatisticalToolsException extends Exception
{
	
	private static final long serialVersionUID = 8394586676612600482L;

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
