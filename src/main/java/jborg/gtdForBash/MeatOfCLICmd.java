package jborg.gtdForBash;

import java.io.IOException;

import consoleTools.InputArgumentException;
import someMath.InterfaceNumberException;

@FunctionalInterface
public interface MeatOfCLICmd <R>
{
	
	public R execute(String s) throws InterfaceNumberException, SpawnProjectException, 
	SpawnStepException, ProjectTerminationException, StepTerminationException, IOException,
	TimeGoalOfProjectException, InputArgumentException;
}
