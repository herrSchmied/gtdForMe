package jborg.gtdForBash;

import java.io.IOException;

import consoleTools.InputArgumentException;
import someMath.NaturalNumberException;


@FunctionalInterface
public interface MeatOfCLICmd <R>
{
	
	public R execute(String s) throws CLICMDException, SpawnProjectException, 
	SpawnStepException, ProjectTerminationException, StepTerminationException, IOException,
	TimeGoalOfProjectException, InputArgumentException, NaturalNumberException;
}
