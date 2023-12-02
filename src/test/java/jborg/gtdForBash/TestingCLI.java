package jborg.gtdForBash;


import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.time.LocalDateTime;

import java.util.InputMismatchException;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import allgemein.LittleTimeTools;
import consoleTools.InputStreamSession;
import someMath.CollectionManipulation;


class TestingCLI
{

	//TODO: die \n muessen weg!!!
	String wakeProjectName = "Wake up\n";

	String terminatePrjctName = "Terminated-Project\n";
	
	String addNotePrjctName = "Project to add Note";
	
	String appendStpPrjctName = "Appending Steps Project\n";
	
	
	String modPrjctName = "Maybe Baby";
	String modPrjctGoal = "MOD-Project Test";
	
	String newPrjctName = "Project Nuovo";
	String newPrjctGoal = "Testing this here";
	
	String stepDesc = "Hello Bello GoodBye!";
	String stepDesc2 = "Grrrl\n";
	String stepDesc3 = "Bla bla\n";
	
	String noticeOne = "Note1";
	String noticeTwo = "Note2";
	
	GTDCLI gtdCli;
	
	private void clearFolder()
	{

    	File[] listOfFiles = GTDCLI.getListOfFilesFromDataFolder();
    	
    	for(File file: listOfFiles)
    	{
    		
    		if(file.isFile())file.delete();
    	}
	}

	public String setupNewProject(String prjctName, LocalDateTime ldtPrjctDLDT, LocalDateTime ldtStpDLDT)
	{
		
		String changePrjctBDT = "No";
		String prjctDLDT = translateTimeToAnswerString(ldtPrjctDLDT);
		String changeStepBDT = "No";
		String chosenFromStatieList = "1";//ATBD
		String stepDLDT = translateTimeToAnswerString(ldtStpDLDT);
		
		String data = GTDCLI.new_Project + '\n'
				+ prjctName + '\n'
				+ newPrjctGoal + '\n'
				+ changePrjctBDT + '\n'
				+ prjctDLDT
				+ changeStepBDT + '\n'
				+ chosenFromStatieList + '\n'
				+ stepDesc + '\n'
				+ stepDLDT;
				
		return data;
	}

	public String setupMODProject(String prjctName, LocalDateTime ldtPrjctDLDT, LocalDateTime ldtStpDLDT)
	{
		
		String changePrjctBDT = "No";
		
		String data = GTDCLI.new_MOD + '\n'
				+ prjctName + '\n'
				+ modPrjctGoal + '\n'
				+ changePrjctBDT + '\n';
				
		return data;
	}
	
	public String setupAddNote(String prjctName, LocalDateTime ldtPrjctDLDT, LocalDateTime ldtStpDLDT)
	{

		String data = setupNewProject(prjctName, ldtPrjctDLDT, ldtStpDLDT);
				
		data = data
				+ GTDCLI.add_Note + " " + prjctName + '\n'
				+ noticeOne + "\n"
				+ GTDCLI.add_Note + " " + prjctName + '\n'
				+ noticeTwo + "\n";
				
		return data;
	}
	
