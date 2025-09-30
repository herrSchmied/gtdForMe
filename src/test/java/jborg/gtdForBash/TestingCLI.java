package jborg.gtdForBash;


import java.io.File;
import java.io.IOException;

import java.net.URISyntaxException;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import allgemein.LittleTimeTools;

import consoleTools.BashSigns;


import someMath.NaturalNumberException;


import static jborg.gtdForBash.SequenzesForISS.*;
import static jborg.gtdForBash.ProjectJSONToolbox.*;

public class TestingCLI
{

	static GTDCLI gtdCli;
	static Set<JSONObject> projects = new HashSet<>();

	final  LocalDateTime prjctDLDT = jetzt.plusHours(1);
	final  LocalDateTime stepDLDT = jetzt.plusMinutes(30);


	@BeforeAll
	public static void clearFolder() throws JSONException, IOException, URISyntaxException, NaturalNumberException
	{

    	File[] listOfFiles = GTDCLI.getListOfFilesFromDataFolder(GTDCLI.projectDataFolderRelativePath);
    	
    	for(File file: listOfFiles)
    	{
    		
    		if(file.isFile())file.delete();
    	}
    	
		projects = ProjectSetForTesting.get();
	}

	@Test
	public void testNewPrjct() throws JSONException, IOException, URISyntaxException, NaturalNumberException
	{

		LocalDateTime jetzt = LocalDateTime.now().plusMinutes(1);
		JSONObject newProject = pickProjectByName(SequenzesForISS.getNewProjectName(1), projects);
		
		String status = newProject.getString(ProjectJSONKeyz.statusKey);
		assert(status.equals(StatusMGMT.atbd));
		
		String bdtStr = newProject.getString(ProjectJSONKeyz.BDTKey);
		LocalDateTime bdt = LittleTimeTools.LDTfromTimeString(bdtStr);
		assert(jetzt.isAfter(bdt));
		
		String nddtStr = newProject.getString(ProjectJSONKeyz.NDDTKey);
		LocalDateTime nddt = LittleTimeTools.LDTfromTimeString(nddtStr);
		assert(jetzt.isAfter(nddt));
		assert(bdt.equals(nddt));

		String dldtStr = newProject.getString(ProjectJSONKeyz.DLDTKey);
		LocalDateTime dldt = LittleTimeTools.LDTfromTimeString(dldtStr);
		assert(jetzt.isAfter(dldt));
		
		JSONArray stpArr = newProject.getJSONArray(ProjectJSONKeyz.stepArrayKey);
		JSONObject step = stpArr.getJSONObject(0);
		
		bdtStr = step.getString(StepJSONKeyz.BDTKey);
		bdt = LittleTimeTools.LDTfromTimeString(bdtStr);
		assert(jetzt.isAfter(bdt));
		
		nddtStr = step.getString(StepJSONKeyz.NDDTKey);
		nddt = LittleTimeTools.LDTfromTimeString(nddtStr);
		assert(jetzt.isAfter(nddt));
		
		dldtStr = step.getString(StepJSONKeyz.DLDTKey);
		dldt = LittleTimeTools.LDTfromTimeString(dldtStr);
		assert(jetzt.isAfter(dldt));//dldt is not older than 5 seconds.

		String goal = newProject.getString(ProjectJSONKeyz.goalKey);
		assert(goal.equals(newPrjctGoal));
		
		String stepDesc = step.getString(StepJSONKeyz.descKey);
		assert(stepDesc.equals(stepDesc));
	}

	@Test
	public void testWakeMOD() throws JSONException, IOException, URISyntaxException, NaturalNumberException
	{

		JSONObject project = pickProjectByName(wakeProjectName, projects);
		assert(project!=null);
		String prjctStatus = project.getString(ProjectJSONKeyz.statusKey);
		assert(!prjctStatus.equals(StatusMGMT.mod));
		String goal = project.getString(ProjectJSONKeyz.goalKey);
		assert(goal.equals(modPrjctGoal));
	}

	@Test
	public void testNewMODProject() throws JSONException, IOException, URISyntaxException, NaturalNumberException
	{

		JSONObject project = pickProjectByName(modPrjctName, projects);
		String ziel = project.getString(ProjectJSONKeyz.goalKey);
		assert(ziel.equals(modPrjctGoal));
		assert(isMODProject.test(project));
	}
	 

	@Test
	public void testAddNoteToProject() throws JSONException, IOException, URISyntaxException, NaturalNumberException
	{

		JSONObject project = pickProjectByName(SequenzesForISS.getNewProjectName(2), projects);
		assert(project!=null);

		JSONArray notesArr = project.getJSONArray(ProjectJSONKeyz.noteArrayKey);
		String note1 = notesArr.getString(0); String note2 = notesArr.getString(1);
		assert(note1.equals(noticeOne)); assert(note2.equals(noticeTwo));
	}
	  
	@Test
	public void testKillProjectWithNoDLDT() throws JSONException, IOException, URISyntaxException, NaturalNumberException
	{

		JSONObject project = pickProjectByName(killPrjctNameNoDLDT, projects);
		assert(project!=null);
	 
	
		JSONObject step = getLastStep(project);
		StatusMGMT statusMGMT =	StatusMGMT.getInstance();
		Set<String> terminalSet = statusMGMT.getStatesOfASet(StatusMGMT.terminalSetName);
		String stepStatus = step.getString(StepJSONKeyz.statusKey);
		System.out.println(BashSigns.boldRBCPX+stepStatus+BashSigns.boldRBCSX);
		assert(terminalSet.contains(stepStatus));
		assert(terminalSet.contains(project.get(ProjectJSONKeyz.statusKey)));

	}
	 
