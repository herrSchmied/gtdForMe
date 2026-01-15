package jborg.gtdForBash;



import static jborg.gtdForBash.ProjectJSONKeyz.*;


import java.io.IOException;

import java.net.URISyntaxException;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.temporal.ChronoUnit;

import java.util.ArrayList;

import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


import org.json.JSONObject;


import consoleTools.TerminalXDisplay;


import javafx.util.Pair;
import jborg.gtdForBash.exceptions.TimeSpanCreatorException;
import jborg.gtdForBash.exceptions.TimeSpanException;
import jborg.gtdForBash.exceptions.ToolBoxException;

import static jborg.gtdForBash.ProjectJSONToolbox.*;

public class TimeSpanCreator
{

	private final LocalDateTime beginAnker;
	private final LocalDateTime endAnker;
	

	Set<JSONObject> prjctSet = new HashSet<>();
	
	Map<ChronoUnit, List<TimeSpanData>> chronoUnitTimeSpanMap = new HashMap<>();
		
	private List<TimeSpanData> yearList;
	private List<TimeSpanData> monthList;
	private List<TimeSpanData> weekList;
	private List<TimeSpanData> dayList;
	private List<TimeSpanData> hourList;
	
	private static final LocalTime earlyInTheDay = LocalTime.of(0, 0);

    private static String comparatorJSONKey = NDTKey;
    
    public static final Comparator<JSONObject> ldtComparator = (a,b) ->
    {
    	
    	try
		{
			LocalDateTime ldtA = extractLDT(a, comparatorJSONKey);
			LocalDateTime ldtB = extractLDT(b, comparatorJSONKey);
			
			if(ldtA.isBefore(ldtB))return 1;
			if(ldtA.isAfter(ldtB))return -1;

		}
    	catch (IOException | URISyntaxException e)
		{
			e.printStackTrace();
		}
    
    	return 0;
    };

	public TimeSpanCreator(Set<JSONObject> prjctSet) throws IOException, URISyntaxException, TimeSpanException, ToolBoxException, TimeSpanCreatorException
	{

		if(prjctSet.isEmpty()) throw new TimeSpanException("No Projects to evaluate.");
		this.prjctSet = prjctSet;
		
	    Pair<String, LocalDateTime> oldPair = oldestLDTOverall();
	    Pair<String, LocalDateTime> youngPair = youngestLDTOverall();

	    LocalDateTime old = oldPair.getValue();
		LocalDateTime young = youngPair.getValue();
	    if(young.isBefore(old))throw new TimeSpanException("Evaluation went wrong this should not happen!");
	    
	    this.beginAnker = old;
		this.endAnker = young;
		
		createListsOfAllChronoUnitTimeSpans();
	}
	
	public List<Pair<LocalDateTime, LocalDateTime>> createTimeSpanFrames(ChronoUnit cu) throws IOException, URISyntaxException
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

		   	start = getLastMonday(beginAnker);
		   	
		   	//Get the very last Sunday. At the last NanoSeconde.
		   	DayOfWeek dow = endAnker.getDayOfWeek();
		   	int dowNr = dow.getValue();
		   	//Next Monday
		   	LocalDate endLD = endAnker.plusDays(8-dowNr).toLocalDate();
		   	//Tada!!!
		   	end = LocalDateTime.of(endLD, earlyInTheDay).minusNanos(1);
		   	
		   	int d = (int)ChronoUnit.WEEKS.between(start, end);
		   	
