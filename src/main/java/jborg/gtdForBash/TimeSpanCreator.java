package jborg.gtdForBash;

import static jborg.gtdForBash.ProjectJSONKeyz.ADTKey;
import static jborg.gtdForBash.ProjectJSONKeyz.DLDTKey;
import static jborg.gtdForBash.ProjectJSONKeyz.NDTKey;
import static jborg.gtdForBash.ProjectJSONKeyz.TDTKey;
import static jborg.gtdForBash.ProjectJSONKeyz.nameKey;
import static jborg.gtdForBash.ProjectJSONToolbox.extractLDT;
import static jborg.gtdForBash.ProjectJSONToolbox.isMODProject;
import static jborg.gtdForBash.ProjectJSONToolbox.projectHasNoDLDT;
import static jborg.gtdForBash.ProjectJSONToolbox.projectIsTerminated;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONObject;

import allgemein.ExactPeriode;
import javafx.util.Pair;
import jborg.gtdForBash.exceptions.TimeSpanException;

import static jborg.gtdForBash.ProjectJSONToolbox.*;

public class TimeSpanCreator
{

	private final LocalDateTime beginAnker;
	private final LocalDateTime endAnker;
	
	private LocalDateTime begin;
	private LocalDateTime end;

	Set<JSONObject> prjctSet = new HashSet<>();
	
	Map<ChronoUnit, List<TimeSpanData>> chronoUnitTimeSpanMap = new HashMap<>();
	
	
	List<Pair<LocalDateTime, LocalDateTime>> yearFrames = new ArrayList<>();
	List<Pair<LocalDateTime, LocalDateTime>> monthFrames = new ArrayList<>();
	List<Pair<LocalDateTime, LocalDateTime>> weekFrames = new ArrayList<>();
	List<Pair<LocalDateTime, LocalDateTime>> dayFrames = new ArrayList<>();
	List<Pair<LocalDateTime, LocalDateTime>> hourFrames = new ArrayList<>();
	
	public TimeSpanCreator(Set<JSONObject> prjctSet) throws IOException, URISyntaxException, TimeSpanException
	{
		//TODO: end must be after begin and both need to be not null!! Throw Exception!!!!
		if(prjctSet.isEmpty()) throw new TimeSpanException("No Projects to evaluate.");
		
	    Pair<String, LocalDateTime> oldPair = oldestLDTOverall();
	    Pair<String, LocalDateTime> youngPair = youngestLDTOverall();

	    LocalDateTime old = oldPair.getValue();
		LocalDateTime young = youngPair.getValue();
	    if(young.isBefore(old))throw new TimeSpanException("Evaluation went wrong this should not happen!");
	    
	    this.beginAnker = old;
		this.endAnker = young;
	}
	
	public List<Pair<LocalDateTime, LocalDateTime>> createTimeSpansFrame(ChronoUnit cu) throws IOException, URISyntaxException
	{

		List<Pair<LocalDateTime, LocalDateTime>> outputSpans = new ArrayList<>();
		
		LocalDateTime start = null;
		LocalDateTime end = null;

		if(cu.equals(ChronoUnit.YEARS))
		{
			start = LocalDateTime.of(beginAnker.getYear(), Month.JANUARY, 1, 0, 0);
			end = LocalDateTime.of(endAnker.getYear()+1, Month.JANUARY, 1, 0, 0).minusNanos(1);

			for(int year=start.getYear();year<=end.getYear();year++)
			{
				LocalDateTime a = LocalDateTime.of(year, Month.JANUARY, 1, 0, 0);
				LocalDateTime b = LocalDateTime.of(year+1, Month.JANUARY, 1, 0, 0).minusNanos(1);
	    	
				Pair<LocalDateTime, LocalDateTime> pair = new Pair<>(a, b);
				outputSpans.add(pair);
			}
		}
		
		if(cu.equals(ChronoUnit.MONTHS))
		{
			start = LocalDateTime.of(beginAnker.getYear(), beginAnker.getMonth(), 1, 0, 0);
			end = LocalDateTime.of(endAnker.getYear(), endAnker.getMonth(), 1, 0, 0).minusNanos(1);

			int k = end.getMonthValue()-start.getMonthValue();
			int y = end.getYear()-start.getYear();
			int d = y*12+k;
	
			for(int diffMonat=0;diffMonat<=d;diffMonat++)
			{
				LocalDateTime a = start.plusMonths(diffMonat);
				LocalDateTime b = a.plusMonths(1).minusNanos(1);
	    	
				Pair<LocalDateTime, LocalDateTime> pair = new Pair<>(a, b);
				outputSpans.add(pair);
			}

		}

		if(cu.equals(ChronoUnit.WEEKS))
		{
			
		}
		
		if(cu.equals(ChronoUnit.DAYS))
		{
			
		}

		if(cu.equals(ChronoUnit.HOURS))
		{
			
		}

    	return outputSpans;
	}

	private List<TimeSpanData> createListOfChronoUnitTimeSpan(ChronoUnit cu)
	{

		List<TimeSpanData> outputList = new ArrayList<>();

		int weekSpansSize = 100; //TODO://Remember:!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!1

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

		return list;
	}
	
