package jborg.gtdForBash;




import java.io.IOException;

import java.net.URISyntaxException;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import java.util.List;
import java.util.Set;
import java.util.function.Function;


import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import allgemein.LittleTimeTools;


import javafx.util.Pair;


import someMath.NaturalNumberException;

import someMath.exceptions.ConsoleToolsException;



import static jborg.gtdForBash.SequenzesForISS.*;
import static jborg.gtdForBash.ProjectJSONKeyz.*;
import static jborg.gtdForBash.ProjectJSONToolBox.*;
import jborg.gtdForBash.exceptions.StatisticalToolsException;
import jborg.gtdForBash.exceptions.TimeSpanCreatorException;
import jborg.gtdForBash.exceptions.TimeSpanException;
import jborg.gtdForBash.exceptions.ToolBoxException;
import jborg.gtdForBash.exceptions.WeekDataException;



public class TestingStats
{

    static GTDCLI gtdCli;
    static Set<JSONObject> prjctSet;

    Function<JSONObject, String> mapJSONToName = (pJSON)->pJSON.getString(nameKey);

	@BeforeEach
	void setup() throws JSONException, IOException, URISyntaxException, NaturalNumberException, InterruptedException, WeekDataException, TimeSpanException, ToolBoxException, StatisticalToolsException, TimeSpanCreatorException
	{

		prjctSet = ProjectSetForTesting.get();

    	assert(!prjctSet.isEmpty());
	}

	public void makeUpProjectWithLaterNDTAndADT()
	{
		
		JSONObject pJSON = new JSONObject();
		
		pJSON.put(nameKey, "One_Week_Later");
		pJSON.put(statusKey, StatusMGMT.atbd);
		pJSON.put(goalKey, "Testing.");
		
		LocalDateTime ldt = LocalDateTime.now().plusDays(7);
		String ldtStr =  LittleTimeTools.timeString(ldt);
		pJSON.put(NDTKey, ldtStr);
		pJSON.put(ADTKey, ldtStr);


		JSONObject sJSON = new JSONObject();
		sJSON.put(StepJSONKeyz.descKey, "Step by Step .. ooh baby!");
		sJSON.put(StepJSONKeyz.statusKey, StatusMGMT.atbd);
		sJSON.put(StepJSONKeyz.DLDTKey, stepDeadlineNone);
		ldt = ldt.plusSeconds(70);
		ldtStr = LittleTimeTools.timeString(ldt);
		sJSON.put(StepJSONKeyz.ADTKey, ldtStr);
		
		JSONArray steps = new JSONArray();
		steps.put(0, sJSON);
		pJSON.put(stepArrayKey, steps);

		prjctSet.add(pJSON);
		
	}

	@Test
	public void sortingLists() throws IOException, URISyntaxException, WeekDataException, TimeSpanException, ToolBoxException, StatisticalToolsException, TimeSpanCreatorException, InterruptedException
	{

        StatisticalTools st = new StatisticalTools(prjctSet);
        TimeSpanCreator tsc = st.getTimeSpanCreator();
        
        List<JSONObject> list = tsc.sortedListProjectsByLDT(NDTKey);
        
        for(int n=0;n<list.size();n++)
        {

        	JSONObject pJSON = list.get(n);
        	LocalDateTime ndt = extractLDT(pJSON, NDTKey);
        	
        	if(n>0)
        	{
        		JSONObject beforePJSON = list.get(n-1);
                LocalDateTime beforeNDT = extractLDT(beforePJSON, NDTKey);
        		assert(beforeNDT.isAfter(ndt));
        	}
        }

	}
	
