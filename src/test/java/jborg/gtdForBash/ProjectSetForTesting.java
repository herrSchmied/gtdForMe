package jborg.gtdForBash;


import java.io.ByteArrayInputStream;
import java.io.IOException;

import java.net.URISyntaxException;

import java.nio.file.Files;
import java.nio.file.Path;

import java.util.HashSet;
import java.util.Set;


import org.json.JSONException;
import org.json.JSONObject;


import consoleTools.InputStreamSession;

import static consoleTools.TerminalXDisplay.*;


import jborg.gtdForBash.exceptions.StatisticalToolsException;
import jborg.gtdForBash.exceptions.TimeSpanCreatorException;
import jborg.gtdForBash.exceptions.TimeSpanException;
import jborg.gtdForBash.exceptions.ToolBoxException;
import jborg.gtdForBash.exceptions.WeekDataException;
import static jborg.gtdForBash.SequenzesForISS.sequenzManyProjects;


import someMath.NaturalNumberException;



public class ProjectSetForTesting
{

	static Set<JSONObject> prjctSet = new HashSet<>();

	public static Set<JSONObject> get() throws JSONException, IOException, URISyntaxException, NaturalNumberException, WeekDataException, TimeSpanException, ToolBoxException, StatisticalToolsException, TimeSpanCreatorException
	{
		
		
	    // Create a guaranteed-empty temp directory for all project data
        Path tempProjectDir = Files.createTempDirectory("gtdTestProjectData");
        String tempPrjctDirStr = tempProjectDir.toString();
        System.out.println("Temp Dir: " + tempPrjctDirStr);

        // IMPORTANT: Override the CLI project data directory for this test
        GTDCLI.setDataFolder(tempProjectDir);
        
        String tempDirCheck = GTDCLI.getDataFolder().toString();

        assert(tempPrjctDirStr.equals(tempDirCheck));

		String data = sequenzManyProjects();

		ByteArrayInputStream bais = new ByteArrayInputStream(data.getBytes());
		InputStreamSession iss = new InputStreamSession(bais);

        GTDCLI cli = new GTDCLI(iss);
        cli.saveAll();


        Path prjctDataPath = GTDCLI.getDataFolder();
        String prjctDataFolder = prjctDataPath.toString();
        System.out.println("Loading from this Folder: " + prjctDataFolder);
 
        prjctSet = GTDCLI.loadProjects(prjctDataPath);

        assert(!prjctSet.isEmpty());

        return prjctSet;
	}

	public static JSONObject pickProjectByName(String pName)
	{

		for(JSONObject pJSON: prjctSet)
		{
			if(pJSON.getString(ProjectJSONKeyz.nameKey).equals(pName))return pJSON;
		}
		throw new IllegalArgumentException("No Project JSON Object by that Name.");
	}
}