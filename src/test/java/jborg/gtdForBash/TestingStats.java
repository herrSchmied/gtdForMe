package jborg.gtdForBash;



import java.awt.Point;

import java.io.File;
import java.io.IOException;

import java.net.URISyntaxException;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

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
import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import allgemein.LittleTimeTools;


import javafx.util.Pair;


import someMath.NaturalNumberException;


import static consoleTools.TerminalXDisplay.*;


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
	void setup() throws JSONException, IOException, URISyntaxException, NaturalNumberException, InterruptedException
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
		
		ldt = ldt.plusSeconds(70);
		ldtStr = LittleTimeTools.timeString(ldt);
		sJSON.put(StepJSONKeyz.ADTKey, ldtStr);
		
		JSONArray steps = new JSONArray();
		steps.put(0, sJSON);
		pJSON.put(stepArrayKey, steps);

		prjctSet.add(pJSON);
		
	}

	@Test
	public void oldPrjctTest() throws IOException, URISyntaxException, WeekDataException, TimeSpanException, ToolBoxException, StatisticalToolsException
	{

		assert(!prjctSet.isEmpty());
        StatisticalTools st = new StatisticalTools(prjctSet);
        TimeSpanCreator tsc = st.getTimeSpanCreator();
        
        Pair<String, LocalDateTime> oldPrjct = tsc.oldestLDTOverall();
        String newPrjctName = SequenzesForISS.getNewProjectName(1);
        String oldPrjctName = oldPrjct.getKey();
        
        System.out.println(formatBashStringBoldAndRed("oldPrjct: " + oldPrjctName + ". oldPrjct(0,12): " + oldPrjctName.substring(0,12)));
        System.out.println(formatBashStringBoldAndRed("newPrjct: " + newPrjctName + ". newPrjct(0,12): " + newPrjctName.substring(0,12)));
        System.out.println("Working Folder:" + Paths.get("").toAbsolutePath());

        assert(oldPrjctName.substring(0, 12).equals(newPrjctName.substring(0,12)));
        assert(oldPrjct.getKey().startsWith(newPrjctName.substring(0, 12)));


        JSONObject oldPJSON = st.pickByName(newPrjctName);
        assert(st.pickAndCheckByName(ChronoUnit.WEEKS, newPrjctName, 0, oldPJSON, ADTKey));

        System.out.println("Youngest: " + tsc.youngestLDTOverall());
	}

	@Test
	public void areWeeksWherePlacedRightTest() throws WeekDataException, IOException, URISyntaxException, NaturalNumberException, TimeSpanException, ToolBoxException, StatisticalToolsException, InterruptedException
	{
		makeUpProjectWithLaterNDTAndADT();

		assert(!prjctSet.isEmpty());
		System.out.println(prjctSet);
		Thread.sleep(3000);
		
		
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
        //int lastWeekIndex = weeksSize-1;
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
				assert(projectsWritten==1);
				assert(projectsActive==6);
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

        System.out.println("Number of weeks: " + weeksSize);

		Point wknrAndN = st.weekWithMostLDTs(NDTKey);
		System.out.println("Week with the most NDTs: " + wknrAndN.x + ".\n" + wknrAndN.y + " Projects written.");

		wknrAndN = st.weekWithMostLDTs(ADTKey);
		System.out.println("Week with the most ADTs: " + wknrAndN.x + ".\n" + wknrAndN.y + " Projects written.");

		wknrAndN = st.weekWithMostLDTs(DLDTKey);
		if(wknrAndN!=null)
		{
			System.out.println("Week with the most DLDTs: " + wknrAndN.x + ".\n" + wknrAndN.y + " Project Deadlines.");
		}

		wknrAndN = st.weekWithMostLDTs(TDTKey);
		if(wknrAndN!=null)
		{
			System.out.println("Week with the most TDTs: " + wknrAndN.x + ".\n" + wknrAndN.y + " Project Terminated.");
		}
	}
}