	@Test
	public void timeSpanTests() throws IOException, URISyntaxException, WeekDataException, TimeSpanException, ToolBoxException, StatisticalToolsException, TimeSpanCreatorException, InterruptedException
	{

        StatisticalTools st = new StatisticalTools(prjctSet);
        TimeSpanCreator tsc = st.getTimeSpanCreator();

        List<TimeSpanData> tspdListHours = tsc.getTimeSpanList(ChronoUnit.HOURS);
        List<TimeSpanData> tspdListDays = tsc.getTimeSpanList(ChronoUnit.DAYS);
        List<TimeSpanData> tspdListWeeks = tsc.getTimeSpanList(ChronoUnit.WEEKS);
        List<TimeSpanData> tspdListMonth = tsc.getTimeSpanList(ChronoUnit.MONTHS);
        List<TimeSpanData> tspdListYears = tsc.getTimeSpanList(ChronoUnit.YEARS);
 
        assert(tspdListHours.size()>=337&&tspdListHours.size()<=338);
        assert(tspdListDays.size()>=15&&tspdListDays.size()<=16);
        assert(tspdListWeeks.size()>=3&&tspdListWeeks.size()<=4);
        assert(tspdListMonth.size()>=1&&tspdListMonth.size()<=2);
        //printTSPList(tspdListMonth);
        assert(tspdListYears.size()>=1&&tspdListYears.size()<=2);     
        //printTSPList(tspdListYears);
	}

	public void printTSPList(List<TimeSpanData> list) throws InterruptedException
	{
        for(int n=0;n<list.size();n++)
        {
        	TimeSpanData tsd = list.get(n);
        	System.out.println(tsd);
        }
	}
	
	@Test
	public void oldVSYoungTest() throws IOException, URISyntaxException, WeekDataException, TimeSpanException, ToolBoxException, StatisticalToolsException, InterruptedException, ConsoleToolsException, TimeSpanCreatorException
	{

        StatisticalTools st = new StatisticalTools(prjctSet);
        TimeSpanCreator tsc = st.getTimeSpanCreator();

        Pair<String, LocalDateTime> oldPair = tsc.oldestLDTOverall();
        LocalDateTime oldLDT = oldPair.getValue();
        
        Pair<String, LocalDateTime> youngPair = tsc.youngestLDTOverall();
        LocalDateTime youngLDT = youngPair.getValue();

        assert(oldLDT.isBefore(youngLDT));

        for(JSONObject pJSON: prjctSet)
        {

        	Pair<String, LocalDateTime> pair = tsc.youngestLDTInThisProject(pJSON);
        	LocalDateTime pLDT = pair.getValue();

   			assert(pLDT.isAfter(oldLDT)||pLDT.equals(oldLDT));     		
 
   			assert(pLDT.isBefore(youngLDT)||pLDT.equals(youngLDT));     		
        }
	}

