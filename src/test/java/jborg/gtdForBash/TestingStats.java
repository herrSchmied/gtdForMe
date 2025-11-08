package jborg.gtdForBash;



import java.awt.Point;

import java.io.File;
import java.io.IOException;

import java.net.URISyntaxException;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;


import org.json.JSONException;
import org.json.JSONObject;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import javafx.util.Pair;


import someMath.NaturalNumberException;



import static jborg.gtdForBash.SequenzesForISS.*;
import static jborg.gtdForBash.ProjectJSONKeyz.*;
import static jborg.gtdForBash.ProjectJSONToolbox.*;
import jborg.gtdForBash.exceptions.StatisticalToolsException;
import jborg.gtdForBash.exceptions.TimeSpanException;
import jborg.gtdForBash.exceptions.ToolBoxException;
import jborg.gtdForBash.exceptions.WeekDataException;



public class TestingStats
{

    static GTDCLI gtdCli;
    static Set<JSONObject> prjctSet;

    Function<JSONObject, String> mapJSONToName = (pJSON)->pJSON.getString(nameKey);
    
	@BeforeEach
	public void clearFolder() throws JSONException, IOException, URISyntaxException, NaturalNumberException
	{

    	File[] listOfFiles = GTDCLI.getListOfFilesFromDataFolder(GTDCLI.projectDataFolderRelativePath);

    	for(File file: listOfFiles)
    	{
	
    		if(file.isFile())file.delete();
    	}

    	prjctSet = ProjectSetForTesting.get();
    	assert(!prjctSet.isEmpty());
	}

	@Test
	public void oldPrjctTest() throws IOException, URISyntaxException, WeekDataException, TimeSpanException, ToolBoxException, StatisticalToolsException
	{

		assert(!prjctSet.isEmpty());
        StatisticalTools st = new StatisticalTools(prjctSet);
        TimeSpanCreator tsc = st.getTimeSpanCreator();
        
        Pair<String, LocalDateTime> oldPrjct = tsc.oldestLDTOverall();
        String newPrjctName = SequenzesForISS.getNewProjectName(1);

        assert(oldPrjct.getKey().startsWith(newPrjctName.substring(0, 12)));
 
        JSONObject oldPJSON = st.pickByName(newPrjctName);
        assert(st.pickAndCheckByName(ChronoUnit.WEEKS, newPrjctName, 0, oldPJSON, ADTKey));

        System.out.println("Youngest: " + tsc.youngestLDTOverall());
	}

