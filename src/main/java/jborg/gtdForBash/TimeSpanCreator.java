package jborg.gtdForBash;



import static jborg.gtdForBash.ProjectJSONKeyz.*;


import java.io.IOException;

import java.net.URISyntaxException;
import java.time.Clock;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.temporal.ChronoUnit;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import org.json.JSONObject;

import CollectionTools.CollectionManipulation;
import consoleTools.TerminalXDisplay;


import javafx.util.Pair;
import jborg.gtdForBash.exceptions.StatisticalToolsException;
import jborg.gtdForBash.exceptions.TimeSpanCreatorException;
import jborg.gtdForBash.exceptions.TimeSpanException;
import jborg.gtdForBash.exceptions.ToolBoxException;
import someMath.exceptions.CollectionException;
import someMath.exceptions.NaturalNumberException;

import static jborg.gtdForBash.ProjectJSONToolBox.*;

public class TimeSpanCreator
{

	
	private final Clock clock;
	private final LocalDateTime beginAnker;
	private final LocalDateTime endAnker;
	

	private Set<JSONObject> prjctSet = new HashSet<>();

	private Map<ChronoUnit, List<TimeSpanData>> chronoUnitTimeSpanMap = new HashMap<>();
		
	private List<TimeSpanData> yearList = new ArrayList<>();
	private List<TimeSpanData> monthList = new ArrayList<>();
	private List<TimeSpanData> weekList = new ArrayList<>();
	private List<TimeSpanData> dayList = new ArrayList<>();
	private List<TimeSpanData> hourList = new ArrayList<>();
	
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

    public static final Comparator<TimeSpanData> TSDComparator = (a,b) ->
    {

    	return b.getTimeNr()-a.getTimeNr();
    };

    public TimeSpanCreator(Set<JSONObject> prjctSet, Clock clock, List<List<TimeSpanData>> listOfTSDLists) throws TimeSpanException, IOException, URISyntaxException, TimeSpanCreatorException
    {
    	
		if(prjctSet==null) throw new TimeSpanException("ProjectSet can't be null.");
		this.prjctSet = prjctSet;
		this.clock = clock;
		
		if(prjctSet.isEmpty())
		{
			LocalDateTime now = LocalDateTime.now(clock);
			this.beginAnker = now;
			this.endAnker = now;
		}
		else
		{
			
			Pair<String, LocalDateTime> oldPair = oldestLDTOverall();
			Pair<String, LocalDateTime> youngPair = youngestLDTOverall();
	    
			this.beginAnker = oldPair.getValue();
			this.endAnker = youngPair.getValue();
		}
		
		this.yearList.addAll(listOfTSDLists.get(GTDCLI.yearListIndex));
		//Remember: Does this sort the right way around?????
		Collections.sort(yearList, TSDComparator);
		
		this.monthList.addAll(listOfTSDLists.get(GTDCLI.monthListIndex));
		//Remember: Does this sort the right way around?????
		Collections.sort(monthList, TSDComparator);
		
		this.weekList.addAll(listOfTSDLists.get(GTDCLI.weekListIndex));
		//Remember: Does this sort the right way around?????
		Collections.sort(weekList, TSDComparator);
		
		this.dayList.addAll(listOfTSDLists.get(GTDCLI.dayListIndex));
		//Remember: Does this sort the right way around?????
		Collections.sort(dayList, TSDComparator);
		
		this.hourList.addAll(listOfTSDLists.get(GTDCLI.hourListIndex));
		//Remember: Does this sort the right way around?????
		Collections.sort(hourList, TSDComparator);

		pickupListsAndExtrapolateThem(ChronoUnit.YEARS);
		pickupListsAndExtrapolateThem(ChronoUnit.MONTHS);
		pickupListsAndExtrapolateThem(ChronoUnit.WEEKS);
		pickupListsAndExtrapolateThem(ChronoUnit.DAYS);
		pickupListsAndExtrapolateThem(ChronoUnit.HOURS);
    }

	public TimeSpanCreator(Set<JSONObject> prjctSet, Clock clock) throws IOException, URISyntaxException, TimeSpanException, ToolBoxException, TimeSpanCreatorException
	{

		if(prjctSet==null) throw new TimeSpanException("ProjectSet can't be null.");
		this.prjctSet = prjctSet;
		this.clock = clock;

		if(prjctSet.isEmpty())
		{
			LocalDateTime now = LocalDateTime.now(clock);
			this.beginAnker = now;
			this.endAnker = now;
		}
		else
		{
			
			Pair<String, LocalDateTime> oldPair = oldestLDTOverall();
			Pair<String, LocalDateTime> youngPair = youngestLDTOverall();
	    
			this.beginAnker = oldPair.getValue();
			this.endAnker = youngPair.getValue();
		}
		
		createListsOfAllChronoUnitTimeSpans();
	}
	
