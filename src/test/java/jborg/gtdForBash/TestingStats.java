package jborg.gtdForBash;




import java.io.IOException;

import java.net.URISyntaxException;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


import org.json.JSONException;
import org.json.JSONObject;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import CollectionTools.CollectionManipulation;


import javafx.util.Pair;


import someMath.NaturalNumberException;
import someMath.exceptions.CollectionException;
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
	void setup() throws JSONException, IOException, URISyntaxException, NaturalNumberException, InterruptedException, WeekDataException, TimeSpanException, ToolBoxException, StatisticalToolsException, TimeSpanCreatorException, ClassNotFoundException
	{

		prjctSet = ProjectSetForTesting.get();

    	assert(!prjctSet.isEmpty());
	}


	@Test
	public void sortingLists() throws IOException, URISyntaxException, WeekDataException, TimeSpanException, ToolBoxException, StatisticalToolsException, TimeSpanCreatorException, InterruptedException, NaturalNumberException
	{

		List<List<TimeSpanData>> empty = new ArrayList<>();
		for(int n=0;n<5;n++)empty.add(new ArrayList<>());
        StatisticalTools st = new StatisticalTools(prjctSet, empty);
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
	public void oldVSYoungTest() throws IOException, URISyntaxException, WeekDataException, TimeSpanException, ToolBoxException, StatisticalToolsException, InterruptedException, ConsoleToolsException, TimeSpanCreatorException, NaturalNumberException
	{
		
		List<List<TimeSpanData>> empty = new ArrayList<>();
		for(int n=0;n<5;n++)empty.add(new ArrayList<>());
        StatisticalTools st = new StatisticalTools(prjctSet, empty);
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
	public void statsTest() throws IOException, URISyntaxException, WeekDataException, StatisticalToolsException, TimeSpanException, ToolBoxException, InterruptedException, TimeSpanCreatorException, someMath.exceptions.NaturalNumberException, NaturalNumberException, CollectionException
	{
		
		assert(!prjctSet.isEmpty());
		
		
		List<List<TimeSpanData>> empty = new ArrayList<>();
		for(int n=0;n<5;n++)empty.add(new ArrayList<>());
        StatisticalTools st = new StatisticalTools(prjctSet, empty);
        TimeSpanCreator tsc = st.getTimeSpanCreator();
 
        System.out.println("\nNr. of Projects: " + prjctSet.size());
        
        LocalDateTime start = tsc.getBeginAnker();
        LocalDateTime stop = tsc.getEndAnker();
        List<Pair<LocalDateTime, LocalDateTime>> wochen = tsc.createTimeSpanFrames(ChronoUnit.WEEKS, start, stop);

        int weeksSize = wochen.size();

        System.out.println("Number of weeks: " + weeksSize);

		Set<TimeSpanData> set = tsc.timeSpansWithMostLDTs(ChronoUnit.WEEKS, NDTKey);
		TimeSpanData tsd = CollectionManipulation.catchRandomElementOfSet(set);
		int weekNr = tsd.getTimeNr();
		int n = tsd.allTheNames().size();
		System.out.println("Week with the most NDTs: " + weekNr + ".\n" + n + " Projects written..\n");
		//Thread.sleep(3750);
		
		set = tsc.timeSpansWithMostLDTs(ChronoUnit.WEEKS, ADTKey);
		tsd = CollectionManipulation.catchRandomElementOfSet(set);
		weekNr = tsd.getTimeNr();
		n = tsd.allTheNames().size();
		System.out.println("Week with the most ADTs: " + weekNr + ".\n" + n + " Projects active..\n");
		//Thread.sleep(750);

		set = tsc.timeSpansWithMostLDTs(ChronoUnit.WEEKS, DLDTKey);
		tsd = CollectionManipulation.catchRandomElementOfSet(set);
		weekNr = tsd.getTimeNr();
		n = tsd.allTheNames().size();
		System.out.println("Week with the most DLDTs: " + weekNr + ".\n" + n + " Project Deadlines..\n");
		//Thread.sleep(750);

		set = tsc.timeSpansWithMostLDTs(ChronoUnit.WEEKS, TDTKey);
		tsd = CollectionManipulation.catchRandomElementOfSet(set);
		weekNr = tsd.getTimeNr();
		n = tsd.allTheNames().size();
		System.out.println("Week with the most TDTs: " + weekNr + ".\n" + n + " Project Terminated..\n");
		//Thread.sleep(750);

        List<Pair<LocalDateTime, LocalDateTime>> hours 
        			= tsc.createTimeSpanFrames(ChronoUnit.HOURS, start, stop);

        int hoursSize = hours.size();

        System.out.println("Number of Hours: " + hoursSize);

		set = tsc.timeSpansWithMostLDTs(ChronoUnit.HOURS, NDTKey);
		tsd = CollectionManipulation.catchRandomElementOfSet(set);
		int hourNr = tsd.getTimeNr();
		n = tsd.allTheNames().size();
		System.out.println("Hour with the most NDTs: " + hourNr+ ".\n" + n + " Projects written..\n");
		//Thread.sleep(750);

		set = tsc.timeSpansWithMostLDTs(ChronoUnit.HOURS, ADTKey);
		tsd = CollectionManipulation.catchRandomElementOfSet(set);
		hourNr = tsd.getTimeNr();
		n =  tsd.allTheNames().size();
		System.out.println("Hour with the most ADTs: " + hourNr + ".\n" + n + " Projects active..\n");
		//Thread.sleep(750);

		set = tsc.timeSpansWithMostLDTs(ChronoUnit.HOURS, DLDTKey);
		tsd = CollectionManipulation.catchRandomElementOfSet(set);
		hourNr = tsd.getTimeNr();
		n = tsd.allTheNames().size();
		System.out.println("Hour with the most DLDTs: " + hourNr + ".\n" + n + " Projects active..\n");
		//Thread.sleep(750);

		set = tsc.timeSpansWithMostLDTs(ChronoUnit.HOURS, TDTKey);
		tsd = CollectionManipulation.catchRandomElementOfSet(set);
		hourNr = tsd.getTimeNr();
		n =  tsd.allTheNames().size();
		System.out.println("Hour with the most TDTs: " + hourNr + ".\n" + n + " Projects active..\n");
		//Thread.sleep(750);

		Set<TimeSpanData>tsdList = tsc.selectSubSetOfTSDListByExtremValue(ChronoUnit.WEEKS, 0.0, PositivityOfATSD.posiValue);
		Set<TimeSpanData> tsdSet = new HashSet<>(tsdList);
		tsd = CollectionManipulation.catchRandomElementOfSet(tsdSet);
		System.out.println("Week Most Positive: \n" + (new PositivityOfATSD(tsd).toString()));
		//Thread.sleep(4000);
		assert(tsd.getTimeNr()==0);
		assert(tsdSet.size()==1);
	}
}