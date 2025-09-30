package jborg.gtdForBash;



import java.awt.Point;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.json.JSONException;
import org.json.JSONObject;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;


import consoleTools.InputStreamSession;


import javafx.util.Pair;


import someMath.NaturalNumberException;

import static jborg.gtdForBash.SequenzesForISS.*;
import static jborg.gtdForBash.ProjectJSONKeyz.*;
import static jborg.gtdForBash.ProjectJSONToolbox.*;
import static jborg.gtdForBash.WeekData.mapJSONToName;
import static jborg.gtdForBash.StatisticalTools.*;
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
    	
		String data = sequenzManyProjects(LocalDateTime.now().minusDays(14));
		
		ByteArrayInputStream bais = new ByteArrayInputStream(data.getBytes());
		InputStreamSession iss = new InputStreamSession(bais);

        gtdCli = new GTDCLI(iss);			
        prjctSet = GTDCLI.loadProjects();
	}
	
	@Test
	public void oldPrjctTest() throws IOException, URISyntaxException, WeekDataException
	{

        StatisticalTools st = new StatisticalTools(prjctSet);
        
        Pair<String, LocalDateTime> oldPrjct = st.oldestLDT(ProjectJSONKeyz.BDTKey);
        Pair<String, LocalDateTime> oldPrjct2 = st.oldestLDT(ProjectJSONKeyz.NDDTKey);

        assert(oldPrjct.getKey().equals(newPrjctName));
        assert(oldPrjct2.getKey().equals(newPrjctName));
        
        JSONObject oldPJSON = st.pickByName(newPrjctName);
        assert(st.pickAndCheckByName(newPrjctName, 0, oldPJSON, ProjectJSONKeyz.BDTKey));
        
        System.out.println("Youngest: " + st.youngestLDT(ProjectJSONKeyz.BDTKey));

	}

	@Test
	public void areWeeksWherePlacedRightTest() throws WeekDataException, IOException, URISyntaxException
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
     
        
        JSONObject pJSON = st.pickByName(wakeProjectName);
		assert(st.pickAndCheckByName(wakeProjectName, lastWeekIndex, pJSON, BDTKey));
		
		pJSON = st.pickByName(modPrjctName);
		assert(st.pickAndCheckByName(modPrjctName, lastWeekIndex, pJSON, BDTKey));
		
		pJSON = st.pickByName(addNotePrjctName);
		assert(st.pickAndCheckByName(addNotePrjctName, lastWeekIndex, pJSON, BDTKey));

		pJSON = st.pickByName(killPrjctNameNoDLDT);
		assert(st.pickAndCheckByName(killPrjctNameNoDLDT, lastWeekIndex, pJSON, BDTKey));
		
		pJSON = st.pickByName(killPrjctName);
		assert(st.pickAndCheckByName(killPrjctName, lastWeekIndex, pJSON, BDTKey));
		assert(projectIsTerminated.test(pJSON));
		
		pJSON = st.pickByName(killStepPrjctName);
		assert(st.pickAndCheckByName(killStepPrjctName, lastWeekIndex, pJSON, BDTKey));

		pJSON = st.pickByName(appendStpPrjctName);
		assert(st.pickAndCheckByName(appendStpPrjctName, lastWeekIndex, pJSON, BDTKey));
	
		pJSON = st.pickByName(newPrjctNoDLDT);
		assert(st.pickAndCheckByName(newPrjctNoDLDT, lastWeekIndex, pJSON, BDTKey));

		for(int n=0;n<weeksSize;n++)
		{
	
			int projectsBorn = st.getWeekDatas().get(n).getProjectsBorn().size();
			int projectsWritten = st.getWeekDatas().get(n).getProjectsWrittenDown().size();
			int projectsActive = st.getWeekDatas().get(n).getActiveProjects().size();
			int projectsSucceeded = st.getWeekDatas().get(n).projectsSucceededThisWeek().size();
			int projectsFailed = st.getWeekDatas().get(n).projectsFailedThisWeek().size();
			
			System.out.println("\nWeekNr.: " + n);
			System.out.println("Projects born: " + projectsBorn);
			Set<String> names = new HashSet<>();
		    names.addAll(st.getWeekDatas().get(n).getProjectsBorn().stream()
		    		.map(mapJSONToName)
		    		.collect(Collectors.toSet()));
			System.out.println(names);

			System.out.println("Projects written: " + projectsWritten);
			names.clear();
			names.addAll(st.getWeekDatas().get(n).getProjectsWrittenDown().stream()
					.map(mapJSONToName)
					.collect(Collectors.toSet()));
			System.out.println(names);
			
			System.out.println("Projects active: " + projectsActive);
			names.clear();
			names.addAll(st.getWeekDatas().get(n).getActiveProjects().stream()
					.map(mapJSONToName)
					.collect(Collectors.toSet()));
			System.out.println(names);

			System.out.println("Projects succeeded: " + projectsSucceeded);
			names.clear();
			names.addAll(st.getWeekDatas().get(n).projectsSucceededThisWeek());
			System.out.println(names);

			System.out.println("Projects failed: " + projectsFailed);
			names.clear();
			names.addAll(st.getWeekDatas().get(n).projectsFailedThisWeek());
			System.out.println(names);

			if(n==lastWeekIndex)
			{
				assert(projectsBorn==8);
				assert(projectsWritten==9);
				assert(projectsActive==7);
				assert(projectsSucceeded==1);
				assert(projectsFailed==1); }
			  
			  if(n==1)
			  {
				  assert(projectsBorn==0);
				  assert(projectsWritten==0);
				  assert(projectsActive==1);
				  assert(projectsSucceeded==0);
				  assert(projectsFailed==0);		  
			  }
			  
			  if(n==0)
			  {
				  assert(projectsBorn==1);
				  assert(projectsWritten==0);
				  assert(projectsActive==1);
				  assert(projectsSucceeded==0); 
				  assert(projectsFailed==0);
			  }
		}
	}

	@Test
	public void statsTest() throws IOException, URISyntaxException, WeekDataException
	{

        StatisticalTools st = new StatisticalTools(prjctSet);
        System.out.println("\nNr. of Projects: " + prjctSet.size());

        List<Pair<LocalDate, LocalDate>> wochen = st.getWeekSpans();

        int weeksSize = wochen.size();
        int lastWeekIndex = weeksSize-1;

        System.out.println("Number of weeks: " + weeksSize);

		Point wknrAndN = st.weekWithMostLDTs(BDTKey);
		System.out.println("Week with the most BDTs: " + wknrAndN.x + ".\n" + wknrAndN.y + " Birthes.");
		assert((wknrAndN.x)==(lastWeekIndex));
		assert((wknrAndN.y)==(prjctSet.size()-1));

		wknrAndN = st.weekWithMostLDTs(NDDTKey);
		System.out.println("Week with the most NDDTs: " + wknrAndN.x + ".\n" + wknrAndN.y + " Projects written.");
		assert((wknrAndN.x)==(lastWeekIndex));
		assert((wknrAndN.y)==(prjctSet.size()));

		wknrAndN = st.weekWithMostLDTs(DLDTKey);
		System.out.println("Week with the most DLDTs: " + wknrAndN.x + ".\n" + wknrAndN.y + " Project Deadlines.");
		assert((wknrAndN.x)==(lastWeekIndex));
		assert((wknrAndN.y)==(6));

		wknrAndN = st.weekWithMostLDTs(TDTKey);
		System.out.println("Week with the most TDTs: " + wknrAndN.x + ".\n" + wknrAndN.y + " Project Terminated.");
		assert((wknrAndN.x)==(lastWeekIndex));
		assert((wknrAndN.y)==(2));//TODO:Why Six?
	}
}