	@Test
	public void areWeeksWherePlacedRightTest() throws WeekDataException, IOException, URISyntaxException, NaturalNumberException, TimeSpanException, ToolBoxException, StatisticalToolsException, InterruptedException, TimeSpanCreatorException
	{
		makeUpProjectWithLaterNDTAndADT();

        StatisticalTools st = new StatisticalTools(prjctSet);
        TimeSpanCreator tsc = st.getTimeSpanCreator();
        
        List<Pair<LocalDateTime, LocalDateTime>> wochen = tsc.createTimeSpanFrames(ChronoUnit.WEEKS);
        List<TimeSpanData> weekDatas = tsc.getTimeSpanList(ChronoUnit.WEEKS);
        
        for(Pair<LocalDateTime, LocalDateTime> pair: wochen)
        {
        	assert(pair.getKey().getDayOfWeek().equals(DayOfWeek.MONDAY));
        	assert(pair.getValue().getDayOfWeek().equals(DayOfWeek.SUNDAY));
        }
        
        int weeksSize = wochen.size();
        int firstWeekIndex = 0;
        assert((weeksSize==3)||(weeksSize==4));

        JSONObject pJSON = st.projectJSONObjByName(wakeProjectName);
		assert(pickAndCheckByName(ChronoUnit.WEEKS, wakeProjectName, firstWeekIndex, pJSON, NDTKey, st));
		
		pJSON = st.projectJSONObjByName(modPrjctName);
		assert(pickAndCheckByName(ChronoUnit.WEEKS, modPrjctName, firstWeekIndex, pJSON, NDTKey, st));
		
		String addNotePrjctName = SequenzesForISS.getNewProjectName(2);
		pJSON = st.projectJSONObjByName(addNotePrjctName);
		assert(pickAndCheckByName(ChronoUnit.WEEKS, addNotePrjctName, firstWeekIndex, pJSON, ADTKey, st));

		pJSON = st.projectJSONObjByName(killPrjctNameNoDLDT);
		assert(pickAndCheckByName(ChronoUnit.WEEKS, killPrjctNameNoDLDT, firstWeekIndex, pJSON, ADTKey, st));
		
		String killPrjctName = SequenzesForISS.getNewProjectName(3);
		pJSON = st.projectJSONObjByName(killPrjctName);
		assert(pickAndCheckByName(ChronoUnit.WEEKS, killPrjctName, firstWeekIndex, pJSON, ADTKey, st));
		assert(projectIsTerminated.test(pJSON));
		
		String killStepPrjctName = SequenzesForISS.getNewProjectName(3);
		pJSON = st.projectJSONObjByName(killStepPrjctName);
		assert(pickAndCheckByName(ChronoUnit.WEEKS, killStepPrjctName, firstWeekIndex, pJSON, ADTKey, st));

		String appendStpPrjctName = SequenzesForISS.getNewProjectName(4);
		pJSON = st.projectJSONObjByName(appendStpPrjctName);
		assert(pickAndCheckByName(ChronoUnit.WEEKS, appendStpPrjctName, firstWeekIndex, pJSON, ADTKey, st));
	
		pJSON = st.projectJSONObjByName(newPrjctNoDLDT);
		assert(pickAndCheckByName(ChronoUnit.WEEKS, newPrjctNoDLDT, firstWeekIndex, pJSON, ADTKey, st));

		for(int n=0;n<weeksSize;n++)
		{
	
			int projectsWritten =  weekDatas.get(n).getProjectsWrittenDown().size();
			int projectsActive = weekDatas.get(n).getActiveProjects().size();
			int projectsSucceeded = weekDatas.get(n).projectsSucceededThisTimeSpan().size();
			int projectsFailed =  weekDatas.get(n).projectsFailedThisTimeSpan().size();
			TimeSpanData tsd = weekDatas.get(n);
			
			System.out.println(tsd);
			System.out.println("Projects succeeded: " 
								+ tsd.projectsSucceededThisTimeSpan() + "\n");
			System.out.println("Projects failed: " 
								+ tsd.projectsFailedThisTimeSpan() + "\n");
			System.out.println("Most Pressing Deadline: " 
								+ tsd.mostPressingProjectDeadline() + "\n");
			Thread.sleep(750);

			if(n==0)
			{	
				assert(projectsWritten==8);
				assert(projectsActive==7);
				assert(projectsSucceeded==1); 
				assert(projectsFailed==1);  
			}

			if(n==1)
			{
				assert(projectsWritten==1);
				assert(projectsActive==0);
				assert(projectsSucceeded==0);
				assert(projectsFailed==0);
			}
		}
	}

