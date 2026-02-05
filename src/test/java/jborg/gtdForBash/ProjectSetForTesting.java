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

		String data = sequenzManyProjects();

		ByteArrayInputStream bais = new ByteArrayInputStream(data.getBytes());
		InputStreamSession iss = new InputStreamSession(bais);

		Path tempProjectDir = Files.createTempDirectory("gtdTestProjectData");
	    // Create a guaranteed-empty temp directory for all project data

        System.out.println(formatBashStringBoldAndGreen("Setting Data Folder to this: " + tempProjectDir.toString()));

        // IMPORTANT: Override the CLI project data directory for this test
        GTDCLI.setDataFolder(tempProjectDir);

        GTDCLI cli = new GTDCLI(iss);
        cli.saveAll();

        prjctSet = GTDCLI.loadProjects(GTDCLI.getDataFolder());

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