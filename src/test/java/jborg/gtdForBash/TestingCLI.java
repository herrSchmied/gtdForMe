package jborg.gtdForBash;


import java.io.ByteArrayInputStream;
import java.io.File;
import java.time.LocalDateTime;
import java.util.Set;

import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import consoleTools.InputStreamSession;
import someMath.CollectionManipulation;


class TestingCLI
{

	//TODO: die \n muessen weg!!!
	String wakeProjectName = "Wake up\n";

	String terminatePrjctName = "Terminated-Project\n";
	
	String addNotePrjctName = "Project to add Note\n";
	
	String appendStpPrjctName = "Appending Steps Project\n";
	
	
	String modPrjctName = "Maybe Baby\n";
	String modPrjctGoal = "MOD-Project Test.\n";
	
	String newPrjctName = "Project Nuovo\n";
	String newPrjctGoal = "Testing this here.\n";
	
	String stepDesc = "Hello Bello GoodBye!\n";
	String stepDesc2 = "Grrrl\n";
	String stepDesc3 = "Bla bla\n";
	
	String noticeOne = "Note1";
	String noticeTwo = "Note2";
	
	String cliExitCmd = "exit\n";
	
	GTDCLI gtdCli;
	
	private void clearFolder()
	{

    	File[] listOfFiles = GTDCLI.getListOfFilesFromDataFolder();
    	
    	for(File file: listOfFiles)
    	{
    		
    		if(file.isFile())file.delete();
    	}
	}
	
	public String setUpDataNewProject(String prjctName, LocalDateTime ldtPrjctDLDT, LocalDateTime ldtStpDLDT)
	{
		
		String clicommand = "new project\n";
		String changePrjctBDT = "No\n";
		String prjctDLDT = translateTimeToAnswerString(ldtPrjctDLDT);
		String changeStepBDT = "No\n";
		String chosenFromStatieList = "1\n";//ATBD
		String stepDLDT = translateTimeToAnswerString(ldtStpDLDT);
		
		String data = clicommand
				+ prjctName
				+ newPrjctGoal
				+ changePrjctBDT
				+ prjctDLDT
				+ changeStepBDT
				+ chosenFromStatieList
				+ stepDesc
				+ stepDLDT
				+ cliExitCmd;
				
		return data;
	}
	
	@Test
	public void testNewPrjct() throws Exception
	{
				
		LocalDateTime jetzt = LocalDateTime.now();
		

		String data = setUpDataNewProject(newPrjctName, jetzt.plusHours(10), jetzt.plusHours(9));
		
		ByteArrayInputStream bais = new ByteArrayInputStream(data.getBytes());
		InputStreamSession iss = new InputStreamSession(bais);
		
		clearFolder();

        gtdCli = new GTDCLI(iss);				
		
		Set<JSONObject> projects = GTDCLI.loadProjects();
		
		assert(projects.size()==1);
		
		JSONObject np = CollectionManipulation.catchRandomElementOfSet(projects);
		
		assert(np.has(ProjectJSONKeyz.nameKey));
		assert(np.has(ProjectJSONKeyz.statusKey));
		assert(np.has(ProjectJSONKeyz.BDTKey));
		assert(np.has(ProjectJSONKeyz.NDDTKey));
		assert(np.has(ProjectJSONKeyz.DLDTKey));
		assert(np.has(ProjectJSONKeyz.stepArrayKey));
		
		assert(!np.has(ProjectJSONKeyz.noteArrayKey));
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

	/*
	@Test
	public void testSpawnNewProject() throws InputMismatchException, SpawnProjectException, TimeGoalOfProjectException, SpawnStepException, IOException
	{
		JSONObject pJSON = arrangeNewPrjct().getValue();
		String name = pJSON.getString(ProjectJSONKeyz.nameKey);
		
		int l = newPrjctName.length()-1;
		assert(name.equals(newPrjctName.substring(0, l)));

		String status = pJSON.getString(ProjectJSONKeyz.statusKey);
		assert(StatusMGMT.atbd.equals(status));
		
		JSONArray stepArray = pJSON.getJSONArray(ProjectJSONKeyz.stepArrayKey);
		JSONObject step = stepArray.getJSONObject(0);
		String stpStatus = step.getString(StepJSONKeyz.statusKey);
		assert(stpStatus.equals(status));
		
		String stepDesc = step.getString(StepJSONKeyz.descKey);
		l = this.stepDesc.length()-1;
		assert(stepDesc.equals(this.stepDesc.substring(0,l)));
		
		String goal = pJSON.getString(ProjectJSONKeyz.goalKey);
		l = newPrjctGoal.length()-1;
		assert(goal.equals(newPrjctGoal.substring(0,l)));
	}
	*/

}
