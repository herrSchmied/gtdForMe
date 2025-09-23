package jborg.gtdForBash;

import static jborg.gtdForBash.SequenzesForISS.sequenzManyProjects;
import static jborg.gtdForBash.ProjectJSONToolbox.*;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.util.Set;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import consoleTools.InputStreamSession;
import someMath.NaturalNumberException;

public class ValidationTests
{

	static GTDCLI gtdCli;
	static Set<JSONObject> projects;
	
	private static void doCLI() throws JSONException, IOException, URISyntaxException, NaturalNumberException
	{
		
		ByteArrayInputStream bais = new ByteArrayInputStream(sequenzManyProjects(LocalDateTime.now()).getBytes());
		InputStreamSession iss = new InputStreamSession(bais);

        gtdCli = new GTDCLI(iss);				
	}

	@BeforeAll
	public static void clearFolder() throws JSONException, IOException, URISyntaxException, NaturalNumberException
	{

    	File[] listOfFiles = GTDCLI.getListOfFilesFromDataFolder(GTDCLI.projectDataFolderRelativePath);
    	
    	for(File file: listOfFiles)
    	{
    		
    		if(file.isFile())file.delete();
    	}
    	
    	doCLI();
    	
		projects = GTDCLI.loadProjects();
	}

	@Test
	public void test()
	{
		JSONObject pJSON = pickProjectByName(SequenzesForISS.newPrjctName, projects);
		ProjectJSONValidator pjv = new ProjectJSONValidator();
		
		assert(pjv.validate(pJSON.toString()));
	}
}
