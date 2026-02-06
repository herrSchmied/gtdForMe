package jborg.gtdForBash;


import java.io.IOException;

import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import allgemein.LittleTimeTools;

import consoleTools.BashSigns;
import jborg.gtdForBash.exceptions.StatisticalToolsException;
import jborg.gtdForBash.exceptions.TimeSpanCreatorException;
import jborg.gtdForBash.exceptions.TimeSpanException;
import jborg.gtdForBash.exceptions.ToolBoxException;
import jborg.gtdForBash.exceptions.WeekDataException;
import someMath.NaturalNumberException;


import static jborg.gtdForBash.SequenzesForISS.*;
import static jborg.gtdForBash.ProjectJSONToolbox.*;
import static jborg.gtdForBash.ProjectJSONKeyz.*;

public class TestingCLI
{

	static Set<JSONObject> projects = new HashSet<>();

	final  LocalDateTime prjctDLDT = jetzt.plusHours(1);
	final  LocalDateTime stepDLDT = jetzt.plusMinutes(30);

	@BeforeEach
	public void setup() throws JSONException, IOException, URISyntaxException, NaturalNumberException, WeekDataException, TimeSpanException, ToolBoxException, StatisticalToolsException, TimeSpanCreatorException
	{    	
		projects = ProjectSetForTesting.get();
	}

	@Test
	public void testNewPrjct() throws JSONException, IOException, URISyntaxException, NaturalNumberException
	{

		LocalDateTime jetzt = LocalDateTime.now().plusMinutes(1);
		JSONObject newProject = pickProjectByName(SequenzesForISS.getNewProjectName(1), projects);
		
		String status = newProject.getString(ProjectJSONKeyz.statusKey);
		assert(status.equals(StatusMGMT.atbd));
		
		String adtStr = newProject.getString(ADTKey);
		LocalDateTime adt = LittleTimeTools.LDTfromTimeString(adtStr);
		assert(jetzt.isAfter(adt));

		String dldtStr = newProject.getString(DLDTKey);
		LocalDateTime dldt = LittleTimeTools.LDTfromTimeString(dldtStr);
		assert(jetzt.isBefore(dldt));
		
		JSONArray stpArr = newProject.getJSONArray(stepArrayKey);
		JSONObject step = stpArr.getJSONObject(0);
		
		
		adtStr = step.getString(StepJSONKeyz.ADTKey);
		adt = LittleTimeTools.LDTfromTimeString(adtStr);
		assert(jetzt.isAfter(adt));
		
		dldtStr = step.getString(StepJSONKeyz.DLDTKey);
		dldt = LittleTimeTools.LDTfromTimeString(dldtStr);
		assert(jetzt.isBefore(dldt));

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
		String stepStatus = step.getString(StepJSONKeyz.statusKey);
		System.out.println(BashSigns.boldRBCPX+stepStatus+BashSigns.boldRBCSX);
		assert(StatusMGMT.terminalSet.contains(stepStatus));
		assert(StatusMGMT.terminalSet.contains(project.get(ProjectJSONKeyz.statusKey)));

	}
	 
	public void testKillProject() throws JSONException, IOException, URISyntaxException, NaturalNumberException
	{
		
		JSONObject project = pickProjectByName(SequenzesForISS.getNewProjectName(3), projects);
		assert(project!=null);
		
		JSONObject step = getLastStep(project);
		String stepStatus = step.getString(StepJSONKeyz.statusKey);
	
		assert(StatusMGMT.terminalSet.contains(stepStatus));
		assert(StatusMGMT.terminalSet.contains(project.get(ProjectJSONKeyz.statusKey)));
	}

	@Test 
	public void testKillStep() throws JSONException, IOException, URISyntaxException, NaturalNumberException
	{

		JSONObject project = pickProjectByName(SequenzesForISS.getNewProjectName(3), projects);
		assert(project!=null);
		
		JSONObject step = getLastStep(project);
	 	assert(StatusMGMT.terminalSet.contains(step.getString(StepJSONKeyz.statusKey))); 
	}

	@Test
	public void testNextStep() throws JSONException, IOException, URISyntaxException, NaturalNumberException
	{

		JSONObject project = pickProjectByName(SequenzesForISS.getNewProjectName(4), projects);
		assert(project!=null);

		JSONObject step2 = getLastStep(project);

		String stepStatus = step2.getString(StepJSONKeyz.statusKey);
		assert(StatusMGMT.stepStarterSet.contains(stepStatus));

		assert(StatusMGMT.onTheWayStepSet.contains(stepStatus));

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

		String status = newProject.getString(statusKey);
		assert(status.equals(StatusMGMT.atbd));
		assert(newProject.has(ADTKey));

		String adtStr = newProject.getString(ADTKey);
		assert(newProject.has(DLDTKey));
		assert(prjctDeadlineNone.equals(newProject.getString(DLDTKey)));
		assert(newProject.has(stepArrayKey));
		
		JSONArray stpArr = newProject.getJSONArray(stepArrayKey);
		JSONObject step = stpArr.getJSONObject(0);

		adtStr = step.getString(StepJSONKeyz.ADTKey);
		LocalDateTime adt = LittleTimeTools.LDTfromTimeString(adtStr);
		assert(adt.isBefore(LocalDateTime.now()));
		
		assert(step.has(StepJSONKeyz.DLDTKey));
		assert(stepDeadlineNone.equals(step.getString(StepJSONKeyz.DLDTKey)));
		
		String goal = newProject.getString(goalKey);
		assert(goal.equals(newPrjctGoal));

		String stepDesc2 = step.getString(StepJSONKeyz.descKey);
		assert(stepDesc2.equals(stepDesc));
	}
}