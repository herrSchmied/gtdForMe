package jborg.gtdForBash;


import java.awt.Point;
import java.io.IOException;
import java.net.URISyntaxException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


import org.json.JSONObject;


import javafx.util.Pair;
import jborg.gtdForBash.exceptions.StatisticalToolsException;
import jborg.gtdForBash.exceptions.TimeSpanException;
import jborg.gtdForBash.exceptions.WeekDataException;


import someMath.NaturalNumberException;


import static jborg.gtdForBash.ProjectJSONToolbox.*;
import static jborg.gtdForBash.ProjectJSONKeyz.*;

public class StatisticalTools
{

	final Set<JSONObject> prjctSet;
	final TimeSpanCreator tsc;

	public StatisticalTools(Set<JSONObject> prjctSet) throws IOException, URISyntaxException, WeekDataException, TimeSpanException
	{

		this.prjctSet = prjctSet;
		tsc = new TimeSpanCreator(prjctSet);
	}

    //Remember: should this Method be here?
	public boolean pickAndCheckByName(ChronoUnit cu, String name, int n, JSONObject pJSON, String jsonKey) throws IOException, URISyntaxException, TimeSpanException
	{

        LocalDateTime ldt = extractLDT(pJSON, jsonKey);
        TimeSpanData tsd = tsc.getTimeSpanList(cu).get(n);
        
		return tsd.isInThisTimeSpan(ldt);
	}

