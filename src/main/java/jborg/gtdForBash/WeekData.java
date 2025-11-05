package jborg.gtdForBash;

import static jborg.gtdForBash.ProjectJSONToolbox.*;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.json.JSONArray;
import org.json.JSONObject;

import allgemein.ExactPeriode;
import allgemein.LittleTimeTools;
import jborg.gtdForBash.exceptions.WeekDataException;
import someMath.NaturalNumberException;


public class WeekData
{
	
	private final LocalDate begin, end;
	private final int weekNr;
	
	private final Set<JSONObject> projectsActive;

	private final Set<JSONObject> newProjectsWrittenDown;
	
	private final Set<JSONObject> projectsTerminated;
	
	private int howManyStepsDoneInThisWeek;
	
	public static final String weekBeginExceptionMsg = "Week must begin Monday.";

	public static final Function<JSONObject, String> mapJSONToName = obj->obj.getString(ProjectJSONKeyz.nameKey);

	public WeekData(LocalDate begin, int weekNr) throws WeekDataException
	{
		
		if(!begin.getDayOfWeek().equals(DayOfWeek.MONDAY))throw new WeekDataException(weekBeginExceptionMsg);
		this.begin = begin;
		this.end = begin.plusDays(6);
		this.weekNr = weekNr;
		
		projectsActive = new HashSet<>();
		newProjectsWrittenDown = new HashSet<>();
		projectsTerminated = new HashSet<>();
	}
	
	public boolean weekIsInThePast()
	{
		return end.isBefore(LocalDate.now());
	}

	public void addProjectActive(JSONObject pJSON)
	{
		projectsActive.add(pJSON);
	}
	
	public void setProjectsActive(Set<JSONObject> pJSONs) throws WeekDataException
	{
		projectsActive.addAll(pJSONs);
	}

	public void addProjectWrittenDown(JSONObject pJSON)
	{
		newProjectsWrittenDown.add(pJSON);
	}

	public void setProjectsWrittenDown(Set<JSONObject> pJSONs) throws WeekDataException
	{
		newProjectsWrittenDown.addAll(pJSONs);
	}
	
	public void addProjectTerminated(JSONObject pJSON)
	{
		projectsTerminated.add(pJSON);
	}

	public void setProjectsTerminated(Set<JSONObject> projectNames)
	{
		projectsTerminated.addAll(projectNames);
	}