	@Test
	public void testNewPrjct() throws Exception
	{
				
		LocalDateTime jetzt = LocalDateTime.now();
		
		String data = setupNewProject(newPrjctName, jetzt.plusHours(10), jetzt.plusHours(9));
		data = data + GTDCLI.exit + '\n';
		
		ByteArrayInputStream bais = new ByteArrayInputStream(data.getBytes());
		InputStreamSession iss = new InputStreamSession(bais);
		
		clearFolder();

        gtdCli = new GTDCLI(iss);				
		
		Set<JSONObject> projects = GTDCLI.loadProjects();
		
		assert(projects.size()==1);
		
		JSONObject np = CollectionManipulation.catchRandomElementOfSet(projects);
		
		assert(np.has(ProjectJSONKeyz.nameKey));
		String name = np.getString(ProjectJSONKeyz.nameKey);
		assert(name.equals(newPrjctName));
		
		assert(np.has(ProjectJSONKeyz.statusKey));
		String status = np.getString(ProjectJSONKeyz.statusKey);
		assert(status.equals(StatusMGMT.atbd));
		
		assert(np.has(ProjectJSONKeyz.BDTKey));
		String bdtStr = np.getString(ProjectJSONKeyz.BDTKey);
		LocalDateTime bdt = LittleTimeTools.LDTfromTimeString(bdtStr);
		assert(jetzt.minusSeconds(4).isBefore(bdt));//bdt not older than 4 seconds!
		
		assert(np.has(ProjectJSONKeyz.NDDTKey));
		String nddtStr = np.getString(ProjectJSONKeyz.NDDTKey);
		LocalDateTime nddt = LittleTimeTools.LDTfromTimeString(nddtStr);
		assert(jetzt.minusSeconds(4).isBefore(nddt));//nddt ist nicht älter als 4 Sekunden.
		
		assert(np.has(ProjectJSONKeyz.DLDTKey));
		String dldtStr = np.getString(ProjectJSONKeyz.DLDTKey);
		LocalDateTime dldt = LittleTimeTools.LDTfromTimeString(dldtStr);
		assert(jetzt.minusSeconds(4).isBefore(dldt));//dldt is not older than 4 seconds.
		
		assert(np.has(ProjectJSONKeyz.stepArrayKey));
		JSONArray stpArr = np.getJSONArray(ProjectJSONKeyz.stepArrayKey);
		JSONObject step = stpArr.getJSONObject(0);
		
		bdtStr = step.getString(StepJSONKeyz.BDTKey);
		bdt = LittleTimeTools.LDTfromTimeString(bdtStr);
		assert(jetzt.minusSeconds(5).isBefore(bdt));//bdt not older than 5 seconds!
		
		nddtStr = step.getString(StepJSONKeyz.NDDTKey);
		nddt = LittleTimeTools.LDTfromTimeString(nddtStr);
		assert(jetzt.minusSeconds(5).isBefore(nddt));//nddt ist nicht älter als 5 Sekunden.
		
		dldtStr = step.getString(StepJSONKeyz.DLDTKey);
		dldt = LittleTimeTools.LDTfromTimeString(dldtStr);
		assert(jetzt.minusSeconds(5).isBefore(dldt));//dldt is not older than 5 seconds.

		String goal = np.getString(ProjectJSONKeyz.goalKey);
		assert(goal.equals(newPrjctGoal));
		
		String stepDesc = step.getString(StepJSONKeyz.descKey);
		assert(stepDesc.equals(this.stepDesc));
	}

	@Test
	public void testNewMODProject() throws InputMismatchException, JSONException, IOException, URISyntaxException, StepTerminationException, ProjectTerminationException, SpawnStepException, SpawnProjectException, TimeGoalOfProjectException
	{
		LocalDateTime jetzt = LocalDateTime.now();
		
		LocalDateTime pDLDT = jetzt.plusHours(1);
		LocalDateTime stpDLDT = jetzt.plusMinutes(30);
		String data = setupMODProject(modPrjctName, pDLDT, stpDLDT);
		data = data + GTDCLI.exit + '\n';
		
		ByteArrayInputStream bais = new ByteArrayInputStream(data.getBytes());
		InputStreamSession iss = new InputStreamSession(bais);

		clearFolder();

        gtdCli = new GTDCLI(iss);				
		
		Set<JSONObject> projects = GTDCLI.loadProjects();
		
		assert(projects.size()==1);
	}
	
	@Test
	public void testAddNoteToProject() throws InputMismatchException, JSONException, IOException, URISyntaxException, StepTerminationException, ProjectTerminationException, SpawnStepException, SpawnProjectException, TimeGoalOfProjectException
	{
		
		LocalDateTime jetzt = LocalDateTime.now();
				
		String data = setupAddNote(addNotePrjctName, jetzt.plusHours(1), jetzt.plusMinutes(3));
		data = data + GTDCLI.exit + '\n';
		
		ByteArrayInputStream bais = new ByteArrayInputStream(data.getBytes());
		InputStreamSession iss = new InputStreamSession(bais);
		
		clearFolder();
		gtdCli = new GTDCLI(iss);
		System.out.println(data);
		
		Set<JSONObject> projects = GTDCLI.loadProjects();
		System.out.println(projects.size());
		//assert(projects.size()==1);
	}

	public String translateTimeToAnswerString(LocalDateTime ldt)
	{
		
		int hour = ldt.getHour();
		int minute = ldt.getMinute();
		int year = ldt.getYear();
		int month = ldt.getMonthValue();
		int day = ldt.getDayOfMonth();
		
		return hour + "\n" + minute +"\n" + year + "\n" + month + "\n" + day +"\n";

	}
}
