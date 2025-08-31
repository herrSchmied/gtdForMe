package jborg.gtdForBash;



import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.json.JSONException;

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
        
        List<Pair<LocalDate, LocalDate>> wochen = gtdCli.listOfWeeks();
        
        for(Pair<LocalDate, LocalDate> pair: wochen)
        {
        	assert(pair.getKey().getDayOfWeek().equals(DayOfWeek.MONDAY));
        	assert(pair.getValue().getDayOfWeek().equals(DayOfWeek.SUNDAY));
        }
        
        System.out.println("Weeks: " + wochen.size());
	}
}