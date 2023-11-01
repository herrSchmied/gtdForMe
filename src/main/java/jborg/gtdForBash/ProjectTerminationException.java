package jborg.gtdForBash;

@SuppressWarnings("serial")
public class ProjectTerminationException extends Exception 
{

	public ProjectTerminationException(String msg)
	{
		System.out.println(msg);
	}
}