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
	public void testCLIWeekMethods() throws WeekDataException, IOException, URISyntaxException
	{
		
        StatisticalTools st = new StatisticalTools(prjctSet);
        
        Pair<String, LocalDateTime> oldPrjct = st.oldestLDT(ProjectJSONKeyz.BDTKey);
        
        assert(oldPrjct.getKey().equals(newPrjctName));
        
        assert(st.pickAndCheckByName(newPrjctName, 0, gtdCli));
        
        List<Pair<LocalDate, LocalDate>> wochen = st.getWeekSpans();
        
        for(Pair<LocalDate, LocalDate> pair: wochen)
        {
        	assert(pair.getKey().getDayOfWeek().equals(DayOfWeek.MONDAY));
        	assert(pair.getValue().getDayOfWeek().equals(DayOfWeek.SUNDAY));
        }
        
        int weeksSize = wochen.size();
        
		assert(st.pickAndCheckByName(wakeProjectName, weeksSize-1, gtdCli));
		assert(st.pickAndCheckByName(modPrjctName, weeksSize-1, gtdCli));
		assert(st.pickAndCheckByName(addNotePrjctName, weeksSize-1, gtdCli));
		assert(st.pickAndCheckByName(killPrjctNameNoDLDT, weeksSize-1, gtdCli));
		assert(st.pickAndCheckByName(killPrjctName, weeksSize-1, gtdCli));
		assert(st.pickAndCheckByName(killStepPrjctName, weeksSize-1, gtdCli));
		assert(st.pickAndCheckByName(appendStpPrjctName, weeksSize-1, gtdCli));
		assert(st.pickAndCheckByName(newPrjctNoDLDT, weeksSize-1, gtdCli));
		
		Point wknrAndN = st.weekWithMostBDTs();
		System.out.println("Week with the most BDTs: " + wknrAndN.x + ".\n" + wknrAndN.y + " Birthes.");
		assert((wknrAndN.x)==(weeksSize-1));
		assert((wknrAndN.y)==(prjctSet.size()));
	}
}