	/**
	 *
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
	public Set<JSONObject> getProjectsTerminated()
	{
		return Set.copyOf(projectsTerminated);
	}

	public Map<String, Integer> getStepsTerminated()
	{

		Map<String, Integer> terminated = new HashMap<>();

		for(JSONObject pJSON: allTheJSON())
		{
			if(isMODProject.test(pJSON))continue;
			String pName = pJSON.getString(ProjectJSONKeyz.nameKey);
			terminated.put(pName, 0);
		}

		for(JSONObject pJSON: allTheJSON())
		{
			if(isMODProject.test(pJSON))continue;
			String pName = pJSON.getString(ProjectJSONKeyz.nameKey);
			iterateOverSteps(pJSON, (jo)->
			{
				if(stepIsTerminated.test(jo))
				{
					int k = terminated.get(pName);
					k++;
					terminated.put(pName, k);
				}
			});
		}

		return terminated;
	}

	public Map<String, Integer> stepsSucceededThisWeek()
	{

		Map<String, Integer> successes = new HashMap<>();
		Map<String, Integer> terminated = getStepsTerminated();

		for(String pName: terminated.keySet())
		{
			successes.put(pName, 0);
		}

		for(String pName: terminated.keySet())
		{
			int k = terminated.get(pName);
			if(k==0)continue;
			JSONObject pJSON = pickProjectByName(pName);
			iterateOverSteps(pJSON, (jo)->
			{
				String stepStatus = jo.getString(StepJSONKeyz.statusKey);
				if(StatusMGMT.success.equals(stepStatus))
				{
					int m = successes.get(pName)+1;
					successes.put(pName, m);
				}

			});
		}

		return successes;
	}
	
	public Map<String, Integer> stepsFailedThisWeek()
	{

		Map<String, Integer> fails = new HashMap<>();
		Map<String, Integer> terminated = getStepsTerminated();

		for(String pName: terminated.keySet())
		{
			fails.put(pName, 0);
		}

		for(String pName: terminated.keySet())
		{
			int k = terminated.get(pName);
			if(k==0)continue;
			JSONObject pJSON = pickProjectByName(pName);
			iterateOverSteps(pJSON, (jo)->
			{
				String stepStatus = jo.getString(StepJSONKeyz.statusKey);
				if(StatusMGMT.failed.equals(stepStatus))
				{
					int m = fails.get(pName)+1;
					fails.put(pName, m);
				}

			});
		}

		return fails;
	}
	
	public Map<String, Integer> stepsViolatedDLThisWeek()
	{

		Map<String, Integer> violations = new HashMap<>();
		Map<String, Integer> howManyInWichProjectMap = stepsFailedThisWeek();
		
		for(String pName: howManyInWichProjectMap.keySet())
		{
			violations.put(pName, 0);
		}

		for(String pName: howManyInWichProjectMap.keySet())
		{

			int h = howManyInWichProjectMap.get(pName);
			if(h==0)continue;
			JSONObject pJSON = pickProjectByName(pName);
			iterateOverSteps(pJSON, (sJSON)->
			{
				if(sJSON.has(StepJSONKeyz.TDTNoteKey)&&
					(sJSON.getString(StepJSONKeyz.TDTNoteKey).equals(tdtNoteStpDLDTAbuse)))
				{
					
					int m = violations.get(pName)+1;
					violations.put(pName, m);
				}

			});
		}
		
		return violations;
	}

	public Map<String, JSONObject> allActiveStepsWithDLs() throws IOException, URISyntaxException
	{

		Map<String, JSONObject> output = new HashMap<>();

		for(JSONObject pJSON: allTheJSON())
		{

			if(isMODProject.test(pJSON))continue;
			if(!lastStepIsTerminated.test(pJSON))
			{

				String pName = pJSON.getString(ProjectJSONKeyz.nameKey);
				JSONObject sJSON = getLastStepOfProject(pJSON);
				String dldtStr = sJSON.getString(StepJSONKeyz.DLDTKey);
				if(!dldtStr.equals(stepDeadlineNone))output.put(pName, sJSON);					
			}
		}
	
		return output;
	}

	public Map<String, LocalDateTime> stepDLsThisWeek() throws IOException, URISyntaxException
	{

		Map<String, LocalDateTime> outputMap = new HashMap<>();

		Map<String, JSONObject> map = allActiveStepsWithDLs();
		for(String pName: map.keySet())
		{

			String dldtStr = map.get(pName).getString(StepJSONKeyz.DLDTKey);
			LocalDateTime dldt = LittleTimeTools.LDTfromTimeString(dldtStr);
			if(isInThisWeek(dldt))outputMap.put(pName, dldt);
		}

		return outputMap;
	}
	
	//TODO: Maybe a list in case it is more than one?
	public Map<String, LocalDateTime> mostPressingStepDeadline() throws IOException, URISyntaxException, NaturalNumberException
	{

		Map<String, LocalDateTime> output  = new HashMap<>();

		int secs = 0;
		LocalDateTime jetzt = LocalDateTime.now();
	
		Map<String, JSONObject> map = allActiveStepsWithDLs();
		
		for(String pName: map.keySet())
		{

			JSONObject sJSON = map.get(pName);
			LocalDateTime dldt = extractLDT(sJSON, StepJSONKeyz.DLDTKey);
			ExactPeriode ep = new ExactPeriode(jetzt, dldt);
			if(Math.abs(secs)>Math.abs(ep.getAbsoluteSeconds()))
			{
				secs = ep.getAbsoluteSeconds();
				output.clear();
				output.put(pName, dldt);
			}
			if(Math.abs(secs)==Math.abs(ep.getAbsoluteSeconds()))
			{
				output.put(pName, dldt);
			}
		}

		return output;
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
				String dldtStr = pJSON.getString(ProjectJSONKeyz.DLDTKey);
				if(dldtStr.equals(prjctDeadlineNone))continue;
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
	public Set<String> mostPressingProjectDeadline() throws IOException, URISyntaxException, NaturalNumberException
	{

		int secs = (int) Math.pow(10, 12);
		LocalDateTime jetzt = LocalDateTime.now();
		Set<String> currentName = new HashSet<>();
	
		Map<String, LocalDateTime> map = allActiveProjectsWithDLs();
		
		for(String pName: map.keySet())
		{

			JSONObject pJSON = pickProjectByName(pName);
			LocalDateTime dldt = extractLDT(pJSON, ProjectJSONKeyz.DLDTKey);
			if(dldt.isBefore(jetzt))continue;
			ExactPeriode ep = new ExactPeriode(jetzt, dldt);
			if(Math.abs(secs)>Math.abs(ep.getAbsoluteSeconds()))
			{
				secs = ep.getAbsoluteSeconds();
				currentName.clear();
				currentName.add(pName);
			}
			if(Math.abs(secs)==Math.abs(ep.getAbsoluteSeconds()))
			{
				currentName.add(pName);
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
		all.addAll(projectsTerminated);

		return all;
	}
	public Set<String> allTheNames()
	{
		
		Set<String> names = new HashSet<>();
	    names.addAll(allTheJSON().stream().map(mapJSONToName)
	    		.collect(Collectors.toSet()));

		return names;
	}

	public boolean isInThisWeek(LocalDateTime ldt)
	{
		LocalDate ld = ldt.toLocalDate();
		return ld.isAfter(begin.minusDays(1))&&ld.isBefore(end.plusDays(1));
	}
	
	public boolean isAfterThisWeek(LocalDateTime ldt)
	{
		LocalDate ld = ldt.toLocalDate();
		return ld.isAfter(end);
	}
	
	public boolean isBeforeThisWeek(LocalDateTime ldt)
	{
		LocalDate ld = ldt.toLocalDate();
		return ld.isBefore(begin);
	}


	public int getHowManyStepsDone()
	{
		return howManyStepsDoneInThisWeek;
	}
}