	public List<Pair<LocalDateTime, LocalDateTime>> createTimeSpanFrames(ChronoUnit cu, LocalDateTime startAnker, LocalDateTime stopAnker) throws IOException, URISyntaxException
	{

		List<Pair<LocalDateTime, LocalDateTime>> outputSpans = new ArrayList<>();
		if(startAnker.equals(stopAnker))return outputSpans;

		LocalDateTime start = null;
		LocalDateTime end = null;

		if(cu.equals(ChronoUnit.YEARS))
		{
			start = LocalDateTime.of(startAnker.getYear(), Month.JANUARY, 1, 0, 0);
			end = LocalDateTime.of(stopAnker.getYear()+1, Month.JANUARY, 1, 0, 0).minusNanos(1);

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
			start = LocalDateTime.of(startAnker.getYear(), startAnker.getMonth(), 1, 0, 0);
			end = LocalDateTime.of(stopAnker.getYear(), stopAnker.getMonth(), 1, 0, 0).minusNanos(1);

			int k = end.getMonthValue()-start.getMonthValue()+1;
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

		   	start = getLastMonday(startAnker);
		   	
		   	//Get the very last Sunday. At the last NanoSeconde.
		   	DayOfWeek dow = stopAnker.getDayOfWeek();
		   	int dowNr = dow.getValue();
		   	//Next Monday
		   	LocalDate endLD = stopAnker.plusDays(8-dowNr).toLocalDate();
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
			start = LocalDateTime.of(startAnker.getYear(), startAnker.getMonthValue(), startAnker.getDayOfMonth(), 0, 0);
			end = LocalDateTime.of(stopAnker.getYear(), stopAnker.getMonthValue(), stopAnker.getDayOfMonth(), 0, 0);
		
			int d=(int)ChronoUnit.DAYS.between(start, end);
			
			for(int day=0;day<=d;day++)
			{
				
				LocalDateTime a = start.plusDays(day);
				LocalDateTime b = a.plusDays(1).minusNanos(1);
				
				Pair<LocalDateTime, LocalDateTime> pair = new Pair<>(a, b);
				
				outputSpans.add(pair);
			}
		}

		if(cu.equals(ChronoUnit.HOURS))
		{
			start = LocalDateTime.of(startAnker.getYear(), startAnker.getMonthValue(), startAnker.getDayOfMonth(), startAnker.getHour(), 0);
			end = LocalDateTime.of(stopAnker.getYear(), stopAnker.getMonthValue(), stopAnker.getDayOfMonth(), stopAnker.getHour(), 0);

			int d = (int)ChronoUnit.HOURS.between(start, end);
			
			for(int hour=0;hour<=d;hour++)
			{
				
				LocalDateTime a = start.plusHours(hour);
				LocalDateTime b = a.plusHours(1).minusNanos(1);
				
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

	
	private void pickupListsAndExtrapolateThem(ChronoUnit cu) throws IOException, URISyntaxException, TimeSpanException
	{

		List<TimeSpanData> tsdList = yearList;

		if(cu.equals(ChronoUnit.MONTHS))tsdList = monthList;
		if(cu.equals(ChronoUnit.WEEKS))tsdList = weekList;
		if(cu.equals(ChronoUnit.DAYS))tsdList = dayList;
		if(cu.equals(ChronoUnit.HOURS))tsdList = hourList;
		
		if(tsdList.isEmpty())
		{
			tsdList.addAll(createListOfChronoUnitTimeSpan(cu));
			return;
		}
		
		TimeSpanData lastTSD = tsdList.getLast();
		
		LocalDateTime startAnker = lastTSD.getEnd().plusNanos(1);
		int unitTimeNr = lastTSD.getTimeNr();
		System.out.println("Pickup Nr: " + unitTimeNr);
		tsdList.addAll(pickup(cu, unitTimeNr, startAnker, endAnker));
	}
	
	private List<TimeSpanData> pickup(ChronoUnit cu, int unitTimeNr, LocalDateTime startAnker, LocalDateTime stopAnker) throws IOException, URISyntaxException, TimeSpanException
	{

		List<TimeSpanData> outputList = new ArrayList<>();
		List<Pair<LocalDateTime, LocalDateTime>> frames = createTimeSpanFrames(cu, startAnker, stopAnker);
		
		int howManyFrames = frames.size();
		int plus = unitTimeNr+1;
		for(int n=plus;n<howManyFrames+plus;n++)
		{

			Pair<LocalDateTime, LocalDateTime> span = frames.get(n);
			LocalDateTime start = span.getKey();
			LocalDateTime end = span.getValue();

			TimeSpanData tsd = new TimeSpanData(cu, start, end, n);

			for(JSONObject pJSON: prjctSet)
			{
				
				String prjctStr = pJSON.toString();
				if(isActiveGivenTimeSpan(pJSON, tsd))tsd.addProjectActive(prjctStr);
				if(isWrittenGivenTimeSpan(pJSON, tsd))tsd.addProjectWrittenDown(prjctStr);
				if(isTerminatedGivenTimeSpan(pJSON, tsd))tsd.addProjectTerminated(prjctStr);
			}
			outputList.add(tsd);
		}

		return outputList;

	}

	private List<TimeSpanData> createListOfChronoUnitTimeSpan(ChronoUnit cu) throws IOException, URISyntaxException, TimeSpanException
	{

		List<TimeSpanData> outputList = new ArrayList<>();
		List<Pair<LocalDateTime, LocalDateTime>> frames = createTimeSpanFrames(cu, beginAnker, endAnker);
		
		int howManyFrames = frames.size();
		
		for(int n=0;n<howManyFrames;n++)
		{

			Pair<LocalDateTime, LocalDateTime> span = frames.get(n);
			LocalDateTime start = span.getKey();
			LocalDateTime end = span.getValue();

			TimeSpanData tsd = new TimeSpanData(cu, start, end, n);

			for(JSONObject pJSON: prjctSet)
			{
				String prjctStr = pJSON.toString();
				if(isActiveGivenTimeSpan(pJSON, tsd))tsd.addProjectActive(prjctStr);
				if(isWrittenGivenTimeSpan(pJSON, tsd))tsd.addProjectWrittenDown(prjctStr);
				if(isTerminatedGivenTimeSpan(pJSON, tsd))tsd.addProjectTerminated(prjctStr);
			}
			outputList.add(tsd);
		}

		return outputList;
	}
	
	public void createListsOfAllChronoUnitTimeSpans() throws IOException, URISyntaxException, TimeSpanException
	{
		
		yearList = createListOfChronoUnitTimeSpan(ChronoUnit.YEARS);
		monthList = createListOfChronoUnitTimeSpan(ChronoUnit.MONTHS);
		weekList = createListOfChronoUnitTimeSpan(ChronoUnit.WEEKS);
		dayList = createListOfChronoUnitTimeSpan(ChronoUnit.DAYS);
		hourList = createListOfChronoUnitTimeSpan(ChronoUnit.HOURS);
		
		chronoUnitTimeSpanMap.put(ChronoUnit.YEARS, yearList);
		chronoUnitTimeSpanMap.put(ChronoUnit.MONTHS, monthList);
		chronoUnitTimeSpanMap.put(ChronoUnit.WEEKS, weekList);
		chronoUnitTimeSpanMap.put(ChronoUnit.DAYS, dayList);
		chronoUnitTimeSpanMap.put(ChronoUnit.HOURS, hourList);
	}

	public List<JSONObject> sortedListProjectsByLDT(String jsonKey) throws IOException, URISyntaxException, TimeSpanCreatorException
	{
		
		if(prjctSet.isEmpty())
		{
			System.out.println(TerminalXDisplay.formatBashStringBoldAndRed("No Projects!"));
			return new ArrayList<JSONObject>();
		}

		List<JSONObject> projectsThatHaveThatKey = new ArrayList<>();
		for(JSONObject pJSON: prjctSet)
		{

			
			if((projectHasNoDLDT.test(pJSON))&&(jsonKey.equals(DLDTKey)))continue;
			if((!projectIsTerminated.test(pJSON))&&(jsonKey.equals(TDTKey)))continue;
			if((isMODProject.test(pJSON))&&(jsonKey.equals(ADTKey)))continue;
			
			projectsThatHaveThatKey.add(pJSON);
		}

		if(projectsThatHaveThatKey.isEmpty())return projectsThatHaveThatKey;
		
		comparatorJSONKey = jsonKey;
		projectsThatHaveThatKey.sort(ldtComparator);
				
		return projectsThatHaveThatKey;
	}

	public List<JSONObject> sortedListStepsByLDT(String jsonKey) throws IOException, URISyntaxException, TimeSpanCreatorException, ToolBoxException
	{
		
		LocalDateTime oldestLDT = LocalDateTime.now(clock);

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
		if(tsd.timeSpanIsInTheFuture())return false;
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

	public TimeSpanData getCurrentTimeSpanDataObject(ChronoUnit cu) throws IOException, URISyntaxException, TimeSpanException
	{

		int nr = isInWhichTimeSpan(cu, LocalDateTime.now(clock));

    	List<TimeSpanData> list = null;

    	if(cu.equals(ChronoUnit.YEARS))list = yearList;
    	if(cu.equals(ChronoUnit.MONTHS))list = monthList;
    	if(cu.equals(ChronoUnit.WEEKS))list = weekList;
    	if(cu.equals(ChronoUnit.DAYS))list = dayList;
    	if(cu.equals(ChronoUnit.HOURS))list = hourList;
    	
    	return list.get(nr);
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
   			
   			LocalDateTime adt = extractLDT(pJSON, ADTKey);
   			if(adt.isAfter(ldt))
   			{
   				ldt = adt;
   				jsonKey = ADTKey;
   			}
   	
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
    	if(old==null)return null;

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
    	if(list.isEmpty())return null;
   
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

	public Set<TimeSpanData> timeSpansMostPositive(ChronoUnit cu) throws IOException, URISyntaxException, NaturalNumberException, TimeSpanException, someMath.NaturalNumberException
	{
	
		List<TimeSpanData> list = getTimeSpanList(cu);
		Set<TimeSpanData> tsdSet = new HashSet<>();
	
		Double n = 0.0;
		for(TimeSpanData tsd: list)
		{
	
			PositivityOfATSD pTSD = new PositivityOfATSD(tsd);
			Double m = pTSD.getValue();
			
			if(n<m)
			{
				n=m;
				tsdSet.clear();
				tsdSet.add(tsd);
			}
	
			if(n==m)tsdSet.add(tsd);
		}
	
		return tsdSet;
	}

	public Set<TimeSpanData> timeSpansLeastPositive(ChronoUnit cu) throws IOException, URISyntaxException, NaturalNumberException, TimeSpanException, someMath.NaturalNumberException, CollectionException
	{
	
		List<TimeSpanData> list = getTimeSpanList(cu);
		Set<TimeSpanData> least = new HashSet<>();
		Set<TimeSpanData> most = timeSpansMostPositive(cu);
		TimeSpanData tsd0 = CollectionManipulation.catchRandomElementOfSet(most);
		
		PositivityOfATSD pTSD0 = new PositivityOfATSD(tsd0);
		Double n = pTSD0.getValue();
		
		for(TimeSpanData tsd: list)
		{
	
			PositivityOfATSD pTSD = new PositivityOfATSD(tsd);
			Double m = pTSD.getValue();
			
			if(n>m)
			{
				n=m;
				least.clear();
				least.add(tsd);
			}
	
			if(n==m)least.add(tsd);
		}
	
		return least;
	}

	public Set<TimeSpanData> timeSpansWithMostLDTs(ChronoUnit cu, String jsonKey) throws IOException, URISyntaxException, TimeSpanException, StatisticalToolsException
	{

		List<TimeSpanData> list = getTimeSpanList(cu);
		Set<TimeSpanData> tsdSet = new HashSet<>();

		int n = 0;
		for(TimeSpanData tsd: list)
		{

			int m = tsd.timeSpansLDTs(jsonKey);
			if(n<m)
			{
				n=m;
				tsdSet.clear();
				tsdSet.add(tsd);
			}

			if(n==m)tsdSet.add(tsd);
		}

		return tsdSet;
	}

	public Double someAggregateOfProjects(Function<Collection<JSONObject>, Double> aggregator)
	{
		return aggregator.apply(prjctSet);
	}

	public Double someAggregateOfTSDs(ChronoUnit cu, Function<Collection<TimeSpanData>, Double> aggregator) throws TimeSpanException
	{
		List<TimeSpanData> tsdList = getTimeSpanList(cu);
		return aggregator.apply(tsdList);
	}
	
	public LocalDateTime getBeginAnker()
	{
		return beginAnker;
	}
	
	public LocalDateTime getEndAnker()
	{
		return endAnker;
	}

}