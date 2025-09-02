package jborg.gtdForBash;



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
import someMath.NaturalNumberException;

import static jborg.gtdForBash.SequenzesForISS.*;


public class TestingStats
{

	@BeforeAll
	public static void clearFolder()
	{

    	File[] listOfFiles = GTDCLI.getListOfFilesFromDataFolder(GTDCLI.projectDataFolderRelativePath);
    	
    	for(File file: listOfFiles)
    	{
    		
    		if(file.isFile())file.delete();
    	}
	}

	@Test
	public void newPrjcts() throws JSONException, IOException, URISyntaxException, NaturalNumberException
	{

		

	}
	
	@Test
	public void testCLIWeekMethods() throws JSONException, IOException, URISyntaxException, NaturalNumberException
	{
		
		String data = sequenzManyProjects();
		
		ByteArrayInputStream bais = new ByteArrayInputStream(data.getBytes());
		InputStreamSession iss = new InputStreamSession(bais);

        GTDCLI gtdCli = new GTDCLI(iss);			
        Set<JSONObject> prjctSet = GTDCLI.loadProjects();
        StatisticalTools st = new StatisticalTools(prjctSet);
        
        Pair<String, LocalDateTime> oldPrjct = st.oldestProject();
        
        assert(oldPrjct.getKey().equals(newPrjctName));
        
        assert(st.pickAndCheckByName(newPrjctName, 0, gtdCli));
        
        List<Pair<LocalDate, LocalDate>> wochen = st.listOfWeeks();
        
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
	}
	
}