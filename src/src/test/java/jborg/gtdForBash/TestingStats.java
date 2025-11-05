package jborg.gtdForBash;



import java.awt.Point;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;

import java.util.Set;
import java.util.stream.Collectors;

import org.json.JSONException;
import org.json.JSONObject;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;





import javafx.util.Pair;


import someMath.NaturalNumberException;

import static jborg.gtdForBash.SequenzesForISS.*;
import static jborg.gtdForBash.ProjectJSONKeyz.*;
import static jborg.gtdForBash.ProjectJSONToolbox.*;
import static jborg.gtdForBash.WeekData.mapJSONToName;

import jborg.gtdForBash.exceptions.StatisticalToolsException;
import jborg.gtdForBash.exceptions.WeekDataException;

public class TestingStats
{

    static GTDCLI gtdCli;
    static Set<JSONObject> prjctSet;
    

	@BeforeAll
	public static void clearFolder() throws JSONException, IOException, URISyntaxException, NaturalNumberException
	{

    	File[] listOfFiles = GTDCLI.getListOfFilesFromDataFolder(GTDCLI.projectDataFolderRelativePath);

    	for(File file: listOfFiles)
    	{
    		
    		if(file.isFile())file.delete();
    	}

    	prjctSet = ProjectSetForTesting.get();
	}

	@Test
	public void oldPrjctTest() throws IOException, URISyntaxException, WeekDataException
	{

        StatisticalTools st = new StatisticalTools(prjctSet);

        Pair<String, LocalDateTime> oldPrjct = st.oldestLDT(ADTKey);
        String newPrjctName = SequenzesForISS.getNewProjectName(1);

        assert(oldPrjct.getKey().equals(newPrjctName));
 
        JSONObject oldPJSON = st.pickByName(newPrjctName);
        assert(st.pickAndCheckByName(newPrjctName, 0, oldPJSON, ADTKey));

        System.out.println("Youngest: " + st.youngestLDT(ADTKey));
	}

	@Test
	public void areWeeksWherePlacedRightTest() throws WeekDataException, IOException, URISyntaxException, NaturalNumberException
	{

        StatisticalTools st = new StatisticalTools(prjctSet);

        List<Pair<LocalDate, LocalDate>> wochen = st.getWeekSpans();
        
        for(Pair<LocalDate, LocalDate> pair: wochen)
        {
        	assert(pair.getKey().getDayOfWeek().equals(DayOfWeek.MONDAY));
        	assert(pair.getValue().getDayOfWeek().equals(DayOfWeek.SUNDAY));
        }
        
        int weeksSize = wochen.size();
        int lastWeekIndex = weeksSize-1;
        int firstWeekIndex = 0;
        
        JSONObject pJSON = st.pickByName(wakeProjectName);
		assert(st.pickAndCheckByName(wakeProjectName, firstWeekIndex, pJSON, ADTKey));
		
		pJSON = st.pickByName(modPrjctName);
		assert(st.pickAndCheckByName(modPrjctName, firstWeekIndex, pJSON, NDTKey));
		
		String addNotePrjctName = SequenzesForISS.getNewProjectName(2);
		pJSON = st.pickByName(addNotePrjctName);
		assert(st.pickAndCheckByName(addNotePrjctName, firstWeekIndex, pJSON, ADTKey));

		pJSON = st.pickByName(killPrjctNameNoDLDT);
		assert(st.pickAndCheckByName(killPrjctNameNoDLDT, firstWeekIndex, pJSON, ADTKey));
		
		String killPrjctName = SequenzesForISS.getNewProjectName(3);
		pJSON = st.pickByName(killPrjctName);
		assert(st.pickAndCheckByName(killPrjctName, firstWeekIndex, pJSON, ADTKey));
		assert(projectIsTerminated.test(pJSON));
		
		String killStepPrjctName = SequenzesForISS.getNewProjectName(3);
		pJSON = st.pickByName(killStepPrjctName);
		assert(st.pickAndCheckByName(killStepPrjctName, firstWeekIndex, pJSON, ADTKey));

		String appendStpPrjctName = SequenzesForISS.getNewProjectName(4);
		pJSON = st.pickByName(appendStpPrjctName);
		assert(st.pickAndCheckByName(appendStpPrjctName, firstWeekIndex, pJSON, ADTKey));
	
		pJSON = st.pickByName(newPrjctNoDLDT);
		assert(st.pickAndCheckByName(newPrjctNoDLDT, firstWeekIndex, pJSON, ADTKey));

		for(int n=0;n<weeksSize;n++)
		{
	
			int projectsWritten = st.getWeekDatas().get(n).getProjectsWrittenDown().size();
			int projectsActive = st.getWeekDatas().get(n).getActiveProjects().size();
			int projectsSucceeded = st.getWeekDatas().get(n).projectsSucceededThisWeek().size();
			int projectsFailed = st.getWeekDatas().get(n).projectsFailedThisWeek().size();
			WeekData wd = st.getWeekDatas().get(n);
			
			System.out.println("\nWeekNr.: " + n);
			Set<String> names = new HashSet<>();
			System.out.println("Projects written: " + projectsWritten);
			names.clear();
			names.addAll(wd.getProjectsWrittenDown().stream()
					.map(mapJSONToName)
					.collect(Collectors.toSet()));
			System.out.println(names);
			
			System.out.println("Projects active: " + projectsActive);
			names.clear();
			names.addAll(wd.getActiveProjects().stream()
					.map(mapJSONToName)
					.collect(Collectors.toSet()));
			System.out.println(names);

			System.out.println("Projects succeeded: " + projectsSucceeded);
			names.clear();
			names.addAll(wd.projectsSucceededThisWeek());
			System.out.println(names);

			System.out.println("Projects failed: " + projectsFailed);
			names.clear();
			names.addAll(wd.projectsFailedThisWeek());
			System.out.println(names);
			Set<String> pressing = wd.mostPressingProjectDeadline();
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
	public void statsTest() throws IOException, URISyntaxException, WeekDataException, StatisticalToolsException
	{

        StatisticalTools st = new StatisticalTools(prjctSet);
        System.out.println("\nNr. of Projects: " + prjctSet.size());

        List<Pair<LocalDate, LocalDate>> wochen = st.getWeekSpans();

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