	public Map<Integer, Map<String, LocalDateTime>> weeksLDTs(String jsonKey) throws IOException, URISyntaxException, StatisticalToolsException, TimeSpanException
	{
		
		Map<Integer, Map<String, LocalDateTime>> map = new HashMap<>();
		List<Map<String, LocalDateTime>> listOfMaps = new ArrayList<>();
		List<TimeSpanData> weekSpans = tsc.getTimeSpanList(ChronoUnit.WEEKS);
		
		for(int n=0;n<weekSpans.size();n++)
		{
			listOfMaps.add(new HashMap<>());
		}

		for(JSONObject pJSON: prjctSet)
		{
			
			String pName = pJSON.getString(nameKey);
			if((jsonKey.equals(TDTKey))&&(!projectIsTerminated.test(pJSON)))continue;
			if((jsonKey.equals(DLDTKey))&&(projectHasNoDLDT.test(pJSON)))continue;
			if((jsonKey.equals(ADTKey))&&(isMODProject.test(pJSON)))continue;
			
			LocalDateTime ldt = extractLDT(pJSON, jsonKey);
			Map<String, LocalDateTime> innerMap = listOfMaps.get(tsc.isInWhichTimeSpan(ChronoUnit.WEEKS, ldt));
			//DLDT might be in no week but to far in the future.

			innerMap.put(pName, ldt);
		}

		for(int n=0;n<weekSpans.size();n++)
		{
			map.put(n, listOfMaps.get(n));
		}

		return map;
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

	public Point weekWithMostLDTs(String jsonKey) throws IOException, URISyntaxException, StatisticalToolsException, TimeSpanException
	{

		Map<Integer, Map<String, LocalDateTime>> map = weeksLDTs(jsonKey);
		if(map.isEmpty())return null;
		
		int whichOne = 0;
		int howMany = 0;
		for(Integer n: map.keySet())
		{
			Map<String, LocalDateTime> set = map.get(n);
			int size = set.size();
			if(size>howMany)
			{
				howMany=size;
				whichOne=n;
			}
		}
		return new Point(whichOne, howMany);
	}
	
	public Set<JSONObject> getPrjctSet()
	{
		return prjctSet;
	}
	
	public TimeSpanCreator getTimeSpanCreator()
	{
		return tsc;
	}

	public JSONObject pickByName(String name)
	{

		for(JSONObject pJSON: prjctSet)
		{
			String pName = pJSON.getString(nameKey);
			if(name.equals(pName))return pJSON;
		}

		return null;
	}
	
	public String periodeResume(ChronoUnit cu, int unitNr) throws IOException, URISyntaxException, NaturalNumberException
	{

		/*
		 * WeekData wd = weekDatas.get(unitNr);
		 * 
		 * int prjctNDTs = wd.getProjectsWrittenDown().size(); int prjctsSucceded =
		 * wd.projectsSucceededThisWeek().size(); int prjctsFailed =
		 * wd.projectsFailedThisWeek().size(); int prjctDeadlineViolations =
		 * wd.projectsViolatedDLThisWeek().size(); int openPrjctDeadlines =
		 * wd.allActiveProjectsWithDLs().size(); int prjctDeadlinesHere =
		 * wd.projectDLsThisWeek().size(); Set<String> prjctMostPressingDeadline =
		 * wd.mostPressingProjectDeadline();
		 * 
		 * int stepADTs = 0; int stepsSucceded = 0; int stepsFailed = 0; int
		 * stepDeadlineViolation = 0; int openStepDeadlines = 0; int stepDeadlinesHere =
		 * 0; String stepMostPressingDeadline = "";
		 * 
		 * int l = cu.toString().length()-1; String unit = cu.toString().substring(l);
		 * 
		 * if(cu.equals(ChronoUnit.WEEKS)) {
		 * 
		 * 
		 * 
		 * Set<JSONObject> allTheNames = wd.allTheJSON();
		 * 
		 * for(JSONObject pJSON: allTheNames) {
		 * 
		 * if(!pJSON.has(ProjectJSONKeyz.stepArrayKey))break; JSONObject lastStep =
		 * getLastStepOfProject(pJSON);
		 * 
		 * LocalDateTime adt = extractLDT(lastStep, StepJSONKeyz.ADTKey);
		 * if(wd.isInThisWeek(adt))stepADTs++;
		 * 
		 * 
		 * LocalDateTime dldt = null; if(lastStep.has(StepJSONKeyz.DLDTKey))dldt =
		 * extractLDT(lastStep, StepJSONKeyz.DLDTKey);
		 * if(stepIsAlreadyTerminated(lastStep)) {
		 * 
		 * String status = lastStep.getString(StepJSONKeyz.statusKey);
		 * 
		 * 
		 * if(wd.isInThisWeek(dldt)) {
		 * if(StatusMGMT.success.equals(status))stepsSucceded++;
		 * if(StatusMGMT.failed.equals(status)) { stepsFailed++;
		 * if(lastStep.has(StepJSONKeyz.TDTNoteKey)) { String note =
		 * lastStep.getString(StepJSONKeyz.TDTNoteKey);
		 * if(note.equals(tdtNoteStpDLDTAbuse))stepDeadlineViolation++; } } } } else {
		 * if(dldt!=null)openStepDeadlines++;
		 * if(wd.isInThisWeek(dldt))stepDeadlinesHere++; } } }
		 * 
		 */
			String s = "";//"\nProjects:" + "\nNew Projects written: " + prjctNDTs +
		 /* "\nSuccesses: " + prjctsSucceded + "\nFailed: " + prjctsFailed +
		 * "\nDeadlineViolations: " + prjctDeadlineViolations + "\nOpen Deadlines: " +
		 * openPrjctDeadlines + "\nDeadlines this " + unit + ": " + prjctDeadlinesHere +
		 * "\nMost pressing Deadline: " + prjctMostPressingDeadline + "\n" + "\nSteps:"
		 * + "\nNew Steps written: " + stepADTs + "\nSuccesses: " + stepsSucceded +
		 * "\nFailed: " + stepsFailed + "\nDeadlineViolations: " + stepDeadlineViolation
		 * + "\nOpen Deadlines: " + openStepDeadlines + "\nDeadlines this " + unit +
		 * ": " + stepDeadlinesHere + "\nMost pressing Deadline: " +
		 * stepMostPressingDeadline;
		 */
		return s;
	}
}