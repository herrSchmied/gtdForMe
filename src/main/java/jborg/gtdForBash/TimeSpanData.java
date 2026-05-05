package jborg.gtdForBash;



import java.io.IOException;
import java.io.Serializable;
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
import jborg.gtdForBash.exceptions.StatisticalToolsException;
import jborg.gtdForBash.exceptions.TimeSpanException;
import jborg.gtdForBash.exceptions.WeekDataException;

import static jborg.gtdForBash.ProjectJSONKeyz.ADTKey;
import static jborg.gtdForBash.ProjectJSONKeyz.DLDTKey;
import static jborg.gtdForBash.ProjectJSONKeyz.TDTKey;
import static jborg.gtdForBash.ProjectJSONToolBox.*;


import someMath.NaturalNumberException;



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
	
	Map<String, Double> dataMap;
	
	private static final String howManyProjectsSuceeded = "howManyProjectsSucceeded";
	private static final String howManyProjectsFailed   = "howManyProjectsFailed";
	
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

		dataMap = new HashMap<>();
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

	public Map<String, Integer> getStepsTerminatedThisTimeSpan()
	{

		Map<String, Integer> terminated = new HashMap<>();

		for(JSONObject pJSON: allTheProjectJSON())
		{
			if(isMODProject.test(pJSON))continue;
			String pName = pJSON.getString(ProjectJSONKeyz.nameKey);
			terminated.put(pName, 0);
		}

		for(JSONObject pJSON: allTheProjectJSON())
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

	public Set<JSONObject> getStepsSucceededThisTimeSpan()
	{

		Set<JSONObject> winner = new HashSet<>();

		for(JSONObject sJSON: allTheStepsOfAllActiveProjects())
		{

			String stepStatus = sJSON.getString(StepJSONKeyz.statusKey);
			
			if(StatusMGMT.success.equals(stepStatus))winner.add(sJSON);
		}

		return winner;
	}
	
	public int howManyStepsSucceededInThisTSD()
	{
		return getStepsSucceededThisTimeSpan().size();
	}

	public Set<JSONObject> getStepsFailedThisTimeSpan()
	{

		Set<JSONObject> fails = new HashSet<>();

		for(JSONObject sJSON: allTheStepsOfAllActiveProjects())
		{
			String stepStatus = sJSON.getString(StepJSONKeyz.statusKey);
			if(stepStatus.equals(StatusMGMT.failed))fails.add(sJSON);
		}

		return fails;
	}
	
	public int howManyStepsFailedInThisTSD()
	{
		return getStepsFailedThisTimeSpan().size();
	}

	public int howManyNewStepsInThisTSD() throws IOException, URISyntaxException
	{
		int n = 0;
		
		for(JSONObject sJSON: allTheStepsOfAllActiveProjects())
		{
			LocalDateTime adt = extractLDT(sJSON, StepJSONKeyz.ADTKey);
			
			if(isInThisTimeSpan(adt))n++;
		}

		return n;
	}
	
	public Set<JSONObject> stepsViolatedDLThisTimeSpan()
	{

		Set<JSONObject> violaters = new HashSet<>();
		

		for(JSONObject sJSON: getStepsFailedThisTimeSpan())
		{
			if(sJSON.has(StepJSONKeyz.TDTNoteKey)&&
				(sJSON.getString(StepJSONKeyz.TDTNoteKey).equals(tdtNoteStpDLDTAbuse)))
			{
				violaters.add(sJSON);
			}
		}
		
		return violaters;
	}
	
	public int howManyStepsViolatedDLInThisTSD()
	{
		return stepsViolatedDLThisTimeSpan().size();
	}

	public Set<JSONObject> getAllActiveStepsWithDLs() throws IOException, URISyntaxException, NaturalNumberException
	{

		Set<JSONObject> activeDLSteps = new HashSet<>();

		for(JSONObject sJSON: allTheStepsOfAllActiveProjects())
		{

			if(!stepIsTerminated.test(sJSON))
			{
				String dldtStr = sJSON.getString(StepJSONKeyz.DLDTKey);
				if(!dldtStr.equals(stepDeadlineNone))
				{
					LocalDateTime dldt = LittleTimeTools.LDTfromTimeString(dldtStr);					
					if(dldt.isBefore(GTDCLI.now()))continue;
					activeDLSteps.add(sJSON);
				}
			}
		}

		return activeDLSteps;
	}


	public Set<JSONObject> mostPressingStepDeadline() throws IOException, URISyntaxException, NaturalNumberException
	{

		Set<JSONObject> pressingers  = new HashSet<>();

		int secs = (int)Math.pow(10, 21);
		LocalDateTime jetzt = GTDCLI.now();


		for(JSONObject sJSON: getAllActiveStepsWithDLs())
		{

			LocalDateTime dldt = extractLDT(sJSON, StepJSONKeyz.DLDTKey);
			ExactPeriode ep = new ExactPeriode(jetzt, dldt);
			if(Math.abs(secs)>Math.abs(ep.getAbsoluteSeconds()))
			{
				secs = ep.getAbsoluteSeconds();
				pressingers.clear();
				pressingers.add(sJSON);
			}

			if(Math.abs(secs)==Math.abs(ep.getAbsoluteSeconds()))
			{
				pressingers.add(sJSON);
			}
		}

		return pressingers;
	}

	public Set<String> projectsSucceededThisTimeSpan()
	{

		Set<String> successes = new HashSet<>();

		for(String prjctStr: getProjectsTerminated())
		{
			JSONObject pJSON = new JSONObject(prjctStr);
			String pName = pJSON.getString(ProjectJSONKeyz.nameKey);
			String status = pJSON.getString(ProjectJSONKeyz.statusKey);
			if(StatusMGMT.success.equals(status))successes.add(pName);
		}

		return successes;
	}

	public int howManyProjectsSuceeded()
	{

		int n=0;

		if(dataMap.containsKey(howManyProjectsSuceeded))
		{
			return dataMap.get(howManyProjectsSuceeded).intValue();
		}

		n = projectsSucceededThisTimeSpan().size();
		dataMap.put(howManyProjectsSuceeded, (double)n);

		return n;
	}

	public Set<String> projectsFailedThisTimeSpan()
	{

		Set<String> fails= new HashSet<>();

		for(String prjctStr: getProjectsTerminated())
		{
			
			JSONObject pJSON = new JSONObject(prjctStr);
			String pName = pJSON.getString(ProjectJSONKeyz.nameKey);
			String status = pJSON.getString(ProjectJSONKeyz.statusKey);
			if(StatusMGMT.failed.equals(status))fails.add(pName);
		}

		return fails;
	}

	public int howManyProjectsFailed()
	{

		int n=0;

		if(dataMap.containsKey(howManyProjectsFailed))
		{
			return dataMap.get(howManyProjectsFailed).intValue();
		}

		n = projectsFailedThisTimeSpan().size();
		dataMap.put(howManyProjectsFailed, (double)n);

		return n;
	}

	public Set<String> projectsViolatedDLThisTimeSpan()
	{

		Set<String> violations = new HashSet<>();

		for(String prjctStr: getProjectsTerminated())
		{

			JSONObject pJSON = new JSONObject(prjctStr);
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

	public Set<JSONObject> getAllActiveProjectDLs() throws IOException, URISyntaxException
	{

		Set<JSONObject> projectsWithDLs = new HashSet<>();

		for(String prjctStr: getActiveProjects())
		{

			JSONObject pJSON = new JSONObject(prjctStr);
			if(projectIsTerminated.test(pJSON))continue;

			if(pJSON.has(ProjectJSONKeyz.DLDTKey))
			{
				String dldtStr = pJSON.getString(ProjectJSONKeyz.DLDTKey);
				if(dldtStr.equals(prjctDeadlineNone))continue;
				projectsWithDLs.add(pJSON);				
			}
		}
	
		return projectsWithDLs;
	}

	public Set<String> getProjectNamesWithDLsThisTimeSpan() throws IOException, URISyntaxException
	{

		Set<String> names = new HashSet<>();

		Set<JSONObject> set = getAllActiveProjectDLs();
		for(JSONObject pJSON: set)
		{

			LocalDateTime dldt = extractLDT(pJSON, DLDTKey);
			if(isInThisTimeSpan(dldt))
			{
				String name = pJSON.getString(ProjectJSONKeyz.nameKey);
				names.add(name);
			}
		}

		return names;
	}

	public Set<JSONObject> mostPressingProjectDeadline() throws IOException, URISyntaxException, NaturalNumberException
	{

		int secs = (int) Math.pow(10, 21);
		LocalDateTime jetzt = GTDCLI.now();
		Set<JSONObject> projects = new HashSet<>();

		Set<JSONObject> set = getAllActiveProjectDLs();

		for(JSONObject pJSON: set)
		{

			LocalDateTime dldt = extractLDT(pJSON, ProjectJSONKeyz.DLDTKey);

			ExactPeriode ep = new ExactPeriode(jetzt, dldt);
			if(Math.abs(secs)>Math.abs(ep.getAbsoluteSeconds()))
			{
				secs = ep.getAbsoluteSeconds();
				projects.clear();
				projects.add(pJSON);
			}
			if(Math.abs(secs)==Math.abs(ep.getAbsoluteSeconds()))
			{
				projects.add(pJSON);
			}
		}

		return projects;
	}

	public JSONObject projectJSONObjByName(String name)
	{

		JSONObject pJSON;
		pJSON = ProjectJSONToolBox.pickProjectByName(name, allTheProjectJSON());

		return pJSON;
	}

	public Set<JSONObject> allTheProjectJSON()
	{
		Set<JSONObject> all = new HashSet<>();
		
		for(String prjctStr: newProjectsWrittenDown)all.add(new JSONObject(prjctStr));
		for(String prjctStr: projectsActive)all.add(new JSONObject(prjctStr));
		for(String prjctStr: projectsTerminated)all.add(new JSONObject(prjctStr));

		return all;
	}

	public Set<String> allTheNames()
	{

		Set<String> names = new HashSet<>();
	    names.addAll(allTheProjectJSON().stream().map(mapJSONToName)
	    		.collect(Collectors.toSet()));

		return names;
	}
	
	public Set<JSONObject> allTheStepsOfAllActiveProjects()
	{

		Set<JSONObject> output = new HashSet<>();

		for(String prjctStr: getActiveProjects())
		{

			JSONObject pJSON = new JSONObject(prjctStr);
			JSONArray stepJSONArray = pJSON.getJSONArray(ProjectJSONKeyz.stepArrayKey);

			for(Object obj: stepJSONArray)
			{

				JSONObject sJSON = (JSONObject) obj;
				output.add(sJSON);
			}
		}

		return output;
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
	
	public int timeSpansLDTs(String jsonKey) throws IOException, URISyntaxException, StatisticalToolsException, TimeSpanException
	{

		int n = 0;
		for(String pName: allTheNames())
		{

			JSONObject pJSON = projectJSONObjByName(pName);
			if((jsonKey.equals(TDTKey))&&(!projectIsTerminated.test(pJSON)))continue;
			if((jsonKey.equals(DLDTKey))&&(projectHasNoDLDT.test(pJSON)))continue;
			if((jsonKey.equals(ADTKey))&&(isMODProject.test(pJSON)))continue;

			n++;
		}

		return n;
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