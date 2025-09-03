package jborg.gtdForBash;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import jborg.gtdForBash.exceptions.WeekDataException;


public class WeekData
{

	public static final String inThePastStatus = "aWeekOfThePastWeek";
	public static final String inTheMomentStatus = "currentWeek";

	private final String status;
	private final LocalDate begin, end;
	private final int weekNr;
	
	private final Set<String> projectNamesBornInThisWeek;
	private boolean projectsBornIsSet = false;
	
	private final Set<String> projectNamesTerminatedInThisWeek;
	private boolean projectsTerminatedIsSet = false;
	
	private int howManyStepsDoneInThisWeek;
	private boolean stepNrIsSet = false;

	public WeekData(LocalDate begin, int weekNr) throws WeekDataException
	{
		
		if(!begin.getDayOfWeek().equals(DayOfWeek.MONDAY))throw new WeekDataException(" Week must begin Monday.");
		this.begin = begin;
		this.end = begin.plusDays(6);
		this.weekNr = weekNr;
		if(end.isBefore(LocalDate.now()))status = inThePastStatus;
		else status = inTheMomentStatus;
		
		projectNamesBornInThisWeek = new HashSet<>();
		projectNamesTerminatedInThisWeek = new HashSet<>();
	}
	
	/**
	 * Can only be uses once.
	 * @param projectNames
	 * @throws WeekDataException
	 */
	public void setProjectsBorn(Set<String> projectNames) throws WeekDataException
	{
		if(!projectsBornIsSet)
		{
			projectNamesBornInThisWeek.addAll(projectNames);
			projectsBornIsSet = true;
		}
		else throw new WeekDataException("Born Projects already Set.");

	}
	
	/**
	 * Can only be used once.
	 * @param projectNames
	 * @throws WeekDataException
	 */
	public void setProjectsTerminated(Set<String> projectNames) throws WeekDataException
	{
		if(!projectsTerminatedIsSet)
		{
			projectNamesTerminatedInThisWeek.addAll(projectNames);
			projectsTerminatedIsSet = true;
		}
		else throw new WeekDataException("Terminated Projects already Set.");
	}

	/**
	 * Can only be used once.
	 * @param n
	 * @throws WeekDataException
	 */
	public void sethowManyStepsDoneInThisWeek(int n) throws WeekDataException
	{
		if(!stepNrIsSet)
		{
			howManyStepsDoneInThisWeek = n;
			stepNrIsSet = true;
		}
		else throw new WeekDataException("Step Nr. already Set.");
	}

	public String getStatus()
	{
		return status;
	}


	public LocalDate getBegin()
	{
		return begin;
	}


	public LocalDate getEnd()
	{
		return end;
	}


	public int getWeekNr()
	{
		return weekNr;
	}


	public Set<String> getProjectNamesBornInThisWeek()
	{
		return projectNamesBornInThisWeek;
	}


	public Set<String> getProjectNamesTerminatedInThisWeek()
	{
		return projectNamesTerminatedInThisWeek;
	}


	public int getHowManyStepsDoneInThisWeek()
	{
		return howManyStepsDoneInThisWeek;
	}
}
