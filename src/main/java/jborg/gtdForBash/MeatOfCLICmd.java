package jborg.gtdForBash;

import java.io.IOException;
import java.io.Serializable;

import jborg.gtdForBash.exceptions.CLICMDException;
import someMath.NaturalNumberException;


@FunctionalInterface
public interface MeatOfCLICmd <R> extends Serializable
{
	
	public R execute(String s) throws CLICMDException, IOException, NaturalNumberException;
}
