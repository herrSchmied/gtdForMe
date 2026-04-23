package jborg.gtdForBash;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.json.JSONException;
import org.junit.jupiter.api.Test;

import consoleTools.InputStreamSession;
import jborg.gtdForBash.exceptions.StatisticalToolsException;
import jborg.gtdForBash.exceptions.TimeSpanCreatorException;
import jborg.gtdForBash.exceptions.TimeSpanException;
import jborg.gtdForBash.exceptions.ToolBoxException;
import jborg.gtdForBash.exceptions.WeekDataException;
import someMath.NaturalNumberException;

public class TestingPickUp
{

	@Test
	public void pickUpTest() throws JSONException, ClassNotFoundException, IOException, URISyntaxException, NaturalNumberException, WeekDataException, TimeSpanException, ToolBoxException, StatisticalToolsException, TimeSpanCreatorException, InterruptedException
	{

		newProject();
		doNothing();
	}

	public static void newProject() throws IOException, JSONException, ClassNotFoundException, URISyntaxException, NaturalNumberException, WeekDataException, TimeSpanException, ToolBoxException, StatisticalToolsException, TimeSpanCreatorException, InterruptedException
	{
	    // Create a guaranteed-empty temp directory for all project data
        Path tempProjectDir = Files.createTempDirectory("gtdTestProjectData");
        String tempPrjctDirStr = tempProjectDir.toString();
        System.out.println("Temp Dir: " + tempPrjctDirStr);

        // IMPORTANT: Override the CLI project data directory for this test
        GTDCLI.setDataFolder(tempProjectDir);
        
        String tempDirCheck = GTDCLI.getDataFolder().toString();

        assert(tempPrjctDirStr.equals(tempDirCheck));

        Path prjctDataPath = GTDCLI.getDataFolder();
        String prjctDataFolder = prjctDataPath.toString();
        System.out.println("Loading from this Folder: " + prjctDataFolder);
 

		SequenzesForISS sfiss = new SequenzesForISS(ProjectSetForTesting.getClock());
		
		String data = sfiss.sequenzNewProject("NewProject")+SomeCommands.exit + '\n';
		

		ByteArrayInputStream bais = new ByteArrayInputStream(data.getBytes());
		InputStreamSession iss = new InputStreamSession(bais);

        new GTDCLI(iss, ProjectSetForTesting.getClock());
	}
	
	public static void doNothing() throws IOException, JSONException, ClassNotFoundException, URISyntaxException, NaturalNumberException, WeekDataException, TimeSpanException, ToolBoxException, StatisticalToolsException, TimeSpanCreatorException, InterruptedException
	{
	    // Create a guaranteed-empty temp directory for all project data
        Path tempProjectDir = Files.createTempDirectory("gtdTestProjectData");
        String tempPrjctDirStr = tempProjectDir.toString();
        System.out.println("Temp Dir: " + tempPrjctDirStr);

        // IMPORTANT: Override the CLI project data directory for this test
        GTDCLI.setDataFolder(tempProjectDir);
        
        String tempDirCheck = GTDCLI.getDataFolder().toString();

        assert(tempPrjctDirStr.equals(tempDirCheck));

        Path prjctDataPath = GTDCLI.getDataFolder();
        String prjctDataFolder = prjctDataPath.toString();
        System.out.println("Loading from this Folder: " + prjctDataFolder);
		
		String data = SomeCommands.exit + '\n';

		ByteArrayInputStream bais = new ByteArrayInputStream(data.getBytes());
		InputStreamSession iss = new InputStreamSession(bais);

        new GTDCLI(iss, ProjectSetForTesting.getClock());
	}
}
