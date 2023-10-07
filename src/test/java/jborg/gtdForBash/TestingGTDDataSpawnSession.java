package jborg.gtdForBash;


import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.Set;
import javafx.util.Pair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import consoleTools.InputStreamSession;


public class TestingGTDDataSpawnSession
{

	String terminatePrjctName = "Terminated-Project\n";
	
	String modPrjctName = "Maybe Baby\n";
	String modPrjctGoal = "Limbo.\n";
	
	String newPrjctName = "Project Nuovo\n";
	String newPrjctGoal = "Testing this here.\n";
	
	String stepDesc = "Hello Bello GoodBye!\n";
	String stepDesc2 = "Grrrl\n";
	String stepDesc3 = "Bla bla\n";

	public void basicModInputLines()
	{

	}
	
	public Pair<GTDDataSpawnSession, JSONObject> arrangeNewPrjct()
	{
				
		LocalDateTime jetzt = LocalDateTime.now();
		

		String data = setUpDataNewProject(jetzt.plusMinutes(10), jetzt.plusMinutes(9));
		
		ByteArrayInputStream bais = new ByteArrayInputStream(data.getBytes());
		InputStreamSession iss = new InputStreamSession(bais);
		GTDDataSpawnSession gdss = new GTDDataSpawnSession(iss);
		JSONObject pJSON = gdss.spawnNewProject(new HashMap<String, JSONObject>(), StatusMGMT.getInstance());
		
		return new Pair<GTDDataSpawnSession, JSONObject>(gdss, pJSON);
	}
	
	public Pair<GTDDataSpawnSession, JSONObject> arrangeAppendStepPrjct()
	{
				
		LocalDateTime jetzt = LocalDateTime.now();	

		String data = setUpDataAppendSteps(jetzt.plusMinutes(10), jetzt.plusMinutes(9));
		
		ByteArrayInputStream bais = new ByteArrayInputStream(data.getBytes());
		InputStreamSession iss = new InputStreamSession(bais);
		GTDDataSpawnSession gdss = new GTDDataSpawnSession(iss);
		JSONObject pJSON = gdss.spawnNewProject(new HashMap<String, JSONObject>(), StatusMGMT.getInstance());
		
		return new Pair<GTDDataSpawnSession, JSONObject>(gdss, pJSON);
	}
	
	public Pair<GTDDataSpawnSession, JSONObject> arrangeMODPrjct()
	{
		
		LocalDateTime jetzt = LocalDateTime.now();
				
		LocalDateTime pDLDT = jetzt.plusHours(1);
		LocalDateTime stpDLDT = jetzt.plusMinutes(30);
		String prjctMODData = setUpMODProject(modPrjctName, pDLDT, stpDLDT);
		
		ByteArrayInputStream bais = new ByteArrayInputStream(prjctMODData.getBytes());
		InputStreamSession iss = new InputStreamSession(bais);
		GTDDataSpawnSession gdss = new GTDDataSpawnSession(iss);
		JSONObject pJSONMod = gdss.spawnNewProject(new HashMap<String, JSONObject>(), StatusMGMT.getInstance());
		
		return new Pair<GTDDataSpawnSession, JSONObject>(gdss, pJSONMod);
	}
	
	public Pair<GTDDataSpawnSession, JSONObject> arrangeTerminatePrjct()
	{
		
		LocalDateTime jetzt = LocalDateTime.now();
				
		LocalDateTime pDLDT = jetzt.plusHours(1);
		LocalDateTime stpDLDT = jetzt.plusMinutes(30);
		String prjctTerminateData = setUpDataTerminateNewProject(pDLDT, stpDLDT);
		
		ByteArrayInputStream bais = new ByteArrayInputStream(prjctTerminateData.getBytes());
		InputStreamSession iss = new InputStreamSession(bais);
		GTDDataSpawnSession gdss = new GTDDataSpawnSession(iss);
		JSONObject pJSONMod = gdss.spawnNewProject(new HashMap<String, JSONObject>(), StatusMGMT.getInstance());
		
		return new Pair<GTDDataSpawnSession, JSONObject>(gdss, pJSONMod);
	}
	
