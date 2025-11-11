package jborg.gtdForBash;



import java.io.IOException;

import java.net.URISyntaxException;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;


import org.json.JSONArray;
import org.json.JSONObject;



import allgemein.ExactPeriode;
import allgemein.LittleTimeTools;


import jborg.gtdForBash.exceptions.TimeSpanException;
import jborg.gtdForBash.exceptions.WeekDataException;
import static jborg.gtdForBash.ProjectJSONToolbox.*;


import someMath.NaturalNumberException;



public class TimeSpanData
{
	private final LocalDateTime begin, end;
	private final int timeNr;
	
	private final Set<JSONObject> projectsActive;

	private final Set<JSONObject> newProjectsWrittenDown;
	
	private final Set<JSONObject> projectsTerminated;
	
	private int howManyStepsDoneInThisWeek;
	
	public static final String weekBeginExceptionMsg = "Week must begin Monday.";

	public static final Function<JSONObject, String> mapJSONToName = obj->obj.getString(ProjectJSONKeyz.nameKey);

	private final ChronoUnit cu;
	
	public TimeSpanData(ChronoUnit cu, LocalDateTime begin, LocalDateTime end, int timeNr) throws TimeSpanException
	{
		
		if(end.isBefore(begin))throw new TimeSpanException("Time Span ends before it begins.");
		
		this.cu = cu;
		this.begin = begin;
		this.end = end;
		this.timeNr = timeNr;
		
		projectsActive = new HashSet<>();
		newProjectsWrittenDown = new HashSet<>();
		projectsTerminated = new HashSet<>();
	}
	
	public boolean timeSpanIsInThePast()
	{
		return end.isBefore(LocalDateTime.now());
	}

	public boolean timeSpanIsInTheFuture()
	{
		return begin.isAfter(LocalDateTime.now());
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

	public void setHowManyStepsDone(int n)
	{
		howManyStepsDoneInThisWeek = n;
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

	public Map<String, Integer> getStepsTerminatedThisTimeSpan()
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

					LocalDateTime tdt = null;
					
					try
					{
						tdt = extractLDT(jo, StepJSONKeyz.TDTKey);
					}
					catch(URISyntaxException | IOException e)
					{
						e.printStackTrace();
					}
					
					if(isInThisTimeSpan(tdt))
					{
						int k = terminated.get(pName);
						k++;
						terminated.put(pName, k);
					}
				}
			});
		}

		return terminated;
	}

	public Map<String, Integer> getStepsSucceededThisTimeSpan()
	{

		Map<String, Integer> successes = new HashMap<>();
		Map<String, Integer> terminatedSteps = getStepsTerminatedThisTimeSpan();

		for(String pName: terminatedSteps.keySet())
		{
			successes.put(pName, 0);
		}

		for(String pName: terminatedSteps.keySet())
		{
			int k = terminatedSteps.get(pName);
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
	
	public Map<String, Integer> getStepsFailedThisTimeSpan()
	{

		Map<String, Integer> fails = new HashMap<>();
		Map<String, Integer> terminated = getStepsTerminatedThisTimeSpan();

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
	
	public Map<String, Integer> stepsViolatedDLThisTimeSpan()
	{

		Map<String, Integer> violations = new HashMap<>();
		Map<String, Integer> howManyInWichProjectMap = getStepsFailedThisTimeSpan();
		
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

	public Map<String, JSONObject> getAllActiveStepDLs() throws IOException, URISyntaxException
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
				if(!dldtStr.equals(stepDeadlineNone))
				{
					output.put(pName, sJSON);					
				}
			}
		}
	
		return output;
	}

	public Map<String, LocalDateTime> getStepDLsThisTimeSpan() throws IOException, URISyntaxException
	{

		Map<String, LocalDateTime> outputMap = new HashMap<>();

		Map<String, JSONObject> map = getAllActiveStepDLs();
		for(String pName: map.keySet())
		{

			String dldtStr = map.get(pName).getString(StepJSONKeyz.DLDTKey);
			LocalDateTime dldt = LittleTimeTools.LDTfromTimeString(dldtStr);
			if(isInThisTimeSpan(dldt))outputMap.put(pName, dldt);
		}

		return outputMap;
	}

	public Map<String, LocalDateTime> mostPressingStepDeadline() throws IOException, URISyntaxException, NaturalNumberException
	{

		Map<String, LocalDateTime> output  = new HashMap<>();

		int secs = 0;
		LocalDateTime jetzt = LocalDateTime.now();
	
		Map<String, JSONObject> map = getAllActiveStepDLs();
		
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

	public Set<String> projectsSucceededThisTimeSpan()
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
	
	public Set<String> projectsFailedThisTimeSpan()
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

	public Set<String> projectsViolatedDLThisTimeSpan()
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

	public Map<String, LocalDateTime> getAllActiveProjectDLs() throws IOException, URISyntaxException
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

	public Set<String> getProjectDLsThisTimeSpan() throws IOException, URISyntaxException
	{

		Set<String> names = new HashSet<>();

		Map<String, LocalDateTime> map = getAllActiveProjectDLs();
		for(String pName: map.keySet())
		{

			LocalDateTime dldt = map.get(pName);
			if(isInThisTimeSpan(dldt))names.add(pName);
		}

		return names;
	}

	public Set<String> mostPressingProjectDeadline() throws IOException, URISyntaxException, NaturalNumberException
	{

		int secs = (int) Math.pow(10, 12);
		LocalDateTime jetzt = LocalDateTime.now();
		Set<String> currentName = new HashSet<>();
	
		Map<String, LocalDateTime> map = getAllActiveProjectDLs();
		
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

	public boolean isInThisTimeSpan(LocalDateTime ldt)
	{

		LocalDateTime beginBevorBegin = begin.minusNanos(1);
		LocalDateTime endAfterEnd = end.plusNanos(1);
		
		return ldt.isAfter(beginBevorBegin)&&ldt.isBefore(endAfterEnd);
	}
	
	public boolean isAfterThisTimeSpan(LocalDateTime ldt)
	{
		return ldt.isAfter(end);
	}
	
	public boolean isBeforeThisTimeSpan(LocalDateTime ldt)
	{
		return ldt.isBefore(begin);
	}

	public int getHowManyStepsDone()
	{
		return howManyStepsDoneInThisWeek;
	}

}