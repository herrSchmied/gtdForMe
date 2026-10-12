package jborg.gtdForBash;




import java.io.IOException;

import java.net.URISyntaxException;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;

import allgemein.ExactPeriode;
import allgemein.LittleTimeTools;
import jborg.gtdForBash.exceptions.StatisticalToolsException;
import jborg.gtdForBash.exceptions.TimeSpanCreatorException;
import jborg.gtdForBash.exceptions.TimeSpanException;
import jborg.gtdForBash.exceptions.ToolBoxException;
import jborg.gtdForBash.exceptions.WeekDataException;
import static jborg.gtdForBash.ProjectJSONToolBox.*;
import someMath.exceptions.*;




public class StatisticalTools
{

	final Set<JSONObject> prjctSet;
	final TimeSpanCreator tsc;
	
	Map<String, Double> dataMap;
	
	private static final String howManyProjectsSuceeded = "howManyProjectsSucceeded";
	private static final String howManyProjectsFailed   = "howManyProjectsFailed";


	public StatisticalTools(Set<JSONObject> prjctSet, Map<String, List<TimeSpanData>> MapOfTSDLists) throws IOException, URISyntaxException, WeekDataException, TimeSpanException, ToolBoxException, StatisticalToolsException, TimeSpanCreatorException, NaturalNumberException
	{

		if(prjctSet==null)throw new NullPointerException("Argument is null.");
		this.prjctSet = prjctSet;
		tsc = new TimeSpanCreator(this, prjctSet, MapOfTSDLists);
		
		dataMap = new HashMap<>();
	}

	public void printMap(Map<Integer, Map<String, LocalDateTime>> map)
	{
		int s = map.size();
		for(int n=0;n<s;n++)
		{
			int ds = map.get(n).size();
			System.out.println("WeekNr: " + n + ". LDTs: " + ds);
			if(ds>0)
			{
				Map<String, LocalDateTime> innerMap = map.get(n);
				for(String pName: innerMap.keySet())
				{
					LocalDateTime ldt = innerMap.get(pName);
					System.out.println("Project Name: " + pName+ ". LDT: " + ldt);
				}
			}
		}
	}

	public Set<JSONObject> getPrjctSet()
	{
		return prjctSet;
	}
	
	public TimeSpanCreator getTimeSpanCreator()
	{
		return tsc;
	}

	public JSONObject projectJSONObjByName(String name)
	{

		JSONObject pJSON;
		pJSON = ProjectJSONToolBox.pickProjectByName(name, prjctSet);

		return pJSON;
	}
	
	public List<TimeSpanData> getTSDList(ChronoUnit cu) throws TimeSpanException
	{
		return tsc.getTimeSpanList(cu);
	}

	public Set<JSONObject> allTheProjectJSON()
	{
		return prjctSet;
	}

	public int timeSpansLDTs(TimeSpanData tsd, String jsonKey) throws IOException, URISyntaxException, StatisticalToolsException, TimeSpanException
	{
	
		int n = 0;
		for(String pName: tsd.allTheProjectNames())
		{
	
			JSONObject pJSON = projectJSONObjByName(pName);
			if((jsonKey.equals(ProjectJSONKeyz.TDTKey))&&(!projectIsTerminated.test(pJSON)))continue;
			if((jsonKey.equals(ProjectJSONKeyz.DLDTKey))&&(projectHasNoDLDT.test(pJSON)))continue;
			if((jsonKey.equals(ProjectJSONKeyz.ADTKey))&&(isMODProject.test(pJSON)))continue;
	
			n++;
		}
	
		return n;
	}