	@Test
	public void statsTest() throws IOException, URISyntaxException, WeekDataException, StatisticalToolsException, TimeSpanException, ToolBoxException, InterruptedException, TimeSpanCreatorException, someMath.exceptions.NaturalNumberException, NaturalNumberException
	{
		
		assert(!prjctSet.isEmpty());
        StatisticalTools st = new StatisticalTools(prjctSet);
        TimeSpanCreator tsc = st.getTimeSpanCreator();
 
        System.out.println("\nNr. of Projects: " + prjctSet.size());

        List<Pair<LocalDateTime, LocalDateTime>> wochen = tsc.createTimeSpanFrames(ChronoUnit.WEEKS);

        int weeksSize = wochen.size();

        System.out.println("Number of weeks: " + weeksSize);

		Pair<Integer, List<TimeSpanData>> pair = tsc.timeSpansWithMostLDTs(ChronoUnit.WEEKS, NDTKey);
		List<TimeSpanData> tsdList = pair.getValue();
		TimeSpanData tsd = tsdList.get(0);
		int weekNr = tsd.getTimeNr();
		int n = pair.getKey();
		System.out.println("Week with the most NDTs: " + weekNr + ".\n" + n + " Projects written..\n");
		Thread.sleep(750);
		
		pair = tsc.timeSpansWithMostLDTs(ChronoUnit.WEEKS, ADTKey);
		tsdList = pair.getValue();
		tsd = tsdList.get(0);
		weekNr = tsd.getTimeNr();
		n = pair.getKey();
		System.out.println("Week with the most ADTs: " + weekNr + ".\n" + n + " Projects active..\n");
		Thread.sleep(750);

		pair = tsc.timeSpansWithMostLDTs(ChronoUnit.WEEKS, DLDTKey);
		tsdList = pair.getValue();
		tsd = tsdList.get(0);
		weekNr = tsd.getTimeNr();
		n = pair.getKey();
		System.out.println("Week with the most DLDTs: " + weekNr + ".\n" + n + " Project Deadlines..\n");
		Thread.sleep(750);

		pair = tsc.timeSpansWithMostLDTs(ChronoUnit.WEEKS, TDTKey);
		tsdList = pair.getValue();
		tsd = tsdList.get(0);
		weekNr = tsd.getTimeNr();
		n = pair.getKey();
		System.out.println("Week with the most TDTs: " + weekNr + ".\n" + n + " Project Terminated..\n");
		Thread.sleep(750);

        List<Pair<LocalDateTime, LocalDateTime>> hours 
        			= tsc.createTimeSpanFrames(ChronoUnit.HOURS);

        int hoursSize = hours.size();

        System.out.println("Number of Hours: " + hoursSize);

		pair = tsc.timeSpansWithMostLDTs(ChronoUnit.HOURS, NDTKey);
		tsdList = pair.getValue();
		tsd = tsdList.get(0);
		int hourNr = tsd.getTimeNr();
		n = pair.getKey();
		System.out.println("Hour with the most NDTs: " + hourNr+ ".\n" + n + " Projects written..\n");
		Thread.sleep(750);

		pair = tsc.timeSpansWithMostLDTs(ChronoUnit.HOURS, ADTKey);
		tsdList = pair.getValue();
		tsd = tsdList.get(0);
		hourNr = tsd.getTimeNr();
		n = pair.getKey();
		System.out.println("Hour with the most ADTs: " + hourNr + ".\n" + n + " Projects active..\n");
		Thread.sleep(750);

		pair = tsc.timeSpansWithMostLDTs(ChronoUnit.HOURS, DLDTKey);
		tsdList = pair.getValue();
		tsd = tsdList.get(0);
		hourNr = tsd.getTimeNr();
		n = pair.getKey();
		System.out.println("Hour with the most DLDTs: " + hourNr + ".\n" + n + " Projects active..\n");
		Thread.sleep(750);

		pair = tsc.timeSpansWithMostLDTs(ChronoUnit.HOURS, TDTKey);
		tsdList = pair.getValue();
		tsd = tsdList.get(0);
		hourNr = tsd.getTimeNr();
		n = pair.getKey();
		System.out.println("Hour with the most TDTs: " + hourNr + ".\n" + n + " Projects active..\n");
		Thread.sleep(750);

		tsdList = tsc.timeSpansMostPositive(ChronoUnit.WEEKS);
		List<Integer> listWeekNr = tsdList.stream()
				.map(t-> t.getTimeNr())
				.toList();
		System.out.println("Weeks Most Positive: " + listWeekNr);
		Thread.sleep(2000);
		assert(listWeekNr.contains(0));
	}
	
	public boolean pickAndCheckByName(ChronoUnit cu, String name, int unitNr, JSONObject pJSON, String jsonKey, StatisticalTools st) throws IOException, URISyntaxException, TimeSpanException
	{

		LocalDateTime ldt = extractLDT(pJSON, jsonKey);
		TimeSpanData tsd = st.tsc.getTimeSpanList(cu).get(unitNr);

		return tsd.isInThisTimeSpan(ldt);
	}
}