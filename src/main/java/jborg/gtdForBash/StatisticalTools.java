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
import jborg.gtdForBash.exceptions.WeekDataException;


import someMath.NaturalNumberException;


import static jborg.gtdForBash.ProjectJSONToolbox.*;
import static jborg.gtdForBash.ProjectJSONKeyz.*;

public class StatisticalTools
{

	final Set<JSONObject> prjctSet;
	final List<Pair<LocalDate, LocalDate>> weekSpans;
	final List<WeekData> weekDatas;

	public StatisticalTools(Set<JSONObject> prjctSet) throws IOException, URISyntaxException, WeekDataException
	{

		this.prjctSet = prjctSet;
		weekSpans = computeWeekSpans();
		weekDatas = computeWeekDataList();
	}
	
	public List<Pair<LocalDate, LocalDate>> computeWeekSpans() throws IOException, URISyntaxException
	{

		List<Pair<LocalDate, LocalDate>> weekSpans = new ArrayList<>();
		
	    LocalDateTime old = oldestLDT(NDTKey).getValue();
	 
	    LocalDateTime maxDLDT = youngestLDT(ProjectJSONKeyz.DLDTKey).getValue();
	    LocalDateTime jetzt = LocalDateTime.now();
	    
	    LocalDate maxLD;
	    if(jetzt.isBefore(maxDLDT))maxLD = maxDLDT.toLocalDate();
	    else maxLD = jetzt.toLocalDate();
	    

	   	LocalDate currentMonday = getLastMonday(old);
	    	
    	while(true)
	   	{
	   		LocalDate currentSunday = currentMonday.plusDays(6);
	   		Pair<LocalDate, LocalDate> week = new Pair<>(currentMonday, currentSunday);
	   		weekSpans.add(week);
    		currentMonday = currentMonday.plusDays(7);
    		if(currentMonday.isAfter(maxLD))break;
    	}
	    	
    	return weekSpans;
	}
	
	public static LocalDate getLastMonday(LocalDateTime ldt)
	{

		DayOfWeek dow = ldt.getDayOfWeek();
		int dowNr = dow.getValue();
	   	return ldt.minusDays(dowNr-1).toLocalDate();
	}

	public List<WeekData> computeWeekDataList() throws IOException, URISyntaxException, WeekDataException
	{

		List<WeekData> outputList = new ArrayList<>();
		int weekSpansSize = weekSpans.size();

		for(int weekNr=0;weekNr<weekSpansSize;weekNr++)
		{

			Pair<LocalDate, LocalDate> span = weekSpans.get(weekNr);
			LocalDate wStart = span.getKey();
			WeekData wd = new WeekData(wStart, weekNr);

			for(JSONObject pJSON: prjctSet)
			{
				if(isActiveGivenWeek(pJSON, wd))wd.addProjectActive(pJSON);
				if(isWrittenGivenWeek(pJSON, wd))wd.addProjectWrittenDown(pJSON);
				if(isTerminatedGivenWeek(pJSON, wd))wd.addProjectTerminated(pJSON);
			}
			outputList.add(wd);
		}

		return outputList;
	}

	public static boolean isWrittenGivenWeek(JSONObject pJSON, WeekData wd) throws IOException, URISyntaxException
	{
		
		LocalDateTime ldt  = extractLDT(pJSON, NDTKey);

		return wd.isInThisWeek(ldt);
	}

	public static boolean isActiveGivenWeek(JSONObject pJSON, WeekData wd) throws IOException, URISyntaxException
	{

		if(isMODProject.test(pJSON))return false;
		
		LocalDateTime activatedLDT;
		activatedLDT = extractLDT(pJSON, ADTKey);
		
		if(wd.isAfterThisWeek(activatedLDT))return false;
		
		if(wd.isInThisWeek(activatedLDT))return true;

		if(wd.isBeforeThisWeek(activatedLDT)&&projectIsTerminated.test(pJSON))
		{
			LocalDateTime tdt = extractLDT(pJSON, TDTKey);
			if(wd.isInThisWeek(tdt)||wd.isAfterThisWeek(tdt))return true;
		}

		if((wd.isBeforeThisWeek(activatedLDT))&&(!projectHasNoDLDT.test(pJSON)))
		{
			LocalDateTime dldt = extractLDT(pJSON, DLDTKey);
			if((wd.isInThisWeek(dldt))||(wd.isAfterThisWeek(dldt)))return true;
		}


		return false;
	}

