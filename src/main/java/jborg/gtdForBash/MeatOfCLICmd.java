package jborg.gtdForBash;

import java.io.IOException;
import java.io.Serializable;

import consoleTools.InputArgumentException;
import someMath.NaturalNumberException;


@FunctionalInterface
public interface MeatOfCLICmd <R> extends Serializable
{
	
	public R execute(String s) throws CLICMDException, SpawnProjectException, 
	SpawnStepException, ProjectTerminationException, StepTerminationException, IOException,
	TimeGoalOfProjectException, InputArgumentException, NaturalNumberException;
}
