package jborg.gtdForBash;




import java.io.Serializable;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;


import org.json.JSONObject;


import allgemein.LittleTimeTools;


import jborg.gtdForBash.exceptions.TimeSpanException;
import jborg.gtdForBash.exceptions.WeekDataException;


import someMath.exceptions.*;



public class TimeSpanData implements Serializable
{

	private static final long serialVersionUID = 1L;
	
	
	private final LocalDateTime begin, end;
	private final int timeNr;
	
	private final Set<String> projectsActive;

	private final Set<String> newProjectsWrittenDown;
	
	private final Set<String> projectsTerminated;

	public static final String weekBeginExceptionMsg = "Week must begin Monday.";

	public static final Function<JSONObject, String> mapJSONToName = obj->obj.getString(ProjectJSONKeyz.nameKey);

	private final ChronoUnit cu;
	
	
	public TimeSpanData(ChronoUnit cu, LocalDateTime begin, LocalDateTime end, int timeNr) throws TimeSpanException
	{

		if(!begin.isBefore(end))throw new TimeSpanException("Time Span begin is not after end.");
		
		this.cu = cu;
		this.begin = begin;
		this.end = end;
		this.timeNr = timeNr;

		projectsActive = new HashSet<>();
		newProjectsWrittenDown = new HashSet<>();
		projectsTerminated = new HashSet<>();
	}

	public boolean timeSpanIsInThePast() throws NaturalNumberException
	{
		return end.isBefore(GTDCLI.now());
	}

	public boolean timeSpanIsInTheFuture() throws NaturalNumberException
	{
		return begin.isAfter(GTDCLI.now());
	}

	public void addProjectActive(String prjctStr)
	{
		projectsActive.add(prjctStr);
	}
	
	public void setProjectsActive(Set<String> prjctStrs) throws WeekDataException
	{
		projectsActive.addAll(prjctStrs);
	}

	public void addProjectWrittenDown(String prjctStr)
	{
		newProjectsWrittenDown.add(prjctStr);
	}

	public void setProjectsWrittenDown(Set<String> prjctStrs) throws WeekDataException
	{
		newProjectsWrittenDown.addAll(prjctStrs);
	}
	
	public void addProjectTerminated(String prjctStr)
	{
		projectsTerminated.add(prjctStr);
	}

	public void setProjectsTerminated(Set<String> prjctStrs)
	{
		projectsTerminated.addAll(prjctStrs);
	}

	public LocalDateTime getBegin()
	{
		//Giving a Copy back? No because LocalDateTime is immutable!
		return begin;
	}

	public LocalDateTime getEnd()
	{
		//Giving a Copy back? No because LocalDateTime is immutable!
		return end;
	}

	public int getTimeNr()
	{
		return timeNr;
	}

	public ChronoUnit getChronoUnit()
	{
		return cu;
	}

	/**
	 * Returns immutable Set!
	 * @return
	 */
	public Set<String> getActiveProjects()
	{
		return Set.copyOf(projectsActive);
	}

	/**
	 * Returns immutable Set!
	 * @return
	 */
	public Set<String> getProjectsWrittenDown()
	{
		return Set.copyOf(newProjectsWrittenDown);
	}

	/**
	 * Returns immutable Set!
	 * @return
	 */
	public Set<String> getProjectsTerminated()
	{
		return Set.copyOf(projectsTerminated);
	}

	public Set<String> allTheProjectNames()
	{

		Set<String> names = new HashSet<>();
		names.addAll(getActiveProjects());
		names.addAll(getProjectsWrittenDown());
		names.addAll(getProjectsTerminated());
		
		return names;
	}
	
	public boolean isInThisTimeSpan(LocalDateTime ldt)
	{

		boolean oldEnough = ldt.isAfter(begin) || ldt.equals(begin);
		boolean youngEnough = ldt.isBefore(end) || ldt.equals(end);

		return oldEnough&&youngEnough;
	}

	public boolean isAfterThisTimeSpan(LocalDateTime ldt)
	{
		return ldt.isAfter(end);
	}

	public boolean isBeforeThisTimeSpan(LocalDateTime ldt)
	{
		return ldt.isBefore(begin);
	}
	
	public String toString()
	{
		
		
		String beginStr = LittleTimeTools.timeString(begin);
		String endStr = LittleTimeTools.timeString(end);
		
		return 		"TSD Nr: " + timeNr + "\n"
				+	"ChronoUnit: " + cu + "\n"
				+	"Begin: " + beginStr + "\n"
				+	"End: " + endStr + "\n"
				+	"Projects active: " + projectsActive.size()+"\n"
				+	"Projects created: " + newProjectsWrittenDown.size()+"\n"
				+	"Projects terminated: " + projectsTerminated.size() + "\n";
	}
}