	public static boolean isTerminatedGivenWeek(JSONObject pJSON, WeekData wd) throws IOException, URISyntaxException
	{
		if(isMODProject.test(pJSON))return false;
		if(projectIsTerminated.test(pJSON))
		{
			LocalDateTime tdt = extractLDT(pJSON, TDTKey);
			return wd.isInThisWeek(tdt);
		}
		
		return false;
	}

    public Pair<String, LocalDateTime> oldestLDT(String jsonKey) throws IOException, URISyntaxException
    {
		LocalDateTime oldestLDT = LocalDateTime.now();
		
		String name = "";
		
		for(JSONObject pJSON: prjctSet)
		{

			if((projectHasNoDLDT.test(pJSON))&&(jsonKey.equals(DLDTKey)))continue;
			if((!projectIsTerminated.test(pJSON))&&(jsonKey.equals(TDTKey)))continue;
			if((isMODProject.test(pJSON))&&(jsonKey.equals(ADTKey)))continue;
			
			LocalDateTime ldt = extractLDT(pJSON, jsonKey);
			if(ldt.isBefore(oldestLDT))
			{
				oldestLDT = ldt;
				name = pJSON.getString(nameKey);
			}
		}
		
    	Pair<String, LocalDateTime> output = new Pair<>(name, oldestLDT);
    	
    	return output;
    }
    
    public Pair<String, LocalDateTime> youngestLDT(String jsonKey) throws IOException, URISyntaxException
    {

		LocalDateTime youngest = oldestLDT(jsonKey).getValue();
		
		String name = "";
		
		for(JSONObject pJSON: prjctSet)
		{
			if((jsonKey.equals(DLDTKey)&&projectHasNoDLDT.test(pJSON)))continue;
			if((jsonKey.equals(ADTKey)&&isMODProject.test(pJSON)))continue;
			if((jsonKey.equals(TDTKey)&&!projectIsTerminated.test(pJSON)))continue;
				
			LocalDateTime ldt = extractLDT(pJSON, jsonKey);
			if(ldt.isAfter(youngest))
			{
				youngest = ldt;
				name = pJSON.getString(ProjectJSONKeyz.nameKey);
			}
		}
		
    	Pair<String, LocalDateTime> output = new Pair<>(name, youngest);
    	
    	return output;
    }

    public int isInWhichWeek(LocalDateTime ldt) throws IOException, URISyntaxException, StatisticalToolsException
    {

    	return weekDatas.stream()
    		    .filter(wd -> wd.isInThisWeek(ldt))
    		    .map(WeekData::getWeekNr)
    		    .findFirst()
    		    .orElseThrow(() -> new StatisticalToolsException("No week found for given date: " + ldt));
    }

    //Remember: should this Method be here?
	public boolean pickAndCheckByName(String name, int weekNr, JSONObject pJSON, String jsonKey) throws IOException, URISyntaxException
	{

        LocalDateTime ldt = ProjectJSONToolbox.extractLDT(pJSON, jsonKey);
        WeekData wd = weekDatas.get(weekNr);
		return wd.isInThisWeek(ldt);
	}