	public void testKillProject() throws JSONException, IOException, URISyntaxException, NaturalNumberException
	{
		
		JSONObject project = pickProjectByName(SequenzesForISS.getNewProjectName(3), projects);
		assert(project!=null);
		
		JSONObject step = getLastStep(project); 
		StatusMGMT statusMGMT =	StatusMGMT.getInstance(); 
		Set<String> terminalSet = statusMGMT.getStatesOfASet(StatusMGMT.terminalSetName);
		String stepStatus = step.getString(StepJSONKeyz.statusKey);
		//System.out.println(BashSigns.boldRBCPX+stepStatus+BashSigns.boldRBCSX);
		assert(terminalSet.contains(stepStatus));
		assert(terminalSet.contains(project.get(ProjectJSONKeyz.statusKey)));
	}

	@Test 
	public void testKillStep() throws JSONException, IOException, URISyntaxException, NaturalNumberException
	{

		JSONObject project = pickProjectByName(SequenzesForISS.getNewProjectName(3), projects);
		assert(project!=null);
		
		JSONObject step = getLastStep(project);
		StatusMGMT statusMGMT = StatusMGMT.getInstance();
		Set<String> terminalSet = statusMGMT.getStatesOfASet(StatusMGMT.terminalSetName);
	 	assert(terminalSet.contains(step.getString(StepJSONKeyz.statusKey))); 
	}

	@Test
	public void testNextStep() throws JSONException, IOException, URISyntaxException, NaturalNumberException
	{

		JSONObject project = pickProjectByName(SequenzesForISS.getNewProjectName(4), projects);
		assert(project!=null);

		JSONObject step2 = getLastStep(project);
		StatusMGMT statusMGMT = StatusMGMT.getInstance();
		Set<String> atStartSet = statusMGMT.getStatesOfASet(StatusMGMT.atStartSetName);
		String stepStatus = step2.getString(StepJSONKeyz.statusKey);
		assert(atStartSet.contains(stepStatus));
	 
		Set<String> onTheWaySet = statusMGMT.getStatesOfASet(StatusMGMT.onTheWaySetName);
		assert(onTheWaySet.contains(stepStatus));

		JSONArray steps = project.getJSONArray(ProjectJSONKeyz.stepArrayKey);
		assert(steps.length()==2);
	 
		String desc1 = step2.getString(StepJSONKeyz.descKey);
		assert(desc1.equals(stepDesc2));

		JSONObject step1 = steps.getJSONObject(0); 
		String desc0 = step1.getString(StepJSONKeyz.descKey);
		assert(desc0.equals(stepDesc));
	}

	@Test
	public void testNewProjectWithoutDeadline() throws JSONException, IOException, URISyntaxException, NaturalNumberException
	{
		
		JSONObject newProject = pickProjectByName(newPrjctNoDLDT, projects);
		assert(newProject!=null);
		assert(newProject.has(ProjectJSONKeyz.statusKey));
		
		String status = newProject.getString(ProjectJSONKeyz.statusKey);
		assert(status.equals(StatusMGMT.atbd));
		assert(newProject.has(ProjectJSONKeyz.BDTKey));

		String bdtStr = newProject.getString(ProjectJSONKeyz.BDTKey);
		LocalDateTime bdt = LittleTimeTools.LDTfromTimeString(bdtStr);
		assert(jetzt.minusSeconds(4).isBefore(bdt));//bdt not older than 4 seconds!
		assert(newProject.has(ProjectJSONKeyz.NDDTKey));

		String nddtStr = newProject.getString(ProjectJSONKeyz.NDDTKey);
		LocalDateTime nddt = LittleTimeTools.LDTfromTimeString(nddtStr);
		assert(jetzt.minusSeconds(4).isBefore(nddt));//nddt ist nicht älter als 4 Sekunden.
		assert(newProject.has(ProjectJSONKeyz.DLDTKey));
		assert(prjctDeadlineNone.equals(newProject.getString(ProjectJSONKeyz.DLDTKey)));
		assert(newProject.has(ProjectJSONKeyz.stepArrayKey)); 
		
		JSONArray stpArr = newProject.getJSONArray(ProjectJSONKeyz.stepArrayKey);
		JSONObject step = stpArr.getJSONObject(0);
		bdtStr = step.getString(StepJSONKeyz.BDTKey);
		bdt = LittleTimeTools.LDTfromTimeString(bdtStr);
		assert(jetzt.minusSeconds(5).isBefore(bdt));//bdt not older than 5 seconds!

		nddtStr = step.getString(StepJSONKeyz.NDDTKey);
		nddt = LittleTimeTools.LDTfromTimeString(nddtStr);
		assert(jetzt.minusSeconds(5).isBefore(nddt));//nddt ist nicht älter als 5 Sekunden.
		assert(step.has(StepJSONKeyz.DLDTKey));
		assert(stepDeadlineNone.equals(step.getString(StepJSONKeyz.DLDTKey)));
		
		String goal = newProject.getString(ProjectJSONKeyz.goalKey);
		assert(goal.equals(newPrjctGoal));

		String stepDesc2 = step.getString(StepJSONKeyz.descKey);
		assert(stepDesc2.equals(stepDesc));
	}
}
