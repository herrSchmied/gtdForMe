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

import allgemein.LittleTimeTools;
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

		LocalDateTime customBDT = LocalDateTime.now().minusYears(1);
		
		String data = sequenzNewProjectCustomBDT(newPrjctName, customBDT);

		data = data + sequenzMODProject(wakeProjectName);
		
		data = data + sequenzMODProject(modPrjctName);
		
		data = data + sequenzNewProject(addNotePrjctName);

		data = data + sequenzNewProjectNoDLDT(killPrjctNameNoDLDT);
		
		data = data + sequenzNewProject(killPrjctName);
		
		data = data + sequenzNewProject(killStepPrjctName);
		
		data = data + sequenzNewProject(appendStpPrjctName);

		data = data + sequenzNewProjectNoDLDT(newPrjctNoDLDT);
		
		data = data + SomeCommands.exit + '\n';
		
		ByteArrayInputStream bais = new ByteArrayInputStream(data.getBytes());
		InputStreamSession iss = new InputStreamSession(bais);

        GTDCLI gtdCli = new GTDCLI(iss);			

        Pair<String, LocalDateTime> oldPrjct = gtdCli.oldestProject();
        
        assert(oldPrjct.getKey().equals(newPrjctName));
        
        assert(pickAndCheckByName(newPrjctName, 0, gtdCli));
        
        List<Pair<LocalDate, LocalDate>> wochen = gtdCli.listOfWeeks();
        
        for(Pair<LocalDate, LocalDate> pair: wochen)
        {
        	assert(pair.getKey().getDayOfWeek().equals(DayOfWeek.MONDAY));
        	assert(pair.getValue().getDayOfWeek().equals(DayOfWeek.SUNDAY));
        }
        
        System.out.println("Weeks: " + wochen.size());
        
        LocalDateTime bdt = extractLDT(wakeProjectName, ProjectJSONKeyz.BDTKey);
        int nr = gtdCli.isInWhichWeek(bdt);
        System.out.println(wakeProjectName + " is in the Week: " + nr);
        
		assert(pickAndCheckByName(wakeProjectName, 53, gtdCli));
		assert(pickAndCheckByName(modPrjctName, 53, gtdCli));
		assert(pickAndCheckByName(addNotePrjctName, 53, gtdCli));
		assert(pickAndCheckByName(killPrjctNameNoDLDT, 53, gtdCli));
		assert(pickAndCheckByName(killPrjctName, 53, gtdCli));
		assert(pickAndCheckByName(killStepPrjctName, 53, gtdCli));
		assert(pickAndCheckByName(appendStpPrjctName, 53, gtdCli));
		assert(pickAndCheckByName(newPrjctNoDLDT, 53, gtdCli));
	}
	
	private boolean pickAndCheckByName(String name, int weekNr, GTDCLI gtdCli) throws IOException, URISyntaxException
	{

        LocalDateTime bdt = extractLDT(name, ProjectJSONKeyz.BDTKey);

		return gtdCli.isInThatWeek(weekNr, bdt);
	}
	
	private LocalDateTime extractLDT(String name, String key) throws IOException, URISyntaxException
	{
		
        Set<JSONObject> jsonSet = GTDCLI.loadProjects();

	    JSONObject pJSON = TestingCLI.pickProjectByName(name, jsonSet);

		return  LittleTimeTools.LDTfromTimeString(pJSON.getString(key));
	}
}