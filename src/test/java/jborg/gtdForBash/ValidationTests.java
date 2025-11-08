package jborg.gtdForBash;


import static jborg.gtdForBash.ProjectJSONToolbox.*;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

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

    	File[] listOfFiles = GTDCLI.getListOfFilesFromDataFolder(GTDCLI.projectDataFolderRelativePath);
    	
    	for(File file: listOfFiles)
    	{
    		
    		if(file.isFile())file.delete();
    	}
  	
		projects = ProjectSetForTesting.get();
	}

	@Test
	public void test()
	{
		JSONObject pJSON = pickProjectByName(SequenzesForISS.getNewProjectName(1), projects);
		ProjectJSONValidator pjv = new ProjectJSONValidator();
		
		assert(pjv.validate(pJSON.toString()));
	}
}