		   	for(int week=0;week<=d;week++)
		   	{

		   		LocalDateTime a = start.plusDays(week*7);
		   		LocalDateTime b = a.plusDays(7).minusNanos(1);

		   		Pair<LocalDateTime, LocalDateTime> pair = new Pair<>(a, b);

		   		outputSpans.add(pair);
		   	}

		}

		if(cu.equals(ChronoUnit.DAYS))
		{
			start = LocalDateTime.of(beginAnker.getYear(), beginAnker.getMonthValue(), beginAnker.getDayOfMonth(), 0, 0);
			end = LocalDateTime.of(endAnker.getYear(), endAnker.getMonthValue(), endAnker.getDayOfMonth(), 0, 0);
		
			int d=(int)ChronoUnit.DAYS.between(start, end);
			
			for(int day=0;day<=d;day++)
			{
				
				LocalDateTime a = start.plusDays(day);
				LocalDateTime b = a.plusDays(1);
				
				Pair<LocalDateTime, LocalDateTime> pair = new Pair<>(a, b);
				
				outputSpans.add(pair);
			}
		}

		if(cu.equals(ChronoUnit.HOURS))
		{
			start = LocalDateTime.of(beginAnker.getYear(), beginAnker.getMonthValue(), beginAnker.getDayOfMonth(), beginAnker.getHour(), 0);
			end = LocalDateTime.of(endAnker.getYear(), endAnker.getMonthValue(), endAnker.getDayOfMonth(), endAnker.getHour(), 0);

			int d = (int)ChronoUnit.HOURS.between(start, end);
			
			for(int hour=0;hour<=d;hour++)
			{
				
				LocalDateTime a = start.plusHours(hour);
				LocalDateTime b = a.plusHours(1);
				
				Pair<LocalDateTime, LocalDateTime> pair = new Pair<>(a, b);
				
				outputSpans.add(pair);
			}
		}

    	return outputSpans;
	}

	public static LocalDateTime getLastMonday(LocalDateTime ldt)
	{

		DayOfWeek dow = ldt.getDayOfWeek();
		int dowNr = dow.getValue();
	   	LocalDate startLD = ldt.minusDays(dowNr-1).toLocalDate();
	   	return LocalDateTime.of(startLD, earlyInTheDay);
	}

	private List<TimeSpanData> createListOfChronoUnitTimeSpan(ChronoUnit cu) throws IOException, URISyntaxException, TimeSpanException
	{

		List<TimeSpanData> outputList = new ArrayList<>();
		List<Pair<LocalDateTime, LocalDateTime>> frames = createTimeSpanFrames(cu);
		
		int howManyFrames = frames.size();
		
		for(int n=0;n<howManyFrames;n++)
		{

			Pair<LocalDateTime, LocalDateTime> span = frames.get(n);
			LocalDateTime start = span.getKey();
			LocalDateTime end = span.getValue();

			TimeSpanData tsd = new TimeSpanData(cu, start, end, n);

			for(JSONObject pJSON: prjctSet)
			{
				if(isActiveGivenTimeSpan(pJSON, tsd))tsd.addProjectActive(pJSON);
				if(isWrittenGivenTimeSpan(pJSON, tsd))tsd.addProjectWrittenDown(pJSON);
				if(isTerminatedGivenTimeSpan(pJSON, tsd))tsd.addProjectTerminated(pJSON);
			}
			outputList.add(tsd);
		}

		return outputList;
	}
	
	public void createListsOfAllChronoUnitTimeSpans() throws IOException, URISyntaxException, TimeSpanException
	{
		
		Map<ChronoUnit, List<TimeSpanData>> outputMap = new HashMap<>();
		
		yearList = createListOfChronoUnitTimeSpan(ChronoUnit.YEARS);
		monthList = createListOfChronoUnitTimeSpan(ChronoUnit.MONTHS);
		weekList = createListOfChronoUnitTimeSpan(ChronoUnit.WEEKS);
		dayList = createListOfChronoUnitTimeSpan(ChronoUnit.DAYS);
		hourList = createListOfChronoUnitTimeSpan(ChronoUnit.HOURS);
		
		outputMap.put(ChronoUnit.YEARS, yearList);
		outputMap.put(ChronoUnit.MONTHS, monthList);
		outputMap.put(ChronoUnit.WEEKS, weekList);
		outputMap.put(ChronoUnit.DAYS, dayList);
		outputMap.put(ChronoUnit.HOURS, hourList);

		chronoUnitTimeSpanMap = outputMap;
	}

	public List<JSONObject> sortedListProjectsByLDT(String jsonKey) throws IOException, URISyntaxException, TimeSpanCreatorException
	{
		
		LocalDateTime oldestLDT = LocalDateTime.now();

		String name = "";
		if(prjctSet.isEmpty())
		{
			System.out.println(TerminalXDisplay.formatBashStringBoldAndRed("No Projects!"));
			new Pair<>(name, oldestLDT);
		}

		List<JSONObject> projectsThatHaveThatKey = new ArrayList<>();
		for(JSONObject pJSON: prjctSet)
		{

			
			if((projectHasNoDLDT.test(pJSON))&&(jsonKey.equals(DLDTKey)))continue;
			if((!projectIsTerminated.test(pJSON))&&(jsonKey.equals(TDTKey)))continue;
			if((isMODProject.test(pJSON))&&(jsonKey.equals(ADTKey)))continue;
			
			projectsThatHaveThatKey.add(pJSON);
		}

		if(projectsThatHaveThatKey.isEmpty())
			throw new TimeSpanCreatorException("No Project with the desired LDT");
		
		comparatorJSONKey = jsonKey;
		projectsThatHaveThatKey.sort(ldtComparator);
		
		JSONObject pJSON = projectsThatHaveThatKey.getLast();
		name = pJSON.getString(nameKey);
		oldestLDT = extractLDT(pJSON, jsonKey);

				
		return projectsThatHaveThatKey;
	}

	public List<JSONObject> sortedListStepsByLDT(String jsonKey) throws IOException, URISyntaxException, TimeSpanCreatorException, ToolBoxException
	{
		
		LocalDateTime oldestLDT = LocalDateTime.now();

		String name = "";
		Set<JSONObject> stepSet = allSteps();
		
		if(stepSet.isEmpty())
		{
			System.out.println(TerminalXDisplay.formatBashStringBoldAndRed("No Steps!"));
			new Pair<>(name, oldestLDT);
		}

		List<JSONObject> stepsThatHaveThatKey = new ArrayList<>();
		for(JSONObject pJSON: prjctSet)
		{
		
			if((projectHasNoDLDT.test(pJSON))&&(jsonKey.equals(StepJSONKeyz.DLDTKey)))continue;
			if((!projectIsTerminated.test(pJSON))&&(jsonKey.equals(StepJSONKeyz.TDTKey)))continue;
			
			stepsThatHaveThatKey.add(pJSON);
		}

		if(stepsThatHaveThatKey.isEmpty())
			throw new TimeSpanCreatorException("No Project with the desired LDT");
		
		comparatorJSONKey = jsonKey;
		stepsThatHaveThatKey.sort(ldtComparator);
		
		JSONObject pJSON = stepsThatHaveThatKey.getLast();
		name = pJSON.getString(nameKey);
		oldestLDT = extractLDT(pJSON, jsonKey);

				
		return stepsThatHaveThatKey;
	}

	public static boolean isWrittenGivenTimeSpan(JSONObject pJSON, TimeSpanData tsd) throws IOException, URISyntaxException
	{

		LocalDateTime ldt  = extractLDT(pJSON, NDTKey);
		return tsd.isInThisTimeSpan(ldt);
	}

	public static boolean isActiveGivenTimeSpan(JSONObject pJSON, TimeSpanData tsd) throws IOException, URISyntaxException
	{

		if(isMODProject.test(pJSON))return false;
		
		LocalDateTime activatedLDT;
		activatedLDT = extractLDT(pJSON, ADTKey);
		
		if(tsd.isAfterThisTimeSpan(activatedLDT))return false;
		
		if(tsd.isInThisTimeSpan(activatedLDT))return true;

		if(tsd.isBeforeThisTimeSpan(activatedLDT)&&projectIsTerminated.test(pJSON))
		{
			LocalDateTime tdt = extractLDT(pJSON, TDTKey);
			if(tsd.isInThisTimeSpan(tdt)||tsd.isAfterThisTimeSpan(tdt))return true;
		}

		if((tsd.isBeforeThisTimeSpan(activatedLDT))&&(!projectHasNoDLDT.test(pJSON)))
		{
			LocalDateTime dldt = extractLDT(pJSON, DLDTKey);
			if((tsd.isInThisTimeSpan(dldt))||(tsd.isAfterThisTimeSpan(dldt)))return true;
		}


		return false;
	}
	
	public static boolean isTerminatedGivenTimeSpan(JSONObject pJSON, TimeSpanData tsd) throws IOException, URISyntaxException
	{
		if(isMODProject.test(pJSON))return false;
		if(projectIsTerminated.test(pJSON))
		{
			LocalDateTime tdt = extractLDT(pJSON, TDTKey);
			return tsd.isInThisTimeSpan(tdt);
		}
		
		return false;
	}

    public int isInWhichTimeSpan(ChronoUnit cu, LocalDateTime ldt) throws IOException, URISyntaxException, TimeSpanException
    {

    	if(cu==null)throw new TimeSpanException("Null is not a ChronoUnit.");
    	if(ldt==null)throw new TimeSpanException("Can't use LDT to evaluate because its null.");

    	List<TimeSpanData> list = null;

    	if(cu.equals(ChronoUnit.YEARS))list = yearList;
    	if(cu.equals(ChronoUnit.MONTHS))list = monthList;
    	if(cu.equals(ChronoUnit.WEEKS))list = weekList;
    	if(cu.equals(ChronoUnit.DAYS))list = dayList;
    	if(cu.equals(ChronoUnit.HOURS))list = hourList;

    	if(list==null)throw new TimeSpanException("Unsupported ChronoUnit.");
    	
    	return list.stream()
    		    .filter(tsd -> tsd.isInThisTimeSpan(ldt))
    		    .map(TimeSpanData::getTimeNr)
    		    .findFirst()
    		    .orElseThrow(() -> new TimeSpanException("No TimeSpan found for given date: " + ldt));
    }

    public List<TimeSpanData> getTimeSpanList(ChronoUnit cu) throws TimeSpanException
    {
    	
    	if(cu==null)throw new TimeSpanException("Null is not a ChronoUnit.");
    	
       	if(cu.equals(ChronoUnit.YEARS))return yearList;
    	if(cu.equals(ChronoUnit.MONTHS))return monthList;
    	if(cu.equals(ChronoUnit.WEEKS))return weekList;
    	if(cu.equals(ChronoUnit.DAYS))return dayList;
    	if(cu.equals(ChronoUnit.HOURS))return hourList;
    	
    	throw new TimeSpanException("Unsupported ChronoUnit.");
    }
    
    public static void setComparatorJSONKey(String jsonKey)
    {
 
    	comparatorJSONKey = jsonKey;
    }
    
    public static String getComparatorJSONKey()
    {
    	return comparatorJSONKey;
    }
    
    public Set<JSONObject> allSteps() throws ToolBoxException
    {

    	Set<JSONObject> output = new HashSet<>();
    	
    	for(JSONObject pJSON: prjctSet)
    	{
			if((isMODProject.test(pJSON)))continue;

    		int s = getIndexOfLastStepInPrjct(pJSON);
    		for(int n=0;n<s;n++)
    		{
    			JSONObject sJSON = getStepOfIndexN(n, pJSON);
    			output.add(sJSON);
    		}
    	}

    	return output;
    }
    
    public Pair<String, LocalDateTime> youngestLDTInThisStep(JSONObject sJSON) throws IOException, URISyntaxException, TimeSpanCreatorException
    {

    	if(!stepHasNoDLDT.test(sJSON))
    	{
    		LocalDateTime dldt = extractLDT(sJSON, StepJSONKeyz.DLDTKey);
    		return new Pair<>(StepJSONKeyz.DLDTKey, dldt);
    	}

    	if(sJSON.has(StepJSONKeyz.TDTKey))
    	{
    		LocalDateTime tdt = extractLDT(sJSON, StepJSONKeyz.TDTKey);
    		return new Pair<>(StepJSONKeyz.TDTKey, tdt);
    	}

    	LocalDateTime ndt = extractLDT(sJSON, StepJSONKeyz.ADTKey);
    	
    	return new Pair<>(StepJSONKeyz.ADTKey, ndt);
    }

    public Pair<String, LocalDateTime> youngestLDTInThisProject(JSONObject pJSON) throws IOException, URISyntaxException, TimeSpanCreatorException
    {

    	String jsonKey = "";
    	LocalDateTime ldt = oldestLDTOverall().getValue();

    	if(!projectHasNoDLDT.test(pJSON))
    	{
    		LocalDateTime dldt = extractLDT(pJSON, DLDTKey);
    		return new Pair<>(DLDTKey, dldt);
    	}

    	if(pJSON.has(TDTKey))
    	{
    		ldt = extractLDT(pJSON, TDTKey);
    		jsonKey = TDTKey;
    	}
   	
   		LocalDateTime ndt = extractLDT(pJSON, NDTKey);
   		if(ndt.isAfter(ldt))
   		{
   			ldt = ndt;
   			jsonKey = NDTKey;
  		}
    	
   		if(!isMODProject.test(pJSON))
   		{
   			JSONObject sJSON = getLastStep(pJSON);
   			Pair<String, LocalDateTime> candidate = youngestLDTInThisStep(sJSON);
   			if(candidate.getValue().isAfter(ldt))
   			{
   				ldt = candidate.getValue();
   				jsonKey = candidate.getKey();
   			}
   		}
 
    	return new Pair<>(jsonKey, ldt);
    }

    public Pair<String, LocalDateTime> youngestLDTOverall() throws IOException, URISyntaxException, TimeSpanCreatorException
    {

    	Pair<String, LocalDateTime> old = oldestLDTOverall();
    	String name = old.getKey();
    	LocalDateTime ldt = old.getValue();
    	
    	for(JSONObject pJSON: prjctSet)
    	{
    		Pair<String, LocalDateTime> candidate = youngestLDTInThisProject(pJSON);
    		if(candidate.getValue().isAfter(ldt))
    		{
    			name = pJSON.getString(nameKey);
    			ldt = candidate.getValue();
    		}
    	}
    	
    	
    	return new Pair<>(name, ldt);
    }
    
    public Pair<String, LocalDateTime> oldestLDTOverall() throws IOException, URISyntaxException, TimeSpanCreatorException
    {
   	
    	List<JSONObject> list = sortedListProjectsByLDT(NDTKey);//Which is thee oldest LDT!
    	
    	JSONObject pJSON = list.getLast();
    	String name = pJSON.getString(nameKey);
    	LocalDateTime ldt = extractLDT(pJSON, NDTKey);
    	
    	return new Pair<>(name, ldt);
    }
    
    public Pair<String, LocalDateTime> oldestLDTInThisProject(JSONObject pJSON) throws IOException, URISyntaxException, TimeSpanCreatorException
    {

    	LocalDateTime ldt = extractLDT(pJSON, NDTKey);
    	
    	return new Pair<>(NDTKey, ldt);
    }
    
    public Pair<String, LocalDateTime> oldestLDTInThisStep(JSONObject sJSON) throws IOException, URISyntaxException, TimeSpanCreatorException
    {

    	LocalDateTime ldt = extractLDT(sJSON, NDTKey);
    	
    	return new Pair<>(NDTKey, ldt);
    }
}