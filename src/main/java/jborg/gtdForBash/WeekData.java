package jborg.gtdForBash;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import jborg.gtdForBash.exceptions.WeekDataException;


public class WeekData
{
	
	private final LocalDate begin, end;
	private final int weekNr;
	
	private final Set<String> projectsActive;
	private boolean projectsActiveIsSet = false;

	private final Set<String> newProjectNamesWrittenDown;
	private boolean projectsWrittenIsSet = false;
	
	private final Set<String> projectNamesBorn;
	private boolean projectsBornIsSet = false;
	
	private final Set<String> projectNamesTerminated;
	private boolean projectsTerminatedIsSet = false;
	
	private int howManyStepsDoneInThisWeek;
	private boolean stepNrIsSet = false;

	public WeekData(LocalDate begin, int weekNr) throws WeekDataException
	{
		
		if(!begin.getDayOfWeek().equals(DayOfWeek.MONDAY))throw new WeekDataException("Week must begin Monday.");
		this.begin = begin;
		this.end = begin.plusDays(6);
		this.weekNr = weekNr;
		
		projectsActive = new HashSet<>();
		newProjectNamesWrittenDown = new HashSet<>();
		projectNamesBorn = new HashSet<>();
		projectNamesTerminated = new HashSet<>();
	}
	
	private boolean weekIsInThePast()
	{
		return end.isBefore(LocalDate.now());
	}

	private void throwsExceptionIfWeekIsNotInThePast(String msg) throws WeekDataException
	{
		if(!weekIsInThePast())throw new WeekDataException(msg);
	}

	/**
	 * Can only be uses once.
	 * @param projectNames
	 * @throws WeekDataException
	 */
	public void setProjectsActive(Set<String> projectNames) throws WeekDataException
	{

		throwsExceptionIfWeekIsNotInThePast("Can't Conclude because week is not in the Past.");

		if(!projectsActiveIsSet)
		{
			projectsActive.addAll(projectNames);
			projectsActiveIsSet = true;
		}
		else throw new WeekDataException("Active Projects is already Set.");
	}

	/**
	 * Can only be uses once.
	 * @param projectNames
	 * @throws WeekDataException
	 */
	public void setProjectsWrittenDown(Set<String> projectNames) throws WeekDataException
	{
		
		throwsExceptionIfWeekIsNotInThePast("Can't Conclude because week is not in the Past.");
			
		if(!projectsWrittenIsSet)
		{
			newProjectNamesWrittenDown.addAll(projectNames);
			projectsWrittenIsSet = true;
		}
		else throw new WeekDataException("New Projects Written is already Set.");
	}

	/**
	 * Can only be uses once.
	 * @param projectNames
	 * @throws WeekDataException
	 */
	public void setProjectsBorn(Set<String> projectNames) throws WeekDataException
	{
		
		throwsExceptionIfWeekIsNotInThePast("Can't Conclude because week is not in the Past.");
			
		if(!projectsBornIsSet)
		{
			projectNamesBorn.addAll(projectNames);
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
		
		throwsExceptionIfWeekIsNotInThePast("Can't Conclude because week is not in the Past.");

		if(!projectsTerminatedIsSet)
		{
			projectNamesTerminated.addAll(projectNames);
			projectsTerminatedIsSet = true;
		}
		else throw new WeekDataException("Terminated Projects is already Set.");
	}

	/**
	 * Can only be used once.
	 * @param n
	 * @throws WeekDataException
	 */
	public void sethowManyStepsDoneInThisWeek(int n) throws WeekDataException
	{
		
		throwsExceptionIfWeekIsNotInThePast("Can't Conclude because week is not in the Past.");

		if(!stepNrIsSet)
		{
			howManyStepsDoneInThisWeek = n;
			stepNrIsSet = true;
		}
		else throw new WeekDataException("Step Nr. already Set.");
	}

	public LocalDate getBegin()
	{
		return begin;//Remember: can give a copy back?
	}

	public LocalDate getEnd()
	{
		return end;//Remember: can give a copy back?
	}

	public int getWeekNr()
	{
		return weekNr;
	}

	public Set<String> getActiveProjects()
	{
		return Set.copyOf(projectsActive);
	}

	public Set<String> getNewProjectNamesWrittenDown()
	{
		return Set.copyOf(newProjectNamesWrittenDown);
	}

	public Set<String> getProjectNamesBornInThisWeek()
	{
		return Set.copyOf(projectNamesBorn);
	}

	public Set<String> getProjectNamesTerminatedInThisWeek()
	{
		return Set.copyOf(projectNamesTerminated);
	}

	public int getHowManyStepsDoneInThisWeek()
	{
		return howManyStepsDoneInThisWeek;
	}
}