package jborg.gtdForBash;


import static consoleTools.TerminalXDisplay.formatBashStringBoldAndGreen;
import static jborg.gtdForBash.ProjectJSONToolbox.*;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import someMath.NaturalNumberException;

public class ValidationTests
{

	static GTDCLI gtdCli;
	static Set<JSONObject> projects;
	
	@BeforeEach
	public void clearFolder() throws JSONException, IOException, URISyntaxException, NaturalNumberException
	{

	    // Create a guaranteed-empty temp directory for all project data
        Path tempProjectDir = Files.createTempDirectory("gtdTestProjectData");

        // IMPORTANT: Override the CLI project data directory for this test
        GTDCLI.setDataFolder(tempProjectDir);
        System.out.println(formatBashStringBoldAndGreen(GTDCLI.getDataFolder().toString()));

    	File[] listOfFiles = GTDCLI.getListOfFilesFromDataFolder(GTDCLI.getDataFolder());
    	
    	for(File file: listOfFiles)
    	{
    		
    		if(file.isFile())file.delete();
    	}
  	
		projects = ProjectSetForTesting.get();
		
		assert(!projects.isEmpty());
	}

	@Test
	public void test()
	{
		JSONObject pJSON = pickProjectByName(SequenzesForISS.getNewProjectName(1), projects);
		ProjectJSONValidator pjv = new ProjectJSONValidator();
		
		assert(pjv.validate(pJSON.toString()));
	}
}
