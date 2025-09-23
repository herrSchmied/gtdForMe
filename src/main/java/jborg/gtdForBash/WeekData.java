package jborg.gtdForBash;

import static jborg.gtdForBash.ProjectJSONToolbox.*;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.json.JSONArray;
import org.json.JSONObject;

import allgemein.ExactPeriode;
import jborg.gtdForBash.exceptions.WeekDataException;
import someMath.NaturalNumberException;


public class WeekData
{
	
	private final LocalDate begin, end;
	private final int weekNr;
	
	private final Set<JSONObject> projectsActive;

	private final Set<JSONObject> newProjectsWrittenDown;
	
	private final Set<JSONObject> projectsBorn;
	
	private final Set<JSONObject> projectsTerminated;
	
	private int howManyStepsDoneInThisWeek;
	
	public static final String weekBeginExceptionMsg = "Week must begin Monday.";

	public WeekData(LocalDate begin, int weekNr) throws WeekDataException
	{
		
		if(!begin.getDayOfWeek().equals(DayOfWeek.MONDAY))throw new WeekDataException(weekBeginExceptionMsg);
		this.begin = begin;
		this.end = begin.plusDays(6);
		this.weekNr = weekNr;
		
		projectsActive = new HashSet<>();
		newProjectsWrittenDown = new HashSet<>();
		projectsBorn = new HashSet<>();
		projectsTerminated = new HashSet<>();
	}
	
	public boolean weekIsInThePast()
	{
		return end.isBefore(LocalDate.now());
	}

	/**
	 * Can only be uses once.
	 * @param projectNames
	 * @throws WeekDataException
	 */
	public void setProjectsActive(Set<JSONObject> projectNames) throws WeekDataException
	{
		projectsActive.addAll(projectNames);
	}

	/**
	 * Can only be uses once.
	 * @param projectNames
	 * @throws WeekDataException
	 */
	public void setProjectsWrittenDown(Set<JSONObject> projectNames) throws WeekDataException
	{
		newProjectsWrittenDown.addAll(projectNames);
	}

	/**
	 * Can only be uses once.
	 * @param projectNames
	 * @throws WeekDataException
	 */
	public void setProjectsBorn(Set<JSONObject> projectNames) throws WeekDataException
	{
		projectsBorn.addAll(projectNames);
	}
	
	/**
	 * Can only be used once.
	 * @param projectNames
	 * @throws WeekDataException
	 */
	public void setProjectsTerminated(Set<JSONObject> projectNames) throws WeekDataException
	{
		projectsTerminated.addAll(projectNames);
	}