	@Test
	public void areWeeksWherePlacedRightTest() throws WeekDataException, IOException, URISyntaxException, NaturalNumberException, TimeSpanException, ToolBoxException, StatisticalToolsException
	{
		
		assert(!prjctSet.isEmpty());
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
        int lastWeekIndex = weeksSize-1;
        int firstWeekIndex = 0;
        
        JSONObject pJSON = st.pickByName(wakeProjectName);
		assert(st.pickAndCheckByName(ChronoUnit.WEEKS, wakeProjectName, firstWeekIndex, pJSON, ADTKey));
		
		pJSON = st.pickByName(modPrjctName);
		assert(st.pickAndCheckByName(ChronoUnit.WEEKS, modPrjctName, firstWeekIndex, pJSON, NDTKey));
		
		String addNotePrjctName = SequenzesForISS.getNewProjectName(2);
		pJSON = st.pickByName(addNotePrjctName);
		assert(st.pickAndCheckByName(ChronoUnit.WEEKS, addNotePrjctName, firstWeekIndex, pJSON, ADTKey));

		pJSON = st.pickByName(killPrjctNameNoDLDT);
		assert(st.pickAndCheckByName(ChronoUnit.WEEKS, killPrjctNameNoDLDT, firstWeekIndex, pJSON, ADTKey));
		
		String killPrjctName = SequenzesForISS.getNewProjectName(3);
		pJSON = st.pickByName(killPrjctName);
		assert(st.pickAndCheckByName(ChronoUnit.WEEKS, killPrjctName, firstWeekIndex, pJSON, ADTKey));
		assert(projectIsTerminated.test(pJSON));
		
		String killStepPrjctName = SequenzesForISS.getNewProjectName(3);
		pJSON = st.pickByName(killStepPrjctName);
		assert(st.pickAndCheckByName(ChronoUnit.WEEKS, killStepPrjctName, firstWeekIndex, pJSON, ADTKey));

		String appendStpPrjctName = SequenzesForISS.getNewProjectName(4);
		pJSON = st.pickByName(appendStpPrjctName);
		assert(st.pickAndCheckByName(ChronoUnit.WEEKS, appendStpPrjctName, firstWeekIndex, pJSON, ADTKey));
	
		pJSON = st.pickByName(newPrjctNoDLDT);
		assert(st.pickAndCheckByName(ChronoUnit.WEEKS, newPrjctNoDLDT, firstWeekIndex, pJSON, ADTKey));

		for(int n=0;n<weeksSize;n++)
		{
	
			int projectsWritten =  weekDatas.get(n).getProjectsWrittenDown().size();
			int projectsActive = weekDatas.get(n).getActiveProjects().size();
			int projectsSucceeded = weekDatas.get(n).projectsSucceededThisTimeSpan().size();
			int projectsFailed =  weekDatas.get(n).projectsFailedThisTimeSpan().size();
			TimeSpanData tsd = weekDatas.get(n);
			
			System.out.println("\nWeekNr.: " + n);
			Set<String> names = new HashSet<>();
			System.out.println("Projects written: " + projectsWritten);
			names.clear();
			names.addAll(tsd.getProjectsWrittenDown().stream()
					.map(mapJSONToName)
					.collect(Collectors.toSet()));
			System.out.println(names);
			
			System.out.println("Projects active: " + projectsActive);
			names.clear();
			names.addAll(tsd.getActiveProjects().stream()
					.map(mapJSONToName)
					.collect(Collectors.toSet()));
			System.out.println(names);

			System.out.println("Projects succeeded: " + projectsSucceeded);
			names.clear();
			names.addAll(tsd.projectsSucceededThisTimeSpan());
			System.out.println(names);

			System.out.println("Projects failed: " + projectsFailed);
			names.clear();
			names.addAll(tsd.projectsFailedThisTimeSpan());
			System.out.println(names);
			Set<String> pressing = tsd.mostPressingProjectDeadline();
			System.out.println("Most Pressing Deadline: " + pressing);
			
			if(n==0)
			{	
				assert(projectsWritten==8);
				assert(projectsActive==7);
				assert(projectsSucceeded==1); 
				assert(projectsFailed==1);  
			}

			if(n==1)
			{
				assert(projectsWritten==0);
				assert(projectsActive==5);
				assert(projectsSucceeded==0);
				assert(projectsFailed==0);
			}
				  
				/*
				 * if(n==lastWeekIndex) { assert(projectsWritten==4); assert(projectsActive==7);
				 * assert(projectsSucceeded==1); assert(projectsFailed==1); }
				 */	 			  
		}
	}

	@Test
	public void statsTest() throws IOException, URISyntaxException, WeekDataException, StatisticalToolsException, TimeSpanException, ToolBoxException
	{
		
		assert(!prjctSet.isEmpty());
        StatisticalTools st = new StatisticalTools(prjctSet);
        TimeSpanCreator tsc = st.getTimeSpanCreator();
 
        System.out.println("\nNr. of Projects: " + prjctSet.size());

        List<Pair<LocalDateTime, LocalDateTime>> wochen = tsc.createTimeSpanFrames(ChronoUnit.WEEKS);

        int weeksSize = wochen.size();
        //int lastWeekIndex = weeksSize-1;

        System.out.println("Number of weeks: " + weeksSize);

		Point wknrAndN = st.weekWithMostLDTs(NDTKey);
		System.out.println("Week with the most NDTs: " + wknrAndN.x + ".\n" + wknrAndN.y + " Projects written.");
		//assert((wknrAndN.x)==(lastWeekIndex));
		//assert((wknrAndN.y)==(prjctSet.size()));

		wknrAndN = st.weekWithMostLDTs(ADTKey);
		System.out.println("Week with the most ADTs: " + wknrAndN.x + ".\n" + wknrAndN.y + " Projects written.");
		//assert((wknrAndN.x)==(lastWeekIndex));
		//assert((wknrAndN.y)==(prjctSet.size()));

		wknrAndN = st.weekWithMostLDTs(DLDTKey);
		if(wknrAndN!=null)
		{
			System.out.println("Week with the most DLDTs: " + wknrAndN.x + ".\n" + wknrAndN.y + " Project Deadlines.");
			//assert((wknrAndN.x)==(lastWeekIndex));
			//assert((wknrAndN.y)==(6));
		}

		wknrAndN = st.weekWithMostLDTs(TDTKey);
		if(wknrAndN!=null)
		{
			System.out.println("Week with the most TDTs: " + wknrAndN.x + ".\n" + wknrAndN.y + " Project Terminated.");
			//assert((wknrAndN.x)==(lastWeekIndex));
			//assert((wknrAndN.y)==(2));//TODO:Why Six?
		}
	}
}