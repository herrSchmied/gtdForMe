package jborg.gtdForBash;

@SuppressWarnings("serial")
public class StepTerminationException extends Exception 
{

	public StepTerminationException(String msg)
	{
		System.out.println(msg);
	}
}