	public String setUpDataAppendSteps(LocalDateTime ldtPrjctDLDT, LocalDateTime ldtStpDLDT)
	{
		
		String modPrjct= "No\n";
		String prjctGoal = "Appending Steps to New Project Test.\n";
		String changePrjctBDT = "No\n";
		String prjctDLDT = translateTimeToAnswerString(ldtPrjctDLDT);
		String changeStepBDT = "No\n";
		String chosenFromStatieList = "1\n";//ATBD
		String stpDesc = "First do this than that.\n";
		String stepDLDT = translateTimeToAnswerString(ldtStpDLDT);
						
		
		String oldStepWasSuccess = "Yes\n";
		String wantToMakeTerminalNote = "Yes\n";
		String terminalNote = "I'm Thru wit it.\n";
		String wantToChangeTDT = "No\n";
		String specialStpBDT2 = "No\n";
		String step2DLDT = translateTimeToAnswerString(ldtPrjctDLDT.minusSeconds(1));

		String step2WasSuccess = "No\n";
		String wantToMakeTerminalNote2 = "No\n";
		String wantToChangeTDT2 = "No\n";
		String specialStpBDT3 = "No\n";
		String step3DLDT = translateTimeToAnswerString(ldtPrjctDLDT.minusSeconds(2));

		String data = newPrjctName
		+ modPrjct
		+ prjctGoal
		+ changePrjctBDT
		+ prjctDLDT
		+ changeStepBDT
		+ chosenFromStatieList
		+ stpDesc
		+ stepDLDT
		+ oldStepWasSuccess
		+ wantToMakeTerminalNote
		+ terminalNote
		+ wantToChangeTDT
		+ specialStpBDT2
		+ chosenFromStatieList
		+ stepDesc2
		+ step2DLDT
		+ step2WasSuccess
		+ wantToMakeTerminalNote2
		+ wantToChangeTDT2
		+ specialStpBDT3
		+ chosenFromStatieList
		+ stepDesc3
		+ step3DLDT;
		
		return data;
	}
	
	public String setUpDataTerminateNewProject(LocalDateTime ldtPrjctDLDT, LocalDateTime ldtStpDLDT)
	{
		
		
		String modPrjct= "No\n";
		String prjctGoal = "New Project Test.\n";
		String changePrjctBDT = "No\n";
		String prjctDLDT = translateTimeToAnswerString(ldtPrjctDLDT);

		String changeStepBDT = "No\n";
		String chosenFromStatieList = "1\n";//ATBD
		String stpDesc = "First do this than that.\n";
		String stepDLDT = translateTimeToAnswerString(ldtStpDLDT);
		
		String stpWasSuccess = "Yes\n";
		String makeStpTerminalNote = "No\n";
		String changeStpTDT = "No\n";
		
		String projectSuccess= "Yes\n";
		String changePrjctTDT = "No\n";
		String makeTDTNote = "Yes\n";
		String tdtNote = "Ich habe Fertig!\n";
		
		String data = terminatePrjctName
				+ modPrjct
				+ prjctGoal
				+ changePrjctBDT
				+ prjctDLDT
				+ changeStepBDT
				+ chosenFromStatieList
				+ stpDesc
				+ stepDLDT
				+ stpWasSuccess
				+ makeStpTerminalNote
				+ changeStpTDT
				+ projectSuccess
				+ changePrjctTDT
				+ makeTDTNote
				+ tdtNote;
		
		return data;
	}
	