	/**
	 * Can only be used once.
	 * @param n
	 * @throws WeekDataException
	 */
	public void setHowManyStepsDone(int n) throws WeekDataException
	{
		howManyStepsDoneInThisWeek = n;
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

	/**
	 * Returns immutable Set!
	 * @return
	 */
	public Set<JSONObject> getActiveProjects()
	{
		return Set.copyOf(projectsActive);
	}

	/**
	 * Returns immutable Set!
	 * @return
	 */
	public Set<JSONObject> getProjectsWrittenDown()
	{
		return Set.copyOf(newProjectsWrittenDown);
	}

	/**
	 * Returns immutable Set!
	 * @return
	 */
	public Set<JSONObject> getProjectsBorn()
	{
		return Set.copyOf(projectsBorn);
	}

	/**
	 * Returns immutable Set!
	 * @return
	 */
	public Set<JSONObject> getProjectsTerminated()
	{
		return Set.copyOf(projectsTerminated);
	}

	public Set<String> projectsSucceededThisWeek()
	{
		
		Set<String> successes = new HashSet<>();
		
		for(JSONObject pJSON: getProjectsTerminated())
		{

			String pName = pJSON.getString(ProjectJSONKeyz.nameKey);
			String status = pJSON.getString(ProjectJSONKeyz.statusKey);
			if(StatusMGMT.success.equals(status))successes.add(pName);
		}

		return successes;
	}
	
	public Set<String> projectsFailedThisWeek()
	{

		Set<String> fails= new HashSet<>();

		for(JSONObject pJSON: getProjectsTerminated())
		{
			
			String pName = pJSON.getString(ProjectJSONKeyz.nameKey);
			String status = pJSON.getString(ProjectJSONKeyz.statusKey);
			if(StatusMGMT.failed.equals(status))fails.add(pName);
		}

		return fails;
	}

	public Set<String> projectsViolatedDLThisWeek()
	{
		
		Set<String> violations = new HashSet<>();

		for(JSONObject pJSON: getProjectsTerminated())
		{
			
			String pName = pJSON.getString(ProjectJSONKeyz.nameKey);
			String status = pJSON.getString(ProjectJSONKeyz.statusKey);
			if(StatusMGMT.failed.equals(status))
			{
				
				boolean gotNoteArray = pJSON.has(ProjectJSONKeyz.noteArrayKey);
				
				if(gotNoteArray)
				{
					JSONArray noteArray = pJSON.getJSONArray(ProjectJSONKeyz.noteArrayKey);
					
					for(int n=0;n<noteArray.length();n++)
					{
						String note = noteArray.getString(n);
						if(note.equals(tdtNotePrjctDLDTAbuse))
						{
							violations.add(pName);
							break;
						}
					}
				}
			}
		}

		return violations;
	}

	public Map<String, LocalDateTime> allActiveProjectsWithDLs() throws IOException, URISyntaxException
	{

		Map<String, LocalDateTime> output = new HashMap<>();

		for(JSONObject pJSON: getActiveProjects())
		{

			if(pJSON.has(ProjectJSONKeyz.DLDTKey))
			{

				String pName = pJSON.getString(ProjectJSONKeyz.nameKey);
				LocalDateTime dldt = extractLDT(pJSON, ProjectJSONKeyz.DLDTKey);
				output.put(pName, dldt);					
			}
		}
	
		return output;
	}

	public Set<String> projectDLsThisWeek() throws IOException, URISyntaxException
	{

		Set<String> names = new HashSet<>();

		Map<String, LocalDateTime> map = allActiveProjectsWithDLs();
		for(String pName: map.keySet())
		{

			LocalDateTime dldt = map.get(pName);
			if(isInThisWeek(dldt))names.add(pName);
		}

		return names;
	}
	
	//TODO: Maybe a list in case it is more than one?
	public String mostPressingProjectDeadline() throws IOException, URISyntaxException, NaturalNumberException
	{

		int secs = 0;
		LocalDateTime jetzt = LocalDateTime.now();
		String currentName = "";
	
		Map<String, LocalDateTime> map = allActiveProjectsWithDLs();
		
		for(String pName: map.keySet())
		{

			JSONObject pJSON = pickProjectByName(pName);
			LocalDateTime dldt = extractLDT(pJSON, ProjectJSONKeyz.DLDTKey);
			ExactPeriode ep = new ExactPeriode(jetzt, dldt);
			if(Math.abs(secs)>Math.abs(ep.getAbsoluteSeconds()))
			{
				secs = ep.getAbsoluteSeconds();
				currentName = pName;
			}
		}

		return currentName;
	}

	public JSONObject pickProjectByName(String name)
	{

		JSONObject pJSON;
		pJSON = ProjectJSONToolbox.pickProjectByName(name, allTheJSON());

		return pJSON;
	}

	public Set<JSONObject> allTheJSON()
	{
		Set<JSONObject> all = new HashSet<>();
		all.addAll(newProjectsWrittenDown);
		all.addAll(projectsActive);
		all.addAll(projectsBorn);
		all.addAll(projectsTerminated);

		return all;
	}
	public Set<String> allTheNames()
	{
		
		
		Function<JSONObject, String> f = obj->obj.getString(ProjectJSONKeyz.nameKey);
		
		Set<String> names = new HashSet<>();
	    names.addAll(allTheJSON().stream().map(f)
	    		.collect(Collectors.toSet()));

		return names;
	}

	public boolean isInThisWeek(LocalDateTime ldt)
	{
		LocalDate ld = ldt.toLocalDate();
		return ld.isAfter(begin.minusDays(1))&&ld.isBefore(end.plusDays(1));
	}

	public int getHowManyStepsDone()
	{
		return howManyStepsDoneInThisWeek;
	}
}