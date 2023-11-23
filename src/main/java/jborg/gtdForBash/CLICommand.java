package jborg.gtdForBash;


import java.util.function.Function;


public class CLICommand <O>
{

	public final static String mustHaveArgumentStr = "This command must have an Argument";
	public final static String cantHaveArgumentStr = "This command can't have Arguments";
	
	public final boolean mustHaveArgument;
	public final boolean canHaveArgument;
	public final boolean cantHaveArgument;
	
	public final boolean mustHaveOutput;
	public final boolean canHaveOutput;
	public final boolean cantHaveOutput;
	
	private final Function<String, O> action;
	
	private final String cmdName;
	
	public CLICommand(String command, boolean mustHaveArgument, boolean canHaveArgument, boolean mustHaveOutput,
			boolean canHaveOutput, Function<String, O> action)
	{
		/*
		 * only one of the Argument booleans can be true!!
		 * if you know mustHaveArgument and canHaveArgumet
		 * then you now cantHaveArgument.!!!
		 */
		this.mustHaveArgument = mustHaveArgument;
		this.canHaveArgument = canHaveArgument;

		boolean wrongBothTrue = mustHaveArgument&&canHaveArgument;
		if(wrongBothTrue)throw new IllegalArgumentException("'Must have'-Argument and 'can have'-Argument can't be both true!");
				
		cantHaveArgument = !(mustHaveArgument||canHaveArgument);
		
		/*
		 * only one of the output booleans can be true!!
		 * if you know mustHaveArgument and canHaveArgumet
		 * then you now cantHaveArgument.!!!
		 */
		this.mustHaveOutput = mustHaveOutput;
		this.canHaveOutput = canHaveOutput;

		wrongBothTrue = mustHaveOutput&&canHaveOutput;
		if(wrongBothTrue)throw new IllegalArgumentException("'Must have'-Output and 'can have'-Output can't be both true!");
				
		cantHaveOutput = !(mustHaveOutput||canHaveOutput);
		
		
		this.action = action;
		this.cmdName = command;
	}
	

    public void inCase(String command)
    {
    }
    
    public boolean hasArgument()
    {
    	return false;
    }
    
    public String getName()
    {
    	return cmdName;
    }
    
    public O executeCmd(String argument) throws CLICMDException
    {
    	
    	if(mustHaveArgument&&argument.trim().equals("")) throw new CLICMDException(mustHaveArgumentStr);
    	
    	if(cantHaveArgument)
    	{
    		if(!argument.trim().equals("")) throw new CLICMDException(cantHaveArgumentStr);
    		return action.apply("");
    	}
    	
    	return action.apply(argument);
    }
}
