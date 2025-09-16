package jborg.gtdForBash.exceptions;

@SuppressWarnings("serial")
public class CLICMDException extends Exception
{

	public CLICMDException(String msg)
	{
		System.out.println(msg);
	}
}