	public Set<JSONObject> getAllActiveProjectDLs(TimeSpanData tsd) throws IOException, URISyntaxException
	{
	
		Set<JSONObject> projectsWithDLs = new HashSet<>();
	
		for(String prjctName: tsd.getActiveProjects())
		{
	
			JSONObject pJSON = projectJSONObjByName(prjctName);
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

	public int howManyStepsFailed(TimeSpanData tsd)
	{
		return getStepsFailed(tsd).size();
	}

	public int howManyStepsSucceeded(TimeSpanData tsd)
	{
		return getStepsSucceeded(tsd).size();
	}

	public int howManyStepsViolatedDL(TimeSpanData tsd)
	{
		return stepsViolatedDL(tsd).size();
	}

	public Set<JSONObject> mostPressingProjectDeadline(TimeSpanData tsd) throws IOException, URISyntaxException, NaturalNumberException
	{
	
		int secs = (int) Math.pow(10, 21);
		LocalDateTime jetzt = GTDCLI.now();
		Set<JSONObject> projects = new HashSet<>();
	
		Set<JSONObject> set = getAllActiveProjectDLs(tsd);
	
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

	public Set<JSONObject> mostPressingStepDeadline(TimeSpanData tsd) throws IOException, URISyntaxException, NaturalNumberException
	{
	
		Set<JSONObject> pressingers  = new HashSet<>();
	
		int secs = (int)Math.pow(10, 21);
		LocalDateTime jetzt = GTDCLI.now();
	
	
		for(JSONObject sJSON: getAllActiveStepsWithDLs(tsd))
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

	public Set<String> projectsFailed(TimeSpanData tsd)
	{
	
		Set<String> fails= new HashSet<>();
	
		for(String prjctName: tsd.getProjectsTerminated())
		{
			
			JSONObject pJSON = projectJSONObjByName(prjctName);
			String pName = pJSON.getString(ProjectJSONKeyz.nameKey);
			String status = pJSON.getString(ProjectJSONKeyz.statusKey);
			if(StatusMGMT.failed.equals(status))fails.add(pName);
		}
	
		return fails;
	}

	public Set<String> projectsSucceeded(TimeSpanData tsd)
	{
	
		Set<String> successes = new HashSet<>();
	
		for(String prjctName: tsd.getProjectsTerminated())
		{
			JSONObject pJSON = projectJSONObjByName(prjctName);
			String pName = pJSON.getString(ProjectJSONKeyz.nameKey);
			String status = pJSON.getString(ProjectJSONKeyz.statusKey);
			if(StatusMGMT.success.equals(status))successes.add(pName);
		}
	
		return successes;
	}

	public Set<String> projectsViolatedDL(TimeSpanData tsd)
	{
	
		Set<String> violations = new HashSet<>();
	
		for(String prjctName: tsd.getProjectsTerminated())
		{
	
			JSONObject pJSON = projectJSONObjByName(prjctName);
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

	public Set<JSONObject> stepsViolatedDL(TimeSpanData tsd)
	{
	
		Set<JSONObject> violaters = new HashSet<>();
		
	
		for(JSONObject sJSON: getStepsFailed(tsd))
		{
			if(sJSON.has(StepJSONKeyz.TDTNoteKey)&&
				(sJSON.getString(StepJSONKeyz.TDTNoteKey).equals(tdtNoteStpDLDTAbuse)))
			{
				violaters.add(sJSON);
			}
		}
		
		return violaters;
	}

	public int howManyNewStepsHere(TimeSpanData tsd) throws IOException, URISyntaxException
	{
		int n = 0;
		
		for(JSONObject sJSON: allTheStepsOfAllActiveProjects(tsd))
		{
			LocalDateTime adt = extractLDT(sJSON, StepJSONKeyz.ADTKey);
			
			if(tsd.isInThisTimeSpan(adt))n++;
		}
	
		return n;
	}

	public int howManyProjectsFailed(TimeSpanData tsd)
	{
	
		int n=0;
	
		if(dataMap.containsKey(howManyProjectsFailed))
		{
			return dataMap.get(howManyProjectsFailed).intValue();
		}
	
		n = projectsFailed(tsd).size();
		dataMap.put(howManyProjectsFailed, (double)n);
	
		return n;
	}

	public int howManyProjectsSuceeded(TimeSpanData tsd)
	{
	
		int n=0;
	
		if(dataMap.containsKey(howManyProjectsSuceeded))
		{
			return dataMap.get(howManyProjectsSuceeded).intValue();
		}
	
		n = projectsSucceeded(tsd).size();
		dataMap.put(howManyProjectsSuceeded, (double)n);
	
		return n;
	}

	public Set<JSONObject> allTheStepsOfAllActiveProjects(TimeSpanData tsd)
	{
	
		Set<JSONObject> output = new HashSet<>();
	
		for(String prjctName: tsd.getActiveProjects())
		{
	
			JSONObject pJSON = projectJSONObjByName(prjctName);
			JSONArray stepJSONArray = pJSON.getJSONArray(ProjectJSONKeyz.stepArrayKey);
	
			for(Object obj: stepJSONArray)
			{
	
				JSONObject sJSON = (JSONObject) obj;
				output.add(sJSON);
			}
		}
	
		return output;
	}

	public Set<JSONObject> getAllActiveStepsWithDLs(TimeSpanData tsd) throws IOException, URISyntaxException, NaturalNumberException
	{
	
		Set<JSONObject> activeDLSteps = new HashSet<>();
	
		for(JSONObject sJSON: allTheStepsOfAllActiveProjects(tsd))
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

	public Set<JSONObject> getStepsFailed(TimeSpanData tsd)
	{
	
		Set<JSONObject> fails = new HashSet<>();
	
		for(JSONObject sJSON: allTheStepsOfAllActiveProjects(tsd))
		{
			String stepStatus = sJSON.getString(StepJSONKeyz.statusKey);
			if(stepStatus.equals(StatusMGMT.failed))fails.add(sJSON);
		}
	
		return fails;
	}

	public Set<JSONObject> getStepsSucceeded(TimeSpanData tsd)
	{
	
		Set<JSONObject> winner = new HashSet<>();
	
		for(JSONObject sJSON: allTheStepsOfAllActiveProjects(tsd))
		{
	
			String stepStatus = sJSON.getString(StepJSONKeyz.statusKey);
			
			if(StatusMGMT.success.equals(stepStatus))winner.add(sJSON);
		}
	
		return winner;
	}

	public Map<String, Integer> getStepsTerminated(TimeSpanData tsd)
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
					
					if(tsd.isInThisTimeSpan(tdt))
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

	public Set<String> getProjectNamesWithDLs(TimeSpanData tsd) throws IOException, URISyntaxException
	{
	
		Set<String> names = new HashSet<>();
	
		Set<JSONObject> set = getAllActiveProjectDLs(tsd);
		for(JSONObject pJSON: set)
		{
	
			LocalDateTime dldt = extractLDT(pJSON, ProjectJSONKeyz.DLDTKey);
			if(tsd.isInThisTimeSpan(dldt))
			{
				String name = pJSON.getString(ProjectJSONKeyz.nameKey);
				names.add(name);
			}
		}
	
		return names;
	}
	
}