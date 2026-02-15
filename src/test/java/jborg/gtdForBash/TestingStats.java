package jborg.gtdForBash;




import java.io.IOException;

import java.net.URISyntaxException;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


import org.json.JSONException;
import org.json.JSONObject;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import javafx.util.Pair;


import someMath.NaturalNumberException;

import someMath.exceptions.ConsoleToolsException;



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

	@BeforeEach
	void setup() throws JSONException, IOException, URISyntaxException, NaturalNumberException, InterruptedException, WeekDataException, TimeSpanException, ToolBoxException, StatisticalToolsException, TimeSpanCreatorException
	{

		prjctSet = ProjectSetForTesting.get();

    	assert(!prjctSet.isEmpty());
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
		Set<TimeSpanData> tsdSet = new HashSet<>(tsdList);
		tsd = tsdList.get(0);
		System.out.println("Week Most Positive: \n" + (new PositivityOfATSD(tsd).toString()));
		Thread.sleep(4000);
		assert(tsd.getTimeNr()==0);
		assert(tsdSet.size()==1);
	}
}