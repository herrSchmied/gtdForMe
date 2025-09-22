package jborg.gtdForBash;



import java.awt.Point;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import consoleTools.InputStreamSession;
import javafx.util.Pair;
import jborg.gtdForBash.exceptions.WeekDataException;
import someMath.NaturalNumberException;

import static jborg.gtdForBash.SequenzesForISS.*;


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
    	
		String data = sequenzManyProjects();
		
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
        String bdtKey = ProjectJSONKeyz.BDTKey;
        
        JSONObject pJSON = st.pickByName(wakeProjectName);
		assert(st.pickAndCheckByName(wakeProjectName, weeksSize-1, pJSON, bdtKey));
		
		pJSON = st.pickByName(modPrjctName);
		assert(st.pickAndCheckByName(modPrjctName, weeksSize-1, pJSON, bdtKey));
		
		pJSON = st.pickByName(addNotePrjctName);
		assert(st.pickAndCheckByName(addNotePrjctName, weeksSize-1, pJSON, bdtKey));

		pJSON = st.pickByName(killPrjctNameNoDLDT);
		assert(st.pickAndCheckByName(killPrjctNameNoDLDT, weeksSize-1, pJSON, bdtKey));
		
		pJSON = st.pickByName(killPrjctName);
		assert(st.pickAndCheckByName(killPrjctName, weeksSize-1, pJSON, bdtKey));

		pJSON = st.pickByName(killStepPrjctName);
		assert(st.pickAndCheckByName(killStepPrjctName, weeksSize-1, pJSON, bdtKey));

		pJSON = st.pickByName(appendStpPrjctName);
		assert(st.pickAndCheckByName(appendStpPrjctName, weeksSize-1, pJSON, bdtKey));
	
		pJSON = st.pickByName(newPrjctNoDLDT);
		assert(st.pickAndCheckByName(newPrjctNoDLDT, weeksSize-1, pJSON, bdtKey));
	}
	
	@Test
	public void statsTest() throws IOException, URISyntaxException, WeekDataException
	{

        StatisticalTools st = new StatisticalTools(prjctSet);

        List<Pair<LocalDate, LocalDate>> wochen = st.getWeekSpans();

        int weeksSize = wochen.size();
        System.out.println("Number of weeks: " + weeksSize);

		Point wknrAndN = st.weekWithMostLDTs(ProjectJSONKeyz.BDTKey);
		System.out.println("Week with the most BDTs: " + wknrAndN.x + ".\n" + wknrAndN.y + " Birthes.");
		assert((wknrAndN.x)==(weeksSize-1));
		assert((wknrAndN.y)==(prjctSet.size()-1));

		Map<Integer, Map<String, LocalDateTime>> map = st.weeksLDTs(ProjectJSONKeyz.BDTKey);
		printMap(map);

		wknrAndN = st.weekWithMostLDTs(ProjectJSONKeyz.NDDTKey);
		System.out.println("Week with the most NDDTs: " + wknrAndN.x + ".\n" + wknrAndN.y + " Projects written.");
		assert((wknrAndN.x)==(weeksSize-1));
		assert((wknrAndN.y)==(prjctSet.size()));

		map = st.weeksLDTs(ProjectJSONKeyz.NDDTKey);
		printMap(map);

		wknrAndN = st.weekWithMostLDTs(ProjectJSONKeyz.DLDTKey);
		System.out.println("Week with the most DLDTs: " + wknrAndN.x + ".\n" + wknrAndN.y + " Project Deadlines.");
		assert((wknrAndN.x)==(weeksSize-1));
		assert((wknrAndN.y)==(6));//TODO:Why Six?
		
		map = st.weeksLDTs(ProjectJSONKeyz.DLDTKey);
		printMap(map);
	}
	
	public void printMap(Map<Integer, Map<String, LocalDateTime>> map)
	{
		int s = map.size();
		for(int n=0;n<s;n++)
		{
			int ds = map.get(n).size();
			System.out.println("WeekNr: " + n + ". LDTs: " + ds);
			if(ds>0)
			{
				Map<String, LocalDateTime> innerMap = map.get(n);
				for(String pName: innerMap.keySet())
				{
					LocalDateTime ldt = innerMap.get(pName);
					System.out.println("Project Name: " + pName+ ". LDT: " + ldt);
				}
			}
		}
	}
}