	public Map<Integer, Map<String, LocalDateTime>> weeksLDTs(String jsonKey) throws IOException, URISyntaxException, StatisticalToolsException
	{
		
		Map<Integer, Map<String, LocalDateTime>> map = new HashMap<>();
		List<Map<String, LocalDateTime>> listOfMaps = new ArrayList<>();
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
			Map<String, LocalDateTime> innerMap = listOfMaps.get(isInWhichWeek(ldt));
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

	public Point weekWithMostLDTs(String jsonKey) throws IOException, URISyntaxException, StatisticalToolsException
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

	public String periodeResume(ChronoUnit cu, int unitNr) throws IOException, URISyntaxException, NaturalNumberException
	{

		WeekData wd = weekDatas.get(unitNr);

		int prjctNDTs = wd.getProjectsWrittenDown().size();
		int prjctsSucceded = wd.projectsSucceededThisWeek().size();
		int prjctsFailed = wd.projectsFailedThisWeek().size();
		int prjctDeadlineViolations = wd.projectsViolatedDLThisWeek().size();
		int openPrjctDeadlines = wd.allActiveProjectsWithDLs().size();
		int prjctDeadlinesHere = wd.projectDLsThisWeek().size();
		Set<String> prjctMostPressingDeadline = wd.mostPressingProjectDeadline();

		int stepADTs = 0;
		int stepsSucceded = 0;
		int stepsFailed = 0;
		int stepDeadlineViolation = 0;
		int openStepDeadlines = 0;
		int stepDeadlinesHere = 0;
		String stepMostPressingDeadline = "";

		int l = cu.toString().length()-1;
		String unit = cu.toString().substring(l);
		
		if(cu.equals(ChronoUnit.WEEKS))
		{

		
		
			Set<JSONObject> allTheNames = wd.allTheJSON();
			
			for(JSONObject pJSON: allTheNames)
			{
		
				if(!pJSON.has(ProjectJSONKeyz.stepArrayKey))break;
				JSONObject lastStep = getLastStepOfProject(pJSON);
				
				LocalDateTime adt = extractLDT(lastStep, StepJSONKeyz.ADTKey);
				if(wd.isInThisWeek(adt))stepADTs++;

				
				LocalDateTime dldt = null;
				if(lastStep.has(StepJSONKeyz.DLDTKey))dldt = extractLDT(lastStep, StepJSONKeyz.DLDTKey);
				if(stepIsAlreadyTerminated(lastStep))
				{

					String status = lastStep.getString(StepJSONKeyz.statusKey);
					

					if(wd.isInThisWeek(dldt))
					{
						if(StatusMGMT.success.equals(status))stepsSucceded++;
						if(StatusMGMT.failed.equals(status))
						{
							stepsFailed++;
							if(lastStep.has(StepJSONKeyz.TDTNoteKey))
							{
								String note = lastStep.getString(StepJSONKeyz.TDTNoteKey);
								if(note.equals(tdtNoteStpDLDTAbuse))stepDeadlineViolation++;
							}
						}
					}					
				}
				else
				{
					if(dldt!=null)openStepDeadlines++;
					if(wd.isInThisWeek(dldt))stepDeadlinesHere++;
				}
			}
		}

		String s = "\nProjects:"
				 + "\nNew Projects written: " + prjctNDTs
				 + "\nSuccesses: " + prjctsSucceded
				 + "\nFailed: " + prjctsFailed
				 + "\nDeadlineViolations: " + prjctDeadlineViolations
				 + "\nOpen Deadlines: " + openPrjctDeadlines
				 + "\nDeadlines this " + unit + ": " + prjctDeadlinesHere
				 + "\nMost pressing Deadline: " + prjctMostPressingDeadline
				 + "\n"
				 + "\nSteps:"
				 + "\nNew Steps written: " + stepADTs
				 + "\nSuccesses: " + stepsSucceded
				 + "\nFailed: " + stepsFailed
				 + "\nDeadlineViolations: " + stepDeadlineViolation
				 + "\nOpen Deadlines: " + openStepDeadlines
				 + "\nDeadlines this " + unit + ": " + stepDeadlinesHere
		 		 + "\nMost pressing Deadline: " + stepMostPressingDeadline;

		return s;
	}

	public List<Pair<LocalDate, LocalDate>> getWeekSpans()
	{
		return weekSpans;
	}
	
	public Set<JSONObject> getPrjctSet()
	{
		return prjctSet;
	}
	
	public List<WeekData> getWeekDatas()//Faster
	{
		return weekDatas;
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
}