	public void createListsOfAllChronoUnitTimeSpans()
	{
		
		Map<ChronoUnit, List<TimeSpanData>> outputMap = new HashMap<>();
		
		List<TimeSpanData> yearList = createListOfChronoUnitTimeSpan(ChronoUnit.YEARS);
		List<TimeSpanData> monthList = createListOfChronoUnitTimeSpan(ChronoUnit.MONTHS);
		List<TimeSpanData> weekList = createListOfChronoUnitTimeSpan(ChronoUnit.WEEKS);
		List<TimeSpanData> dayList = createListOfChronoUnitTimeSpan(ChronoUnit.DAYS);
		List<TimeSpanData> hourList = createListOfChronoUnitTimeSpan(ChronoUnit.HOURS);
		
		outputMap.put(ChronoUnit.YEARS, yearList);
		outputMap.put(ChronoUnit.MONTHS, monthList);
		outputMap.put(ChronoUnit.WEEKS, weekList);
		outputMap.put(ChronoUnit.DAYS, dayList);
		outputMap.put(ChronoUnit.HOURS, hourList);

		chronoUnitTimeSpanMap = outputMap;
	}

	public Pair<String, LocalDateTime> oldestLDTOverall() throws IOException, URISyntaxException, TimeSpanException
	{
	    Pair<String, LocalDateTime> oldPair = oldestProjectLDT(NDTKey);//Which is the eldest LDT!!!
	    
	    if(oldPair.getKey().trim().equals(""))
	    {
	    	oldPair = oldestProjectLDT(ADTKey);
	    	
		    if(oldPair.getKey().trim().equals(""))throw new TimeSpanException("No Project with DateTimes????");
	    	
	    }

	    return oldPair;
	}
	public Pair<String, LocalDateTime> oldestStepLDT(String jsonKey) throws IOException, URISyntaxException
	{

		LocalDateTime oldestLDT = LocalDateTime.now();
		
		String name = "";
		
		for(JSONObject pJSON: prjctSet)
		{

			if((isMODProject.test(pJSON)))continue;
			JSONObject step = getStepOfIndexN(0, pJSON);
			
			LocalDateTime ldt = extractLDT(step, jsonKey);
			if(ldt.isBefore(oldestLDT))
			{
				oldestLDT = ldt;
				name = pJSON.getString(nameKey);
			}
		}
		
    	Pair<String, LocalDateTime> output = new Pair<>(name, oldestLDT);
    	
    	return output;

	}

	public Pair<String, LocalDateTime> oldestProjectLDT(String jsonKey) throws IOException, URISyntaxException
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
	

    public Pair<String, LocalDateTime> youngestStepLDT(String jsonKey) throws IOException, URISyntaxException
    {

		LocalDateTime youngestLDT = oldestStepLDT(jsonKey).getValue();
		
		String name = "";
		
		for(JSONObject pJSON: prjctSet)
		{

			if((isMODProject.test(pJSON)))continue;
			JSONObject step = getStepOfIndexN(0, pJSON);
			
			LocalDateTime ldt = extractLDT(step, jsonKey);
			if(ldt.isAfter(youngestLDT))
			{
				youngestLDT = ldt;
				name = pJSON.getString(nameKey);
			}
		}

    	Pair<String, LocalDateTime> output = new Pair<>(name, youngestLDT);

    	return output;
    }

    public Pair<String, LocalDateTime> youngestProjectLDT(String jsonKey) throws IOException, URISyntaxException
    {

		LocalDateTime youngest = oldestProjectLDT(jsonKey).getValue();
		
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
    
    public Pair<String, LocalDateTime> youngestLDTOverall() throws IOException, URISyntaxException, TimeSpanException
    {
	    Pair<String, LocalDateTime> youngProjectDLDTPair = youngestProjectLDT(DLDTKey);
	    Pair<String, LocalDateTime> youngStepDLDTPair = youngestStepLDT(StepJSONKeyz.DLDTKey);
	    Pair<String, LocalDateTime> youngProjectTDTPair = youngestProjectLDT(TDTKey);
	    Pair<String, LocalDateTime> youngStepTDTPair = youngestStepLDT(StepJSONKeyz.TDTKey);
	    Pair<String, LocalDateTime> youngProjectNDTPair = youngestProjectLDT(NDTKey);
	    Pair<String, LocalDateTime> youngStepADTPair = youngestStepLDT(StepJSONKeyz.ADTKey);

	    Set<Pair<String, LocalDateTime>> set = new HashSet<>(Arrays.asList(youngProjectDLDTPair,
	    		youngStepDLDTPair, youngProjectTDTPair, youngStepTDTPair, youngProjectNDTPair,
	    		youngStepADTPair));
	    
	    Set<Pair<String, LocalDateTime>> set2 = new HashSet<>();
	    
	    for(Pair<String, LocalDateTime> pair: set)
	    {
	    	if(!pair.getKey().trim().equals(""))set2.add(pair);
	    }
	    
	    LocalDateTime youngest = oldestLDTOverall().getValue();
	    String name = "";
	    
	    for(Pair<String, LocalDateTime> pair: set2)
	    {
	    	if(pair.getValue().isAfter(youngest))
	    		youngest = pair.getValue();
	    		name = pair.getKey();
	    }
	    
	    if(name.trim().equals(""))throw new TimeSpanException("No Projects with Datetimes???");

	    return new Pair<>(name, youngest);
    }
}