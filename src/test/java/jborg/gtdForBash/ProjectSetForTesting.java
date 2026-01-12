package jborg.gtdForBash;


import java.io.ByteArrayInputStream;
import java.io.IOException;

import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;

import java.util.HashSet;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import allgemein.LittleTimeTools;


import consoleTools.BashSigns;
import consoleTools.InputStreamSession;
import static consoleTools.TerminalXDisplay.*;


import someMath.NaturalNumberException;


import static jborg.gtdForBash.ProjectJSONToolbox.extractLDT;
import static jborg.gtdForBash.ProjectJSONKeyz.*;
import static jborg.gtdForBash.SequenzesForISS.sequenzManyProjects;


public class ProjectSetForTesting
{

	static Set<JSONObject> prjctSet = new HashSet<>();

	public static Set<JSONObject> get() throws JSONException, IOException, URISyntaxException, NaturalNumberException
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
	
	public static void alterFirstProjectsDLDT() throws IOException, URISyntaxException
	{

		String pName = SequenzesForISS.getNewProjectName(1);
		JSONObject pJSON = pickProjectByName(pName);
		System.out.println(BashSigns.boldRBCPX + pName + BashSigns.boldRBCSX); 

		LocalDateTime ndt = extractLDT(pJSON, ADTKey);
		LocalDateTime newDLDT = ndt.plusHours(1);
		String newDLDTStr = LittleTimeTools.timeString(newDLDT);
		pJSON.put(ProjectJSONKeyz.DLDTKey, newDLDTStr);
	  }
	 
	public static void alterNthStepNDDT(JSONObject pJSON, LocalDateTime newNDDT, int n)
	{
		JSONArray stpArr = pJSON.getJSONArray(stepArrayKey);
		JSONObject step = stpArr.getJSONObject(n);
	
		String nddtStr = LittleTimeTools.timeString(newNDDT);
		step.put(StepJSONKeyz.ADTKey, nddtStr);

	}

	public static void alterProjectADT(JSONObject pJSON, LocalDateTime newADT)
	{
		String newADTStr = LittleTimeTools.timeString(newADT);
		pJSON.put(ADTKey, newADTStr);
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