	public String setUpDataNewProject(LocalDateTime ldtPrjctDLDT, LocalDateTime ldtStpDLDT)
	{
		
		String modPrjct= "No\n";
		String changePrjctBDT = "No\n";
		String prjctDLDT = translateTimeToAnswerString(ldtPrjctDLDT);
		String changeStepBDT = "No\n";
		String chosenFromStatieList = "1\n";//ATBD
		String stepDLDT = translateTimeToAnswerString(ldtStpDLDT);
		
		String data = newPrjctName
				+ modPrjct
				+ newPrjctGoal
				+ changePrjctBDT
				+ prjctDLDT
				+ changeStepBDT
				+ chosenFromStatieList
				+ stepDesc
				+ stepDLDT;				
				
		return data;
	}
	
	public String setUpMODProject(String prjctName, LocalDateTime ldtPrjctDLDT, LocalDateTime ldtStpDLDT)
	{
		
		String modPrjct= "Yes\n";
		String prjctGoal = "MOD-Project Test.\n";
		String changePrjctBDT = "No\n";
		
		String data = prjctName
				+ modPrjct
				+ prjctGoal
				+ changePrjctBDT;
				
		return data;
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
	
	@Test
	public void testSpawnNewProject() 
	{
		JSONObject pJSON = arrangeNewPrjct().getValue();
		String name = pJSON.getString(ProjectJSONKeyz.nameKey);
		
		int l = newPrjctName.length()-1;
		assert(name.equals(newPrjctName.substring(0, l)));

		String status = pJSON.getString(ProjectJSONKeyz.statusKey);
		System.out.println(status);
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
		
		System.out.println(pJSON.toString(4));
	}

	public void testSpawnMODProject()
	{
		
		JSONObject modJSON = arrangeMODPrjct().getValue();
		String name = modJSON.getString(ProjectJSONKeyz.nameKey);
		int l = modPrjctName.length()-1;
		assert(name.equals(modPrjctName.substring(0, l)));

		String status = modJSON.getString(ProjectJSONKeyz.statusKey);
		System.out.println(status);
		assert(StatusMGMT.mod.equals(status));
				
		String goal = modJSON.getString(ProjectJSONKeyz.goalKey);
		l = modPrjctGoal.length()-1;
		assert(goal.equals(modPrjctGoal.substring(0,l)));
		
		System.out.println(modJSON.toString(4));
	}
	
	/*
	public void testWakeMOD() 
	{
		//fail("Not yet implemented");
	}
	*/

	@Test
	public void testAppendStep() throws IOException 
	{
		JSONObject asJSON = arrangeAppendStepPrjct().getValue();
		GTDDataSpawnSession gdss = arrangeAppendStepPrjct().getKey();
		gdss.appendStep(asJSON);
		
		JSONArray jArray = asJSON.getJSONArray(ProjectJSONKeyz.stepArrayKey);
		assert(jArray.length()==2);
		
		JSONObject step2 = jArray.getJSONObject(1);
		String desc2 = step2.getString(StepJSONKeyz.descKey);
		int l = this.stepDesc2.length()-1;
		assert(desc2.equals(this.stepDesc2.substring(0, l)));
		
		gdss.appendStep(asJSON);
		assert(jArray.length()==3);
		
		System.out.println(asJSON.toString(4));
	}
	
	@Test
	public void testTerminateProject() throws InputMismatchException, JSONException, IOException
	{


		GTDDataSpawnSession gdss =arrangeTerminatePrjct().getKey();
		JSONObject tpJSON = arrangeTerminatePrjct().getValue();
		
		gdss.terminateProject(tpJSON);//
		
		String status = tpJSON.getString(ProjectJSONKeyz.statusKey);
		String terminalSetName = StatusMGMT.terminalSetName;
		StatusMGMT statusMGMT = StatusMGMT.getInstance();
		Set<String> terminalSet = statusMGMT.getStatesOfASet(terminalSetName);
		
		assert(terminalSet.contains(status));
	}

	/*
	public void testAddNote() {
		fail("Not yet implemented");
	}
	*/
}
