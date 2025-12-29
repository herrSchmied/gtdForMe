package jborg.gtdForBash;

import static consoleTools.TerminalXDisplay.*;
import static jborg.gtdForBash.ProjectJSONKeyz.ADTKey;
import static jborg.gtdForBash.ProjectJSONKeyz.NDTKey;
import static jborg.gtdForBash.ProjectJSONKeyz.goalKey;
import static jborg.gtdForBash.ProjectJSONKeyz.nameKey;
import static jborg.gtdForBash.ProjectJSONKeyz.statusKey;
import static jborg.gtdForBash.ProjectJSONKeyz.stepArrayKey;
import static jborg.gtdForBash.SequenzesForISS.sequenzNewProject;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import allgemein.LittleTimeTools;
import consoleTools.InputStreamSession;
import someMath.NaturalNumberException;
import static fileShortCuts.TextAndObjSaveAndLoad.*;


public class SimpleTerminalTest
{

	Set<JSONObject> prjctSet = new HashSet<>();

	@Test
	public void Test() throws IOException, URISyntaxException, JSONException, NaturalNumberException
	{


		Path base = Paths.get(System.getProperty("java.io.tmpdir"));
		Path tempProjectDir = base.resolve("gtdTestProjectData");
		Path p = Files.createDirectories(tempProjectDir);
	    // Create a guaranteed-empty temp directory for all project data
      
        System.out.println(formatBashStringBoldAndGreen("Setting Data Folder to this: " + p.toString()));

        // IMPORTANT: Override the CLI project data directory for this test
        GTDCLI.setDataFolder(p);

		String data = sequenzNewProject("Test_Project") + SomeCommands.exit + '\n';

		ByteArrayInputStream bais = new ByteArrayInputStream(data.getBytes());
		InputStreamSession iss = new InputStreamSession(bais);

        System.out.println(formatBashStringBoldAndGreen("Data Folder is set to this: " + GTDCLI.getDataFolder().toString()));
        GTDCLI cli = new GTDCLI(iss);

        File[] list = GTDCLI.getListOfFilesFromDataFolder();

        prjctSet = GTDCLI.loadProjects(GTDCLI.getDataFolder());
        System.out.println(prjctSet);

        saveText(p.toString() + "/" + "blub", "blub");

    	File[] listOfFiles = GTDCLI.getListOfFilesFromDataFolder();

    	for(File f: listOfFiles)
    	{
    		System.out.println("Hi!");
    		System.out.println(f.toString());
    	}

    	makeUpProjectWithLaterNDTAndADT();

        assert(!prjctSet.isEmpty());
        
	}

	public void makeUpProjectWithLaterNDTAndADT()
	{
		
		JSONObject pJSON = new JSONObject();
		
		pJSON.put(nameKey, "One_Week_Later");
		pJSON.put(statusKey, StatusMGMT.atbd);
		pJSON.put(goalKey, "Testing.");
		
		LocalDateTime ldt = LocalDateTime.now().plusDays(7);
		String ldtStr =  LittleTimeTools.timeString(ldt);
		pJSON.put(NDTKey, ldtStr);
		pJSON.put(ADTKey, ldtStr);


		JSONObject sJSON = new JSONObject();
		sJSON.put(StepJSONKeyz.descKey, "Step by Step .. ooh baby!");
		sJSON.put(StepJSONKeyz.statusKey, StatusMGMT.atbd);
		
		ldt = ldt.plusSeconds(70);
		ldtStr = LittleTimeTools.timeString(ldt);
		sJSON.put(StepJSONKeyz.ADTKey, ldtStr);
		
		JSONArray steps = new JSONArray();
		steps.put(0, sJSON);
		pJSON.put(stepArrayKey, steps);

		prjctSet.add(pJSON);
	}
}
